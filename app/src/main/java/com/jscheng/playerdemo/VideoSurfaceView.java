package com.jscheng.playerdemo;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;

public class VideoSurfaceView extends SurfaceView implements Runnable {
    private final static String TAG = "CJS";
    private String mVideoPath;

    public VideoSurfaceView(Context context) {
        super(context);
        init();
    }

    public VideoSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VideoSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        this.getHolder().setFormat(PixelFormat.RGBA_8888);
        this.mVideoPath = null;
    }

    public void play(String videoPath) {
        this.mVideoPath = videoPath;
        new Thread(this).start();
    }

    @Override
    public void run() {
        if (mVideoPath != null) {
            Log.e(TAG, "run: " + mVideoPath );
            FFmpeg.getInstance().playVideo(mVideoPath, this.getHolder().getSurface());
        }
    }
}
