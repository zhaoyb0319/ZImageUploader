package cn.zhaoyb.test;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.zhaoyb.zcore.uploadimage.UploadManager;
import cn.zhaoyb.zcore.uploadimage.entity.SimpleUploadView;

public class MainActivity extends AppCompatActivity {

    private MyUploadModel mUploadModel;
    private SimpleUploadView mUploadView;

    private TextView mUploadFileTip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mUploadView = (SimpleUploadView) findViewById(R.id.upload_view_simple1);
        mUploadFileTip = (TextView) findViewById(R.id.edit_text_simple1);
    }

    // 拍照得到照片路径
    public void doCamera(View v) {
        Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(it, 1);
    }
    // 本地选择图片
    public void doFile(View v) {
        Intent local = new Intent();
        local.setType("image/*");
        local.setAction(Intent.ACTION_PICK);
        startActivityForResult(local, 2);
    }
    // 回传图片或图片路径
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) return;

        if (requestCode == 1) {
            Bundle extras = data.getExtras();
            Bitmap b = (Bitmap) extras.get("data");

            // 设置显示的图片
            mUploadView.getProgressImage().setImageBitmap(b);

            String name = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
            String filePath = Environment.getExternalStorageDirectory().toString() + File.separator + "temp/image/" + name + ".jpg";
            File myCaptureFile = new File(filePath);
            try {
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    if (!myCaptureFile.getParentFile().exists()) {
                        myCaptureFile.getParentFile().mkdirs();
                    }
                    BufferedOutputStream bos;
                    bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
                    b.compress(Bitmap.CompressFormat.JPEG, 80, bos);
                    bos.flush();
                    bos.close();
                    updatePic(filePath);
                } else {
                    Toast toast = Toast.makeText(MainActivity.this, "保存失败，SD卡无效", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            } catch (Exception e) {
            }
        } else {
            try {
                Cursor cursor = managedQuery(data.getData(), new String[]{MediaStore.Images.Media.DATA},
                        null, null, null);
                int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                //最后根据索引值获取图片路径
                String filePath = cursor.getString(index);
                if (TextUtils.isEmpty(filePath)) return;

                updatePic(filePath);
            } catch (Exception e) {
            }
        }
    }

    /**
     * 生成上传实体类并且绑定视图
     * @param picPath
     */
    private void updatePic(String picPath) {
        if (mUploadModel == null) {
            mUploadModel = new MyUploadModel();
        }
        // 添加额外的上传参数
        mUploadModel.setUploadParams(getUploadParams());
        // 显示选择的图片
        mUploadFileTip.setText(picPath);
        // 重新绑定新图片路径
        mUploadModel.setLocalPath(picPath);
        // 上传前会绑定显示视图
        mUploadModel.bindUploadProgress(mUploadView);
    }

    public void doUpload(View v) {
        UploadManager.getInstance().execute("http://up-z1.qiniu.com", mUploadModel);
    }

    /**
     *
     * 因为此处我使用了七牛云存储接收图片,因为需要拼装相应的请求参数
     * 此处只是做了个模拟,随便输入一个token所以上传后,返回的数据是...Bad token
     * @return
     */
    private ContentValues getUploadParams() {
        ContentValues uploadParams = new ContentValues();
        uploadParams.put("token", "asdflkasjdlkfjalksdjflkajsdlkfjalksjdflkajsdljlasjefljlasdf");
        return uploadParams;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mUploadModel != null) {
            mUploadModel.destory();
        }
        ((App)getApplication()).exit();
    }
}
