package com.physi.pac.setter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.physi.pac.setter.data.IMGInfo;
import com.physi.pac.setter.http.HttpPacket;
import com.physi.pac.setter.http.HttpRequestActivity;
import com.physi.pac.setter.list.ImageAdapter;
import com.physi.pac.setter.utils.FileUploader;
import com.physi.pac.setter.utils.FormatConverter;
import com.physi.pac.setter.utils.SwipeAndDragHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class IMGUploadActivity extends HttpRequestActivity implements View.OnClickListener {

    private static final String TAG = IMGUploadActivity.class.getSimpleName();
    private static final int REQ_MEDIA_FILE_SELECTOR = 11;

    private TextView tvNoImg;

    private FormatConverter converter;
    private ImageAdapter imageAdapter;
    private FileUploader fileUploader;

    private List<IMGInfo> imgs = new LinkedList<>();
    private List<String> savedFileNames = new LinkedList<>();
    private String deviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_img_upload);

        init();
        getDeviceImages();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQ_MEDIA_FILE_SELECTOR) {
                assert data != null;
                List<String> files = new LinkedList<>();
                if(data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();
                    for(int i = 0; i < count; i++) {
                        Uri dataUri = data.getClipData().getItemAt(i).getUri();
                        files.add(converter.uriToFilePath(getApplicationContext(), dataUri));
                    }
                }
                else if(data.getData() != null) {
                    Log.e(TAG, converter.uriToFilePath(getApplicationContext(), data.getData()));
                    files.add(converter.uriToFilePath(getApplicationContext(), data.getData()));
                }
                appendLocalMediaFiles(files);
            }

        }
    }

    @Override
    protected void onHttpResponse(String url, JSONObject resObj) {
        super.onHttpResponse(url, resObj);
        try {
            if(url.equals(HttpPacket.GET_IMGs_URL)){
                JSONArray rowsObj = resObj.getJSONArray(HttpPacket.PARAMS_ROWS);
                setDeviceImages(rowsObj);
            }else if(url.equals(HttpPacket.GET_BASIC_IMGs)){
                JSONArray rowsObj = resObj.getJSONArray(HttpPacket.PARAMS_ROWS);
                appendBasicsMediaFile(rowsObj);
            }else if(url.equals(HttpPacket.UPDATE_IMGs_URL)){
//                LoadingDialog.dismiss();
//                isImageLoad = false;
//                getDeviceImages();
//                MQTTClient.getInstance().publish(deviceId, "IMG");
//                Toast.makeText(getApplicationContext(), "UPDATE SUCCESSFUL.", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_img_selector:
                showMediaSelector();
                break;
            case R.id.btn_get_basics:
                requestAPI(HttpPacket.GET_BASIC_IMGs, null);
                break;
            case R.id.btn_file_upload:
//                startFileUpload();
                for(IMGInfo info : imgs)
                    Log.e(TAG, info.getNo() + " / " + info.getFileName());
                break;
        }
    }


//
//    private void startFileUpload(){
//        if(filePaths.size() == 0)
//            return;
//
//        List<String> uploadPaths = new LinkedList<>();
//        for(IMGInfo info : filePaths){
//            if(info.getThumbnailPath() == null && !savedFileNames.contains(info.getFileName())){
//                Log.e(TAG, "# Upload File Path : " + info.getFilePath());
//                uploadPaths.add(info.getFilePath());
//            }
//        }
//
//        LoadingDialog.show(IMGUploadActivity.this, "IMAGE UPLOADING...");
//
//        if(uploadPaths.size() > 0){
//            fileUploader.setUploadFile(deviceId, uploadPaths);
//            fileUploader.sendToServer(new Callback() {
//                @Override
//                public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                    Log.e(TAG, "# File Upload Failed.");
//                    LoadingDialog.dismiss();
//                }
//
//                @Override
//                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//                    Log.e(TAG, "# File Upload Successful. (" + response.code() + ")");
//                    updateImageFiles();
//                }
//            });
//        }else{
//            updateImageFiles();
//        }
//    }
//
//
//    private void updateImageFiles(){
//        JSONObject paramsObj = new JSONObject();
//        JSONArray imgArray = new JSONArray();
//        try {
//            for(int i = 0; i < filePaths.size(); i++){
//                String fileName = filePaths.get(i).getFileName();
//                String fileType = fileName.substring(fileName.lastIndexOf("."));
//                String thumbnailPath;
//                if(fileType.equals(".jpg") || fileType.equals(".png")){
//                    thumbnailPath = HttpPacket.THUMBNAIL_BASE + deviceId + "/"  + fileName;
//                }else{
//                    thumbnailPath = HttpPacket.THUMBNAIL_BASE + deviceId + "/"  +
//                            fileName.substring(0, fileName.lastIndexOf(".")) + ".png";
//                }
//                Log.e(TAG, "# Thumbnail Url : " + thumbnailPath);
//                JSONObject obj = new JSONObject();
//                obj.put(HttpPacket.PARAMS_IMG_ORDER, i);
//                obj.put(HttpPacket.PARAMS_IMG_FILE_PATH, thumbnailPath);
//                obj.put(HttpPacket.PARAMS_IMG_FILE_NAME, fileName);
//                imgArray.put(obj);
//            }
//            paramsObj.put(HttpPacket.PARAMS_DEVICE_ID, deviceId);
//            paramsObj.put(HttpPacket.PARAMS_IMG_INFOs, imgArray);
//            requestAPI(HttpPacket.UPDATE_IMGs_URL, paramsObj);
//        } catch (JSONException e) {
//            LoadingDialog.dismiss();
//            e.printStackTrace();
//        }
//    }
//
//
//
//    private void showImageList(){
//        if(filePaths.size() == 0){
//            rcvImages.setVisibility(View.GONE);
//            tvNoImg.setVisibility(View.VISIBLE);
//        }else{
//            rcvImages.setVisibility(View.VISIBLE);
//            tvNoImg.setVisibility(View.GONE);
//        }
//        imageAdapter.setItems(filePaths);
//    }
//

    private void appendBasicsMediaFile(JSONArray rowsObj){
        try {
            int existFileCnt = 0;
            for(int i = 0; i < rowsObj.length(); i++){
                JSONObject obj = rowsObj.getJSONObject(i);
                String name = obj.getString(HttpPacket.PARAMS_IMG_FILE_NAME);
                if(!checkExistFile(name)){
                    imgs.add(new IMGInfo(
                            String.valueOf(imgs.size()),
                            name,
                            "0"
                    ));
                }else{
                    existFileCnt++;
                }
            }
            if(existFileCnt != 0)
                Toast.makeText(getApplicationContext(),
                        "이미 등록된 " + existFileCnt + "개의 샘플 파일은 제외되었습니다.",
                        Toast.LENGTH_SHORT).show();
            imageAdapter.setItems(imgs);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean checkExistFile(String fileName){
        for(IMGInfo info : imgs){
            if(info.getFileName().equals(fileName))
                return true;
        }
        return false;
    }

    private void appendLocalMediaFiles(List<String> files){
        int existFileCnt = 0;
        for(String file : files){
            String name = file.substring(file.lastIndexOf('/') + 1);
            if(!checkExistFile(name)){
                imgs.add(new IMGInfo(
                        String.valueOf(imgs.size()),
                        name,
                        "1",
                        file
                ));
            }else{
                existFileCnt++;
            }
        }
        if(existFileCnt != 0)
            Toast.makeText(getApplicationContext(),
                    "중복된 " + existFileCnt + "개의 파일이 제외되었습니다.",
                    Toast.LENGTH_SHORT).show();
        imageAdapter.setItems(imgs);
    }

    private void showMediaSelector(){
        Intent intent = new Intent();
        intent.setType("image/* video/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), REQ_MEDIA_FILE_SELECTOR);
    }

    private void setDeviceImages(JSONArray rowsObj){
        try {
            imgs.clear();
            for(int i = 0; i < rowsObj.length(); i++){
                JSONObject obj = rowsObj.getJSONObject(i);
                imgs.add(new IMGInfo(
                        obj.getString(HttpPacket.PARAMS_IMG_ORDER),
                        obj.getString(HttpPacket.PARAMS_IMG_FILE_NAME),
                        obj.getString(HttpPacket.PARAMS_USER_FILE)
                ));
            }
            tvNoImg.setVisibility(imgs.size() == 0? View.VISIBLE : View.GONE);
            imageAdapter.setItems(imgs);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getDeviceImages(){
        JSONObject paramsObj = new JSONObject();
        try {
            paramsObj.put(HttpPacket.PARAMS_DEVICE_ID, deviceId);
            requestAPI(HttpPacket.GET_IMGs_URL, paramsObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void init() {
        deviceId = getIntent().getStringExtra("ID");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setHomeAsUpIndicator(R.drawable.ic_back);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");

        Button btnImgSelector = findViewById(R.id.btn_img_selector);
        btnImgSelector.setOnClickListener(this);
        Button btnFileUpload = findViewById(R.id.btn_file_upload);
        btnFileUpload.setOnClickListener(this);
        Button btnGetBasics = findViewById(R.id.btn_get_basics);
        btnGetBasics.setOnClickListener(this);

        RecyclerView rcvImages = findViewById(R.id.rcv_images);
        LinearLayoutManager itemLayoutManager = new LinearLayoutManager(getApplicationContext());
        itemLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        itemLayoutManager.setItemPrefetchEnabled(true);
        rcvImages.setLayoutManager(itemLayoutManager);
        rcvImages.setAdapter(imageAdapter = new ImageAdapter(getApplicationContext(), deviceId));

        // Set Drag & Drop
        ItemTouchHelper touchHelper = new ItemTouchHelper(new SwipeAndDragHelper(imageAdapter));
        imageAdapter.setTouchHelper(touchHelper);
        touchHelper.attachToRecyclerView(rcvImages);

        tvNoImg = findViewById(R.id.tv_no_image);
        converter = new FormatConverter();

        fileUploader = new FileUploader();
        fileUploader.setServerUrl(HttpPacket.UPLOAD_IMGs_URL);
    }
}
