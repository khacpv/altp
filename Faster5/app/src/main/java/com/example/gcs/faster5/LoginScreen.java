package com.example.gcs.faster5;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;


/**
 * Created by Kien on 07/05/2016.
 */
public class LoginScreen extends AppCompatActivity {

    private LoginButton mLoginButtonFb;
    private CallbackManager mCallbackManager;
    final Context context = this;
    AccessTokenTracker mAccessTokenTracker;
    RelativeLayout mRelativeLayoutBg;
    EditText mEditText;
    String mStringUserName;
    ImageButton mImageButtonPlay, mImageButtonTryAgain;
    ImageView mImageViewConnectFail, mImageViewTextLogin, mImageViewRoundRec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        getSupportActionBar().hide();

        FacebookSdk.sdkInitialize(getApplicationContext(), new FacebookSdk.InitializeCallback() {
                    @Override
                    public void onInitialized() {
                        AccessToken mAccessToken = AccessToken.getCurrentAccessToken();
                        if (mAccessToken == null) {
                        } else {
                            Intent intent = new Intent(LoginScreen.this, InfoScreen.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                }
        );
        setContentView(R.layout.login_screen);

        mRelativeLayoutBg = (RelativeLayout) findViewById(R.id.background);
        mRelativeLayoutBg.setBackgroundResource(R.drawable.background);

        mEditText = (EditText) findViewById(R.id.text_edit);
        Typeface font = Typeface.createFromAsset(getAssets(),
                "fonts/dimboregular.ttf");
        mEditText.setTypeface(font);
        mEditText.setHint("Choose username");
        mEditText.setFocusableInTouchMode(false);
        mEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mEditText.setHint("");
                mEditText.requestFocusFromTouch();
                mEditText.setFocusableInTouchMode(true);
                InputMethodManager keyBoard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                keyBoard.showSoftInput(mEditText, InputMethodManager.SHOW_IMPLICIT);
                return false;
            }
        });
        mEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    mEditText.clearFocus();
                    mEditText.setHint("Choose username");
                    mEditText.setFocusableInTouchMode(false);
                }
            }
        });

        mRelativeLayoutBg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mRelativeLayoutBg.setFocusable(true);
                mEditText.clearFocus();
                InputMethodManager keyBoard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                keyBoard.hideSoftInputFromWindow(mRelativeLayoutBg.getWindowToken(), 0);
                return false;
            }
        });

        mImageButtonPlay = (ImageButton) findViewById(R.id.button_play);
        mImageButtonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStringUserName = mEditText.getText().toString();
                if (mStringUserName.length() <= 3) {
                    AlertDialog alertDialogLogin = new AlertDialog.Builder(context).create();
                    alertDialogLogin.setMessage("Username incorrect. Username must be at least 4 characters!");
                    alertDialogLogin.setCancelable(false);
                    alertDialogLogin.setButton("Try Again", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    alertDialogLogin.show();
                } else {
                    Intent intent = new Intent(LoginScreen.this, InfoScreen.class);
                    intent.putExtra("NAME", mStringUserName);
                    startActivity(intent);
                    finish();
                }
            }
        });

        mImageButtonTryAgain = (ImageButton) findViewById(R.id.button_tryagain);
        mImageButtonTryAgain.setVisibility(View.GONE);
        mImageViewConnectFail = (ImageView) findViewById(R.id.image_connectfail);
        mImageViewConnectFail.setVisibility(View.GONE);
        mImageViewTextLogin = (ImageView) findViewById(R.id.image_textlogin);
        mImageViewTextLogin.setImageResource(R.drawable.text);
        mImageViewRoundRec = (ImageView) findViewById(R.id.image_roundrec);
        mImageViewRoundRec.setImageResource(R.drawable.roundrec);
        LoginFB();
    }

    public void LoginFB() {
        mCallbackManager = CallbackManager.Factory.create();
        mAccessTokenTracker = new
                AccessTokenTracker() {
                    @Override
                    protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {
                        AccessToken mAccessToken = newToken;
                    }
                }
        ;
        mLoginButtonFb = (LoginButton) findViewById(R.id.button_fb);
        mLoginButtonFb.setBackgroundResource(R.drawable.fbbutton);
        mLoginButtonFb.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        mLoginButtonFb.setReadPermissions("public_profile");
        mLoginButtonFb.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (checkInternetConnection(LoginScreen.this)) {
                    mLoginButtonFb.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                                @Override
                                public void onSuccess(LoginResult loginResult) {
                                    mLoginButtonFb.setVisibility(View.INVISIBLE);
                                    AccessToken mAccessToken = loginResult.getAccessToken();
                                    Intent intent = new Intent(LoginScreen.this, InfoScreen.class);
                                    startActivity(intent);
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
                    mAccessTokenTracker.startTracking();
                } else {
                    mLoginButtonFb.setVisibility(View.GONE);
                    mImageButtonPlay.setVisibility(View.GONE);
                    mImageViewTextLogin.setVisibility(View.GONE);
                    mImageViewRoundRec.setVisibility(View.GONE);
                    mEditText.setVisibility(View.GONE);
                    mImageButtonTryAgain.setVisibility(View.VISIBLE);
                    mImageViewConnectFail.setVisibility(View.VISIBLE);

                    mEditText.setHint("");
                    mImageViewConnectFail.setImageResource(R.drawable.connectfail);
                    mImageButtonTryAgain.setImageResource(R.drawable.tryagainbutton);
                    mImageButtonTryAgain.setFocusable(true);
                    mImageButtonTryAgain.setOnClickListener(new View.OnClickListener() {
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
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public boolean checkInternetConnection(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
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