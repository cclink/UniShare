package cnx.cclink.unishare;

/**
 * Created by cclink on 2016/12/30.
 * 调用ShareApi中的share接口时传入的Listener，收到分享回调后，会调用对应的方法
 */

public interface ShareListener {
    void onComplete();
    void onCancel();
    void onError(ShareError shareError);
}
