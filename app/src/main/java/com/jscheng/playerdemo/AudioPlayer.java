package com.jscheng.playerdemo;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

/**
 * Created By Chengjunsen on 2018/10/8
 */
public class AudioPlayer implements FFmpeg.IPlayAudio, Runnable{
    private static final String TAG = "CJS";
    private AudioTrack mAudioTrack;
    private String mPath;

    public void play(String path) {
        this.mPath = path;
        new Thread(this).start();
    }

    @Override
    public void createTrack(int samplerate, int channelNum) {
        Log.e(TAG, "createTrack 采样率: " + samplerate + "  通道数量： " + channelNum );
        int channelConfig = getAudioFormatOutChannel(channelNum);
        int minBufferSize = AudioTrack.getMinBufferSize(samplerate, channelConfig, AudioFormat.ENCODING_PCM_16BIT);
        mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, samplerate, channelConfig,
                AudioFormat.ENCODING_PCM_16BIT, minBufferSize, AudioTrack.MODE_STREAM);
        mAudioTrack.play();
    }

    @Override
    public void playTrack(byte[] buffer, int length) {
        Log.e(TAG, "playTrack length: " + length );
        if (mAudioTrack != null && mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
            mAudioTrack.write(buffer, 0, length);
        }
    }

    private int getAudioFormatOutChannel(int channelNum) {
        switch (channelNum) {
            case 1:
                return AudioFormat.CHANNEL_OUT_MONO;
            case 2:
                return AudioFormat.CHANNEL_OUT_STEREO;
            default:
                return AudioFormat.CHANNEL_OUT_MONO;
        }
    }

    @Override
    public void run() {
        FFmpeg.getInstance().playAudio(mPath, this);
    }
}
