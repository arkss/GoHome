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
		(none)

	(for test) 모든 N버스 노선정보를 반환

	response:
		n: 검색된 노선 수
		nbus_info: 노선 목록

*/
exports.api_get_nbus_routes = (req, res, next) => {
	exports.get_nbus_routes()
	.then(nbus_info => {
		U.response(res, true, `${nbus_info.length} found`, {
			n: nbus_info.length,
			nbus_info: nbus_info
		});
	})
	.catch(next);
};

exports.get_nbus_routes = async () => {
	return get_all_busroutes();
};

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

	let stations = exports.get_near_stations(lat, lon, n);
	U.response(res, true, `${stations.length} found`, {
		n: stations.length,
		stations: stations
	});
};

exports.get_near_stations = (lat, lon, n) => {
	let stations = get_all_busstops();
	let i, len, station;

	// sort
	if (lon && lat) {
		// calculate straight distance to each bikestop
		// forEach not used for optimization
		len = stations.length;
		for (i = 0; i < len; i++) {
			station = stations[i];
			station.distance = U.approx_distance(
				station.stationLatitude, station.stationLongitude,
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
};

/*

	busstop 간 소요시간을 캐시

	bikestops: {
		(stationId): {
			stationId: String
			stationName: String
			stationLongitude: Number
			stationLatitude: Number
			posX: Number
			posY: Number
			station: String
			stationNo: String
		},
		...
	}

	busroutes: {
		(busRouteId): {
			busRouteId: String
			busRouteNm: String
			stStationNm: String
			edStationNm: String
			firstBusTm: String
			lastBusTm: String
			term: Number
			stations: Array
		}
	}

*/

const CACHE = {
	busstops: {},
	busroutes: {},
	promise_loading: null,
	fetch_timeout_handler: null,
	ms_timeout: 1000000
};

const get_all_busstops = () => U.get_values_copied(CACHE.busstops);

const get_all_busroutes = () => U.get_values_copied(CACHE.busroutes);

const cache_busstops = (busstops) => {
	let stationId;
	// use classic for loop for performance
	for (let busstop of busstops) {
		stationId = busstop.stationId;
		busstop.traveltime = CACHE.busstops[stationId]?.traveltime || {};
		CACHE.busstops[stationId] = busstop;
	}
};

const cache_busroutes = (busroutes) => {
	// use classic for loop for performance
	for (let busroute of busroutes) {
		CACHE.busroutes[busroute.busRouteId] = busroute;
	}
};

const cache_bus_info = (bus_info) => {
	let busRouteId, stationId;
	// use classic for loop for performance
	for (let info of bus_info) {
		busRouteId = info.busRouteId;
		let stations = info.stations;
		let stationIDs = [];

		for (let station of stations) {
			stationId = station.arsId;
			stationIDs.push(stationId);
			if (!CACHE.busstops[stationId]) {
				// new station found
				CACHE.busstops[stationId] = {
					stationId: stationId,
					stationName: station.stationNm,
					stationLongitude: Number(station.gpsX),
					stationLatitude: Number(station.gpsY),
					posX: Number(station.posX),
					posY: Number(station.posY),
					station: station.station,
					stationNo: station.stationNo
				}
			}
		}

		CACHE.busroutes[busRouteId] = {
			busRouteId: busRouteId,
			busRouteNm: info.busRouteNm,
			stStationNm: info.stStationNm,
			edStationNm: info.edStationNm,
			firstBusTm: info.firstBusTm,
			lastBusTm: info.lastBusTm,
			term: parseInt(info.term),
			stations: stationIDs
		};
	}
};

// TODO: edit these
// TODO: use cache
exports.get_busstop = (stationId) => {
	return CACHE.busstops[stationId] ?? null;
};
exports.get_traveltime = (stationId_start, stationId_end) => {
	return null;
};
exports.cache_traveltime = (stationId_start, stationId_end, time) => {
	return null;
};

exports.fetch_and_update_busstop = async () => {
	// cancel the reservated
	if (CACHE.fetch_timeout_handler != null) {
		clearTimeout(CACHE.fetch_timeout_handler);
		CACHE.fetch_timeout_handler = null;
	}

	// fetch
	U.log(`fetch and cache`);
	let bus_info = await oapi.load_nbus_info();
	cache_bus_info(bus_info);

	// reserve next fetching
	CACHE.fetch_timeout_handler = setTimeout(() => {
		CACHE.fetch_timeout_handler = null;
		U.log(`Busstop cache is old. Fetch news.`);
		exports.fetch_and_update_busstop();
	}, CACHE.ms_timeout);
};

exports.load_cache_from_db = async () => {
	if (CACHE.promise_loading == null) {
		// cancel fetching
		if (CACHE.fetch_timeout_handler != null) {
			clearTimeout(CACHE.fetch_timeout_handler);
			CACHE.fetch_timeout_handler = null;
		}

		// load
		let promise_loading = async () => {
			try {
				let i, len;

				// TODO: perform these in parallel

				// load Busstop
				let bs = await Busstop.find();
				len = bs.length;
				cache_busstops(bs);
				U.log(`${len} Busstop found`);

				// load BusstopTraveltime
				// use classic for loop for performance
				let bstt = await BusstopTraveltime.find();
				len = bstt.length;
				for (i = 0; i < len; i++) {
					exports.cache_traveltime(
						bstt[i].stationId_start,
						bstt[i].stationId_end,
						bstt[i].traveltime
					);
				}
				U.log(`${len} BusstopTraveltime found`);

				let br = await BusRoute.find();
				len = br.length;
				cache_busroutes(br);
				U.log(`${len} BusRoute found`);

				// fetch real-time info (ex: count)
				await exports.fetch_and_update_busstop();
				exports.save_cache_to_db();
			} catch (err) {
				// TODO: handle unexpected error
				U.error(err);
			}

			return;
		};

		U.log(`Load bus cache from DB ...`);
		CACHE.promise_loading = promise_loading();
	}

	// wait for loading
	await CACHE.promise_loading;
	CACHE.promise_loading = null;

	return;
};
exports.save_cache_to_db = (ignore_traveltime = true) => {
	U.log(`Start updating busstops in DB ...`);
	for (let [stationId, busstop] of Object.entries(CACHE.busstops)) {
		update_busstop_in_db(
			stationId,
			busstop.stationName,
			busstop.stationLatitude,
			busstop.stationLongitude,
		);
		if (!ignore_traveltime) {
			for (let [stationId_end, traveltime] of Object.entries(busstop.traveltime)) {
				update_traveltime_in_db(
					stationId,
					stationId_end,
					traveltime
				);
			}
		}
	}
	U.log(`DB successfully updated.`);
};

// update one or create if not exist
const update_busstop_in_db = (stationId, stationName, stationLatitude, stationLongitude) => {
	Busstop.findOneAndUpdate({
		stationId: stationId
	}, {
		stationName: stationName,
		stationLatitude: stationLatitude,
		stationLongitude: stationLongitude,
	}, {
		new: true, // if true, it return the updated but takes more time
		upsert: true // when no matches, insert new one
	}, (err, res) => {
		if (err) U.error(err);
	});
};

// update one or create if not exist
const update_traveltime_in_db = (stationId_start, stationId_end, traveltime) => {
	BusstopTraveltime.findOneAndUpdate({
		stationId_start: stationId_start,
		stationId_end: stationId_end
	}, {
		traveltime: traveltime
	}, {
		new: true, // if true, it return the updated but takes more time
		upsert: true // when no matches, insert new one
	}, (err, res) => {
		if (err) U.error(err);
	});
};

// update one or create if not exist
const update_busroute_in_db = (routeId, stations) => {
	BusRoute.findOneAndUpdate({
		routeId: routeId
	}, {
		stations: stations
	}, {
		new: true, // if true, it return the updated but takes more time
		upsert: true // when no matches, insert new one
	}, (err, res) => {
		if (err) U.error(err);
	});
};