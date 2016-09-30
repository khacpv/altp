package com.oic.game.ailatrieuphu.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by kienht on 9/29/16.
 */

public class GameOverMessage implements Serializable {

    @SerializedName("win")
    public String win;

    @SerializedName("lose")
    public String lose;

    @SerializedName("draw")
    public String draw;
}
