package com.oic.game.ailatrieuphu.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.oic.game.ailatrieuphu.R;
import com.bumptech.glide.Glide;
import com.oic.game.ailatrieuphu.MainApplication;
import com.oic.game.ailatrieuphu.model.Question;
import com.oic.game.ailatrieuphu.model.Room;
import com.oic.game.ailatrieuphu.model.User;
import com.oic.game.ailatrieuphu.sock.AltpHelper;
import com.oic.game.ailatrieuphu.sock.SockAltp;
import com.oic.game.ailatrieuphu.util.NetworkUtils;
import com.oic.game.ailatrieuphu.util.SoundPoolManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by Kien on 07/12/2016.
 */
public class SearchOpponent extends AppCompatActivity {

    private static final String EXTRA_USER = "user";
    private static final String EXTRA_ENEMY = "enemy";
    private static final String EXTRA_ROOM = "room";
    private SockAltp mSocketAltp;
    private AltpHelper mAltpHelper;
    private User mUser = new User();
    private User enemyUser = new User();
    private Room mRoom = new Room();
    private Dialog waitDialog;
    Intent intent;
    private TextView mTextViewCityUser1;
    private TextView mTextViewCityUser2;
    private TextView mTextViewUserName1;
    private TextView mTextViewUserName2;
    private TextView mTextViewScore1;
    private TextView mTextViewScore2;
    private TextView mTextViewReady;
    private TextView mTextViewWaitText;
    private ImageView mImageViewUserAvatar1;
    private ImageView mImageViewUserAvatar2;
    Button mButtonPlay;
    Button mButtonSeach;
    Button btnCancel;
    Handler handler = new Handler();
    Runnable runMovePlayScr;
    private boolean isCancel = false;
    private boolean isMovePlayScr = false;
    private boolean notReady;

    private SockAltp.OnSocketEvent playCallback = new SockAltp.OnSocketEvent() {
        @Override
        public void onEvent(String event, Object... args) {
            if (isCancel || isMovePlayScr) {
                return;
            }
            OnPlayCallbackEvent eventBus = new OnPlayCallbackEvent();

            notReady = mAltpHelper.playCallbackReady(args);

            Question mQuestion = mAltpHelper.playCallbackQuestion(args);

            eventBus.notReady = notReady;
            eventBus.mQuestion = mQuestion;
            EventBus.getDefault().post(eventBus);

        }
    };

    public static class OnPlayCallbackEvent {
        boolean notReady;
        Question mQuestion;
    }

    public static Intent createIntent(Context context, User user, User enemy, Room room) {
        Intent intent = new Intent(context, SearchOpponent.class);
        intent.putExtra(EXTRA_USER, user);
        intent.putExtra(EXTRA_ENEMY, enemy);
        intent.putExtra(EXTRA_ROOM, room);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.search_opponent);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        SoundPoolManager.getInstance().playSound(R.raw.search_opponent);
        getBundle();
        EventBus.getDefault().register(this);
        mSocketAltp = MainApplication.sockAltp();
        mAltpHelper = new AltpHelper(mSocketAltp);
        if (!mSocketAltp.isConnected()) {
            mSocketAltp.connect();
        }
        mSocketAltp.addEvent("play", playCallback);
        findViewById();
        setWaitDialog();
        setInfoUser();
    }

    @Subscribe
    public void onEventMainThread(OnPlayCallbackEvent event) {
        notReady = event.notReady;

        if (notReady) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!isFinishing()) {
                        waitDialog.show();
                    }
                }
            });
            Log.e("TAG", "waiting for other players ready.");
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnCancel.setVisibility(View.GONE);
                mTextViewWaitText.setVisibility(View.GONE);
            }
        });

        mTextViewReady.getHandler().post(new Runnable() {
            @Override
            public void run() {
                mTextViewReady.setText(getResources().getString(R.string.ready));
                if (Build.VERSION.SDK_INT < 23) {
                    mTextViewReady.setTextColor(getResources().getColor(R.color.RED));
                } else {
                    mTextViewReady.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.RED));
                }

            }
        });

        Question mQuestion = event.mQuestion;
        final Intent playScrnIntent = PlayScreen.createIntent(SearchOpponent.this, mUser, enemyUser, mRoom, mQuestion);
        runMovePlayScr = new Runnable() {
            @Override
            public void run() {
                startActivity(playScrnIntent);
                overridePendingTransition(R.animator.right_in, R.animator.left_out);
                finish();
            }
        };

        if (!isFinishing() && !isMovePlayScr) {
            SoundPoolManager.getInstance().playSound(R.raw.ready);
            isMovePlayScr = true;
            handler.postDelayed(runMovePlayScr, 5000);
        }

    }

    /**
     * get data from previous activity
     */
    private void getBundle() {
        mUser = (User) getIntent().getSerializableExtra(EXTRA_USER);
        enemyUser = (User) getIntent().getSerializableExtra(EXTRA_ENEMY);
        mRoom = (Room) getIntent().getSerializableExtra(EXTRA_ROOM);
    }

    public void setInfoUser() {
        // my info
        mTextViewUserName1.setText(mUser.name);
        Glide.with(getApplicationContext()).load(mUser.avatar).fitCenter()
                .placeholder(R.drawable.avatar_default).dontAnimate()
                .error(R.drawable.avatar_default).into(mImageViewUserAvatar1);
        mTextViewCityUser1.setText(mUser.address);
        mTextViewScore1.setText("" + mUser.totalScore);

        // enemy user
        mTextViewUserName2.setText(enemyUser.name);
        Glide.with(getApplicationContext()).load(enemyUser.avatar).fitCenter()
                .placeholder(R.drawable.avatar_default).dontAnimate()
                .error(R.drawable.avatar_default).into(mImageViewUserAvatar2);
        mTextViewCityUser2.setText(enemyUser.address);
        mTextViewScore2.setText("" + enemyUser.totalScore);

    }

    public void findViewById() {
        intent = new Intent(SearchOpponent.this, PlayScreen.class);
        waitDialog = new Dialog(this);

        Typeface font = Typeface.createFromAsset(getAssets(),
                "fonts/roboto.ttf");

        mTextViewUserName1 = (TextView) findViewById(R.id.textview_username1);
        mImageViewUserAvatar1 = (ImageView) findViewById(R.id.imageview_useravatar1);
        mTextViewCityUser1 = (TextView) findViewById(R.id.textview_city_user1);
        mTextViewScore1 = (TextView) findViewById(R.id.textview_score1);


        mTextViewUserName2 = (TextView) findViewById(R.id.textview_username2);
        mImageViewUserAvatar2 = (ImageView) findViewById(R.id.imageview_useravatar2);
        mTextViewCityUser2 = (TextView) findViewById(R.id.textview_city_user2);
        mTextViewScore2 = (TextView) findViewById(R.id.textview_score2);

        setTypeface(font, mTextViewUserName1, mTextViewCityUser1, mTextViewScore1, mTextViewUserName2, mTextViewCityUser2, mTextViewScore2);

        mButtonSeach = (Button) findViewById(R.id.button_search_again);
        mButtonPlay = (Button) findViewById(R.id.button_play);

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("666F50DBC9F76F90D726062FAA38B130")
                .addTestDevice("F62A1ABE4DDAA8A709CCEBA71211561A")
                .build();
        mAdView.loadAd(adRequest);

    }

    public static void setTypeface(Typeface font, TextView... textviews) {
        for (TextView textView : textviews) {
            textView.setTypeface(font);
        }
    }

    public void setWaitDialog() {
        waitDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        waitDialog.setContentView(R.layout.layout_wait_popup);
        waitDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        waitDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        waitDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        waitDialog.setCancelable(false);
        mTextViewReady = (TextView) waitDialog.findViewById(R.id.textview_ready);
        mTextViewWaitText = (TextView) waitDialog.findViewById(R.id.textview_wait_text);

        btnCancel = (Button) waitDialog.findViewById(R.id.button_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SoundPoolManager.getInstance().playSound(R.raw.touch_sound);
                mAltpHelper.quit(mUser, mRoom, false);
                waitDialog.hide();
                isCancel = true;
                btnSearch(getCurrentFocus());
            }
        });

    }

    public void btnSearch(View view) {
        mAltpHelper.quit(mUser, mRoom, false);
        SoundPoolManager.getInstance().playSound(R.raw.touch_sound);
        SoundPoolManager.getInstance().stop();
        Intent intent = new Intent(SearchOpponent.this, InfoScreen.class);
        startActivity(intent);
        overridePendingTransition(R.animator.in_from_left, R.animator.out_to_right);
        finish();
    }

    @Override
    public void onBackPressed() {

    }

    public void btnPlay(View view) {
        SoundPoolManager.getInstance().playSound(R.raw.touch_sound);
        waitDialog.show();
        mAltpHelper.play(mUser, mRoom);

    }

    @Override
    protected void onPause() {
        if (SoundPoolManager.getInstance().isPlaySound()) {
            SoundPoolManager.getInstance().stop();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (waitDialog != null) {
            waitDialog.dismiss();
        }
        if (!notReady && runMovePlayScr != null) {
            handler.removeCallbacks(runMovePlayScr);
        }
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        super.onDestroy();
    }

    @Override
    public void onResume() {
        if (!NetworkUtils.checkInternetConnection(this)) {
            NetworkUtils.movePopupConnection(this);
        }
        super.onResume();
    }
}
