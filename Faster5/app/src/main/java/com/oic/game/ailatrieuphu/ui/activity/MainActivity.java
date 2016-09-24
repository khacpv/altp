package com.oic.game.ailatrieuphu.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.bumptech.glide.Glide;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }


        Glide.with(this).load(R.drawable.loading);
        
        userId = PrefUtils.getInstance(this).get(PrefUtils.KEY_USER_ID, "");
        username = PrefUtils.getInstance(this).get(PrefUtils.KEY_NAME, "");
        linkAvatar = PrefUtils.getInstance(this).get(PrefUtils.KEY_URL_AVATAR, "");
        location = PrefUtils.getInstance(this).get(PrefUtils.KEY_LOCATION, "");

        loadSound();

        Log.e("TAG", String.format("user: {id=%s,name:%s,location:%s}", userId, username, location));
    }

    public void loadSound() {
        SoundPoolManager.CreateInstance();
        List<Integer> sounds = new ArrayList<Integer>();
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
                    if (TextUtils.isEmpty(userId) || TextUtils.isEmpty(username) || TextUtils.isEmpty(linkAvatar)
                            || TextUtils.isEmpty(location)) {
//                        Intent myIntent = new Intent(getApplicationContext(), LoginScreen.class);
//                        myIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                        startActivity(myIntent);
//                        overridePendingTransition(0, 0);
//                        finish();
                    } else {
//                        Intent myIntent = new Intent(getApplicationContext(), InfoScreen.class);
//                        myIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                        startActivity(myIntent);
//                        overridePendingTransition(0, 0);
//                        finish();
                    }
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

