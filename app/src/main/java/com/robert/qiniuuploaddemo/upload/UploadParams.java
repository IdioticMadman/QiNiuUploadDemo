package com.robert.qiniuuploaddemo.upload;

import android.text.TextUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 上传定义参数
 */
public class UploadParams {

    /**
     * 自定义参数需x: 开头
     */
    public static final String PARAM_PREFIX = "x:";
    /**
     * 待上传文件
     */
    private File file;
    /**
     * 上传的参数
     */
    private Map<String, String> params;
    /**
     * 上传的token
     */
    private String uploadToken;
    /**
     * 文件对应的key，后续通过Host+key的方式能访问到
     */
    private String key;
    /**
     * 文件的媒体类型
     */
    private String mimeType;
    /**
     * 异步上传的回调，已做线程切换，回调回来会在主线程
     */
    private UploadListener uploadListener;

    /**
     * 是否取消此次上传，true为取消此次上传
     */
    private volatile boolean isCanceled;
    /**
     * 是否开启crc校验
     */
    private boolean checkCrc;

    public UploadListener getUploadListener() {
        return uploadListener;
    }

    public void setUploadListener(UploadListener uploadListener) {
        this.uploadListener = uploadListener;
    }

    public boolean isCanceled() {
        return isCanceled;
    }

    public void setCanceled(boolean canceled) {
        isCanceled = canceled;
    }

    public boolean isCheckCrc() {
        return checkCrc;
    }

    public void setCheckCrc(boolean checkCrc) {
        this.checkCrc = checkCrc;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public String getUploadToken() {
        return uploadToken;
    }

    public void setUploadToken(String uploadToken) {
        this.uploadToken = uploadToken;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public static Builder basic(File file, String key, String token) {
        return new Builder()
                .file(file)
                .key(key)
                .token(token);
    }

    public static class Builder {
        private File file;
        private Map<String, String> params;
        private String token;
        private String key;
        private String mimeType = "";
        private UploadListener uploadListener;
        private boolean isCheckCrc = false;

        public Builder() {
            this.params = new HashMap<>();
        }

        public Builder file(File file) {
            this.file = file;
            return this;
        }

        public Builder isCheckCrc(boolean isCheckCrc) {
            this.isCheckCrc = isCheckCrc;
            return this;
        }

        public Builder mimeType(String mimeType) {
            this.mimeType = mimeType;
            return this;
        }

        public Builder key(String key) {
            this.key = key;
            return this;
        }

        public Builder token(String token) {
            this.token = token;
            return this;
        }


        public Builder addParams(String key, String value) {
            this.params.put(PARAM_PREFIX + key, value);
            return this;
        }

        public Builder addAllParams(Map<String, String> paramsMap) {
            for (Map.Entry<String, String> params : paramsMap.entrySet()) {
                String key = params.getKey();
                String value = params.getValue();
                this.params.put(PARAM_PREFIX + key, value);
            }
            return this;
        }

        public Builder listener(UploadListener listener) {
            this.uploadListener = listener;
            return this;
        }


        public UploadParams build() {
            if (file == null || TextUtils.isEmpty(key) || TextUtils.isEmpty(token)) {
                throw new IllegalArgumentException("file, key, token必须设置");
            }
            UploadParams uploadParams = new UploadParams();
            uploadParams.setFile(file);
            uploadParams.setKey(key);
            uploadParams.setUploadToken(token);
            uploadParams.setParams(params);
            uploadParams.setUploadListener(uploadListener);
            uploadParams.setMimeType(mimeType);
            uploadParams.setCheckCrc(isCheckCrc);
            return uploadParams;
        }

    }
}
