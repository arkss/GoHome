/* Usage
	response(res, true, 'success!');
*/
exports.response = (res, success, message, data = {}) => {
	console.log(`[RESPONSE] ${message}`);
	if (res) {
		res.json({
			result: success ? 1 : -1,
			message: message ? message : (success ? 'success' : 'fail'),
			data: data
		});
	}
};

/* Usage
	if (isInvalid(res, req.body.p1, req.body.p2, ...)) return;
*/
exports.isInvalid = (res, ...args) => {
	let b = args.some(v => (v == null || v == undefined || v.length == 0));
	if (b) exports.response(res, false, 'incomplete query');
	return b;
};

exports.status = (res, code) => {
	let message = '';
	switch (code) {
		case 404: message = '404 not found';             break;
		case 500: message = '500 internal server error'; break;
		default:                                         break;
	}
	console.log(`[STATUS ${code}] ${message}`);
	if (res)
		res.status(code).send(message);
};