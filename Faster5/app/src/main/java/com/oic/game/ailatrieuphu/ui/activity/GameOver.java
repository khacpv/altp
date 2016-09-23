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
import com.oic.game.ailatrieuphu.util.PrefUtils;
import com.oic.game.ailatrieuphu.util.SoundPoolManager;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Kien on 07/14/2016.
 */
public class GameOver extends AppCompatActivity {

    public static final String EXTRA_SCORE = "score";
    public static final String EXTRA_WINNER = "winner";
    public static final int DRAW = -1;
    public static final int WIN = 1;
    public static final int LOSE = 0;
    public static final int GIVEUP = 2;

    ImageView mImageViewUserAvatar;
    TextView mTextViewNameUser;
    TextView mTextViewScore;
    TextView mTextViewResultText;
    TextView mTextViewCity;
    Button mButtonBack;
    private int mScore = 0;
    private int mTotalScore;
    private int mWinner;
    private String username;
    private String location;
    private String linkAvatar;
    private MediaPlayer mediaPlayer;

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
        setUserInfo();
        getBundle();
        bgMusic();
    }

    public void findViewById() {
        mButtonBack = (Button) findViewById(R.id.button_backhome);
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/roboto.ttf");

        mTextViewResultText = (TextView) findViewById(R.id.textview_result);
        mImageViewUserAvatar = (ImageView) findViewById(R.id.imageview_useravatarwin);
        mTextViewNameUser = (TextView) findViewById(R.id.textview_usernnamewin);
        mTextViewScore = (TextView) findViewById(R.id.textview_money_win);
        mTextViewCity = (TextView) findViewById(R.id.textview_city_win);

        setTypeface(font, mTextViewNameUser, mTextViewResultText, mTextViewScore, mTextViewCity);
    }

    public void setUserInfo() {
        username = PrefUtils.getInstance(GameOver.this).get(PrefUtils.KEY_NAME, "");
        location = PrefUtils.getInstance(GameOver.this).get(PrefUtils.KEY_LOCATION, "");
        linkAvatar = PrefUtils.getInstance(GameOver.this).get(PrefUtils.KEY_URL_AVATAR, "");

        Log.e("TAG", "setUserInfo: " + username + location + linkAvatar);
        mTextViewNameUser.setText(username);
        mTextViewCity.setText(location);
        Glide.with(getApplicationContext()).load(linkAvatar).into(mImageViewUserAvatar);
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

    public static Intent createIntent(Context context, int score, int isWinner) {
        Intent intent = new Intent(context, GameOver.class);
        intent.putExtra(EXTRA_SCORE, score);
        intent.putExtra(EXTRA_WINNER, isWinner);
        return intent;
    }

    private void getBundle() {
        mScore = getIntent().getIntExtra(EXTRA_SCORE, 0);
        mTextViewScore.setText(Integer.toString(mScore));
        mWinner = getIntent().getIntExtra(EXTRA_WINNER, 0);
        switch (mWinner) {
            case DRAW:
                mTextViewResultText.setText("BẤT PHÂN THẮNG BẠI");
                SoundPoolManager.getInstance().playSound(R.raw.pass_good);
                startMedia(6000);
                break;
            case LOSE:
                if (mScore == 0) {
                    mTextViewResultText.setText("THẤT BẠI ĐAU ĐỚN");
                } else {
                    mTextViewResultText.setText("CHÚC BẠN MAY MẮN");
                }
                SoundPoolManager.getInstance().playSound(R.raw.lose);
                startMedia(3000);
                break;
            case GIVEUP:
                if (mScore == 0) {
                    mTextViewResultText.setText("THẤT BẠI ĐAU ĐỚN");
                } else {
                    mTextViewResultText.setText("BẠN RẤT TỈNH TÁO");
                }
                SoundPoolManager.getInstance().playSound(R.raw.lose);
                startMedia(3000);
                break;
            case WIN:
                mTextViewResultText.setText("CHÚC MỪNG CHIẾN THẮNG");
                SoundPoolManager.getInstance().playSound(R.raw.best_player);
                startMedia(12000);
                break;
        }

        mTotalScore = PrefUtils.getInstance(GameOver.this).get(PrefUtils.KEY_TOTAL_SCORE, 0);
        mTotalScore += mScore;
        PrefUtils.getInstance(GameOver.this).set(PrefUtils.KEY_TOTAL_SCORE, mTotalScore);
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
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        mediaPlayer.start();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
