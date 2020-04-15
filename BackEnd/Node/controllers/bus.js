const U = require('./util');
const oapi = require('./oapi');

// TODO: edit comment
/*

	method: GET

*/
// CHECK: experimental
exports.get_nbus_info = (req, res, next) => {
	oapi
	.load_nbus_info()
	.then(nbus_info => {

		// response
		U.res.response(res, true, `${nbus_info.length} found`, {
			n: nbus_info.length,
			nbus_info: nbus_info
		});
	});
};
