package cn.zhaoyb.zcore.uploadimage.listener;

/**
 * 上传数据实体类对应的视图
 *
 * @author zhaoyb.cn
 */
public interface IUploadViewDisplay extends IUploadProgress {

    /** 设置其视图重传监听*/
    void setOnRetryListener(IUploadRetryListener retryListener);

    /** 回收视图*/
    void recyleView();
}
