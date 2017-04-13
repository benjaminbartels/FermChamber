var mongoose = require('mongoose');

var beerSchema = new mongoose.Schema({
	name: String,
	maxTemp: Number,
	minTemp: Number,
	isEnabled: Boolean,
	createdOn: { type: Date, default: Date.now },
}, { toJSON: { virtuals: true } });


module.exports = mongoose.model('beer', beerSchema);