package cnx.cclink.unishare.yxapi;

import cnx.cclink.unishare.ShareApi;
import cnx.cclink.unishare.ShareError;
import cnx.cclink.unishare.platform.YixinShareApi;
import im.yixin.sdk.api.BaseReq;
import im.yixin.sdk.api.BaseResp;
import im.yixin.sdk.api.BaseYXEntryActivity;
import im.yixin.sdk.api.IYXAPI;
import im.yixin.sdk.api.SendMessageToYX;
import im.yixin.sdk.util.YixinConstants;

public class YXEntryActivity extends BaseYXEntryActivity {
    /**
     * 易信调用调用时的触发函数
     */
    @Override
    public void onReq(BaseReq arg0) {
        // TODO Auto-generated method stub

    }

    /*******************
     * 返回第三方app根据app id创建的IYXAPI，
     *
     */
    @Override
    public void onResp(BaseResp resp) {
        if (resp.getType() == YixinConstants.RESP_SEND_MESSAGE_TYPE) {
            SendMessageToYX.Resp resp1 = (SendMessageToYX.Resp) resp;
            switch (resp1.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    ShareApi.shareComplete();
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    ShareApi.shareCanceled();
                    break;
                default:
                    ShareError error = new ShareError(resp1.errCode, resp1.errStr, resp1.errStr);
                    ShareApi.shareError(error);
                    break;
            }
        }

        finish();
    }

    @Override
    protected IYXAPI getIYXAPI() {
        return YixinShareApi.getYixinApi(this);
    }
}
