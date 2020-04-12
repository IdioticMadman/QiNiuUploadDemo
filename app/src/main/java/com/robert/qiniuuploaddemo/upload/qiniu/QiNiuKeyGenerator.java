package com.robert.qiniuuploaddemo.upload.qiniu;

import com.qiniu.android.storage.KeyGenerator;

import org.jetbrains.annotations.NotNull;

import java.io.File;

public class QiNiuKeyGenerator implements KeyGenerator {

    @Override
    public String gen(String key, @NotNull File file) {
        return key + "_._" + (new StringBuffer(file.getAbsolutePath())).reverse();
    }

}
