
const mysql = require('mysql2');

const MYSQLIP = 'localhost';
const MYSQLID = 'root';
const MYSQLPWD = '1234';
const DBNAME = 'pacs';

var dbConfig = {
  host : MYSQLIP,
  port : 3306,
  user : MYSQLID,
  password : MYSQLPWD,
  connectionLimit:100,
  waitForConnections:true
};

const SQL_CD = "CREATE DATABASE " + DBNAME;

const SQL_CT_DEVICE = "CREATE TABLE device ( " +
  "did varchar(20) not null primary key, " +
  "city varchar(20), " +
  "province varchar(20), " +
  "lat varchar(20), " +
  "lon varchar(20), " +
  "playtime varchar(10) not null default '5');";

const SQL_CT_IMAGEs = "CREATE TABLE img_resource ( " +
  "did varchar(20) not null, " +
  "_order int not null, " +
  "usf char(1) not null, " +
  "filename varchar(50) not null);"; 


let dbPool = mysql.createPool(dbConfig);

module.exports.init = () => {
  initDB().then(async()=>{
    const db = dbPool.promise();
    await db.query(SQL_CT_DEVICE);    
    await db.query(SQL_CT_IMAGEs);    
  });
}

function initDB() {
  return new Promise((res, rej) => {
    dbPool.query(SQL_CD, (err, rows, fields) => {
      dbConfig.database = DBNAME;
      dbPool = mysql.createPool(dbConfig);
      res();
    });
  });
}


module.exports.insert = (table, params) => {
  return new Promise(async(resolve, reject) => {
    var cnt = 0;
    var param = params.isArray() ? params[0] : params;
    var sql = "INSERT INTO " + table + "(";
    var val = "VALUES (";
    const columns = JSON.parse(JSON.stringify(Object.keys(param)));
    for await (const column of columns) {
      sql += column;
      val += "'" + params[column] + "'";
      if(columns.length -1 != cnt++){
        sql += ", ";
        val += ", ";
      }else{
        sql += ") ";
        val += ")";
      }
    }

    if (params.isArray()) {
      params.shift(); // remove array 0 index.
      for await(const obj of params){
        val += ", (";
        for await (const column of columns) {       
          val += "'" + obj[column] + "'";
          if(columns.length -1 != cnt++){
            val += ", ";
          }else{
            val += ")";
          }
        }
      }
    }
    sql += val;
    dbPool.query(sql, (err, rows, fields) => {
      if(err)
        reject(err);    
      if(rows)
        resolve(rows);
    });
  });  
}



module.exports.select = (table, params, target, option) => {
  return new Promise(async(resolve, reject)=>{
    var cnt = 0;
    var sql = "SELECT ";
    if(params){
      for await (const column of params) {
        sql += column;
        if(params.length -1 != cnt++){
          sql += ", ";
        } 
      }
    }else{
      sql += "*";
    }
    sql += " FROM " + table;
    if(target){
      const name = Object.keys(target)[0];
      sql += " WHERE " + name + " = '"  + target[name] + "'";
    }
    dbPool.query(sql, (err, rows, fields) => {
      if(err)
        reject(err);    
      if(rows)
        resolve(rows);
    });
  });
}


module.exports.delete = (table, target, option) =>{
  return new Promise(async(resolve, reject)=>{
    var sql = "DELETE FROM " + table;
    if(target){
      const name = Object.keys(target)[0];
      sql += " WHERE " + name + " = '"  + target[name] + "'";
    }
    dbPool.query(sql, (err, rows, fields) => {
      if(err)
        reject(err);    
      if(rows)
        resolve(rows);
    });
  });  
}


module.exports.update = (table, params, target, option) =>{
  return new Promise(async(resolve, reject)=>{
    sql = "UPDATE " + table + " SET ";
    var columns = Object.keys(params);
    if(params){
      for await (const column of columns) {
        sql += column + "='" + params[column] + "'";      
        if(columns.length -1 != cnt++){
          sql += ", ";
        } 
      }
    }
    if(target){
      const name = Object.keys(target)[0];
      sql += " WHERE " + name + " = '"  + target[name] + "'";
    }
    dbPool.query(sql, (err, rows, fields) => {
      if(err)
        reject(err);    
      if(rows)
        resolve(rows);
    });
  });
}

