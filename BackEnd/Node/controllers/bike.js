const Bikestop = require('../models/bikestop');

const U = require('./util');
const oapi = require('./oapi');

/*

	method: GET
	query:
		lat: [Number] latitude (값 없음: 임의 반환)
		lon: [Number] longitude (값 없음: 임의 반환)
		n: [Integer] 검색할 대여소 수. (0이하 또는 값 없음: 제한없음)

	주변의 따릉이 대여소 정보를 반환

	response:
		n: 검색된 대여소 수
		bikestops: 대여소 목록
			{
				rackTotCnt: 거치대 개수
				stationName: 대여소 이름
				parkingBikeTotCnt: 자전거 수
				shared: 거치율 (원천부서에서 정확한 의미 확인중)
				stationLatitude: 위도
				stationLongitude: 경도
				stationId: 대여소 ID
				distance: 대여소까지의 직진 거리 (근사값, 단위: m)
			}

*/
exports.api_get_bikestops = (req, res, next) => {
	let lat = parseFloat(req.query.lat) || 0;
	let lon = parseFloat(req.query.lon) || 0;
	let n   = parseInt(req.query.n)     || 0;

	exports.get_bikestops(lat, lon, n)
	.then(bikestops => {
		U.res.response(res, true, `${bikestops.length} found`, {
			n: bikestops.length,
			bikestops: bikestops
		});
	})
	.catch(next);
};

exports.get_bikestops = (lat, lon, n = 0, d = 0) =>
	oapi.load_bikestops()
	.then(bikestops => {

		// sort
		if (lat && lon) {
			// calculate straight distance to each bikestop
			// forEach not used for optimization
			let i, bikestop, len = bikestops.length;
			for (i = 0; i < len; i++) {
				bikestop = bikestops[i];
				bikestop.distance = U.geo.distance(
					bikestop.stationLatitude, bikestop.stationLongitude,
					lat, lon
				);
			}
			bikestops.sort((a, b) => a.distance - b.distance);

			// slice
			if (d > 0) {
				for (i = 0; bikestops[i].distance < d; i++);
				bikestops = bikestops.slice(0, i);
			}
		}

		// slice
		if (n > 0) {
			bikestops = bikestops.slice(0, n);
		}

		return bikestops;
	});

/*

	method: GET
	query:
		(none)

	따릉이 대여소의 자전거 수를 반환

	response:
		bikestops: 대여소 목록
			{
				stationId: 대여소 ID
				parkingBikeTotCnt: 자전거 수
			}

*/
exports.api_get_bikestop_parked_counts = (req, res, next) => {
	exports.get_bikestop_parked_counts()
	.then(bikestops => {
		U.res.response(res, true, `${bikestops.length} parked counts`, {
			bikestops: bikestops
		});
	})
	.catch(next);
};

exports.get_bikestop_parked_counts = () =>
	oapi.load_bikestops()
	.then(bikestops => {
		let list = [];
		for (let bikestop of bikestops) {
			list.push({
				stationId: bikestop.stationId,
				parkingBikeTotCnt: bikestop.parkingBikeTotCnt
			});
		}

		return list;
	});

/*

	bikestop 간 소요시간을 캐시
	TODO: 적합한 자료구조 선정

	{
		(stationId): {
			(stationId): (travel time in sec)
		},
		...
	}

*/
exports.cache_bikestop_traveltime = {};

/*

	Return travel time between bikestops or null.

*/
exports.get_cached_traveltime = (stationId_start, stationId_end) =>
	U.json.get_value(exports.cache_bikestop_traveltime, null, stationId_start, stationId_end);

// TODO: use and test it
/*

	Cache travel time.
	If cache already exist, update it to average. (CHECK: this is little dangerous!)

*/
exports.cache_traveltime = (stationId_start, stationId_end, time) => {
	let cached = exports.get_cached_traveltime(stationId_start, stationId_end);

	// when cache not found
	if (!cached)
		cached = time;

	// when cache not found for stationId_start
	if (!exports.cache_bikestop_traveltime[stationId_start]) {
		exports.cache_bikestop_traveltime[stationId_start] = {};
	}

	// do cache
	exports.cache_bikestop_traveltime[stationId_start][stationId_end] = (time + cached) / 2;
};

exports.load_bikestop_cache_from_db = () => {

};

exports.load_traveltime_cache_from_db = () => {

};

// update one or create if not exist
const mongodb_update_bikestop = (stationId, stationName, stationLatitude, stationLongitude) => {

};

// return all bikestops 
const mongodb_get_all_bikestops = () => {

};

// 
const mongodb_delete_bikestop = (stationId) => {

};