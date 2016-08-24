package com.example.gcs.faster5.ui.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kien on 07/05/2016.
 * test
 */
public class MainScreen extends AppCompatActivity {

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

    Button btnMy;

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


    @Subscribe
    public void onEventMainThread(final OnAnsCallbackEvent event) {
        if(event.isFromNextQuestion){
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mQuestion = event.mQuestion;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setQA(1);
                        }
                    });
                }
            },4000);
            return;
        }
        Pair<Integer, ArrayList<User>> result = event.result;
        if (result.first < 0) {
            return;
        }


        answerRight = result.first;
        List<User> answerUserList = result.second;

        for (User user : answerUserList) {
            if (user.id != mUser.id) {
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
                            setQA(1);
                            clickable = true;
                        }
                    }, 2000);
                } else {
                    mButtonAns[myAnswerIndex].setBackgroundResource(R.drawable.answer_wrong);

                    AnimationDrawable btnAnswerDrawable = (AnimationDrawable)
                            getResources().getDrawable(R.drawable.xml_btn_anim);
                    mButtonAns[answerRight].setBackgroundDrawable(btnAnswerDrawable);
                    btnAnswerDrawable.start();

                }
            }
        }, 2000);

    }

    public static Intent createIntent(Context context, User user, User enemy, Room room, Question question) {
        Intent intent = new Intent(context, MainScreen.class);
        intent.putExtra(EXTRA_USER, user);
        intent.putExtra(EXTRA_ENEMY, enemy);
        intent.putExtra(EXTRA_ROOM, room);
        intent.putExtra(EXTRA_QUESTION, question);
        return intent;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.main_screen);
        EventBus.getDefault().register(this);
        getBundle();

        mSocketAltp = MainApplication.sockAltp();
        mAltpHelper = new AltpHelper(mSocketAltp);
        if (!mSocketAltp.isConnected()) {
            mSocketAltp.connect();
        }

        mSocketAltp.addEvent("answer", answerCallback);
        mSocketAltp.addEvent("answerNext", answerNextCallback);

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
        setTxtRound(mStt + 1);
    }

    public void findViewById() {

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
//        mTimeLeft.start();
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
            clickable = false;
            mTimeLeft.cancel();

            switch (btnAnswer.getId()) {
                case R.id.button_ans1:
                    myAnswerIndex = 0;
                    break;
                case R.id.button_ans2:
                    myAnswerIndex = 1;
                    break;
                case R.id.button_ans3:
                    myAnswerIndex = 2;
                    break;
                case R.id.button_ans4:
                    myAnswerIndex = 3;
                    break;
            }
            mAltpHelper.answer(mUser, mRoom, myAnswerIndex);

            btnAnswer.setBackgroundResource(R.drawable.answer1);

//              mWaitTime = new CountDownTimer(10000, 100) {
//                boolean isBlue = true;
//
//                public void onTick(long millisUntilFinished) {
//                    if ((millisUntilFinished / 100) % 5 == 0) {
//                        isBlue = !isBlue;
//                    }
//                    btnAnswer.setBackgroundResource(isBlue ? R.drawable.answer1 : R.drawable.answer_right);
//                }
//
//                           public void onFinish() {
//                    if (checkAns(answerRight)) {
//                        btnAnswer.setBackgroundResource(R.drawable.answer_right);
//
//                    } else {
//                        btnAnswer.setBackgroundResource(R.drawable.answer_wrong);
//                    }
//              }
//             };
//              mWaitTime.start();
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
    protected void onDestroy() {
        super.onDestroy();
        if(EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
