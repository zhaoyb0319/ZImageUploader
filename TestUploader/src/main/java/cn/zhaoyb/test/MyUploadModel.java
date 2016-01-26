package cn.zhaoyb.test;

import android.content.ContentValues;

import cn.zhaoyb.zcore.uploadimage.entity.SimpleUploadModel;

public class MyUploadModel extends SimpleUploadModel {

    private ContentValues uploadParams;
    public void setUploadParams(ContentValues uploadParams) {
        this.uploadParams = uploadParams;
    }
    @Override
    public ContentValues getUploadParams() {
        return uploadParams;
    }
}
