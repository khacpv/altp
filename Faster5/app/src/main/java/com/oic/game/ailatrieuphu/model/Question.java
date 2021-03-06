package com.oic.game.ailatrieuphu.model;


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

    @SerializedName("answerRight")
    public Integer mCorrectAnsId;

    @SerializedName("questionIndex")
    public int questionIndex = -1;

}
