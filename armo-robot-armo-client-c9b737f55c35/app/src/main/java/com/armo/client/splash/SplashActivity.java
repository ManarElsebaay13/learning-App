package com.armo.client.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.armo.client.base.BaseActivity;
import com.armo.client.home.HomeActivity;
import com.armo.client.network.firebase.FirebaseClientHandler;
import com.armorobot.client.R;

public class SplashActivity extends BaseActivity {

    Handler handler = new Handler();
    Runnable runnable;
    boolean ignore = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        FirebaseClientHandler.getInstance().signin();

        runnable = () -> {
            if (!ignore) {
                Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                startActivity(intent);
            }
            finish();
        };
        handler.postDelayed(runnable, 2000);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ignore = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        ignore = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
        runnable = null;
    }
}
