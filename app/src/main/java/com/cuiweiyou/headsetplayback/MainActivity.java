package com.cuiweiyou.headsetplayback;

import android.Manifest;
import android.app.ActivityManager;
import android.bluetooth.BluetoothHeadset;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

/**
 * www.gaohaiyan.com
 */
public class MainActivity extends ExitAppActivity {

    private TextView devicesTextView;
    private HeadsetPlugReceiver headsetPlugReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 最近任务界面的app名称和图标
        Bitmap recentsIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.task);
        String title = "飞速回音";
        int color = Color.rgb(228, 204, 51); // 通常默认白色，不支持自定义
        ActivityManager.TaskDescription description = new ActivityManager.TaskDescription(title, recentsIcon, color);
        this.setTaskDescription(description);

        devicesTextView = findViewById(R.id.devicesTextView);
        devicesTextView.setKeepScreenOn(true);
        headsetPlugReceiver = new HeadsetPlugReceiver(this, onDeviceChangedListener);

        int checked1 = checkSelfPermission(Manifest.permission.RECORD_AUDIO);
        int checked2 = checkSelfPermission(Manifest.permission.BLUETOOTH);
        if (checked1 == PackageManager.PERMISSION_GRANTED && checked2 == PackageManager.PERMISSION_GRANTED) {
            registerRecerver();
        } else {
            ActivityCompat //
                    .requestPermissions(this,  //
                                        new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.BLUETOOTH}, //
                                        990);
        }
    }

    @Override
    public void whenAppExit() {
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
        intentFilter.addAction(Intent.ACTION_HEADSET_PLUG); // 有线耳机
        intentFilter.addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED); // 蓝牙
        registerReceiver(headsetPlugReceiver, intentFilter);

        RecordAndPlaybackUtil.getInstance().startRecord();
    }

    private HeadsetPlugReceiver.OnDeviceChangedListener onDeviceChangedListener = new HeadsetPlugReceiver.OnDeviceChangedListener() {
        @Override
        public void onDeviceChanged(int flag) {
            if (3 == flag) {
                devicesTextView.append("正在使用有线耳机\n");
                RecordAndPlaybackUtil.getInstance().setPlayback(true);
            } else if (78 == flag) {
                devicesTextView.append("正在使用蓝牙耳机\n");
                RecordAndPlaybackUtil.getInstance().setPlayback(true);
            } else {
                devicesTextView.append("--- 请连接耳机 ---\n");
                RecordAndPlaybackUtil.getInstance().setPlayback(false);
            }
        }
    };
}