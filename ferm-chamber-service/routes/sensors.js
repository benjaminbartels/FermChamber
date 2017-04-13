var express = require('express');
var router = express.Router();

var mongoose = require('mongoose');
var sensor = require('../models/sensor.js');

/* GET /sensors listing. */
router.get('/', function(req, res, next) {
	sensor.find(function (err, sensors) {
		if (err) return next(err);
		res.json(sensors);
	});
});

/* POST /sensors */
router.post('/', function(req, res, next) {
    sensor.create(req.body, function (err, post) {
        if (err) return next(err);
        res.json(post);
    });
});

module.exports = router;

// var express = require('express');
// var router = express.Router();

// /* GET sensors. */
// router.get('/', function(req, res) {
//     var db = req.app.get('db');
//     db.get("sensors").find({}, { limit: 100 }, function(e, docs) {
// 		res.json(docs);
// 	})
// })

// module.exports = router;