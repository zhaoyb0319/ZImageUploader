package cn.zhaoyb.zcore.uploadimage.listener;

/**
 * 上传进度监听
 *
 * @author zhaoyb.cn
 */
public interface IUploadProgress {

    /** 处理上传结果,非UI线程操作 */
    void doResult(int uploadResult, Object resultMessage);

    void initUploadData(String tip, String filePath);

    void onProgress(long bytesWrite, long contentLength);
}
