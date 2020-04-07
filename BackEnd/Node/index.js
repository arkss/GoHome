const express = require('express');
const session = require('express-session');
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
	res.status(404).send('404 file not found');
});

// handle 500
app.use((err, req, res, next) => {
	console.log(`[ERROR] ${err.stack || err}`);
	res.status(500).send('500 internal server error');
});

// start server
server.listen(port, () => {
	console.log(`listen now with port:${port}`);
});