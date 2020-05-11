const U = require('./util');
const oapi = require('./oapi');

const bike = require('./bike');

const CONCALL_BIKE_ROUTE_SEARCH =   1;
const MAX_BIKE_ROUTE_SEARCH     =  10;
const MAX_BIKE_ROUTE_RETURN     =   5;

/*

	method: GET
	query:
		lat_start: [Number] latitude (값 없음: 경로 없음)
		lon_start: [Number] longitude (값 없음: 경로 없음)
		lat_end: [Number] latitude (값 없음: 경로 없음)
		lon_end: [Number] longitude (값 없음: 경로 없음)
		include_bike: [Y/N] include bike into the route (기본값: Y)
		include_bus: [Y/N] include bike into the route (기본값: Y)

	경로 탐색 후 몇 가지 추천경로를 반환

	response:
		n: 검색된 경로 수
		routes: 경로 목록
			{
				time: 예상 소요시간
				walking_distance: 예상 도보 거리
				brief_list: 간단한 경로 내용
				route_list: 상세한 경로 내용
					[
						{
							type: 경로 타입(도보/자전거/버스 등)
							type_info: 경로 상세(버스 번호 등)
							expected_time: 예상 소요시간
							points: 체크포인트 목록
							start_point_name: 출발지 이름 (정류장, 노선 번호 등)
							end_point_name: 도착지 이름 (정류장, 노선 번호 등)
						},
						...
					]
			}

*/
exports.api_get_routes = (req, res, next) => {
	let lat_start = req.query.lat_start;
	let lon_start = req.query.lon_start;
	let lat_end = req.query.lat_end;
	let lon_end = req.query.lon_end;
	let include_bike = req.query.include_bike == 'N';
	let include_bus = req.query.include_bus == 'N';
	
	// handle exception: invalid query
	if (U.res.isInvalid(res, lat_start, lon_start, lat_end, lon_end)) return;
	exports.get_routes(lat_start, lon_start, lat_end, lon_end)
	.then(routes => {
		U.res.response(res, true, `pedestrian route found`, {
			n: routes.length,
			routes: routes
		});
	})
	.catch(next);
};

exports.get_routes = (lat_start, lon_start, lat_end, lon_end) =>
	new Promise((resolve, reject) => {
		let routes = [];

		// *stops are sorted by distance
		let linear_distance, bikestops_near_start, bikestops_near_end, nbusstops_near_start, nbusstops_near_end;

		// find near bikestops and search routes
		bikestops_near_start = [];
		bikestops_near_end = [];
		linear_distance = U.geo.distance(lat_start, lon_start, lat_end, lon_end);
		let promise_bikestop = new Promise((resolve, reject) => {
			Promise.all([
				bike.get_bikestops(lat_start, lon_start, 0, linear_distance / 2),
				bike.get_bikestops(lat_end, lon_end, 0, linear_distance / 2)
			])
			.then(([v1, v2]) => {
				bikestops_near_start = v1;
				bikestops_near_end = v2;

				// for test
				console.log(`linear distance: ${linear_distance}, the number of near bikestops: ${v1.length}, ${v2.length}`);

				// 쌍 매칭하고 예상 시간 계산 및 정렬
				// 예상시간 짧은 순으로 길찾기 하면서 예상시간 최댓값을 낮춰감
				let travel_time, candidate_routes = [], searched_results = [];

				// for every possible pairs
				for (let bs1 of v1) {
					for (let bs2 of v2) {
						// calculate expected riding & walking time (in sec)
						travel_time = (
							bike.get_cached_travel_time(bs1.stationId, bs2.stationId) ||
							U.geo.riding_time(
								bs1.stationLatitude, bs1.stationLongitude,
								bs2.stationLatitude, bs2.stationLongitude
							)
						)
						+ U.geo.walking_time(lat_start, lon_start, bs1.stationLatitude, bs1.stationLongitude)
						+ U.geo.walking_time(bs2.stationLatitude, bs2.stationLongitude, lat_end, lon_end);
						// add to the candidates
						candidate_routes.push({
							bs: [bs1, bs2],
							travel_time: travel_time
						});
					}
				}

				// for test
				console.log(`${candidate_routes.length} candidate pairs found`);

				// sort pairs out by expected travel time
				candidate_routes.sort((a, b) => a.travel_time - b.travel_time);
				candidate_routes = candidate_routes.slice(0, MAX_BIKE_ROUTE_SEARCH);

				// search real time: promise-sequentially
				// it may takes really long time but able to reduce the number of API call.
				let time_upperbound = Infinity;

				const end_searching = () => {
					console.log(`${searched_results.length} routes are really searched with API call`);
					console.log(`time upperbound: ${time_upperbound}`);

					// sort result out by its travel time
					searched_results.sort((a, b) => a.time - b.time);
					searched_results = searched_results.slice(0, MAX_BIKE_ROUTE_RETURN);
					routes = routes.concat(searched_results);

					return resolve();
				};

				const search_candidate_routes = (i) => {
					let promises, candidates;

					candidates = candidate_routes.slice(i, i + CONCALL_BIKE_ROUTE_SEARCH);
					// handle exception: all is searched
					if (candidates.length == 0) {
						console.log(`all is searched`);
						end_searching();
						return;
					}

					const search_candidate_route = (candidate) =>
						new Promise((resolve, reject) => {

							// don't have to search longer way
							if (time_upperbound < candidate.travel_time) {
								console.log(`over the upperbound`);
								return resolve(false);
							}

							console.log(`search real route ${i}`);
							console.log(`expected minimum travel time: ${candidate.travel_time}, upperbound: ${time_upperbound}`);
							//return resolve(true);
							let bs1 = candidate.bs[0];
							let bs2 = candidate.bs[1];

							oapi.get_pedestrian_route([
								[lat_start, lon_start],
								[bs1.stationLatitude, bs1.stationLongitude],
								[bs2.stationLatitude, bs2.stationLongitude],
								[lat_end, lon_end],
							])
							.then(result => {
								console.log(`real route searched ${i}`);

								// for test: handle exception
								if (result.section_time.length != 3)
									console.log(`unexpected section length: ${result.section_time.length}`);

								// add more info for response
								result.bs = [bs1, bs2];
								result.brief_list = ['도보', '자전거', '도보'];

								// re-calculate time since the middle section is for riding, not walking
								result.section_time[1] = U.geo.walking_time_2_riding_time(result.section_time[1]);
								result.time = result.section_time.reduce((a, c) => a + c, 0);

								// cache the riding time
								bike.cache_travel_time(bs1.stationId, bs2.stationId, result.section_time[1]);

								// add result to the list
								searched_results.push(result);

								// update time_upperbound
								if (result.time < time_upperbound)
									time_upperbound = result.time;

								return resolve(true);
							});
						});

					promises = [];
					for (let candidate of candidates)
						promises.push(search_candidate_route(candidate));
					Promise.all(promises)
					.then(results => {
						if (results.some(e => e)) {
							// more search needed
							search_candidate_routes(i + CONCALL_BIKE_ROUTE_SEARCH);
						} else {
							end_searching();
						}
					});
				};

				search_candidate_routes(0);
			});
		});

		// search pedestran route
		let promise_pedestrian = oapi.get_pedestrian_route([[lat_start, lon_start], [lat_end, lon_end]])
		.then(result => {
			result.brief_list = ['도보'];
			routes.push(result);
			return;
		});

		// search all routes
		Promise.all([
			promise_bikestop,
			promise_pedestrian
		])
		.then(() => {
			// end of searching
			console.log(`All routes are found.`);
			resolve(routes);
		});
	});
