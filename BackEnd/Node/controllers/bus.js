const Busstop = require('../models/busstop');
const BusstopTraveltime = require('../models/busstoptraveltime');
const BusRoute = require('../models/busroute');

const U = require('./util');
const oapi = require('./oapi');

/*

	method: GET
	query:
		include_stations: [Y/N] (기본값: N)

	(for test) 모든 N버스 노선정보를 반환

	response:
		n: 검색된 노선 수
		nbus_info: 노선 목록

*/
exports.api_get_nbus_info = (req, res, next) => {
	let include_stations = req.query.include_stations == 'Y';

	exports.get_nbus_info(include_stations)
	.then(nbus_info => {
		U.response(res, true, `${nbus_info.length} found`, {
			n: nbus_info.length,
			nbus_info: nbus_info
		});
	})
	.catch(next);
};

exports.get_nbus_info = (include_stations) =>
	oapi
	.load_nbus_info()
	.then(nbus_info => {

		if (!include_stations)
			U.delete_properties(nbus_info, 'stations');

		// response
		return nbus_info;
	});

/*

	method: GET
	query:
		lat: [Number] latitude (값 없음: 임의 반환)
		lon: [Number] longitude (값 없음: 임의 반환)
		n: [Integer] 검색할 정류장 수. (0이하 또는 값 없음: 제한없음)

	(for test) 주변의 N버스 정류장 목록을 반환

	response:
		n: 검색된 정류장 수
		stations: 정류장 목록

*/
exports.api_get_near_stations = (req, res, next) => {
	let lat = parseFloat(req.query.lat) || 0;
	let lon = parseFloat(req.query.lon) || 0;
	let n   = parseInt(req.query.n)     || 0;

	exports.get_near_stations(lat, lon, n)
	.then(stations => {
		U.response(res, true, `${stations.length} found`, {
			n: stations.length,
			stations: stations
		});
	})
	.catch(next);
};

// CHECK: 환승가능한 정류장의 경우 중복됨
exports.get_near_stations = (lat, lon, n) =>
	oapi.load_nbus_info()
	.then(nbus_info => {
		let i, len, station, stations = [];

		// populate stations
		len = nbus_info.length;
		for (i = 0; i < len; i++) {
			stations = stations.concat(nbus_info[i].stations);
		}

		// sort
		if (lon && lat) {
			// calculate straight distance to each bikestop
			// forEach not used for optimization
			len = stations.length;
			for (i = 0; i < len; i++) {
				station = stations[i];
				station.distance = U.approx_distance(
					station.gpsY, station.gpsX,
					lat, lon
				);
			}
			stations.sort((a, b) => a.distance - b.distance);
		}

		// slice
		if (n > 0) {
			stations = stations.slice(0, n);
		}

		return stations;
	});

/*

	busstop 간 소요시간을 캐시

	bikestops: {
		(stationId): {
			stationId: String
			stationName: String,
			stationLatitude: Number,
			stationLongitude: Number
			traveltime: {
				(stationId): (travel time in sec)
			}
		},
		...
	}

*/


const CACHE = {
	busstops: {},
	promise_loading: null,
	timeout_handler: null,
	ms_timeout: 1000000
};

const get_all_busstops = () => {};
const cache_busstops = () => {};

exports.get_traveltime = () => {};
exports.cache_traveltime = () => {};

exports.fetch_and_update_busstop = async () => {};

exports.load_cache_from_db = async () => {};
exports.save_cache_to_db = (ignore_traveltime = true) => {};
const update_busstop_in_db = (stationId, stationName, stationLatitude, stationLongitude) => {};
const update_traveltime_in_db = (stationId_start, stationId_end, traveltime) => {};