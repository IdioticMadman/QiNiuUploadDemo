package com.robert.qiniuuploaddemo.upload;

import android.content.Context;

import com.qiniu.android.http.ResponseInfo;

public interface IUploadHelper {

    /**
     * 异步上传
     */
    void upload(Context context, UploadParams uploadParams);

    /**
     * 同步上传，没有进度回调，别上传大文件
     */
    ResponseInfo uploadSync(Context context, UploadParams uploadParams);

}
