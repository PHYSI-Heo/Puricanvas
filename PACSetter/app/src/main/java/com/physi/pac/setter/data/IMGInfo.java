package com.physi.pac.setter.data;

public class IMGInfo {

    private String no, fileName, usfState;
    private String localFilePath;

    public IMGInfo(){
    }

    public IMGInfo(String no, String fileName, String usfState){
        this.no = no;
        this.fileName = fileName;
        this.usfState = usfState;
    }

    public IMGInfo(String no, String fileName, String usfState, String localFilePath){
        this.no = no;
        this.fileName = fileName;
        this.usfState = usfState;
        this.localFilePath = localFilePath;
    }

    public String getLocalFilePath() {
        return localFilePath;
    }

    public String getFileName() {
        return fileName;
    }

    public String getNo() {
        return no;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getUsfState() {
        return usfState;
    }

    public void setUsfState(String usfState) {
        this.usfState = usfState;
    }
}
