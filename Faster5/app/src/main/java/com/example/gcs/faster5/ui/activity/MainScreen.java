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
    String mQuestion, mAns1, mAns2, mAns3, mAns4, username2;
    Integer mCorrectAnsId, mStt = 1, mUserScore1 = 0, mUserScore2 = 0;
    Button[] mButtonAns;
    CountDownTimer mTimeLeft, mWaitTimeNextQues, mWaitTime;
    boolean clickable = true;
    long timeLeft;
    int mMoney = 0;
    public String URL;
    int idUser2;

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
        mTextViewTimer.setTypeface(font);
        mTextViewTimer.setBackgroundResource(R.drawable.clock);

      /*  Bitmap mBitmap = null;
        BitmapDrawable mDrawable = new BitmapDrawable(mBitmap);
        mTextViewTimer.setBackground(mDrawable);*/

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

        if (mTextViewCityUser1 != null) {
            mTextViewCityUser1.setText(LoginScreen.city.toUpperCase());
        } else {
            mTextViewCityUser1.setText("VIETNAM");
        }
        mTextViewCityUser2.setText("VIETNAM");

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username2 = extras.getString("NAMEUSER2");
            idUser2 = extras.getInt("IDUSER2");
            linkAvatarUser2(idUser2);
            mTextViewUserName2.setText(username2);
        }

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

        setQA(0);
        setTxtRound(mStt + 1);
    }

    public void setQA(int stt) {
        this.mStt = stt;
        mQuestion = SearchOpponent.questions.get(stt).getQuestion();
        mAns1 = SearchOpponent.questions.get(stt).getmAns().get(0);
        mAns2 = SearchOpponent.questions.get(stt).getmAns().get(1);
        mAns3 = SearchOpponent.questions.get(stt).getmAns().get(2);
        mAns4 = SearchOpponent.questions.get(stt).getmAns().get(3);
        mCorrectAnsId = SearchOpponent.questions.get(stt).getIdAnsCorrect();
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



    public void linkAvatarUser2(int x) {
        switch (x) {
            case 0:
                URL = "http://img.saobiz.net/d/2016/05/ngoc-trinh-giaoduc999-01084727_03.jpg";
                Glide.with(getApplicationContext())
                        .load(URL).into(mImageViewUserAvatar2);
                break;
            case 1:
                URL = "http://media.doisongphapluat.com/2015/07/27/angela_2_dspl.jpg";
                Glide.with(getApplicationContext())
                        .load(URL).into(mImageViewUserAvatar2);
                break;
            case 2:
                URL = "http://congly.com.vn/data/news/2016/3/8/83/hoahaukyduyen.jpg";
                Glide.with(getApplicationContext())
                        .load(URL).into(mImageViewUserAvatar2);
                break;
            case 3:
                URL = "http://media.hotbirthdays.com/upload/2015/05/24/nguyen-ngoc-ngan.jpg";
                Glide.with(getApplicationContext())
                        .load(URL).into(mImageViewUserAvatar2);
                break;
            case 4:
                URL = "http://img.saobiz.net/d/2015/10/nhung2-1443749932505-56-0-362-600-crop-1443750244536.jpg";
                Glide.with(getApplicationContext())
                        .load(URL).into(mImageViewUserAvatar2);
                break;
            case 5:
                URL = "http://www.phunuvagiadinh.vn/uploads/2016/03/24/1385548239736_500-20160324-00031574.jpg";
                Glide.with(getApplicationContext())
                        .load(URL).into(mImageViewUserAvatar2);
                break;
            case 6:
                URL = "http://media.tinmoi.vn/2015/07/23/ho-ngoc-ha-tm1.jpg";
                Glide.with(getApplicationContext())
                        .load(URL).into(mImageViewUserAvatar2);
                break;
            case 7:
                URL = "http://phunutoday.vn/upload_images/images/2016/07/20/lai-van-sam-phunutoday_vn.jpg";
                Glide.with(getApplicationContext())
                        .load(URL).into(mImageViewUserAvatar2);
                break;
        }

    }

    public void onBackPressed() {

    }

}
