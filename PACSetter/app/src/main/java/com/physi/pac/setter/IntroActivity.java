package com.physi.pac.setter;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class IntroActivity extends AppCompatActivity {

    private static final int INTRO_DELAY = 1500;
    private static final int REQ_APP_PERMISSION = 1500;

    private List<String> appPermissions = Arrays.asList(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    );

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        checkPermissions();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handler.removeCallbacks(intentRunnable);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        if(requestCode == REQ_APP_PERMISSION){
            boolean accessStatus = true;
            for(int grantResult : grantResults){
                if (grantResult == PackageManager.PERMISSION_DENIED) {
                    accessStatus = false;
                    break;
                }
            }
            if(!accessStatus){
                Toast.makeText(getApplicationContext(), "Permission Denied.", Toast.LENGTH_SHORT).show();
                finish();
            }else{
                nextActivity();
            }
        }
    }

    private void checkPermissions(){
        final List<String> reqPermissions = new LinkedList<>();
        for(String permission : appPermissions){
            if(checkSelfPermission(permission) == PackageManager.PERMISSION_DENIED){
                reqPermissions.add(permission);
            }
        }

        if(reqPermissions.size() != 0){
            requestPermissions(reqPermissions.toArray(new String[reqPermissions.size()]), REQ_APP_PERMISSION);
        }else{
            nextActivity();
        }
    }

    private void nextActivity(){
        handler.postDelayed(intentRunnable, INTRO_DELAY);
    }

    private Runnable intentRunnable = new Runnable() {
        @Override
        public void run() {
            startActivity(new Intent(IntroActivity.this, DeviceActivity.class));
            finish();
        }
    };
}
