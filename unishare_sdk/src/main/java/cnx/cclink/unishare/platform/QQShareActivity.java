package cnx.cclink.unishare.platform;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.tencent.connect.common.AssistActivity;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import java.util.ArrayList;
import java.util.List;

import cnx.cclink.unishare.R;
import cnx.cclink.unishare.ShareApi;
import cnx.cclink.unishare.ShareError;

/**
 * Created by zjn0645 on 2016/12/30.
 * QQ分享功能，调用QQ SDK相关的Api
 * 分享完成后，回调的时候需要原先的Activity通过onActivityResult，将相关信息传递到Tencent.onActivityResultData接口中，
 * 为了避免每个分享的activity都要这样做一遍，
 * 这里封装一个透明的activity，用来作为调用分享api的activity
 */
public class QQShareActivity extends AppCompatActivity {
    // QQ的APP ID
    private static final String QQ_APP_ID = "1104906138";

    private Tencent mTencent;
    private IUiListener mQQShareListener;

    private String mShareTitle;
    private String mShareDetail;
    private String mShareImageFile;
    private String mShareURL;
    private boolean mIsTimeLine;
    private boolean mIsQQStart;
    private boolean mCancelAnyway;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transparent);

        // 获取要分享的内容
        Intent intent = getIntent();
        mShareTitle = intent.getStringExtra("title");
        mShareDetail = intent.getStringExtra("detail");
        mShareImageFile = intent.getStringExtra("imageFile");
        mShareURL = intent.getStringExtra("shareURL");
        mIsTimeLine = intent.getBooleanExtra("isTimeline", false);
        mIsQQStart = false;
        mCancelAnyway = false;
    }

    @Override
    protected void onStart() {
        // 如果已经启动QQ分享，又再次回到onStart，说明分享过程应该已经结束了，这里等待1秒钟后判断是否已经finish，
        // 如果已经finish，说明Activity收到了QQ的回调，不需要在执行操作了
        // 如果没有，则需要手动将QQ的AssitActivity结束掉
        if (mIsQQStart) {
            getWindow().getDecorView().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!isFinishing()) {
                        finishAssitActivity();
                    }
                }
            }, 1000);
            mIsQQStart = false;
        }
        super.onStart();
    }

    private void finishAssitActivity() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        // 在Android 21以下版本中ActivityManager.getRunningTasks()是可以使用的，
        // 在21及以上版本中此api被标记为了deprecated，不过仍然可以获取部分信息。
        // 在21以上版本中可以用21以下版本中ActivityManager.getAppTasks()这个新的api来获取，
        // 但是在21-23版本中，这个api获取到的AppTask中没有得到activity信息
        // 所以在21-23版本仍然用原先的ActivityManager.getRunningTasks() api
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            List<ActivityManager.RunningTaskInfo> appTasks = activityManager.getRunningTasks(10);
            for (ActivityManager.RunningTaskInfo task : appTasks) {
                if (task.topActivity.getClassName().equals("com.tencent.connect.common.AssistActivity")) {
                    startAssitActivity();
                    break;
                }
            }
        } else {
            List<ActivityManager.AppTask> appTasks = activityManager.getAppTasks();
            for (ActivityManager.AppTask task : appTasks) {
                if (task.getTaskInfo().topActivity.getClassName().equals("com.tencent.connect.common.AssistActivity")) {
                    startAssitActivity();
                    break;
                }
            }
        }
    }

    // 由于AssistActivity仍然在当前Acitivity堆栈中，重新start该Activity会触发该Activity的onNewIntent方法
    // 在onNewIntent中会把AssistActivity finish掉
    // 事实上，AssistActivity正常返回时也是通过onNewIntent来实现的
    private void startAssitActivity() {
        Intent intent = new Intent(QQShareActivity.this, AssistActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        mCancelAnyway = true;
    }

    @Override
    protected void onResume() {
        if (!mIsQQStart) {
            shareToQQ(this, mShareTitle, mShareDetail, mShareImageFile, mShareURL, mIsTimeLine);
            mIsQQStart = true;
        }
        super.onResume();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mCancelAnyway) {
            if (mQQShareListener != null) {
                mQQShareListener.onCancel();
            }
            mCancelAnyway = false;
        } else {
            if (mTencent != null) {
                Tencent.onActivityResultData(requestCode, resultCode, data, mQQShareListener);
            }
        }
    }

    private void shareToQQ(final Activity activity, String title, String detail, String imageFile, String shareURL, boolean isTimeline) {
        mTencent = getQQApi(activity);
        mQQShareListener = new IUiListener() {
            @Override
            public void onComplete(Object o) {
                ShareApi.shareComplete();
                finish();
            }

            @Override
            public void onCancel() {
                ShareApi.shareCanceled();
                finish();
            }

            @Override
            public void onError(UiError uiError) {
                ShareError error = new ShareError(uiError.errorCode, uiError.errorMessage, uiError.errorDetail);
                ShareApi.shareError(error);
                finish();
            }
        };
        Bundle params = new Bundle();
        // 分享到朋友圈
        // 注意：和分享给好友不同的是，分享到朋友圈的时候，图片要用ArrayList，因为朋友圈支持多图
        // （然而实际上现在分享的接口还是只支持一张图。。。）
        if (isTimeline) {
            params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
            params.putString(QzoneShare.SHARE_TO_QQ_TITLE, title);
            params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, detail);
            params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, shareURL);
            ArrayList<String> imageUrls = new ArrayList<>();
            imageUrls.add(imageFile);
            params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);
            mTencent.shareToQzone(activity, params, mQQShareListener);
        }
        // 分享给好友
        else {
            params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
            params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
            params.putString(QQShare.SHARE_TO_QQ_SUMMARY, detail);
            params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, shareURL);
            params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imageFile);
            params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE);
            mTencent.shareToQQ(activity, params, mQQShareListener);
        }
    }

    // 此方法的代码是将sdk里面SystemUtils.checkMobileQQ()的代码复制过来的
    // SystemUtils类只在完整的QQ sdk包里存在，精简版本没有
    public static boolean isAppInstalled(Context var0) {
        PackageManager var1 = var0.getPackageManager();
        PackageInfo var2 = null;

        try {
            var2 = var1.getPackageInfo("com.tencent.mobileqq", 0);
        } catch (PackageManager.NameNotFoundException var7) {
            var7.printStackTrace();
        }

        if(var2 != null) {
            String var3 = var2.versionName;
            try {
                String[] var4 = var3.split("\\.");
                int var5 = Integer.parseInt(var4[0]);
                int var6 = Integer.parseInt(var4[1]);
                return var5 > 4 || var5 == 4 && var6 >= 1;
            } catch (Exception var8) {
                var8.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }

    private static Tencent getQQApi(Context context) {
        return Tencent.createInstance(QQ_APP_ID, context);
    }
}
