var express = require('express');
var router = express.Router();

var mongoose = require('mongoose');
var temperature = require('../models/temperature.js');

/* GET /temperatures listing. */
router.get('/', function(req, res, next) {
    temperature.find(null, null, { skip:0, limit:100, sort: { createdOn: -1 } }, function (err, temperatures) {
        if (err) return next(err);
        res.json(temperatures);
    });
});

/* POST /temperatures */
router.post('/', function(req, res, next) {
    temperature.create(req.body, function (err, post) {
        if (err) return next(err);
        res.json(post);
    });
});

module.exports = router;




// var express = require('express');
// var router = express.Router();

// /* GET temperatures. */
// router.get('/', function(req, res) {

// 	var filter = {};

// 	if (typeof req.param("sensor") !== 'undefined')
// 		filter = { sensor: req.param("sensor") };

//     var db = req.app.get('db');
//     db.get("temperatures").find(
//     	filter,
//     	{ 
// 			limit: 100, 
// 			sort: { created_on: 1 } 
// 		},
//     	function(e, docs) { res.json(docs); })
// })

// /*
//  * POST to temperatures.
//  */
// router.post('/', function(req, res) {
// 	console.log(req.body);
//     var db = req.app.get('db');
//     db.get("temperatures").insert(req.body, function(err, result){
//     	err ? res.json(501, err) : res.json(201, result)
//     });
// });

// module.exports = router;