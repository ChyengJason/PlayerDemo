package com.jscheng.playerdemo;

import android.content.Intent;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;

import com.jscheng.playerdemo.utils.MediaUtil;
import com.jscheng.playerdemo.utils.PermissionUtil;
import com.jscheng.playerdemo.utils.PhoneUtil;
import com.jscheng.playerdemo.utils.StorageUtil;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback{
    private static final int REQUEST_CODE = 1;
    private static final int REQUEST_PICK = 2;
    private VideoSurfaceView mVideoView;
    private AudioPlayer mAudioPlayer;
    private boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAudioPlayer = new AudioPlayer();
        mVideoView = findViewById(R.id.videoView);
        mVideoView.getHolder().addCallback(this);
    }

    private void play() {
        if (isPlaying) {
            return;
        }
        if (!PermissionUtil.checkPermissionsAndRequest(this, PermissionUtil.STORAGE, REQUEST_CODE, "存储失败")){
            return;
        }
        isPlaying = true;
        MediaUtil.pickVideoFile(this, REQUEST_PICK);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            play();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PICK && resultCode == RESULT_OK) {
            String videoPath = StorageUtil.getUrlAbsulotePath(this, data.getData());
            //mVideoView.play(videoPath);
            mAudioPlayer.play(videoPath);
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
