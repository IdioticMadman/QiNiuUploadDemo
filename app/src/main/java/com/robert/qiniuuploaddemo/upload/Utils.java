package com.robert.qiniuuploaddemo.upload;

import android.content.Context;

import java.io.File;

public class Utils {

    /**
     * 获取缓存文件夹路径
     */
    public static String getCacheDir(Context context) {
        File externalCacheDir = context.getExternalCacheDir();
        if (externalCacheDir == null) {
            return context.getCacheDir().getAbsolutePath();
        } else {
            return externalCacheDir.getAbsolutePath();
        }
    }

}
