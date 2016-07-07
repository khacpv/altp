package com.example.gcs.faster5;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;

/**
 * Created by Kien on 07/05/2016.
 */
public class InfoScreen extends AppCompatActivity {
    RelativeLayout hinhNen;
    Button doneButton;
    ImageView avatarfb;
    String idUserFB, nameUserFB;
    TextView userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.info_screen);
        userName = (TextView) findViewById(R.id.nameUser);
        avatarfb = (ImageView) findViewById(R.id.avatarUser);

        hinhNen = (RelativeLayout) findViewById(R.id.BackGround);
        hinhNen.setBackgroundResource(R.drawable.backsh);
        doneButton = (Button) findViewById(R.id.buttonDone);
        doneButton.setOnClickListener(new PlayGame());

        Intent intentLogin = getIntent();
        nameUserFB = intentLogin.getStringExtra("NAME");
        idUserFB = intentLogin.getStringExtra("ID");


        userName.setText(nameUserFB);
        if (idUserFB == null) {
            avatarfb.setImageResource(R.drawable.avatar);
        } else {
            Glide.with(this).load("https://graph.facebook.com/" + idUserFB + "/picture?width=100&height=100").into(avatarfb);
        }
    }


    public class PlayGame implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent myIntent = new Intent(getApplicationContext(), MainScreen.class);
            myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(myIntent);
            finish();
        }
    }

    public void onBackPressed() {
        Intent myIntent = new Intent(getApplicationContext(), LoginScreen.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(myIntent);
        finish();
        return;
    }
}
