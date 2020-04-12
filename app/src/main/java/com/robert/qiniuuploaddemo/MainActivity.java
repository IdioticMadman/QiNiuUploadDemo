package com.robert.qiniuuploaddemo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.utils.AsyncRun;
import com.robert.qiniuuploaddemo.upload.UploadParams;
import com.robert.qiniuuploaddemo.upload.Uploader;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;

import java.io.File;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private static final String TAG = "MainActivity";

    public static final String TOKEN =
            "K0EWOrRAgWR2gvZILRXx8EUDJ6YZ8MI7JLTXWUME:5TDz34825vh6pwU3o6TotneKwVo=:eyJzY29wZSI6InJvYmVydCIsImRlYWRsaW5lIjoxNTg2NzA3NTUxfQ==";

    private Button mStartBtn;
    private Button mCancelBtn;
    private Button mOpenFile;

    private ProgressBar progressBar;

    private static final int REQUEST_CODE = 8090;
    private TextView mLog;
    String[] allpermissions =
            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE
                    , Manifest.permission.READ_EXTERNAL_STORAGE};

    private String uploadFilePath;
    private List<String> mSelected;
    private UploadParams uploadParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        applypermission();
        initView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.openFile:
                Matisse.from(this)
                        .choose(MimeType.ofImage())
                        .countable(true)
                        .maxSelectable(9)
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                        .thumbnailScale(0.85f)
                        .imageEngine(new GlideEngine())
                        .forResult(REQUEST_CODE);
                break;

            case R.id.start:
                upload();
                break;
            case R.id.cancel:
                cancel();
                break;
            default:
                break;
        }
    }

    private void cancel() {
        Log.e(TAG, "cancel: ");
        uploadParams.setCanceled(true);
    }


    private void initView() {
        mOpenFile = findViewById(R.id.openFile);
        mStartBtn = findViewById(R.id.start);
        mCancelBtn = findViewById(R.id.cancel);
        mLog = findViewById(R.id.log);
        mLog.setMovementMethod(ScrollingMovementMethod.getInstance());
        mOpenFile.setOnClickListener(this);
        mStartBtn.setOnClickListener(this);
        mCancelBtn.setOnClickListener(this);

        progressBar = findViewById(R.id.pb);
    }


    private void upload() {
        new Thread() {
            @Override
            public void run() {
                File file = new File(uploadFilePath);
                uploadParams = UploadParams
                        .basic(file, file.getName(), TOKEN)
                        .build();
                ResponseInfo responseInfo =
                        Uploader.getUploadHelper()
                                .uploadSync(MainActivity.this, uploadParams);
                Log.e(TAG, "run: " + responseInfo);
            }
        }.start();
    }

    public void applypermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            boolean needapply = false;
            for (int i = 0; i < allpermissions.length; i++) {
                int chechpermission = ContextCompat.checkSelfPermission(getApplicationContext(),
                        allpermissions[i]);
                if (chechpermission != PackageManager.PERMISSION_GRANTED) {
                    needapply = true;
                }
            }
            if (needapply) {
                ActivityCompat.requestPermissions(MainActivity.this, allpermissions, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, permissions[i] + "已授权", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, permissions[i] + "拒绝授权", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // If the file selection was successful
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            mSelected = Matisse.obtainPathResult(data);
            Log.d("Matisse", "mSelected: " + mSelected);
            this.uploadFilePath = mSelected.get(0);
            this.clearLog();
            this.writeLog(this
                    .getString(R.string.qiniu_select_upload_file)
                    + uploadFilePath);
            Glide.with(this)
                    .load(uploadFilePath)
                    .into((ImageView) findViewById(R.id.image_view));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void clearLog() {
        this.mLog.setText("");
    }

    private void writeLog(final String msg) {
        AsyncRun.runInMain(new Runnable() {
            @Override
            public void run() {
                mLog.append(msg);
                mLog.append("\r\n");
            }
        });

    }

}
