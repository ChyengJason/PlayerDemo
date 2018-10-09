package com.jscheng.playerdemo;

import android.util.Log;
import android.view.Surface;

public class FFmpeg {
    private static FFmpeg instance = null;

    public static FFmpeg getInstance() {
        if (instance == null) {
            instance = new FFmpeg();
        }
        return instance;
    }

    private FFmpeg() {

    }

    private static final String TAG = "CJS";
    static {
        System.loadLibrary("avcodec-56");
        System.loadLibrary("avdevice-56");
        System.loadLibrary("avfilter-5");
        System.loadLibrary("avformat-56");
        System.loadLibrary("avutil-54");
        System.loadLibrary("postproc-53");
        System.loadLibrary("swresample-1");
        System.loadLibrary("swscale-3");
        System.loadLibrary("native-lib");
    }

    private IPlayAudio mPlayAudioListener;

    public void playVideo(String mVideoPath, Surface surface) {
        renderVideo(mVideoPath, surface);
    }

    public void playAudio(String path, IPlayAudio playInter) {
        mPlayAudioListener = playInter;
        renderAudio(path);
    }

    public void playSlesAudio(String path) {
        renderSlesAudio(path);
    }

    public void createTrack(int samplerate, int channelNum) {
        if (mPlayAudioListener != null) {
            mPlayAudioListener.createTrack(samplerate, channelNum);
        }
    }

    public void playTrack(byte[] buffer, int length) {
        if (mPlayAudioListener != null) {
            mPlayAudioListener.playTrack(buffer, length);
        }
    }

    public native String stringFromJNI();

    public native void renderVideo(String path, Surface surface);

    public native void renderAudio(String path);

    public native void renderSlesAudio(String path);

    public interface IPlayAudio {
        void createTrack(int samplerate, int channelNum);
        void playTrack(byte[] buffer, int length);
    }
}
