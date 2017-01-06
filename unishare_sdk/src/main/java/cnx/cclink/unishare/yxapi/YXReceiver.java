package cnx.cclink.unishare.yxapi;


import cnx.cclink.unishare.platform.YixinShareApi;
import im.yixin.sdk.api.YXAPIBaseBroadcastReceiver;
import im.yixin.sdk.channel.YXMessageProtocol;

public class YXReceiver extends YXAPIBaseBroadcastReceiver {
	@Override
	protected String getAppId()	{
		return YixinShareApi.YIXIN_APP_ID;
	}

	/***********************
	 * 易信启动后，会广播消息给第三方APP，第三方APP注册之后，系统会调用此函数。
	 * 当第三方APP需要在易信启动后，完成相关工作，可以实现此函数。
	 * 此函数默认实现为空。
	 * @param protocol
	 */
	protected void onAfterYixinStart(final YXMessageProtocol protocol){

	}
}
