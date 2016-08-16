package com.example.gcs.faster5.ui.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.gcs.faster5.MainApplication;
import com.example.gcs.faster5.R;
import com.example.gcs.faster5.model.Room;
import com.example.gcs.faster5.model.User;
import com.example.gcs.faster5.sock.SockAltp;
import com.example.gcs.faster5.sock.AltpHelper;
import com.example.gcs.faster5.util.CameraUtils;
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
import java.util.ArrayList;
import java.util.List;

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
    public static final int IMAGE_FROM_CAMERA = 0;
    public static final int IMAGE_FROM_GALLERY = 1;
    public static final String prefixHOST = "http://ailatrieuphu.96.lt/imgupload/";
    final Context context = this;
    AccessTokenTracker mAccessTokenTracker;
    RelativeLayout mRelativeLayoutBg;
    EditText mEditText;
    String mStringUserName;
    Button mImageButtonPlay;
    TextView mTextViewCity;
    Typeface font;
    ImageView mImageViewAvatar;
    private LoginButton mLoginButtonFb;
    private CallbackManager mCallbackManager;
    private SockAltp mSocketAltp;
    private AltpHelper mAltpHelper;
    private User mUser = new User();
    ProgressDialog prgDialog;
    String imgPath, fileName, imgUrl;
    Uri uriPhoto;
    ImageView imgView;
    String photoFileName = "cameraphoto.jpg";
    boolean uploadResult;
    UploadPhotoUtils uploadPhotoUtils = new UploadPhotoUtils();
    int uploadFail = 0;

    /**
     * global events
     */
    private SockAltp.OnSocketEvent globalCallback = new SockAltp.OnSocketEvent() {
        @Override
        public void onEvent(String event, Object... args) {
            switch (event) {
                case Socket.EVENT_CONNECT:  // auto call on connect to server
                    // send login
                    try {
                        JSONObject data = new JSONObject("{user: {name:\"khac\"," +
                                "address:\"Hanoi\",fbId:\"12315\"}}");
                        mSocketAltp.send("login", data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mUser.name = "Han Thuy";
                    mUser.address = "Vung tau";
                    mUser.fbId = "123123";
                    mUser.avatar =
                            "http://fullhdpictures.com/wp-content/uploads/2016/01/Most-Beautiful-Face-Girl-Wallpaper.jpg";
                    mAltpHelper.login(mUser);
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
            PrefUtils.getInstance(LoginScreen.this).set(PrefUtils.KEY_URL_AVATAR, mUser.id);
            PrefUtils.getInstance(LoginScreen.this).set(PrefUtils.KEY_URL_AVATAR, mUser.avatar);
            PrefUtils.getInstance(LoginScreen.this).set(PrefUtils.KEY_NAME, mUser.name);
            PrefUtils.getInstance(LoginScreen.this).set(PrefUtils.KEY_LOGGED_IN, true);

            loggedAndMoveInfoScreen();
        }
    };

    public void guiThongTin(User user) {
        this.mUser = user;
        mAltpHelper.login(mUser);
        Log.e("guiThongTin", mUser.fbId + " " + mUser.name + " " + mUser.address + "\n" + mUser.avatar);
    }

    private SockAltp.OnSocketEvent searchCallback = new SockAltp.OnSocketEvent() {
        @Override
        public void onEvent(String event, Object... args) {
            Pair<Room, ArrayList<User>> result = mAltpHelper.searchCallback(args);
            Room room = result.first;
            List<User> dummyUsers = result.second;

            Log.e("TAG", "join room: " + room.roomId);
            Log.e("TAG", "dummy user: " + dummyUsers.size());
        }
    };

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
        parserJSON();
        loginFB();
        loginManualClicked();

        if (TextUtils.isEmpty(city)) {
            city = "VIETNAM";
            mTextViewCity.setText(city);
        } else {
            mTextViewCity.setText(city.toUpperCase());
        }
    }

    public void setAvatar() {
        mImageViewAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerForContextMenu(view);
                view.showContextMenu();
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, IMAGE_FROM_CAMERA, 0, "Camera");
        menu.add(0, IMAGE_FROM_GALLERY, 0, "Gallery");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case IMAGE_FROM_CAMERA:
                captureImage();
                break;
            case IMAGE_FROM_GALLERY:
                loadImagefromGallery();
                break;
        }
        return false;
    }

    public void findViewbyId() {
        prgDialog = new ProgressDialog(this);
        prgDialog.setCancelable(false);
        mImageViewAvatar = (ImageView) findViewById(R.id.imageview_avatar);
        mTextViewCity = (TextView) findViewById(R.id.textview_city_login);
        mRelativeLayoutBg = (RelativeLayout) findViewById(R.id.background);
        mEditText = (EditText) findViewById(R.id.text_edit);
        mImageButtonPlay = (Button) findViewById(R.id.button_login);
        font = Typeface.createFromAsset(getAssets(), "fonts/roboto.ttf");
    }

    public void captureImage() {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePicture.putExtra(MediaStore.EXTRA_OUTPUT, CameraUtils.getPhotoFileUri(this, photoFileName));
        if (takePicture.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePicture, IMAGE_FROM_CAMERA);
        }
    }

    public void loadImagefromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, IMAGE_FROM_GALLERY);
    }


    // When Upload button is clicked
    public void uploadImage() {
        uploadResult = uploadPhotoUtils.isUploadAvailable(imgPath);
        if (!uploadResult) {
            Toast.makeText(getApplicationContext(), "Bạn chưa chọn ảnh?", Toast.LENGTH_LONG).show();
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
                        guiThongTin(mUser);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        uploadFail += 1;
                        if (uploadFail < 3) {
                            uploadImage();
                        } else {
                            imgUrl = "";
                            loggedAndMoveInfoScreen();
                        }
                        Log.e("statusCode: ", "" + statusCode);
                    }

                });


            }
        }.execute(null, null, null);
    }

    public void editTexConfig() {
        mEditText.setTypeface(font);
        mEditText.setHint("Choose username");
        mEditText.setFocusableInTouchMode(false);
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

    public void parserJSON() {
        if (NetworkUtils.checkInternetConnection(LoginScreen.this)) {
            JSONParser jParser = new JSONParser();
            JSONObject json = jParser.getJSONFromUrl(url);
            try {
                city = json.getString(TAG_CITY);
                Log.e("CITY", " " + city);
            } catch (JSONException e) {
                e.printStackTrace();
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

                                    Log.e("Fb", mUser.fbId + " " + mUser.name + " " + mUser.address + "\n" + mUser.avatar);

                                }

                                @Override
                                public void onCancel() {
                                }

                                @Override
                                public void onError(FacebookException exception) {
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
                    guiThongTin(mUser);

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
                    if (PrefUtils.getInstance(LoginScreen.this).get(PrefUtils.KEY_LOGGED_IN, false)) {
                        parserJSON();
                        loggedAndMoveInfoScreen();
                        return;
                    } else {
                        mStringUserName = mEditText.getText().toString();
                        if (mStringUserName.length() <= 3) {
                            AlertDialog alertDialogLogin =
                                    new AlertDialog.Builder(context).create();
                            alertDialogLogin.setMessage("Username incorrect. Username must be at "
                                    + "least 4 characters!");
                            alertDialogLogin.setCancelable(false);
                            alertDialogLogin.setButton("Try Again",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    });
                            alertDialogLogin.show();
                        } else {
                            uploadImage();
                        }
                    }
                } else {
                    NetworkUtils.movePopupConnection(LoginScreen.this);
                }
            }
        });
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
        mSocketAltp.disconnect();
        if (prgDialog != null) {
            prgDialog.dismiss();

        }
    }
}