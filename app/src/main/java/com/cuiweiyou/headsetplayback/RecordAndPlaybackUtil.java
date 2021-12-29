package com.cuiweiyou.headsetplayback;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.media.audiofx.AcousticEchoCanceler;
import android.util.Log;

import java.util.Arrays;

/**
 * www.gaohaiyan.com
 */
public class RecordAndPlaybackUtil {
    private int audioSource = MediaRecorder.AudioSource.MIC;  // 声源
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT; // 采样精度，一个采样点16比特，相当于2个字节。
    private int channelConfig = AudioFormat.CHANNEL_IN_MONO;  // CHANNEL_IN_STEREO双声道，CHANNEL_IN_MONO单声道
    private int sampleRateInHz = 16000;                       // 采样率。
    private int bufferSize = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat); // 采样率。
    private AudioRecord audioRecord;
    private AudioTrack audioTrack;

    // 21.11.23
    // 回音消除器
    AcousticEchoCanceler acousticEchoCanceler;

    private RecordThread recordThread;

    private static RecordAndPlaybackUtil instance;
    private boolean echoCancelAvailable = false;

    private RecordAndPlaybackUtil() {
        audioRecord = new AudioRecord(audioSource, sampleRateInHz, channelConfig, audioFormat, bufferSize * 2);

        // 支持回声消除
        echoCancelAvailable = AcousticEchoCanceler.isAvailable();
        Log.e("ard", "回声消除 " + echoCancelAvailable);
        if (echoCancelAvailable){
            int AUDIO_SESSION_ID = audioRecord.getAudioSessionId();               // id
            acousticEchoCanceler = AcousticEchoCanceler.create(AUDIO_SESSION_ID); // 消除器

            audioTrack = new AudioTrack(
                    AudioManager.STREAM_SYSTEM, // 配合AcousticEchoCanceler
                    sampleRateInHz,
                    AudioFormat.CHANNEL_OUT_MONO,
                    audioFormat,
                    bufferSize * 2,
                    AudioTrack.MODE_STREAM,
                    AUDIO_SESSION_ID);         // 添加id
        } else {
            audioTrack = new AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    sampleRateInHz,
                    AudioFormat.CHANNEL_OUT_MONO,
                    audioFormat,
                    bufferSize * 2,
                    AudioTrack.MODE_STREAM);
        }
    }

    public static RecordAndPlaybackUtil getInstance() {
        if (instance == null) {
            synchronized (RecordAndPlaybackUtil.class) {
                if (instance == null) {
                    instance = new RecordAndPlaybackUtil();
                }
            }
        }
        return instance;
    }

    // 播放设备id
    public int getAudioSessionId(){
        return audioTrack.getAudioSessionId();

        //如果是用 MediaPlayer 播放，使用 MediaPlayer.getAudioSessionId()
        //如果使用 AudioTrack 播放，使用 AudioTrack.getAudioSessionId()
        //如果是用 ExoPlayer 播放，使用如下方式
        //SimpleExoplayer.addAnalyticsListener(object: AnalyticsListener {
        //    override fun onAudioSessionId(
        //            eventTime: AnalyticsListener.EventTime,
        //            audioSessionId: Int
        //) {
        //        val enhancer = LoudnessEnhancer(audioSessionId)
        //  }
        //})
    }

    /**
     * 开始录制
     */
    public boolean startRecord() {
        if (null == recordThread) {
            recordThread = new RecordThread();
            recordThread.start();
        }

        return echoCancelAvailable;
    }

    public void stopRecord() {
        if (null != recordThread) {
            recordThread.setDestroy(true);
        }
        recordThread = null;
    }

    public void setEnchoCancel(boolean ec){
        if (null != acousticEchoCanceler) {
            acousticEchoCanceler.setEnabled(ec);
        }
    }

    public void playback() {
        if (null != recordThread) {
            recordThread.setPlayback(true);
        }
    }

    public void stopback() {
        if (null != recordThread) {
            recordThread.setPlayback(false);
        }
    }

    class RecordThread extends Thread {
        private boolean isDestroy = false;
        private boolean playbackable = false;

        public void setDestroy(boolean isDestroy) {
            this.isDestroy = isDestroy;
        }

        public void setPlayback(boolean able) {
            playbackable = able;
        }

        @Override
        public void run() {
            if (audioRecord.getState() == AudioRecord.STATE_UNINITIALIZED) {
                return;
            }
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
            audioRecord.startRecording();
            audioTrack.play();

            while (!isDestroy) {
                if (playbackable) {
                    byte[] buffer = new byte[bufferSize];

                    int readCount = audioRecord.read(buffer, 0, bufferSize);// 录制

                    if (readCount == AudioTrack.ERROR_INVALID_OPERATION || readCount == AudioTrack.ERROR_BAD_VALUE) {
                        continue;
                    }
                    if (readCount != 0 && readCount != -1) {
                        audioTrack.write(buffer, 0, readCount); // 播放
                    }
                }
            }

            if (null != acousticEchoCanceler) {
                acousticEchoCanceler.setEnabled(false);
                acousticEchoCanceler.release();
            }
            audioRecord.stop();
            audioRecord.release();
            audioTrack.stop();
            audioTrack.release();
        }
    }
}

