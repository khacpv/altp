package com.example.gcs.faster5.ui.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.gcs.faster5.R;
import com.example.gcs.faster5.model.Topic;
import com.example.gcs.faster5.network.ServiceMng;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Kien on 07/05/2016.
 */
public class SplashScreen extends AppCompatActivity {
    RelativeLayout mRelativeLayoutBg;
    TextView mTextViewLoading;
    ImageView mImageViewStarLoad1, mImageViewStarLoad2, mImageViewStarLoad3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.splash_screen);

        mRelativeLayoutBg = (RelativeLayout) findViewById(R.id.background);
        mRelativeLayoutBg.setBackgroundResource(R.drawable.background);

        mImageViewStarLoad1 = (ImageView) findViewById(R.id.image_starload1);
        mImageViewStarLoad2 = (ImageView) findViewById(R.id.image_starload2);
        mImageViewStarLoad3 = (ImageView) findViewById(R.id.image_starload3);
        mImageViewStarLoad1.setImageResource(R.drawable.starnoload);
        mImageViewStarLoad2.setImageResource(R.drawable.starnoload);
        mImageViewStarLoad3.setImageResource(R.drawable.starnoload);

        mTextViewLoading = (TextView) findViewById(R.id.text_test);
        Typeface font = Typeface.createFromAsset(getAssets(),
                "fonts/dimboregular.ttf");
        mTextViewLoading.setTypeface(font);
        new CountDownTimer(1000, 1000) {
            public void onFinish() {
                mTextViewLoading.setText("30%.");
                mImageViewStarLoad1.setImageResource(R.drawable.starunfinish);
            }

            public void onTick(long millisUntilFinished) {
            }
        }.start();

        new CountDownTimer(2000, 1000) {
            public void onFinish() {
                mTextViewLoading.setText("33%.");
                mImageViewStarLoad1.setImageResource(R.drawable.starloaded);
            }

            public void onTick(long millisUntilFinished) {
            }
        }.start();
        new CountDownTimer(3000, 1000) {
            public void onFinish() {
                mTextViewLoading.setText("66%..");
                mImageViewStarLoad2.setImageResource(R.drawable.starloaded);
            }

            public void onTick(long millisUntilFinished) {
            }
        }.start();
        new CountDownTimer(3500, 1000) {
            public void onFinish() {
                mTextViewLoading.setText("99%...");
                mImageViewStarLoad3.setImageResource(R.drawable.starunfinish);
            }

            public void onTick(long millisUntilFinished) {
            }
        }.start();
        new CountDownTimer(5000, 1000) {
            public void onFinish() {
                mTextViewLoading.setText("100%");
                mImageViewStarLoad3.setImageResource(R.drawable.starloaded);
                Intent mIntent = new Intent(getBaseContext(), WellcomeScreen.class);
                mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mIntent);
                finish();
            }

            public void onTick(long millisUntilFinished) {
            }
        }.start();

        getAllTopics();
    }

    /**
     * get all topic
     * */
    private void getAllTopics(){
        new ServiceMng().api().getTopic().enqueue(new Callback<List<Topic>>() {
            @Override
            public void onResponse(Call<List<Topic>> call, Response<List<Topic>> response) {
                List<Topic> topics = response.body();
                Log.e("TAG","SUCCESS topics: "+topics.size());
            }

            @Override
            public void onFailure(Call<List<Topic>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}