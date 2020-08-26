const express = require('express');
const router = express.Router();
module.exports = router;


router.post('/exist/code', async(req, res)=>{
	var resObj;
	try{
		resObj.rows = await db.select("device", 
			null, 
			req.body, 
			null);
		resObj.result = 1001;
		resObj.exist = rows.length !=0;
	}catch(err){
		resObj.err = err;
	}
	res.json(resObj);
});


router.post('/register/code', async(req, res)=>{
	var resObj;
	try{
		resObj.rows = await db.insert("device", 
			req.body);
		resObj.result = 1001;
		setClientFolder(req.body.did);
		// set default imgs
	}catch(err){
		resObj.err = err;
	}
	res.json(resObj);
});


router.post('/reset', async(req, res)=>{
	var resObj;
	try{
		await db.delete("device", req.body, null);
		await db.delete("img_resource", req.body, null);
		resObj.rows = await db.insert("device", 
			req.body);
		resObj.result = 1001;
		// set default imgs
	}catch(err){
		resObj.err = err;
	}
	res.json(resObj);
});


router.post('/update/options', async(req, res)=>{
	var resObj;
	try{
		await db.delete("device", {"did" : "temp"}, null);
		resObj.rows = await db.insert("device", 
			req.body);
		resObj.result = 1001;
	}catch(err){
		resObj.err = err;
	}
	res.json(resObj);
});


router.post('/update/imgs', async(req, res)=>{
	var resObj;
	try{
		await db.delete("img_resource", {"did" : "temp"}, null);
		resObj.rows = await db.insert("img_resource", 
			req.body);
		resObj.result = 1001;
	}catch(err){
		resObj.err = err;
	}
	res.json(resObj);
});


router.post('/get/imgs', async(req, res)=>{
	var resObj;
	try{
		resObj.rows = await db.select("img_resource", 
			null, 
			req.body, 
			null);
		resObj.result = 1001;
	}catch(err){
		resObj.err = err;
	}
	res.json(resObj);
});




function setClientFolder(id) {
	let thumbPath = "./public/thumb/" + id;
	let resPath = "./public/res/" + id;
	if (!fs.existsSync(thumbPath))
		fs.mkdirSync(thumbPath);
	if (!fs.existsSync(resPath))
		fs.mkdirSync(resPath);
}

