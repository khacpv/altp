package com.example.gcs.faster5.model;

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
    public String room;

    @SerializedName("answerIndex")
    public int answerIndex;

    public boolean isDummy = true;
}
