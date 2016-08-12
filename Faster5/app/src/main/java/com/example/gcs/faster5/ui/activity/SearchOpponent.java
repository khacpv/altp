package com.example.gcs.faster5.ui.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.example.gcs.faster5.model.Question;
import com.example.gcs.faster5.network.ServiceMng;
import com.example.gcs.faster5.util.NetworkUtils;
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
    String username2;
    TextView mTextViewCityUser1, mTextViewCityUser2, mTextViewUserName1, mTextViewUserName2, mTextViewMoney1, mTextViewMoney2;
    ImageView mImageViewUserAvatar1, mImageViewUserAvatar2;
    public static RelativeLayout mRelativeLayoutPlay;
    public static Button mButtonPlay, mButtonSeach;
    String URL;
    int idUser2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.search_opponent);

        mRelativeLayoutPlay = (RelativeLayout) findViewById(R.id.relative_layout_play);
        mRelativeLayoutPlay.setVisibility(View.GONE);

        Typeface font = Typeface.createFromAsset(getAssets(),
                "fonts/roboto.ttf");
        getQuestion();

        mTextViewCityUser1 = (TextView) findViewById(R.id.textview_city_user1);
        mTextViewCityUser2 = (TextView) findViewById(R.id.textview_city_user2);
        mTextViewCityUser1.setTypeface(font);
        mTextViewCityUser2.setTypeface(font);
        if (mTextViewCityUser1 != null) {
            mTextViewCityUser1.setText(LoginScreen.city.toUpperCase());
        } else {
            mTextViewCityUser1.setText("VIETNAM");
        }
        mTextViewCityUser2.setText("VIETNAM");

        mButtonSeach = (Button) findViewById(R.id.button_search_again);
        mButtonSeach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), InfoScreen.class);
                startActivity(intent);
                overridePendingTransition(R.animator.in_from_left, R.animator.out_to_right);
                finish();
            }
        });


        mImageViewUserAvatar1 = (ImageView) findViewById(R.id.imageview_useravatar1);
        mImageViewUserAvatar2 = (ImageView) findViewById(R.id.imageview_useravatar2);

        mTextViewUserName1 = (TextView) findViewById(R.id.textview_username1);
        mTextViewUserName2 = (TextView) findViewById(R.id.textview_username2);
        mTextViewUserName1.setTypeface(font);
        mTextViewUserName2.setTypeface(font);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username2 = extras.getString("NAMEUSER2");
            idUser2 = extras.getInt("IDUSER2");
            linkAvatarUser2(idUser2);
            mTextViewUserName2.setText(username2);
        }

        mTextViewMoney1 = (TextView) findViewById(R.id.textview_money1);
        mTextViewMoney1.setTypeface(font);
        mTextViewMoney1.setText(String.valueOf(PrefUtils.getInstance(this).get(PrefUtils.KEY_MONEY, 0)));
        mTextViewMoney2 = (TextView) findViewById(R.id.textview_money2);
        mTextViewMoney2.setTypeface(font);
        mTextViewMoney2.setText(String.valueOf(PrefUtils.getInstance(this).get(PrefUtils.KEY_MONEY, 0)));
        mButtonPlay = (Button) findViewById(R.id.button_play);
        mButtonPlay.setTypeface(font);
        mButtonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainScreen.class);
                intent.putExtra("NAMEUSER2", username2);
                intent.putExtra("IDUSER2", idUser2);
                startActivity(intent);
                overridePendingTransition(R.animator.right_in, R.animator.left_out);
                finish();
            }
        });

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
                mRelativeLayoutPlay.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<List<Question>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void linkAvatarUser2(int x) {
        switch (x) {
            case 0:
                URL = "http://img.saobiz.net/d/2016/05/ngoc-trinh-giaoduc999-01084727_03.jpg";
                Glide.with(getApplicationContext())
                        .load(URL).into(mImageViewUserAvatar2);
                break;
            case 1:
                URL = "http://media.doisongphapluat.com/2015/07/27/angela_2_dspl.jpg";
                Glide.with(getApplicationContext())
                        .load(URL).into(mImageViewUserAvatar2);
                break;
            case 2:
                URL = "http://congly.com.vn/data/news/2016/3/8/83/hoahaukyduyen.jpg";
                Glide.with(getApplicationContext())
                        .load(URL).into(mImageViewUserAvatar2);
                break;
            case 3:
                URL = "http://media.hotbirthdays.com/upload/2015/05/24/nguyen-ngoc-ngan.jpg";
                Glide.with(getApplicationContext())
                        .load(URL).into(mImageViewUserAvatar2);
                break;
            case 4:
                URL = "http://img.saobiz.net/d/2015/10/nhung2-1443749932505-56-0-362-600-crop-1443750244536.jpg";
                Glide.with(getApplicationContext())
                        .load(URL).into(mImageViewUserAvatar2);
                break;
            case 5:
                URL = "http://www.phunuvagiadinh.vn/uploads/2016/03/24/1385548239736_500-20160324-00031574.jpg";
                Glide.with(getApplicationContext())
                        .load(URL).into(mImageViewUserAvatar2);
                break;
            case 6:
                URL = "http://media.tinmoi.vn/2015/07/23/ho-ngoc-ha-tm1.jpg";
                Glide.with(getApplicationContext())
                        .load(URL).into(mImageViewUserAvatar2);
                break;
            case 7:
                URL = "http://phunutoday.vn/upload_images/images/2016/07/20/lai-van-sam-phunutoday_vn.jpg";
                Glide.with(getApplicationContext())
                        .load(URL).into(mImageViewUserAvatar2);
                break;
        }

    }

    @Override
    public void onResume() {
        if (!NetworkUtils.checkInternetConnection(this)) {
            NetworkUtils.movePopupConnection(this);
        }
        super.onResume();
    }
}
