package com.example.gcs.faster5.model;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Kien on 07/12/2016.
 */
public class Question implements Serializable{
    @SerializedName("question")
    public String mQuestion;

    @SerializedName("answers")
    public List<String> mAns;

    @SerializedName("id")
    public Integer mStt;

    @SerializedName("answerRight")
    public Integer mCorrectAnsId;

}
