package cnx.cclink.unishare.platform;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import cnx.cclink.unishare.ShareHelper;
import im.yixin.sdk.api.IYXAPI;
import im.yixin.sdk.api.SendMessageToYX;
import im.yixin.sdk.api.YXAPIFactory;
import im.yixin.sdk.api.YXMessage;
import im.yixin.sdk.api.YXWebPageMessageData;
import im.yixin.sdk.util.BitmapUtil;

/**
 * Created by cclink on 2017/01/04.
 *  易信分享功能，调用易信SDK相关的Api
 */

public class YixinShareApi {

    // 易信的APP ID，由于易信在回调Receiver（YXReceiver）里面需要用到APP ID，所以这里定义为public
    public static final String YIXIN_APP_ID = "0000000000000000000000";

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
        IYXAPI api = getYixinApi(context);
        SendMessageToYX.Req req = getYixinShareReq(imageFile, shareURL, title, detail, isTimeline);
        api.sendRequest(req);
    }

    private static SendMessageToYX.Req getYixinShareReq(String imageFile, String shareURL, String title, String detail, boolean isTimeline) {
        SendMessageToYX.Req req = new SendMessageToYX.Req();
        YXMessage msg = new YXMessage();

        msg.messageData = new YXWebPageMessageData(shareURL);
        msg.title = title;
        msg.description = detail;
        req.transaction = String.valueOf(System.currentTimeMillis());

        Bitmap bmp = ShareHelper.getThumbBitmapFromFile(imageFile);
        msg.thumbData = BitmapUtil.bmpToByteArray(bmp, true);
        req.message = msg;
        req.scene = isTimeline ? SendMessageToYX.Req.YXSceneTimeline : SendMessageToYX.Req.YXSceneSession;
        return req;
    }

    public static boolean isAppInstalled(Activity activity) {
        return getYixinApi(activity).isYXAppInstalled();
    }

    public static IYXAPI getYixinApi(Context context) {
        IYXAPI api = YXAPIFactory.createYXAPI(context, YIXIN_APP_ID);
        api.registerApp();
        return api;
    }
}
