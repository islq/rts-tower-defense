package io.anuke.mindustry;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(()-> startGame(), 1000);

    }

    public void startGame() {
        Intent intent = new Intent();
        intent.setClass(this, AndroidLauncher.class);
        startActivity(intent);
        finish();
    }
}
