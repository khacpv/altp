package com.oic.game.ailatrieuphu.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.WorkerThread;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.oic.game.ailatrieuphu.R;
import com.oic.game.ailatrieuphu.util.ISoundPoolLoaded;
import com.oic.game.ailatrieuphu.util.PrefUtils;
import com.oic.game.ailatrieuphu.util.SoundPoolManager;
import com.oicmap.game.multiparalaxview.DataItem;
import com.oicmap.game.multiparalaxview.ParallaxView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    String userId, username, linkAvatar, location;

    TextView textviewLoading;

    ParallaxView mParallaxView;

    List<DataItem> data = new ArrayList<>();

    final long LOAD_MAX_TIME = 2000;    // 2 seconds

    long startTime = 0;

    Handler handler = new Handler();

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

        textviewLoading = (TextView) findViewById(R.id.textview_loading);

        startTime = System.currentTimeMillis();

        loadParallaxView();

        Log.e("TAG", String.format("user: {id=%s,name:%s,location:%s}", userId, username,
                location));
    }

    @Override
    protected void onResume() {
        super.onResume();

        userId = PrefUtils.getInstance(this).get(PrefUtils.KEY_USER_ID, "");
        username = PrefUtils.getInstance(this).get(PrefUtils.KEY_NAME, "");
        linkAvatar = PrefUtils.getInstance(this).get(PrefUtils.KEY_URL_AVATAR, "");
        location = PrefUtils.getInstance(this).get(PrefUtils.KEY_LOCATION, "");

        new Thread(new Runnable() {
            @Override
            public void run() {
                loadSound();
            }
        }).start();

    }

    public void loadParallaxView() {
        mParallaxView = (ParallaxView) findViewById(R.id.parallax);

        {
            DataItem item = DataItem.Builder.make("Nhất cử lưỡng tiện", DataItem.Builder
                    .MODE_DEFAULT)
                    .setXPercent(0)
                    .setYPercent(0)
                    .build();
            data.add(item);
        }

        {
            DataItem item = DataItem.Builder.make("Xôi hỏng bỏng không", DataItem.Builder.MODE_HIGH)
                    .setXPercent(10)
                    .setYPercent(10)
                    .build();
            data.add(item);
        }

        {
            DataItem item = DataItem.Builder.make("\"Đói cho sạch, rách cho ...\"", DataItem
                    .Builder.MODE_LOW)
                    .setXPercent(20)
                    .setYPercent(30)
                    .build();
            data.add(item);
        }

        {
            DataItem item = DataItem.Builder.make("\"Mật ngọt chết ...\"?", DataItem.Builder
                    .MODE_DEFAULT)
                    .setXPercent(30)
                    .setYPercent(50)
                    .build();
            data.add(item);
        }

        {
            DataItem item = DataItem.Builder.make("\"Phép ... thua lệ làng\"?", DataItem.Builder
                    .MODE_HIGH)
                    .setXPercent(40)
                    .setYPercent(65)
                    .build();
            data.add(item);
        }

        {
            DataItem item = DataItem.Builder.make("\"Chim sa ... lặn\"?", DataItem.Builder.MODE_LOW)
                    .setXPercent(50)
                    .setYPercent(75)
                    .build();
            data.add(item);
        }

        {
            DataItem item = DataItem.Builder.make("Nước uống nhớ nguồn", DataItem.Builder
                    .MODE_DEFAULT)
                    .setXPercent(60)
                    .setYPercent(20)
                    .build();
            data.add(item);
        }

        {
            DataItem item = DataItem.Builder.make("\"Ăn bờ ở ...\"?", DataItem.Builder.MODE_DEFAULT)
                    .setXPercent(70)
                    .setYPercent(35)
                    .build();
            data.add(item);
        }

        {
            DataItem item = DataItem.Builder.make("Nem công chả phượng", DataItem.Builder.MODE_HIGH)
                    .setXPercent(80)
                    .setYPercent(55)
                    .build();
            data.add(item);
        }

        {
            DataItem item = DataItem.Builder.make("\"Xa mặt cách ...\"?", DataItem.Builder.MODE_LOW)
                    .setXPercent(90)
                    .setYPercent(70)
                    .build();
            data.add(item);
        }

        {
            DataItem item = DataItem.Builder.make("\"Đất rộng trời ...\"?", DataItem.Builder
                    .MODE_DEFAULT)
                    .setXPercent(10)
                    .setYPercent(80)
                    .build();
            data.add(item);
        }

        {
            DataItem item = DataItem.Builder.make("\"Ăn nên làm ...\"?", DataItem.Builder.MODE_LOW)
                    .setXPercent(20)
                    .setYPercent(90)
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
                            Intent myIntent;
                            if (TextUtils.isEmpty(userId) || TextUtils.isEmpty(username) ||
                                    TextUtils.isEmpty(linkAvatar)
                                    || TextUtils.isEmpty(location)) {
                                myIntent = new Intent(getApplicationContext(), LoginScreen.class);
                            } else {
                                myIntent = new Intent(getApplicationContext(), InfoScreen.class);
                            }

                            startActivity(myIntent);
                            overridePendingTransition(R.anim.xml_fade_in, R.anim.xml_fade_out);
                            finish();
                        }
                    }, Math.max(LOAD_MAX_TIME, LOAD_MAX_TIME - (System.currentTimeMillis()
                            - startTime)));
                }

                @Override
                public void onLoadUpdate(final int totalSound, final int itemLoad) {
                    handler.postAtFrontOfQueue(new Runnable() {
                        @Override
                        public void run() {
                            int percent = (int)(itemLoad * 1f / totalSound * 100);
                            if(percent<99) {
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
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}

