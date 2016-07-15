package com.example.gcs.faster5;


/**
 * Created by Kien on 07/12/2016.
 */
public class ListQuestion {
    String mQuestion, mAns1, mAns2, mAns3, mAns4;
    Integer mStt, mCorrectAnsId;

    public ListQuestion(Integer stt, String question, String ans1, String ans2, String ans3, String ans4, Integer idAnsCorrect) {
        this.mStt = stt;
        this.mQuestion = question;
        this.mAns1 = ans1;
        this.mAns2 = ans2;
        this.mAns3 = ans3;
        this.mAns4 = ans4;
        this.mCorrectAnsId = idAnsCorrect;
    }

    public Integer getStt() {
        return mStt;
    }

    public void setStt(Integer stt) {
        this.mStt = stt;
    }

    public String getQuestion() {
        return mQuestion;
    }

    public void setQuestion(String question) {
        this.mQuestion = question;
    }

    public String getAns1() {
        return mAns1;
    }

    public void setAns1(String ans1) {
        this.mAns1 = ans1;
    }

    public String getAns2() {
        return mAns2;
    }

    public void setAns2(String ans2) {
        this.mAns2 = ans2;
    }

    public String getAns3() {
        return mAns3;
    }

    public void setAns3(String ans3) {
        this.mAns3 = ans3;
    }

    public String getAns4() {
        return mAns4;
    }

    public void setAns4(String ans4) {
        this.mAns4 = ans4;
    }

    public Integer getIdAnsCorrect() {
        return mCorrectAnsId;
    }

    public void setIdAnsCorrect(Integer idAnsCorrect) {
        this.mCorrectAnsId = idAnsCorrect;
    }
}
