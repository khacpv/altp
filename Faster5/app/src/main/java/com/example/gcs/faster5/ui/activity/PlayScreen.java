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
import android.util.TypedValue;
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

import me.grantland.widget.AutofitHelper;

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
    TextView mTextViewMyScore;
    TextView mTextViewEnemyScore;
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
    Button[] mButtonAns;
    CountDownTimer mTimeLeft;
    CountDownTimer mWaitTimeNextQues;
    CountDownTimer mWaitTime;
    private SockAltp mSocketAltp;
    private AltpHelper mAltpHelper;
    private User mUser;
    private User mEnemy;
    private Room mRoom;
    private Question mQuestion;
    Handler handler = new Handler();
    Dialog ruleDialog;
    MediaPlayer mediaPlayer;
    private int mStt = 1;
    private int answerRight;
    private boolean clickable = true;
    private boolean isCheckFifty = true;
    private long timeLeft;
    private String mMoney = "";
    private int mWinner;
    private int mFifty = 0;

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


    private SockAltp.OnSocketEvent gameOverCallback = new SockAltp.OnSocketEvent() {
        @Override
        public void onEvent(String event, Object... args) {
            OnGameOverCallbackEvent eventBus = new OnGameOverCallbackEvent();
            Pair<Integer, ArrayList<User>> result = mAltpHelper.gameOverCallback(args);

            List<User> userGameOver = result.second;

            User user1 = userGameOver.get(0);
            User user2 = userGameOver.get(1);

            // hoa
            if (user1.isWinner == user2.isWinner) {
                mWinner = GameOver.DRAW;
            } else {

                if ((user1.isWinner && user1.id.equalsIgnoreCase(mUser.id))
                        || user2.isWinner && user2.id.equalsIgnoreCase(mUser.id)) {
                    // thang
                    mWinner = GameOver.WIN;
                } else {
                    // thua
                    mWinner = GameOver.LOSE;
                }
            }

            if (user1.id.equalsIgnoreCase(mUser.id)) {
                mUser = user1;
                Log.e("TAG", "Score Gameover1: " + mUser.score);
            } else {
                mUser = user2;
                Log.e("TAG", "Score Gameover2: " + mUser.score);
            }


            boolean isLastQuestion = mAltpHelper.gameOverCallbackGetLastQuestion(args);
            Runnable moveGameOverScr = new Runnable() {
                @Override
                public void run() {
                    startActivity(GameOver.createIntent(PlayScreen.this, mUser.score, mWinner));
                    overridePendingTransition(R.animator.right_in, R.animator.left_out);
                    finish();
                }
            };
            handler.postDelayed(moveGameOverScr, 7000);
            eventBus.result = result;
            EventBus.getDefault().post(eventBus);

            if (isLastQuestion) {
                handler.removeCallbacks(moveGameOverScr);
                startActivity(GameOver.createIntent(PlayScreen.this, mUser.score, mWinner));
                overridePendingTransition(R.animator.right_in, R.animator.left_out);
                finish();
                return;
            }
        }
    };

    @Subscribe
    public void onEventMainThread(final OnGameOverCallbackEvent event) {
        Log.e("TAG", "onEventMainThread: GAMEOVER");
        Pair<Integer, ArrayList<User>> result = event.result;
        if (result.first < 0) {
            return;
        }
        answerRight = result.first;
        final List<User> userGameOver = result.second;
        for (User user : userGameOver) {
            if (!String.valueOf(user.id).equalsIgnoreCase(mUser.id)) {
                mEnemy.answerIndex = user.answerIndex;
                mButtonAns[mEnemy.answerIndex].post(new Runnable() {
                    @Override
                    public void run() {
                        mButtonAns[mEnemy.answerIndex].setBackgroundResource(
                                checkEnemyAns(mEnemy.answerIndex) ? R.drawable.answer4 : R.drawable.answer2);
                    }
                });
            }
        }

        mButtonAns[mUser.answerIndex].postDelayed(new Runnable() {
            @Override
            public void run() {
                AnimationDrawable btnAnswerDrawable = (AnimationDrawable)
                        getResources().getDrawable(R.drawable.xml_btn_anim);


                if (!checkAns(mEnemy.answerIndex)) {
                    mButtonAns[mEnemy.answerIndex].setBackgroundResource(R.drawable.answer_wrong);
                } else {
                    //Add score for enemy user

                    mTextViewMyScore.post(new Runnable() {
                        @Override
                        public void run() {
                            mEnemy.score = userGameOver.get(0).score;
                            mTextViewEnemyScore.setText(String.valueOf(mEnemy.score));
                        }
                    });
                }

                if (checkAns(mUser.answerIndex)) {

                    //Add score for user
                    mTextViewMyScore.post(new Runnable() {
                        @Override
                        public void run() {
                            mUser.score = userGameOver.get(1).score;
                            mTextViewMyScore.setText(String.valueOf(mUser.score));
                        }
                    });

                    switch (mUser.answerIndex) {
                        case 0:
                            SoundPoolManager.getInstance().playSound(R.raw.true_a);
                            break;
                        case 1:
                            SoundPoolManager.getInstance().playSound(R.raw.true_b);
                            break;
                        case 2:
                            SoundPoolManager.getInstance().playSound(R.raw.true_c);
                            break;
                        case 3:
                            SoundPoolManager.getInstance().playSound(R.raw.true_d);
                            break;
                    }


                    mButtonAns[mUser.answerIndex].setBackgroundDrawable(btnAnswerDrawable);
                    btnAnswerDrawable.start();
                } else {
                    mButtonAns[mUser.answerIndex].setBackgroundResource(R.drawable.answer_wrong);

                    switch (answerRight) {
                        case 0:
                            SoundPoolManager.getInstance().playSound(R.raw.lose_a);
                            break;
                        case 1:
                            SoundPoolManager.getInstance().playSound(R.raw.lose_b);
                            break;
                        case 2:
                            SoundPoolManager.getInstance().playSound(R.raw.lose_c);
                            break;
                        case 3:
                            SoundPoolManager.getInstance().playSound(R.raw.lose_d);
                            break;
                    }
                    mButtonAns[answerRight].setBackgroundDrawable(btnAnswerDrawable);
                    btnAnswerDrawable.start();
                }
            }
        }, 3000);
    }

    @Subscribe
    public void onEventMainThread(final OnAnsCallbackEvent event) {
        //get Next Question
        if (event.isFromNextQuestion) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mQuestion = event.mQuestion;
                    Log.e("TAG", "Index Question: " + mQuestion.questionIndex);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            isCheckFifty = true;
                            clickable = true;
                            setSoundQuestion(mQuestion.questionIndex);
                            setQA(mQuestion.questionIndex);
                        }
                    });
                    if (mQuestion.questionIndex == 5
                            || mQuestion.questionIndex == 10
                            || mQuestion.questionIndex == 15) {
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                SoundPoolManager.getInstance().playSound(R.raw.important);
                                Log.e("TAG", "NHAC CAU 5, 10, 15 ");
                            }
                        }, 1000);
                    }
                }
            }, 7000);
            return;
        }

        Pair<Integer, ArrayList<User>> result = event.result;
        if (result.first < 0) {
            return;
        }

        answerRight = result.first;
        final List<User> answerUserList = result.second;

        // Kiem tra cau tra loi cua enemy
        for (User user : answerUserList) {
            if (!String.valueOf(user.id).equalsIgnoreCase(mUser.id)) {
                Log.e("TAG", "mEnemyanswer: " + mEnemy.answerIndex);
                mEnemy.answerIndex = user.answerIndex;

                mAltpHelper.getNextQuestion(mUser, mRoom);

                mButtonAns[mEnemy.answerIndex].post(new Runnable() {
                    @Override
                    public void run() {
                        mButtonAns[mEnemy.answerIndex].setBackgroundResource(R.drawable.answer4);
                    }
                });
            }
        }

        // Kiem tra cau tra loi
        mButtonAns[mUser.answerIndex].postDelayed(new Runnable() {
            @Override
            public void run() {
                if (checkAns(mUser.answerIndex)) {
                    //Add score for user
                    mTextViewMyScore.post(new Runnable() {
                        @Override
                        public void run() {
                            mEnemy.score = answerUserList.get(0).score;
                            mUser.score = answerUserList.get(1).score;
                            mTextViewMyScore.setText(String.valueOf(mUser.score));
                            mTextViewEnemyScore.setText(String.valueOf(mEnemy.score));
                        }
                    });

                    switch (mUser.answerIndex) {
                        case 0:
                            SoundPoolManager.getInstance().playSound(R.raw.true_a);
                            break;
                        case 1:
                            SoundPoolManager.getInstance().playSound(R.raw.true_b);
                            break;
                        case 2:
                            SoundPoolManager.getInstance().playSound(R.raw.true_c);
                            break;
                        case 3:
                            SoundPoolManager.getInstance().playSound(R.raw.true_d);
                            break;
                    }

                    AnimationDrawable btnAnswerDrawable = (AnimationDrawable)
                            getResources().getDrawable(R.drawable.xml_btn_anim);
                    mButtonAns[mUser.answerIndex].setBackgroundDrawable(btnAnswerDrawable);
                    btnAnswerDrawable.start();

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < 4; i++) {
                                mButtonAns[i].clearAnimation();
                                mButtonAns[i].setBackgroundResource(R.drawable.answer0);
                                mButtonAns[i].setEnabled(true);
                            }
                        }
                    }, 2000);
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
        setQA(1);
        popupRule();

    }

    public void findViewById() {

        ruleDialog = new Dialog(this);

        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/roboto.ttf");
        mButtonAns = new Button[4];

        mTextViewTimer = (TextView) findViewById(R.id.textview_timer);
        mTextViewTimer.setBackgroundResource(R.drawable.clock);

        mTextViewRound = (TextView) findViewById(R.id.textview_numberquestion);

        mTextViewMyScore = (TextView) findViewById(R.id.textview_money1);
        mTextViewEnemyScore = (TextView) findViewById(R.id.textview_money2);

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

        setTypeface(font, mTextViewTimer, mTextViewRound, mTextViewMyScore, mTextViewEnemyScore,
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
                ruleDialog.dismiss();
                mediaPlayer.start();
            }
        };
        SoundPoolManager.getInstance().playSound(R.raw.luatchoi);

        handler.postDelayed(hideRuleDialog, 8000);

        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SoundPoolManager.getInstance().stop();

                handler.removeCallbacks(hideRuleDialog);
                ruleDialog.dismiss();
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
    }

    /**
     * get data from previous activity
     */
    private void getBundle() {
        mUser = (User) getIntent().getSerializableExtra(EXTRA_USER);
        mEnemy = (User) getIntent().getSerializableExtra(EXTRA_ENEMY);
        mRoom = (Room) getIntent().getSerializableExtra(EXTRA_ROOM);
        mQuestion = (Question) getIntent().getSerializableExtra(EXTRA_QUESTION);
        answerRight = mQuestion.mCorrectAnsId;
    }

    private void setUserInfo() {
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
        setTxtRound(mStt);
        mTextViewQuestion.setText(mQuestion.mQuestion);
        mTextViewAns1.setText("A: " + mQuestion.mAns.get(0));
        mTextViewAns2.setText("B: " + mQuestion.mAns.get(1));
        mTextViewAns3.setText("C: " + mQuestion.mAns.get(2));
        mTextViewAns4.setText("D: " + mQuestion.mAns.get(3));
        autoFitText(mTextViewQuestion, mTextViewAns1, mTextViewAns2, mTextViewAns3, mTextViewAns4);
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

    public boolean checkEnemyAns(int answerIndex) {
        return answerIndex == mUser.answerIndex;
    }

    public void btnAnswerClick(final View btnAnswer) {
        if (clickable) {
            mediaPlayer.pause();
            isCheckFifty = false;
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
                    mUser.answerIndex = 0;
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
                    mUser.answerIndex = 1;
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
                    mUser.answerIndex = 2;
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
                    mUser.answerIndex = 3;
                    break;
            }
            mAltpHelper.answer(mUser, mRoom, mUser.answerIndex);

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
                mMoney = "200.000";
                break;
            case 2:
                mMoney = "400.000";
                break;
            case 3:
                mMoney = "600.000";
                break;
            case 4:
                mMoney = "1.000.000";
                break;
            case 5:
                mMoney = "2.000.000";
                break;
            case 6:
                mMoney = "3.000.000";
                break;
            case 7:
                mMoney = "6.000.000";
                break;
            case 8:
                mMoney = "10.000.000";
                break;
            case 9:
                mMoney = "14.000.000";
                break;
            case 10:
                mMoney = "22.000.000";
                break;
            case 11:
                mMoney = "30.000.000";
                break;
            case 12:
                mMoney = "40.000.000";
                break;
            case 13:
                mMoney = "60.000.000";
                break;
            case 14:
                mMoney = "85.000.000";
                break;
            case 15:
                mMoney = "150.000.000";
                break;
        }
        mTextViewMoneyQuestion.setText(mMoney);
        mTextViewRound.setText(String.valueOf(round));
    }

    public void gameOver() {
        Intent intent = new Intent(getApplicationContext(), GameOver.class);
        startActivity(intent);
        overridePendingTransition(R.animator.right_in, R.animator.left_out);
        finish();
    }

    public void fifty(View fifty) {
        if (isCheckFifty && clickable && mFifty == 0) {
            mFifty = 1;
            SoundPoolManager.getInstance().playSound(R.raw.sound5050);
            isCheckFifty = false;
            mButtonAns[0].postDelayed(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 4; i++) {
                        mButtonAns[i].setText("");
                        mButtonAns[i].setEnabled(false);
                    }

                    answerRight = mQuestion.mCorrectAnsId;
                    int rdIdx = getFifty(answerRight);
                    mButtonAns[rdIdx].setText(tileQuestion(rdIdx) + mQuestion.mAns.get(rdIdx));
                    mButtonAns[rdIdx].setEnabled(true);
                    mButtonAns[answerRight].setText(tileQuestion(answerRight) + mQuestion.mAns.get(answerRight));
                    mButtonAns[answerRight].setEnabled(true);
                }
            }, 3100);
        }
    }

    public String tileQuestion(int indexAns) {
        String alphabet = "";
        switch (indexAns) {
            case 0:
                alphabet = "A: ";
                break;
            case 1:
                alphabet = "B: ";
                break;
            case 2:
                alphabet = "C: ";
                break;
            case 3:
                alphabet = "D: ";
                break;
        }
        return alphabet;
    }

    public int getFifty(int rightIndex) {
        int randomIndex = 0;
        while (true) {
            randomIndex = new Random().nextInt(3);
            Log.e("TAG", "50/50 idx:" + randomIndex + " RightIndex: " + rightIndex);
            if (randomIndex != rightIndex) {
                break;
            }
        }
        return randomIndex;
    }


    public static void setTypeface(Typeface font, TextView... textviews) {
        for (TextView textView : textviews) {
            textView.setTypeface(font);
        }
    }

    public static void autoFitText(TextView... textviews) {
        for (TextView textView : textviews) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
            AutofitHelper helper = AutofitHelper.create(textView);
            helper.setTextSize(textView.getTextSize());
        }
    }

    public static class OnGameOverCallbackEvent {
        Pair<Integer, ArrayList<User>> result;
    }

    public static class OnAnsCallbackEvent {
        boolean isFromNextQuestion = false;
        Pair<Integer, ArrayList<User>> result;
        Question mQuestion;
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
