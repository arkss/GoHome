const Bikestop = require('../models/bikestop');
const BikestopTraveltime = require('../models/bikestoptraveltime');

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

	let bikestops = exports.get_bikestops(lat, lon, n);

<<<<<<< HEAD
	U.response(res, true, `${bikestops.length} found`, {
=======
	U.res.response(res, true, `${bikestops.length} found`, {
>>>>>>> client/merge-AR
		n: bikestops.length,
		bikestops: bikestops
	});
};

exports.get_bikestops = (lat, lon, n = 0, d = 0) => {
<<<<<<< HEAD
	let bikestops = get_all_bikestops();
=======
	let bikestops = list_all_cached_bikestop();
>>>>>>> client/merge-AR

	// sort
	if (lat && lon) {
		// calculate straight distance to each bikestop
		// forEach not used for optimization
		let i, bikestop, len = bikestops.length;
		for (i = 0; i < len; i++) {
			bikestop = bikestops[i];
<<<<<<< HEAD
			bikestop.distance = U.distance(
=======
			bikestop.distance = U.geo.distance(
>>>>>>> client/merge-AR
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
<<<<<<< HEAD

	// slice
	if (n > 0) {
		bikestops = bikestops.slice(0, n);
	}

	return bikestops;
};

/*

	method: GET
	query:
		(none)

	따릉이 대여소의 자전거 수를 반환

	response:
		n: 검색된 대여소 수
		bikestops: 대여소 목록
			{
				stationId: 대여소 ID
				parkingBikeTotCnt: 자전거 수
			}

*/
exports.api_get_bikestop_parked_counts = (req, res, next) => {
	let bikestops = exports.get_bikestop_parked_counts();

	U.response(res, true, `${bikestops.length} parked counts`, {
		n: bikestops.length,
		bikestops: bikestops
	});
};

exports.get_bikestop_parked_counts = () => {
	let bikestops = get_all_bikestops();
=======

	// slice
	if (n > 0) {
		bikestops = bikestops.slice(0, n);
	}

	return bikestops;
};

/*

	method: GET
	query:
		(none)

	따릉이 대여소의 자전거 수를 반환

	response:
		n: 검색된 대여소 수
		bikestops: 대여소 목록
			{
				stationId: 대여소 ID
				parkingBikeTotCnt: 자전거 수
			}

*/
exports.api_get_bikestop_parked_counts = (req, res, next) => {
	let bikestops = exports.get_bikestop_parked_counts();

	U.res.response(res, true, `${bikestops.length} parked counts`, {
		n: bikestops.length,
		bikestops: bikestops
	});
};

exports.get_bikestop_parked_counts = () => {
	let bikestops = list_all_cached_bikestop();
>>>>>>> client/merge-AR
	let list = [];

	for (let bikestop of bikestops) {
		list.push({
			stationId: bikestop.stationId,
			parkingBikeTotCnt: bikestop.parkingBikeTotCnt
		});
	}

	return list;
};

/*

<<<<<<< HEAD
	각 bikestop 정보 및 bikestop 간 소요시간을 캐시

	bikestops: {
		(stationId): {
			stationId: String
			stationName: String,
			stationLatitude: Number,
			stationLongitude: Number
			traveltime: {
				(stationId): (travel time in sec)
			}
=======
	bikestop 간 소요시간을 캐시
	TODO: 적합한 자료구조 선정

	{
		(stationId): {
			(stationId): (travel time in sec)
>>>>>>> client/merge-AR
		},
		...
	}

*/

const CACHE = {
<<<<<<< HEAD
	bikestops: {},
	promise_loading: null,
	fetch_timeout_handler: null,
	ms_timeout: 200000
};

const get_all_bikestops = () => U.get_values_copied(CACHE.bikestops);

const cache_bikestops = (bikestops) => {
	let stationId;
	// use classic for loop for performance
	for (let bikestop of bikestops) {
		stationId = bikestop.stationId;
		bikestop.stationLatitude = parseFloat(bikestop.stationLatitude);
		bikestop.stationLongitude = parseFloat(bikestop.stationLongitude);
		bikestop.traveltime = CACHE.bikestops[stationId]?.traveltime || {};
		CACHE.bikestops[stationId] = bikestop;
	}
=======
	bikestops: {}, // stationID as key
	promise_loading: null,
	timeout_handler: null,
	ms_timeout: 200000 // const
>>>>>>> client/merge-AR
};

/*

	Return travel time between bikestops or null.

*/
<<<<<<< HEAD
exports.get_traveltime = (stationId_start, stationId_end) =>
	U.get_value(CACHE.bikestops, null, stationId_start, 'traveltime', stationId_end);

=======
exports.get_cached_traveltime = (stationId_start, stationId_end) =>
	U.json.get_value(CACHE.bikestops, null, stationId_start, 'traveltime', stationId_end);

const list_all_cached_bikestop = () => {
	let list = [];
	for (let [stationId, bikestop] of Object.entries(CACHE.bikestops)) {
		list.push(bikestop);
	}

	return JSON.parse(JSON.stringify(list));
};

exports.cache_bikestop = (bikestop) => {
	let traveltime = CACHE.bikestops[bikestop.stationId]?.traveltime || {};
	CACHE.bikestops[bikestop.stationId] = bikestop;
	CACHE.bikestops[bikestop.stationId].traveltime = traveltime;
};

// TODO: use and test it
>>>>>>> client/merge-AR
/*

	Cache travel time.
	If cache already exist, update it to average. (CHECK: this is little dangerous!)

*/
exports.cache_traveltime = (stationId_start, stationId_end, time) => {
	// handle exception: station not found
	if (!CACHE.bikestops[stationId_start] || !CACHE.bikestops[stationId_end]) {
<<<<<<< HEAD
		U.error(`Unexpected stationId: ${stationId_start}, ${stationId_end}`);
		return;
	}

	// handle exception: invalid time
	if (time <= 0 || isNaN(time)) {
		U.error(`Invalid traveltime: ${time}`);
=======
		console.log(`Unexpected stationId: ${stationId_start}, ${stationId_end}`);
		return;
	}
	
	// handle exception: invalid time
	if (time <= 0) {
		console.log(`Invalid traveltime: ${time}`);
>>>>>>> client/merge-AR
		return;
	}

	// find cached value
<<<<<<< HEAD
	let cached_time = exports.get_traveltime(stationId_start, stationId_end) || time;
	cached_time = (time + cached_time) / 2;

	// do cache and update db
	CACHE.bikestops[stationId_start].traveltime[stationId_end] = cached_time;
	update_traveltime_in_db(stationId_start, stationId_end, cached_time);
};

exports.fetch_and_update_bikestop = async () => {
	// cancel the reservated
	if (CACHE.fetch_timeout_handler != null) {
		clearTimeout(CACHE.fetch_timeout_handler);
		CACHE.fetch_timeout_handler = null;
	}

	// fetch
	cache_bikestops(await oapi.load_bikestops());

	// reserve next fetching
	CACHE.fetch_timeout_handler = setTimeout(() => {
		CACHE.fetch_timeout_handler = null;
		U.log(`Bikestop cache is old. Fetch news.`);
		exports.fetch_and_update_bikestop();
	}, CACHE.ms_timeout);
=======
	let cached_time = exports.get_cached_traveltime(stationId_start, stationId_end) || time;
	cached_time = (time + cached_time) / 2;

	// do cache
	CACHE.bikestops[stationId_start].traveltime[stationId_end] = cached_time;
	update_bikestop_traveltime_in_db(stationId_start, stationId_end, cached_time);
>>>>>>> client/merge-AR
};

exports.load_cache_from_db = async () => {
	if (CACHE.promise_loading == null) {
		// cancel fetching
<<<<<<< HEAD
		if (CACHE.fetch_timeout_handler != null) {
			clearTimeout(CACHE.fetch_timeout_handler);
			CACHE.fetch_timeout_handler = null;
		}

		// load
		let promise_loading = async () => {
			try {
				let i, len;

				// TODO: perform these in parallel

				// load Bikestop
				let bs = await Bikestop.find();
				len = bs.length;
				cache_bikestops(bs);
				U.log(`${len} Bikestop found`);
=======
		if (CACHE.fetch_timeout != null) {
			clearTimeout(CACHE.fetch_timeout);
			CACHE.fetch_timeout = null;
		}

		// load
		CACHE.promise_loading = async () => {
			try {
				let i, len;

				// load Bikestop
				// use classic for loop for performance
				let bs = await Bikestop.find();
				len = bs.length;
				for (i = 0; i < len; i++) {
					exports.cache_bikestop(bs[i]);
				}
				console.log(`${len} Bikestop found`);
>>>>>>> client/merge-AR

				// load BikestopTraveltime
				// use classic for loop for performance
				let bstt = await BikestopTraveltime.find();
				len = bstt.length;
				for (i = 0; i < len; i++) {
					exports.cache_traveltime(
						bstt[i].stationId_start,
						bstt[i].stationId_end,
						bstt[i].traveltime
					);
				}
<<<<<<< HEAD
				U.log(`${len} BikestopTraveltime found`);

				// fetch real-time info (ex: count)
				await exports.fetch_and_update_bikestop();
				exports.save_cache_to_db();
			} catch (err) {
				// TODO: handle unexpected error
				U.error(err);
=======
				console.log(`${len} BikestopTraveltime found`);

				// fetch real-time info (ex: count)
				await exports.update_bikestop_cache_from_fetch();
				exports.save_cache_to_db();
			} catch (err) {
				// TODO: handle unexpected error
				console.log(err);
>>>>>>> client/merge-AR
			}

			return;
		};
<<<<<<< HEAD

		U.log(`Load bike cache from DB ...`);
		CACHE.promise_loading = promise_loading();
=======
		CACHE.promise_loading();
>>>>>>> client/merge-AR
	}

	// wait for loading
	await CACHE.promise_loading;
	CACHE.promise_loading = null;

	return;
};

<<<<<<< HEAD
exports.save_cache_to_db = (ignore_traveltime = true) => {
	U.log(`Start updating bikestops in DB ...`);
=======
exports.update_bikestop_cache_from_fetch = async () => {
	// cancel the reservated
	if (CACHE.fetch_timeout != null) {
		clearTimeout(CACHE.fetch_timeout);
		CACHE.fetch_timeout = null;
	}

	// fetch
	// use classic for loop for performance
	let i, len, bs = await oapi.load_bikestops();
	len = bs.length;
	for (i = 0; i < len; i++) {
		exports.cache_bikestop(bs[i]);
	}

	// reserve next fetching
	CACHE.fetch_timeout = setTimeout(() => {
		CACHE.fetch_timeout = null;
		console.log(`Bikestop cache is old. Fetch news.`);
		exports.update_bikestop_cache_from_fetch();
	}, CACHE.ms_timeout);
};

exports.save_cache_to_db = (ignore_traveltime = true) => {
	console.log(`Start updating bikestops in DB ...`);
>>>>>>> client/merge-AR
	for (let [stationId, bikestop] of Object.entries(CACHE.bikestops)) {
		update_bikestop_in_db(
			stationId,
			bikestop.stationName,
			bikestop.stationLatitude,
			bikestop.stationLongitude,
		);
		if (!ignore_traveltime) {
			for (let [stationId_end, traveltime] of Object.entries(bikestop.traveltime)) {
<<<<<<< HEAD
				update_traveltime_in_db(
=======
				update_bikestop_traveltime_in_db(
>>>>>>> client/merge-AR
					stationId,
					stationId_end,
					traveltime
				);
			}
		}
	}
<<<<<<< HEAD
	U.log(`DB successfully updated.`);
=======
	console.log(`DB successfully updated.`);
>>>>>>> client/merge-AR
};

// update one or create if not exist
const update_bikestop_in_db = (stationId, stationName, stationLatitude, stationLongitude) => {
	Bikestop.findOneAndUpdate({
		stationId: stationId
	}, {
		stationName: stationName,
		stationLatitude: stationLatitude,
		stationLongitude: stationLongitude,
	}, {
		new: true, // if true, it return the updated but takes more time
		upsert: true // when no matches, insert new one
	}, (err, res) => {
<<<<<<< HEAD
		if (err) U.error(err);
=======
		if (err) console.log(err);
>>>>>>> client/merge-AR
	});
};

// update one or create if not exist
<<<<<<< HEAD
const update_traveltime_in_db = (stationId_start, stationId_end, traveltime) => {
=======
const update_bikestop_traveltime_in_db = (stationId_start, stationId_end, traveltime) => {
>>>>>>> client/merge-AR
	BikestopTraveltime.findOneAndUpdate({
		stationId_start: stationId_start,
		stationId_end: stationId_end
	}, {
		traveltime: traveltime
	}, {
		new: true, // if true, it return the updated but takes more time
		upsert: true // when no matches, insert new one
	}, (err, res) => {
<<<<<<< HEAD
		if (err) U.error(err);
=======
		if (err) console.log(err);
>>>>>>> client/merge-AR
	});
};