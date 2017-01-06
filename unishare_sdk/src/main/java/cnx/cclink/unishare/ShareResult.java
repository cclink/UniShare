package cnx.cclink.unishare;

/**
 * Created by cclink on 2017/1/4.
 * 分享结果
 */

public interface ShareResult {
    int SHARE_COMPLETE = 0;     // 分享成功
    int SHARE_CANCELLED = 1;    // 分享取消
    int SHARE_FAILED = 2;       // 分享失败
}
