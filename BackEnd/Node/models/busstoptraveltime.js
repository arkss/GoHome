const mongoose = require('mongoose');

const BusstopTraveltimeSchema = new mongoose.Schema({
	stationId_start: String, // Busstop.stationId
	stationId_end: String,   // Busstop.stationId
	traveltime: Number       // in sec
});

module.exports = mongoose.model('BusstopTraveltime', BusstopTraveltimeSchema);