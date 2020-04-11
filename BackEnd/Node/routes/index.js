const R = require('../controllers/response');
const bike = require('../controllers/bike');
const express = require('express');
const router = express.Router({ mergeParams: true });

router.get('/', (req, res, next) => {
	res.json({ message: 'hi' });
});

/* RESTful API - GET */
router.get('/api/:model', (req, res, next) => {
	switch (req.params.model) {
		case 'bikeList': bike.get_bikestops(req, res, next); break;
		default:         R.status(res, 404);                 break;
	}
});

module.exports = router;