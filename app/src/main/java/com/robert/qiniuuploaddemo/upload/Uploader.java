package com.robert.qiniuuploaddemo.upload;

import com.robert.qiniuuploaddemo.upload.qiniu.QiNiuUploadHelper;

public class Uploader {

    private IUploadHelper uploadHelper;

    private Uploader() {
        //默认使用七牛
        this.uploadHelper = new QiNiuUploadHelper();
    }

    /**
     * 上传管理单例
     */
    private static final Uploader instance = new Uploader();

    private static Uploader getInstance() {
        return instance;
    }

    public void setUploadHelper(IUploadHelper uploadHelper) {
        synchronized (Uploader.class) {
            this.uploadHelper = uploadHelper;
        }
    }

    public static IUploadHelper getUploadHelper() {
        synchronized (Uploader.class) {
            return getInstance().uploadHelper;
        }
    }

}
