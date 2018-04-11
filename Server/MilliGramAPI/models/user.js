/**
 * http://usejsdoc.org/
 */


'use strict';

const mongoose = require('mongoose');

const Schema = mongoose.Schema;

const userSchema = mongoose.Schema({ 

	name 			: String,
	email			: String,
	mobile			: String,
	username		: String,
	bio				: String,
	propicfilename	: String,
	verified		: String,
	hashed_password	: String,
	created_at		: String,
	temp_password	: String,
	temp_password_time: String

});

mongoose.Promise = global.Promise;
mongoose.connect('mongodb://127.0.0.1:27017/milligram');

module.exports = mongoose.model('user', userSchema);
