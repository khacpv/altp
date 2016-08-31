package com.example.gcs.faster5.ui.activity;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import com.example.gcs.faster5.util.SoundPoolManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import io.socket.client.Socket;

/**
 * Created by Kien on 07/12/2016.
 */
public class SearchOpponent extends AppCompatActivity {

    private static final String EXTRA_USER = "user";
    private static final String EXTRA_ENEMY = "enemy";
    private static final String EXTRA_ROOM = "room";

    private SockAltp mSocketAltp;
    private AltpHelper mAltpHelper;
    private User mUser = new User();
    private User enemyUser = new User();
    private Room mRoom = new Room();
    private Dialog waitDialog;
    Intent intent;
    private TextView mTextViewCityUser1;
    private TextView mTextViewCityUser2;
    private TextView mTextViewUserName1;
    private TextView mTextViewUserName2;
    private TextView mTextViewMoney1;
    private TextView mTextViewMoney2;
    private ImageView mImageViewUserAvatar1;
    private ImageView mImageViewUserAvatar2;
    Button mButtonPlay;
    Button mButtonSeach;
    Handler handler = new Handler();

    private SockAltp.OnSocketEvent globalCallback = new SockAltp.OnSocketEvent() {
        @Override
        public void onEvent(String event, Object... args) {
            switch (event) {
                case Socket.EVENT_CONNECTING:
                    Log.e("TAG_SeaOppo", "connecting");
                    break;
                case Socket.EVENT_CONNECT:  // auto call on connect to server
                    Log.e("TAG_SeaOppo", "connect");
                    break;
                case Socket.EVENT_CONNECT_ERROR:
                    Log.e("TAG_SeaOppo", "error");
                    break;
                case Socket.EVENT_CONNECT_TIMEOUT:
                    Log.e("TAG_SeaOppo", "timeout");
                    break;
            }
        }
    };

    private SockAltp.OnSocketEvent playCallback = new SockAltp.OnSocketEvent() {
        @Override
        public void onEvent(String event, Object... args) {
            OnPlayCallbackEvent eventBus = new OnPlayCallbackEvent();

            boolean notReady = mAltpHelper.playCallbackReady(args);

            int count = mAltpHelper.playCallbackCount(args);

            Question mQuestion = mAltpHelper.playCallbackQuestion(args);

            eventBus.notReady = notReady;
            eventBus.mQuestion = mQuestion;
            EventBus.getDefault().post(eventBus);

        }
    };

    public static class OnPlayCallbackEvent {
        boolean notReady;
        Question mQuestion;
    }

    public static Intent createIntent(Context context, User user, User enemy, Room room) {
        Intent intent = new Intent(context, SearchOpponent.class);
        intent.putExtra(EXTRA_USER, user);
        intent.putExtra(EXTRA_ENEMY, enemy);
        intent.putExtra(EXTRA_ROOM, room);
        return intent;
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
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        SoundPoolManager.getInstance().playSound(R.raw.search_opponent);
        getBundle();
        EventBus.getDefault().register(this);
        mSocketAltp = MainApplication.sockAltp();
        mAltpHelper = new AltpHelper(mSocketAltp);
        if (!mSocketAltp.isConnected()) {
            mSocketAltp.connect();
        }
        mSocketAltp.addEvent("play", playCallback);
        mSocketAltp.addGlobalEvent(globalCallback);
        findViewById();
        popupLogin();
        setInfoUser();

    }


    @Subscribe
    public void onEventMainThread(OnPlayCallbackEvent event) {
        boolean notReady = event.notReady;

        if (notReady) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!isFinishing()) {
                        waitDialog.show();
                    }
                }
            });
            Log.e("TAG", "waiting for other players ready.");
            return;
        }

        Question mQuestion = event.mQuestion;
        final Intent playScrnIntent = PlayScreen.createIntent(SearchOpponent.this, mUser, enemyUser, mRoom, mQuestion);
        SoundPoolManager.getInstance().playSound(R.raw.ready);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                startActivity(playScrnIntent);
                overridePendingTransition(R.animator.right_in, R.animator.left_out);
                finish();
            }
        }, 5000);

    }

    /**
     * get data from previous activity
     */
    private void getBundle() {
        mUser = (User) getIntent().getSerializableExtra(EXTRA_USER);
        enemyUser = (User) getIntent().getSerializableExtra(EXTRA_ENEMY);
        mRoom = (Room) getIntent().getSerializableExtra(EXTRA_ROOM);
    }

    public void setInfoUser() {
        // my info
        mTextViewUserName1.setText(mUser.name);
        Glide.with(getApplicationContext()).load(mUser.avatar).into(mImageViewUserAvatar1);
        mTextViewCityUser1.setText(mUser.address);

        // enemy user
        mTextViewUserName2.setText(enemyUser.name);
        Glide.with(getApplicationContext()).load(enemyUser.avatar).into(mImageViewUserAvatar2);
        mTextViewCityUser2.setText(enemyUser.address);

    }

    public void findViewById() {
        intent = new Intent(SearchOpponent.this, PlayScreen.class);
        waitDialog = new Dialog(this);

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

    public void popupLogin() {
        waitDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        waitDialog.setContentView(R.layout.layout_wait_popup);
        waitDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        waitDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        waitDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        ImageView loading = (ImageView) waitDialog.findViewById(R.id.imgView_loading);

        Glide.with(this).load(R.drawable.loading).asGif().into(loading);

        Button btnCancel = (Button) waitDialog.findViewById(R.id.button_cancel);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SoundPoolManager.getInstance().playSound(R.raw.touch_sound);
                waitDialog.hide();
            }
        });

    }

    public void btnSearch(View view) {
        SoundPoolManager.getInstance().playSound(R.raw.touch_sound);
        SoundPoolManager.getInstance().stop();
        Intent intent = new Intent(SearchOpponent.this, InfoScreen.class);
        startActivity(intent);
        overridePendingTransition(R.animator.in_from_left, R.animator.out_to_right);
        finish();
    }

    @Override
    public void onBackPressed() {
        btnSearch(getCurrentFocus());
    }

    public void btnPlay(View view) {
        SoundPoolManager.getInstance().playSound(R.raw.touch_sound);
        waitDialog.show();
        mAltpHelper.play(mUser, mRoom);
    }


    @Override
    protected void onDestroy() {
        if (waitDialog != null) {
            waitDialog.dismiss();
        }
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        super.onDestroy();
    }

    @Override
    public void onResume() {
        if (!NetworkUtils.checkInternetConnection(this)) {
            NetworkUtils.movePopupConnection(this);
        }
        super.onResume();
    }
}
