const U = require('./controllers/util');
const bike = require('./controllers/bike');
const bus = require('./controllers/bus');
const express = require('express');
const session = require('express-session');
const mongoose = require('mongoose');
const cors = require('cors');
const router = require('./routes');
const keys = require('./keys.json');
const app = express();

var server = require('http').createServer(app);
var io = require('socket.io')(server);
var port = process.env.PORT || keys.port;

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

	server.listen(port, () => {
		U.log(`Server listen now with ${port} port.`);
	});
})
.catch(U.error);

// prevent termination due to uncaughtException
process.on('uncaughtException', U.error);
