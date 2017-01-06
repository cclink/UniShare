package cnx.cclink.unishare.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

import cnx.cclink.unishare.ShareApi;
import cnx.cclink.unishare.ShareError;
import cnx.cclink.unishare.platform.WeixinShareApi;


public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    // IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        api = WeixinShareApi.getWeixinApi(this);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
        api.handleIntent(intent, this);
    }

    // 微信发送请求到第三方应用时，会回调到该方法
    @Override
    public void onReq(BaseReq arg0) {
        // TODO Auto-generated method stub

    }

    // 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
    @Override
    public void onResp(BaseResp resp) {

        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                ShareApi.shareComplete();
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                ShareApi.shareCanceled();
                break;
            default:
                ShareError error = new ShareError(resp.errCode, resp.errStr, resp.errStr);
                ShareApi.shareError(error);
                break;
        }
        finish();
    }
}
