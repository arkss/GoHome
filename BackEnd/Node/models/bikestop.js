const mongoose = require('mongoose');

const BikestopSchema = new mongoose.Schema({
	stationId: {
		type: String,
		unique: true
	},
	stationName: String,
	stationLatitude: Number,
	stationLongitude: Number,
	traveltime: {
		type: Map,
		of: Number
	}
});

module.exports = mongoose.model('Bikestop', BikestopSchema);