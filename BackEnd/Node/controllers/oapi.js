const keys = require('../keys.json');
const U = require('./util');

const request = require('request-promise');
const xml2js = require('xml2js');
const iconv = require('iconv-lite');

exports.bikestops = {
	expired: true,
	list: []
};

exports.nbus_info = {
	expired: true,
	list: []
};

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
		exports.load_nbus_info();
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
			if (json_1 &&
				json_1.rentBikeStatus &&
				json_1.rentBikeStatus.row)
				list = list.concat(json_1.rentBikeStatus.row);

			if (json_2 &&
				json_2.rentBikeStatus &&
				json_2.rentBikeStatus.row)
				list = list.concat(json_2.rentBikeStatus.row);
		})
		.catch(console.log)
		.then(() => {
			// update cache
			exports.bikestops.list = list;
			exports.bikestops.expired = false;

			// after 200sec, expire cache and fetch new
			setTimeout(() => {
				console.log(`bikestops cache expired`);
				exports.bikestops.expired = true;
				exports.load_bikestops();
			}, 200000);

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
		let option = {
			uri: `http://ws.bus.go.kr/api/rest/busRouteInfo/getBusRouteList`,
			qs: {
				serviceKey: keys.api_key.data_portal
			}
		};

		let list = [];

		// request bus info list
		requestAndParseAsJSON(option, 'xml')
		.then(json => {
			// take N-bus only
			let temp_list = parse_itemList(json);
			let promises = [];
			let i, info;
			for (i = 0; i < temp_list.length; i++) {
				info = temp_list[i];
				if (!info.busRouteNm ||
					!info.busRouteNm[0] ||
					info.busRouteNm[0][0] != 'N')
					continue;

				// beautify: "property:[value]" -> "property:value"
				info = U.json.unwrap_properties(info);

				info.stations = [];
				list.push(info);

				// request stations of the bus
				promises.push(requestAndParseAsJSON({
					uri: `http://ws.bus.go.kr/api/rest/busRouteInfo/getStaionByRoute`,
					qs: {
						serviceKey: keys.api_key.data_portal,
						busRouteId: info.busRouteId
					}
				}, 'xml'));
			}

			// request stations of the bus
			Promise.all(promises)
			.then(jsons => {
				let i, itemList;
				for (i = 0; i < jsons.length; i++) {
					itemList = parse_itemList(jsons[i]);

					// beautify
					for (let info of itemList) {
						info = U.json.unwrap_properties(info);
					}

					list[i].stations = itemList;
				}
			})
			.catch(console.log)
			.then(() => {
				// update cache
				exports.nbus_info.list = list;
				exports.nbus_info.expired = false;
	
				// after 200sec, expire cache and fetch new
				setTimeout(() => {
					console.log(`N-Bus info cache expired`);
					exports.nbus_info.expired = true;
					exports.load_nbus_info();
				}, 200000);
	
				resolve();
			});
		}, console.log);
	})
	.then(() => JSON.parse(JSON.stringify(exports.nbus_info.list))); // return deep copy

// TODO: edit it
exports.get_route = () =>
	new Promise((resolve, reject) => {

		let option = {
			method: "POST",
			uri: "https://apis.openapi.sk.com/tmap/routes/pedestrian?version=1&format=json&callback=result",
			form: {
				appKey: keys.api_key.tmap,
				
				// test data
				startX: "126.977022",
				startY: "37.569758",
				endX: "126.997589",
				endY: "37.570594",
				passList: "126.987319,37.565778_126.983072,37.573028",
				reqCoordType: "WGS84GEO",
				resCoordType: "EPSG3857",
				startName: "출발지",
				endName: "도착지"
			}
		};

		let list = [];

		// request pedestrian route
		requestAndParseAsJSON(option)
		.then(json => {
			if (!json.type ||
				json.type != "FeatureCollection" ||
				!json.features)
				return;
			
			list = json.features;
		}, console.log)
		.then(() => resolve(list));
	});

/*

	Extract itemList from response json.

*/
parse_itemList = (json) => {
	if (!json ||
		!json.ServiceResult ||
		!json.ServiceResult.msgBody ||
		!json.ServiceResult.msgBody[0] ||
		!json.ServiceResult.msgBody[0].itemList) {
		console.log('itemList not found');
		return [];
	}
	return json.ServiceResult.msgBody[0].itemList;
};

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
			return result;
		})
		.catch(err => {
			console.log('Error on requestAndParseAsJSON: ', err.statusCode);
			resolve({});
		});
	});