package com.example.gcs.faster5.ui.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.gcs.faster5.R;
import com.example.gcs.faster5.logic.QuestionMng;
import com.example.gcs.faster5.util.PrefUtils;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;

import java.util.List;

/**
 * Created by Kien on 07/05/2016.
 * test
 */
public class MainScreen extends AppCompatActivity {

    private final String TXT_TIME_OUT = "TIME\nOUT";

    ImageView mImageViewUserAvatar1, mImageViewUserAvatar2;
    TextView mTextViewScore1, mTextViewScore2, mTextViewRound, mTextViewQuestion,
            mTextViewAns1, mTextViewAns2, mTextViewAns3, mTextViewAns4, mTextViewTimer,
            mTextViewUserName1, mTextViewUserName2,
            mTextViewCityUser1, mTextViewCityUser2, mTextViewMoneyQuestion;
    String mQuestion, mAns1, mAns2, mAns3, mAns4;
    int mCorrectAnsId, mStt = 1, mUserScore1 = 0, mUserScore2 = 0;
    Button[] mButtonAns;
    CountDownTimer mTimeLeft, mWaitTimeNextQues, mWaitTime;
    boolean clickable = true;
    long timeLeft;
    int mMoney = 0;
    public String URL;

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

        mTimeLeft = new CountDownTimer(12000, 1000) {
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
        mTextViewTimer.setTypeface(font);
        mTextViewTimer.setBackgroundResource(R.drawable.clock);

        mTextViewRound = (TextView) findViewById(R.id.textview_numberquestion);
        mTextViewRound.setTypeface(font);

        mTextViewScore1 = (TextView) findViewById(R.id.textview_money1);
        mTextViewScore1.setTypeface(font);
        mTextViewScore1.setText(String.valueOf(mUserScore1));
        mTextViewScore2 = (TextView) findViewById(R.id.textview_money2);
        mTextViewScore2.setTypeface(font);
        mTextViewScore2.setText(String.valueOf(mUserScore2));

        mTextViewQuestion = (TextView) findViewById(R.id.textview_tablequestion);
        mTextViewAns1 = (TextView) findViewById(R.id.button_ans1);
        mTextViewAns2 = (TextView) findViewById(R.id.button_ans2);
        mTextViewAns3 = (TextView) findViewById(R.id.button_ans3);
        mTextViewAns4 = (TextView) findViewById(R.id.button_ans4);

        mTextViewQuestion.setTypeface(font);
        mTextViewAns1.setTypeface(font);
        mTextViewAns2.setTypeface(font);
        mTextViewAns3.setTypeface(font);
        mTextViewAns4.setTypeface(font);

        mTextViewUserName1 = (TextView) findViewById(R.id.textview_username1);
        mTextViewUserName1.setTypeface(font);
        mTextViewUserName2 = (TextView) findViewById(R.id.textview_username2);
        mTextViewUserName2.setTypeface(font);

        mImageViewUserAvatar1 = (ImageView) findViewById(R.id.imageview_useravatar1);
        mImageViewUserAvatar2 = (ImageView) findViewById(R.id.imageview_useravatar2);

        mTextViewCityUser1 = (TextView) findViewById(R.id.textview_city_user1);
        mTextViewCityUser2 = (TextView) findViewById(R.id.textview_city_user2);
        mTextViewCityUser1.setTypeface(font);
        mTextViewCityUser2.setTypeface(font);

        mTextViewMoneyQuestion = (TextView) findViewById(R.id.textview_moneyquestion);


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
        mTextViewUserName1.setText(PrefUtils.getInstance(MainScreen.this).get(PrefUtils.KEY_NAME, ""));
        Glide.with(getApplicationContext()).load(PrefUtils.getInstance(MainScreen.this).get(PrefUtils.KEY_URL_AVATAR, ""))
                .into(mImageViewUserAvatar1);
        mTextViewCityUser1.setText(PrefUtils.getInstance(MainScreen.this).get(PrefUtils.KEY_LOCATION, ""));


        mTextViewUserName2.setText(PrefUtils.getInstance(MainScreen.this).get(PrefUtils.KEY_ENEMY_NAME, ""));
        Glide.with(getApplicationContext()).load(PrefUtils.getInstance(MainScreen.this).get(PrefUtils.KEY_ENEMY_AVATAR, ""))
                .into(mImageViewUserAvatar2);
        mTextViewCityUser2.setText(PrefUtils.getInstance(MainScreen.this).get(PrefUtils.KEY_ENEMY_LOCATION, ""));
    }

    public void setQA(int stt) {
        this.mStt = stt;

  /*      if (mStt == 0) {
            mQuestion = getIntent().getExtras().getString("mQuestion");
            mAns1 = getIntent().getExtras().getString("mAns1");
            mAns2 = getIntent().getExtras().getString("mAns2");
            mAns3 = getIntent().getExtras().getString("mAns3");
            mAns4 = getIntent().getExtras().getString("mAns4");
            mCorrectAnsId = getIntent().getExtras().getInt("mCorrectAnsId");
        }*/
        mTextViewQuestion.setText(mQuestion);
        mTextViewAns1.setText("A: " + mAns1);
        mTextViewAns2.setText("B: " + mAns2);
        mTextViewAns3.setText("C: " + mAns3);
        mTextViewAns4.setText("D: " + mAns4);


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
                    btnAnswer.setBackgroundResource(isBlue ? R.drawable.answer1 : R.drawable.answer_right);
                }

                public void onFinish() {
                    if (checkAns(_answerIndex)) {
                        clickable = false;
                        btnAnswer.setBackgroundResource(R.drawable.answer1);
                        plusPoint();
                        correct(btnAnswer);

                    } else {
                        clickable = false;
                        btnAnswer.setBackgroundResource(R.drawable.answer_wrong);
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
                //  setNewQuestion();
                mWaitTime.cancel();
                mWaitTimeNextQues.cancel();
            }
        };
        mWaitTimeNextQues.start();
    }

    public void inCorrect() {
        mWaitTimeNextQues = new CountDownTimer(2000, 100) {
            public void onTick(long millisUntilFinished) {
                mButtonAns[mCorrectAnsId].setBackgroundResource(R.drawable.answer_right);
            }

            public void onFinish() {
                mWaitTime.cancel();
                mWaitTimeNextQues.cancel();
                //gameOver();
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
          //  gameOver();
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
        finish();
        overridePendingTransition(R.animator.right_in, R.animator.left_out);
    }

    public void onBackPressed() {

    }

}
