package cn.zhaoyb.zcore.uploadimage.entity;

import android.content.ContentValues;
import android.text.TextUtils;

import cn.zhaoyb.zcore.uploadimage.listener.IUploadModel;
import cn.zhaoyb.zcore.uploadimage.listener.IUploadProgress;
import cn.zhaoyb.zcore.uploadimage.utils.UploadConstant;

/**
 * 本地选择的待上传图片
 *
 * @author zhaoyb.cn
 */
public class SimpleUploadModel implements IUploadModel {

    // 默认为正常图片
    public int upload_state = UploadConstant.UPLOAD_PIC_NORMAL;
    // 本地图片路径,,有可能发生变化(比如压缩后的新图片路径)
    private String localPath;

    public void setLocalPath(String localPath) {
        // 只要修改了本地图片,则将相应字段清空
        upload_state = UploadConstant.UPLOAD_PIC_NORMAL;
        // 更新路径
        this.localPath = localPath;
    }

    /** ================IUploadModel接口方法============================*/
    @Override
    public int doResult(int uploadResult, String newFilePath, Object resultMessage) {
        // 默认失败了
        int realUploadSuccess = UploadConstant.RESULT_FAILED;
        // 记录新的图片路径(copy并且压缩后的)
        if (!TextUtils.isEmpty(newFilePath)) {
            this.localPath = newFilePath;
        }
        try {
            if (uploadResult == UploadConstant.RESULT_SUCCESS) {
                if (resultMessage != null && resultMessage instanceof String) {
                    boolean hasError = ((String) resultMessage).contains("error");
                    if (hasError) {
                    } else {
                        // 接口返回的数据 resultMessage 进行解析等扫尾巴操作
                        realUploadSuccess = UploadConstant.RESULT_SUCCESS;
                    }
                }

            }
        } catch (Exception e) {
        }
        // 记录最终状态
        if (realUploadSuccess == UploadConstant.RESULT_FAILED) {
            upload_state = UploadConstant.UPLOAD_PIC_FAILED;
        } else {
            upload_state = UploadConstant.UPLOAD_PIC_SUCCESS;
        }
        return realUploadSuccess;
    }

    /**
     * 验证是否需要上传
     *
     * 1，网络图不用上传
     * 2, 本地图片路径不存在不用上传
     * 3, 图片当前状态为“更多”，进行中，成功，不用上传
     *
     * @return
     */
    @Override
    public boolean isNeedUpload() {
        if (TextUtils.isEmpty(localPath)) {
            return false;
        }
        if (upload_state == UploadConstant.UPLOAD_PIC_TIP ||
                upload_state == UploadConstant.UPLOAD_PIC_ING ||
                upload_state == UploadConstant.UPLOAD_PIC_SUCCESS) {
            return false;
        }
        return true;
    }

    @Override
    public String getUploadFilePath() {
        return localPath;
    }

    @Override
    public ContentValues getUploadParams() {
        return null;
    }

    private IUploadProgress mIUploadProgress;
    @Override
    public void bindUploadProgress(IUploadProgress mIUploadProgress) {
        this.mIUploadProgress = mIUploadProgress;
    }
    @Override
    public IUploadProgress getUploadProgress() {
        return mIUploadProgress;
    }

    public void destory() {
        mIUploadProgress = null;
    }
}

