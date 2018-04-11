/**
 * http://usejsdoc.org/
 */


'use strict';

const user = require('../models/user');
const bcrypt = require('bcryptjs');

exports.registerUser = (name, email, mobile, username, password, bio, propicfilename) => 

	new Promise((resolve,reject) => {

	    const salt = bcrypt.genSaltSync(10);
		const hash = bcrypt.hashSync(password, salt);

		const newUser = new user({

			name: name,
			email: email,
			mobile: mobile,
			username: username,
			hashed_password: hash,
			bio: bio,
			propicfilename: propicfilename,
			verified: 'false',
			created_at: new Date()
		});

		newUser.save()

		.then(() => resolve({ status: 201, message: 'User Registered Sucessfully !' }))

		.catch(err => {

			if (err.code == 11000) {

				reject({ status: 409, message: 'User Already Registered with a similar Email or Username! Try using a Different one!' });

			} else {

				reject({ status: 500, message: 'Internal Server Error !' });
			}
		});
	});