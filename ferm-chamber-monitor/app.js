var fs = require('fs');
var Client = require('node-rest-client').Client;
var gpio = require("pi-gpio");

function getTemp(sensor) {

	fs.exists("/sys/bus/w1/devices/" + sensor.deviceId + "/w1_slave", function (exists) {

		fs.readFile("/sys/bus/w1/devices/" + sensor.deviceId + "/w1_slave", 'utf8',
			function (err, data) {

				if (err) throw err;
			
				var temperature = parseInt(data.split("=")[2]) / 1000;

				var args = {
					data: {value: temperature, sensor: sensor.name, beer: beer.name},
					headers:{"Content-Type": "application/json"} 
				};

				client.post("http://localhost:3000/temperatures", args, function(data, response) {

				    console.log("Saving sensor data: " + sensor.name + " = " + temperature);

				});

				if (sensor.name == 'Fermenter') {

					if (parseFloat(temperature) > parseFloat(beer.maxTemp)) {

						console.log("Fermenter temperature above threshold.");

						gpio.open(11, "output", function(err) {
					    		gpio.write(11, 1, function() { 
					        		gpio.close(11);
					    		});
						});
					}
					else {
						gpio.open(11, "output", function(err) {
					    		gpio.write(11, 0, function() {
					        		gpio.close(11);
					    		});						    		
						});

					}

				}

			});
	});

}

function loop() {

	for (i = 0; i < sensors.length; i++) { 

		getTemp(sensors[i])
	}

	    
	setTimeout(loop, 30000);

}

client = new Client();

var sensors;
var beer;

gpio.open(11, "output", function(err) {
    gpio.write(11, 0, function() {
        gpio.close(11);
    });
});

client.get("http://localhost:3000/beers", function(data, response){

    beers = JSON.parse(data);

	for (i = 0; i < beers.length; i++) { 
		if (beers[i].isEnabled) {
			beer = beers[i];
			break;
		}
	}

	client.get("http://localhost:3000/sensors", function(data, response){

	    sensors = JSON.parse(data);

		loop();

	});

});

