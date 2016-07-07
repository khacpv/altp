package com.example.gcs.faster5;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;

/**
 * Created by Kien on 07/05/2016.
 * test
 */
public class MainScreen extends AppCompatActivity {
    RelativeLayout hinhNen;
    ImageView avatarfb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.main_screen);
        hinhNen = (RelativeLayout) findViewById(R.id.BackGround);
        hinhNen.setBackgroundResource(R.drawable.bgmain);

        avatarfb = (ImageView) findViewById(R.id.avatarUser1);
        String data = getIntent().getExtras().getString("IDFB");
        Glide.with(getApplicationContext()).load("https://graph.facebook.com/" + data + "/picture?width=100&height=100").into(avatarfb);
    }

    public void onBackPressed() {
        Intent myIntent = new Intent(getApplicationContext(), InfoScreen.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(myIntent);
        finish();
        return;
    }
}
