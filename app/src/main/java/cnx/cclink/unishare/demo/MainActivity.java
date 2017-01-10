package cnx.cclink.unishare.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import cnx.cclink.unishare.ShareApi;
import cnx.cclink.unishare.ShareContent;
import cnx.cclink.unishare.ShareError;
import cnx.cclink.unishare.ShareListener;
import cnx.cclink.unishare.SharePlatform;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button shareBtn = (Button) findViewById(R.id.shareBtn);
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareApi.share(MainActivity.this, SharePlatform.QQ, new ShareContent("标题", "内容", "", "https://github.com/cclink"), new ShareListener() {
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
    }
}
