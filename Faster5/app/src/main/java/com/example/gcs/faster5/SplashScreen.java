package com.example.gcs.faster5;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Kien on 07/05/2016.
 */
public class SplashScreen extends AppCompatActivity {
    RelativeLayout background;
    TextView loadingText;
    ImageView starLoad1, starLoad2, starLoad3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.splash_screen);

        background = (RelativeLayout) findViewById(R.id.background);
        background.setBackgroundResource(R.drawable.background);

        starLoad1 = (ImageView) findViewById(R.id.starnoload1);
        starLoad2 = (ImageView) findViewById(R.id.starnoload2);
        starLoad3 = (ImageView) findViewById(R.id.starnoload3);
        starLoad1.setImageResource(R.drawable.starnoload);
        starLoad2.setImageResource(R.drawable.starnoload);
        starLoad3.setImageResource(R.drawable.starnoload);


        loadingText = (TextView) findViewById(R.id.testText);
        Typeface font = Typeface.createFromAsset(getAssets(),
                "fonts/dimboregular.ttf");
        loadingText.setTypeface(font);
        new CountDownTimer(1000, 1000) {
            public void onFinish() {
                loadingText.setText("30%.");
                starLoad1.setImageResource(R.drawable.starunfinish);
            }

            public void onTick(long millisUntilFinished) {
            }
        }.start();

        new CountDownTimer(2000, 1000) {
            public void onFinish() {
                loadingText.setText("33%.");
                starLoad1.setImageResource(R.drawable.starloaded);
            }

            public void onTick(long millisUntilFinished) {
            }
        }.start();
        new CountDownTimer(3000, 1000) {
            public void onFinish() {
                loadingText.setText("66%..");
                starLoad2.setImageResource(R.drawable.starloaded);
            }

            public void onTick(long millisUntilFinished) {
            }
        }.start();
        new CountDownTimer(3500, 1000) {
            public void onFinish() {
                loadingText.setText("99%...");
                starLoad3.setImageResource(R.drawable.starunfinish);
            }

            public void onTick(long millisUntilFinished) {
            }
        }.start();
        new CountDownTimer(5000, 1000) {
            public void onFinish() {
                loadingText.setText("100%");
                starLoad3.setImageResource(R.drawable.starloaded);
                Intent startActivity = new Intent(getBaseContext(), WellcomeScreen.class);
                startActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(startActivity);
                finish();
            }

            public void onTick(long millisUntilFinished) {
            }
        }.start();
    }
}