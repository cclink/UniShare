package cnx.cclink.unishare.platform;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import cnx.cclink.unishare.ShareHelper;


/**
 * Created by zjn0645 on 2017/1/10.
 * 短信分享接口
 */

public class ShortMessageShareApi {
    public static void share(Context context, String title, String detail, String imageFile, String shareURL) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"));
        intent.putExtra("sms_body", ShareHelper.mergeString(title, detail, shareURL));
        context.startActivity(intent);
    }
}
