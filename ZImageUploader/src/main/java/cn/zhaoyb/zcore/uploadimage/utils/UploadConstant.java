package cn.zhaoyb.zcore.uploadimage.utils;

import android.os.Environment;

import java.io.File;

/**
 *
 * 常量相关
 *
 * @author zhaoyb.cn
 */
public class UploadConstant {

    /**缓存应用主目录名称**/
    public final static String ROOT_APP_DIR = Environment.getExternalStorageDirectory() + File.separator + "ZCore";
    /**用户上传图片缓存路径**/
    public static final String DEFAULT_UPLOAD_IMG_DIR = ROOT_APP_DIR + File.separator + "uploadimg";

    public static final int RESULT_SUCCESS = 0;
    public static final int RESULT_FAILED = RESULT_SUCCESS + 1;

    public static final String UPLOAD_PROGRESS = "upload_progress";
    public static final String UPLOAD_TIP = "upload_tip";
    public static final String UPLOAD_FILE_PATH = "upload_file_path";

    // 上传相关提示语
    public static final String UPLOAD_PROGRESS_TIP_START = "准备上传";
    public static final String UPLOAD_PROGRESS_TIP_COPY = "正在复制";
    public static final String UPLOAD_PROGRESS_TIP_COMPRESS = "正在压缩";
    public static final String UPLOAD_PROGRESS_TIP_ING = "上传中(%s)";
    public static final String UPLOAD_PROGRESS_TIP_FINISH = "正在保存";
    public static final String UPLOAD_PROGRESS_TIP_SUCCESS = "上传成功";
    public static final String UPLOAD_PROGRESS_TIP_FAILED = "上传失败";
    public static final String UPLOAD_PROGRESS_TIP_ERROR = "服务器异常";

    // 上传相关状态
    public static final int UPLOAD_PIC_TIP = 0;                         // 只用于显示的"更多"
    public static final int UPLOAD_PIC_NORMAL = UPLOAD_PIC_TIP + 1;     // 正常状态
    public static final int UPLOAD_PIC_ING = UPLOAD_PIC_NORMAL + 1;     // 上传中
    public static final int UPLOAD_PIC_SUCCESS = UPLOAD_PIC_ING + 1;    // 上传成功
    public static final int UPLOAD_PIC_FAILED = UPLOAD_PIC_SUCCESS + 1; // 上传失败
}
