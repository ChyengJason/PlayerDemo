package com.jscheng.playerdemo;

import android.util.Log;

/**
 * Created By Chengjunsen on 2018/10/8
 */
public class AudioPlayer implements FFmpeg.IPlayAudio{
    private static final String TAG = "CJS";

    public void play(String path) {
        FFmpeg.getInstance().playAudio(path, this);
    }

    @Override
    public void createTrack(int samplerate, int channelNum) {
        Log.e(TAG, "createTrack 采样率: " + samplerate + "  通道数量： " + channelNum );

    }

    @Override
    public void playTrack(byte[] buffer, int length) {
        Log.e(TAG, "playTrack length: " + length );

    }
}
