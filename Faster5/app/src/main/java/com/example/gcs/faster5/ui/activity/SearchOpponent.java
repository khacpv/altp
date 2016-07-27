package com.example.gcs.faster5.ui.activity;

import android.content.Intent;
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
import com.example.gcs.faster5.R;
import com.example.gcs.faster5.model.Question;
import com.example.gcs.faster5.network.ServiceMng;
import com.example.gcs.faster5.util.PrefUtils;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Kien on 07/12/2016.
 */
public class SearchOpponent extends AppCompatActivity {

    public static final String EXTRA_ID = "topic_id";
    public static final String EXTRA_NAME = "topic_name";
    public static final String EXTRA_ANSWER_RIGHT = "right_answer";
    public static List<Question> questions;
    RelativeLayout mRelativeLayoutBg;
    TextView mTextViewTopicName, mTextViewUserName1, mTextViewUserName2, mTextViewGold1, mTextViewGold2;
    ImageView mImageViewUserAvatar1, mImageViewUserAvatar2;
    public static ImageButton mImageButtonPlay;
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

        getQuestion();

        Bundle extrasName = getIntent().getExtras();
        if (extrasName != null) {
            mTopicId = extrasName.getInt(EXTRA_ID);
            mTopicName = extrasName.getString(EXTRA_NAME);
        }

        Typeface font = Typeface.createFromAsset(getAssets(),
                "fonts/dimboregular.ttf");

        mRelativeLayoutBg = (RelativeLayout) findViewById(R.id.background);
        mRelativeLayoutBg.setBackgroundResource(R.drawable.background);

        mImageButtonPlay = (ImageButton) findViewById(R.id.button_play);
        mImageButtonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainScreen.class);
                intent.putExtra(EXTRA_ANSWER_RIGHT, mTopicId);
                startActivity(intent);
                overridePendingTransition(R.animator.right_in, R.animator.left_out);
                finish();
            }
        });

        mImageViewUserAvatar1 = (ImageView) findViewById(R.id.image_useravatar1);
        mImageViewUserAvatar2 = (ImageView) findViewById(R.id.image_useravatar2);

        mTextViewUserName1 = (TextView) findViewById(R.id.text_username1);
        mTextViewUserName2 = (TextView) findViewById(R.id.text_username2);
        mTextViewUserName1.setTypeface(font);
        mTextViewUserName2.setTypeface(font);

        mTextViewGold1 = (TextView) findViewById(R.id.text_gold1);
        mTextViewGold1.setTypeface(font);
        mTextViewGold1.setText(String.valueOf(PrefUtils.getInstance(this).get(PrefUtils.KEY_GOLD, 0)));

        mTextViewTopicName = (TextView) findViewById(R.id.text_topicname);
        mTextViewTopicName.setTypeface(font);
        mTextViewTopicName.setText(mTopicName);

        setInfo();
    }

    public void setInfo() {
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
    }

    public static void getQuestion() {
        new ServiceMng().api().getQuestion(0).enqueue(new Callback<List<Question>>() {
            @Override
            public void onResponse(Call<List<Question>> call, Response<List<Question>> response) {
                questions = response.body();
                mImageButtonPlay.setImageResource(R.drawable.playbutton);
            }

            @Override
            public void onFailure(Call<List<Question>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

}
