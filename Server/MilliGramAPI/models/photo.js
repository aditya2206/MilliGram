/**
 * http://usejsdoc.org/
 */


'use strict';

const mongoose = require('mongoose');

const Schema = mongoose.Schema;

const photosSchema = mongoose.Schema({ 

	photofilename 			: String,
	photocaption			: String,
	location				: String,
	uploadedby				: String,
	propicfilename			: String,
	created_at				: String,
	uploadedbyUsername		: String


});

mongoose.Promise = global.Promise;
mongoose.connect('mongodb://127.0.0.1:27017/milligram');

module.exports = mongoose.model('photo', photosSchema);
