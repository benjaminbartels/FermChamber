var express = require('express');
var router = express.Router();

var mongoose = require('mongoose');
var beer = require('../models/beer.js');

/* GET /beers listing. */
router.get('/', function(req, res, next) {
    beer.find(function (err, beers) {
        if (err) return next(err);
        res.json(beers);
    });
});

/* POST /beers */
router.post('/', function(req, res, next) {
    beer.create(req.body, function (err, post) {
        if (err) return next(err);
        res.json(post);
    });
});

module.exports = router;

// var express = require('express');
// var router = express.Router();

// /* GET beer profiles. */
// router.get('/', function(req, res) {

// 	var filter = {};

// 	if (typeof req.param("is_enabled") !== 'undefined') {

// 		var isEnabled = parseInt(req.param("is_enabled"))

// 		if (isEnabled == 0)
//     		filter = { is_enabled: false }
//     	else if (isEnabled == 1)
//     		filter = { is_enabled: true }
//     	else
//     		filter = { is_enabled: isEnabled }
// 	}

//     var db = req.app.get('db');
//     db.get("beer_profiles").find(
//     	filter,
//     	{ limit: 100 },
//     	function(e, docs) { res.json(docs); })
// })

// module.exports = router;