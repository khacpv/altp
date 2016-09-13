package com.example.gcs.faster5.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.gcs.faster5.R;
import com.example.gcs.faster5.model.Room;
import com.example.gcs.faster5.model.User;
import com.example.gcs.faster5.util.PrefUtils;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Kien on 07/14/2016.
 */
public class GameOver extends AppCompatActivity {

    public static final String EXTRA_SCORE = "score";
    public static final String EXTRA_WINNER = "winner";

    ImageView mImageViewUserAvatar;
    TextView mTextViewNameUser;
    TextView mTextViewScore;
    TextView mTextViewResultText;
    TextView mTextViewCity;
    Button mButtonBack;
    private int mScore = 0;
    private int mTotalScore;
    private int mWinner;
    private String username;
    private String location;
    private String linkAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.game_over);
        findViewById();
        setUserInfo();
        getBundle();
    }

    public void findViewById() {
        mButtonBack = (Button) findViewById(R.id.button_backhome);
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/roboto.ttf");

        mTextViewResultText = (TextView) findViewById(R.id.textview_result);
        mImageViewUserAvatar = (ImageView) findViewById(R.id.imageview_useravatarwin);
        mTextViewNameUser = (TextView) findViewById(R.id.textview_usernnamewin);
        mTextViewScore = (TextView) findViewById(R.id.textview_money_win);
        mTextViewCity = (TextView) findViewById(R.id.textview_city_win);

        setTypeface(font, mTextViewNameUser, mTextViewResultText, mTextViewScore, mTextViewCity);
    }

    public void setUserInfo() {
        username = PrefUtils.getInstance(GameOver.this).get(PrefUtils.KEY_NAME, "");
        location = PrefUtils.getInstance(GameOver.this).get(PrefUtils.KEY_LOCATION, "");
        linkAvatar = PrefUtils.getInstance(GameOver.this).get(PrefUtils.KEY_URL_AVATAR, "");

        Log.e("TAG", "setUserInfo: " + username + location + linkAvatar);
        mTextViewNameUser.setText(username);
        mTextViewCity.setText(location);
        Glide.with(getApplicationContext()).load(linkAvatar).into(mImageViewUserAvatar);
    }

    public static void setTypeface(Typeface font, TextView... textviews) {
        for (TextView textView : textviews) {
            textView.setTypeface(font);
        }
    }


    public static Intent createIntent(Context context, int score, int isWinner) {
        Intent intent = new Intent(context, GameOver.class);
        intent.putExtra(EXTRA_SCORE, score);
        intent.putExtra(EXTRA_WINNER, isWinner);
        return intent;
    }

    private void getBundle() {
        mScore = getIntent().getIntExtra(EXTRA_SCORE, 0);
        mTextViewScore.setText(Integer.toString(mScore));
        mWinner = getIntent().getIntExtra(EXTRA_WINNER, 0);
        switch (mWinner) {
            case -1:
                mTextViewResultText.setText("BẤT PHÂN THẮNG BẠI");
                break;
            case 0:
                mTextViewResultText.setText("CHÚC BẠN MAY MẮN");
                break;
            case 1:
                mTextViewResultText.setText("CHÚC MỪNG CHIẾN THẮNG");
                break;
        }

        mTotalScore = PrefUtils.getInstance(GameOver.this).get(PrefUtils.KEY_TOTAL_SCORE, 0);
        mTotalScore += mScore;
        PrefUtils.getInstance(GameOver.this).set(PrefUtils.KEY_TOTAL_SCORE, mTotalScore);
    }

    public void backInfo(View view) {
        Intent intent = new Intent(getApplicationContext(), InfoScreen.class);
        startActivity(intent);
        overridePendingTransition(R.animator.in_from_left, R.animator.out_to_right);
        finish();
    }


    public void onBackPressed() {
        backInfo(getCurrentFocus());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
