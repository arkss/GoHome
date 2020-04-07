var express = require('express');
var router = express.Router({ mergeParams: true });

router.get('/', (req, res, next) => {
	res.json({ message: 'hi' });
});

module.exports = router;