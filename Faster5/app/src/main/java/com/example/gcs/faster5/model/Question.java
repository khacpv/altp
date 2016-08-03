package com.example.gcs.faster5.model;


import android.view.View;
import android.widget.Button;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Kien on 07/12/2016.
 */
public class Question {
    @SerializedName("question")
    String mQuestion;

    @SerializedName("answers")
    List<String> mAns;

    @SerializedName("id")
    Integer mStt;

    @SerializedName("answer_right")
    Integer mCorrectAnsId;

    public Question(Integer stt, String question, List<String> ans, Integer idAnsCorrect) {
        this.mStt = stt;
        this.mQuestion = question;
        this.mAns = ans;
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

    public Integer getIdAnsCorrect() {
        return mCorrectAnsId;
    }

    public void setIdAnsCorrect(Integer idAnsCorrect) {
        this.mCorrectAnsId = idAnsCorrect;
    }

    public List<String> getmAns() {
        return mAns;
    }

    public void setmAns(List<String> mAns) {
        this.mAns = mAns;
    }
}
