const R = require('../controllers/util').res;
const bike = require('../controllers/bike');
const bus = require('../controllers/bus');
const express = require('express');
const router = express.Router({ mergeParams: true });

router.get('/', (req, res, next) => {
	res.json({ message: 'hi' });
});

/* RESTful API - GET */
router.get('/api/:model', (req, res, next) => {
	switch (req.params.model) {
		case 'bikeList':  bike.get_bikestops(req, res, next); break;
		case 'nbus_info': bus.get_nbus_info(req, res, next);  break;
		default:          R.status(res, 404);             break;
	}
});

module.exports = router;