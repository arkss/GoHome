const winston = require('winston');
const { format } = require('winston');
const moment = require('moment');
require('moment-timezone');
const fs = require('fs');
const logDir = './.log';

// set timezone and make a directory to save logs
moment.tz.setDefault("Asia/Seoul");
if (!fs.existsSync(logDir)) {
	fs.mkdirSync(logDir);
}

// define formats
const timestampFormat = () => moment().format('YYYY-MM-DD HH:mm:ss.SSS ZZ');
const printfFormat = i => `${i.timestamp} [${i.level}]: ${i.message}`;
const combinedFormat = format.combine(
	format.timestamp({ format: timestampFormat }),
	format.colorize(),
	format.align(),
	format.errors({ stack: true }),
	format.printf(printfFormat)
);

/*

	Log the message as info-level.

*/
exports.log = message => {
	let logger = winston.createLogger({
		transports: [
			new winston.transports.Console({
				format: combinedFormat
			}),
			new winston.transports.File({
				filename: `${logDir}/log_${moment().format("YYYYMMDD")}.log`,
				format: combinedFormat
			})
		]
	});

	try {
		logger.info(message);
	} catch (e) {
		logger.error("ERROR => " + e);
	}
}