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
router.get('/api/:model', (req, res, next) => {
	switch (req.params.model) {
		case 'bikestops':              bike.api_get_bikestops(req, res, next);              break;
		case 'bikestop_parked_counts': bike.api_get_bikestop_parked_counts(req, res, next); break;
<<<<<<< HEAD
		//case 'nbus_info':              bus.api_get_nbus_info(req, res, next);               break;
		case 'nbus_routes':            bus.api_get_nbus_routes(req, res, next);             break;
		case 'nbus_near_stations':     bus.api_get_near_stations(req, res, next);           break;

		case 'routes':                 route.api_get_routes(req, res, next);                break;
		default:                       U.status(res, 404);                                  break;
=======
		case 'nbus_info':              bus.api_get_nbus_info(req, res, next);               break;
		case 'nbus_near_stations':     bus.api_get_near_stations(req, res, next);           break;

		case 'routes':                 route.api_get_routes(req, res, next);                break;
		default:                       R.status(res, 404);                                  break;
>>>>>>> client/merge-AR
	}
});

module.exports = router;