package com.oic.game.ailatrieuphu.ui.activity;


import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.game.oic.ailatrieuphu.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.oic.game.ailatrieuphu.MainApplication;
import com.oic.game.ailatrieuphu.model.User;
import com.oic.game.ailatrieuphu.sock.AltpHelper;
import com.oic.game.ailatrieuphu.sock.SockAltp;
import com.oic.game.ailatrieuphu.util.CameraUtils;
import com.oic.game.ailatrieuphu.util.JSONParser;
import com.oic.game.ailatrieuphu.util.NetworkUtils;
import com.oic.game.ailatrieuphu.util.PrefUtils;
import com.oic.game.ailatrieuphu.util.SoundPoolManager;
import com.oic.game.ailatrieuphu.util.UploadPhotoUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by Kien on 07/05/2016.
 */
public class LoginScreen extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG_CITY = "city";
    private static final String photoFileName = "cameraphoto.jpg";
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int REQUEST_READ_PHONE_STATE = 2;
    public static final int IMAGE_FROM_CAMERA = 0;
    public static final int IMAGE_FROM_GALLERY = 1;
    public static final String FIRE_BASE = "gs://ai-la-trieu-phu-online.appspot.com/avatar";
    public static final String DEFAULT_AVATAR = "https://firebasestorage.googleapis.com/v0/b/ai-la-trieu-phu-online.appspot.com/o/avatar%2Favatar.png?alt=media&token=24133aa8-e81e-4019-bdb9-c6c5e49c6ff3";
    private static String city;
    private static String url = "http://209.58.180.196/json/"; //URL to get CITY from JSON Array
    private AccessTokenTracker mAccessTokenTracker;
    private RelativeLayout mRelativeLayoutBg;
    private EditText mEditText;
    private Button mImageButtonPlay;
    private LoginButton mLoginButtonFb;
    private Button mButtonFakeFb;
    private TextView mTextViewCity;
    private Typeface font;
    private ImageView mImageViewAvatar;
    private CallbackManager mCallbackManager;
    private SockAltp mSocketAltp;
    private AltpHelper mAltpHelper;
    private User mUser = new User();
    private ProgressDialog prgDialog;
    private Dialog avatarDialog;
    private Dialog edittexDialog;
    private Dialog loginDialog;
    private Dialog connectionDialog;
    private String mStringUserName;
    private String imgPath;
    private String imgUrl;
    private Uri uriPhoto;
    AccessToken mAccessToken;
    MediaPlayer mediaPlayer;
    private boolean uploadResult;
    private boolean isCheckPickImage = false;
    private boolean isCheckBtnLater = true;
    private boolean isMoveInfo = false;
    private boolean reward = false;
    private boolean isCamera = false;
    private UploadPhotoUtils uploadPhotoUtils = new UploadPhotoUtils();
    private int uploadFail = 0;

    /**
     * global events
     */

    private SockAltp.OnSocketEvent loginCallback = new SockAltp.OnSocketEvent() {
        @Override
        public void onEvent(String event, Object... args) {
            if (isMoveInfo) {
                return;
            }
            OnLoginCallbackEvent eventBus = new OnLoginCallbackEvent();
            User user = mAltpHelper.loginCallback(args);
            reward = mAltpHelper.loginCallbackCheckReward(args);
            Log.e("TAG", "reward: " + reward);
            eventBus.user = user;
            EventBus.getDefault().post(eventBus);
        }
    };

    public void sendLoginRequest(User user) {
        this.mUser = user;

        this.mUser.fcmToken = PrefUtils.getInstance(this).get(PrefUtils.KEY_FCM, "");

        this.mUser.id = NetworkUtils.getUniqueID(this).replaceAll("-", "");

        mAltpHelper.login(mUser);
        Log.e("TAG", "loginRequest: " + mUser.fbId + " " + mUser.name + " " + mUser.address + "\n" + mUser.avatar);
    }


    @Subscribe
    public void onEventMainThread(OnLoginCallbackEvent event) {
        User user = event.user;

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
        PrefUtils.getInstance(LoginScreen.this).set(PrefUtils.KEY_TOTAL_SCORE, mUser.totalScore);
        PrefUtils.getInstance(LoginScreen.this).set(PrefUtils.KEY_LOGGED_IN, true);
        Log.e("TAG", "LoginCallback: " + user.totalScore + user.fbId + " " + user.id + " " + user.name + " " + user.address + " " + "\n" + user.avatar);
        if (!isFinishing() && !isMoveInfo) {
            isMoveInfo = true;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loggedAndMoveInfoScreen();
                }

            });
        }
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
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        new Thread(new Runnable() {
            @Override
            public void run() {
                bgMusic();
            }
        }).start();

        EventBus.getDefault().register(this);

        mSocketAltp = MainApplication.sockAltp();

        mAltpHelper = new AltpHelper(mSocketAltp);

        if (!mSocketAltp.isConnected()) {
            mSocketAltp.connect();
        }
        mSocketAltp.addEvent("login", loginCallback);

        strictMode();
        findViewbyId();
        editTexConfig();
        getLocation();
        setLoginDialog();
        loginFB();
        setPickAvatarDialog();
        setEdittexDialog();
        setConnectDialog();

        PrefUtils.getInstance(LoginScreen.this).set(PrefUtils.KEY_FIRST_USE, true);

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
        connectionDialog = new Dialog(this);
        prgDialog = new ProgressDialog(this);
        prgDialog.setCancelable(false);
        font = Typeface.createFromAsset(getAssets(), "fonts/roboto.ttf");
        mImageViewAvatar = (ImageView) findViewById(R.id.imageview_avatar);
        mTextViewCity = (TextView) findViewById(R.id.textview_city_login);
        mTextViewCity.setTypeface(font);
        mRelativeLayoutBg = (RelativeLayout) findViewById(R.id.background);
        mEditText = (EditText) findViewById(R.id.text_edit);
        mEditText.setTypeface(font);
        mLoginButtonFb = (LoginButton) findViewById(R.id.button_fb);
        mImageButtonPlay = (Button) findViewById(R.id.button_login);
        mButtonFakeFb = (Button) findViewById(R.id.btn_fakefb);

        mButtonFakeFb.setOnClickListener(this);
        mImageViewAvatar.setOnClickListener(this);
        mImageButtonPlay.setOnClickListener(this);
    }

    public void editTexConfig() {
        mEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                playSound(R.raw.touch_sound);
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
                city = "VIETNAM";
            }
        }
    }

    public void fakeBtnFb() {
        playSound(R.raw.touch_sound);
        if (NetworkUtils.checkInternetConnection(LoginScreen.this) && mSocketAltp.isConnected()) {
            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                Log.e("TAG", "fakeBtnFb: get Permission");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
            } else {
                mLoginButtonFb.performClick();
            }

        } else {
            connectionDialog.show();
        }
    }

    public void loginFB() {
        mCallbackManager = CallbackManager.Factory.create();

        //mLoginButtonFb.setBackgroundResource(R.drawable.fbbutton);
        //mLoginButtonFb.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        mLoginButtonFb.setVisibility(View.GONE);
        mLoginButtonFb.setReadPermissions("public_profile");
        mLoginButtonFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLoginButtonFb.setClickable(false);
                loginDialog.show();
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
                                loginDialog.hide();
                                mLoginButtonFb.setClickable(true);
                            }

                            @Override
                            public void onError(FacebookException exception) {
                                mLoginButtonFb.setClickable(true);
                                loginDialog.hide();
                            }
                        });
                    }
                });

                mAccessTokenTracker.startTracking();
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
                    mUser.avatar = "https://graph.facebook.com/" + object.getString("id") + "/picture?width=200&height=200";

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

    public void loginManual() {
        playSound(R.raw.touch_sound);
        if (NetworkUtils.checkInternetConnection(LoginScreen.this) && mSocketAltp.isConnected()) {
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
            connectionDialog.show();
        }
    }

    public void setPickAvatarDialog() {
        avatarDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        avatarDialog.setContentView(R.layout.layout_avatar_popup);
        avatarDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        avatarDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        avatarDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        avatarDialog.setCancelable(false);

        Button btnCamera = (Button) avatarDialog.findViewById(R.id.button_camera);
        Button btnLater = (Button) avatarDialog.findViewById(R.id.button_later);
        Button btnGallery = (Button) avatarDialog.findViewById(R.id.button_gallery);

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playSound(R.raw.touch_sound);
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                    if (checkPermission()) {
                        captureImage();
                    } else {
                        isCamera = true;
                        requestPermission();
                    }
                } else {
                    captureImage();
                }
            }
        });

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playSound(R.raw.touch_sound);
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                    if (checkPermission()) {
                        loadImagefromGallery();
                    } else {
                        isCamera = false;
                        requestPermission();
                    }
                } else {
                    loadImagefromGallery();

                }
            }
        });

        btnLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playSound(R.raw.touch_sound);
                pickAvatarLater();
            }
        });
    }

    public void setEdittexDialog() {
        edittexDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        edittexDialog.setContentView(R.layout.layout_edittex_popup);
        edittexDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        edittexDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        edittexDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        edittexDialog.setCancelable(false);

        Button okay = (Button) edittexDialog.findViewById(R.id.button_okay);

        okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                playSound(R.raw.touch_sound);
                edittexDialog.hide();

            }
        });
    }

    public void setLoginDialog() {
        loginDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loginDialog.setContentView(R.layout.layout_login_popup);
        loginDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        loginDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        loginDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        loginDialog.setCancelable(false);

        ImageView loading = (ImageView) loginDialog.findViewById(R.id.imgView_loading);

        Glide.with(this).load(R.drawable.loading).asGif().into(loading);

    }

    public void setConnectDialog() {
        connectionDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        connectionDialog.setContentView(R.layout.layout_popup_connection);
        connectionDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        connectionDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        connectionDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        connectionDialog.setCancelable(false);

        Button tryAgain = (Button) connectionDialog.findViewById(R.id.btn_tryagain);

        tryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                playSound(R.raw.touch_sound);
                connectionDialog.hide();

            }
        });

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
        playSound(R.raw.touch_sound);
        isCheckBtnLater = false;
        avatarDialog.show();
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
        uploadAvatarToFireBase();
    }

    /**
     * //Upload avatar to firebase server
     */
    public void uploadAvatarToFireBase() {
        //Check if google play services is up to date
        final int playServicesStatus = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if (playServicesStatus != ConnectionResult.SUCCESS) {
            //If google play services in not available show an error dialog and return
            final Dialog errorDialog = GoogleApiAvailability.getInstance().getErrorDialog(this, playServicesStatus, 0, null);
            errorDialog.show();
            return;
        }
        prgDialog.setMessage("Uploading Avatar");
        prgDialog.show();
        CameraUtils.autoRotateImage(imgPath);
        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(FIRE_BASE);
        String androidID = NetworkUtils.getUniqueID(this).replaceAll("-", "");
        StorageReference imagesRef = storageRef.child(androidID + "_" + System.currentTimeMillis());
        Uri file = Uri.fromFile(new File(imgPath));
        UploadTask uploadTask = imagesRef.putFile(file);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                uploadFail += 1;
                if (uploadFail < 3) {
                    uploadImage();
                } else {
                    imgUrl = DEFAULT_AVATAR;
                    mUser.name = mStringUserName;
                    mUser.address = city;
                    mUser.avatar = imgUrl;
                    sendLoginRequest(mUser);
                }
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                String imgUrl = taskSnapshot.getDownloadUrl().toString();
                loginDialog.show();
                uploadPhotoUtils.isUploading = false;
                prgDialog.hide();
                uploadFail = 4;

                PrefUtils.getInstance(LoginScreen.this).set(PrefUtils.KEY_URL_AVATAR, imgUrl);
                PrefUtils.getInstance(LoginScreen.this).set(PrefUtils.KEY_NAME, mStringUserName);
                PrefUtils.getInstance(LoginScreen.this).set(PrefUtils.KEY_TOTAL_SCORE, 0);
                PrefUtils.getInstance(LoginScreen.this).set(PrefUtils.KEY_LOCATION, city);

                mUser.name = mStringUserName;
                mUser.address = city;
                mUser.avatar = imgUrl;
                sendLoginRequest(mUser);
            }
        });
    }

    public void bgMusic() {
        AudioManager amanager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = amanager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
        amanager.setStreamVolume(AudioManager.STREAM_ALARM, maxVolume, 0);
        mediaPlayer = MediaPlayer.create(LoginScreen.this, R.raw.bgmusic);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_fakefb:
                fakeBtnFb();
                break;
            case R.id.imageview_avatar:
                setAvatar();
                break;
            case R.id.button_login:
                loginManual();
                break;
            default:
                break;
        }

    }

    public static class OnLoginCallbackEvent {
        User user;
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

                // Get the Image's file name for Upload file to PHP server
                // String fileNameSegments[] = imgPath.split("/");
                //fileName = System.currentTimeMillis() + "_" + fileNameSegments[fileNameSegments.length - 1];


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

                // Get the Image's file name for Upload file to PHP server
                //String fileNameSegments[] = imgPath.split("/");
                //fileName = System.currentTimeMillis() + "_" + fileNameSegments[fileNameSegments.length - 1];

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
        Intent loginScrnIntent = InfoScreen.createIntent(LoginScreen.this, reward);
        startActivity(loginScrnIntent);
        overridePendingTransition(R.animator.right_in, R.animator.left_out);
        finish();
    }

    public void playSound(int SoundId) {
        if (SoundPoolManager.getInstance() != null) {
            SoundPoolManager.getInstance().playSound(SoundId);
        }
    }

    private boolean checkPermission() {
        int resultWrite = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int resultRead = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        int resultCamera = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA);
        int resultPhoneState = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE);
        if (resultWrite == PackageManager.PERMISSION_GRANTED && resultCamera == PackageManager.PERMISSION_GRANTED
                && resultRead == PackageManager.PERMISSION_GRANTED && resultPhoneState ==
                PackageManager.PERMISSION_GRANTED) {
            return true;

        } else {
            return false;

        }
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)
                && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)) {

            captureImage();
        } else {

            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE
            }, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager
                        .PERMISSION_GRANTED && grantResults[3] == PackageManager
                        .PERMISSION_GRANTED) {
                    if (isCamera) {
                        captureImage();
                    } else {
                        loadImagefromGallery();
                    }
                }
                break;
            case REQUEST_READ_PHONE_STATE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    mLoginButtonFb.performClick();
                }
                break;

            default:
                break;

        }
    }

    @Override
    protected void onPause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
        if (SoundPoolManager.getInstance() != null) {
            if (SoundPoolManager.getInstance().isPlaySound()) {
                SoundPoolManager.getInstance().stop();
            }
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
        super.onResume();
    }

    @Override
    public void onDestroy() {
        if (prgDialog != null) {
            prgDialog.dismiss();
        }
        if (loginDialog != null) {
            loginDialog.dismiss();
        }
        if (edittexDialog != null) {
            edittexDialog.dismiss();
        }
        if (avatarDialog != null) {
            avatarDialog.dismiss();
        }
        if (connectionDialog != null) {
            connectionDialog.dismiss();
        }
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        super.onDestroy();
    }
}