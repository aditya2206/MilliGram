/**
 * http://usejsdoc.org/
 */

'use strict';

const auth = require('basic-auth');
const jwt = require('jsonwebtoken');
var express = require('express');
var formidable = require('formidable');
var fs = require('fs');
const register = require('./functions/register');
const profile = require('./functions/profile');
const userlogin = require('./functions/userlogin');
const uploadphotos = require('./functions/uploadphotos');
const config = require('./config/config.json');



module.exports = router => {
	
	router.get('/', (req, res) => res.end('Welcome!'));
	
	router.post('/users', (req, res) => {

		const name = req.body.name;
		const email = req.body.email;
		const mobile = req.body.mobile;
		const username = req.body.username;
		const password = req.body.password;
		const bio = req.body.bio;
		const propicfilename = req.body.propicfilename;

		if (!name || !email || !mobile || !username || !password || !bio || !propicfilename || !name.trim() || !email.trim() || !mobile.trim() || !username.trim() || !password.trim() || !bio.trim() || !propicfilename.trim() ) {

			res.status(400).json({message: 'Invalid Request !'});

		} else {

			register.registerUser(name, email,mobile,username, password,bio,propicfilename)

			.then(result => {

				res.setHeader('Location', '/users/'+email);
				res.status(result.status).json({ message: result.message })
			})

			.catch(err => res.status(err.status).json({ message: err.message }));
		}
	});
	
	router.post('/uploadpropic', function (req, res){
	    var form = new formidable.IncomingForm();

	    form.parse(req);

	    form.on('fileBegin', function (name, file){
	        file.path = '/Volumes/Macintosh HDD/Projects/Milligram/profilepics/' + file.name;
	    });

	    form.on('file', function (name, file){
	        console.log('Uploaded ' + file.name);
	    });
	    
	    res.json({'response':"Profile Photo Uploaded!"});

	});
	
	
	router.post('/uploadphotos', (req, res) => {

		const photofilename = req.body.photofilename;
		const photocaption = req.body.photocaption;
		const location = req.body.location;
		const uploadedby = req.body.uploadedby;
		const propicfilename = req.body.propicfilename;
		const uploadedbyusername = req.body.uploadedbyusername;
				

		if (!photofilename || !photocaption || !uploadedby  || !propicfilename || !uploadedbyusername || !photofilename.trim() || !photocaption.trim() || !uploadedby.trim() || !propicfilename.trim() || !uploadedbyusername.trim()) {

			res.status(400).json({message: 'Invalid Request !'});

		} else {

			uploadphotos.uploadPhotos(photofilename, photocaption, location, uploadedby, propicfilename,uploadedbyusername)

			.then(result => {

				res.status(result.status).json({ message: result.message })
			})

			.catch(err => res.status(err.status).json({ message: err.message }));
		}
	});
	
	
	router.post('/uploadphotosfile', function (req, res){
	    var form = new formidable.IncomingForm();

	    form.parse(req);

	    form.on('fileBegin', function (name, file){
	        file.path = '/Volumes/Macintosh HDD/Projects/Milligram/photos/' + file.name;
	    });

	    form.on('file', function (name, file){
	        console.log('Uploaded ' + file.name);
	    });
	    
	    res.json({'response':"Photo Uploaded!"});

	});
	
	
	
	router.post('/editbio', function(req,res){
		const email = req.body.email;
		const newbio = req.body.bio;
		
		var MongoClient = require('mongodb').MongoClient;
		var url = "mongodb://127.0.0.1:27017/";

		MongoClient.connect(url, function(err, db) {
		  if (err) throw err;
		  var dbo = db.db("milligram");
		  var myquery = { email: email };
		  var newvalues = { $set: { bio: newbio } };
		  dbo.collection("users").updateOne(myquery, newvalues, function(err, res) {
		    if (err) throw err;
		    console.log("Bio has been editted!")
		    db.close();
		  });
		});
		
		res.status(200).json({ message: 'Bio Updated Successfully!'})
		
	});
		
	
	
	
	router.post('/usersauthenticate', (req, res) => {

		const credentials = auth(req);

		if (!credentials) {

			res.status(400).json({ message: 'Invalid Request !' });

		} else {

			userlogin.loginUser(credentials.name, credentials.pass)
			
			
			.then(result => {
				
				const token = jwt.sign(result, config.secret, { expiresIn: 1440 })
				res.status(result.status).json({ message: result.message, token: token });
				
			})

			.catch(err => res.status(err.status).json({ message: err.message }));
			
		}
		
	});
	
	router.get('/users/:id', (req,res) => {

		if (checkToken(req)) {

			profile.getProfile(req.params.id)

			.then(result => res.json(result))

			.catch(err => res.status(err.status).json({ message: err.message }));

		} else {

			res.status(401).json({ message: 'Invalid Token !' });
		}
	});
	
	router.get('/getphotos',(req,res)=>{
	
		const photos = require('./models/photo');

		photos.find(function(err, photo) {

			if (err)
				res.send(err)

			const result = JSON.stringify(photo);
			const finalresult = '{"photos":'+result+'}';
			res.status(200).json({ message : finalresult }); 
		});
	
		
	});
	
	router.get('/getsingleuserphotos/:name',(req,res)=>{
		
		const photos = require('./models/photo');
		const name = req.params.name;
		photos.find({uploadedbyUsername: name}, function(err, photo) {

			if (err)
				res.send(err)

			const result = JSON.stringify(photo);
			const finalresult = '{"photos":'+result+'}';
			res.status(200).json({ message : finalresult }); 
		});
	
		
	});
	
	
	router.get('/getsingleuserprofile/:name',(req,res)=>{
		
		const user = require('./models/user');
		const name = req.params.name;
		user.find({username: name},{name: 1,email: 1, mobile: 1, username:1, bio: 1, propicfilename: 1, verified: 1, created_at: 1}, function(err, photo) {

			if (err)
				res.send(err)

			const result = JSON.stringify(photo);
			const finalresult = '{"user":'+result+'}';
			res.status(200).json({ message : finalresult }); 
		});
	
		
	});


	router.get('/searchUsersList/:name',(req,res)=>{
	
		const user = require('./models/user');
		const name = req.params.name;
		
		user.find({name:{'$regex' : name, '$options' : 'i'}},{name: 1, username: 1}, function(err, photo) {
	
			if (err)
				res.send(err)
	
			const result = JSON.stringify(photo);
			const finalresult = '{"user":'+result+'}';
			res.status(200).json({ message : finalresult }); 
		});

	
});
	
	router.get('/getparticularpic/:file', function (req, res){
		var file = req.params.file;
		var dirname = "/Volumes/Macintosh HDD/Projects/Milligram";
		var img = fs.readFileSync(dirname + "/photos/" + file);
		res.writeHead(200, {'Content-Type': 'image/jpg' });
		res.end(img, 'binary');

});
	
	router.get('/getpropic/:file', function (req, res){
		var file = req.params.file;
		var dirname = "/Volumes/Macintosh HDD/Projects/Milligram";
		var img = fs.readFileSync(dirname + "/profilepics/" + file);
		res.writeHead(200, {'Content-Type': 'image/jpg' });
		res.end(img, 'binary');

});
	
	
	function checkToken(req) {

		const token = req.headers['x-access-token'];

		if (token) {

			try {

  				var decoded = jwt.verify(token, config.secret);

  				return decoded.message === req.params.id;

			} catch(err) {

				return false;
			}

		} else {

			return false;
		}
	}
	
	
}