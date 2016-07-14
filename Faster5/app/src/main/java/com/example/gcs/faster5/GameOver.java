package com.example.gcs.faster5;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;

/**
 * Created by Kien on 07/14/2016.
 */
public class GameOver extends AppCompatActivity {
    ImageView avatarUser1ImgV, avatarUser2ImgV;
    TextView userName1TxtV, userName2TxtV, result;
    RelativeLayout backGround;
    AccessToken accessToken;
    ImageButton buttonOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.game_over);
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/dimboregular.ttf");
        backGround = (RelativeLayout) findViewById(R.id.background);
        backGround.setBackgroundResource(R.drawable.background);

        avatarUser1ImgV = (ImageView) findViewById(R.id.avatarUser1);
        userName1TxtV = (TextView) findViewById(R.id.userName1);
        userName2TxtV = (TextView) findViewById(R.id.userName2);
        userName1TxtV.setTypeface(font);
        userName2TxtV.setTypeface(font);
        FacebookSdk.sdkInitialize(getApplicationContext(), new FacebookSdk.InitializeCallback() {
            @Override
            public void onInitialized() {
                accessToken = AccessToken.getCurrentAccessToken();
                if (accessToken != null) {
                    userName1TxtV.setText(InfoScreen.fullNameFb);
                    Glide.with(getApplicationContext()).load("https://graph.facebook.com/" + InfoScreen.idUserFB + "/picture?width=500&height=500").into(avatarUser1ImgV);
                } else {
                    userName1TxtV.setText(InfoScreen.nameManual);
                    avatarUser1ImgV.setImageResource(R.drawable.avatar);
                }
            }
        });

        result = (TextView) findViewById(R.id.result);
        result.setTypeface(font);
        result.setText("YOU WIN");

        buttonOk = (ImageButton) findViewById(R.id.buttonOk);
        buttonOk.setImageResource(R.drawable.okbutton);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), InfoScreen.class);
                startActivity(intent);
                finish();
            }
        });

    }

}
