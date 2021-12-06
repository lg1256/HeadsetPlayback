package com.cuiweiyou.headsetplayback;

import android.Manifest;
import android.app.ActivityManager;
import android.bluetooth.BluetoothHeadset;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * www.gaohaiyan.com
 */
public class MainActivity extends ExitAppActivity {

    @BindView(R.id.tipView)
    public TextView tipView;

    @BindView(R.id.runningBtn)
    public Button runningBtn;

    @BindView(R.id.autoRecordCbox)
    public CheckBox autoRecordCbox;

    private HeadsetPlugReceiver headsetPlugReceiver;
    private AudioManager audioManager;
    private Unbinder bind;

    private boolean isLoundspeaker = true;
    private boolean isPlayback = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bind = ButterKnife.bind(this);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION); // 解决acousticEchoCanceler  not enough Memorry

        headsetPlugReceiver = new HeadsetPlugReceiver(this, onDeviceChangedListener);

        initState();
        initView();
        initTaskDescription();
        initPremissions();
    }

    @Override
    public void whenAppExit() {
        RecordAndPlaybackUtil.getInstance().stopRecord();
        unregisterReceiver(headsetPlugReceiver);
        bind.unbind();
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
                runningBtn.setClickable(true);
                registerRecerver();
            } else {
                runningBtn.setClickable(false);
                Toast.makeText(this, "不授权没法录音", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @OnClick(R.id.runningBtn)     // 运行
    public void onRunningBtnClick() {
        play();
    }

    @OnCheckedChanged(R.id.autoRecordCbox) // 自动运行
    public void onAutoRecordCboxClick(boolean checked) {
        ConfigUtil.getInstance().setAutoRecord(checked);
    }

    private void initState() {
        AudioDeviceInfo[] devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS);
        Log.e("ard", "设备数量 " + devices.length);
        for (AudioDeviceInfo device : devices) {
            int deviceType = device.getType();
            Log.e("ard", "设备 " + device.getProductName()+" = "+deviceType);
            if ((deviceType == AudioDeviceInfo.TYPE_WIRED_HEADSET) || (deviceType == AudioDeviceInfo.TYPE_WIRED_HEADPHONES)) { // 有线耳机
                tipView.setText("正在使用有线耳机");
                isLoundspeaker = false;
            } else if ((deviceType == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP) || (deviceType == AudioDeviceInfo.TYPE_BLUETOOTH_SCO)) {// 蓝牙耳机
                tipView.setText("正在使用蓝牙耳机");
                isLoundspeaker = false;
            } else {
                tipView.setText("正在使用手机外放");
                isLoundspeaker = true;
            }
        }
    }

    private void initView() {
        boolean autoRecord = ConfigUtil.getInstance().getAutoRecord();
        Log.e("ard", "自动 " + autoRecord);
        autoRecordCbox.setChecked(autoRecord);
    }

    private void initTaskDescription() { // 最近任务界面的app名称和图标
        Bitmap recentsIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.task);
        String title = "飞速回音";
        int color = Color.rgb(228, 204, 51); // 通常默认白色，不支持自定义
        ActivityManager.TaskDescription description = new ActivityManager.TaskDescription(title, recentsIcon, color);
        this.setTaskDescription(description);
    }

    private void initPremissions() {
        int checked1 = checkSelfPermission(Manifest.permission.RECORD_AUDIO);
        int checked2 = checkSelfPermission(Manifest.permission.BLUETOOTH);
        if (checked1 == PackageManager.PERMISSION_GRANTED && checked2 == PackageManager.PERMISSION_GRANTED) {
            runningBtn.setClickable(true);
            registerRecerver();
        } else {
            ActivityCompat //
                    .requestPermissions(this,  //
                                        new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.BLUETOOTH}, //
                                        990);
        }
    }

    private void registerRecerver() {
        if (autoRecordCbox.isChecked()) {
            play();
        }

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);                       // 有线耳机
        intentFilter.addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED); // 蓝牙
        registerReceiver(headsetPlugReceiver, intentFilter);
    }

    private void play() {
        if (!isPlayback){
            boolean echoCancelAvailable = RecordAndPlaybackUtil.getInstance().startRecord();
            if (echoCancelAvailable) {
                RecordAndPlaybackUtil.getInstance().playback();
                if (isLoundspeaker){
                    RecordAndPlaybackUtil.getInstance().setEnchoCancel(true);
                }else{
                    RecordAndPlaybackUtil.getInstance().setEnchoCancel(false);
                }
                isPlayback = true;
            } else {
                RecordAndPlaybackUtil.getInstance().setEnchoCancel(false);
                if (isLoundspeaker) {
                    RecordAndPlaybackUtil.getInstance().stopback();
                    isPlayback = false;
                    Toast.makeText(this, "手机不支持", Toast.LENGTH_SHORT).show();
                } else {
                    RecordAndPlaybackUtil.getInstance().playback();
                    isPlayback = true;
                }
            }
        }else{
            RecordAndPlaybackUtil.getInstance().stopback();
            isPlayback = false;
        }

        if (isPlayback) {
            runningBtn.setText("正在运行ing");
        } else {
            runningBtn.setText("已经停止");
        }
    }

    private HeadsetPlugReceiver.OnDeviceChangedListener onDeviceChangedListener = new HeadsetPlugReceiver.OnDeviceChangedListener() {
        @Override
        public void onDeviceChanged(int flag) {
            if (3 == flag) {
                tipView.setText("正在使用有线耳机");
                isLoundspeaker = false;
            } else if (78 == flag) {
                tipView.setText("正在使用蓝牙耳机");
                isLoundspeaker = false;
            } else {
                tipView.setText("正在使用外放");
                isLoundspeaker = true;
            }
        }
    };
}