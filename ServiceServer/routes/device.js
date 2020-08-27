const express = require('express');
const router = express.Router();
module.exports = router;

const fs = require('fs');
const db = require('../myDB');

var Sample_Res = [{"_order" : "0", "usf" : "0", "filename" : "sample1.jpg"},
{"_order" : "1", "usf" : "0", "filename" : "sample2.jpg"},
{"_order" : "2", "usf" : "0", "filename" : "sample3.jpg"},
{"_order" : "3", "usf" : "0", "filename" : "sample4.jpg"},];


router.post('/exist/code', async(req, res)=>{	
	var resObj = {};
	try{
		resObj.rows = await db.select("device", 
			null, 
			req.body, 
			null);
		resObj.result = 1001;
		resObj.exist = resObj.rows.length !=0;
	}catch(err){
		console.log(err);
	}
	res.json(resObj);
});


router.post('/register/code', async(req, res)=>{
	var resObj = {};
	try{
		resObj.rows = await db.insert("device", 
			req.body);
		let resPath = "./public/res/" + req.body.did;
		if (!fs.existsSync(resPath))
			fs.mkdirSync(resPath);
		for await(var imgRes of Sample_Res){
			imgRes.did = req.body.did;
		}
		resObj.rows = await db.insert("img_resource", Sample_Res);
		resObj.result = 1001;
	}catch(err){
		resObj.result = err.errno;
		resObj.err = err.code;
	}
	console.log(resObj);
	res.json(resObj);
});


router.post('/get/options', async(req, res)=>{
	var resObj = {};
	try{
		resObj.rows = await db.select("device", 
			null, 
			req.body, 
			null);
		resObj.result = 1001;
	}catch(err){
		resObj.result = err.errno;
		resObj.err = err.code;
	}
	console.log(resObj);
	res.json(resObj);
});


router.post('/reset', async(req, res)=>{
	var resObj = {};
	try{
		await db.delete("device", req.body, null);
		await db.delete("img_resource", req.body, null);
		resObj.rows = await db.insert("device", 
			req.body);
		for await(var imgRes of Sample_Res){
			imgRes.did = req.body.did;
		}
		resObj.rows = await db.insert("img_resource", Sample_Res);
		resObj.result = 1001;
	}catch(err){
		resObj.result = err.errno;
		resObj.err = err.code;
	}
	console.log(resObj);
	res.json(resObj);
});


router.post('/update/options', async(req, res)=>{
	var resObj = {};
	try{
		await db.delete("device", {"did" : req.body.did}, null);
		resObj.rows = await db.insert("device", 
			req.body);
		resObj.result = 1001;
	}catch(err){
		resObj.result = err.errno;
		resObj.err = err.code;
	}
	console.log(resObj);
	res.json(resObj);
});


router.post('/update/imgs', async(req, res)=>{
	var resObj = {};
	try{
		await db.delete("img_resource", {"did" : "temp"}, null);
		resObj.rows = await db.insert("img_resource", 
			req.body);
		resObj.result = 1001;
	}catch(err){
		resObj.result = err.errno;
		resObj.err = err.code;
	}
	console.log(resObj);
	res.json(resObj);
});


router.post('/get/imgs', async(req, res)=>{
	var resObj = {};
	try{
		resObj.rows = await db.select("img_resource", 
			null, 
			req.body, 
			null);
		resObj.result = 1001;
	}catch(err){
		resObj.result = err.errno;
		resObj.err = err.code;
	}
	console.log(resObj);
	res.json(resObj);
});


router.post('/get/basic/imgs', async(req, res)=>{
	var resObj = {
		"result" : 1001,
		"rows" : Sample_Res
	};
	res.json(resObj);
});