package com.example.gcs.faster5;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

/**
 * Created by Kien on 07/05/2016.
 */
public class SplashScreen extends AppCompatActivity {
    RelativeLayout hinhNen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.splash_screen);
        hinhNen = (RelativeLayout) findViewById(R.id.BackGround);
        hinhNen.setBackgroundResource(R.drawable.splash);

        new CountDownTimer(2500, 1000) {
            public void onFinish() {
                Intent startActivity = new Intent(getBaseContext(), LoginScreen.class);
                startActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(startActivity);
                finish();
            }
            public void onTick(long millisUntilFinished) {
            }
        }.start();
    }
}