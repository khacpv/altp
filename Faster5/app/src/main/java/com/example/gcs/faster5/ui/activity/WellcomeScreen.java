package com.example.gcs.faster5.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.example.gcs.faster5.R;

/**
 * Created by Kien on 07/10/2016.
 */
public class WellcomeScreen extends AppCompatActivity {

    RelativeLayout mRelativeLayoutBg;
    ImageButton mImageButtonPlayNormal;
    boolean isPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.wellcome_screen);
        mRelativeLayoutBg = (RelativeLayout) findViewById(R.id.background);
        mRelativeLayoutBg.setBackgroundResource(R.drawable.background);

        mImageButtonPlayNormal = (ImageButton) findViewById(R.id.button_play);
        //  mImageButtonPlayNormal.setImageResource(R.drawable.play_normal);
        playButtonPressed();
    }

    public void playButtonPressed() {
        View.OnClickListener buttonListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (isPressed)
//                    mImageButtonPlayNormal.setImageResource(R.drawable.play_normal);
//                else
//                    mImageButtonPlayNormal.setImageResource(R.drawable.play_pressed);
                isPressed = !isPressed;
                Intent moveMainScreen = new Intent(getApplicationContext(), LoginScreen.class);
                moveMainScreen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(moveMainScreen);
                overridePendingTransition(R.animator.right_in, R.animator.left_out);
                finish();
            }
        };
        mImageButtonPlayNormal.setOnClickListener(buttonListener);
    }

}