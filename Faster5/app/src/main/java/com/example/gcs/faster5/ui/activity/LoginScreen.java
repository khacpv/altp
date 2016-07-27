package com.example.gcs.faster5.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.gcs.faster5.R;
import com.example.gcs.faster5.util.NetworkUtils;
import com.example.gcs.faster5.util.PrefUtils;
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
                            overridePendingTransition(R.animator.right_in, R.animator.left_out);
                            startActivity(intent);
                            finish();
                        }
                    }
                }
        );
        setContentView(R.layout.login_screen);

        if (PrefUtils.getInstance(this).get(PrefUtils.KEY_LOGGED_IN, false)) {
            Intent intent = new Intent(LoginScreen.this, InfoScreen.class);
            startActivity(intent);
            finish();
        }

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
//        mImageButtonPlay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mStringUserName = mEditText.getText().toString();
//                if (mStringUserName.length() <= 3) {
//                    AlertDialog alertDialogLogin = new AlertDialog.Builder(context).create();
//                    alertDialogLogin.setMessage("Username incorrect. Username must be at least 4 characters!");
//                    alertDialogLogin.setCancelable(false);
//                    alertDialogLogin.setButton("Try Again", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                        }
//                    });
//                    alertDialogLogin.show();
//                } else {
//                    PrefUtils.getInstance(LoginScreen.this).set(PrefUtils.KEY_NAME, mStringUserName);
//                    PrefUtils.getInstance(LoginScreen.this).set(PrefUtils.KEY_GOLD, 0);
//
//                    Intent intent = new Intent(LoginScreen.this, InfoScreen.class);
//                    startActivity(intent);
//                    finish();
//                }
//            }
//      });
        mImageButtonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
//                View popupView = layoutInflater.inflate(R.layout.popup_login, null);
//                final PopupWindow pw = new PopupWindow(popupView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
//                pw.showAtLocation(popupView, Gravity.CENTER, 0, 0);
//
//                Button btnDismiss = (Button)popupView.findViewById(R.id.button_close);
//                btnDismiss.setOnClickListener(new Button.OnClickListener(){
//                    @Override
//                    public void onClick(View v) {
//                        // TODO Auto-generated method stub
//                        pw.dismiss();
//                    }});

                Intent intent = new Intent(getApplicationContext(), PopupLogin.class);
                startActivity(intent);
                overridePendingTransition(R.animator.slide_in_bottom, R.animator.slide_out_bottom);
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
                };
        mLoginButtonFb = (LoginButton) findViewById(R.id.button_fb);
        mLoginButtonFb.setBackgroundResource(R.drawable.fbbutton);
        mLoginButtonFb.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        mLoginButtonFb.setReadPermissions("public_profile");
        mLoginButtonFb.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (NetworkUtils.checkInternetConnection(LoginScreen.this)) {
                    mLoginButtonFb.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                                @Override
                                public void onSuccess(LoginResult loginResult) {
                                    mLoginButtonFb.setVisibility(View.INVISIBLE);
                                    AccessToken mAccessToken = loginResult.getAccessToken();
                                    PrefUtils.getInstance(LoginScreen.this).set(PrefUtils.KEY_ACCESS_TOKEN, mAccessToken.getToken());
                                    Intent intent = new Intent(LoginScreen.this, InfoScreen.class);
                                    startActivity(intent);
                                    overridePendingTransition(R.animator.right_in, R.animator.left_out);
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
                            startActivity(intent);
                            overridePendingTransition(R.animator.in_from_left, R.animator.out_to_right);
                            finish();
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

}