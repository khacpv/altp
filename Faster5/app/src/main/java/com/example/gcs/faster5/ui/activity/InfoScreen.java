package com.example.gcs.faster5.ui.activity;


import android.graphics.Typeface;
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
import com.example.gcs.faster5.MainApplication;
import com.example.gcs.faster5.R;
import com.example.gcs.faster5.model.Room;
import com.example.gcs.faster5.model.User;
import com.example.gcs.faster5.sock.AltpHelper;
import com.example.gcs.faster5.sock.SockAltp;
import com.example.gcs.faster5.ui.widget.HexagonDrawable;
import com.example.gcs.faster5.util.PrefUtils;

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
    TextView mTextViewNameUser, mTextViewMoney, mTextViewCity,
            mTextViewPlayer1, mTextViewPlayer2, mTextViewPlayer3, mTextViewPlayer4,
            mTextViewPlayer5, mTextViewPlayer6, mTextViewPlayer7, mTextViewPlayer8;
    ImageView mImageViewAvatar;
    Button[] mButtonPlayer;
    RelativeLayout mButtonSearch;
    private SockAltp mSocketAltp;
    private AltpHelper mAltpHelper;
    private User mUser = new User();
    private User mEnemy = new User();
    String username, linkAvatar, location, money;
    long userId;
    final HexagonDrawable searchBg = new HexagonDrawable();
    int searchTimes = 0, enemyNumberInList;
    boolean isEnemy = false;
    Handler handler = new Handler();
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
                case Socket.EVENT_CONNECT:  // auto call on connect to server
                    Log.e("TAG_INFO", "connect");
                    break;
                case Socket.EVENT_CONNECT_ERROR:
                case Socket.EVENT_CONNECT_TIMEOUT:
                    Log.e("TAG_INFO", "disconnect");
                    if (!mSocketAltp.isConnected()) {
                        mSocketAltp.connect();
                    }
                    searchBg.stop();
                    mButtonSearch.setClickable(true);
                    break;
            }
        }
    };

    private SockAltp.OnSocketEvent searchCallback = new SockAltp.OnSocketEvent() {
        @Override
        public void onEvent(String event, Object... args) {
            Pair<Room, ArrayList<User>> result = mAltpHelper.searchCallback(args);
            OnSearCallbackEvent eventBus = new OnSearCallbackEvent();
            eventBus.result = result;
            EventBus.getDefault().post(eventBus);
        }
    };

    public void sendSearchRequest(User user) {
        if (searchTimes > 0) {
            for (int i = 0; i < 8; i++) {
                mButtonPlayer[i].setText("NGƯỜI CHƠI");
                mButtonPlayer[i].setBackgroundResource(R.drawable.button_player);
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
        PrefUtils.getInstance(InfoScreen.this).set(PrefUtils.KEY_ENEMY_LOCATION, enemy.address);
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
                                                 setUserInfo();
                                                 sendSearchRequest(mUser);
                                                 searchBg.start();
                                                 mButtonSearch.setClickable(false);
                                             }
                                         }
        );
    }

    public void findViewById() {
        Typeface font = Typeface.createFromAsset(getAssets(),
                "fonts/roboto.ttf");

        mTextViewNameUser = (TextView) findViewById(R.id.textview_usernname);
        mTextViewNameUser.setTypeface(font);

        mTextViewCity = (TextView) findViewById(R.id.textview_city_info);
        mTextViewCity.setTypeface(font);

        mImageViewAvatar = (ImageView) findViewById(R.id.imageview_useravatar);

        mTextViewMoney = (TextView) findViewById(R.id.textview_money);
        mTextViewMoney.setTypeface(font);

    }

    public void getUserInfo() {
        username = PrefUtils.getInstance(InfoScreen.this).get(PrefUtils.KEY_NAME, "");
        location = PrefUtils.getInstance(InfoScreen.this).get(PrefUtils.KEY_LOCATION, "");
        linkAvatar = PrefUtils.getInstance(InfoScreen.this).get(PrefUtils.KEY_URL_AVATAR, "");
        money = Integer.toString(PrefUtils.getInstance(InfoScreen.this).get(PrefUtils.KEY_MONEY, 0));
        userId = PrefUtils.getInstance(InfoScreen.this).get(PrefUtils.KEY_USER_ID, Long.valueOf(0));
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
        mTextViewMoney.setText(money);
    }

    public void buttonPlayer() {
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
            mButtonPlayer[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });
        }

    }

    @Subscribe
    public void onEventMainThread(OnSearCallbackEvent event) {
        Pair<Room, ArrayList<User>> result = event.result;
        final Room room = result.first;
        final List<User> dummyUsers = result.second;

        searchTimes = 1;
        isEnemy = false;

        for (User user : room.users) {
            if (user.id != mUser.id) {
                user.isDummy = false;
                isEnemy = true;
                updateEnemy(user);
                dummyUsers.add(user);
                break;
            }
        }

        Collections.shuffle(dummyUsers);

        if (isEnemy) {
            searchBg.stop();

            for (int i = 0; i < dummyUsers.size(); i++) {
                final int _i = i;
                if (!dummyUsers.get(_i).isDummy) {
                    enemyNumberInList = _i;
                    mButtonPlayer[enemyNumberInList].postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mButtonPlayer[enemyNumberInList].setBackgroundResource(R.drawable.answer3);
                            mButtonSearch.setClickable(true);
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

    @Override
    public void onPause() {
        super.onPause();
//        handler.removeCallbacksAndMessages(null);
//        handler.removeCallbacks(insertName);
//        handler.removeCallbacks(painter);
//        handler.removeCallbacks(moveScreen);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    public static class OnSearCallbackEvent {
        Pair<Room, ArrayList<User>> result;

    }
}
