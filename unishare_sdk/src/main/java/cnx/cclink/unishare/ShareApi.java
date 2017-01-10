package cnx.cclink.unishare;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import java.lang.ref.WeakReference;

import cnx.cclink.unishare.platform.QQShareActivity;
import cnx.cclink.unishare.platform.WBShareActivity;
import cnx.cclink.unishare.platform.WeixinShareApi;
import cnx.cclink.unishare.platform.YixinShareApi;


/** Created by cclink on 2016/12/30.
 *  分享api，根据要分享的目标平台来决定使用哪个sdk下面的接口
 */
public class ShareApi {
    // 成员变量
    private static WeakReference<ShareListener> mListener;     // 分享完成后的回调，它是static的，只能保存最近一次调用share接口的回调对象

    public ShareApi() {

    }

    // 判断要分享的目标app是否已经安装
    public static boolean isPlatformInstalled(Activity activity, SharePlatform platform) {
        switch (platform) {
            case SINA_WEIBO:
                return WBShareActivity.isAppInstalled(activity);
            case QQ:
            case QZONE:
                return QQShareActivity.isAppInstalled(activity);
            case WEIXIN:
            case WEIXIN_TIMELINE:
                return WeixinShareApi.isAppInstalled(activity);
            case YIXIN:
            case YIXIN_TIMELINE:
                return YixinShareApi.isAppInstalled(activity);
        }
        return false;
    }

    // 当收到回调消息，表示分享被取消时调用
    public static void shareCanceled() {
        if (mListener != null) {
            ShareListener listener = mListener.get();
            if (listener != null) {
                listener.onCancel();
            }
            mListener = null;
        }
    }

    // 当收到回调消息，表示分享完成时调用
    public static void shareComplete() {
        if (mListener != null) {
            ShareListener listener = mListener.get();
            if (listener != null) {
                listener.onComplete();
            }
            mListener = null;
        }
    }

    // 当收到回调消息，表示分享失败时调用
    public static void shareError(ShareError error) {
        if (mListener != null) {
            ShareListener listener = mListener.get();
            if (listener != null) {
                listener.onError(error);
            }
            mListener = null;
        }
    }

    // 分享
    public static void share(Activity activity, SharePlatform platform, ShareContent content, ShareListener listener) {
        // 没有分享内容，返回
        if (content == null) {
            return;
        }

        mListener = new WeakReference<>(listener);

        switch (platform) {
            case SINA_WEIBO:
                shareToSina(activity, content.shareTitle, content.shareDetail, content.shareImage, content.shareUrl);
                break;
            case QQ:
                shareToQQ(activity, content.shareTitle, content.shareDetail, content.shareImage, content.shareUrl, false);
                break;
            case QZONE:
                shareToQQ(activity, content.shareTitle, content.shareDetail, content.shareImage, content.shareUrl, true);
                break;
            case WEIXIN:
                shareToWeixin(activity, content.shareTitle, content.shareDetail, content.shareImage, content.shareUrl, false);
                break;
            case WEIXIN_TIMELINE:
                shareToWeixin(activity, content.shareTitle, content.shareDetail, content.shareImage, content.shareUrl, true);
                break;
            case YIXIN:
                shareToYixin(activity, content.shareTitle, content.shareDetail, content.shareImage, content.shareUrl, false);
                break;
            case YIXIN_TIMELINE:
                shareToYixin(activity, content.shareTitle, content.shareDetail, content.shareImage, content.shareUrl, true);
                break;
        }
    }

    // 分享到新浪微博
    private static void shareToSina(Activity activity, String title, String detail, String imageFile, String shareURL) {
        Intent intent = new Intent(activity, WBShareActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("detail", detail);
        intent.putExtra("imageFile", imageFile);
        intent.putExtra("shareURL", shareURL);
        activity.startActivity(intent);
    }

    // 分享到QQ
    private static void shareToQQ(final Activity activity, String title, String detail, String imageFile, String shareURL,boolean isTimeline) {
        Intent intent = new Intent(activity, QQShareActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("detail", detail);
        intent.putExtra("imageFile", imageFile);
        intent.putExtra("shareURL", shareURL);
        intent.putExtra("isTimeline", isTimeline);
        activity.startActivity(intent);
    }

    // 分享到微信
    private static void shareToWeixin(Context context, String title, String detail, String imageFile, String shareURL,  boolean isTimeline) {
        WeixinShareApi.share(context, title, detail, imageFile, shareURL, isTimeline);
    }

    // 分享到易信
    private static void shareToYixin(Context context, String title, String detail, String imageFile, String shareURL, boolean isTimeline) {
        YixinShareApi.share(context, title, detail, imageFile, shareURL, isTimeline);
    }
}
