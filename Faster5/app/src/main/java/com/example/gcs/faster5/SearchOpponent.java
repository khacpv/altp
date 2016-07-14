package com.example.gcs.faster5;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
 * Created by Kien on 07/12/2016.
 */
public class SearchOpponent extends AppCompatActivity {

    RelativeLayout backGround;
    TextView nameTopicTxtV, userName1TxtV, userName2TxtV;
    ImageView avatarUser1ImgV, avatarUser2ImgV;
    ImageButton playButtonImgB;
    AccessToken accessToken;
    int idTopic;
    String nameTopic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.search_opponent);

        Bundle extrasName = getIntent().getExtras();
        if (extrasName != null) {
            idTopic = extrasName.getInt("IDTOPIC");
            nameTopic = extrasName.getString("NAMETOPIC");
        }

        Typeface font = Typeface.createFromAsset(getAssets(),
                "fonts/dimboregular.ttf");

        backGround = (RelativeLayout) findViewById(R.id.BackGround);
        backGround.setBackgroundResource(R.drawable.background);

        playButtonImgB = (ImageButton) findViewById(R.id.playbutton);
        playButtonImgB.setImageResource(R.drawable.playbutton);

        avatarUser1ImgV = (ImageView) findViewById(R.id.avatarUser1);
        avatarUser2ImgV = (ImageView) findViewById(R.id.avatarUser2);

        userName1TxtV = (TextView) findViewById(R.id.userName1);
        userName2TxtV = (TextView) findViewById(R.id.userName2);
        userName1TxtV.setTypeface(font);
        userName2TxtV.setTypeface(font);

        nameTopicTxtV = (TextView) findViewById(R.id.nameTopic);
        nameTopicTxtV.setTypeface(font);
        nameTopicTxtV.setText(nameTopic);



        FacebookSdk.sdkInitialize(getApplicationContext(), new FacebookSdk.InitializeCallback() {
            @Override
            public void onInitialized() {
                //AccessToken is for us to check whether we have previously logged in into
                //this app, and this information is save in shared preferences and sets it during SDK initialization
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



        playButtonImgB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveMainScreenIntent = new Intent(getApplicationContext(), MainScreen.class);
                moveMainScreenIntent.putExtra("IDTOPIC", idTopic);
                startActivity(moveMainScreenIntent);
                finish();
            }
        });

    }
}
