package com.example.gcs.faster5.ui.activity;

import android.content.Intent;
import android.graphics.Typeface;
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
import com.example.gcs.faster5.model.Question;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;

/**
 * Created by Kien on 07/05/2016.
 * test
 */
public class MainScreen extends AppCompatActivity {

    private final String TXT_TIME_OUT = "TIME OUT";

    RelativeLayout mRelativeLayoutBg;
    ImageView mImageViewUserAvatar1, mImageViewUserAvatar2;
    TextView mTextViewTimeLeft, mTextViewScore1, mTextViewScore2, mTextViewRound, mTextViewQuestion,
            mTextViewAns1, mTextViewAns2, mTextViewAns3, mTextViewAns4, mTextViewTimer, mTextViewNameUser1, mTextViewNameUser2;
    String mQuestion, mAns1, mAns2, mAns3, mAns4;
    Integer mCorrectAnsId, mTopicId, mStt = 1, mUserScore1 = 0, mUserScore2 = 0;
    Button[] mButtonAns;
    Question mListQuestion;
    CountDownTimer mTimeLeft, mWaitTimeNextQues, mWaitTime;
    boolean clickable = true;
    long timeLeft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
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

        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/dimboregular.ttf");

        mRelativeLayoutBg = (RelativeLayout) findViewById(R.id.background);
        mRelativeLayoutBg.setBackgroundResource(R.drawable.background);
        mTextViewTimeLeft = (TextView) findViewById(R.id.text_timeleft);
        mTextViewTimeLeft.setTypeface(font);
        mTextViewTimer = (TextView) findViewById(R.id.text_timer);
        mTextViewTimer.setTypeface(font);
        mTextViewRound = (TextView) findViewById(R.id.text_roundnumber);
        mTextViewRound.setTypeface(font);
        mTextViewScore1 = (TextView) findViewById(R.id.text_userscore1);
        mTextViewScore1.setTypeface(font);
        mTextViewScore1.setText(String.valueOf(mUserScore1));
        mTextViewScore2 = (TextView) findViewById(R.id.text_userscore2);
        mTextViewScore2.setTypeface(font);
        mTextViewScore2.setText(String.valueOf(mUserScore2));

        mTextViewQuestion = (TextView) findViewById(R.id.text_question);
        mTextViewAns1 = (TextView) findViewById(R.id.button_ans1);
        mTextViewAns2 = (TextView) findViewById(R.id.button_ans2);
        mTextViewAns3 = (TextView) findViewById(R.id.button_ans3);
        mTextViewAns4 = (TextView) findViewById(R.id.button_ans4);

        mTextViewQuestion.setTypeface(font);
        mTextViewAns1.setTypeface(font);
        mTextViewAns2.setTypeface(font);
        mTextViewAns3.setTypeface(font);
        mTextViewAns4.setTypeface(font);

        mTextViewNameUser1 = (TextView) findViewById(R.id.text_username1);
        mTextViewNameUser1.setTypeface(font);
        mTextViewNameUser2 = (TextView) findViewById(R.id.text_username2);
        mTextViewNameUser2.setTypeface(font);
        mImageViewUserAvatar1 = (ImageView) findViewById(R.id.image_useravatar1);
        setAvatar();

        for (int i = 0; i < 4; i++) {
            if (i == 0) {
                mButtonAns[i] = (Button)mTextViewAns1;
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
            mButtonAns[i].setBackgroundResource(R.drawable.opt);
        }

        Bundle extrasName = getIntent().getExtras();
        if (extrasName != null) {
            mCorrectAnsId = extrasName.getInt(SearchOpponent.EXTRA_ANSWER_RIGHT);
        }
        QuestionMng.sTopicId = mCorrectAnsId;
        setQA(0, QuestionMng.sTopicId);

        setTxtRound(mStt+1);

    }

    public void setQA(int stt, int idTopic) {
        this.mStt = stt;
        this.mTopicId = idTopic;
        mListQuestion = QuestionMng.getQuestion().get(stt);
        mQuestion = mListQuestion.getQuestion();
        mAns1 = mListQuestion.getAns1();
        mAns2 = mListQuestion.getAns2();
        mAns3 = mListQuestion.getAns3();
        mAns4 = mListQuestion.getAns4();
        mCorrectAnsId = mListQuestion.getIdAnsCorrect();
        mTextViewQuestion.setText(mQuestion);
        mTextViewAns1.setText(mAns1);
        mTextViewAns2.setText(mAns2);
        mTextViewAns3.setText(mAns3);
        mTextViewAns4.setText(mAns4);
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
                    btnAnswer.setBackgroundResource(isBlue ? R.drawable.opttrue : R.drawable.optfail);
                }

                public void onFinish() {
                    if (checkAns(_answerIndex)) {
                        clickable = false;
                        btnAnswer.setBackgroundResource(R.drawable.opttrue);
                        plusPoint();
                        correct(btnAnswer);

                    } else {
                        clickable = false;
                        btnAnswer.setBackgroundResource(R.drawable.optfail);
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
                mButtonAns[mCorrectAnsId].setBackgroundResource(R.drawable.opttrue);
            }

            public void onFinish() {
                mWaitTime.cancel();
                mWaitTimeNextQues.cancel();
                gameOver();
            }
        };
        mWaitTimeNextQues.start();
    }

    public void plusPoint(){
        mUserScore1 = mUserScore1 + (int) timeLeft;
        mTextViewScore1.setText(String.valueOf(mUserScore1));
    }

    public void setNewQuestion() {
        clickable = true;
        mStt = mStt + 1;
        setTxtRound(mStt+1);
        if (mStt == (QuestionMng.listQuestion.size())) {
            gameOver();
        } else {
            setQA(mStt, mTopicId);
            for (int i = 0; i < 4; i++) {
                mButtonAns[i].setBackgroundResource(R.drawable.opt);
            }
            mTimeLeft.start();
        }
    }

    private void setTxtRound(int round){
        String txtRound = String.format("ROUND %s OF %s", round, QuestionMng.listQuestion.size());
        mTextViewRound.setText(txtRound);
    }

    public void gameOver() {
        Intent intent = new Intent(getApplicationContext(), GameOver.class);
        intent.putExtra(GameOver.EXTRA_SCORE, mUserScore1);
        startActivity(intent);
        finish();
    }

    public void setAvatar() {
        FacebookSdk.sdkInitialize(getApplicationContext(), new FacebookSdk.InitializeCallback() {
                    @Override
                    public void onInitialized() {
                        AccessToken accessToken = AccessToken.getCurrentAccessToken();
                        if (accessToken == null) {
                            mTextViewNameUser1.setText(InfoScreen.sManualName);
                        } else {
                            mTextViewNameUser1.setText(InfoScreen.sFullNameFb);
                            Glide.with(getApplicationContext())
                                    .load("https://graph.facebook.com/" + InfoScreen.sUserFbId + "/picture?width=500&height=500").into(mImageViewUserAvatar1);
                        }
                    }
                }
        );
    }


    public void onBackPressed() {
        Intent myIntent = new Intent(getApplicationContext(), GameOver.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(myIntent);
        finish();
    }
}
