var mongoose = require('mongoose');

var sensorSchema = new mongoose.Schema({
	name: String,
	deviceId: String,
	createdOn: { type: Date, default: Date.now },
});

module.exports = mongoose.model('sensor', sensorSchema);