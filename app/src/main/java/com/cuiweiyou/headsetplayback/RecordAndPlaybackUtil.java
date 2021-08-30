package com.cuiweiyou.headsetplayback;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;

import java.io.BufferedOutputStream;

/**
 * www.gaohaiyan.com
 * */
public class RecordAndPlaybackUtil {
    private int audioSource = MediaRecorder.AudioSource.MIC;  // 声源
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT; // 采样精度，一个采样点16比特，相当于2个字节。
    private int channelConfig = AudioFormat.CHANNEL_IN_MONO;  // CHANNEL_IN_STEREO双声道，CHANNEL_IN_MONO单声道
    private int sampleRateInHz = 16000;                       // 采样率。
    private int bufferSize = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat); // 采样率。
    private AudioRecord audioRecord;
    private AudioTrack audioTrack;

    private MyThread thread;

    private static RecordAndPlaybackUtil instance;

    private RecordAndPlaybackUtil() {
        audioRecord = new AudioRecord(audioSource, sampleRateInHz, channelConfig, audioFormat, bufferSize * 2);
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRateInHz, AudioFormat.CHANNEL_OUT_MONO, audioFormat, bufferSize * 2, AudioTrack.MODE_STREAM);
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

    /**
     * 开始录制
     */
    public void startRecord() {
        thread = new MyThread();
        thread.start();
    }

    public void stopRecord() {
        if (null != thread) {
            thread.setDestroy(true);
        }
        thread = null;
    }

    public void setPlayback(boolean playbackable) {
        thread.setPlayback(playbackable);
    }

    class MyThread extends Thread {
        private boolean isDestroy = false;
        private boolean playbackable = false;
        private BufferedOutputStream bos;

        public void setDestroy(boolean isDestroy) {
            this.isDestroy = isDestroy;
        }

        public void setPlayback(boolean able) {
            this.playbackable = able;
        }

        @Override
        public void run() {
            if (audioRecord.getState() == AudioRecord.STATE_UNINITIALIZED) {
                return;
            }
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
            audioRecord.startRecording();

            while (!isDestroy) {
                byte[] buffer = new byte[bufferSize];

                // 录制
                int readCount = audioRecord.read(buffer, 0, bufferSize);

                if (playbackable) { // 播放
                    if (readCount == AudioTrack.ERROR_INVALID_OPERATION || readCount == AudioTrack.ERROR_BAD_VALUE) {
                        continue;
                    }
                    if (readCount != 0 && readCount != -1) {
                        audioTrack.play();
                        audioTrack.write(buffer, 0, readCount);
                    }
                }
            }

            audioRecord.stop();
            audioRecord.release();
            audioTrack.stop();
            audioTrack.release();
        }
    }
}

