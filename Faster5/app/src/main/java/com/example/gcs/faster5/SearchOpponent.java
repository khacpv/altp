package com.example.gcs.faster5;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;

/**
 * Created by Kien on 07/12/2016.
 */
public class SearchOpponent extends AppCompatActivity {

    RelativeLayout mRelativeLayoutBg;
    TextView mTextViewTopicName, mTextViewUserName1, mTextViewUserName2, mTextViewGold1, mTextViewGold2;
    ImageView mImageViewUserAvatar1, mImageViewUserAvatar2;
    ImageButton mImageButtonPlay;
    int mTopicId;
    String mTopicName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.search_opponent);
        SharedPreferences prefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        Bundle extrasName = getIntent().getExtras();
        if (extrasName != null) {
            mTopicId = extrasName.getInt("IDTOPIC");
            mTopicName = extrasName.getString("NAMETOPIC");
        }

        Typeface font = Typeface.createFromAsset(getAssets(),
                "fonts/dimboregular.ttf");

        mRelativeLayoutBg = (RelativeLayout) findViewById(R.id.background);
        mRelativeLayoutBg.setBackgroundResource(R.drawable.background);

        mImageButtonPlay = (ImageButton) findViewById(R.id.button_play);
        mImageButtonPlay.setImageResource(R.drawable.playbutton);

        mImageViewUserAvatar1 = (ImageView) findViewById(R.id.image_useravatar1);
        mImageViewUserAvatar2 = (ImageView) findViewById(R.id.image_useravatar2);

        mTextViewUserName1 = (TextView) findViewById(R.id.text_username1);
        mTextViewUserName2 = (TextView) findViewById(R.id.text_username2);
        mTextViewUserName1.setTypeface(font);
        mTextViewUserName2.setTypeface(font);

        mTextViewGold1 = (TextView) findViewById(R.id.text_gold1);
        mTextViewGold1.setTypeface(font);
        mTextViewGold1.setText(Integer.toString(prefs.getInt("Gold", 0)));

        mTextViewTopicName = (TextView) findViewById(R.id.text_topicname);
        mTextViewTopicName.setTypeface(font);
        mTextViewTopicName.setText(mTopicName);

        FacebookSdk.sdkInitialize(getApplicationContext(), new FacebookSdk.InitializeCallback() {
            @Override
            public void onInitialized() {

                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                if (accessToken != null) {
                    mTextViewUserName1.setText(InfoScreen.sFullNameFb);
                    Glide.with(getApplicationContext())
                            .load("https://graph.facebook.com/" + InfoScreen.sUserFbId + "/picture?width=500&height=500").into(mImageViewUserAvatar1);
                } else {
                    mTextViewUserName1.setText(InfoScreen.sManualName);
                    mImageViewUserAvatar1.setImageResource(R.drawable.avatar);
                }
            }
        });

        mImageButtonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainScreen.class);
                intent.putExtra("IDTOPIC", mTopicId);
                startActivity(intent);
                finish();
            }
        });

    }
}
