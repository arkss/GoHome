const G = require('./util/geo');
const R = require('./response');
const oapi = require('./oapi');

/*

	method: GET
	query:
		lon: longitude (값 없음: 임의 반환)
		lat: latitude (값 없음: 임의 반환)
		n: 검색할 대여소 수. (0이하 또는 값 없음: 제한없음)

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
exports.get_bikestops = (req, res, next) => {
	let lon = parseFloat(req.query.lon) || 0;
	let lat = parseFloat(req.query.lat) || 0;
	let n   = parseInt(req.query.n);

	// get list to response and sort
	oapi.
	load_bikestops()
	.then(bikestops => {

		// sort
		if (lon && lat) {
			// calculate straight distance to each bikestop
			// forEach not used for optimization
			let i, bikestop, len = bikestops.length;
			for (i = 0; i < len; i++) {
				bikestop = bikestops[i];
				bikestop.distance = G.approx_distance(
					bikestop.stationLatitude, lat,
					bikestop.stationLongitude, lon
					);
			}
			bikestops.sort((a, b) => a.distance - b.distance);
		}

		// slice
		if (n > 0) {
			bikestops = bikestops.slice(0, n);
		}

		// response
		R.response(res, true, `${bikestops.length} found`, {
			n: bikestops.length,
			bikestops: bikestops
		});
	});
};
