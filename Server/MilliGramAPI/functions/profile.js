/**
 * http://usejsdoc.org/
 */

'use strict';

const user = require('../models/user');

exports.getProfile = email => 

	new Promise((resolve,reject) => {

		user.find({ email: email }, { name: 1, email: 1,mobile: 1, username:1, bio: 1, propicfilename: 1, verified: 1, created_at: 1, _id: 0 })

		.then(users => resolve(users[0]))

		.catch(err => reject({ status: 500, message: 'Internal Server Error !' }))

	});
