const keys = require('../keys.json');
const U = require('./util');

const request = require('request-promise');
const xml2js = require('xml2js');
const iconv = require('iconv-lite');

/*

	Return all bikestops.
	Refer to: http://data.seoul.go.kr/dataList/OA-15493/A/1/datasetView.do
	
	Example of a bikestop:
	{
		"rackTotCnt": "22",
		"stationName": "102. 망원역 1번출구 앞",
		"parkingBikeTotCnt": "5",
		"shared": "0",
		"stationLatitude": "37.55564880",
		"stationLongitude": "126.91062927",
		"stationId": "ST-4"
	}

*/
exports.load_bikestops = () =>
	new Promise((resolve, reject) => {

		// load from cache
		if (exports.bikestops.expired == false) {
			console.log(`load bikestops from cache`);
			return resolve();
		}

		// cache expired, fetch new
		console.log(`load bikestops from fetched`);
		let base_url = `http://openapi.seoul.go.kr:8088/${keys.api_key.seoul_opendata}/json/bikeList`;
		let option_1 = {
			url: `${base_url}/1/1000/`,
			encoding: null
		};
		let option_2 = {
			url: `${base_url}/1001/2000/`,
			encoding: null
		};

		let list = [];

		// request bikestop list
		Promise.all([
			requestAndParseAsJSON(option_1),
			requestAndParseAsJSON(option_2)
		])
		.then(([json_1, json_2]) => {
			/*
			NOTE: at ES2020, you can use optional chaining operator (?.)
			and nullish coalescing operator (??) to make it simpler
			*/
			list = list.concat(U.json.get_value(json_1, [], "rentBikeStatus", "row"));
			list = list.concat(U.json.get_value(json_2, [], "rentBikeStatus", "row"));
		})
		.catch(console.log)
		.then(() => {
			// sort by id and update cache
			list.sort((a, b) => a.stationId.slice(3) - b.stationId.slice(3));
			update_cache(exports.bikestops, list);
			resolve();
		});
	})
	.then(() => JSON.parse(JSON.stringify(exports.bikestops.list))); // return deep copy

/*

	Return all N bus and its stations.

	Example of a info:
	{
		"busRouteId": "100100414",
		"busRouteNm": "6003",
		"corpNm": "공항리무진 02-2664-9898",
		"edStationNm": "서울대학교",
		"firstBusTm": "20200412065000",
		"firstLowTm": " ",
		"lastBusTm": "20200412230000",
		"lastBusYn": " ",
		"lastLowTm": "20191105000000",
		"length": "131",
		"routeType": "1",
		"stStationNm": "관악구청",
		"term": "55",

		"stations":
		[
			{
				"arsId": "22793",
				"beginTm": ":",
				"busRouteId": "115000009",
				"busRouteNm": "N6002",
				"direction": "인천공항(T2)",
				"gpsX": "127.0039283325",
				"gpsY": "37.5060680574",
				"lastTm": ":",
				"posX": "200347.29184193382",
				"posY": "445183.48014029907",
				"routeType": "1",
				"sectSpd": "6",
				"section": "121600113",
				"seq": "1",
				"station": "121000977",
				"stationNm": "강남고속버스터미널",
				"stationNo": "22793",
				"transYn": "N",
				"fullSectDist": "473",
				"trnstnid": "101000003"

				"arriveInfo":
				{
					"arrmsg1": ["운행종료"],
					"arrmsg2": ["운행종료"],
					"arsId": ["22793"],
					"avgCf1": ["0"],
					"avgCf2": ["0"],
					"brdrde_Num1": ["0"],
					"brdrde_Num2": ["0"],
					"brerde_Div1": ["0"],
					"brerde_Div2": ["0"],
					"busRouteId": ["115000009"],
					"busType1": ["0"],
					"busType2": ["0"],
					"deTourAt": ["00"],
					"dir": [" "],
					"expCf1": ["0"],
					"expCf2": ["0"],
					"exps1": ["0"],
					"exps2": ["0"],
					"firstTm": ["2020042300"],
					"full1": ["0"],
					"full2": ["0"],
					"goal1": ["0"],
					"goal2": ["0"],
					"isArrive1": ["0"],
					"isArrive2": ["0"],
					"isLast1": ["0"],
					"isLast2": ["0"],
					"kalCf1": ["0"],
					"kalCf2": ["0"],
					"kals1": ["0"],
					"kals2": ["0"],
					"lastTm": ["2020042300"],
					"mkTm": ["2020-04-23 13:27:30.0"],
					"namin2Sec1": ["0"],
					"namin2Sec2": ["0"],
					"neuCf1": ["0"],
					"neuCf2": ["0"],
					"neus1": ["0"],
					"neus2": ["0"],
					"nextBus": ["Y"],
					"nmain2Ord1": ["0"],
					"nmain2Ord2": ["0"],
					"nmain2Stnid1": ["0"],
					"nmain2Stnid2": ["0"],
					"nmain3Ord1": ["0"],
					"nmain3Ord2": ["0"],
					"nmain3Sec1": ["0"],
					"nmain3Sec2": ["0"],
					"nmain3Stnid1": ["0"],
					"nmain3Stnid2": ["0"],
					"nmainOrd1": ["0"],
					"nmainOrd2": ["0"],
					"nmainSec1": ["0"],
					"nmainSec2": ["0"],
					"nmainStnid1": ["0"],
					"nmainStnid2": ["0"],
					"nstnId1": ["0"],
					"nstnId2": ["0"],
					"nstnOrd1": ["0"],
					"nstnOrd2": ["0"],
					"nstnSec1": ["0"],
					"nstnSec2": ["0"],
					"nstnSpd1": ["0"],
					"nstnSpd2": ["0"],
					"plainNo1": [" "],
					"plainNo2": [" "],
					"rerdie_Div1": ["0"],
					"rerdie_Div2": ["0"],
					"reride_Num1": ["0"],
					"reride_Num2": ["0"],
					"routeType": ["1"],
					"rtNm": ["N6002"],
					"sectOrd1": ["0"],
					"sectOrd2": ["0"],
					"stId": ["121000977"],
					"stNm": ["강남고속버스터미널"],
					"staOrd": ["1"],
					"term": ["80"],
					"traSpd1": ["0"],
					"traSpd2": ["0"],
					"traTime1": ["0"],
					"traTime2": ["0"],
					"vehId1": ["0"],
					"vehId2": ["0"]
				}
			},
			...
		]
	}

*/
exports.load_nbus_info = () =>
	new Promise((resolve, reject) => {

		// load from cache
		if (exports.nbus_info.expired == false) {
			console.log(`load N-Bus info from cache`);
			return resolve();
		}

		// cache expired, fetch new
		console.log(`load N-Bus info from fetched`);

		let nbus_info_list = [];

		// request bus info list
		requestAndParseAsJSON({
			uri: `http://ws.bus.go.kr/api/rest/busRouteInfo/getBusRouteList`,
			qs: {
				serviceKey: keys.api_key.data_portal
			}
		}, 'xml')
		.then(parse_itemList)
		.then(info_list => {
			// get all N-bus and find stations of each route

			let promises = [];
			let i, info, option_getstations, option_arrive_info;

			option_getstations = {
				uri: `http://ws.bus.go.kr/api/rest/busRouteInfo/getStaionByRoute`,
				qs: {
					serviceKey: keys.api_key.data_portal,
					busRouteId: ''
				}
			};

			option_arrive_info = {
				uri: `http://ws.bus.go.kr/api/rest/arrive/getArrInfoByRouteAll`,
				qs: {
					serviceKey: keys.api_key.data_portal,
					busRouteId: ''
				}
			};

			for (i = 0; i < info_list.length; i++) {
				info = info_list[i];

				// take N-bus only
				if (U.json.get_value(info, null, "busRouteNm", 0, 0) != 'N') continue;

				// beautify: "property:[value]" -> "property:value"
				U.json.unwrap_properties(info);
				info.stations = [];
				nbus_info_list.push(info);

				// request to get all stations of the bus
				option_getstations.qs.busRouteId = info.busRouteId;
				option_arrive_info.qs.busRouteId = info.busRouteId;
				promises.push(
					requestAndParseAsJSON(option_getstations, 'xml')
					.then(parse_itemList)
					.then(U.json.unwrap_properties),
					requestAndParseAsJSON(option_arrive_info, 'xml')
					.then(parse_itemList)
					.then(U.json.unwrap_properties)
				);
			}

			Promise.all(promises)
			.then(jsons => {
				let i, stations, arriveInfos;
				for (i = 0; i < jsons.length; i += 2) {

					stations = jsons[i];
					arriveInfos = jsons[i + 1];

					// TODO: sort arriveInfo for optimization
					// save every arrive info into its station info
					for (let station of stations) {
						let stationNo = station.stationNo;
						station.arriveInfo = null;
						for (let arriveInfo of arriveInfos) {
							if (arriveInfo.arsId == stationNo) {
								station.arriveInfo = arriveInfo;
							}
						}
					}

					nbus_info_list[i / 2].stations = stations;
				}
			})
			.then(() => {
				update_cache(exports.nbus_info, nbus_info_list);
				resolve();
			});
		})
		.catch(err => {
			console.log(err);
			update_cache(exports.nbus_info, exports.nbus_info.list);
			resolve();
		});
	})
	.then(() => JSON.parse(JSON.stringify(exports.nbus_info.list))); // return deep copy

/*

	Return a pedestrian route with the time required.

	Example of a route:
	{
		time: 868,
		walking_distance: 1099,
		points: [
			[127.05353861565403, 37.5865481484368],
			...
		]
	}

*/
exports.get_pedestrian_route = (points) =>
	new Promise((resolve, reject) => {

		let i, passList;
		let result = {
			time: 0,
			walking_distance: 0,
			points: []
		};

		// handle exceptions
		if (points.length < 2)
			return reject('There should be 2 or more points.');
		
		passList = '';
		for (i = 1; i < points.length - 1; i++) {
			if (points[i].length == 2) {
				passList += `_${points[i][0]},${points[i][1]}`;
			}
		}
		passList = passList.slice(1);

		console.log(`points: ${points}`);
		let option = {
			method: "POST",
			uri: "https://apis.openapi.sk.com/tmap/routes/pedestrian?version=1",
			form: {
				appKey: keys.api_key.tmap,
				startX: points[0][0], // lon
				startY: points[0][1], // lat
				endX: points[points.length - 1][0], // lon
				endY: points[points.length - 1][1], // lat
				passList: passList,
				reqCoordType: "WGS84GEO", // WGS84GEO, EPSG3857
				resCoordType: "WGS84GEO", // WGS84GEO, EPSG3857
				startName: "출발지",
				endName: "도착지"
			}
		};

		// request pedestrian route
		requestAndParseAsJSON(option)
		.then(json => {
			if (json?.type != 'FeatureCollection')
				return;
			
			let i,
				feature,
				features = json.features || [];

			for (i = 0; i < features.length; i++) {
				feature = features[i];

				// handle exception: not a point
				if (feature?.type != 'Feature' ||
				feature?.geometry?.type != 'LineString' ||
				!feature?.geometry?.coordinates ||
				!feature?.properties?.lineIndex) continue;

				// push the point to the list
				result.points[feature.properties.lineIndex] = feature.geometry.coordinates;
				result.time += feature.properties.time ?? 0;
				result.walking_distance += feature.properties.distance ?? 0;
			}

			// delete all false-values
			result.points = result.points.filter(Boolean).flat();
		}, console.log)
		.then(() => resolve(result));
	});

/*

	update cache

*/
exports.bikestops = {
	expired: true,
	list: [],
	func_update: exports.load_bikestops,
	term_update: 200000
};

exports.nbus_info = {
	expired: true,
	list: [],
	func_update: exports.load_nbus_info,
	term_update: 200000
};

update_cache = (cache, list) => {
	// update cache
	cache.list = list;
	cache.expired = false;
	
	// after 200sec, expire cache and fetch new
	console.log(`Cache updated.`);
	setTimeout(() => {
		console.log(`Cache expired. Fetch new info.`);
		cache.expired = true;
		cache.func_update();
	}, cache.term_update);
};

/*

	Extract itemList from response json.

*/
parse_itemList = (json) =>
	U.json.get_value(json, [], "ServiceResult", "msgBody", 0, "itemList");

/*

	Request and decode the result to UTF8, and return it as JSON.

*/
requestAndParseAsJSON = (option, type = 'json') =>
	new Promise((resolve, reject) => {
		console.log(`request to ${option.url || option.uri}`);

		request(option)
		.then(result => iconv.decode(Buffer.from(result), 'utf8'))
		.then(decoded => {

			// json to json
			if (type == 'json') {
				return resolve(JSON.parse(decoded));
			}

			// xml to json
			if (type == 'xml') {
				return xml2js
				.parseStringPromise(decoded)
				.then(resolve)
				.catch(err => {
					console.log('Error on parseStringPromise: ', err);
					resolve({});
				});
			}

			// undefined type
			return decoded;
		})
		.catch(err => {
			console.log('Error on requestAndParseAsJSON: ', err);
			resolve({});
		});
	});