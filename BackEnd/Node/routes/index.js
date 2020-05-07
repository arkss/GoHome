const R = require('../controllers/util').res;
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
		case 'bikestops':              bike.get_bikestops(req, res, next);              break;
		case 'bikestop_parked_counts': bike.get_bikestop_parked_counts(req, res, next); break;
		case 'nbus_info':              bus.get_nbus_info(req, res, next);               break;
		case 'nbus_near_stations':     bus.get_near_stations(req, res, next);           break;

		case 'routes':                 route.get_routes(req, res, next);                break;
		default:                       R.status(res, 404);                              break;
	}
});

module.exports = router;