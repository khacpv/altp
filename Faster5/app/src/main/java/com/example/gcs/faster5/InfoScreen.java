package com.example.gcs.faster5;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kien on 07/05/2016.
 */
public class InfoScreen extends AppCompatActivity {

    public static String idUserFB, fullNameFb, nameManual;
    TextView userNameTxTV, favTopicTxTV, nameAppTxTV;
    Intent  logoutIntent;
    ImageView avatarfbImgV, logoutButtonImgV;
    ConnectivityManager connectivityManager;
    RelativeLayout backGround;
    AccessToken accessToken;
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

        Question.listQuestion = null;

        List<Topic> rowListItem = getAllItemList();
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(this, 3);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RecyclerViewAdapter(rowListItem);
        mRecyclerView.setAdapter(mAdapter);

        Typeface font = Typeface.createFromAsset(getAssets(),
                "fonts/dimboregular.ttf");
        backGround = (RelativeLayout) findViewById(R.id.BackGround);
        backGround.setBackgroundResource(R.drawable.background);

        userNameTxTV = (TextView) findViewById(R.id.userName);
        userNameTxTV.setTypeface(font);

        nameAppTxTV = (TextView) findViewById(R.id.faster5);
        nameAppTxTV.setText("FASTER5");
        nameAppTxTV.setTypeface(font);

        favTopicTxTV = (TextView) findViewById(R.id.favtopic);
        favTopicTxTV.setTypeface(font);

        avatarfbImgV = (ImageView) findViewById(R.id.avatarUser);
        FacebookSdk.sdkInitialize(getApplicationContext(), new FacebookSdk.InitializeCallback() {
                    @Override
                    public void onInitialized() {
                        //AccessToken is for us to check whether we have previously logged in into
                        //this app, and this information is save in shared preferences and sets it during SDK initialization
                        accessToken = AccessToken.getCurrentAccessToken();
                        if (accessToken == null) {

                            Bundle extrasName = getIntent().getExtras();
                            if (extrasName != null) {
                                nameManual = extrasName.getString("NAME");
                                userNameTxTV.setText(nameManual);

                            }
                        } else {
                            if (checkInternetConnection(InfoScreen.this)) {
                                GetUserInfo();
                            }
                        }
                    }
                }
        );

        logoutIntent = new Intent(InfoScreen.this, LoginScreen.class);
        logoutButtonImgV = (ImageView) findViewById(R.id.fblogout_button);
        logoutButtonImgV.setImageResource(R.drawable.logout);
        logoutButtonImgV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logOut();
                startActivity(logoutIntent);
                finish();
            }
        });
    }

    public boolean checkInternetConnection(Context context) {
        connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getActiveNetworkInfo() != null
                && connectivityManager.getActiveNetworkInfo().isAvailable()
                && connectivityManager.getActiveNetworkInfo().isConnected()) {
            return true;
        } else {
            return false;
        }
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
                            idUserFB = object.getString("id");
                            if (idUserFB == null) {
                                avatarfbImgV.setImageResource(R.drawable.avatar);
                            } else {
                                fullNameFb = object.getString("name");
                                userNameTxTV.setText(fullNameFb);
                                Glide.with(getApplicationContext()).load("https://graph.facebook.com/" + idUserFB + "/picture?width=500&height=500").into(avatarfbImgV);
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


    private List<Topic> getAllItemList() {

        List<Topic> allItems = new ArrayList<Topic>();
        allItems.add(new Topic(1, "FootBall", R.drawable.football));
        allItems.add(new Topic(2, "Art", R.drawable.art));
        allItems.add(new Topic(3, "Basic Math", R.drawable.math));
        allItems.add(new Topic(4, "Fruits", R.drawable.fruit));
        allItems.add(new Topic(5, "Music", R.drawable.music));
        allItems.add(new Topic(6, "Technology", R.drawable.tech));
        allItems.add(new Topic(0, "Lock", R.drawable.lock));

        return allItems;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void onBackPressed() {
    }
}
