package com.cuiweiyou.headsetplayback;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.os.Build;
import android.util.Log;

/**
 * www.gaohaiyan.com
 */
public class HeadsetPlugReceiver extends BroadcastReceiver {

    private AudioManager audoManager;
    private OnDeviceChangedListener onDeviceChangedListener;
    private final BluetoothAdapter bluetoothAdapter;

    public HeadsetPlugReceiver(Context context, OnDeviceChangedListener listener) {
        audoManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        onDeviceChangedListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED.equals(action)) {
            getHeadsetState();
        } else if (Intent.ACTION_HEADSET_PLUG.equals(action)) {
            if (intent.hasExtra("state")) {
                if (intent.getIntExtra("state", 0) == 0) {
                    // Log.e("ard", "有线拔出\n");
                } else if (intent.getIntExtra("state", 0) == 1) {
                    // Log.e("ard", "有线插入\n");
                }
            }

            getHeadsetState();
        }
    }

    private void getHeadsetState() {

        AudioDeviceInfo[] devices = audoManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS);
        int flag = -100;
        for (AudioDeviceInfo info : devices) {
            int type = info.getType();
            Log.e("ard类型--", "" + type);
            switch (type) {
                case 0: {
                    Log.e("ard类型", "0 TYPE_UNKNOWN 与未知或未初始化设备关联的设备类型");
                    break;
                }

                case 1: {
                    // Log.e("ard类型", "1 TYPE_BUILTIN_EARPIECE 所连接耳机扬声器的设备类型");
                    break;
                }

                case 2: {
                    // Log.e("ard类型", "2 TYPE_BUILTIN_SPEAKER 内置扬声器系统（即单声道扬声器或立体声扬声器）的设备类型");
                    break;
                }

                case 3: {
                    Log.e("ard类型", "3 TYPE_WIRED_HEADSET 耳机的设备类型，它是耳机和麦克风的组合");
                    // 有线耳机插入后响应
                    flag = 3;
                    break;
                }

                case 4: {
                    // Log.e("ard类型", "4 TYPE_WIRED_HEADPHONES 一对有线耳机的设备类型");
                    break;
                }

                case 5: {
                    // Log.e("ard类型", "5 TYPE_LINE_ANALOG 模拟线路级连接的设备类型。");
                    break;
                }

                case 6: {
                    // Log.e("ard类型", "6 TYPE_LINE_DIGITAL 数字线路连接的设备类型（如SPDIF）");
                    break;
                }

                case 7: {
                    Log.e("ard类型", "7 TYPE_BLUETOOTH_SCO 通常用于电话的蓝牙设备的设备类型。");
                    break;
                }

                case 8: {
                    Log.e("ard类型", "8 TYPE_BLUETOOTH_A2DP 支持A2DP配置文件的蓝牙设备的设备类型。");//可操控蓝牙设备，如带播放暂停功能的蓝牙耳机
                    break;
                }

                case 9: {
                    // Log.e("ard类型", "9 TYPE_HDMI HDMI连接的设备类型。");
                    break;
                }

                case 10: {
                    // Log.e("ard类型", "10 TYPE_HDMI_ARC HDMI连接的音频返回通道的设备类型。");
                    break;
                }

                case 11: {
                    // Log.e("ard类型", "11 TYPE_USB_DEVICE USB音频设备的设备类型。");
                    break;
                }

                case 12: {
                    //Log.e("ard类型", "12 TYPE_USB_ACCESSORY 处于附件模式的USB音频设备的设备类型");
                    break;
                }

                case 13: {
                    // Log.e("ard类型", "13 TYPE_DOCK 与驳接关联的音频设备的设备类型");
                    break;
                }

                case 14: {
                    // Log.e("ard类型", "14 TYPE_FM 与通过FM传输音频信号有关的一种设备类型。");
                    break;
                }

                case 15: {
                    //Log.e("ard类型", "15 TYPE_BUILTIN_MIC 设备内置麦克风的设备类型。");
                    break;
                }

                case 16: {
                    // Log.e("ard类型", "16 TYPE_FM_TUNER 用于访问通过FM传输的音频内容的设备类型。");
                    break;
                }

                case 17: {
                    // Log.e("ard类型", "17 TYPE_TV_TUNER 用于访问通过电视调谐器系统传输的音频内容的设备类型。");
                    break;
                }

                case 18: {
                    // Log.e("ard类型", "18 TYPE_TELEPHONY 通过电话网络传输音频信号的一种设备类型。");
                    break;
                }

                case 19: {
                    // Log.e("ard类型", "19 TYPE_AUX_LINE 辅助线路电平连接器的设备类型。");
                    break;
                }

                case 20: {
                    // Log.e("ard类型", "20 TYPE_IP 通过IP连接的设备类型。");
                    break;
                }

                case 21: {
                    // Log.e("ard类型", "21 TYPE_BUS 一种类型无关(type-agnostic)的设备，用于与外部音频系统通信");
                    break;
                }

                case 22: {
                    // Log.e("ard类型", "22 TYPE_USB_HEADSET USB音频耳机的设备类型");
                    break;
                }

                case 23: {
                    // Log.e("ard类型", "23 TYPE_HEARING_AID 助听器的设备类型。");
                    break;
                }

                case 24: {
                    // Log.e("ard类型", "24 TYPE_SPEAKER_SAFE 描述内置在设备中的扬声器系统（即单声道扬声器或立体声扬声器）的一种设备类型，专门针对输出通知和警报之类的声音（即用户不一定能预料到的声音）进行调整。请注意，此物理音频设备可能与{TYPE_BUILTIN_SPEAKER}相同，但驱动方式不同，以安全适应不同的用例。");
                    break;
                }

                case 25: {
                    // Log.e("ard类型", "25 TYPE_REMOTE_SUBMIX 隐藏的系统api。hide、SystemApi，在Android框架内，在混音和系统应用程序之间重新路由音频的设备类型。通常是在使用{android.media.audiopolicy.audiopolicy}进行使用{android.media.audiopolicy.AudioMix#ROUTE_FLAG_RENDER}标志创建的混音时创建的。");
                    break;
                }
            } // end switch

        }// end for

        if (3 == flag) { // 没有插入有线耳机
            Log.e("ard", "正在使用有线耳机");
        } else {
            Log.e("ard", "未插入有线耳机");
            int state1 = bluetoothAdapter.getProfileConnectionState(BluetoothProfile.HEADSET);
            int state2 = bluetoothAdapter.getProfileConnectionState(BluetoothProfile.A2DP);
            Log.e("ard==", ""+state1);
            Log.e("ard==", ""+state2);
            if (BluetoothProfile.STATE_CONNECTED == state1 || BluetoothProfile.STATE_CONNECTED == state2) {
                Log.e("ard", "蓝牙耳机连上了");
                flag = 78;
            } else {
                Log.e("ard", "蓝牙耳机out了");
            }
        }

        if (null != onDeviceChangedListener) {
            onDeviceChangedListener.onDeviceChanged(flag);
        }
    }

    public interface OnDeviceChangedListener {
        void onDeviceChanged(int flag);
    }
}
