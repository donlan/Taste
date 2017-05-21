
package dong.lan.taste.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import dong.lan.avoscloud.bean.AVOUser;
import dong.lan.taste.R;

/**
 * 闪屏页面
 */

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (AVOUser.getCurrentUser() == null) { //没有登录则直接跳转到登陆页面
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else { //否则直接进入主页
            setContentView(R.layout.activity_splash);
            new Handler().sendEmptyMessageDelayed(0, 1000);
        }
    }

    private class Handler extends android.os.Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }
    }
}
