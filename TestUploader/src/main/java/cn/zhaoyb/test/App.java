package cn.zhaoyb.test;

import android.app.Application;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        cn.zhaoyb.zcore.uploadimage.UploadManager.init();
        //cn.zhaoyb.zcore.uploadimage.UploadManager.init("userAgent"); //可自定义user-agent
        //cn.zhaoyb.zcore.uploadimage.UploadManager.init("userAgent", "backupDir"); // 可自定义user-agent和上传图片的备份目录(保留原图)
    }

    public void exit() {
        cn.zhaoyb.zcore.uploadimage.UploadManager.exit();
    }
}
