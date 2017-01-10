package cnx.cclink.unishare.platform;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.sina.weibo.sdk.WeiboAppManager;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.utils.Utility;

import cnx.cclink.unishare.R;
import cnx.cclink.unishare.ShareApi;
import cnx.cclink.unishare.ShareError;
import cnx.cclink.unishare.ShareHelper;


/**
 * Created by cclink on 2016/12/30.
 * 微博分享功能，调用微博SDK相关的Api
 * 分享完成后，回调的时候需要原先的Activity实现IWeiboHandler.Respons接口，为了避免每个分享的activity都要这样做一遍，
 * 这里封装一个透明的activity，用来作为调用分享api的activity
 */
public class WeiBoShareActivity extends AppCompatActivity implements IWeiboHandler.Response {
    // 新浪微博的APP ID
    private static final String WEIBO_APP_KEY = "0000000000000000000000";

    private IWeiboShareAPI mWeiboShareAPI;
    private String mShareTitle;
    private String mShareDetail;
    private String mShareImage;
    private String mShareURL;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transparent);

        // 创建微博分享接口实例
        mWeiboShareAPI = getWeiboApi(this);

        // 注册第三方应用到微博客户端中，注册成功后该应用将显示在微博的应用列表中。
        // 但该附件栏集成分享权限需要合作申请，详情请查看 Demo 提示
        // NOTE：请务必提前注册，即界面初始化的时候或是应用程序初始化时，进行注册
        mWeiboShareAPI.registerApp();

        // 当 Activity 被重新初始化时（该 Activity 处于后台时，可能会由于内存不足被杀掉了），
        // 需要调用 {@link IWeiboShareAPI#handleWeiboResponse} 来接收微博客户端返回的数据。
        // 执行成功，返回 true，并调用 {@link IWeiboHandler.Response#onResponse}；
        // 失败返回 false，不调用上述回调
        if (savedInstanceState != null) {
            mWeiboShareAPI.handleWeiboResponse(getIntent(), this);
        }

        // 获取要分享的内容
        Intent intent = getIntent();
        mShareImage = intent.getStringExtra("imageFile");
        mShareTitle = intent.getStringExtra("title");
        mShareDetail = intent.getStringExtra("detail");
        mShareURL = intent.getStringExtra("shareURL");
        // 分享
        share();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void share() {
        Bitmap bm = BitmapFactory.decodeFile(mShareImage);
        Bitmap thumbBm = ShareHelper.getBitmapFromFile(mShareImage);

        // 要分享的文本内容
        TextObject textObject = new TextObject();
        textObject.text = mShareDetail;

        // 要分享的图片内容
        ImageObject imageObject = new ImageObject();
        imageObject.setImageObject(bm);

        // 要分享的链接（设置WebpageObject时必须设置每个成员变量，不过最终显示的只有title）
        WebpageObject webpageObject = new WebpageObject();
        webpageObject.identify = Utility.generateGUID();
        webpageObject.setThumbImage(thumbBm);
        webpageObject.title = mShareTitle;
        webpageObject.description = mShareDetail;
        webpageObject.actionUrl = mShareURL;
        webpageObject.defaultText = mShareDetail;

        // 将要分享的所有内容封装到WeiboMultiMessage中
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        weiboMessage.textObject = textObject;
        weiboMessage.imageObject = imageObject;
        weiboMessage.mediaObject = webpageObject;

        // 将要分享的内容封装到SendMultiMessageToWeiboRequest中
        SendMultiMessageToWeiboRequest multiRequest = new SendMultiMessageToWeiboRequest();
        multiRequest.transaction = String.valueOf(System.currentTimeMillis());
        multiRequest.multiMessage = weiboMessage;

        // 发送分享请求
        mWeiboShareAPI.sendRequest(this, multiRequest);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        // 从当前应用唤起微博并进行分享后，返回到当前应用时，需要在此处调用该函数
        // 来接收微博客户端返回的数据；执行成功，返回 true，并调用
        // {@link IWeiboHandler.Response#onResponse}；失败返回 false，不调用上述回调
        mWeiboShareAPI.handleWeiboResponse(intent, this);
    }

    // 分享完成后收到的回调信息
    @Override
    public void onResponse(BaseResponse baseResponse) {
        switch (baseResponse.errCode) {
            case WBConstants.ErrorCode.ERR_OK:
                ShareApi.shareComplete();
                break;
            case WBConstants.ErrorCode.ERR_CANCEL:
                ShareApi.shareCanceled();
                break;
            case WBConstants.ErrorCode.ERR_FAIL: {
                ShareError error = new ShareError(baseResponse.errCode, baseResponse.errMsg, baseResponse.errMsg);
                ShareApi.shareError(error);
                break;
            }
            default: {
                ShareError error = new ShareError(baseResponse.errCode, baseResponse.errMsg, baseResponse.errMsg);
                ShareApi.shareError(error);
                break;
            }
        }
        finish();
    }

    // 这个函数是复制的WeiboShareAPIImpl的isWeiboAppInstalled()方法，但是避免了创建IWeiboShareAPI对象，
    // 在创建IWeiboShareAPI对象时会有一些额外的操作，还有一个异步的线程在运行。
    public static boolean isAppInstalled(Context context) {
        WeiboAppManager.WeiboInfo weiboInfo = WeiboAppManager.getInstance(context).getWeiboInfo();
        return weiboInfo != null && weiboInfo.isLegal();
    }

    private static IWeiboShareAPI getWeiboApi(Context context) {
        return WeiboShareSDK.createWeiboAPI(context, WEIBO_APP_KEY);
    }
}
