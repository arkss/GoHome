const keys = require('../keys.json');
const R = require('./response');
const request = require('request-promise');
const xml2js = require('xml2js');
const iconv = require('iconv-lite');

// http://openapi.seoul.go.kr:8088/564c42436667756e393665726c6356/json/bikeList/1/1000/

/*

	method: POST
	body:
		(none)

*/
exports.load_bikelist = (req, res, next) => {
	let base_url = `http://openapi.seoul.go.kr:8088/${keys.api_key_seoul_opendata_1}/json/bikeList`;
	let option = {
		qs: {},
		encoding: null
	};

	let n = 0;
	let promise = Promise.resolve();
	let row = [];

	end_request = () => {
		R.response(res, true, `${row.length} data loaded`);
	};
	
	request1000 = () => {
		n += 1000;
		option.url = `${base_url}/${n - 999}/${n}/`;
		requestAndParseJson(option)
		.then(json => {
			// handle exceptions
			if (!json.rentBikeStatus) return end_request();
			if (!json.rentBikeStatus.list_total_count) return end_request();
			if (!json.rentBikeStatus.row) return end_request();

			// concat row
			row = row.concat(json.rentBikeStatus.row);

			if (json.rentBikeStatus.list_total_count == 1000) {
				// load more
				promise = promise.then(request1000);
			} else {
				// end
				end_request();
			}
		});
	};

	promise.then(request1000);
};

requestAndParseJson = (option) => {
	console.log(`request to ${option.url}`);
	return request(option)
	.then(strJSON => JSON.parse(iconv.decode(Buffer.from(strJSON), 'utf8')))
	.catch(err => {
		console.log('Error on requestAndParse: ', err);
		return {};
	});
};

parseXml = (strXML) =>
	new Promise((resolve, reject) => {
		new xml2js.Parser(xml2js.defaults["0.2"])
		.parseString(strXML, (err, data) => {
			if (err)
				reject(err);
			else
				resolve(data);
		});
	});