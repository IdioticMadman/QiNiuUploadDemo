package com.robert.qiniuuploaddemo.upload;

import com.qiniu.android.http.ResponseInfo;

public interface UploadListener {

    void onSuccess(String key, ResponseInfo responseInfo);

    void onError(String key, Throwable throwable);

    void onProgress(String key, double percent);
}
