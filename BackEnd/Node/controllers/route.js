const U = require('./util');
const oapi = require('./oapi');

// TODO: edit it
exports.route_test = (req, res, next) => {
	oapi
	.get_pedestrian_route()
	.then(list => {

		// response
		U.res.response(res, true, `route found`, list);
	});
};
