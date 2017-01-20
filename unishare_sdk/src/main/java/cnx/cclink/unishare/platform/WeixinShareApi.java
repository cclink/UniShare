package cnx.cclink.unishare.platform;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;

import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import cnx.cclink.unishare.ShareHelper;

/**
 * Created by cclink on 2017/01/04.
 * 微信分享功能，调用微信SDK相关的Api
 */
public class WeixinShareApi {
    // 微信的APP ID，由于微信在回调Receiver（WXReceiver）里面需要用到APP ID，所以这里定义为public
    public static final String WEIXIN_APP_ID = "0000000000000000000000";

    /**
     * 分享
     * @param context       context
     * @param title         要分享的标题
     * @param detail        要分享的内容
     * @param imageFile     要分享的图片（缩略图）
     * @param shareURL      跳转的URL
     * @param isTimeline    是否分享到朋友圈
     */
    public static void share(Context context, String title, String detail, String imageFile, String shareURL, boolean isTimeline) {
        IWXAPI api = getWeixinApi(context);
        SendMessageToWX.Req req = getWeixinShareReq(imageFile, shareURL, title, detail, isTimeline);
        api.sendReq(req);
    }

    private static SendMessageToWX.Req getWeixinShareReq(String imageFile, String shareURL, String title, String detail, boolean isTimeline) {
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        WXMediaMessage msg = new WXMediaMessage();

        WXWebpageObject webObj = new WXWebpageObject();
        webObj.webpageUrl = shareURL;

        msg.mediaObject = webObj;
        msg.title = title;
        msg.description = detail;
        req.transaction = String.valueOf(System.currentTimeMillis());

        Bitmap bmp = ShareHelper.getThumbBitmapFromFile(imageFile);
        if (bmp != null) {
            msg.thumbData = ShareHelper.bmpToByteArray(bmp);
        }
        req.message = msg;
        req.scene = isTimeline ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
        return req;
    }

    public static boolean isAppInstalled(Activity activity) {
        return getWeixinApi(activity).isWXAppInstalled();
    }

    public static IWXAPI getWeixinApi(Context context) {
        IWXAPI api = WXAPIFactory.createWXAPI(context, WEIXIN_APP_ID, false);
        api.registerApp(WEIXIN_APP_ID);
        return api;
    }
}
