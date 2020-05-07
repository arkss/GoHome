const U = require('./util');
const oapi = require('./oapi');

/*

	method: GET
	query:
		lon_start: [Number] longitude (값 없음: 경로 없음)
		lat_start: [Number] latitude (값 없음: 경로 없음)
		lon_end: [Number] longitude (값 없음: 경로 없음)
		lat_end: [Number] latitude (값 없음: 경로 없음)
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
exports.get_routes = (req, res, next) => {
	let lon_start = req.query.lon_start;
	let lat_start = req.query.lat_start;
	let lon_end = req.query.lon_end;
	let lat_end = req.query.lat_end;
	let include_bike = req.query.include_bike == 'N';
	let include_bus = req.query.include_bus == 'N';

	let routes = [];

	// handle exception: invalid query
	if (U.res.isInvalid(res, lon_start, lat_start, lon_end, lat_end)) return;

	oapi
	.get_pedestrian_route([[lon_start, lat_start], [lon_end, lat_end]])
	.then(result => {
		result.brief_list = ['도보'];
		routes.push(result);
		U.res.response(res, true, `pedestrian route found`, {
			n: routes.length,
			routes: routes
		});
	});
};