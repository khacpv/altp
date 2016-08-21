package com.example.gcs.faster5.ui.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.gcs.faster5.R;
import com.example.gcs.faster5.model.Question;
import com.example.gcs.faster5.model.Room;
import com.example.gcs.faster5.model.User;

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

    int mCorrectAnsId;
    int mStt = 1;
    int mUserScore1 = 0;
    int mUserScore2 = 0;

    Button[] mButtonAns;

    CountDownTimer mTimeLeft;
    CountDownTimer mWaitTimeNextQues;
    CountDownTimer mWaitTime;

    boolean clickable = true;
    long timeLeft;
    int mMoney = 0;

    User mUser;
    User mEnemy;
    Room mRoom;
    Question mQuestion;

    public static Intent createIntent(Context context, User user, User enemy, Room room, Question
            question) {
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
        if(getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.main_screen);

        getBundle();

        mTimeLeft = new CountDownTimer(12000, 1000) {
            public void onTick(long millisUntilFinished) {
                timeLeft = (millisUntilFinished / 1000) - 1;
                if (timeLeft > 0) {
                    mTextViewTimer.setText("" + timeLeft);
                } else {
                    mTextViewTimer.setText(TXT_TIME_OUT);
                    clickable = false;
                    gameOver();
                }
            }

            public void onFinish() {
                mTextViewTimer.setText(TXT_TIME_OUT);
                clickable = false;
            }
        };
        mButtonAns = new Button[4];

        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/roboto.ttf");

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

        // question info
        mTextViewQuestion.setText(mQuestion.mQuestion);
        mTextViewAns1.setText("A: " + mQuestion.mAns.get(0));
        mTextViewAns2.setText("B: " + mQuestion.mAns.get(1));
        mTextViewAns3.setText("C: " + mQuestion.mAns.get(2));
        mTextViewAns4.setText("D: " + mQuestion.mAns.get(3));

        // enemy info
        Glide.with(getApplicationContext()).load(mEnemy.avatar).into(mImageViewUserAvatar2);
        mTextViewUserName2.setText(mEnemy.name);
        mTextViewCityUser2.setText(mEnemy.address);
    }

    public void setQA(int stt) {
        this.mStt = stt;
        mTimeLeft.start();
    }

    public boolean checkAns(int answerIndex) {
        return answerIndex == mCorrectAnsId;
    }

    public void btnAnswerClick(final View btnAnswer) {
        if (clickable) {
            mTimeLeft.cancel();
            int answerIndex = 0;
            switch (btnAnswer.getId()) {
                case R.id.button_ans1:
                    answerIndex = 0;
                    break;
                case R.id.button_ans2:
                    answerIndex = 1;
                    break;
                case R.id.button_ans3:
                    answerIndex = 2;
                    break;
                case R.id.button_ans4:
                    answerIndex = 3;
                    break;
            }
            final int _answerIndex = answerIndex;
            mWaitTime = new CountDownTimer(3000, 100) {
                boolean isBlue = true;

                public void onTick(long millisUntilFinished) {
                    if ((millisUntilFinished / 100) % 5 == 0) {
                        isBlue = !isBlue;
                        clickable = false;
                    }
                    btnAnswer.setBackgroundResource(isBlue ? R.drawable.answer1 : R.drawable.answer3);
                }

                public void onFinish() {
                    if (checkAns(_answerIndex)) {
                        clickable = false;
                        btnAnswer.setBackgroundResource(R.drawable.answer1);
                        plusPoint();
                        correct(btnAnswer);

                    } else {
                        clickable = false;
                        btnAnswer.setBackgroundResource(R.drawable.answer3);
                        inCorrect();
                    }
                }
            };
            mWaitTime.start();
        }
    }

    public void correct(final View btnAnswer) {
        mWaitTimeNextQues = new CountDownTimer(2000, 100) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                setNewQuestion();
                mWaitTime.cancel();
                mWaitTimeNextQues.cancel();
            }
        };
        mWaitTimeNextQues.start();
    }

    public void inCorrect() {
        mWaitTimeNextQues = new CountDownTimer(2000, 100) {
            public void onTick(long millisUntilFinished) {
                mButtonAns[mCorrectAnsId].setBackgroundResource(R.drawable.answer1);
            }

            public void onFinish() {
                mWaitTime.cancel();
                mWaitTimeNextQues.cancel();
                gameOver();
            }
        };
        mWaitTimeNextQues.start();
    }

    public void plusPoint() {
        mUserScore1 = mUserScore1 + mMoney;
        mTextViewScore1.setText(String.valueOf(mUserScore1));
    }

    public void setNewQuestion() {
        mStt = mStt + 1;
        clickable = true;
        if (mStt == SearchOpponent.questions.size()) {
            setTxtRound(mStt);
        } else {
            setTxtRound(mStt + 1);
        }
        if (mStt == (SearchOpponent.questions.size())) {
            gameOver();
        } else {
            setQA(mStt);
            for (int i = 0; i < 4; i++) {
                mButtonAns[i].setBackgroundResource(R.drawable.answer0);
            }
            mTimeLeft.start();
        }

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
        intent.putExtra(GameOver.EXTRA_SCORE, mUserScore1);
        startActivity(intent);
        overridePendingTransition(R.animator.right_in, R.animator.left_out);
        finish();
    }

    public static void setTypeface(Typeface font, TextView... textviews) {
        for (TextView textView : textviews) {
            textView.setTypeface(font);
        }
    }
}
