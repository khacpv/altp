package com.example.gcs.faster5.ui.activity;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.gcs.faster5.MainApplication;
import com.example.gcs.faster5.R;
import com.example.gcs.faster5.model.User;
import com.example.gcs.faster5.sock.AltpHelper;
import com.example.gcs.faster5.util.CameraUtils;
import com.example.gcs.faster5.sock.SockAltp;
import com.example.gcs.faster5.util.JSONParser;
import com.example.gcs.faster5.util.NetworkUtils;
import com.example.gcs.faster5.util.PrefUtils;
import com.example.gcs.faster5.util.UploadPhotoUtils;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.io.File;
import java.io.UnsupportedEncodingException;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import io.socket.client.Socket;

/**
 * Created by Kien on 07/05/2016.
 */
public class LoginScreen extends AppCompatActivity {

    private static final String TAG_CITY = "city";
    public static String city;
    private static String url = "http://209.58.180.196/json/"; //URL to get JSON Array
    private static final String photoFileName = "cameraphoto.jpg";
    public static final int IMAGE_FROM_CAMERA = 0;
    public static final int IMAGE_FROM_GALLERY = 1;
    public static final String prefixHOST = "http://ailatrieuphu.esy.es/imgupload/";
    public static final String DEFAULT_AVATAR = "http://ailatrieuphu.esy.es/imgupload/uploadedimages/avatar.png";
    final Context context = this;
    private AccessToken mAccessToken;
    private AccessTokenTracker mAccessTokenTracker;
    private RelativeLayout mRelativeLayoutBg;
    private EditText mEditText;
    private Button mImageButtonPlay;
    private TextView mTextViewCity;
    private Typeface font;
    private ImageView mImageViewAvatar;
    private LoginButton mLoginButtonFb;
    private CallbackManager mCallbackManager;
    private SockAltp mSocketAltp;
    private AltpHelper mAltpHelper;
    private User mUser = new User();
    private ProgressDialog prgDialog;
    private Dialog avatarDialog, edittexDialog, loginDialog;
    private String mStringUserName, imgPath, fileName, imgUrl;
    private Uri uriPhoto;
    private boolean uploadResult,
            isCheckPickImage = false,
            isCheckBtnLater = true;
    private UploadPhotoUtils uploadPhotoUtils = new UploadPhotoUtils();
    int uploadFail = 0;

    /**
     * global events
     */
    private SockAltp.OnSocketEvent globalCallback = new SockAltp.OnSocketEvent() {
        @Override
        public void onEvent(String event, Object... args) {
            switch (event) {
                case Socket.EVENT_CONNECTING:
                    Log.e("TAG_LOGIN", "connecting");
                    break;
                case Socket.EVENT_CONNECT:  // auto call on connect to server
                    Log.e("TAG_LOGIN", "connect");
                    break;
                case Socket.EVENT_CONNECT_ERROR:
                case Socket.EVENT_CONNECT_TIMEOUT:
                    Log.e("TAG_LOGIN", "disconnect");
                    if (!mSocketAltp.isConnected()) {
                        mSocketAltp.connect();
                    }
                    break;
            }
        }
    };
    private SockAltp.OnSocketEvent loginCallback = new SockAltp.OnSocketEvent() {
        @Override
        public void onEvent(String event, Object... args) {
            User user = mAltpHelper.loginCallback(args);
            if (user == null) {
                Log.e("Login", "Failed");
                return;
            }

            // login success
            mUser = user;
            PrefUtils.getInstance(LoginScreen.this).set(PrefUtils.KEY_USER_ID, mUser.id);
            PrefUtils.getInstance(LoginScreen.this).set(PrefUtils.KEY_NAME, mUser.name);
            PrefUtils.getInstance(LoginScreen.this).set(PrefUtils.KEY_LOCATION, mUser.address);
            PrefUtils.getInstance(LoginScreen.this).set(PrefUtils.KEY_URL_AVATAR, mUser.avatar);
            PrefUtils.getInstance(LoginScreen.this).set(PrefUtils.KEY_LOGGED_IN, true);
            Log.e("TAG", "LoginCallback: " + user.fbId + " " + user.id + " " + user.name + " " + user.address + " " + "\n" + user.avatar);
            loggedAndMoveInfoScreen();
        }
    };

    public void sendLoginRequest(User user) {
        this.mUser = user;
        mAltpHelper.login(mUser);
        Log.e("TAG", "loginRequest: " + mUser.fbId + " " + mUser.name + " " + mUser.address + "\n" + mUser.avatar);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        FacebookSdk.sdkInitialize(getApplicationContext(), new FacebookSdk.InitializeCallback() {
            @Override
            public void onInitialized() {
                mAccessToken = AccessToken.getCurrentAccessToken();
            }
        });

        setContentView(R.layout.login_screen);
        mSocketAltp = MainApplication.sockAltp();

        mAltpHelper = new AltpHelper(mSocketAltp);
        if (!mSocketAltp.isConnected()) {
            mSocketAltp.connect();
        }

        mSocketAltp.addGlobalEvent(globalCallback);
        mSocketAltp.addEvent("login", loginCallback);

        strictMode();
        findViewbyId();
        editTexConfig();
        setAvatar();
        getLocation();
        popupLogin();
        checkLogin();
        loginFB();
        loginManualClicked();
        popUpPickAvatar();
        popUpEdittex();

        if (TextUtils.isEmpty(city)) {
            city = "VIETNAM";
            mTextViewCity.setText(city);
        } else {
            mTextViewCity.setText(city);
        }
    }

    public void findViewbyId() {
        avatarDialog = new Dialog(this);
        edittexDialog = new Dialog(this);
        loginDialog = new Dialog(this);
        prgDialog = new ProgressDialog(this);
        prgDialog.setCancelable(false);
        mImageViewAvatar = (ImageView) findViewById(R.id.imageview_avatar);
        mTextViewCity = (TextView) findViewById(R.id.textview_city_login);
        mRelativeLayoutBg = (RelativeLayout) findViewById(R.id.background);
        mEditText = (EditText) findViewById(R.id.text_edit);
        mImageButtonPlay = (Button) findViewById(R.id.button_login);
        font = Typeface.createFromAsset(getAssets(), "fonts/roboto.ttf");
    }

    public void editTexConfig() {
        mEditText.setTypeface(font);
        mEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mEditText.setHint("");
                mEditText.requestFocusFromTouch();
                mEditText.setFocusableInTouchMode(true);
                InputMethodManager keyBoard =
                        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
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
                InputMethodManager keyBoard =
                        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                keyBoard.hideSoftInputFromWindow(mRelativeLayoutBg.getWindowToken(), 0);
                return false;
            }
        });
    }

    public void getLocation() {
        if (NetworkUtils.checkInternetConnection(LoginScreen.this)) {
            JSONParser jParser = new JSONParser();
            JSONObject json = jParser.getJSONFromUrl(url);
            try {
                city = json.getString(TAG_CITY).toUpperCase();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void checkLogin() {
        if (NetworkUtils.checkInternetConnection(LoginScreen.this)) {
            if (mAccessToken != null || PrefUtils.getInstance(LoginScreen.this).get(PrefUtils.KEY_LOGGED_IN, false)) {
                if (!loginDialog.isShowing()) {
                    loginDialog.show();
                }
                mUser.name = PrefUtils.getInstance(LoginScreen.this).get(PrefUtils.KEY_NAME, "");
                mUser.avatar = PrefUtils.getInstance(LoginScreen.this).get(PrefUtils.KEY_URL_AVATAR, "");
                mUser.address = city;
                sendLoginRequest(mUser);
            }
        }
    }

    public void loginFB() {
        mCallbackManager = CallbackManager.Factory.create();

        mLoginButtonFb = (LoginButton) findViewById(R.id.button_fb);
        mLoginButtonFb.setBackgroundResource(R.drawable.fbbutton);
        mLoginButtonFb.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        mLoginButtonFb.setReadPermissions("public_profile");
        mLoginButtonFb.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                //mLoginButtonFb.setVisibility(View.INVISIBLE);
                loginDialog.show();
                if (NetworkUtils.checkInternetConnection(LoginScreen.this)) {
                    mAccessTokenTracker = new AccessTokenTracker() {
                        @Override
                        protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {
                            AccessToken mAccessToken = newToken;
                        }
                    };
                    FacebookSdk.sdkInitialize(getApplicationContext(), new FacebookSdk.InitializeCallback() {
                        @Override
                        public void onInitialized() {
                            mLoginButtonFb.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                                @Override
                                public void onSuccess(LoginResult loginResult) {
                                    AccessToken mAccessToken = loginResult.getAccessToken();
                                    PrefUtils.getInstance(LoginScreen.this).set(PrefUtils.KEY_ACCESS_TOKEN_FB, mAccessToken.getToken());
                                    getUserInfoFromFb();
                                }

                                @Override
                                public void onCancel() {
                                }

                                @Override
                                public void onError(FacebookException exception) {
                                    loginDialog.hide();
                                }
                            });
                            return;

                        }
                    });

                    mAccessTokenTracker.startTracking();
                } else {
                    NetworkUtils.movePopupConnection(LoginScreen.this);
                }
                return false;
            }
        });
    }

    private void getUserInfoFromFb() {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(
                    JSONObject object, GraphResponse response) {
                // Application code
                try {
                    mUser.fbId = object.getString("id");
                    mUser.name = object.getString("name");
                    mUser.address = city;
                    mUser.avatar = "https://graph.facebook.com/" + object.getString("id") + "/picture?width=500&height=500";

                    sendLoginRequest(mUser);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,gender,name,birthday,picture.type(large)");
        request.setParameters(parameters);
        request.executeAsync();
    }

    public void loginManualClicked() {
        mImageButtonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkUtils.checkInternetConnection(LoginScreen.this)) {
                    mStringUserName = mEditText.getText().toString();
                    if (mStringUserName.length() <= 3) {
                        edittexDialog.show();
                    } else {
                        if (!isCheckPickImage) {
                            uploadImage();
                        } else {
                            isCheckBtnLater = true;
                            avatarDialog.show();
                        }
                    }
                } else {
                    NetworkUtils.movePopupConnection(LoginScreen.this);
                }
            }
        });
    }

    public void popUpPickAvatar() {
        avatarDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        avatarDialog.setContentView(R.layout.layout_avatar_popup);
        avatarDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        avatarDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        avatarDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Button btnCamera = (Button) avatarDialog.findViewById(R.id.button_camera);
        Button btnLater = (Button) avatarDialog.findViewById(R.id.button_later);
        Button btnGallery = (Button) avatarDialog.findViewById(R.id.button_gallery);

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                captureImage();
            }
        });

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadImagefromGallery();
            }
        });

        btnLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickAvatarLater();
            }
        });
    }

    public void popUpEdittex() {
        edittexDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        edittexDialog.setContentView(R.layout.layout_edittex_popup);
        edittexDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        edittexDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        edittexDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Button okay = (Button) edittexDialog.findViewById(R.id.button_okay);

        okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edittexDialog.hide();
            }
        });
    }

    public void popupLogin() {
        loginDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loginDialog.setContentView(R.layout.layout_login_popup);
        loginDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        loginDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        loginDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        ImageView loading = (ImageView) loginDialog.findViewById(R.id.imgView_loading);

        Glide.with(this).load(R.drawable.loading).asGif().into(loading);

        Glide.with(this).load(R.drawable.loading);


    }

    public void captureImage() {
        isCheckPickImage = false;
        avatarDialog.hide();
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePicture.putExtra(MediaStore.EXTRA_OUTPUT, CameraUtils.getPhotoFileUri(this, photoFileName));
        if (takePicture.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePicture, IMAGE_FROM_CAMERA);
        }
    }

    public void loadImagefromGallery() {
        isCheckPickImage = false;
        avatarDialog.hide();
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, IMAGE_FROM_GALLERY);
    }

    public void pickAvatarLater() {
        isCheckPickImage = true;
        avatarDialog.hide();
        if (!TextUtils.isEmpty(mStringUserName) && isCheckBtnLater) {
            defaultAvatarToLogin();
        }
    }

    public void setAvatar() {
        mImageViewAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isCheckBtnLater = false;
                avatarDialog.show();
            }
        });
    }

    public void defaultAvatarToLogin() {
        loginDialog.show();
        mUser.name = mStringUserName;
        mUser.address = city;
        mUser.avatar = DEFAULT_AVATAR;
        sendLoginRequest(mUser);
    }

    // When Upload button is clicked
    public void uploadImage() {
        uploadResult = uploadPhotoUtils.isUploadAvailable(imgPath);
        if (!uploadResult) {
            avatarDialog.show();
            return;
        }

        encodeAndUploadImage();

        prgDialog.setMessage("Uploading Avatar");
        prgDialog.show();
    }

    public void encodeAndUploadImage() {
        new AsyncTask<Void, Void, String>() {

            protected void onPreExecute() {
            }

            @Override
            protected String doInBackground(Void... params) {
                return uploadPhotoUtils.encodeImage();
            }

            @Override
            protected void onPostExecute(String encode) {
                uploadPhotoUtils.startUploadImage(LoginScreen.this, fileName, encode, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        loginDialog.show();
                        uploadPhotoUtils.isUploading = false;
                        try {
                            imgUrl = prefixHOST + new String(responseBody, "UTF-8").replace("\"", "").replaceAll("\\\\", File.separator);

                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        prgDialog.hide();
                        uploadFail = 4;

                        PrefUtils.getInstance(LoginScreen.this).set(PrefUtils.KEY_URL_AVATAR, imgUrl);
                        PrefUtils.getInstance(LoginScreen.this).set(PrefUtils.KEY_NAME, mStringUserName);
                        PrefUtils.getInstance(LoginScreen.this).set(PrefUtils.KEY_MONEY, 0);
                        PrefUtils.getInstance(LoginScreen.this).set(PrefUtils.KEY_LOCATION, city);

                        mUser.name = mStringUserName;
                        mUser.avatar = imgUrl;
                        mUser.address = city;
                        sendLoginRequest(mUser);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        uploadFail += 1;
                        if (uploadFail < 3) {
                            uploadImage();
                        } else {
                            imgUrl = DEFAULT_AVATAR;
                            loggedAndMoveInfoScreen();
                        }
                        Log.e("statusCode: ", "" + statusCode);
                    }

                });


            }
        }.execute(null, null, null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_FROM_CAMERA) {
                uriPhoto = CameraUtils.getPhotoFileUri(this, photoFileName);
                imgPath = uriPhoto.getPath();
                Glide.with(getApplicationContext()).load(uriPhoto).override(600, 600)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true).into(mImageViewAvatar);

                String fileNameSegments[] = imgPath.split("/");
                fileName = System.currentTimeMillis() + "_" + fileNameSegments[fileNameSegments.length - 1];

            } else if (requestCode == IMAGE_FROM_GALLERY && null != data) {
                uriPhoto = data.getData();
                Glide.with(getApplicationContext()).loadFromMediaStore(uriPhoto).override(600, 600)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true).into(mImageViewAvatar);

                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                // Get the cursor
                Cursor cursor = getContentResolver().query(uriPhoto,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgPath = cursor.getString(columnIndex);
                cursor.close();
                // Get the Image's file name
                String fileNameSegments[] = imgPath.split("/");
                fileName = System.currentTimeMillis() + "_" + fileNameSegments[fileNameSegments.length - 1];
            }
        }
    }

    public void strictMode() {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    public void loggedAndMoveInfoScreen() {
        Intent intent = new Intent(LoginScreen.this, InfoScreen.class);
        startActivity(intent);
        overridePendingTransition(R.animator.right_in, R.animator.left_out);
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (prgDialog != null) {
            prgDialog.dismiss();
        }
        loginDialog.dismiss();
        edittexDialog.dismiss();
        avatarDialog.dismiss();
    }
}