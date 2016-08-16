package com.example.gcs.faster5.ui.activity;


import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.gcs.faster5.R;
import com.example.gcs.faster5.ui.widget.HexagonDrawable;
import com.example.gcs.faster5.util.NetworkUtils;
import com.example.gcs.faster5.util.PrefUtils;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Kien on 07/05/2016.
 */
public class InfoScreen extends AppCompatActivity {
    public static String sUserFbId, sFullNameFb, sManualName;
    public static int sMoney;
    TextView mTextViewNameUser, mTextViewMoney, mTextViewCity,
            mTextViewPlayer1, mTextViewPlayer2, mTextViewPlayer3, mTextViewPlayer4,
            mTextViewPlayer5, mTextViewPlayer6, mTextViewPlayer7, mTextViewPlayer8;
    ImageView mImageViewFbAvatar, logoutButtonImgV;
    Button[] mButtonPlayer;
    RelativeLayout mButtonSearch;
    Intent mIntentSearchOpponent;
    List<String> names;
    int x;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if(getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.info_screen);

        /**
         * RecyclerView
         */
        /*List<Topic> rowListItem = TopicMng.getAllItemList();
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(this, 3);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new TopicAdapter(rowListItem);
        mRecyclerView.setAdapter(mAdapter);*/

        Typeface font = Typeface.createFromAsset(getAssets(),
                "fonts/roboto.ttf");

        mTextViewNameUser = (TextView) findViewById(R.id.textview_usernname);
        mTextViewNameUser.setTypeface(font);

        mTextViewCity = (TextView) findViewById(R.id.textview_city_info);
        mTextViewCity.setTypeface(font);

        if (LoginScreen.city != null) {
            mTextViewCity.setText(LoginScreen.city.toUpperCase());
        } else {
            mTextViewCity.setText("VIETNAM");
        }

        mImageViewFbAvatar = (ImageView) findViewById(R.id.imageview_useravatar);

        mTextViewMoney = (TextView) findViewById(R.id.textview_money);
        mTextViewMoney.setTypeface(font);
        sMoney = PrefUtils.getInstance(this).get(PrefUtils.KEY_MONEY, 0);
        mTextViewMoney.setText(Integer.toString(sMoney));

        mButtonPlayer = new Button[8];
        mTextViewPlayer1 = (TextView) findViewById(R.id.button_player1).findViewById(R.id.button_player);
        mTextViewPlayer2 = (TextView) findViewById(R.id.button_player2).findViewById(R.id.button_player);
        mTextViewPlayer3 = (TextView) findViewById(R.id.button_player3).findViewById(R.id.button_player);
        mTextViewPlayer4 = (TextView) findViewById(R.id.button_player4).findViewById(R.id.button_player);
        mTextViewPlayer5 = (TextView) findViewById(R.id.button_player5).findViewById(R.id.button_player);
        mTextViewPlayer6 = (TextView) findViewById(R.id.button_player6).findViewById(R.id.button_player);
        mTextViewPlayer7 = (TextView) findViewById(R.id.button_player7).findViewById(R.id.button_player);
        mTextViewPlayer8 = (TextView) findViewById(R.id.button_player8).findViewById(R.id.button_player);

        for (int i = 0; i < 8; i++) {
            if (i == 0) {
                mButtonPlayer[i] = (Button) mTextViewPlayer1;
            }
            if (i == 1) {
                mButtonPlayer[i] = (Button) mTextViewPlayer2;
            }
            if (i == 2) {
                mButtonPlayer[i] = (Button) mTextViewPlayer3;
            }
            if (i == 3) {
                mButtonPlayer[i] = (Button) mTextViewPlayer4;
            }
            if (i == 4) {
                mButtonPlayer[i] = (Button) mTextViewPlayer5;
            }
            if (i == 5) {
                mButtonPlayer[i] = (Button) mTextViewPlayer6;
            }
            if (i == 6) {
                mButtonPlayer[i] = (Button) mTextViewPlayer7;
            }
            if (i == 7) {
                mButtonPlayer[i] = (Button) mTextViewPlayer8;
            }
            mButtonPlayer[i].setBackgroundResource(R.drawable.button_player);
        }

        Random random = new Random();
        x = random.nextInt(8 + 0);


        mButtonSearch = (RelativeLayout) findViewById(R.id.button_search);
        final HexagonDrawable searchBg = new HexagonDrawable();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mButtonSearch.setBackground(searchBg);
        }else{
            mButtonSearch.setBackgroundDrawable(new HexagonDrawable());
        }
        mButtonSearch.setClickable(true);
        mButtonSearch.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View v) {
                                                 searchBg.start();
                                                 mButtonSearch.setClickable(false);
                                                 names = new ArrayList<String>();
                                                 names.add("Ngọc Trinh");
                                                 names.add("Angela Phương Trinh");
                                                 names.add("Kỳ Duyên");
                                                 names.add("Nguyễn Ngọc Ngạn");
                                                 names.add("Phi Nhung");
                                                 names.add("Mạnh Quỳnh");
                                                 names.add("Hồ Ngọc Hà");
                                                 names.add("Lại Văn Sâm");
                                                 for (int i = 0; i < names.size(); i++) {
                                                     mButtonPlayer[i].setText(names.get(i));

                                                 }
                                                 final CountDownTimer mNext = new CountDownTimer(2000, 100) {

                                                     @Override
                                                     public void onTick(long l) {

                                                     }

                                                     @Override
                                                     public void onFinish() {
                                                         mIntentSearchOpponent = new Intent(InfoScreen.this, SearchOpponent.class);
                                                         mIntentSearchOpponent.putExtra("NAMEUSER2", names.get(x));
                                                         mIntentSearchOpponent.putExtra("IDUSER2", x);
                                                         startActivity(mIntentSearchOpponent);
                                                         overridePendingTransition(R.animator.right_in, R.animator.left_out);
                                                         finish();
                                                     }
                                                 };

                                                 CountDownTimer mWaitTime = new CountDownTimer(2000, 100) {
                                                     @Override
                                                     public void onTick(long l) {
                                                     }

                                                     @Override
                                                     public void onFinish() {
                                                         searchBg.stop();
                                                         mButtonPlayer[x].setBackgroundResource(R.drawable.answer3);
                                                         mNext.start();
                                                     }
                                                 };
                                                 mWaitTime.start();


                                             }
                                         }
        );

        FacebookSdk.sdkInitialize(
                getApplicationContext(),
                new FacebookSdk.InitializeCallback() {
                    @Override
                    public void onInitialized() {
                        //AccessToken is for us to check whether we have previously logged in into
                        //this app, and this information is save in shared preferences and sets it during SDK initialization
                        AccessToken accessToken = AccessToken.getCurrentAccessToken();
                        if (accessToken == null) {
                            sManualName = PrefUtils.getInstance(InfoScreen.this).get(PrefUtils.KEY_NAME, "");
                            mTextViewNameUser.setText(sManualName);
                        } else {
                            if (NetworkUtils.checkInternetConnection(InfoScreen.this)) {
                                GetUserInfo();
                            }
                        }
                    }
                }
        );
       /* logoutButtonImgV = (ImageView) findViewById(R.id.fblogout_button);
        logoutButtonImgV.setImageResource(R.drawable.logout);
        logoutButtonImgV.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    LoginManager.getInstance().logOut();
                                                    Intent intent = new Intent(InfoScreen.this, LoginScreen.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            }
        );
    }*/
    }

    private void GetUserInfo() {
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        // Application code
                        try {
                            sUserFbId = object.getString("id");
                            if (sUserFbId == null) {
                                mImageViewFbAvatar.setImageResource(R.drawable.avatar);
                            } else {
                                sFullNameFb = object.getString("name");
                                mTextViewNameUser.setText(sFullNameFb);
                                Glide.with(getApplicationContext())
                                        .load("https://graph.facebook.com/" + sUserFbId + "/picture?width=500&height=500").into(mImageViewFbAvatar);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,gender,name,birthday,picture.type(large)");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    public void onResume() {
        if (!NetworkUtils.checkInternetConnection(this)) {
            NetworkUtils.movePopupConnection(this);
        }
        super.onResume();
    }
}
