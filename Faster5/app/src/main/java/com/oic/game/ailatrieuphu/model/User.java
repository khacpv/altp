package com.oic.game.ailatrieuphu.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by FRAMGIA\pham.van.khac on 8/11/16.
 */
public class User implements Serializable{

    @SerializedName("id")
    public String id;

    @SerializedName("name")
    public String name;

    @SerializedName("address")
    public String address;

    @SerializedName("fbId")
    public String fbId;

    @SerializedName("avatar")
    public String avatar;

    @SerializedName("room")
    public String room; // room id

    @SerializedName("answerIndex")
    public int answerIndex;

    @SerializedName("score")
    public int score;

    @SerializedName("totalScore")
    public int totalScore;

    public boolean isDummy = true;

    @SerializedName("winner")
    public boolean isWinner = true;

    @SerializedName("lang")
    public String lang = "vi";

    @SerializedName("fcm")
    public String fcmToken;
}
