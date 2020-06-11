const mongoose = require('mongoose');

const BusstopSchema = new mongoose.Schema({
	stationId: {
		type: String,
		unique: true
	},
	stationName: String,
	stationLatitude: Number,
	stationLongitude: Number
});

module.exports = mongoose.model('Busstop', BusstopSchema);