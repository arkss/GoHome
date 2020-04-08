const R = require('../controllers/response');
const oapi = require('../controllers/oapi');
const express = require('express');
const router = express.Router({ mergeParams: true });

router.get('/', (req, res, next) => {
	res.json({ message: 'hi' });
});

/* FUNCTION */
router.post('/api/:model', (req, res, next) => {
	switch (req.params.model) {
		case 'bikeList': oapi.load_bikelist(req, res, next); break;
		default: 	     R.status(res, 404);                 break;
	}
});

module.exports = router;