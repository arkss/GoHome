const U = require('../controllers/util');
const bike = require('../controllers/bike');
const bus = require('../controllers/bus');
const route = require('../controllers/route');
const express = require('express');
const router = express.Router({ mergeParams: true });

router.get('/', (req, res, next) => {
	res.json({ message: 'hi' });
});

/* RESTful API - GET */
router.get('/api/:model', async (req, res, next) => {
	// start_time
	let start_time = Date.now();

	switch (req.params.model) {
		case 'bikestops':              await bike.api_get_bikestops(req, res, next);              break;
		case 'bikestop_parked_counts': await bike.api_get_bikestop_parked_counts(req, res, next); break;
		//case 'nbus_info':            await bus.api_get_nbus_info(req, res, next);               break;
		case 'nbus_routes':            await bus.api_get_nbus_routes(req, res, next);             break;
		case 'nbus_near_stations':     await bus.api_get_near_stations(req, res, next);           break;
		
		case 'routes':                 await route.api_get_routes(req, res, next);                break;
		default:                       await U.status(res, 404);                                  break;
	}

	// end_time
	let end_time = Date.now();

	// log processing time
	U.responseTime(`[${req.params.model}] res ${end_time - start_time}ms`);
});

module.exports = router;