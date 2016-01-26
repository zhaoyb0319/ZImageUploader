package cn.zhaoyb.zcore.uploadimage;

import android.content.ContentValues;
import android.text.TextUtils;

import java.io.File;

import cn.zhaoyb.zcore.uploadimage.listener.IUploadModel;
import cn.zhaoyb.zcore.uploadimage.progress.ProgressRequestBody;
import cn.zhaoyb.zcore.uploadimage.utils.BitmapCompressManager;
import cn.zhaoyb.zcore.uploadimage.utils.UploadConstant;
import okhttp3.Call;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * 上传请求
 * 1，目标上传地址
 * 2，上传对应的model
 * 3，自身请求需要添加超时机型,在超过指定时间后，若还未执行完,则自动销毁上传操作
 *
 * @author zhaoyb.cn
 */
public class UploadRequest {

    // 数据上传的目标地址(服务端地址)
    private String uploadUrl;
    // 上传请求携带的数据
    private IUploadModel mIUploadModel;

    // 对原始照片进行压缩和处理后,会得到临时的数据和临时图片路径
    private String tempFilePath;

    // 请求对象,方便cancel
    private Request mRequest;
    private Call mCall;

    public UploadRequest(String uploadUrl,
                         IUploadModel mIUploadModel) {
        this.uploadUrl = uploadUrl;
        this.mIUploadModel = mIUploadModel;
    }

    /**
     * 对上传操作进行初始化
     * 不会影响视图的状态
     */
    public boolean init() {
        // 上传地址不存在，直接忽略
        if (TextUtils.isEmpty(uploadUrl)) {
            return false;
        }
        // 上传数据不存在，或不需要上传时,忽略
        if (mIUploadModel == null || !mIUploadModel.isNeedUpload()) {
            return false;
        }
        return true;
    }

    public Request getRealRequest() {
        return mRequest;
    }

    public void setRealCall(Call mCall) {
        this.mCall = mCall;
    }

    public void cancel() {
        if (mCall == null) return;
        try {
            mCall.cancel();
        } catch (Exception e){
        }
    }

    /**
     *
     * 生成请求任务
     * @return
     */
    public void initRequest(String userAgent) {
        if (mRequest != null) {
            mRequest = null;
        }
        // 复制、压缩待上传的图片
        String newFilePath = compress(mIUploadModel.getUploadFilePath());

        File targetFile = null;
        if (!TextUtils.isEmpty(newFilePath)) {
            targetFile = new File(newFilePath);
        }
        // 新图片路径不存在时,无法上传
        if (targetFile == null || !(targetFile.exists())) {
            return;
        }
        // 封装请求参数
        RequestBody mRequestBody = getRequestBody(targetFile, mIUploadModel.getUploadParams());
        if (mRequestBody == null) {
            return;
        }
        // 记录可用的临时文件路径
        tempFilePath = newFilePath;
        Request.Builder mRequestBuilder = new Request.Builder();
        mRequestBuilder.url(uploadUrl);
        if (!TextUtils.isEmpty(userAgent)) {
            mRequestBuilder.addHeader("User-Agent", userAgent);
        }
        mRequestBuilder.post(new ProgressRequestBody(mRequestBody, mIUploadModel));
        mRequest = mRequestBuilder.build();
    }

    /**
     * 此方法调用是在线程中触发的,所以此处可做耗时的操作，比如解析返回的数据
     * @param uploadResult
     * @param resultMessage
     */
    public void doResult(int uploadResult, Object resultMessage) {
        int realUploadResult = uploadResult;
        // 未取消才解析数据等
        if (mIUploadModel != null) {
            realUploadResult = mIUploadModel.doResult(uploadResult, tempFilePath, resultMessage);
        }
        if (mIUploadModel.getUploadProgress() != null) {
            mIUploadModel.getUploadProgress().doResult(realUploadResult,
                    realUploadResult == UploadConstant.RESULT_SUCCESS ? null: resultMessage);
        }
    }

    /**
     * 显示具体上传状态
     *
     * @param tip
     */
    public void showProgressListenerTip(String tip) {
        if (mIUploadModel.getUploadProgress() == null) {
            return;
        }
        mIUploadModel.getUploadProgress().initUploadData(tip, tempFilePath);
    }

    /** 获取请求中的实体类*/
    public IUploadModel getUploadModel() {
        return mIUploadModel;
    }

    /**
     * 获取上传参数
     * @return
     */
    private RequestBody getRequestBody(File uploadFilePath, ContentValues uploadParams) {
        // 注意 必须指定 filename 这个参数
        MultipartBody.Builder mRequestBuidler = new MultipartBody.Builder();
        mRequestBuidler.setType(MultipartBody.FORM);
        mRequestBuidler.addFormDataPart("file", "name_" + System.currentTimeMillis(), RequestBody.create(MultipartBody.FORM, uploadFilePath));
        if (uploadParams != null && uploadParams.size() > 0) {
            for (String key : uploadParams.keySet()) {
                mRequestBuidler.addFormDataPart(key, uploadParams.get(key).toString());
            }
        }
        return mRequestBuidler.build();
    }

    /** 对指定文件进行校验（copy和压缩)*/
    private String compress(String uploadFilePath) {
        tempFilePath = uploadFilePath;
        // 复制图片
        showProgressListenerTip(UploadConstant.UPLOAD_PROGRESS_TIP_COPY);
        String newFilePath = BitmapCompressManager.getInstance().copyFileUsingFileChannels(uploadFilePath);

        // 压缩图片
        showProgressListenerTip(UploadConstant.UPLOAD_PROGRESS_TIP_COMPRESS);
        try {
            BitmapCompressManager.getInstance().compressImage(newFilePath);
        } catch (Exception e) {
            newFilePath = null;
        } catch (Error error) {
            newFilePath = null;
        }
        return newFilePath;
    }
}
