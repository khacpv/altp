package com.example.gcs.faster5;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;

import android.support.annotation.DrawableRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;

import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;


/**
 * Created by Kien on 07/05/2016.
 */
public class LoginScreen extends AppCompatActivity {

    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private AccessToken accessToken;
    private AccessTokenTracker accessTokenTracker;
    public Intent startActivityIntent, userNameIntent;
    public ConnectivityManager connectivityManager;
    final Context context = this;
    RelativeLayout backGround;
    EditText editText;
    InputMethodManager softkeyboard;
    String username;
    ImageButton playButtonImgB, tryagainButtonImgB;
    ImageView connectFailImgV, textLoginImgV, roundRecEdittxImgV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        getSupportActionBar().hide();

        startActivityIntent = new Intent(LoginScreen.this, InfoScreen.class);
        FacebookSdk.sdkInitialize(getApplicationContext(), new FacebookSdk.InitializeCallback() {
                    @Override
                    public void onInitialized() {
                        //AccessToken is for us to check whether we have previously logged in into
                        //this app, and this information is save in shared preferences and sets it during SDK initialization
                        accessToken = AccessToken.getCurrentAccessToken();
                        if (accessToken == null) {
                        } else {
                            startActivity(startActivityIntent);
                            finish();
                        }
                    }
                }
        );

        setContentView(R.layout.login_screen);
        backGround = (RelativeLayout) findViewById(R.id.BackGround);
        backGround.setBackgroundResource(R.drawable.background);

        softkeyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);


        editText = (EditText) findViewById(R.id.editText);
        Typeface font = Typeface.createFromAsset(getAssets(),
                "fonts/dimboregular.ttf");
        editText.setTypeface(font);
        editText.setHint("Choose username");
        editText.setFocusableInTouchMode(false);
        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                editText.setHint("");
                editText.requestFocusFromTouch();
                editText.setFocusableInTouchMode(true);
                softkeyboard.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                return false;
            }
        });

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    editText.clearFocus();
                    editText.setHint("Choose username");
                    editText.setFocusableInTouchMode(false);
                }
            }
        });
        backGround.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                backGround.setFocusable(true);
                editText.clearFocus();
                softkeyboard.hideSoftInputFromWindow(backGround.getWindowToken(), 0);
                return false;
            }
        });

        userNameIntent = new Intent(LoginScreen.this, InfoScreen.class);
        playButtonImgB = (ImageButton) findViewById(R.id.playbutton);
        playButtonImgB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                username = editText.getText().toString();
                if (username.length() <= 3) {
                    AlertDialog alertDialogLogin = new AlertDialog.Builder(context).create();
                    alertDialogLogin.setMessage("Username incorrect. Username must be at least 4 characters!");
                    alertDialogLogin.setCancelable(false);
                    alertDialogLogin.setButton("Try Again", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    alertDialogLogin.show();
                } else {
                    userNameIntent.putExtra("NAME", username);
                    startActivity(startActivityIntent);
                    startActivity(userNameIntent);
                    finish();
                }
            }
        });

        tryagainButtonImgB = (ImageButton) findViewById(R.id.tryagainButton);
        tryagainButtonImgB.setVisibility(View.GONE);
        connectFailImgV = (ImageView) findViewById(R.id.connectfail);
        connectFailImgV.setVisibility(View.GONE);
        textLoginImgV = (ImageView) findViewById(R.id.textlogin);
        textLoginImgV.setImageResource(R.drawable.text);
        roundRecEdittxImgV = (ImageView) findViewById(R.id.roundrec);
        roundRecEdittxImgV.setImageResource(R.drawable.roundrec);
        LoginFB();
    }

    public void LoginFB() {
        //register a callback to respond to a login result,
        callbackManager = CallbackManager.Factory.create();

        //register access token to check whether user logged in before
        accessTokenTracker = new

                AccessTokenTracker() {
                    @Override
                    protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {
                        accessToken = newToken;
                    }
                }
        ;
        loginButton = (LoginButton) findViewById(R.id.fbbutton);
        loginButton.setBackgroundResource(R.drawable.fbbutton);
        loginButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        loginButton.setReadPermissions("public_profile");
        loginButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (checkInternetConnection(LoginScreen.this)) {
                    // Callback registration
                    loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                                @Override
                                public void onSuccess(LoginResult loginResult) {
                                    loginButton.setVisibility(View.INVISIBLE);
                                    accessToken = loginResult.getAccessToken();
                                    startActivity(startActivityIntent);
                                    finish();
                                }

                                @Override
                                public void onCancel() {

                                }

                                @Override
                                public void onError(FacebookException exception) {

                                }
                            }
                    );
                    accessTokenTracker.startTracking();
                } else {
                    loginButton.setVisibility(View.GONE);
                    playButtonImgB.setVisibility(View.GONE);
                    textLoginImgV.setVisibility(View.GONE);
                    roundRecEdittxImgV.setVisibility(View.GONE);
                    editText.setVisibility(View.GONE);
                    tryagainButtonImgB.setVisibility(View.VISIBLE);
                    connectFailImgV.setVisibility(View.VISIBLE);

                    editText.setHint("");
                    connectFailImgV.setImageResource(R.drawable.connectfail);
                    tryagainButtonImgB.setImageResource(R.drawable.tryagainbutton);
                    tryagainButtonImgB.setFocusable(true);
                    tryagainButtonImgB.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = getIntent();
                            finish();
                            startActivity(intent);
                        }
                    });
                }
                return false;
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public boolean checkInternetConnection(Context context) {
        connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getActiveNetworkInfo() != null
                && connectivityManager.getActiveNetworkInfo().isAvailable()
                && connectivityManager.getActiveNetworkInfo().isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    public void onBackPressed() {

    }
}