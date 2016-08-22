package com.example.gcs.faster5.ui.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.gcs.faster5.R;

import com.example.gcs.faster5.util.PrefUtils;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;

/**
 * Created by Kien on 07/14/2016.
 */
public class GameOver extends AppCompatActivity {

    public static final String EXTRA_SCORE = "score";

    ImageView mImageViewUserAvatarWin;
    TextView mTextViewNameUserWin, mUserScoreWin;

    Button ButtonBack;
    Integer mScore = 0, mMoney;

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

        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/roboto.ttf");

        mImageViewUserAvatarWin = (ImageView) findViewById(R.id.imageview_useravatarwin);

        mTextViewNameUserWin = (TextView) findViewById(R.id.textview_usernnamewin);
        mTextViewNameUserWin.setTypeface(font);

        mUserScoreWin = (TextView) findViewById(R.id.textview_money_win);
        mUserScoreWin.setTypeface(font);

        SearchOpponent.questions.clear();

        buttonBackPressed();
        Score();
    }

    public void Score() {
        Bundle extrasName = getIntent().getExtras();
        if (extrasName != null) {
            mScore = extrasName.getInt(EXTRA_SCORE);
            mUserScoreWin.setText(Integer.toString(mScore));
        }
    }

    public void buttonBackPressed() {
        ButtonBack = (Button) findViewById(R.id.button_backhome);
        ButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), InfoScreen.class);
                mMoney = PrefUtils.getInstance(GameOver.this).get(PrefUtils.KEY_MONEY, 0);
                mMoney = mMoney + mScore;
                PrefUtils.getInstance(GameOver.this).set(PrefUtils.KEY_MONEY, mMoney);
                startActivity(intent);
                finish();
                overridePendingTransition(R.animator.in_from_left, R.animator.out_to_right);
            }
        });
    }

    public void onBackPressed() {
    }

}
