package com.robert.qiniuuploaddemo.upload.qiniu;

import android.content.Context;
import android.util.Log;

import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.KeyGenerator;
import com.qiniu.android.storage.UpCancellationSignal;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.qiniu.android.storage.persistent.FileRecorder;
import com.robert.qiniuuploaddemo.upload.UploadListener;
import com.robert.qiniuuploaddemo.upload.UploadParams;
import com.robert.qiniuuploaddemo.upload.Utils;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

class QiNiuUploadManager {

    private static final String TAG = QiNiuUploadManager.class.getSimpleName();

    /**
     * 七牛上传文件进度记录缓存文件夹
     */
    private static final String RECORD_DIR = File.separator + "qi_niu_upload_record";

    /**
     * 七牛异步上传线程数
     */
    private static final int DEFAULT_THREAD_NUM = 3;

    /**
     * 七牛上传管理
     */
    private UploadManager uploadManager;

    /**
     * 获取七牛上传管理类
     */
    private synchronized UploadManager get(Context context) {
        if (uploadManager == null) {
            try {
                String cacheDir = Utils.getCacheDir(context);
                //配置断点续传的记录文件夹
                FileRecorder fileRecorder = new FileRecorder(cacheDir + RECORD_DIR);
                KeyGenerator keyGenerator = new QiNiuKeyGenerator();
                Configuration configuration = new Configuration.Builder()
                        .recorder(fileRecorder, keyGenerator)
                        .build();
                uploadManager = new UploadManager(configuration, DEFAULT_THREAD_NUM);
            } catch (IOException e) {
                Log.e(TAG, "获取缓存文件夹失败，不使用断点续传功能");
                uploadManager = new UploadManager(new Configuration.Builder().build()
                        , DEFAULT_THREAD_NUM);
            }
        }
        return uploadManager;
    }


    void upload(Context context, final UploadParams uploadParams) {
        UploadManager uploadManager = get(context);
        UploadOptions uploadOptions = new UploadOptions(uploadParams.getParams(), uploadParams.getMimeType(),
                uploadParams.isCheckCrc(), new UpProgressHandler() {
            @Override
            public void progress(String key, double percent) {
                UploadListener uploadListener = uploadParams.getUploadListener();
                if (uploadListener != null) {
                    uploadListener.onProgress(key, percent);
                }
            }
        }, new UpCancellationSignal() {
            @Override
            public boolean isCancelled() {
                return uploadParams.isCanceled();
            }
        });
        UpCompletionHandler upCompletionHandler = new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
                UploadListener uploadListener = uploadParams.getUploadListener();
                if (uploadListener != null) {
                    if (info.isOK()) {
                        uploadListener.onSuccess(key, info);
                    } else {
                        uploadListener.onError(key, new RuntimeException(info.error));
                    }
                }
            }
        };
        uploadManager.put(uploadParams.getFile(), uploadParams.getKey(), uploadParams.getUploadToken(),
                upCompletionHandler, uploadOptions);
    }

    public ResponseInfo uploadSync(Context context, final UploadParams uploadParams) {
        UploadManager uploadManager = get(context);
        UploadOptions uploadOptions = new UploadOptions(uploadParams.getParams(), uploadParams.getMimeType(),
                uploadParams.isCheckCrc(), new UpProgressHandler() {
            @Override
            public void progress(String key, double percent) {
                Log.e(TAG, "progress: ");
                UploadSyncListener listener = uploadParams.getUploadSyncListener();
                if (listener != null) {
                    listener.onProgress(key, percent);
                }
            }
        }, new UpCancellationSignal() {
            @Override
            public boolean isCancelled() {
                Log.e(TAG, "isCancelled: ");
                return uploadParams.isCanceled();
            }
        });
        return uploadManager.syncPut(uploadParams.getFile(), uploadParams.getKey(),
                uploadParams.getUploadToken(), uploadOptions);

    }
}
