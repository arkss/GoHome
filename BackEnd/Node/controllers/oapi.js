const keys = require('../keys.json');
const R = require('./response');
const request = require('request-promise');
const xml2js = require('xml2js');
const iconv = require('iconv-lite');

exports.bikestops = {
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

		Promise.all([
			requestAndParseAsJSON(option_1, 'json'),
			requestAndParseAsJSON(option_2, 'json')
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
		}, console.log)
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

	Request and decode the result to UTF8, and return it as JSON.

*/
requestAndParseAsJSON = (option, type) =>
	new Promise((resolve, reject) => {
		console.log(`request to ${option.url}`);

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
			console.log('Error on requestAndParseAsJSON: ', err);
			resolve({});
		});
	});