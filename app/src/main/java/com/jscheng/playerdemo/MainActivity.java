package com.jscheng.playerdemo;

import android.content.Intent;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;

import com.jscheng.playerdemo.utils.PermissionUtil;
import com.jscheng.playerdemo.utils.PhoneUtil;
import com.jscheng.playerdemo.utils.StorageUtil;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback{
    private static final int REQUEST_CODE = 1;
    private static final int REQUEST_PICK = 2;
    private VideoSurfaceView mVideoView;
    private boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        pickVideo();
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
            mVideoView.play(videoPath);
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

    private void pickVideo() {
        if(PhoneUtil.isMIUI()){
            //是否是小米设备,是的话用到弹窗选取入口的方法去选取视频
            Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"video/*");
            startActivityForResult(Intent.createChooser(intent, "选择要导入的视频"), REQUEST_PICK);
        } else {
            //直接跳到系统相册去选取视频
            Intent intent = new Intent();
            if (Build.VERSION.SDK_INT < 19) {
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("video/*");
            } else {
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("video/*");
            }
            startActivityForResult(Intent.createChooser(intent, "选择要导入的视频"), REQUEST_PICK);
        }
    }
}
