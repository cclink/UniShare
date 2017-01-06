package cnx.cclink.unishare;

/**
 * Created by cclink on 2017/01/02.
 * 分享收到回调中的错误信息
 */

public class ShareError {
    public int errorCode;           // 错误码
    public String errorMessage;     // 错误消息
    public String errorDetail;      // 错误详情(只有QQ分享回调的错误信息中会带有一个详情信息)

    public ShareError(int errorCode, String errorMessage, String errorDetail) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.errorDetail = errorDetail;
    }

    @Override
    public String toString() {
        return "Error Code: " + errorCode + " Error meessage: " + errorMessage + " Error Detail: " + errorDetail;
    }
}
