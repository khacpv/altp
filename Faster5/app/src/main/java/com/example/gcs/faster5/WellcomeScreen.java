package com.example.gcs.faster5;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

/**
 * Created by Kien on 07/10/2016.
 */
public class WellcomeScreen extends AppCompatActivity {

    RelativeLayout backGround;
    ImageButton playNormalButton;
    boolean isPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.wellcome_screen);
        backGround = (RelativeLayout) findViewById(R.id.BackGround);
        backGround.setBackgroundResource(R.drawable.background);

        playNormalButton = (ImageButton) findViewById(R.id.playbutton);
        playNormalButton.setImageResource(R.drawable.play_normal);
        playButtonPressed();
    }

    public void playButtonPressed() {
        View.OnClickListener buttonListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPressed)
                    playNormalButton.setImageResource(R.drawable.play_normal);
                else
                    playNormalButton.setImageResource(R.drawable.play_pressed);
                isPressed = !isPressed;
                Intent moveMainScreen = new Intent(getApplicationContext(), LoginScreen.class);
                moveMainScreen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(moveMainScreen);
                finish();
            }
        };
        playNormalButton.setOnClickListener(buttonListener);
    }

    public void onBackPressed() {

    }
}