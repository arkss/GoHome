const U = require('./controllers/util');
const bike = require('./controllers/bike');
const bus = require('./controllers/bus');
const express = require('express');
const mongoose = require('mongoose');
const cors = require('cors');
const fs = require('fs');

const router = require('./routes');
const keys = require('./keys.json');
const app = express();

var http_server = null;
var https_server = null;
http_server = require('http').createServer(app);
if (keys.SSL) {
	https_server = require('https').createServer({
		ca: fs.readFileSync('/etc/letsencrypt/live/gohome-node.com/fullchain.pem'),
		key: fs.readFileSync('/etc/letsencrypt/live/gohome-node.com/privkey.pem'),
		requestCert: false,
		rejectUnauthorized: false
	}, app);
}
var http_port = keys.http_port;
var https_port = keys.https_port;

// automatically allow cross-origin requests
app.use(cors({ origin: true }));

// settings
app.use(express.static('public'));
app.use(express.urlencoded({ extended: true }));
app.use(express.json());

// log all requests
app.all('*', (req, res, next) => {
	let protocol = req.headers['x-forwarded-proto'] || req.protocol;
	let from = `${protocol}://${req.hostname}${req.url}`;

	U.log(`[${req.method} ${req.ip}] ${from}`);
	next();
});

// route
app.use('/', router);

// handle 404
app.use((req, res, next) => {
	U.status(res, 404);
});

// handle 500
app.use((err, req, res, next) => {
	U.error(err);
	U.status(res, 500);
});

// start server
U.log(`
  /$$$$$$            /$$   /$$                                  
 /$$__  $$          | $$  | $$                                  
| $$  \\__/  /$$$$$$ | $$  | $$  /$$$$$$  /$$$$$$/$$$$   /$$$$$$ 
| $$ /$$$$ /$$__  $$| $$$$$$$$ /$$__  $$| $$_  $$_  $$ /$$__  $$
| $$|_  $$| $$  \\ $$| $$__  $$| $$  \\ $$| $$ \\ $$ \\ $$| $$$$$$$$
| $$  \\ $$| $$  | $$| $$  | $$| $$  | $$| $$ | $$ | $$| $$_____/
|  $$$$$$/|  $$$$$$/| $$  | $$|  $$$$$$/| $$ | $$ | $$|  $$$$$$$
\\______/  \\______/ |__/  |__/ \\______/ |__/ |__/ |__/ \\_______/
`);
U.log(`Starting server...`);
mongoose.Promise = global.Promise;
mongoose.connect(keys.url_mongodb, {
	dbName: keys.db_name,
	useCreateIndex: true,
	useNewUrlParser: true,
	useUnifiedTopology: true,
	useFindAndModify: false // https://mongoosejs.com/docs/deprecations.html#findandmodify
})
.then(async () => {
	U.log(`Succeessfully connected to ${keys.url_mongodb}.`);

	await bike.load_cache_from_db();
	await bus.load_cache_from_db();
	U.log(`Succeessfully load biekstop cache.`);

	http_server.listen(http_port, () => {
		U.log(`Server listen now with ${http_port} port.`);
	});
	https_server?.listen(https_port, () => {
		U.log(`Server listen now with ${https_port} port.`);
	});
})
.catch(U.error);

// prevent termination due to uncaughtException
process.on('uncaughtException', U.error);
