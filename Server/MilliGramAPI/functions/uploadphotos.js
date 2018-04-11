/**
 * http://usejsdoc.org/
 */


const photo = require('../models/photo');

exports.uploadPhotos = (photofilename,photocaption,location,uploadedby,propicfilename,uploadedbyusername) =>

		new Promise((resolve,reject) =>{
			
			const newPhoto = new photo({
				
				photofilename 			: photofilename,
				photocaption			: photocaption,
				location				: location,
				uploadedby				: uploadedby,
				propicfilename			: propicfilename,
				uploadedbyUsername		: uploadedbyusername,
				created_at				: new Date().getTime()
				
				
			});
			
			newPhoto.save()
			
			.then(() => resolve({ status: 201, message: 'Photo Uploaded Sucessfully !' }))

			.catch(err => {

				if (err.code == 11000) {

					reject({ status: 409, message: 'Photo Duplicate Found !' });

				} else {

					reject({ status: 500, message: 'Internal Server Error !' });
				}
			});
			
			
		});
		