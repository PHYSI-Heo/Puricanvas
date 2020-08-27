package com.physi.pac.setter.utils;

import com.physi.pac.setter.http.HttpPacket;

import java.io.File;
import java.util.List;

import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class FileUploader {

    private static final String TAG = FileUploader.class.getSimpleName();

    private String url;

    private RequestBody requestBody;

    public FileUploader(){
    }

    public void setServerUrl(String url){
        this.url = url;
//        Log.e(TAG, "# Set Upload URL : " + url);
    }

    public void setUploadFile(String deviceID, List<String> filePaths){
        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder();
        multipartBodyBuilder.setType(MultipartBody.FORM);
        multipartBodyBuilder.addFormDataPart(HttpPacket.PARAMS_DEVICE_ID, deviceID);
        for (String path : filePaths){
            File file = new File(path);
            multipartBodyBuilder.addFormDataPart(HttpPacket.PARAMS_IMG_FILE_NAME, file.getName(), RequestBody.create(file, MultipartBody.FORM));
        }
        requestBody = multipartBodyBuilder.build();
    }

    public void sendToServer(Callback callback){
        if(requestBody == null)
            return;

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(callback);
    }
}
