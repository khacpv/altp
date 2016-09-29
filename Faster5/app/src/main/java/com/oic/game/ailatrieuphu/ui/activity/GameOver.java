package com.oic.game.ailatrieuphu.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.oic.game.ailatrieuphu.R;
import com.oic.game.ailatrieuphu.model.GameOverMessage;
import com.oic.game.ailatrieuphu.model.User;
import com.oic.game.ailatrieuphu.util.PrefUtils;
import com.oic.game.ailatrieuphu.util.SoundPoolManager;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Kien on 07/14/2016.
 */
public class GameOver extends AppCompatActivity {

    private static final String EXTRA_USER = "user";
    private static final String EXTRA_ENEMY = "enemy";
    private static final String GAME_OVER_MESSAGE = "message";
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
    private int mTotalScore;
    private MediaPlayer mediaPlayer;
    User mUser;
    User mEnemy;
    GameOverMessage mMessage;

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
    }

    public void findViewById() {
        mButtonBack = (Button) findViewById(R.id.button_backhome);
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

    public static Intent createIntent(Context context, User mUser, User enemyUser, GameOverMessage mMessage) {
        Intent intent = new Intent(context, GameOver.class);
        intent.putExtra(EXTRA_USER, mUser);
        intent.putExtra(EXTRA_ENEMY, enemyUser);
        intent.putExtra(GAME_OVER_MESSAGE, mMessage);
        return intent;
    }

    private void getBundle() {
        mUser = (User) getIntent().getSerializableExtra(EXTRA_USER);
        mEnemy = (User) getIntent().getSerializableExtra(EXTRA_ENEMY);
        mMessage = (GameOverMessage) getIntent().getSerializableExtra(GAME_OVER_MESSAGE);

        if (mUser.score == mEnemy.score) {
            mTextViewResultText.setText(mMessage.draw);
            SoundPoolManager.getInstance().playSound(R.raw.pass_good);
            startMedia(6000);
        } else if (mUser.score < mEnemy.score) {
            mTextViewResultText.setText(mMessage.lose);
            SoundPoolManager.getInstance().playSound(R.raw.lose);
            startMedia(3000);
        } else if (mUser.score > mEnemy.score) {
            mTextViewResultText.setText(mMessage.win);
            SoundPoolManager.getInstance().playSound(R.raw.best_player);
            startMedia(12000);
        }

        mTotalScore = PrefUtils.getInstance(GameOver.this).get(PrefUtils.KEY_TOTAL_SCORE, 0);
        mTotalScore = mUser.totalScore + mUser.score;
        PrefUtils.getInstance(GameOver.this).set(PrefUtils.KEY_TOTAL_SCORE, mTotalScore);
    }

    public void setUserInfo() {
        mTextViewMyName.setText(mUser.name);
        mTextViewMyCity.setText(mUser.address);
        mTextViewMyScore.setText("" + mUser.score);
        Glide.with(getApplicationContext()).load(mUser.avatar).fitCenter().placeholder(R.drawable.avatar_default)
                .error(R.drawable.avatar_default).into(mImageViewMyAvatar);


        mTextViewEnemyName.setText(mEnemy.name);
        mTextViewEnemyCity.setText(mEnemy.address);
        mTextViewEnemyScore.setText("" + mEnemy.score);
        Glide.with(getApplicationContext()).load(mEnemy.avatar).fitCenter().placeholder(R.drawable.avatar_default)
                .error(R.drawable.avatar_default).into(mImageViewEnemyAvatar);
    }

    public void backInfo(View view) {
        if (!isFinishing()) {
            SoundPoolManager.getInstance().playSound(R.raw.touch_sound);
            mediaPlayer.stop();
            Intent intent = new Intent(getApplicationContext(), InfoScreen.class);
            startActivity(intent);
            overridePendingTransition(R.animator.in_from_left, R.animator.out_to_right);
            finish();
        }
    }

    public void onBackPressed() {
    }


    @Override
    protected void onPause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
        if (SoundPoolManager.getInstance().isPlaySound()) {
            SoundPoolManager.getInstance().stop();
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
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
