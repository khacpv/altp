package com.example.gcs.faster5.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.gcs.faster5.MainApplication;
import com.example.gcs.faster5.R;
import com.example.gcs.faster5.model.Question;
import com.example.gcs.faster5.model.Room;
import com.example.gcs.faster5.model.User;
import com.example.gcs.faster5.sock.AltpHelper;
import com.example.gcs.faster5.sock.SockAltp;
import com.example.gcs.faster5.util.SoundPoolManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Kien on 07/05/2016.
 * test
 */
public class PlayScreen extends AppCompatActivity {

    private final String TXT_TIME_OUT = "TIME\nOUT";
    private static final String EXTRA_USER = "user";
    private static final String EXTRA_ENEMY = "enemy";
    private static final String EXTRA_ROOM = "room";
    private static final String EXTRA_QUESTION = "question";
    ImageView mImageViewUserAvatar1;
    ImageView mImageViewUserAvatar2;
    TextView mTextViewScore1;
    TextView mTextViewScore2;
    TextView mTextViewRound;
    TextView mTextViewQuestion;
    TextView mTextViewAns1;
    TextView mTextViewAns2;
    TextView mTextViewAns3;
    TextView mTextViewAns4;
    TextView mTextViewTimer;
    TextView mTextViewUserName1;
    TextView mTextViewUserName2;
    TextView mTextViewCityUser1;
    TextView mTextViewCityUser2;
    TextView mTextViewMoneyQuestion;
    int mStt = 1;
    int mUserScore1 = 0;
    int mUserScore2 = 0;
    int answerRight;
    int myAnswerIndex = 0;
    int enemyAnswerIndex = 0;
    Button[] mButtonAns;
    CountDownTimer mTimeLeft;
    CountDownTimer mWaitTimeNextQues;
    CountDownTimer mWaitTime;
    boolean clickable = true;
    long timeLeft;
    int mMoney = 0;
    private SockAltp mSocketAltp;
    private AltpHelper mAltpHelper;
    User mUser;
    User mEnemy;
    Room mRoom;
    Question mQuestion;
    Handler handler = new Handler();
    Dialog ruleDialog;
    Button btnMy;
    MediaPlayer mediaPlayer;

    private SockAltp.OnSocketEvent answerNextCallback = new SockAltp.OnSocketEvent() {
        @Override
        public void onEvent(String event, Object... args) {
            Question mQuestion = mAltpHelper.answerNextCallback(args);
            Log.e("TAG", "mQuestion: " + mQuestion.mQuestion);

            OnAnsCallbackEvent eventBus = new OnAnsCallbackEvent();
            eventBus.isFromNextQuestion = true;
            eventBus.mQuestion = mQuestion;
            EventBus.getDefault().post(eventBus);
        }
    };

    private SockAltp.OnSocketEvent gameOverCallback = new SockAltp.OnSocketEvent() {
        @Override
        public void onEvent(String event, Object... args) {
            List<User> users = mAltpHelper.gameOverCallback(args);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(PlayScreen.this, GameOver.class));
                    finish();
                }
            });
        }
    };


    private SockAltp.OnSocketEvent answerCallback = new SockAltp.OnSocketEvent() {
        @Override
        public void onEvent(String event, Object... args) {
            OnAnsCallbackEvent eventBus = new OnAnsCallbackEvent();
            Pair<Integer, ArrayList<User>> result = mAltpHelper.answerCallback(args);
            eventBus.isFromNextQuestion = false;
            eventBus.result = result;
            EventBus.getDefault().post(eventBus);
        }
    };


    @Subscribe
    public void onEventMainThread(final OnAnsCallbackEvent event) {
        if (event.isFromNextQuestion) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mQuestion = event.mQuestion;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            clickable = true;
                            setSoundQuestion(mQuestion.questionIndex + 1);
                            setQA(mQuestion.questionIndex + 1);
                        }
                    });
                }
            }, 6000);
            return;
        }
        Pair<Integer, ArrayList<User>> result = event.result;
        if (result.first < 0) {
            return;
        }


        answerRight = result.first;
        List<User> answerUserList = result.second;

        for (User user : answerUserList) {
            if (!String.valueOf(user.id).equalsIgnoreCase(mUser.id)) {
                Log.e("TAG", "mEnemyanswer: " + mEnemy.answerIndex);
                enemyAnswerIndex = mEnemy.answerIndex = user.answerIndex;

                mAltpHelper.getNextQuestion(mUser, mRoom);

                mButtonAns[mEnemy.answerIndex].post(new Runnable() {
                    @Override
                    public void run() {
                        mButtonAns[mEnemy.answerIndex].setBackgroundResource(
                                checkEnemyAns(mEnemy.answerIndex) ? R.drawable.answer4 : R.drawable.answer2);
                    }
                });
            }
        }

        mButtonAns[myAnswerIndex].postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!checkAns(enemyAnswerIndex)) {
                    mButtonAns[enemyAnswerIndex].setBackgroundResource(R.drawable.answer_wrong);
                }

                if (checkAns(myAnswerIndex)) {

                    switch (myAnswerIndex){
                        case 0: SoundPoolManager.getInstance().playSound(R.raw.true_a); break;
                        case 1: SoundPoolManager.getInstance().playSound(R.raw.true_b); break;
                        case 2: SoundPoolManager.getInstance().playSound(R.raw.true_c); break;
                        case 3: SoundPoolManager.getInstance().playSound(R.raw.true_d); break;
                    }

                    AnimationDrawable btnAnswerDrawable = (AnimationDrawable)
                            getResources().getDrawable(R.drawable.xml_btn_anim);
                    mButtonAns[myAnswerIndex].setBackgroundDrawable(btnAnswerDrawable);
                    btnAnswerDrawable.start();

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < 4; i++) {
                                mButtonAns[i].clearAnimation();
                                mButtonAns[i].setBackgroundResource(R.drawable.answer0);
                            }
                        }
                    }, 2000);
                } else {
                    mButtonAns[myAnswerIndex].setBackgroundResource(R.drawable.answer_wrong);

                    switch (answerRight){
                        case 0: SoundPoolManager.getInstance().playSound(R.raw.lose_a); break;
                        case 1: SoundPoolManager.getInstance().playSound(R.raw.lose_b); break;
                        case 2: SoundPoolManager.getInstance().playSound(R.raw.lose_c); break;
                        case 3: SoundPoolManager.getInstance().playSound(R.raw.lose_d); break;
                    }

                    AnimationDrawable btnAnswerDrawable = (AnimationDrawable)
                            getResources().getDrawable(R.drawable.xml_btn_anim);
                    mButtonAns[answerRight].setBackgroundDrawable(btnAnswerDrawable);
                    btnAnswerDrawable.start();

                }
            }
        }, 3000);

    }

    public static Intent createIntent(Context context, User user, User enemy, Room room, Question question) {
        Intent intent = new Intent(context, PlayScreen.class);
        intent.putExtra(EXTRA_USER, user);
        intent.putExtra(EXTRA_ENEMY, enemy);
        intent.putExtra(EXTRA_ROOM, room);
        intent.putExtra(EXTRA_QUESTION, question);
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
        setContentView(R.layout.play_screen);
        EventBus.getDefault().register(this);
        getBundle();
        bgMusic();
        mSocketAltp = MainApplication.sockAltp();
        mAltpHelper = new AltpHelper(mSocketAltp);
        if (!mSocketAltp.isConnected()) {
            mSocketAltp.connect();
        }

        mSocketAltp.addEvent("answer", answerCallback);
        mSocketAltp.addEvent("answerNext", answerNextCallback);
        mSocketAltp.addEvent("gameOver", gameOverCallback);

        mTimeLeft = new CountDownTimer(11000, 1000) {
            public void onTick(long millisUntilFinished) {
                timeLeft = (millisUntilFinished / 1000) - 1;
                if (timeLeft > 0) {
                    mTextViewTimer.setText("" + timeLeft);
                } else {
                    mTextViewTimer.setText(TXT_TIME_OUT);
                    clickable = false;
                    //gameOver();
                }
            }

            public void onFinish() {
                mTextViewTimer.setText(TXT_TIME_OUT);
                clickable = false;
            }
        };

        findViewById();
        setUserInfo();
        setQA(0);
        popupRule();
        setTxtRound(mStt + 1);
    }

    public void findViewById() {

        ruleDialog = new Dialog(this);


        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/roboto.ttf");
        mButtonAns = new Button[4];

        mTextViewTimer = (TextView) findViewById(R.id.textview_timer);
        mTextViewTimer.setBackgroundResource(R.drawable.clock);

        mTextViewRound = (TextView) findViewById(R.id.textview_numberquestion);

        mTextViewScore1 = (TextView) findViewById(R.id.textview_money1);
        mTextViewScore1.setText(String.valueOf(mUserScore1));
        mTextViewScore2 = (TextView) findViewById(R.id.textview_money2);
        mTextViewScore2.setText(String.valueOf(mUserScore2));

        mTextViewQuestion = (TextView) findViewById(R.id.textview_tablequestion);
        mTextViewAns1 = (TextView) findViewById(R.id.button_ans1);
        mTextViewAns2 = (TextView) findViewById(R.id.button_ans2);
        mTextViewAns3 = (TextView) findViewById(R.id.button_ans3);
        mTextViewAns4 = (TextView) findViewById(R.id.button_ans4);

        mTextViewUserName1 = (TextView) findViewById(R.id.textview_username1);

        mTextViewUserName2 = (TextView) findViewById(R.id.textview_username2);

        mImageViewUserAvatar1 = (ImageView) findViewById(R.id.imageview_useravatar1);
        mImageViewUserAvatar2 = (ImageView) findViewById(R.id.imageview_useravatar2);

        mTextViewCityUser1 = (TextView) findViewById(R.id.textview_city_user1);
        mTextViewCityUser2 = (TextView) findViewById(R.id.textview_city_user2);

        mTextViewMoneyQuestion = (TextView) findViewById(R.id.textview_moneyquestion);

        setTypeface(font, mTextViewTimer, mTextViewRound, mTextViewScore1, mTextViewScore2,
                mTextViewQuestion, mTextViewAns1, mTextViewAns2, mTextViewAns3, mTextViewAns4,
                mTextViewUserName1, mTextViewUserName2, mTextViewCityUser1, mTextViewCityUser2);

        for (int i = 0; i < 4; i++) {
            if (i == 0) {
                mButtonAns[i] = (Button) mTextViewAns1;
            }
            if (i == 1) {
                mButtonAns[i] = (Button) mTextViewAns2;
            }
            if (i == 2) {
                mButtonAns[i] = (Button) mTextViewAns3;
            }
            if (i == 3) {
                mButtonAns[i] = (Button) mTextViewAns4;
            }
            mButtonAns[i].setBackgroundResource(R.drawable.answer0);
        }
    }

    public void bgMusic() {
        AudioManager amanager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = amanager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
        amanager.setStreamVolume(AudioManager.STREAM_ALARM, maxVolume, 0);
        mediaPlayer = MediaPlayer.create(PlayScreen.this, R.raw.bgmusic_playscr);
        mediaPlayer.setLooping(true);
    }

    public void popupRule() {
        mediaPlayer.pause();
        ruleDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ruleDialog.setContentView(R.layout.layout_popup_rule);
        ruleDialog.getWindow().getAttributes().windowAnimations = R.style.DialogNoAnimation;
        ruleDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        ruleDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        ruleDialog.setCancelable(false);

        Button skipBtn = (Button) ruleDialog.findViewById(R.id.button_skip);
        final int n = new Random().nextInt(2) + 1;

        final Runnable hideRuleDialog = new Runnable() {
            @Override
            public void run() {
                switch (n) {
                    case 1:
                        SoundPoolManager.getInstance().playSound(R.raw.ques1);
                        break;
                    case 2:
                        SoundPoolManager.getInstance().playSound(R.raw.ques1_b);
                        break;
                }
                ruleDialog.hide();
                mediaPlayer.start();
            }
        };

        handler.postDelayed(hideRuleDialog, 8000);

        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SoundPoolManager.getInstance().playSound(R.raw.touch_sound);
                SoundPoolManager.getInstance().stop();
                handler.removeCallbacks(hideRuleDialog);
                ruleDialog.hide();
                mediaPlayer.start();
                switch (n) {
                    case 1:
                        SoundPoolManager.getInstance().playSound(R.raw.ques1);
                        break;
                    case 2:
                        SoundPoolManager.getInstance().playSound(R.raw.ques1_b);
                        break;
                }
            }
        });


        ruleDialog.show();
        SoundPoolManager.getInstance().playSound(R.raw.luatchoi);
    }

    public void setUserInfo() {
        fillData();
        setQA(0);
        setTxtRound(mStt + 1);
    }

    /**
     * get data from previous activity
     */
    private void getBundle() {
        mUser = (User) getIntent().getSerializableExtra(EXTRA_USER);
        mEnemy = (User) getIntent().getSerializableExtra(EXTRA_ENEMY);
        mRoom = (Room) getIntent().getSerializableExtra(EXTRA_ROOM);
        mQuestion = (Question) getIntent().getSerializableExtra(EXTRA_QUESTION);
    }

    private void fillData() {
        // my info
        mTextViewUserName1.setText(mUser.name);
        mTextViewCityUser1.setText(mUser.address);
        Glide.with(getApplicationContext()).load(mUser.avatar).into(mImageViewUserAvatar1);

        // enemy info
        Glide.with(getApplicationContext()).load(mEnemy.avatar).into(mImageViewUserAvatar2);
        mTextViewUserName2.setText(mEnemy.name);
        mTextViewCityUser2.setText(mEnemy.address);
    }

    public void setQA(int stt) {
        this.mStt = stt;
        mTextViewQuestion.setText(mQuestion.mQuestion);
        mTextViewAns1.setText("A: " + mQuestion.mAns.get(0));
        mTextViewAns2.setText("B: " + mQuestion.mAns.get(1));
        mTextViewAns3.setText("C: " + mQuestion.mAns.get(2));
        mTextViewAns4.setText("D: " + mQuestion.mAns.get(3));
        mediaPlayer.start();
    }

    public void setSoundQuestion(int stt) {
        switch (stt) {
            case 2:
                SoundPoolManager.getInstance().playSound(R.raw.ques2);
                break;
            case 3:
                SoundPoolManager.getInstance().playSound(R.raw.ques3);
                break;
            case 4:
                SoundPoolManager.getInstance().playSound(R.raw.ques4);
                break;
            case 5:
                SoundPoolManager.getInstance().playSound(R.raw.ques5);
                break;
            case 6:
                SoundPoolManager.getInstance().playSound(R.raw.ques6);
                break;
            case 7:
                SoundPoolManager.getInstance().playSound(R.raw.ques7);
                break;
            case 8:
                SoundPoolManager.getInstance().playSound(R.raw.ques8);
                break;
            case 9:
                SoundPoolManager.getInstance().playSound(R.raw.ques9);
                break;
            case 10:
                SoundPoolManager.getInstance().playSound(R.raw.ques10);
                break;
            case 11:
                SoundPoolManager.getInstance().playSound(R.raw.ques11);
                break;
            case 12:
                SoundPoolManager.getInstance().playSound(R.raw.ques12);
                break;
            case 13:
                SoundPoolManager.getInstance().playSound(R.raw.ques13);
                break;
            case 14:
                SoundPoolManager.getInstance().playSound(R.raw.ques14);
                break;
            case 15:
                SoundPoolManager.getInstance().playSound(R.raw.ques15);
                break;
        }


    }

    public boolean checkAns(int answerIndex) {
        return answerIndex == answerRight;
    }

    public boolean checkEnemyAns(int enemyAnswerIndex) {
        return enemyAnswerIndex == myAnswerIndex;
    }

    public void btnAnswerClick(final View btnAnswer) {
        btnMy = (Button) btnAnswer;
        if (clickable) {
            mediaPlayer.pause();
            clickable = false;
            mTimeLeft.cancel();
            final int n = new Random().nextInt(2) + 1;
            switch (btnAnswer.getId()) {
                case R.id.button_ans1:
                    switch (n) {
                        case 1:
                            SoundPoolManager.getInstance().playSound(R.raw.ans_a);
                            break;
                        case 2:
                            SoundPoolManager.getInstance().playSound(R.raw.ans_a2);
                            break;
                    }
                    myAnswerIndex = 0;
                    break;
                case R.id.button_ans2:
                    switch (n) {
                        case 1:
                            SoundPoolManager.getInstance().playSound(R.raw.ans_b);
                            break;
                        case 2:
                            SoundPoolManager.getInstance().playSound(R.raw.ans_b2);
                            break;
                    }
                    myAnswerIndex = 1;
                    break;
                case R.id.button_ans3:
                    switch (n) {
                        case 1:
                            SoundPoolManager.getInstance().playSound(R.raw.ans_c);
                            break;
                        case 2:
                            SoundPoolManager.getInstance().playSound(R.raw.ans_c2);
                            break;
                    }
                    myAnswerIndex = 2;
                    break;
                case R.id.button_ans4:
                    switch (n) {
                        case 1:
                            SoundPoolManager.getInstance().playSound(R.raw.ans_d);
                            break;
                        case 2:
                            SoundPoolManager.getInstance().playSound(R.raw.ans_d2);
                            break;
                    }
                    myAnswerIndex = 3;
                    break;
            }
            mAltpHelper.answer(mUser, mRoom, myAnswerIndex);

            btnAnswer.setBackgroundResource(R.drawable.answer1);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        gameOver();
    }

    private void setTxtRound(int round) {
        switch (round) {
            case 1:
                mMoney = 100000;
                break;
            case 2:
                mMoney = 200000;
                break;
            case 3:
                mMoney = 300000;
                break;
            case 4:
                mMoney = 500000;
                break;
            case 5:
                mMoney = 1000000;
                break;
        }
        mTextViewMoneyQuestion.setText(String.valueOf(mMoney));
        mTextViewRound.setText(String.valueOf(round));
    }

    public void gameOver() {
        Intent intent = new Intent(getApplicationContext(), GameOver.class);
        startActivity(intent);
        overridePendingTransition(R.animator.right_in, R.animator.left_out);
        finish();
    }

    public static void setTypeface(Typeface font, TextView... textviews) {
        for (TextView textView : textviews) {
            textView.setTypeface(font);
        }
    }

    public static class OnAnsCallbackEvent {
        boolean isFromNextQuestion = false;
        Pair<Integer, ArrayList<User>> result;
        Question mQuestion;
    }

    @Override
    protected void onPause() {
        if(mediaPlayer.isPlaying()){
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
        if(mediaPlayer != null ){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        if (ruleDialog != null) {
            ruleDialog.dismiss();
        }
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
