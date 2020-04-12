package com.robert.qiniuuploaddemo.upload.qiniu;

import android.content.Context;

import com.qiniu.android.http.ResponseInfo;
import com.robert.qiniuuploaddemo.upload.IUploadHelper;
import com.robert.qiniuuploaddemo.upload.UploadParams;

public class QiNiuUploadHelper implements IUploadHelper {

    private final QiNiuUploadManager uploadManager;

    public QiNiuUploadHelper() {
        uploadManager = new QiNiuUploadManager();
    }

    @Override
    public void upload(Context context, UploadParams uploadParams) {
        uploadManager.upload(context, uploadParams);
    }

    @Override
    public ResponseInfo uploadSync(Context context, UploadParams uploadParams){
        return uploadManager.uploadSync(context, uploadParams);
    }
}
