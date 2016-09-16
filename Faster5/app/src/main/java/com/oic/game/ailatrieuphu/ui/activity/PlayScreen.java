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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.oic.game.ailatrieuphu.MainApplication;
import com.oic.game.ailatrieuphu.R;
import com.oic.game.ailatrieuphu.model.Question;
import com.oic.game.ailatrieuphu.model.Room;
import com.oic.game.ailatrieuphu.model.User;
import com.oic.game.ailatrieuphu.sock.AltpHelper;
import com.oic.game.ailatrieuphu.sock.SockAltp;
import com.oic.game.ailatrieuphu.util.SoundPoolManager;

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
    Dialog quitDialog;
    MediaPlayer mediaPlayer;
    private boolean clickable = true;
    private boolean isCheckFifty = true;
    private long timeLeft;
    private String mMoney = "";
    private int mWinner;
    private int mFifty = 0;
    private int timeQuestionImpor = 0;
    private int timeQuestion15 = 0;
    boolean checkVuotMoc = false;
    Runnable hideRuleDialog;
    Runnable offSoundVuotMoc;

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
            ArrayList<User> userGameOver = mAltpHelper.gameOverCallback(args);
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
                Log.e("TAG", "Score Gameover1: " + mUser.score + mUser.name);
            } else {
                mUser = user2;
                Log.e("TAG", "Score Gameover2: " + mUser.score + mUser.name);
            }

            Runnable moveGameOverScr = new Runnable() {
                @Override
                public void run() {
                    startActivity(GameOver.createIntent(PlayScreen.this, mUser.score, mWinner));
                    overridePendingTransition(R.animator.right_in, R.animator.left_out);
                    finish();
                }
            };
            handler.postDelayed(moveGameOverScr, 8000 + timeQuestionImpor);
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
            }
        }

        Runnable ansNow = new Runnable() {
            @Override
            public void run() {
                switch (mQuestion.questionIndex) {
                    case 5:
                        SoundPoolManager.getInstance().playSound(R.raw.ans_now1);
                        break;
                    case 10:
                        SoundPoolManager.getInstance().playSound(R.raw.ans_now2);
                        break;
                    case 15:
                        SoundPoolManager.getInstance().playSound(R.raw.ans_now3);
                        break;
                }
            }
        };

        if (mQuestion.questionIndex == 5 || mQuestion.questionIndex == 10 || mQuestion.questionIndex == 15) {
            handler.postDelayed(ansNow, timeQuestionImpor);
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
                    switch (mUser.answerIndex) {
                        case 0:
                            if (n == 1) {
                                SoundPoolManager.getInstance().playSound(R.raw.true_a);
                            } else if (n == 2) {
                                SoundPoolManager.getInstance().playSound(R.raw.true_a2);
                            } else if (n == 3) {
                                SoundPoolManager.getInstance().playSound(R.raw.true_a3);
                            }

                            break;
                        case 1:
                            if (n == 1 || n == 3) {
                                SoundPoolManager.getInstance().playSound(R.raw.true_b);
                            } else if (n == 2) {
                                SoundPoolManager.getInstance().playSound(R.raw.true_b2);
                            }

                            break;
                        case 2:
                            if (n == 1) {
                                SoundPoolManager.getInstance().playSound(R.raw.true_c);
                            } else if (n == 2) {
                                SoundPoolManager.getInstance().playSound(R.raw.true_c2);
                            } else if (n == 3) {
                                SoundPoolManager.getInstance().playSound(R.raw.true_c3);
                            }

                            break;
                        case 3:

                            if (n == 1 || n == 3) {
                                SoundPoolManager.getInstance().playSound(R.raw.true_d3);
                            } else if (n == 2) {
                                SoundPoolManager.getInstance().playSound(R.raw.true_c2);
                            }
                            break;
                    }


                    mButtonAns[mUser.answerIndex].setBackgroundDrawable(btnAnswerDrawable);
                    btnAnswerDrawable.start();
                } else {
                    mButtonAns[mUser.answerIndex].setBackgroundResource(R.drawable.answer_wrong);
                    int n = new Random().nextInt(2) + 1;
                    switch (mQuestion.mCorrectAnsId) {
                        case 0:
                            SoundPoolManager.getInstance().playSound(R.raw.lose_a);
                            break;
                        case 1:
                            SoundPoolManager.getInstance().playSound(R.raw.lose_b);
                            break;
                        case 2:
                            if (n == 1) {
                                SoundPoolManager.getInstance().playSound(R.raw.lose_c);
                            } else if (n == 2) {
                                SoundPoolManager.getInstance().playSound(R.raw.lose_c2);
                            }
                            break;
                        case 3:
                            if (n == 1) {
                                SoundPoolManager.getInstance().playSound(R.raw.lose_d);
                            } else if (n == 2) {
                                SoundPoolManager.getInstance().playSound(R.raw.lose_d2);
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
                    if (mQuestion.questionIndex == 15) {
                        timeQuestion15 = 5000;
                    }
                    Log.e("TAG", "Index Question: " + mQuestion.questionIndex);
                    changBgMusic(mQuestion.questionIndex);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < 4; i++) {
                                mButtonAns[i].clearAnimation();
                                mButtonAns[i].setBackgroundResource(R.drawable.answer0);
                                mButtonAns[i].setTextSize(getResources().getDimensionPixelSize(R.dimen.text_size_large));
                                mButtonAns[i].setEnabled(true);
                            }
                            isCheckFifty = true;
                            clickable = true;
                            if (mQuestion.questionIndex == 6 || mQuestion.questionIndex == 11) {
                                checkVuotMoc = true;
                                ruleDialog.show();
                                switch (mQuestion.questionIndex) {
                                    case 6:
                                        SoundPoolManager.getInstance().playSound(R.raw.vuot_moc_1);
                                        break;
                                    case 11:
                                        SoundPoolManager.getInstance().playSound(R.raw.vuot_moc_2);
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
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                SoundPoolManager.getInstance().playSound(R.raw.important);
                            }
                        }, 1500 + timeQuestion15);
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

                mAltpHelper.getNextQuestion(mUser, mRoom);

                mButtonAns[mEnemy.answerIndex].post(new Runnable() {
                    @Override
                    public void run() {
                        mButtonAns[mEnemy.answerIndex].setBackgroundResource(R.drawable.answer4);
                    }
                });
            }
        }

        Runnable ansNow = new Runnable() {
            @Override
            public void run() {
                switch (mQuestion.questionIndex) {
                    case 5:
                        SoundPoolManager.getInstance().playSound(R.raw.ans_now1);
                        break;
                    case 10:
                        SoundPoolManager.getInstance().playSound(R.raw.ans_now2);
                        break;
                    case 15:
                        SoundPoolManager.getInstance().playSound(R.raw.ans_now3);
                        break;
                }
            }
        };

        if (mQuestion.questionIndex == 5 || mQuestion.questionIndex == 10 || mQuestion.questionIndex == 15) {
            handler.postDelayed(ansNow, timeQuestionImpor);
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
                    switch (mUser.answerIndex) {
                        case 0:
                            if (n == 1) {
                                SoundPoolManager.getInstance().playSound(R.raw.true_a);
                            } else if (n == 2) {
                                SoundPoolManager.getInstance().playSound(R.raw.true_a2);
                            } else if (n == 3) {
                                SoundPoolManager.getInstance().playSound(R.raw.true_a3);
                            }

                            break;
                        case 1:
                            if (n == 1 || n == 3) {
                                SoundPoolManager.getInstance().playSound(R.raw.true_b);
                            } else if (n == 2) {
                                SoundPoolManager.getInstance().playSound(R.raw.true_b2);
                            }

                            break;
                        case 2:
                            if (n == 1) {
                                SoundPoolManager.getInstance().playSound(R.raw.true_c);
                            } else if (n == 2) {
                                SoundPoolManager.getInstance().playSound(R.raw.true_c2);
                            } else if (n == 3) {
                                SoundPoolManager.getInstance().playSound(R.raw.true_c3);
                            }

                            break;
                        case 3:

                            if (n == 1 || n == 3) {
                                SoundPoolManager.getInstance().playSound(R.raw.true_d3);
                            } else if (n == 2) {
                                SoundPoolManager.getInstance().playSound(R.raw.true_d2);
                            }
                            break;
                    }

                    AnimationDrawable btnAnswerDrawable = (AnimationDrawable)
                            getResources().getDrawable(R.drawable.xml_btn_anim);
                    mButtonAns[mUser.answerIndex].setBackgroundDrawable(btnAnswerDrawable);
                    btnAnswerDrawable.start();
                }
            }
        }, 3000 + timeQuestionImpor);

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
        popupCheckQuit();
    }

    public void findViewById() {

        ruleDialog = new Dialog(this);
        quitDialog = new Dialog(this);

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

    public void popupRule() {
        ruleDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ruleDialog.setContentView(R.layout.layout_popup_rule);
        ruleDialog.getWindow().getAttributes().windowAnimations = R.style.DialogNoAnimation;
        ruleDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        ruleDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        ruleDialog.setCancelable(false);

        Button skipBtn = (Button) ruleDialog.findViewById(R.id.button_skip);

        SoundPoolManager.getInstance().playSound(R.raw.luatchoi);

        hideRuleDialog = new Runnable() {
            @Override
            public void run() {
                if (!checkVuotMoc) {
                    int n = new Random().nextInt(2) + 1;
                    changBgMusic(1);
                    switch (n) {
                        case 1:
                            SoundPoolManager.getInstance().playSound(R.raw.ques1);
                            break;
                        case 2:
                            SoundPoolManager.getInstance().playSound(R.raw.ques1_b);
                            break;
                    }
                }
                ruleDialog.hide();
                checkVuotMoc = false;
            }
        };

        handler.postDelayed(hideRuleDialog, 8000);

        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkVuotMoc) {
                    int n = new Random().nextInt(2) + 1;
                    SoundPoolManager.getInstance().stop();
                    changBgMusic(1);
                    switch (n) {
                        case 1:
                            SoundPoolManager.getInstance().playSound(R.raw.ques1);
                            break;
                        case 2:
                            SoundPoolManager.getInstance().playSound(R.raw.ques1_b);
                            break;
                    }
                } else {
                    setSoundQuestion(mQuestion.questionIndex);
                    handler.removeCallbacks(offSoundVuotMoc);
                }
                handler.removeCallbacks(hideRuleDialog);
                ruleDialog.hide();
                checkVuotMoc = false;
            }
        });
        ruleDialog.show();
    }

    public void popupCheckQuit() {
        quitDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        quitDialog.setContentView(R.layout.layout_check_quit);
        quitDialog.getWindow().getAttributes().windowAnimations = R.style.DialogNoAnimation;
        quitDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        quitDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Button quitBtn = (Button) quitDialog.findViewById(R.id.button_quit);
        Button continueBtn = (Button) quitDialog.findViewById(R.id.button_continue);

        quitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SoundPoolManager.getInstance().playSound(R.raw.touch_sound);
                mAltpHelper.quit(mUser, mRoom);
            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SoundPoolManager.getInstance().playSound(R.raw.touch_sound);
                quitDialog.hide();
            }
        });

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
        setTxtRound(stt);
        mTextViewQuestion.setText(mQuestion.mQuestion);
        mTextViewAns1.setText("A: " + mQuestion.mAns.get(0));
        mTextViewAns2.setText("B: " + mQuestion.mAns.get(1));
        mTextViewAns3.setText("C: " + mQuestion.mAns.get(2));
        mTextViewAns4.setText("D: " + mQuestion.mAns.get(3));
        autoFitText(mTextViewQuestion, mTextViewAns1, mTextViewAns2, mTextViewAns3, mTextViewAns4);
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
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
        return answerIndex == mQuestion.mCorrectAnsId;
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

                    int answerRight = mQuestion.mCorrectAnsId;
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
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            AutofitHelper helper = AutofitHelper.create(textView);
            helper.setTextSize(textView.getTextSize());
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
        SoundPoolManager.getInstance().playSound(R.raw.touch_sound);
        quitDialog.show();
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
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
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
        if (quitDialog != null) {
            quitDialog.dismiss();
        }
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
