package com.jscheng.playerdemo;

/**
 * Created By Chengjunsen on 2018/10/9
 */
public class OpenSlesPlayer implements Runnable{

    private String mVideoPath;

    public void play(String path) {
        mVideoPath = path;
        new Thread(this).start();
    }

    @Override
    public void run() {
        FFmpeg.getInstance().playSlesAudio(mVideoPath);
    }
}
