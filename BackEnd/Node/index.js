const R = require('./controllers/util').res; 
const express = require('express');
const session = require('express-session');
const mongoose = require('mongoose');
const router = require('./routes');
const keys = require('./keys.json');
const app = express();

var server = require('http').createServer(app);
var io = require('socket.io')(server);
var port = process.env.PORT || keys.port;
const MONGODB_URL = keys.url_mongodb;

// set middleware
var sessionMiddleWare = session({
	secret: keys.session_secret,
	resave: false,
	saveUninitialized: true
});
app.use(sessionMiddleWare);
io.use((socket, next) => {
	sessionMiddleWare(socket.request, socket.request.res, next);
});

// settings
app.use(express.static('public'));
app.use(express.urlencoded({ extended: true }));
app.use(express.json());

// log all requests
app.all('*', (req, res, next) => {
	let protocol = req.headers['x-forwarded-proto'] || req.protocol;
	let from = `${protocol}://${req.hostname}${req.url}`;

	console.log(`[${req.method} ${req.ip}] ${from}`);
	next();
});

// route
app.use('/', router);

// handle 404
app.use((req, res, next) => {
	R.status(res, 404);
});

// handle 500
app.use((err, req, res, next) => {
	console.log(`[ERROR] ${err.stack || err}`);
	R.status(res, 500);
});

// start server
console.log(`
  /$$$$$$            /$$   /$$                                  
 /$$__  $$          | $$  | $$                                  
| $$  \\__/  /$$$$$$ | $$  | $$  /$$$$$$  /$$$$$$/$$$$   /$$$$$$ 
| $$ /$$$$ /$$__  $$| $$$$$$$$ /$$__  $$| $$_  $$_  $$ /$$__  $$
| $$|_  $$| $$  \\ $$| $$__  $$| $$  \\ $$| $$ \\ $$ \\ $$| $$$$$$$$
| $$  \\ $$| $$  | $$| $$  | $$| $$  | $$| $$ | $$ | $$| $$_____/
|  $$$$$$/|  $$$$$$/| $$  | $$|  $$$$$$/| $$ | $$ | $$|  $$$$$$$
 \\______/  \\______/ |__/  |__/ \\______/ |__/ |__/ |__/ \\_______/
`);
console.log(`Connecting to ${MONGODB_URL}...`);
mongoose.connect(MONGODB_URL, {
		useCreateIndex: true,
		useNewUrlParser: true,
		useUnifiedTopology: true
	}, (err, res) => {
		if (err) {
			console.log(`ERROR while connecting to ${MONGODB_URL}: ${err}`);
		} else {
			console.log(`Succeessfully connected to ${MONGODB_URL}.`);
			server.listen(port, () => {
				console.log(`Server listen now with ${port} port.`);
			});
		}
	}
);

// prevent termination due to uncaughtException
process.on('uncaughtException', console.log);
