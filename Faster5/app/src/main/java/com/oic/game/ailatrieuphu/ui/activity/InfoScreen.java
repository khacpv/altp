package com.oic.game.ailatrieuphu.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.oic.game.ailatrieuphu.MainApplication;
import com.oic.game.ailatrieuphu.R;
import com.oic.game.ailatrieuphu.model.Room;
import com.oic.game.ailatrieuphu.model.User;
import com.oic.game.ailatrieuphu.sock.AltpHelper;
import com.oic.game.ailatrieuphu.sock.SockAltp;
import com.oic.game.ailatrieuphu.ui.widget.HexagonDrawable;
import com.oic.game.ailatrieuphu.util.NetworkUtils;
import com.oic.game.ailatrieuphu.util.PrefUtils;
import com.oic.game.ailatrieuphu.util.SoundPoolManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.socket.client.Socket;

/**
 * Created by Kien on 07/05/2016.
 */
public class InfoScreen extends AppCompatActivity {
    private TextView mTextViewNameUser;
    private TextView mTextViewTotalScore;
    private TextView mTextViewCity;
    private ImageView mImageViewAvatar;
    private Button[] mButtonPlayer = new Button[8];
    private RelativeLayout mButtonSearch;
    private SockAltp mSocketAltp;
    private AltpHelper mAltpHelper;
    private User mUser = new User();
    private User mEnemy = new User();
    private String username;
    private String linkAvatar;
    private String location;
    private String totalScore;
    private String userId;
    private final HexagonDrawable searchBg = new HexagonDrawable();
    private int searchTimes = 0;
    private int enemyNumberInList;
    private boolean isEnemy = false;
    private Dialog connectionDiaglog;
    MediaPlayer mediaPlayer;
    private Handler handler;
    Runnable resetSearch;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    /**
     * global events
     */
    private SockAltp.OnSocketEvent globalCallback = new SockAltp.OnSocketEvent() {
        @Override
        public void onEvent(String event, Object... args) {
            switch (event) {
                case Socket.EVENT_CONNECTING:
                    Log.e("TAG_INFO", "connecting");
                    break;
                case Socket.EVENT_CONNECT:  // auto call on connect to server
                    Log.e("TAG_INFO", "connect");
                    break;
                case Socket.EVENT_CONNECT_ERROR:
                    Log.e("TAG_INFO", "error");
                    break;
                case Socket.EVENT_CONNECT_TIMEOUT:
                    Log.e("TAG_INFO", "timeout");
                    break;
            }
        }
    };
    private SockAltp.OnSocketEvent searchCallback = new SockAltp.OnSocketEvent() {
        @Override
        public void onEvent(String event, Object... args) {
            Pair<Room, ArrayList<User>> result = mAltpHelper.searchCallback(args);
            OnSearhCallbackEvent eventBus = new OnSearhCallbackEvent();
            eventBus.result = result;
            EventBus.getDefault().post(eventBus);
        }
    };

    public void sendSearchRequest(User user) {
        mButtonSearch.getHandler().postDelayed(resetSearch, 36000);
        if (searchTimes > 0) {
            for (int i = 0; i < 8; i++) {
                mButtonPlayer[i].setText("NGƯỜI CHƠI");
                mButtonPlayer[i].setBackgroundResource(R.drawable.answer0);
            }
        }
        this.mUser = user;
        mAltpHelper.search(mUser);
        Log.e("TAG", "searchRequest: " + mUser.id + " " + mUser.name + " " + mUser.address + "\n" + mUser.avatar);
    }

    private void updateEnemy(User enemy) {
        this.mEnemy = enemy;
        PrefUtils.getInstance(InfoScreen.this).set(PrefUtils.KEY_ENEMY_ID, enemy.id);
        PrefUtils.getInstance(InfoScreen.this).set(PrefUtils.KEY_ENEMY_NAME, enemy.name);
        PrefUtils.getInstance(InfoScreen.this).set(PrefUtils.KEY_ENEMY_LOCATION, enemy.address.toUpperCase());
        PrefUtils.getInstance(InfoScreen.this).set(PrefUtils.KEY_ENEMY_AVATAR, enemy.avatar);
        PrefUtils.getInstance(InfoScreen.this).set(PrefUtils.KEY_ROOM_ID, enemy.room);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.info_screen);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        bgMusic();
        EventBus.getDefault().register(this);

        mSocketAltp = MainApplication.sockAltp();
        mAltpHelper = new AltpHelper(mSocketAltp);


        if (!mSocketAltp.isConnected()) {
            mSocketAltp.connect();
        }


        mSocketAltp.addGlobalEvent(globalCallback);
        mSocketAltp.addEvent("search", searchCallback);

        getUserInfo();

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

        findViewById();
        setView();
        buttonPlayer();

        popupConnection();

        handler = new Handler();

        resetSearch = new Runnable() {
            @Override
            public void run() {
                Log.e("TAG", "Search Button STOP ");
                searchBg.stop();
                searchBg.reset();
                mButtonSearch.setClickable(true);
            }
        };

        mButtonSearch = (RelativeLayout) findViewById(R.id.button_search);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mButtonSearch.setBackground(searchBg);
        } else {
            mButtonSearch.setBackgroundDrawable(new HexagonDrawable());
        }
        mButtonSearch.setClickable(true);
        mButtonSearch.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View v) {
                                                 SoundPoolManager.getInstance().playSound(R.raw.touch_sound);
                                                 if (NetworkUtils.checkInternetConnection(InfoScreen.this)) {
                                                     setUserInfo();
                                                     sendSearchRequest(mUser);
                                                     mButtonSearch.setClickable(false);
                                                     searchBg.start();
                                                 } else {
                                                     connectionDiaglog.show();
                                                 }

                                             }
                                         }
        );
    }

    public void findViewById() {
        Typeface font = Typeface.createFromAsset(getAssets(),
                "fonts/roboto.ttf");

        mTextViewNameUser = (TextView) findViewById(R.id.textview_usernname);
        mTextViewCity = (TextView) findViewById(R.id.textview_city_info);
        mImageViewAvatar = (ImageView) findViewById(R.id.imageview_useravatar);
        mTextViewTotalScore = (TextView) findViewById(R.id.textview_total_score);

        setTypeface(font, mTextViewCity, mTextViewNameUser, mTextViewTotalScore);

        connectionDiaglog = new Dialog(this);

    }

    public void popupConnection() {
        connectionDiaglog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        connectionDiaglog.setContentView(R.layout.layout_popup_connection);
        connectionDiaglog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        connectionDiaglog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        connectionDiaglog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Button tryAgain = (Button) connectionDiaglog.findViewById(R.id.btn_tryagain);

        tryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectionDiaglog.hide();
            }
        });

    }


    public void getUserInfo() {
        username = PrefUtils.getInstance(InfoScreen.this).get(PrefUtils.KEY_NAME, "");
        location = PrefUtils.getInstance(InfoScreen.this).get(PrefUtils.KEY_LOCATION, "");
        linkAvatar = PrefUtils.getInstance(InfoScreen.this).get(PrefUtils.KEY_URL_AVATAR, "");
        totalScore = PrefUtils.getInstance(InfoScreen.this).get(PrefUtils.KEY_TOTAL_SCORE, 0)+"";
        userId = PrefUtils.getInstance(InfoScreen.this).get(PrefUtils.KEY_USER_ID, "");
    }

    public void setUserInfo() {
        mUser.id = userId;
        mUser.name = username;
        mUser.address = location;
        mUser.avatar = linkAvatar;
    }

    public void setView() {
        mTextViewNameUser.setText(username);
        mTextViewCity.setText(location);
        Glide.with(getApplicationContext()).load(linkAvatar).into(mImageViewAvatar);
        mTextViewTotalScore.setText(totalScore);
    }

    public void buttonPlayer() {
        int i = 0;
        mButtonPlayer[i++] = (Button) findViewById(R.id.button_player1).findViewById(R.id
                .button_player);
        mButtonPlayer[i++] = (Button) findViewById(R.id.button_player2).findViewById(R.id
                .button_player);
        mButtonPlayer[i++] = (Button) findViewById(R.id.button_player3).findViewById(R.id
                .button_player);
        mButtonPlayer[i++] = (Button) findViewById(R.id.button_player4).findViewById(R.id
                .button_player);
        mButtonPlayer[i++] = (Button) findViewById(R.id.button_player5).findViewById(R.id
                .button_player);
        mButtonPlayer[i++] = (Button) findViewById(R.id.button_player6).findViewById(R.id
                .button_player);
        mButtonPlayer[i++] = (Button) findViewById(R.id.button_player7).findViewById(R.id
                .button_player);
        mButtonPlayer[i] = (Button) findViewById(R.id.button_player8).findViewById(R.id
                .button_player);

    }

    public void bgMusic() {
        AudioManager amanager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = amanager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
        amanager.setStreamVolume(AudioManager.STREAM_ALARM, maxVolume, 0);
        mediaPlayer = MediaPlayer.create(InfoScreen.this, R.raw.bgsearch);
        mediaPlayer.setLooping(true);
    }

    public static void setTypeface(Typeface font, TextView... textviews) {
        for (TextView textView : textviews) {
            textView.setTypeface(font);
        }
    }


    @Subscribe
    public void onEventMainThread(OnSearhCallbackEvent event) {
        handler.removeCallbacks(resetSearch);
        Pair<Room, ArrayList<User>> result = event.result;
        final Room room = result.first;
        final List<User> dummyUsers = result.second;

        searchTimes = 1;
        isEnemy = false;

        for (User user : room.users) {
            if (String.valueOf(user.id).equalsIgnoreCase(mUser.id)) {
                mUser = user;
            }
            // DO NOT use: user.id != mUser.id
            if (!String.valueOf(user.id).equalsIgnoreCase(mUser.id)) {
                user.isDummy = false;
                isEnemy = true;
                updateEnemy(user);
                dummyUsers.add(user);
            }
        }

        Collections.shuffle(dummyUsers);

        if (isEnemy) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            SoundPoolManager.getInstance().playSound(R.raw.search_finish);

            searchBg.stop();

            for (int i = 0; i < Math.min(mButtonPlayer.length,dummyUsers.size()); i++) {
                final int _i = i;
                if (!dummyUsers.get(_i).isDummy) {
                    enemyNumberInList = _i;

                    mButtonPlayer[enemyNumberInList].postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            AnimationDrawable btnAnswerDrawable = (AnimationDrawable)
                                    getResources().getDrawable(R.drawable.xml_btn_anim);
                            mButtonPlayer[enemyNumberInList].setBackgroundDrawable(btnAnswerDrawable);
                            btnAnswerDrawable.start();
                            SoundPoolManager.getInstance().playSound(R.raw.enemy_selected);

                        }
                    }, 2000);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mButtonPlayer[_i].setText(dummyUsers.get(_i).name);
                    }
                });
                Log.e("TAG", "dummy user: " + dummyUsers.get(i).name);
            }

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    moveSearchOpponent(room);
                }
            }, 4000);
            Log.e("TAG", "join room: " + room.roomId);
            Log.e("TAG", "dummy user: " + dummyUsers.size());
        }
    }

    public void moveSearchOpponent(Room room) {
        startActivity(SearchOpponent.createIntent(InfoScreen.this, mUser, mEnemy, room));
        overridePendingTransition(R.animator.right_in, R.animator.left_out);
        finish();
    }

    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onPause() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        }
        super.onPause();
    }


    @Override
    protected void onResume() {
        if (mediaPlayer != null) {
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
            }
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }

        if (connectionDiaglog != null) {
            connectionDiaglog.dismiss();
        }
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        super.onDestroy();

    }

    public static class OnSearhCallbackEvent {
        Pair<Room, ArrayList<User>> result;

    }
}
