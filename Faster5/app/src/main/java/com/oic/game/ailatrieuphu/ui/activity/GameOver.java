package com.oic.game.ailatrieuphu.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.game.oic.ailatrieuphu.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.oic.game.ailatrieuphu.model.GameOverMessage;
import com.oic.game.ailatrieuphu.model.Question;
import com.oic.game.ailatrieuphu.model.Room;
import com.oic.game.ailatrieuphu.model.User;
import com.oic.game.ailatrieuphu.util.PrefUtils;
import com.oic.game.ailatrieuphu.util.SoundPoolManager;

import org.greenrobot.eventbus.EventBus;

import hotchemi.android.rate.AppRate;
import hotchemi.android.rate.OnClickButtonListener;
import hotchemi.android.rate.StoreType;

/**
 * Created by Kien on 07/14/2016.
 */
public class GameOver extends AppCompatActivity {

    private static final String EXTRA_USER = "user";
    private static final String EXTRA_ENEMY = "enemy";
    private static final String GAME_OVER_MESSAGE = "message";
    private static final String SERVER_ERR = "server_erro";
    private static final String EXTRA_ROOM = "room";
    ImageView mImageViewMyAvatar;
    ImageView mImageViewEnemyAvatar;
    TextView mTextViewMyName;
    TextView mTextViewEnemyName;
    TextView mTextViewMyScore;
    TextView mTextViewEnemyScore;
    TextView mTextViewMyCity;
    TextView mTextViewEnemyCity;
    TextView mTextViewResultText;
    Button mButtonBack;
    Button mButtonReport;
    Dialog reportDialog;
    private int mTotalScore;
    private MediaPlayer mediaPlayer;
    private boolean isMoveInfoScr = false;
    private boolean isWin = false;
    User mUser;
    User mEnemy;
    Room mRoom;
    GameOverMessage mMessage;
    private int mReport = 0;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.game_over);
        findViewById();
        getBundle();
        setUserInfo();
        bgMusic();
        setReportDialog();
        rateApp();
        mButtonReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SoundPoolManager.getInstance().playSound(R.raw.touch_sound);
                reportDialog.show();
            }
        });
    }


    public void findViewById() {
        reportDialog = new Dialog(this);
        mButtonBack = (Button) findViewById(R.id.button_backhome);
        mButtonReport = (Button) findViewById(R.id.button_report);
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/roboto.ttf");

        mTextViewResultText = (TextView) findViewById(R.id.textview_result);
        mImageViewMyAvatar = (ImageView) findViewById(R.id.imageview_useravatar1);
        mTextViewMyName = (TextView) findViewById(R.id.textview_username1);
        mTextViewMyScore = (TextView) findViewById(R.id.textview_money1);
        mTextViewMyCity = (TextView) findViewById(R.id.textview_city_user1);

        mImageViewEnemyAvatar = (ImageView) findViewById(R.id.imageview_useravatar2);
        mTextViewEnemyName = (TextView) findViewById(R.id.textview_username2);
        mTextViewEnemyScore = (TextView) findViewById(R.id.textview_money2);
        mTextViewEnemyCity = (TextView) findViewById(R.id.textview_city_user2);

        setTypeface(font, mTextViewResultText, mTextViewMyName, mTextViewMyScore, mTextViewMyCity,
                mTextViewEnemyName, mTextViewEnemyScore, mTextViewEnemyCity);

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(getString(R.string.test_device_1))
                .addTestDevice(getString(R.string.test_device_2))
                .build();
        mAdView.loadAd(adRequest);

    }


    public void bgMusic() {
        AudioManager amanager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = amanager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
        amanager.setStreamVolume(AudioManager.STREAM_ALARM, maxVolume, 0);
        mediaPlayer = MediaPlayer.create(GameOver.this, R.raw.bgmusic_gameover);
        mediaPlayer.setLooping(true);
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }

    }

    public void startMedia(int time) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    mediaPlayer.start();
                }
            }
        }, time);
    }

    public static void setTypeface(Typeface font, TextView... textviews) {
        for (TextView textView : textviews) {
            textView.setTypeface(font);
        }
    }

    public static Intent createIntent(Context context, User mUser, User enemyUser, Room mRoom,
                                      GameOverMessage mMessage, boolean serverErr) {
        Intent intent = new Intent(context, GameOver.class);
        intent.putExtra(EXTRA_USER, mUser);
        intent.putExtra(EXTRA_ENEMY, enemyUser);
        intent.putExtra(EXTRA_ROOM, mRoom);
        intent.putExtra(GAME_OVER_MESSAGE, mMessage);
        intent.putExtra(SERVER_ERR, serverErr);
        return intent;
    }

    private void getBundle() {
        mUser = (User) getIntent().getSerializableExtra(EXTRA_USER);
        mEnemy = (User) getIntent().getSerializableExtra(EXTRA_ENEMY);
        mMessage = (GameOverMessage) getIntent().getSerializableExtra(GAME_OVER_MESSAGE);
        mRoom = (Room) getIntent().getSerializableExtra(EXTRA_ROOM);
        boolean isServerErr = getIntent().getBooleanExtra(SERVER_ERR, false);

        if (mUser.score == mEnemy.score) {
            SoundPoolManager.getInstance().playSound(R.raw.pass_good);
            startMedia(6000);
        } else if (mUser.score < mEnemy.score) {
            SoundPoolManager.getInstance().playSound(R.raw.lose);
            startMedia(3000);
        } else if (mUser.score > mEnemy.score) {
            SoundPoolManager.getInstance().playSound(R.raw.best_player);
            startMedia(12000);
        }
        if (!isServerErr) {
            if (mUser.score == mEnemy.score) {
                mTextViewResultText.setText(mMessage.draw);
            } else if (mUser.score < mEnemy.score) {
                mTextViewResultText.setText(mMessage.lose);
            } else if (mUser.score > mEnemy.score) {
                isWin = true;
                mTextViewResultText.setText(mMessage.win);
            }
        } else {
            mTextViewResultText.setText(getResources().getString(R.string.disconnect_server));
        }

        PrefUtils.getInstance(GameOver.this).set(PrefUtils.KEY_TOTAL_SCORE, mUser.totalScore);
    }

    public void setUserInfo() {
        mTextViewMyName.setText(mUser.name);
        mTextViewMyCity.setText(mUser.address);
        mTextViewMyScore.setText("" + mUser.score);
        Glide.with(getApplicationContext()).load(mUser.avatar).fitCenter()
                .placeholder(R.drawable.avatar_default).dontAnimate()
                .error(R.drawable.avatar_default).into(mImageViewMyAvatar);


        mTextViewEnemyName.setText(mEnemy.name);
        mTextViewEnemyCity.setText(mEnemy.address);
        mTextViewEnemyScore.setText("" + mEnemy.score);
        Glide.with(getApplicationContext()).load(mEnemy.avatar).fitCenter()
                .placeholder(R.drawable.avatar_default).dontAnimate()
                .error(R.drawable.avatar_default).into(mImageViewEnemyAvatar);
    }

    public void rateApp() {
        /**
         * https://github.com/hotchemi/Android-Rate
         * */

        //custom view ratedialog
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_report_popup, null);
        AppRate.with(this)
                .setStoreType(StoreType.GOOGLEPLAY) //default is Google, other option is Amazon
                .setInstallDays(0) // default 10, 0 means install day.
                .setLaunchTimes(10) // default 10 times.
                .setRemindInterval(0) // default 1 day.
                .setShowLaterButton(true) // default true.
                .setDebug(true) // default false.
                .setCancelable(false) // default false.
                .setShowNeverButton(false)
                .setOnClickButtonListener(new OnClickButtonListener() { // callback listener.
                    @Override
                    public void onClickButton(int which) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AppRate.with(GameOver.this).clearAgreeShowDialog();
                            }
                        });
                        intentMoveInfo();
                    }
                })
               // .setView(view)
                .setTitle(R.string.my_own_title)
                .setTextLater(R.string.my_own_cancel)
                .setTextRateNow(R.string.my_own_rate)
                .monitor();
    }

    public void backInfo(View view) {
        if (isWin) {
            AppRate.showRateDialogIfMeetsConditions(this);
            return;
        }
        AppRate.showRateDialogIfMeetsConditions(this);
       // intentMoveInfo();
    }

    public void intentMoveInfo() {
        if (!isFinishing() && !isMoveInfoScr) {
            isMoveInfoScr = true;
            SoundPoolManager.getInstance().playSound(R.raw.touch_sound);
            mediaPlayer.stop();
            Intent intent = new Intent(getApplicationContext(), InfoScreen.class);
            startActivity(intent);
            overridePendingTransition(R.animator.in_from_left, R.animator.out_to_right);
            finish();
        }
    }

    public void setReportDialog() {
        reportDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        reportDialog.setContentView(R.layout.layout_report_popup);
        reportDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        reportDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        reportDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        reportDialog.setCancelable(false);

        Button report = (Button) reportDialog.findViewById(R.id.button_report);
        final Button cancel = (Button) reportDialog.findViewById(R.id.button_cancel);

        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mReport == 0) {
                    String jsonString = new Gson().toJson(mRoom.questions.get(mRoom.questionIndex));
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("report/" + System.currentTimeMillis() + mUser.id);
                    myRef.setValue(jsonString);
                    Toast.makeText(GameOver.this, getResources().getString(R.string.noti_report),
                            Toast.LENGTH_SHORT).show();
                }
                if (mReport == 1) {
                    Toast.makeText(GameOver.this, getResources().getString(R.string.noti_report_2),
                            Toast.LENGTH_SHORT).show();
                }
                cancel.performClick();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reportDialog.hide();
                mReport = 1;
            }
        });
    }

    public void onBackPressed() {
    }

    @Override
    protected void onPause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
        if (SoundPoolManager.getInstance().isPlaySound()) {
            SoundPoolManager.getInstance().stop();
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
    protected void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (reportDialog != null) {
            reportDialog.dismiss();
        }
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
