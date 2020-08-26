const express = require('express');
const bodyParser = require('body-parser');
const path = require('path');


const db = require('./myDB');
db.init();


var app = express();
app.set('port', process.env.PORT || 3000);
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({
  extended: false
}));


app.listen(app.get('port'), function() {
  console.log("# Start Server..");
});


var deviceRouter = require('./routes/device');
app.use('/device', deviceRouter);