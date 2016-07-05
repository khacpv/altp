package com.example.gcs.faster5;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by Kien on 07/05/2016.
 */
public class LoginScreen extends AppCompatActivity {
    RelativeLayout hinhNen;
    ImageButton fbButton, googleButton;
    ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.login_screen);
        HinhAnh();
        addListenerOnfbButton();
        addListenerOnGoogleButton();
    }

    public void HinhAnh(){
        hinhNen = (RelativeLayout) findViewById(R.id.BackGround);
        hinhNen.setBackgroundResource(R.drawable.bgaccount);

        logo = (ImageView) findViewById(R.id.LoGo);
        logo.setImageResource(R.drawable.logo);

        fbButton = (ImageButton) findViewById(R.id.FACEBOOK);
        fbButton.setImageResource(R.drawable.fbbutton);

        googleButton = (ImageButton) findViewById(R.id.GOOGLE);
        googleButton.setImageResource(R.drawable.gbutton);
    }

    public void addListenerOnfbButton() {

        fbButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent myIntent = new Intent(getApplicationContext(), InfoScreen.class);
                myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(myIntent);
                finish();
            }
        });
    }

    public void addListenerOnGoogleButton() {

        googleButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent myIntent = new Intent(getApplicationContext(), InfoScreen.class);
                myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(myIntent);
                finish();
            }
        });
    }
}
