const express = require('express');
const router = express.Router();
const path = require("path");
const multer = require('multer');
const fs = require("fs");
const fse = require('fs-extra');
const ffmpeg = require('fluent-ffmpeg');

const mainDir = path.dirname(require.main.filename);
const rootPath = __dirname.substring(0, __dirname.lastIndexOf('\\'));
const videoTypes = ['.mp4', '.avi', '.wmv', '.mov', '.flv',];

module.exports = router;


let upload = multer({
	storage:  multer.diskStorage({	
		destination: (req, file, callback) => {
			callback(null, "public/res/" + req.body.did + '/');
		},
		filename: (req, file, callback) => {
			let extension = path.extname(file.originalname); // 확장자
			let basename = path.basename(file.originalname, extension); //  파일이름
			callback(null, file.originalname);
		}
	})
});


router.post('/upload', upload.array('fileName', 10), async(req, res) => {  
	for await (const file of req.files){
		console.log("### Upload file ###");
		console.log(file);

		const originalName = file.filename.toString();
		const divisionPos = originalName.lastIndexOf('.');
		const extension = originalName.substring(divisionPos);
		const name = originalName.substring(0, divisionPos);

		var originalPath = mainDir + '\\' + file.path;
		var thumbPath = mainDir + '\\public\\thumb\\' + req.body.did;

		if(videoTypes.includes(extension)){
			const proc = new ffmpeg(originalPath).takeScreenshots({
				count: 1,
				timemarks: [ '5' ], // number of seconds
				filename : fName + '.png',
			}, thumbPath, (err) => {
				if(err)
					console.log(err);
			});
		}else{
			thumbPath = thumbPath + '\\' + file.filename;
			try{
				await fse.copy(originalPath, thumbPath);
			}catch(err){
				console.log(err);
			}			
		}
	}
	res.status(204).end(); 
});


router.post('/download/:did/:filename', (req, res)=>{
	var filePath = rootPath + "/public/res/" + req.params.did + '/' + req.params.fileName;
	res.download(filePath);
});