var mongoose = require('mongoose');

var temperatureSchema = new mongoose.Schema({
	value: Number,
	sensor: String,
	beer: String,
	createdOn: { type: Date, default: Date.now },
});

module.exports = mongoose.model('temperature', temperatureSchema);