const mongoose = require('mongoose');

const BusRouteSchema = new mongoose.Schema({
	busRouteId: {
		type: String,
		unique: true
	},
	stations: [{
		type: String
	}]
});

module.exports = mongoose.model('BusRoute', BusRouteSchema);