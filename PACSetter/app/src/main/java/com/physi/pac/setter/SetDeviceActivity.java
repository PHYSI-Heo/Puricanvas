package com.physi.pac.setter;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.physi.pac.setter.data.DeviceInfo;
import com.physi.pac.setter.http.HttpPacket;
import com.physi.pac.setter.http.HttpRequestActivity;
import com.physi.pac.setter.mqtt.MQTTPublisher;
import com.physi.pac.setter.utils.DBHelper;
import com.physi.pac.setter.utils.LoadingDialog;
import com.physi.pac.setter.utils.LocationInfo;
import com.physi.pac.setter.utils.NotifyDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class SetDeviceActivity extends HttpRequestActivity implements View.OnClickListener {

    private static final String TAG = SetDeviceActivity.class.getSimpleName();

    private EditText etName, etDisplayTime;
    private Spinner spnCity, spnProvince;

    private DBHelper dbHelper;
    private LocationInfo locationInfo;

    private DeviceInfo deviceInfo;
    private String savedProvince;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        init();
        getSetupInformation();
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
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_edit_name:
                editDeviceName();
                break;
            case R.id.btn_delete:
                showDeleteOptionDialog();
                break;
            case R.id.btn_setup:
                updateSetupData();
                break;
        }
    }

    @Override
    protected void onHttpResponse(String url, JSONObject resObj) {
        super.onHttpResponse(url, resObj);
        try {
            LoadingDialog.dismiss();
            switch (url) {
                case HttpPacket.GET_INFO_URL:
                    JSONObject dataObj = resObj.getJSONArray(HttpPacket.PARAMS_ROWS).getJSONObject(0);
                    showSetupInformation(dataObj);
                    break;
                case HttpPacket.RESET_INFO_URL:
                    removeDevice();
                    pushNotification("RESET");
                    finish();
                    break;
                case HttpPacket.UPDATE_INFO_URL:
                    Toast.makeText(getApplicationContext(), "설정이 변경되었습니다.", Toast.LENGTH_SHORT).show();
                    pushNotification("SETUP");
                    finish();
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if(position == -1)
                return;
            locationInfo.setProvince(position);
            spnProvince.setAdapter(new ArrayAdapter<>(getApplicationContext(),
                    R.layout.view_spinner_item,
                    locationInfo.getProvinceNames()));
            if(savedProvince != null && !savedProvince.equals("null")){
                spnProvince.setSelection(locationInfo.getProvincePosition(savedProvince));
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    public LatLng getLocationPoint(String city, String province){
        try {
            Geocoder geocoder = new Geocoder(getApplicationContext());
            List<Address> list = geocoder.getFromLocationName(city + " " + province, 1);
            if (list != null && list.size() != 0) {
                Address addr = list.get(0);
                double lat = addr.getLatitude();
                double lon = addr.getLongitude();
                Log.e(TAG, "> Latitude : " + lat + ", Longitude : " + lon);
                return new LatLng(lat, lon);
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void updateSetupData() {
        if(spnCity.getSelectedItemPosition() == 0){
            Toast.makeText(getApplicationContext(), "날씨 지역 정보를 설정하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        LoadingDialog.show(SetDeviceActivity.this, "Update Information.");

        JSONObject paramsObj = new JSONObject();
        try {
            String cityName = spnCity.getSelectedItem().toString();
            String provinceName = spnProvince.getSelectedItem().toString();
            LatLng latLng = getLocationPoint(cityName, provinceName);
            if(latLng == null){
                Toast.makeText(getApplicationContext(), "지역 설정 오류가 발생하였습니다.\n잠시 후 다시 시도해주세요. .", Toast.LENGTH_SHORT).show();
                return;
            }
            paramsObj.put(HttpPacket.PARAMS_DEVICE_ID, deviceInfo.getId());
            paramsObj.put(HttpPacket.PARAMS_CITY, spnCity.getSelectedItem().toString());
            paramsObj.put(HttpPacket.PARAMS_PROVINCE, spnProvince.getSelectedItem().toString());
            paramsObj.put(HttpPacket.PARAMS_LOCATION_LAT, String.valueOf(latLng.latitude));
            paramsObj.put(HttpPacket.PARAMS_LOCATION_LON, String.valueOf(latLng.longitude));
            paramsObj.put(HttpPacket.PARAMS_DISPLAY_TIME, etDisplayTime.getText().toString());
            requestAPI(HttpPacket.UPDATE_INFO_URL, paramsObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void pushNotification(String msg){
        MQTTPublisher.getInstance(getApplicationContext()).notifyMessage(
                deviceInfo.getId(),
                msg,
                new MQTTPublisher.OnConnectedErrorListener() {
                    @Override
                    public void onError() {
                        SetDeviceActivity.this.runOnUiThread(new Runnable() {
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

    private void resetSetupData() {
        LoadingDialog.show(SetDeviceActivity.this, "Reset Device Setting.");
        JSONObject paramsObj = new JSONObject();
        try {
            paramsObj.put(HttpPacket.PARAMS_DEVICE_ID, deviceInfo.getId());
            requestAPI(HttpPacket.RESET_INFO_URL, paramsObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void removeDevice(){
        boolean result = dbHelper.deleteData(DBHelper.COL_DEVICE_ID, deviceInfo.getId());
        Toast.makeText(getApplicationContext(),
                result ? "디바이스가 삭제되었습니다." : "디바이스 삭제에 실패하였습니다\n잠시 후 다시 시도해주세요.",
                Toast.LENGTH_SHORT).show();
    }

    private void showDeleteOptionDialog(){
        new NotifyDialog().show(SetDeviceActivity.this, null,
                "서버에 등록된 디바이스 설정을 초기화하시겠습니까?\n[아니오] 선택 시, 현재 디바이스에서만 정보가 삭제됩니다.",
                "예", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        resetSetupData();
                    }
                }, "아니오", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeDevice();
                    }
                });
    }

    private void editDeviceName(){
        if(etName.getText().length() == 0)
            return;
        ContentValues param = new ContentValues();
        param.put(DBHelper.COL_NAME, etName.getText().toString());
        boolean result = dbHelper.updateData(param, DBHelper.COL_DEVICE_ID, deviceInfo.getId());
        if(result) {
            deviceInfo.setName(etName.getText().toString());
            Toast.makeText(getApplicationContext(), "디바이스 명칭이 변경되었습니다.", Toast.LENGTH_SHORT).show();
            finish();
        }else{
            etName.setText(deviceInfo.getName());
            Toast.makeText(getApplicationContext(), "디바이스 명칭의 변경에 실패하였습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showSetupInformation(JSONObject dataObj) {
        try {
            etDisplayTime.setText(dataObj.getString(HttpPacket.PARAMS_DISPLAY_TIME));
            String savedCity = dataObj.getString(HttpPacket.PARAMS_CITY);
            savedProvince = dataObj.getString(HttpPacket.PARAMS_PROVINCE);
            if(!savedCity.equals("null")){
                spnCity.setSelection(locationInfo.getCityPosition(savedCity));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getSetupInformation(){
        LoadingDialog.show(SetDeviceActivity.this, "Get Device Setting.");
        JSONObject paramsObj = new JSONObject();
        try {
            paramsObj.put(HttpPacket.PARAMS_DEVICE_ID, deviceInfo.getId());
            requestAPI(HttpPacket.GET_INFO_URL, paramsObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void init() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setHomeAsUpIndicator(R.drawable.ic_back);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");

        dbHelper = new DBHelper(getApplicationContext());
        deviceInfo = getIntent().getParcelableExtra("INFO");
        locationInfo = new LocationInfo(getApplicationContext());

        Button btnEditName = findViewById(R.id.btn_edit_name);
        btnEditName.setOnClickListener(this);
        Button btnDeleteDevice = findViewById(R.id.btn_delete);
        btnDeleteDevice.setOnClickListener(this);
        Button btnSetup = findViewById(R.id.btn_setup);
        btnSetup.setOnClickListener(this);

        etName = findViewById(R.id.et_name);
        etName.setText(deviceInfo.getName());
        etDisplayTime = findViewById(R.id.et_display_time);

        spnCity = findViewById(R.id.spn_city);
        spnProvince = findViewById(R.id.spn_province);

        spnCity.setAdapter(new ArrayAdapter<>(getApplicationContext(),
                R.layout.view_spinner_item,
                locationInfo.getCityList()));
        spnCity.setOnItemSelectedListener(itemSelectedListener);
    }

}
