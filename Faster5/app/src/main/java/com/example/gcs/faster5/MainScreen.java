package com.example.gcs.faster5;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;

/**
 * Created by Kien on 07/05/2016.
 * test
 */
public class MainScreen extends AppCompatActivity {
    RelativeLayout hinhNen;
    ImageView avatarUser1, avatarUser2, fiftyHelp, callHelp, lookerHelp, case1, case2, case3, case4, tableQuestion;
    TranslateAnimation animationCase1, animationCase2, animationHelp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.main_screen);

        avatarUser1 = (ImageView) findViewById(R.id.avatarUser1);
        Glide.with(getApplicationContext()).load("https://graph.facebook.com/" + InfoScreen.idUserFB + "/picture?width=100&height=100").into(avatarUser1);
        avatarUser2 = (ImageView) findViewById(R.id.avatarUser2);

        hinhNen = (RelativeLayout) findViewById(R.id.BackGround);
        hinhNen.setBackgroundResource(R.drawable.bgmain);
        tableQuestion = (ImageView) findViewById(R.id.tableQuestion);
        fiftyHelp = (ImageView) findViewById(R.id.fiftyButton);
        callHelp = (ImageView) findViewById(R.id.callButton);
        lookerHelp = (ImageView) findViewById(R.id.khangiaButton);

        case1 = (ImageView) findViewById(R.id.case1Button);
        case2 = (ImageView) findViewById(R.id.case2Button);
        case3 = (ImageView) findViewById(R.id.case3Button);
        case4 = (ImageView) findViewById(R.id.case4Button);
        Animation();
    }

    public void Animation() {
        animationHelp = new TranslateAnimation(0.0f, 0.0f,
                0.0f, 220.0f);
        animationHelp.setDuration(1000);
        animationHelp.setFillAfter(true);
        fiftyHelp.startAnimation(animationHelp);
        callHelp.startAnimation(animationHelp);
        lookerHelp.startAnimation(animationHelp);

        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(tableQuestion, "alpha", 0f, 1f);
        fadeIn.setDuration(2000);
        final AnimatorSet mAnimationSet = new AnimatorSet();
        mAnimationSet.play(fadeIn);
        mAnimationSet.start();

        animationCase1 = new TranslateAnimation(-250.0f, 580.0f,
                0.0f, 0.0f);
        animationCase2 = new TranslateAnimation(0f, -660.0f,
                0.0f, 0.0f);
        animationCase1.setDuration(500);
        animationCase1.setFillAfter(true);
        animationCase2.setDuration(500);
        animationCase2.setFillAfter(true);
        case1.startAnimation(animationCase1);
        case3.startAnimation(animationCase1);

        animationCase1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                case2.startAnimation(animationCase2);
                case4.startAnimation(animationCase2);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    public void onBackPressed() {
        Intent myIntent = new Intent(getApplicationContext(), InfoScreen.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(myIntent);
        finish();
        return;
    }
}
