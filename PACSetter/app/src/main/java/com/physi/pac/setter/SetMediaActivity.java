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
import com.physi.pac.setter.mqtt.MQTTPublisher;
import com.physi.pac.setter.utils.FileUploader;
import com.physi.pac.setter.utils.FormatConverter;
import com.physi.pac.setter.utils.LoadingDialog;
import com.physi.pac.setter.utils.SwipeAndDragHelper;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SetMediaActivity extends HttpRequestActivity implements View.OnClickListener {

    private static final String TAG = SetMediaActivity.class.getSimpleName();
    private static final int REQ_MEDIA_FILE_SELECTOR = 11;

    private TextView tvNoImg;

    private FormatConverter converter;
    private ImageAdapter imageAdapter;
    private FileUploader fileUploader;

    private List<IMGInfo> setupIMGs = new LinkedList<>();
    private List<String> registerIMGs = new LinkedList<>();
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
            LoadingDialog.dismiss();
            if(url.equals(HttpPacket.GET_IMGs_URL)){
                JSONArray rowsObj = resObj.getJSONArray(HttpPacket.PARAMS_ROWS);
                setDeviceImages(rowsObj);
            }else if(url.equals(HttpPacket.GET_BASIC_IMGs)){
                JSONArray rowsObj = resObj.getJSONArray(HttpPacket.PARAMS_ROWS);
                appendBasicsMediaFile(rowsObj);
            }else if(url.equals(HttpPacket.UPDATE_IMGs_URL)){
                pushNotification("IMG");
                Toast.makeText(getApplicationContext(), "이미지/영상 정보가 갱신되었습니다.", Toast.LENGTH_SHORT).show();
//                finish();
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
                LoadingDialog.show(SetMediaActivity.this, "Get Basic Resource.");
                requestAPI(HttpPacket.GET_BASIC_IMGs, (JSONObject) null);
                break;
            case R.id.btn_file_upload:
                requestFileUpload();
                break;
        }
    }

    private void pushNotification(String msg){
        MQTTPublisher.getInstance(getApplicationContext()).notifyMessage(
                deviceId,
                msg,
                new MQTTPublisher.OnConnectedErrorListener() {
                    @Override
                    public void onError() {
                        SetMediaActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(
                                        getApplicationContext(),
                                        "정보 갱신 알림을 전송할 수 없습니다.\n상태 변경을 위해 디스플레이를 재실행 하세요.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
        );
    }

    private void requestFileUpload(){
        if(setupIMGs.size() == 0) {
            Toast.makeText(getApplicationContext(),
                    "설정된 이미지 또는 영상이 없습니다.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // check upload file
        List<String> uploadFilePath = new LinkedList<>();
        for(IMGInfo info : setupIMGs){
            if(registerIMGs.contains(info.getFileName()) || info.getUsfState().equals("0")){
                continue;
            }
            uploadFilePath.add(info.getLocalFilePath());
        }

        LoadingDialog.show(SetMediaActivity.this, "Image Upload.");
        // upload file
        if(uploadFilePath.size() > 0){
            fileUploader.setUploadFile(deviceId, uploadFilePath);
            fileUploader.sendToServer(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Log.e(TAG, "file upload failed.");
                    LoadingDialog.dismiss();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    Log.e(TAG, "file upload successfully.." + response.code());
                    updateIMGProperties();
                }
            });
        }else{
            updateIMGProperties();
        }
    }

    private void updateIMGProperties(){
        try {
            int no = 0;
            JSONArray params = new JSONArray();
            for(IMGInfo info : setupIMGs){
                JSONObject obj = new JSONObject();
                obj.put(HttpPacket.PARAMS_DEVICE_ID, deviceId);
                obj.put(HttpPacket.PARAMS_IMG_ORDER, no++);
                obj.put(HttpPacket.PARAMS_USER_FILE, info.getUsfState());
                obj.put(HttpPacket.PARAMS_IMG_FILE_NAME, info.getFileName());
                params.put(obj);
            }
            requestAPI(HttpPacket.UPDATE_IMGs_URL, params);
        } catch (JSONException e) {
            LoadingDialog.dismiss();
            e.printStackTrace();
        }
    }

    private void appendBasicsMediaFile(JSONArray rowsObj){
        try {
            int existFileCnt = 0;
            for(int i = 0; i < rowsObj.length(); i++){
                JSONObject obj = rowsObj.getJSONObject(i);
                String name = obj.getString(HttpPacket.PARAMS_IMG_FILE_NAME);
                if(checkNonExistFile(name)){
                    setupIMGs.add(new IMGInfo(
                            String.valueOf(setupIMGs.size()),
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
            imageAdapter.setItems(setupIMGs);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean checkNonExistFile(String fileName){
        for(IMGInfo info : setupIMGs){
            if(info.getFileName().equals(fileName))
                return false;
        }
        return true;
    }

    private void appendLocalMediaFiles(List<String> files){
        int existFileCnt = 0;
        for(String file : files){
            String name = file.substring(file.lastIndexOf('/') + 1);
            if(checkNonExistFile(name)){
                setupIMGs.add(new IMGInfo(
                        String.valueOf(setupIMGs.size()),
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
        imageAdapter.setItems(setupIMGs);
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
            setupIMGs.clear();
            for(int i = 0; i < rowsObj.length(); i++){
                JSONObject obj = rowsObj.getJSONObject(i);
                IMGInfo info = new IMGInfo(
                        obj.getString(HttpPacket.PARAMS_IMG_ORDER),
                        obj.getString(HttpPacket.PARAMS_IMG_FILE_NAME),
                        obj.getString(HttpPacket.PARAMS_USER_FILE)
                );
                setupIMGs.add(info);
                if(info.getUsfState().equals("1")){
                    registerIMGs.add(info.getFileName());
                }
            }
            tvNoImg.setVisibility(setupIMGs.size() == 0? View.VISIBLE : View.GONE);
            imageAdapter.setItems(setupIMGs);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getDeviceImages(){
        LoadingDialog.show(SetMediaActivity.this, "Get Media Resource.");
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
