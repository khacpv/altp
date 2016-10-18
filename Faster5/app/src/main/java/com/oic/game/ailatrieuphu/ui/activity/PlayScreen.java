package com.oic.game.ailatrieuphu.ui.activity;

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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.game.oic.ailatrieuphu.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.oic.game.ailatrieuphu.MainApplication;
import com.oic.game.ailatrieuphu.model.GameOverMessage;
import com.oic.game.ailatrieuphu.model.Question;
import com.oic.game.ailatrieuphu.model.Room;
import com.oic.game.ailatrieuphu.model.User;
import com.oic.game.ailatrieuphu.sock.AltpHelper;
import com.oic.game.ailatrieuphu.sock.SockAltp;
import com.oic.game.ailatrieuphu.util.PrefUtils;
import com.oic.game.ailatrieuphu.util.SoundPoolManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import io.socket.client.Socket;
import me.grantland.widget.AutofitHelper;

/**
 * Created by Kien on 07/05/2016.
 * test
 */
public class PlayScreen extends AppCompatActivity implements View.OnClickListener {

    private static final String EXTRA_USER = "user";
    private static final String EXTRA_ENEMY = "enemy";
    private static final String EXTRA_ROOM = "room";
    private static final String EXTRA_QUESTION = "question";
    ImageView mImageViewUserAvatar1;
    ImageView mImageViewUserAvatar2;
    ImageView[] mImageViewUserAvatarReply = new ImageView[8];
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
    Button mButtonFifty;
    Button mButtonAudience;
    Button mButtonShowAns;
    Button mButtonTut;
    private SockAltp mSocketAltp;
    private AltpHelper mAltpHelper;
    private User mUser;
    private User mEnemy;
    private Room mRoom;
    private Question mQuestion;
    private GameOverMessage mMessage;
    Handler handler = new Handler();
    Runnable hideRuleDialog;
    Runnable offSoundVuotMoc;
    Runnable musicImpor;
    Runnable runServerErr;
    Dialog ruleDialog;
    Dialog quitDialog;
    Dialog barChartDialog;
    Dialog disconnectDialog;
    Dialog quitNoticeDialog;
    MediaPlayer mediaPlayer;
    BarChart barChart;
    Typeface font;
    CountDownTimer timer, timerResume;
    RelativeLayout tutorialLayout;
    private int timeRemaining = 0;
    boolean isPauseClock = false;
    private boolean clickable = true;
    private boolean isCheckFiftyHelp = true;
    private boolean isCheckAudienceHelp = true;
    private boolean isCheckShowAnsRightHelp = true;
    private String mMoney = "";
    private int mFiftyHelp = 0;
    private int mAudienceHelp = 0;
    private int mShowAnsRightHelp = 0;
    private int timeQuestionImpor = 0;
    private int timeQuestion15 = 0;
    private int rdIdxFifty = 0;
    private boolean checkVuotMoc = false;
    private boolean isMoveGameOver = false;
    private boolean isServerErr = false;
    InterstitialAd mInterstitialAd;

    private SockAltp.OnSocketEvent globalCallback = new SockAltp.OnSocketEvent() {
        @Override
        public void onEvent(String event, Object... args) {
            switch (event) {
                case Socket.EVENT_CONNECTING:
                    Log.e("TAG_PLAY", "connecting");
                    break;
                case Socket.EVENT_DISCONNECT:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!disconnectDialog.isShowing() && !isFinishing()) {
                                pauseTimer();
                                disconnectDialog.show();
                            }
                        }
                    });

                    Log.e("TAG_PLAY", "disconnected");
                    break;
                case Socket.EVENT_CONNECT:  // auto call on connect to server
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (disconnectDialog.isShowing() && !isFinishing()) {
                                disconnectDialog.dismiss();
                                if (clickable) {
                                    resumeTimer();
                                } else {
                                    Log.e("TAG", "RECONNECT & REANSWER");
                                    mAltpHelper.answer(mUser, mRoom, mUser.answerIndex);
                                }
                            }
                        }
                    });
                    Log.e("TAG_PLAY", "connect");
                    break;
                case Socket.EVENT_CONNECT_ERROR:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!disconnectDialog.isShowing() && !isFinishing()) {
                                disconnectDialog.show();
                                pauseTimer();
                            }
                        }
                    });
                    Log.e("TAG_PLAY", "error");
                    break;
                case Socket.EVENT_CONNECT_TIMEOUT:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!disconnectDialog.isShowing() && !isFinishing()) {
                                disconnectDialog.show();
                                pauseTimer();
                            }
                        }
                    });
                    Log.e("TAG_PLAY", "timeout");
                    break;
            }
        }
    };

    private SockAltp.OnSocketEvent answerNextCallback = new SockAltp.OnSocketEvent() {
        @Override
        public void onEvent(String event, Object... args) {
            if (isMoveGameOver) {
                return;
            }
            Pair<Room, Question> data = mAltpHelper.answerNextCallback(args);
            handler.removeCallbacks(runServerErr);
            mRoom = data.first;
            mQuestion = data.second;

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
            if (isMoveGameOver) {
                return;
            }
            OnAnsCallbackEvent eventBus = new OnAnsCallbackEvent();
            Pair<Integer, ArrayList<User>> result = mAltpHelper.answerCallback(args);
            handler.removeCallbacks(runServerErr);
            eventBus.isFromNextQuestion = false;
            eventBus.result = result;
            EventBus.getDefault().post(eventBus);
        }
    };

    private SockAltp.OnSocketEvent quitCallback = new SockAltp.OnSocketEvent() {

        @Override
        public void onEvent(String event, Object... args) {
            if (isMoveGameOver) {
                return;
            }
            mMessage = mAltpHelper.gameOverCallbackGetMessages(args);
            String quitUserId = mAltpHelper.quitCallbackGetUserQuitId(args);
            handler.removeCallbacks(runServerErr);
            isServerErr = false;
            int timeQuit = 0;
            if (!mUser.id.equalsIgnoreCase(quitUserId)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        quitNoticeDialog.show();
                    }
                });
                timeQuit = 3000;
            }
            if (!isFinishing() && !isMoveGameOver) {
                mSocketAltp.removeEvent();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        stopSound();
                        mSocketAltp.removeEvent();
                        if (mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();
                            eventAdClose();
                        } else {
                            setMoveGameOver();
                        }
                    }
                }, timeQuit);
            }
        }
    };

    private SockAltp.OnSocketEvent gameOverCallback = new SockAltp.OnSocketEvent() {
        @Override
        public void onEvent(String event, Object... args) {
            if (isMoveGameOver) {
                return;
            }
            OnGameOverCallbackEvent eventBus = new OnGameOverCallbackEvent();
            ArrayList<User> userGameOver = mAltpHelper.gameOverCallback(args);
            mMessage = mAltpHelper.gameOverCallbackGetMessages(args);
            boolean isLastQuesion = mAltpHelper.gameOverCallbackGetLastQuestion(args);
            handler.removeCallbacks(runServerErr);
            isServerErr = false;
            int timeMoveGameOver = 8000;
            if (isLastQuesion) {
                timeMoveGameOver = 2000;
            }

            User user1 = userGameOver.get(0);
            User user2 = userGameOver.get(1);

            if (user1.id.equalsIgnoreCase(mUser.id)) {
                mUser = user1;
                mEnemy = user2;
            } else {
                mUser = user2;
                mEnemy = user1;
            }

            Runnable moveGameOverScr = new Runnable() {
                @Override
                public void run() {
                    mSocketAltp.removeEvent();
                    stopSound();
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                        eventAdClose();
                    } else {
                        setMoveGameOver();
                    }
                }
            };
            if (!isFinishing() && !isMoveGameOver) {
                handler.postDelayed(moveGameOverScr, timeMoveGameOver + timeQuestionImpor + timeQuestion15);
            }
            eventBus.result = userGameOver;
            EventBus.getDefault().post(eventBus);
        }
    };

    @Subscribe
    public void onEventMainThread(final OnGameOverCallbackEvent event) {
        Log.e("TAG", "onEventMainThread: GAMEOVER");
        final ArrayList<User> userGameOver = event.result;

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
                setEnemeAvatarReply(mEnemy.answerIndex,
                        checkEnemyAns(mEnemy.answerIndex) ? (mEnemy.answerIndex * 2) : (mEnemy.answerIndex * 2) + 1);
            }
        }
        if (mQuestion.questionIndex == 5 || mQuestion.questionIndex == 10 || mQuestion.questionIndex == 15) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    switch (mQuestion.questionIndex) {
                        case 5:
                            playSound(R.raw.ans_now1);
                            break;
                        case 10:
                            playSound(R.raw.ans_now2);
                            break;
                        case 15:
                            playSound(R.raw.ans_now3);
                            break;
                    }
                }
            }, timeQuestionImpor);
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
                            for (User user : userGameOver) {
                                if (user.id.equalsIgnoreCase(mEnemy.id)) {
                                    mEnemy.score = user.score;
                                    mTextViewEnemyScore.setText(String.valueOf(mEnemy.score));
                                }
                            }
                        }
                    });
                }

                if (checkAns(mUser.answerIndex)) {

                    //Add score for user
                    mTextViewMyScore.post(new Runnable() {
                        @Override
                        public void run() {
                            for (User user : userGameOver) {
                                if (user.id.equalsIgnoreCase(mUser.id)) {
                                    mUser.score = user.score;
                                    mTextViewMyScore.setText(String.valueOf(mUser.score));
                                }
                            }
                        }
                    });
                    int n = new Random().nextInt(3) + 1;
                    soundCorrectAns(n);

                    mButtonAns[mUser.answerIndex].setBackgroundDrawable(btnAnswerDrawable);
                    btnAnswerDrawable.start();
                } else {
                    mButtonAns[mUser.answerIndex].setBackgroundResource(R.drawable.answer_wrong);
                    int n = new Random().nextInt(2) + 1;
                    switch (mQuestion.mCorrectAnsId) {
                        case 0:
                            playSound(R.raw.lose_a);
                            break;
                        case 1:
                            playSound(R.raw.lose_b);
                            break;
                        case 2:
                            if (n == 1) {
                                playSound(R.raw.lose_c);
                            } else if (n == 2) {
                                playSound(R.raw.lose_c2);
                            }
                            break;
                        case 3:
                            if (n == 1) {
                                playSound(R.raw.lose_d);
                            } else if (n == 2) {
                                playSound(R.raw.lose_d2);
                            }
                            break;
                    }
                    mButtonAns[mQuestion.mCorrectAnsId].setBackgroundDrawable(btnAnswerDrawable);
                    btnAnswerDrawable.start();
                }
            }
        }, 3000 + timeQuestionImpor);
    }

    @Subscribe
    public void onEventMainThread(final OnAnsCallbackEvent event) {
        //get Next Question
        if (event.isFromNextQuestion) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mQuestion = event.mQuestion;
                    startTimer();
                    mTextViewMoneyQuestion.post(new Runnable() {
                        @Override
                        public void run() {
                            mTextViewMoneyQuestion.setBackgroundResource(
                                    (mQuestion.questionIndex == 5 || mQuestion.questionIndex == 10 || mQuestion.questionIndex == 15)
                                            ? R.drawable.money_question2 : R.drawable.money_question);
                        }
                    });
                    if (mQuestion.questionIndex == 15) {
                        timeQuestion15 = 4000;
                    }
                    Log.e("TAG", "mCorrectAnsId Index Question: " + mQuestion.mCorrectAnsId);
                    changBgMusic(mQuestion.questionIndex);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < mButtonAns.length; i++) {
                                mButtonAns[i].clearAnimation();
                                mButtonAns[i].setBackgroundResource(R.drawable.answer0);
                                mButtonAns[i].setTextSize(getResources().getDimensionPixelSize(R.dimen.text_size_large));
                                mButtonAns[i].setEnabled(true);
                            }
                            for (int i = 0; i < mImageViewUserAvatarReply.length; i++) {
                                mImageViewUserAvatarReply[i].setImageDrawable(null);
                            }
                            isCheckFiftyHelp = true;
                            isCheckAudienceHelp = true;
                            isCheckShowAnsRightHelp = true;
                            clickable = true;
                            if (mQuestion.questionIndex == 6 || mQuestion.questionIndex == 11) {
                                checkVuotMoc = true;
                                ruleDialog.show();
                                switch (mQuestion.questionIndex) {
                                    case 6:
                                        playSound(R.raw.vuot_moc_1);
                                        break;
                                    case 11:
                                        playSound(R.raw.vuot_moc_2);
                                        break;
                                }
                                handler.postDelayed(hideRuleDialog, 8000);
                            }
                            setQA(mQuestion.questionIndex);
                            if (!checkVuotMoc) {
                                setSoundQuestion(mQuestion.questionIndex);
                            } else {
                                offSoundVuotMoc = new Runnable() {
                                    @Override
                                    public void run() {
                                        setSoundQuestion(mQuestion.questionIndex);
                                    }
                                };
                                handler.postDelayed(offSoundVuotMoc, 8000);
                            }
                        }
                    });
                    if (mQuestion.questionIndex == 5 || mQuestion.questionIndex == 10 || mQuestion.questionIndex == 15) {
                        timeQuestionImpor = 5000;
                        musicImpor = new Runnable() {
                            @Override
                            public void run() {
                                playSound(R.raw.important);
                            }
                        };
                        handler.postDelayed(musicImpor, 1500 + timeQuestion15);
                    } else {
                        timeQuestionImpor = 0;
                    }
                }
            }, 7000 + timeQuestionImpor);
            return;
        }

        Pair<Integer, ArrayList<User>> result = event.result;
        if (result.first < 0) {
            return;
        }
        final List<User> answerUserList = result.second;

        // Kiem tra cau tra loi cua enemy
        for (User user : answerUserList) {
            if (!String.valueOf(user.id).equalsIgnoreCase(mUser.id)) {
                Log.e("TAG", "mEnemyanswer: " + mEnemy.answerIndex);
                mEnemy.answerIndex = user.answerIndex;
                if (mQuestion.questionIndex < 15) {
                    mAltpHelper.getNextQuestion(mUser, mRoom);
                }
                mButtonAns[mEnemy.answerIndex].post(new Runnable() {
                    @Override
                    public void run() {
                        mButtonAns[mEnemy.answerIndex].setBackgroundResource(R.drawable.answer4);
                    }
                });
                setEnemeAvatarReply(mEnemy.answerIndex, mEnemy.answerIndex * 2);
            }
        }
        if (mQuestion.questionIndex == 5 || mQuestion.questionIndex == 10 || mQuestion.questionIndex == 15) {
            final int index = mQuestion.questionIndex;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    switch (index) {
                        case 5:
                            playSound(R.raw.ans_now1);
                            break;
                        case 10:
                            playSound(R.raw.ans_now2);
                            break;
                        case 15:
                            playSound(R.raw.ans_now3);
                            break;
                    }
                }
            }, timeQuestionImpor);
        }

        // Kiem tra cau tra loi
        mButtonAns[mUser.answerIndex].postDelayed(new Runnable() {
            @Override
            public void run() {
                //   if (checkAns(mUser.answerIndex)) {

                //Add score for user
                mTextViewMyScore.post(new Runnable() {
                    @Override
                    public void run() {
                        for (User user : answerUserList) {
                            if (user.id.equalsIgnoreCase(mUser.id)) {
                                mUser.score = user.score;
                                mTextViewMyScore.setText(String.valueOf(mUser.score));
                            }
                            if (user.id.equalsIgnoreCase(mEnemy.id)) {
                                mEnemy.score = user.score;
                                mTextViewEnemyScore.setText(String.valueOf(mEnemy.score));
                            }
                        }
                    }
                });

                int n = new Random().nextInt(3) + 1;
                soundCorrectAns(n);

                AnimationDrawable btnAnswerDrawable = (AnimationDrawable)
                        getResources().getDrawable(R.drawable.xml_btn_anim);
                mButtonAns[mUser.answerIndex].setBackgroundDrawable(btnAnswerDrawable);
                btnAnswerDrawable.start();

                // }
            }
        }, 3000 + timeQuestionImpor);

    }

    public void soundCorrectAns(int n) {
        switch (mUser.answerIndex) {
            case 0:
                if (n == 1) {
                    playSound(R.raw.true_a);
                } else if (n == 2) {
                    playSound(R.raw.true_a2);
                } else if (n == 3) {
                    playSound(R.raw.true_a3);
                }

                break;
            case 1:
                if (n == 1 || n == 3) {
                    playSound(R.raw.true_b);
                } else if (n == 2) {
                    playSound(R.raw.true_b2);
                }

                break;
            case 2:
                if (n == 1) {
                    playSound(R.raw.true_c);
                } else if (n == 2) {
                    playSound(R.raw.true_c2);
                } else if (n == 3) {
                    playSound(R.raw.true_c3);
                }

                break;
            case 3:

                if (n == 1 || n == 3) {
                    playSound(R.raw.true_d3);
                } else if (n == 2) {
                    playSound(R.raw.true_d2);
                }
                break;
        }
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
        mSocketAltp.addGlobalEvent(globalCallback);
        mSocketAltp.addEvent("answer", answerCallback);
        mSocketAltp.addEvent("answerNext", answerNextCallback);
        mSocketAltp.addEvent("gameOver", gameOverCallback);
        mSocketAltp.addEvent("quit", quitCallback);

        timer = new CountDownTimer(30100, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTextViewTimer.setText("" + (int) (millisUntilFinished / 1000));
                timeRemaining = (int) millisUntilFinished;
            }

            @Override
            public void onFinish() {
                mTextViewTimer.setText("0");
                if (!isMoveGameOver) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            timeOutandQuit();
                        }
                    });
                }
            }
        };

        findViewById();
        setInterstitialAd();
        setRuleDialog();
        setUserInfo();
        setQA(1);
        setCheckQuitDialog();
        setBarChartDialog();
        setQuitNoticeDialog();
        setDisconnectDialog();

        runServerErr = new Runnable() {
            @Override
            public void run() {
                isServerErr = true;
                setMoveGameOver();
            }
        };
    }

    public void findViewById() {
        ruleDialog = new Dialog(this);
        quitDialog = new Dialog(this);
        barChartDialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        disconnectDialog = new Dialog(this);
        quitNoticeDialog = new Dialog(this);

        font = Typeface.createFromAsset(getAssets(), "fonts/roboto.ttf");
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

        tutorialLayout = (RelativeLayout) findViewById(R.id.layout_tutorial_play);
        tutorialLayout.setOnClickListener(this);
        boolean firstUse = PrefUtils.getInstance(PlayScreen.this).get(PrefUtils.KEY_FIRST_USE, false);
        if (!firstUse) {
            tutorialLayout.setVisibility(View.INVISIBLE);
        }

        mButtonFifty = (Button) findViewById(R.id.button_save1);
        mButtonAudience = (Button) findViewById(R.id.button_save2);
        mButtonShowAns = (Button) findViewById(R.id.button_save3);
        mButtonTut = (Button) findViewById(R.id.button_tutorial_play);

        mButtonFifty.setOnClickListener(this);
        mButtonAudience.setOnClickListener(this);
        mButtonShowAns.setOnClickListener(this);
        mButtonTut.setOnClickListener(this);

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

        findViewUserAvatarReply();

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(getString(R.string.test_device_1))
                .addTestDevice(getString(R.string.test_device_2))
                .build();
        mAdView.loadAd(adRequest);

    }

    public void findViewUserAvatarReply() {
        int i = 0;
        mImageViewUserAvatarReply[i++] = (ImageView) findViewById(R.id.layout_avatar_reply1).findViewById(R.id.imageview_useravatar1);

        mImageViewUserAvatarReply[i++] = (ImageView) findViewById(R.id.layout_avatar_reply1).findViewById(R.id.imageview_useravatar2);

        mImageViewUserAvatarReply[i++] = (ImageView) findViewById(R.id.layout_avatar_reply2).findViewById(R.id.imageview_useravatar1);

        mImageViewUserAvatarReply[i++] = (ImageView) findViewById(R.id.layout_avatar_reply2).findViewById(R.id.imageview_useravatar2);

        mImageViewUserAvatarReply[i++] = (ImageView) findViewById(R.id.layout_avatar_reply3).findViewById(R.id.imageview_useravatar1);

        mImageViewUserAvatarReply[i++] = (ImageView) findViewById(R.id.layout_avatar_reply3).findViewById(R.id.imageview_useravatar2);

        mImageViewUserAvatarReply[i++] = (ImageView) findViewById(R.id.layout_avatar_reply4).findViewById(R.id.imageview_useravatar1);

        mImageViewUserAvatarReply[i] = (ImageView) findViewById(R.id.layout_avatar_reply4).findViewById(R.id.imageview_useravatar2);
    }

    public void setEnemeAvatarReply(final int ansId, final int layoutId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (ansId) {
                    case 0:
                        Glide.with(getApplicationContext()).load(mEnemy.avatar).fitCenter().into(mImageViewUserAvatarReply[layoutId]);
                        break;
                    case 1:
                        Glide.with(getApplicationContext()).load(mEnemy.avatar).fitCenter().into(mImageViewUserAvatarReply[layoutId]);
                        break;
                    case 2:
                        Glide.with(getApplicationContext()).load(mEnemy.avatar).fitCenter().into(mImageViewUserAvatarReply[layoutId]);
                        break;
                    case 3:
                        Glide.with(getApplicationContext()).load(mEnemy.avatar).fitCenter().into(mImageViewUserAvatarReply[layoutId]);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    public void setInterstitialAd() {
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.interstitial_ad_unit_id));
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(getString(R.string.test_device_1))
                .addTestDevice(getString(R.string.test_device_2))
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    public void eventAdClose() {
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                if (!isMoveGameOver) {
                    setMoveGameOver();
                }
            }
        });
    }

    public void setMoveGameOver() {
        boolean isFirstUse = PrefUtils.getInstance(PlayScreen.this).get(PrefUtils.KEY_FIRST_USE, false);
        if(isFirstUse){
            PrefUtils.getInstance(PlayScreen.this).set(PrefUtils.KEY_FIRST_USE, false);
        }
        isMoveGameOver = true;
        Intent intent = GameOver.createIntent(PlayScreen.this, mUser, mEnemy, mRoom, mMessage, isServerErr);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        //overridePendingTransition(R.animator.right_in, R.animator.left_out);
        finish();
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

    public void bgMusic() {
        AudioManager amanager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = amanager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
        amanager.setStreamVolume(AudioManager.STREAM_ALARM, maxVolume, 0);

    }

    public void changBgMusic(int questionIndex) {
        if (questionIndex < 5) {
            mediaPlayer = MediaPlayer.create(PlayScreen.this, R.raw.background_music);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }
        if (questionIndex > 5) {
            mediaPlayer = null;
            mediaPlayer = MediaPlayer.create(PlayScreen.this, R.raw.background_music_b);
            mediaPlayer.setLooping(true);
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        }
        if (questionIndex > 10) {
            mediaPlayer = null;
            mediaPlayer = MediaPlayer.create(PlayScreen.this, R.raw.background_music_c);
            mediaPlayer.setLooping(true);
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        }
    }

    public void setDisconnectDialog() {
        disconnectDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        disconnectDialog.setContentView(R.layout.layout_popup_disconnect);
        disconnectDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        disconnectDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        disconnectDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        disconnectDialog.setCancelable(false);

        ImageView loading = (ImageView) disconnectDialog.findViewById(R.id.imgView_loading);

        Glide.with(this).load(R.drawable.loading).asGif().into(loading);
    }

    public void setRuleDialog() {
        ruleDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ruleDialog.setContentView(R.layout.layout_popup_rule);
        ruleDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        ruleDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        ruleDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        ruleDialog.setCancelable(false);

        Button skipBtn = (Button) ruleDialog.findViewById(R.id.button_skip);

        playSound(R.raw.luatchoi);
        hideRuleDialog = new Runnable() {
            @Override
            public void run() {
                if (!checkVuotMoc) {
                    int n = new Random().nextInt(2) + 1;
                    changBgMusic(1);
                    switch (n) {
                        case 1:
                            playSound(R.raw.ques1);
                            break;
                        case 2:
                            playSound(R.raw.ques1_b);
                            break;
                    }
                }
                ruleDialog.hide();
                checkVuotMoc = false;
                startTimer();
            }
        };

        handler.postDelayed(hideRuleDialog, 8000);

        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkVuotMoc) {
                    int n = new Random().nextInt(2) + 1;
                    stopSound();
                    changBgMusic(1);
                    switch (n) {
                        case 1:
                            playSound(R.raw.ques1);
                            break;
                        case 2:
                            playSound(R.raw.ques1_b);
                            break;
                    }
                } else {
                    setSoundQuestion(mQuestion.questionIndex);
                    handler.removeCallbacks(offSoundVuotMoc);
                }
                handler.removeCallbacks(hideRuleDialog);
                ruleDialog.hide();
                checkVuotMoc = false;
                startTimer();
            }
        });
        ruleDialog.show();
    }

    public void setCheckQuitDialog() {
        quitDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        quitDialog.setContentView(R.layout.layout_check_quit);
        quitDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        quitDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        quitDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        quitDialog.setCancelable(false);

        final Button quitBtn = (Button) quitDialog.findViewById(R.id.button_quit);
        final Button continueBtn = (Button) quitDialog.findViewById(R.id.button_continue);

        final TextView noti = (TextView) quitDialog.findViewById(R.id.noti);

        final ImageView loading = (ImageView) quitDialog.findViewById(R.id.imgView_loading);
        Glide.with(this).load(R.drawable.loading).asGif().into(loading);
        loading.setVisibility(View.GONE);

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playSound(R.raw.touch_sound);
                quitDialog.hide();
            }
        });

        quitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.removeCallbacks(runServerErr);
                playSound(R.raw.touch_sound);
                noti.setText(getResources().getString(R.string.wait_text_quit));
                quitBtn.setVisibility(View.GONE);
                continueBtn.setVisibility(View.GONE);
                loading.setVisibility(View.VISIBLE);
                mAltpHelper.quit(mUser, mRoom, true);
                handler.postDelayed(runServerErr, 10000);
            }
        });
    }

    public void setBarChartDialog() {
        barChartDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        barChartDialog.setContentView(R.layout.layout_bar_chart);
        barChartDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        barChartDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        barChartDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        barChartDialog.setCancelable(false);

        Button hideBtn = (Button) barChartDialog.findViewById(R.id.button_hide);
        hideBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playSound(R.raw.touch_sound);
                barChartDialog.hide();
                barChart.destroyDrawingCache();

            }
        });
    }

    public void setQuitNoticeDialog() {
        quitNoticeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        quitNoticeDialog.setContentView(R.layout.layout_quit_notice);
        quitNoticeDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        quitNoticeDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        quitNoticeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        quitNoticeDialog.setCancelable(false);
    }

    public void configBarChart() {
        List<Integer> percent = audienceSuggest(mQuestion.questionIndex, mQuestion.mCorrectAnsId, isCheckFiftyHelp);

        barChart = (BarChart) barChartDialog.findViewById(R.id.barchart);

        barChart.setDescription("");
        barChart.getLegend().setEnabled(false);
        barChart.getAxisLeft().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setGridColor(android.graphics.Color.TRANSPARENT);
        barChart.getXAxis().setTextSize(20f);
        barChart.setDrawGridBackground(false);
        barChart.setTouchEnabled(false);

        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(percent.get(0), 0));
        entries.add(new BarEntry(percent.get(1), 1));
        entries.add(new BarEntry(percent.get(2), 2));
        entries.add(new BarEntry(percent.get(3), 3));

        BarDataSet dataSet = new BarDataSet(entries, "");
        ArrayList<String> labels = new ArrayList<>();
        labels.add("A");
        labels.add("B");
        labels.add("C");
        labels.add("D");
        dataSet.setColors(new int[]{getResources().getColor(R.color.ALTP)});

        BarData data = new BarData(labels, dataSet);
        data.setValueTextSize(16f);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTypeface(font);

        barChart.setData(data);
        barChart.animateXY(3000, 3000);
        barChart.invalidate();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                resumeTimer();
            }
        }, 2000);
    }

    //tro giup 50/50
    public void fiftyHelp() {
        if (isCheckFiftyHelp && mFiftyHelp == 0) {
            mFiftyHelp = 1;
            isCheckFiftyHelp = false;
            pauseTimer();
            final LinearLayout linearLayout = (LinearLayout) barChartDialog.findViewById(R.id.trogiup_khangia);
            linearLayout.setVisibility(View.INVISIBLE);
            barChartDialog.show();

            handler.removeCallbacks(musicImpor);
            Button button = (Button) findViewById(R.id.button_save1);
            button.setBackgroundResource(R.drawable.save1_dis);
            playSound(R.raw.sound5050);
            mButtonAns[0].postDelayed(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 4; i++) {
                        mButtonAns[i].setText("");
                        mButtonAns[i].setEnabled(false);
                    }
                    int answerRight = mQuestion.mCorrectAnsId;
                    rdIdxFifty = getFifty(answerRight);
                    mButtonAns[rdIdxFifty].setText(tileQuestion(rdIdxFifty) + mQuestion.mAns.get(rdIdxFifty));
                    mButtonAns[rdIdxFifty].setEnabled(true);
                    mButtonAns[answerRight].setText(tileQuestion(answerRight) + mQuestion.mAns.get(answerRight));
                    mButtonAns[answerRight].setEnabled(true);
                    barChartDialog.hide();
                    resumeTimer();
                }
            }, 3100);
        }
    }

    //tro giup khan gia
    public void audienceHelp() {
        if (isCheckAudienceHelp && mAudienceHelp == 0) {
            pauseTimer();
            handler.removeCallbacks(musicImpor);
            mAudienceHelp = 1;
            playSound(R.raw.khan_gia);
            Button button = (Button) findViewById(R.id.button_save2);
            button.setBackgroundResource(R.drawable.save2_dis);
            final LinearLayout linearLayout = (LinearLayout) barChartDialog.findViewById(R.id.trogiup_khangia);
            linearLayout.setVisibility(View.INVISIBLE);
            barChartDialog.show();

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    playSound(R.raw.khangia_bg);
                    linearLayout.setVisibility(View.VISIBLE);
                    configBarChart();
                }
            }, 6000);
        }
    }

    //tro giup xem dap an
    public void showAnsRightHelp() {
        if (isCheckShowAnsRightHelp && mShowAnsRightHelp == 0) {
            handler.removeCallbacks(musicImpor);
            mShowAnsRightHelp = 1;
            LinearLayout linearLayout = (LinearLayout) barChartDialog.findViewById(R.id.trogiup_khangia);
            linearLayout.setVisibility(View.GONE);
            barChartDialog.show();

            int n = new Random().nextInt(3) + 1;
            switch (n) {
                case 1:
                    playSound(R.raw.ans_now1);
                    break;
                case 2:
                    playSound(R.raw.ans_now2);
                    break;
                case 3:
                    playSound(R.raw.ans_now3);
                    break;
            }
            Button showAnsRightBtn = (Button) findViewById(R.id.button_save3);
            showAnsRightBtn.setBackgroundResource(R.drawable.save3_dis);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    barChartDialog.hide();
                    switch (mQuestion.mCorrectAnsId) {
                        case 0: //A
                            findViewById(R.id.button_ans1).performClick();
                            break;
                        case 1: //B
                            findViewById(R.id.button_ans2).performClick();
                            break;
                        case 2: //C
                            findViewById(R.id.button_ans3).performClick();
                            break;
                        case 3: //D
                            findViewById(R.id.button_ans4).performClick();
                            break;
                    }
                }
            }, 4500);
        }
    }

    private void setUserInfo() {
        // my info
        mTextViewUserName1.setText(mUser.name);
        mTextViewCityUser1.setText(mUser.address);
        Glide.with(getApplicationContext()).load(mUser.avatar).fitCenter()
                .placeholder(R.drawable.avatar_default).dontAnimate()
                .error(R.drawable.avatar_default)
                .into(mImageViewUserAvatar1);

        // enemy info
        Glide.with(getApplicationContext()).load(mEnemy.avatar).fitCenter()
                .placeholder(R.drawable.avatar_default).dontAnimate()
                .error(R.drawable.avatar_default).into(mImageViewUserAvatar2);
        mTextViewUserName2.setText(mEnemy.name);
        mTextViewCityUser2.setText(mEnemy.address);
    }

    public void setQA(int stt) {
        setTxtRound(stt);
        mTextViewQuestion.setText(mQuestion.mQuestion);
        mTextViewAns1.setText("A: " + mQuestion.mAns.get(0));
        mTextViewAns2.setText("B: " + mQuestion.mAns.get(1));
        mTextViewAns3.setText("C: " + mQuestion.mAns.get(2));
        mTextViewAns4.setText("D: " + mQuestion.mAns.get(3));
        autoFitText(mTextViewQuestion, mTextViewAns1, mTextViewAns2, mTextViewAns3, mTextViewAns4);
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
        handler.postDelayed(runServerErr, 40000);
    }

    public void setSoundQuestion(int stt) {
        switch (stt) {
            case 2:
                playSound(R.raw.ques2);
                break;
            case 3:
                playSound(R.raw.ques3);
                break;
            case 4:
                playSound(R.raw.ques4);
                break;
            case 5:
                playSound(R.raw.ques5);
                break;
            case 6:
                playSound(R.raw.ques6);
                break;
            case 7:
                playSound(R.raw.ques7);
                break;
            case 8:
                playSound(R.raw.ques8);
                break;
            case 9:
                playSound(R.raw.ques9);
                break;
            case 10:
                playSound(R.raw.ques10);
                break;
            case 11:
                playSound(R.raw.ques11);
                break;
            case 12:
                playSound(R.raw.ques12);
                break;
            case 13:
                playSound(R.raw.ques13);
                break;
            case 14:
                playSound(R.raw.ques14);
                break;
            case 15:
                playSound(R.raw.ques15);
                break;
        }

    }

    public boolean checkAns(int answerIndex) {
        return answerIndex == mQuestion.mCorrectAnsId;
    }

    public boolean checkEnemyAns(int answerIndex) {
        return answerIndex == mUser.answerIndex;
    }

    public void btnAnswerClick(final View btnAnswer) {
        if (clickable) {
            pauseTimer();
            handler.removeCallbacks(musicImpor);
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
            isCheckFiftyHelp = false;
            isCheckShowAnsRightHelp = false;
            isCheckAudienceHelp = false;
            clickable = false;
            final int n = new Random().nextInt(2) + 1;
            switch (btnAnswer.getId()) {
                case R.id.button_ans1:
                    switch (n) {
                        case 1:
                            playSound(R.raw.ans_a);
                            break;
                        case 2:
                            playSound(R.raw.ans_a2);
                            break;

                    }
                    Glide.with(getApplicationContext()).load(mUser.avatar).fitCenter().into(mImageViewUserAvatarReply[1]);
                    mUser.answerIndex = 0;
                    break;
                case R.id.button_ans2:
                    switch (n) {
                        case 1:
                            playSound(R.raw.ans_b);
                            break;
                        case 2:
                            playSound(R.raw.ans_b2);
                            break;
                    }
                    Glide.with(getApplicationContext()).load(mUser.avatar).fitCenter().into(mImageViewUserAvatarReply[3]);
                    mUser.answerIndex = 1;
                    break;
                case R.id.button_ans3:
                    switch (n) {
                        case 1:
                            playSound(R.raw.ans_c);
                            break;
                        case 2:
                            playSound(R.raw.ans_c2);
                            break;
                    }
                    Glide.with(getApplicationContext()).load(mUser.avatar).fitCenter().into(mImageViewUserAvatarReply[5]);
                    mUser.answerIndex = 2;
                    break;
                case R.id.button_ans4:
                    switch (n) {
                        case 1:
                            playSound(R.raw.ans_d);
                            break;
                        case 2:
                            playSound(R.raw.ans_d2);
                            break;
                    }
                    Glide.with(getApplicationContext()).load(mUser.avatar).fitCenter().into(mImageViewUserAvatarReply[7]);
                    mUser.answerIndex = 3;
                    break;
            }
            mAltpHelper.answer(mUser, mRoom, mUser.answerIndex);
            btnAnswer.setBackgroundResource(R.drawable.answer1);
        }
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

    //lay ti le tro giup khan gia
    public List<Integer> audienceSuggest(int level, int rightIndex, boolean is50_50) {
        List<Integer> result = new ArrayList<>();

        if (!is50_50) {
            Random rd = new Random();
            int a = rd.nextInt(20) + 70;
            int b = 100 - a;
            int c = 0;
            int d = 0;

            result.add(a);
            result.add(b);
            result.add(c);
            result.add(d);
            Collections.sort(result);

            int temp = result.get(2);
            result.set(rightIndex, result.get(3));
            result.set(rdIdxFifty, temp);

            for (int i = 0; i < 4; i++) {
                if (i != rightIndex && i != rdIdxFifty) {
                    result.set(i, 0);
                }
            }

            return result;
        }
        if (level < 5) {
            Random rd = new Random();
            int a = rd.nextInt(75 - 50 + 1) + 50;
            int b = rd.nextInt(100 - a + 1);
            int c = rd.nextInt(100 - a - b + 1);
            int d = 100 - a - b - c;
            result.add(a);
            result.add(b);
            result.add(c);
            result.add(d);

            Collections.sort(result);   // be -> lon    10, 20, 30, 40
            int temp = result.get(rightIndex);
            result.set(rightIndex, result.get(3));
            result.set(3, temp);

        } else {
            Random rd = new Random();
            int rndFlag = System.currentTimeMillis() % 3 == 2 ? -1 : 1;    // 1 or -1

            int a = rd.nextInt(45 - 40) + 40; // 40-45
            int b = a + (rd.nextInt(5) + 1) * rndFlag; // 40-45
            int c = rd.nextInt(100 - a - b) + 1;
            int d = 100 - a - b - c;

            result.add(a);
            result.add(b);
            result.add(c);
            result.add(d);

            Collections.sort(result);   // be -> lon    10, 20, 30, 40
            int temp = result.get(rightIndex);
            result.set(rightIndex, result.get(3));
            result.set(3, temp);
        }


        return result;
    }

    //lay 2 dap an 50/50
    public int getFifty(int rightIndex) {
        int randomIndex = 0;
        while (true) {
            randomIndex = new Random().nextInt(3);
            if (randomIndex != rightIndex) {
                break;
            }
        }
        return randomIndex;
    }

    public void startTimer() {
        if (isPauseClock && timerResume != null) {
            timerResume.cancel();
        }
        timer.start();
        isPauseClock = false;
    }

    public void pauseTimer() {
        if (!isPauseClock && timer != null) {
            timer.cancel();
        }
        if (!isPauseClock) {
            isPauseClock = true;
        }
        if (isPauseClock && timerResume != null) {
            timerResume.cancel();
        }
    }

    public void resumeTimer() {
        if (isPauseClock) {
            timerResume = new CountDownTimer(timeRemaining, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    mTextViewTimer.setText("" + (int) (millisUntilFinished / 1000));
                    timeRemaining = (int) millisUntilFinished;
                }

                @Override
                public void onFinish() {
                    timer.cancel();
                    mTextViewTimer.setText("0");
                    if (!isMoveGameOver) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                timeOutandQuit();
                            }
                        });
                    }
                }
            }.start();
        }
    }

    public void timeOutandQuit() {
        playSound(R.raw.timesup);
        mAltpHelper.quit(mUser, mRoom, true);
        LinearLayout linearLayout = (LinearLayout) barChartDialog.findViewById(R.id.trogiup_khangia);
        linearLayout.setVisibility(View.GONE);
        if (!isMoveGameOver && barChartDialog != null) {
            try {
                barChartDialog.show();
            } catch (WindowManager.BadTokenException e) {
                e.printStackTrace();
            }
        }

    }

    public static void setTypeface(Typeface font, TextView... textviews) {
        for (TextView textView : textviews) {
            textView.setTypeface(font);
        }
    }

    public static void autoFitText(TextView... textviews) {
        for (TextView textView : textviews) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            AutofitHelper helper = AutofitHelper.create(textView);
            helper.setTextSize(textView.getTextSize());
        }
    }

    public void playSound(int SoundId) {
        if (SoundPoolManager.getInstance() != null) {
            SoundPoolManager.getInstance().playSound(SoundId);
        }
    }

    public void stopSound() {
        if (SoundPoolManager.getInstance() != null) {
            SoundPoolManager.getInstance().stop();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_save1:
                fiftyHelp();
                break;

            case R.id.button_save2:
                audienceHelp();
                break;

            case R.id.button_save3:
                showAnsRightHelp();
                break;

            case R.id.button_tutorial_play:
                if (clickable) {
                    tutorialLayout.setVisibility(View.VISIBLE);
                }
                break;

            case R.id.layout_tutorial_play:
                tutorialLayout.setVisibility(View.INVISIBLE);
                break;
            default:
                break;
        }
    }

    public static class OnGameOverCallbackEvent {
        ArrayList<User> result;
    }

    public static class OnAnsCallbackEvent {
        boolean isFromNextQuestion = false;
        Pair<Integer, ArrayList<User>> result;
        Question mQuestion;
    }

    @Override
    public void onBackPressed() {
        playSound(R.raw.touch_sound);
        if (!isMoveGameOver && quitDialog != null) {
            quitDialog.show();
        }

    }

    @Override
    protected void onPause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
        if (SoundPoolManager.getInstance() != null) {
            if (SoundPoolManager.getInstance().isPlaySound()) {
                stopSound();
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
    protected void onDestroy() {

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (ruleDialog != null) {
            ruleDialog.dismiss();
        }
        if (quitDialog != null) {
            quitDialog.dismiss();
        }
        if (barChartDialog != null) {
            barChartDialog.dismiss();
        }
        if (disconnectDialog != null) {
            disconnectDialog.dismiss();
        }
        if (quitNoticeDialog != null) {
            quitNoticeDialog.dismiss();
        }
        if (timer != null) {
            timer.cancel();
        }
        if (timerResume != null) {
            timerResume.cancel();
        }
        handler.removeCallbacks(hideRuleDialog);
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
