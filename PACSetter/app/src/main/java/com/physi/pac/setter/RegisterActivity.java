package com.physi.pac.setter;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.physi.pac.setter.http.HttpPacket;
import com.physi.pac.setter.http.HttpRequestActivity;
import com.physi.pac.setter.utils.DBHelper;
import com.physi.pac.setter.utils.LoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends HttpRequestActivity implements View.OnClickListener {

    private static final String TAG = RegisterActivity.class.getSimpleName();

    private EditText etDeviceID, etDeviceName;

    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        init();

        etDeviceID.setText("AAAAAA");
        etDeviceName.setText("AA");
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btn_register){
            checkExistSerialNumber();
        }else{
            registerSerialNumber();
        }
    }

    @Override
    protected void onHttpResponse(String url, JSONObject resObj) {
        super.onHttpResponse(url, resObj);
        if(url.equals(HttpPacket.EXIST_ID_URL)) {
            LoadingDialog.dismiss();
            registerDevice(resObj);
        }
    }

    private void registerSerialNumber(){
        if(etDeviceID.length() == 0 || etDeviceName.length() == 0)
            return;
        JSONObject paramsObj = new JSONObject();
        try {
            paramsObj.put(HttpPacket.PARAMS_DEVICE_ID, etDeviceID.getText().toString());
            requestAPI(HttpPacket.REGISTER_ID_URL, paramsObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void checkExistSerialNumber(){
        if(etDeviceID.length() == 0 || etDeviceName.length() == 0)
            return;

        LoadingDialog.show(RegisterActivity.this, "Register Device.");

        JSONObject paramsObj = new JSONObject();
        try {
            paramsObj.put(HttpPacket.PARAMS_DEVICE_ID, etDeviceID.getText().toString());
            requestAPI(HttpPacket.EXIST_ID_URL, paramsObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void registerDevice(JSONObject resObj){
        try {
            if(resObj.getBoolean(HttpPacket.PARAMS_ID_EXIST)){
                ContentValues params = new ContentValues();
                params.put(DBHelper.COL_DEVICE_ID, etDeviceID.getText().toString());
                params.put(DBHelper.COL_NAME, etDeviceName.getText().toString());
                boolean result = dbHelper.insertData(params);
                Toast.makeText(getApplicationContext(),
                        result ? "디바이스가 등록되었습니다." : "이미 등록된 디바이스 입니다.",
                        Toast.LENGTH_SHORT).show();
                finish();
            }else{
                Toast.makeText(getApplicationContext(), "시리얼번호를 확인하세요.", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void init() {
        etDeviceID = findViewById(R.id.et_device_id);
        etDeviceName = findViewById(R.id.et_device_name);

        Button btnRegister = findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(this);
        Button btnCreateID = findViewById(R.id.btn_create_id);
        btnCreateID.setOnClickListener(this);

        dbHelper = new DBHelper(getApplicationContext());
    }

}
