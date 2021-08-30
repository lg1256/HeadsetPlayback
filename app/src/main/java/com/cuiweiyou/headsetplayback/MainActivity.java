package com.cuiweiyou.headsetplayback;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

/**
 * www.gaohaiyan.com
 */
public class MainActivity extends AppCompatActivity {

    private TextView devicesTextView;
    private HeadsetPlugReceiver headsetPlugReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        devicesTextView = findViewById(R.id.devicesTextView);

        headsetPlugReceiver = new HeadsetPlugReceiver(this, onDeviceChangedListener);

        int checked = checkSelfPermission(Manifest.permission.RECORD_AUDIO);
        if (checked == PackageManager.PERMISSION_GRANTED) {
            registerRecerver();
        } else {
            ActivityCompat //
                    .requestPermissions(this,  //
                                        new String[]{Manifest.permission.RECORD_AUDIO}, //
                                        990);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RecordAndPlaybackUtil.getInstance().stopRecord();
        unregisterReceiver(headsetPlugReceiver);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 990) {
            boolean isAllGranted = true;
            for (int grant : grantResults) { // 判断是否所有的权限都已经授予了
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false;
                    break;
                }
            }

            if (isAllGranted) { // 如果所有的权限都授予了
                registerRecerver();
            } else {
                Toast.makeText(this, "不授权没法录音", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void registerRecerver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(headsetPlugReceiver, intentFilter);

        RecordAndPlaybackUtil.getInstance().startRecord();
    }

    private HeadsetPlugReceiver.OnDeviceChangedListener onDeviceChangedListener = new HeadsetPlugReceiver.OnDeviceChangedListener() {
        @Override
        public void onDeviceChanged(boolean enable) {
            if (enable) {
                devicesTextView.append("正在使用有线耳机\n");
                RecordAndPlaybackUtil.getInstance().setPlayback(true);
            } else {
                devicesTextView.append("--- 请插入有线耳机 ---\n");
                RecordAndPlaybackUtil.getInstance().setPlayback(false);
            }
        }
    };
}