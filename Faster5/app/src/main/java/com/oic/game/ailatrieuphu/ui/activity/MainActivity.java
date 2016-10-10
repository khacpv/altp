package com.oic.game.ailatrieuphu.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.WorkerThread;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.game.oic.ailatrieuphu.BuildConfig;
import com.game.oic.ailatrieuphu.R;
import com.oic.game.ailatrieuphu.MainApplication;
import com.oic.game.ailatrieuphu.sock.SockAltp;
import com.oic.game.ailatrieuphu.util.ISoundPoolLoaded;
import com.oic.game.ailatrieuphu.util.NetworkUtils;
import com.oic.game.ailatrieuphu.util.PrefUtils;
import com.oic.game.ailatrieuphu.util.SoundPoolManager;
import com.oicmap.game.multiparalaxview.DataItem;
import com.oicmap.game.multiparalaxview.ParallaxView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.Socket;

public class MainActivity extends AppCompatActivity {

    String userId, username, linkAvatar, location;

    TextView textviewLoading;
    TextView textviewDebug;
    ImageView imvDebug;

    ParallaxView mParallaxView;

    List<DataItem> data = new ArrayList<>();

    final long LOAD_MAX_TIME = 2000;    // 2 seconds

    long startTime = 0;

    Handler handler = new Handler();

    private SockAltp mSocketAltp;
    private Dialog maintainDialog;
    private boolean isLoaded = false;
    Intent myIntent;

    private SockAltp.OnSocketEvent globalCallback = new SockAltp.OnSocketEvent() {
        @Override
        public void onEvent(String event, Object... args) {
            switch (event) {
                case Socket.EVENT_CONNECTING:
                    break;
                case Socket.EVENT_DISCONNECT:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!maintainDialog.isShowing() && !isFinishing()) {
                                maintainDialog.show();
                            }
                        }
                    });
                    break;
                case Socket.EVENT_CONNECT:  // auto call on connect to server
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (maintainDialog.isShowing() && !isFinishing()) {
                                maintainDialog.dismiss();
                                if (isLoaded) {
                                    startActivity(myIntent);
                                    overridePendingTransition(R.animator.right_in, R.animator.left_out);
                                    finish();
                                }
                            }
                        }
                    });
                    break;
                case Socket.EVENT_CONNECT_ERROR:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!maintainDialog.isShowing() && !isFinishing()) {
                                maintainDialog.show();
                            }
                        }
                    });
                    break;
                case Socket.EVENT_CONNECT_TIMEOUT:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!maintainDialog.isShowing() && !isFinishing()) {
                                maintainDialog.show();
                            }
                        }
                    });
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_main);
        mSocketAltp = MainApplication.sockAltp();
        mSocketAltp.addGlobalEvent(globalCallback);
        if (!mSocketAltp.isConnected()) {
            mSocketAltp.connect();
        }
        maintainDialog = new Dialog(this);
        setMaintainDiaglog();

        textviewLoading = (TextView) findViewById(R.id.textview_loading);
        textviewDebug = (TextView) findViewById(R.id.debug);
        imvDebug = (ImageView) findViewById(R.id.imv_debug);

        startTime = System.currentTimeMillis();

        loadParallaxView();

        userId = PrefUtils.getInstance(this).get(PrefUtils.KEY_USER_ID, "");
        username = PrefUtils.getInstance(this).get(PrefUtils.KEY_NAME, "");
        linkAvatar = PrefUtils.getInstance(this).get(PrefUtils.KEY_URL_AVATAR, "");
        location = PrefUtils.getInstance(this).get(PrefUtils.KEY_LOCATION, "");

        if (TextUtils.isEmpty(userId) || TextUtils.isEmpty(username) ||
                TextUtils.isEmpty(linkAvatar)
                || TextUtils.isEmpty(location)) {
            myIntent = new Intent(getApplicationContext(), LoginScreen.class);
        } else {
            myIntent = new Intent(getApplicationContext(), InfoScreen.class);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                loadSound();
            }
        }).start();

        Log.e("TAG", String.format("user: {id=%s,name:%s,location:%s}", userId, username,
                location));

        if (!BuildConfig.DEBUG) {
            textviewDebug.setVisibility(View.GONE);
            imvDebug.setVisibility(View.GONE);
        }
    }


    public void setMaintainDiaglog() {
        maintainDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        maintainDialog.setContentView(R.layout.layout_maintain_popup);
        maintainDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        maintainDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        maintainDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        maintainDialog.setCancelable(false);

        TextView textViewNoti = (TextView) maintainDialog.findViewById(R.id.text_noti);

        if (NetworkUtils.checkInternetConnection(MainActivity.this)) {
            textViewNoti.setText(getResources().getString(R.string.text_baotri));
        } else {
            textViewNoti.setText(getResources().getString(R.string.connect_erro_text));
        }

        Button okBtn = (Button) maintainDialog.findViewById(R.id.button_okay);

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetworkUtils.checkInternetConnection(MainActivity.this)) {
                    maintainDialog.hide();
                    finish();
                } else {
                    maintainDialog.hide();
                    startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));
                    finish();
                }
            }
        });

    }


    public void loadParallaxView() {
        mParallaxView = (ParallaxView) findViewById(R.id.parallax);

        {
            DataItem item = DataItem.Builder.make("Nhất cử lưỡng tiện", DataItem.Builder
                    .MODE_DEFAULT)
                    .setXPercent(0)
                    .setYPercent(0)
                    .setAlpha(0.3f)
                    .setSize(getResources().getDimensionPixelSize(R.dimen.text_size_small))
                    .build();
            data.add(item);
        }

        {
            DataItem item = DataItem.Builder.make("Xôi hỏng bỏng không", DataItem.Builder
                    .MODE_DEFAULT)
                    .setXPercent(0)
                    .setYPercent(10)
                    .setAlpha(0.5f)
                    .setSize(getResources().getDimensionPixelSize(R.dimen.text_size_small))
                    .build();
            data.add(item);
        }

        {
            DataItem item = DataItem.Builder.make("\"Đói cho sạch, rách cho ...\"", DataItem
                    .Builder.MODE_DEFAULT)
                    .setXPercent(20)
                    .setYPercent(30)
                    .setAlpha(0.1f)
                    .setSize(getResources().getDimensionPixelSize(R.dimen.text_size_small))
                    .build();
            data.add(item);
        }

        {
            DataItem item = DataItem.Builder.make("\"Mật ngọt chết ...\"?", DataItem.Builder
                    .MODE_DEFAULT)
                    .setXPercent(5)
                    .setYPercent(50)
                    .setAlpha(0.3f)
                    .setSize(getResources().getDimensionPixelSize(R.dimen.text_size_small))
                    .build();
            data.add(item);
        }

        {
            DataItem item = DataItem.Builder.make("\"Phép ... thua lệ làng\"?", DataItem.Builder
                    .MODE_DEFAULT)
                    .setXPercent(15)
                    .setYPercent(65)
                    .setAlpha(0.5f)
                    .setSize(getResources().getDimensionPixelSize(R.dimen.text_size_small))
                    .build();
            data.add(item);
        }

        {
            DataItem item = DataItem.Builder.make("\"Chim sa ... lặn\"?", DataItem.Builder
                    .MODE_DEFAULT)
                    .setXPercent(30)
                    .setYPercent(75)
                    .setAlpha(0.1f)
                    .setSize(getResources().getDimensionPixelSize(R.dimen.text_size_small))
                    .build();
            data.add(item);
        }

        {
            DataItem item = DataItem.Builder.make("Nước uống nhớ nguồn", DataItem.Builder
                    .MODE_DEFAULT)
                    .setXPercent(50)
                    .setYPercent(20)
                    .setAlpha(0.3f)
                    .setSize(getResources().getDimensionPixelSize(R.dimen.text_size_small))
                    .build();
            data.add(item);
        }

        {
            DataItem item = DataItem.Builder.make("\"Ăn bờ ở ...\"?", DataItem.Builder.MODE_DEFAULT)
                    .setXPercent(58)
                    .setYPercent(35)
                    .setAlpha(0.5f)
                    .setSize(getResources().getDimensionPixelSize(R.dimen.text_size_small))
                    .build();
            data.add(item);
        }

        {
            DataItem item = DataItem.Builder.make("Nem công chả phượng", DataItem.Builder
                    .MODE_DEFAULT)
                    .setXPercent(30)
                    .setYPercent(55)
                    .setAlpha(0.5f)
                    .setSize(getResources().getDimensionPixelSize(R.dimen.text_size_small))
                    .build();
            data.add(item);
        }

        {
            DataItem item = DataItem.Builder.make("\"Xa mặt cách ...\"?", DataItem.Builder
                    .MODE_DEFAULT)
                    .setXPercent(50)
                    .setYPercent(70)
                    .setAlpha(0.1f)
                    .setSize(getResources().getDimensionPixelSize(R.dimen.text_size_small))
                    .build();
            data.add(item);
        }

        {
            DataItem item = DataItem.Builder.make("\"Đất rộng trời ...\"?", DataItem.Builder
                    .MODE_DEFAULT)
                    .setXPercent(10)
                    .setYPercent(80)
                    .setAlpha(0.3f)
                    .setSize(getResources().getDimensionPixelSize(R.dimen.text_size_small))
                    .build();
            data.add(item);
        }

        {
            DataItem item = DataItem.Builder.make("\"Ăn nên làm ...\"?", DataItem.Builder.MODE_DEFAULT)
                    .setXPercent(80)
                    .setYPercent(90)
                    .setAlpha(0.3f)
                    .setSize(getResources().getDimensionPixelSize(R.dimen.text_size_small))
                    .build();
            data.add(item);
        }

        mParallaxView.setData(data);
    }

    @WorkerThread
    public void loadSound() {
        SoundPoolManager.CreateInstance();
        List<Integer> sounds = new ArrayList<>();
        sounds.add(R.raw.touch_sound);
        sounds.add(R.raw.enemy_selected);
        sounds.add(R.raw.search_finish);
        sounds.add(R.raw.search_opponent);
        sounds.add(R.raw.ready);
        sounds.add(R.raw.luatchoi);
        sounds.add(R.raw.ans_a);
        sounds.add(R.raw.ans_a2);
        sounds.add(R.raw.ans_b);
        sounds.add(R.raw.ans_b2);
        sounds.add(R.raw.ans_c);
        sounds.add(R.raw.ans_c2);
        sounds.add(R.raw.ans_d);
        sounds.add(R.raw.ans_d2);
        sounds.add(R.raw.ques1);
        sounds.add(R.raw.ques1_b);
        sounds.add(R.raw.ques2);
        sounds.add(R.raw.ques3);
        sounds.add(R.raw.ques4);
        sounds.add(R.raw.ques5);
        sounds.add(R.raw.ques6);
        sounds.add(R.raw.ques7);
        sounds.add(R.raw.ques8);
        sounds.add(R.raw.ques9);
        sounds.add(R.raw.ques10);
        sounds.add(R.raw.ques11);
        sounds.add(R.raw.ques12);
        sounds.add(R.raw.ques13);
        sounds.add(R.raw.ques14);
        sounds.add(R.raw.ques15);
        sounds.add(R.raw.true_a);
        sounds.add(R.raw.true_a2);
        sounds.add(R.raw.true_a3);
        sounds.add(R.raw.true_b);
        sounds.add(R.raw.true_b2);
        sounds.add(R.raw.true_c);
        sounds.add(R.raw.true_c2);
        sounds.add(R.raw.true_c3);
        sounds.add(R.raw.true_d2);
        sounds.add(R.raw.true_d3);
        sounds.add(R.raw.lose_a);
        sounds.add(R.raw.lose_b);
        sounds.add(R.raw.lose_c);
        sounds.add(R.raw.lose_c2);
        sounds.add(R.raw.lose_d);
        sounds.add(R.raw.lose_d2);
        sounds.add(R.raw.important);
        sounds.add(R.raw.sound5050);
        sounds.add(R.raw.best_player);
        sounds.add(R.raw.pass_good);
        sounds.add(R.raw.lose);
        sounds.add(R.raw.ans_now1);
        sounds.add(R.raw.ans_now2);
        sounds.add(R.raw.ans_now3);
        sounds.add(R.raw.vuot_moc_1);
        sounds.add(R.raw.vuot_moc_2);
        sounds.add(R.raw.khan_gia);
        sounds.add(R.raw.khangia_bg);
        sounds.add(R.raw.timesup);

        SoundPoolManager.getInstance().setSounds(sounds);
        SoundPoolManager.getInstance().setPlaySound(true);
        try {
            SoundPoolManager.getInstance().InitializeSoundPool(this, new ISoundPoolLoaded() {
                @Override
                public void onSuccess() {

                    textviewLoading.setText("99%");

                    Log.e("TAG", "load time delay:" + Math.max(LOAD_MAX_TIME, LOAD_MAX_TIME -
                            (System.currentTimeMillis() - startTime)) + "");

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            textviewLoading.setText("100%");
                            mSocketAltp.removeEvent();
                            if (mSocketAltp.isConnected()) {
                                startActivity(myIntent);
                                overridePendingTransition(R.animator.right_in, R.animator.left_out);
                                finish();
                            }
                        }
                    }, Math.max(LOAD_MAX_TIME, LOAD_MAX_TIME - (System.currentTimeMillis()
                            - startTime)));
                    isLoaded = true;
                }

                @Override
                public void onLoadUpdate(final int totalSound, final int itemLoad) {
                    handler.postAtFrontOfQueue(new Runnable() {
                        @Override
                        public void run() {
                            int percent = (int) (itemLoad * 1f / totalSound * 100);
                            if (percent < 99) {
                                textviewLoading.setText(percent + "%");
                            }
                        }
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (maintainDialog != null) {
            maintainDialog.dismiss();
        }
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
