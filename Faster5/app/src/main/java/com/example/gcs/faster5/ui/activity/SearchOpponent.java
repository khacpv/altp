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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.gcs.faster5.MainApplication;
import com.example.gcs.faster5.R;
import com.example.gcs.faster5.model.Question;
import com.example.gcs.faster5.model.Room;
import com.example.gcs.faster5.model.User;
import com.example.gcs.faster5.sock.AltpHelper;
import com.example.gcs.faster5.sock.SockAltp;
import com.example.gcs.faster5.util.NetworkUtils;
import com.example.gcs.faster5.util.PrefUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.Socket;

/**
 * Created by Kien on 07/12/2016.
 */
public class SearchOpponent extends AppCompatActivity {

    public static final String EXTRA_ID = "topic_id";
    public static final String EXTRA_NAME = "topic_name";
    public static final String EXTRA_ANSWER_RIGHT = "right_answer";
    public static List<Question> questions = new ArrayList<>();
    private SockAltp mSocketAltp;
    private AltpHelper mAltpHelper;
    private User mUser = new User();
    private User enemyUser = new User();
    private Room mRoom = new Room();

    TextView mTextViewCityUser1, mTextViewCityUser2, mTextViewUserName1, mTextViewUserName2, mTextViewMoney1, mTextViewMoney2;
    ImageView mImageViewUserAvatar1, mImageViewUserAvatar2;
    public static Button mButtonPlay, mButtonSeach;

    private SockAltp.OnSocketEvent globalCallback = new SockAltp.OnSocketEvent() {
        @Override
        public void onEvent(String event, Object... args) {
            switch (event) {
                case Socket.EVENT_CONNECT:  // auto call on connect to server
                    Log.e("TAG", "connect");
                    break;
                case Socket.EVENT_CONNECT_ERROR:
                case Socket.EVENT_CONNECT_TIMEOUT:
                    Log.e("TAG", "disconnect");
                    if (!mSocketAltp.isConnected()) {
                        mSocketAltp.connect();
                    }
                    break;
            }
        }
    };

    private SockAltp.OnSocketEvent playCallback = new SockAltp.OnSocketEvent() {
        @Override
        public void onEvent(String event, Object... args) {
            Question question = mAltpHelper.playCallback(args);
            JSONObject data = (JSONObject) args[0];
            boolean ready = data.optBoolean("notReady");
            if (ready) {
                Log.e("TAG", "onEvent: " + "Ready");
            } else {
                Log.e("TAG", "onEvent: " + "Not Ready");
            }
        }
    };


    public void play(User user, Room room) {
        this.mUser = user;
        this.mRoom = room;
        mAltpHelper.play(mUser, mRoom);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.search_opponent);

        mSocketAltp = MainApplication.sockAltp();
        mAltpHelper = new AltpHelper(mSocketAltp);
        if (!mSocketAltp.isConnected()) {
            mSocketAltp.connect();
        }

        mSocketAltp.addEvent("play", playCallback);
        mSocketAltp.addGlobalEvent(globalCallback);
        findViewById();
        setInfoUser();

    }

    public void setInfoUser() {
        mTextViewUserName1.setText(PrefUtils.getInstance(SearchOpponent.this).get(PrefUtils.KEY_NAME, ""));
        Glide.with(getApplicationContext()).load(PrefUtils.getInstance(SearchOpponent.this).get(PrefUtils.KEY_URL_AVATAR, ""))
                .into(mImageViewUserAvatar1);
        mTextViewCityUser1.setText(PrefUtils.getInstance(SearchOpponent.this).get(PrefUtils.KEY_LOCATION, ""));


        mTextViewUserName2.setText(PrefUtils.getInstance(SearchOpponent.this).get(PrefUtils.KEY_ENEMY_NAME, ""));
        Glide.with(getApplicationContext()).load(PrefUtils.getInstance(SearchOpponent.this).get(PrefUtils.KEY_ENEMY_AVATAR, ""))
                .into(mImageViewUserAvatar2);
        mTextViewCityUser2.setText(PrefUtils.getInstance(SearchOpponent.this).get(PrefUtils.KEY_ENEMY_LOCATION, ""));

    }

    public void findViewById() {

        Typeface font = Typeface.createFromAsset(getAssets(),
                "fonts/roboto.ttf");

        mTextViewUserName1 = (TextView) findViewById(R.id.textview_username1);
        mTextViewUserName1.setTypeface(font);

        mImageViewUserAvatar1 = (ImageView) findViewById(R.id.imageview_useravatar1);

        mTextViewCityUser1 = (TextView) findViewById(R.id.textview_city_user1);
        mTextViewCityUser1.setTypeface(font);

        mTextViewMoney1 = (TextView) findViewById(R.id.textview_money1);
        mTextViewMoney1.setTypeface(font);

        mTextViewUserName2 = (TextView) findViewById(R.id.textview_username2);
        mTextViewUserName2.setTypeface(font);

        mImageViewUserAvatar2 = (ImageView) findViewById(R.id.imageview_useravatar2);

        mTextViewCityUser2 = (TextView) findViewById(R.id.textview_city_user2);
        mTextViewCityUser2.setTypeface(font);

        mTextViewMoney2 = (TextView) findViewById(R.id.textview_money2);
        mTextViewMoney2.setTypeface(font);

        mButtonSeach = (Button) findViewById(R.id.button_search_again);
        mButtonPlay = (Button) findViewById(R.id.button_play);
    }


    public void btnSearch(View view) {
        Intent intent = new Intent(SearchOpponent.this, InfoScreen.class);
        startActivity(intent);
        overridePendingTransition(R.animator.in_from_left, R.animator.out_to_right);
        finish();
    }

    public void btnPlay(View view) {
        mUser.name = PrefUtils.getInstance(SearchOpponent.this).get(PrefUtils.KEY_NAME, "");
        mUser.address = PrefUtils.getInstance(SearchOpponent.this).get(PrefUtils.KEY_LOCATION, "");
        mUser.avatar = PrefUtils.getInstance(SearchOpponent.this).get(PrefUtils.KEY_URL_AVATAR, "");
        mUser.id = PrefUtils.getInstance(SearchOpponent.this).get(PrefUtils.KEY_USER_ID, Long.valueOf(0));

        // enemyUser.name = PrefUtils.getInstance(SearchOpponent.this).get(PrefUtils.KEY_ENEMY_NAME, "");
        //enemyUser.address = PrefUtils.getInstance(SearchOpponent.this).get(PrefUtils.KEY_ENEMY_LOCATION, "");
        //enemyUser.avatar = PrefUtils.getInstance(SearchOpponent.this).get(PrefUtils.KEY_ENEMY_AVATAR, "");

//        List<User> listUser = new ArrayList<>();
//        listUser.add(mUser);
//        listUser.add(enemyUser);
//
//        mRoom.users = listUser;
        mRoom.roomId = PrefUtils.getInstance(SearchOpponent.this).get(PrefUtils.KEY_ROOM_ID, "");

        play(mUser, mRoom);

    }

    public void moveMainScreen() {
        Intent intent = new Intent(SearchOpponent.this, MainScreen.class);
        startActivity(intent);
        overridePendingTransition(R.animator.right_in, R.animator.left_out);
        finish();
    }


    @Override
    public void onResume() {
        if (!NetworkUtils.checkInternetConnection(this)) {
            NetworkUtils.movePopupConnection(this);
        }
        super.onResume();
    }
}
