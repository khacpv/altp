package com.example.gcs.faster5.ui.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.gcs.faster5.R;
import com.example.gcs.faster5.logic.QuestionMng;
import com.example.gcs.faster5.logic.TopicMng;
import com.example.gcs.faster5.model.Topic;
import com.example.gcs.faster5.ui.adapter.TopicAdapter;
import com.example.gcs.faster5.util.NetworkUtils;
import com.example.gcs.faster5.util.PrefUtils;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Kien on 07/05/2016.
 */
public class InfoScreen extends AppCompatActivity {
    public static String sUserFbId, sFullNameFb, sManualName;
    public static int sGold;
    TextView mTextViewNameUser, mTextViewTopicFav, mTextViewAppName, mTextViewGold;
    ImageView mImageViewFbAvatar, logoutButtonImgV;
    RelativeLayout mRelativeLayoutBg;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.info_screen);

        QuestionMng.listQuestion = null;

        List<Topic> rowListItem = TopicMng.getAllItemList();
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(this, 3);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new TopicAdapter(rowListItem);
        mRecyclerView.setAdapter(mAdapter);

        Typeface font = Typeface.createFromAsset(getAssets(),
                "fonts/dimboregular.ttf");
        mRelativeLayoutBg = (RelativeLayout) findViewById(R.id.background);
        mRelativeLayoutBg.setBackgroundResource(R.drawable.background);

        mTextViewNameUser = (TextView) findViewById(R.id.text_username);
        mTextViewNameUser.setTypeface(font);

        mTextViewAppName = (TextView) findViewById(R.id.text_appname);
        mTextViewAppName.setText("FASTER5");
        mTextViewAppName.setTypeface(font);

        mTextViewTopicFav = (TextView) findViewById(R.id.text_topicfav);
        mTextViewTopicFav.setTypeface(font);

        mImageViewFbAvatar = (ImageView) findViewById(R.id.image_useravatar);

        mTextViewGold = (TextView) findViewById(R.id.text_gold);
        mTextViewGold.setTypeface(font);
        sGold = PrefUtils.getInstance(this).get(PrefUtils.KEY_GOLD,0);
        mTextViewGold.setText(Integer.toString(sGold));

        FacebookSdk.sdkInitialize(getApplicationContext(), new FacebookSdk.InitializeCallback() {
                    @Override
                    public void onInitialized() {
                        //AccessToken is for us to check whether we have previously logged in into
                        //this app, and this information is save in shared preferences and sets it during SDK initialization
                        AccessToken accessToken = AccessToken.getCurrentAccessToken();
                        if (accessToken == null) {
                            sManualName = PrefUtils.getInstance(InfoScreen.this).get(PrefUtils.KEY_NAME,"");
                            mTextViewNameUser.setText(sManualName);
                        } else {
                            if (NetworkUtils.checkInternetConnection(InfoScreen.this)) {
                                GetUserInfo();
                            }
                        }
                    }
                }
        );
        logoutButtonImgV = (ImageView) findViewById(R.id.fblogout_button);
        logoutButtonImgV.setImageResource(R.drawable.logout);
        logoutButtonImgV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logOut();
                Intent intent = new Intent(InfoScreen.this, LoginScreen.class);
                startActivity(intent);
                finish();
            }
        });
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
        super.onResume();
    }

    public void onBackPressed() {
    }
}
