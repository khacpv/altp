package com.example.gcs.faster5.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by FRAMGIA\pham.van.khac on 8/11/16.
 */
public class Room implements Serializable{

    @SerializedName("id")
    public String roomId;

    @SerializedName("users")
    public List<User> users;

    @SerializedName("questions")
    public List<Question> questions;

    @SerializedName("answerRight")
    public int answerRight;

    @SerializedName("questionIndex")
    public int questionIndex;
}
