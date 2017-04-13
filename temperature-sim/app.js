var fs = require('fs');

var temp1GoingUp = true;
var temp1Lower = 12000;
var temp1Upper = 18000;
var temp2GoingUp = true;
var temp2Lower = 7000;
var temp2Upper = 12000;
var temp3GoingUp = true;
var temp3Lower = 21000;
var temp3Upper = 26000;

function updateTemp(tempName, goingUp, lower, upper) {

	if (fs.existsSync(tempName))
		var temp = fs.readFileSync(tempName, 'utf8');	
	else
		var temp = lower;

	if (goingUp) 
		temp = +temp + 100;
	else
		temp = temp - 100;  

	fs.writeFileSync(tempName, temp);

	console.log(tempName + " = " + temp + " " + (goingUp ? "=>" : "<="));

	if (+temp <= +lower)
		goingUp = true; 
	else if (+temp >= +upper)
		goingUp = false;	

	return goingUp;		

}

function loop() {

	temp1GoingUp = updateTemp('device1', temp1GoingUp, temp1Lower, temp1Upper);
	temp2GoingUp = updateTemp('device2', temp2GoingUp, temp2Lower, temp2Upper);
	temp3GoingUp = updateTemp('device3', temp3GoingUp, temp3Lower, temp3Upper);
	setTimeout(loop, 15000);

}

loop();


