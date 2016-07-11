package com.example.gcs.faster5;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

/**
 * Created by Kien on 07/08/2016.
 */
public class TestInfo extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.test_info);
        ImageView caseImageView = (ImageView) findViewById(R.id.caseImage);
        TranslateAnimation animation1 = new TranslateAnimation(-250.0f, 500.0f,
                0.0f, 0.0f);          //  new TranslateAnimation(xFrom,xTo, yFrom,yTo)
        animation1.setDuration(1000);
        animation1.setFillAfter(true);
        TranslateAnimation animation2 = new TranslateAnimation(750.0f, 500.0f,
                0.0f, 0.0f);          //  new TranslateAnimation(xFrom,xTo, yFrom,yTo)
        animation2.setDuration(1000);
        animation2.setFillAfter(true);
        caseImageView.startAnimation(animation1);
        caseImageView.startAnimation(animation2);
    }
}
