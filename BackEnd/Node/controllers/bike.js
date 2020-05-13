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

	U.res.response(res, true, `${bikestops.length} found`, {
		n: bikestops.length,
		bikestops: bikestops
	});
};

exports.get_bikestops = (lat, lon, n = 0, d = 0) => {
	let bikestops = list_all_cached_bikestop();

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
};

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
	let bikestops = exports.get_bikestop_parked_counts();

	U.res.response(res, true, `${bikestops.length} parked counts`, {
		bikestops: bikestops
	});
};

exports.get_bikestop_parked_counts = () => {
	let bikestops = list_all_cached_bikestop();
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

	bikestop 간 소요시간을 캐시
	TODO: 적합한 자료구조 선정

	{
		(stationId): {
			(stationId): (travel time in sec)
		},
		...
	}

*/

const CACHE = {
	bikestops: {}, // stationID as key
	promise_loading: null,
	timeout_handler: null,
	ms_timeout: 200000 // const
};

/*

	Return travel time between bikestops or null.

*/
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
/*

	Cache travel time.
	If cache already exist, update it to average. (CHECK: this is little dangerous!)

*/
exports.cache_traveltime = (stationId_start, stationId_end, time) => {
	// handle exception: station not found
	if (!CACHE.bikestops[stationId_start] || !CACHE.bikestops[stationId_end]) {
		console.log(`Unexpected stationId: ${stationId_start}, ${stationId_end}`);
		return;
	}
	
	// handle exception: invalid time
	if (time <= 0) {
		console.log(`Invalid traveltime: ${time}`);
		return;
	}

	// find cached value
	let cached_time = exports.get_cached_traveltime(stationId_start, stationId_end) || time;
	cached_time = (time + cached_time) / 2;

	// do cache
	CACHE.bikestops[stationId_start].traveltime[stationId_end] = cached_time;
	update_bikestop_traveltime_in_db(stationId_start, stationId_end, cached_time);
};

exports.load_cache_from_db = async () => {
	if (CACHE.promise_loading == null) {
		// cancel fetching
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
				console.log(`${len} BikestopTraveltime found`);

				// fetch real-time info (ex: count)
				await exports.update_bikestop_cache_from_fetch();
				exports.save_cache_to_db();
			} catch (err) {
				// TODO: handle unexpected error
				console.log(err);
			}

			return;
		};
		CACHE.promise_loading();
	}

	// wait for loading
	await CACHE.promise_loading;
	CACHE.promise_loading = null;

	return;
};

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
	for (let [stationId, bikestop] of Object.entries(CACHE.bikestops)) {
		update_bikestop_in_db(
			stationId,
			bikestop.stationName,
			bikestop.stationLatitude,
			bikestop.stationLongitude,
		);
		if (!ignore_traveltime) {
			for (let [stationId_end, traveltime] of Object.entries(bikestop.traveltime)) {
				update_bikestop_traveltime_in_db(
					stationId,
					stationId_end,
					traveltime
				);
			}
		}
	}
	console.log(`DB successfully updated.`);
};

// update one or create if not exist
const update_bikestop_in_db = (stationId, stationName, stationLatitude, stationLongitude) => {
	console.log(`Update Bikestop ${stationId}`);
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
		if (err) console.log(err);
	});
};

// update one or create if not exist
const update_bikestop_traveltime_in_db = (stationId_start, stationId_end, traveltime) => {
	console.log(`Update BikestopTraveltime ${stationId_start} - ${stationId_end}`);
	BikestopTraveltime.findOneAndUpdate({
		stationId_start: stationId_start,
		stationId_end: stationId_end
	}, {
		traveltime: traveltime
	}, {
		new: true, // if true, it return the updated but takes more time
		upsert: true // when no matches, insert new one
	}, (err, res) => {
		if (err) console.log(err);
	});
};