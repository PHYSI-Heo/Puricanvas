const express = require('express');
const router = express.Router();
const path = require("path");
const multer = require('multer');
const fs = require("fs");
const fse = require('fs-extra');
const ffmpeg = require('fluent-ffmpeg');

const MAX_UPLOAD_SIZE = 10;

const mainDir = path.dirname(require.main.filename);
const rootPath = __dirname.substring(0, __dirname.lastIndexOf('\\'));
const videoTypes = ['.mp4', '.avi', '.wmv', '.mov', '.flv',];

module.exports = router;