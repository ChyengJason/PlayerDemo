package com.jscheng.playerdemo;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;

import com.jscheng.playerdemo.utils.PermissionUtil;
import com.jscheng.playerdemo.utils.StorageUtil;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback{
    private static final int REQUEST_CODE = 1;
    private VideoSurfaceView mVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mVideoView = findViewById(R.id.videoView);
        mVideoView.getHolder().addCallback(this);
    }

    private void play() {
        if (PermissionUtil.checkPermissionsAndRequest(this, PermissionUtil.STORAGE, REQUEST_CODE,
                "存储失败")){
            mVideoView.play(getVideoPath());
        }
    }

    private String getVideoPath() {
        return StorageUtil.getSDPath() +  "/" + "glvideo.mp4";
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            play();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        play();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
