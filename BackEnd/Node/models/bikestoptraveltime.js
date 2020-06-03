const mongoose = require('mongoose');

const BikestopTraveltimeSchema = new mongoose.Schema({
	stationId_start: String, // Bikestop.stationId
	stationId_end: String,   // Bikestop.stationId
	traveltime: Number       // in sec
});

module.exports = mongoose.model('BikestopTraveltime', BikestopTraveltimeSchema);