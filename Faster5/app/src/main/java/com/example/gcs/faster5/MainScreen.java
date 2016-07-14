package com.example.gcs.faster5;


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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;

/**
 * Created by Kien on 07/05/2016.
 * test
 */
public class MainScreen extends AppCompatActivity {
    RelativeLayout backGround;
    ImageView avatarUser1ImgV, avatarUser2ImgV;
    TextView timeleftTxtV, score1TxtV, score2TxtV, roundTxtV, questionTxtV,
            ansTxtV1, ansTxtV2, ansTxtV3, ansTxtV4, timerTxtV, userName1TxtV, userName2TxtV;
    AccessToken accessToken;
    String question, ans1, ans2, ans3, ans4;
    Integer idCorrectAns, idTopic, stt = 1;
    Button[] ansImgB;
    ListQuestion questionList;
    CountDownTimer timeLeft, waitTimenextQues, waitTime;
    boolean clickable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.main_screen);

        timeLeft = new CountDownTimer(12000, 1000) {
            public void onTick(long millisUntilFinished) {
                long timeLeft = (millisUntilFinished / 1000) - 1;
                if (timeLeft > 0) {
                    timerTxtV.setText("" + timeLeft);
                } else {
                    timerTxtV.setText("TIME OUT");
                    clickable = false;
                    gameOver();
                }
            }
            public void onFinish() {
                timerTxtV.setText("TIME OUT");
                clickable = false;
            }
        };
        ansImgB = new Button[4];

        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/dimboregular.ttf");

        backGround = (RelativeLayout) findViewById(R.id.background);
        backGround.setBackgroundResource(R.drawable.background);
        timeleftTxtV = (TextView) findViewById(R.id.timeLeft);
        timeleftTxtV.setTypeface(font);
        timerTxtV = (TextView) findViewById(R.id.timer);
        timerTxtV.setTypeface(font);
        roundTxtV = (TextView) findViewById(R.id.noRound);
        roundTxtV.setTypeface(font);
        score1TxtV = (TextView) findViewById(R.id.scoreUser1);
        score1TxtV.setTypeface(font);
        score2TxtV = (TextView) findViewById(R.id.scoreUser2);
        score2TxtV.setTypeface(font);

        questionTxtV = (TextView) findViewById(R.id.question);
        ansTxtV1 = (TextView) findViewById(R.id.ans1);
        ansTxtV2 = (TextView) findViewById(R.id.ans2);
        ansTxtV3 = (TextView) findViewById(R.id.ans3);
        ansTxtV4 = (TextView) findViewById(R.id.ans4);

        questionTxtV.setTypeface(font);
        ansTxtV1.setTypeface(font);
        ansTxtV2.setTypeface(font);
        ansTxtV3.setTypeface(font);
        ansTxtV4.setTypeface(font);

        userName1TxtV = (TextView) findViewById(R.id.userName1);
        userName1TxtV.setTypeface(font);
        userName2TxtV = (TextView) findViewById(R.id.userName2);
        userName2TxtV.setTypeface(font);
        avatarUser1ImgV = (ImageView) findViewById(R.id.avatarUser1);
        setAvatar();

        for (int i = 0; i < 4; i++) {
            if (i == 0) {
                ansImgB[i] = (Button) findViewById(R.id.ans1);
            }
            if (i == 1) {
                ansImgB[i] = (Button) findViewById(R.id.ans2);
            }
            if (i == 2) {
                ansImgB[i] = (Button) findViewById(R.id.ans3);
            }
            if (i == 3) {
                ansImgB[i] = (Button) findViewById(R.id.ans4);
            }
            ansImgB[i].setBackgroundResource(R.drawable.opt);
        }

        Bundle extrasName = getIntent().getExtras();
        if (extrasName != null) {
            idTopic = extrasName.getInt("IDTOPIC");
        }
        Question.idTopic = idTopic;
        setQA(0, Question.idTopic);

        roundTxtV.setText("ROUND " + (stt + 1) + " OF " + Question.listQuestion.size());

    }

    public void setQA(int stt, int idTopic) {
        this.stt = stt;
        this.idTopic = idTopic;
        questionList = Question.getQuestion().get(stt);
        question = questionList.getQuestion();
        ans1 = questionList.getAns1();
        ans2 = questionList.getAns2();
        ans3 = questionList.getAns3();
        ans4 = questionList.getAns4();
        idCorrectAns = questionList.getIdAnsCorrect();
        questionTxtV.setText(question);
        ansTxtV1.setText(ans1);
        ansTxtV2.setText(ans2);
        ansTxtV3.setText(ans3);
        ansTxtV4.setText(ans4);
        timeLeft.start();
    }

    public boolean checkAns(int answerIndex) {
        return answerIndex == idCorrectAns;
    }

    public void btnAnswerClick(final View btnAnswer) {
        if (clickable) {
            timeLeft.cancel();
            int answerIndex = 0;
            switch (btnAnswer.getId()) {
                case R.id.ans1:
                    answerIndex = 0;
                    break;
                case R.id.ans2:
                    answerIndex = 1;
                    break;
                case R.id.ans3:
                    answerIndex = 2;
                    break;
                case R.id.ans4:
                    answerIndex = 3;
                    break;
            }
            final int _answerIndex = answerIndex;
            waitTime = new CountDownTimer(3000, 100) {
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
                        correct(btnAnswer);

                    } else {
                        clickable = false;
                        btnAnswer.setBackgroundResource(R.drawable.optfail);
                        inCorrect();
                    }
                }
            };
            waitTime.start();
        }
    }

    public void correct(final View btnAnswer) {
        waitTimenextQues = new CountDownTimer(2000, 100) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                setNewQuestion();
                waitTime.cancel();
                waitTimenextQues.cancel();
            }
        };
        waitTimenextQues.start();
    }

    public void inCorrect() {
        waitTimenextQues = new CountDownTimer(2000, 100) {
            public void onTick(long millisUntilFinished) {
                ansImgB[idCorrectAns].setBackgroundResource(R.drawable.opttrue);
            }

            public void onFinish() {
                waitTime.cancel();
                waitTimenextQues.cancel();
                gameOver();
            }
        };
        waitTimenextQues.start();
    }

    public void setNewQuestion() {
        clickable = true;
        stt = stt + 1;
        roundTxtV.setText("ROUND " + (stt + 1) + " OF " + Question.listQuestion.size());
        if (stt == (Question.listQuestion.size())) {
            gameOver();
        } else {
            setQA(stt, idTopic);
            for (int i = 0; i < 4; i++) {
                ansImgB[i].setBackgroundResource(R.drawable.opt);
            }
            timeLeft.start();
        }
    }

    public void gameOver() {
        Intent intent = new Intent(getApplicationContext(), GameOver.class);
        startActivity(intent);
        finish();
    }

    public void setAvatar() {
        FacebookSdk.sdkInitialize(getApplicationContext(), new FacebookSdk.InitializeCallback() {
                    @Override
                    public void onInitialized() {
                        //AccessToken is for us to check whether we have previously logged in into
                        //this app, and this information is save in shared preferences and sets it during SDK initialization
                        accessToken = AccessToken.getCurrentAccessToken();
                        if (accessToken == null) {
                            userName1TxtV.setText(InfoScreen.nameManual);
                        } else {
                            userName1TxtV.setText(InfoScreen.fullNameFb);
                            Glide.with(getApplicationContext())
                                    .load("https://graph.facebook.com/" + InfoScreen.idUserFB + "/picture?width=500&height=500").into(avatarUser1ImgV);
//                            if (checkInternetConnection(InfoScreen.this)) {
//                                GetUserInfo();
//                            } else {
//
//                            }
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
        return;
    }
}
