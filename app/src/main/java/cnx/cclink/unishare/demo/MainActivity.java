package cnx.cclink.unishare.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import cnx.cclink.unishare.ShareApi;
import cnx.cclink.unishare.ShareContent;
import cnx.cclink.unishare.ShareError;
import cnx.cclink.unishare.ShareListener;
import cnx.cclink.unishare.SharePlatform;

public class MainActivity extends AppCompatActivity {

    private static String FILE_NAME = "share.png";
    private File mFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button shareBtn = (Button) findViewById(R.id.shareBtn);
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareApi.share(MainActivity.this, SharePlatform.SYSTEM_SHARE, new ShareContent("标题", "内容", mFile.getAbsolutePath(), "https://github.com/cclink"), new ShareListener() {
                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(ShareError shareError) {

                    }
                });
            }
        });

        // 由于需要将图片路径传递给外部app，而外部app没有访问assets下文件的权限，
        // 所以这里将assets中的share.png复制到/storage/emulated/0/Android/data/cnx.cclink.unishare.demo/cache文件夹中，以便分享的时候使用
        File cacheDir = getExternalCacheDir();
        if (cacheDir != null) {
            String fileName = cacheDir.getAbsolutePath() + File.separator + FILE_NAME;
            mFile = new File(fileName);
            if (!mFile.exists()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            InputStream inStream = getAssets().open("share.png");
                            FileOutputStream outStream = new FileOutputStream(mFile);
                            byte[] buffer = new byte[1444];
                            int byteread;
                            while ( (byteread = inStream.read(buffer)) != -1) {
                                outStream.write(buffer, 0, byteread);
                            }
                            outStream.flush();
                            outStream.close();
                            inStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }
    }
}
