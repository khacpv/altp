package com.example.gcs.faster5.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.example.gcs.faster5.R;
import com.example.gcs.faster5.util.PrefUtils;

import org.greenrobot.eventbus.EventBus;

public class MainActivity extends AppCompatActivity {

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

        String userId = PrefUtils.getInstance(this).get(PrefUtils.KEY_USER_ID, "");
        String username = PrefUtils.getInstance(this).get(PrefUtils.KEY_NAME, "");
        String linkAvatar = PrefUtils.getInstance(this).get(PrefUtils.KEY_URL_AVATAR, "");
        String location = PrefUtils.getInstance(this).get(PrefUtils.KEY_LOCATION, "");

        Log.e("TAG", String.format("user: {id=%s,name:%s,location:%s}", userId,username,location));

        if (TextUtils.isEmpty(userId) ||TextUtils.isEmpty(username) || TextUtils.isEmpty(linkAvatar)
                || TextUtils.isEmpty(location)) {
            Intent myIntent = new Intent(getApplicationContext(), LoginScreen.class);
            startActivity(myIntent);
            finish();
        } else {
            Intent myIntent = new Intent(getApplicationContext(), InfoScreen.class);
            startActivity(myIntent);
            finish();
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

