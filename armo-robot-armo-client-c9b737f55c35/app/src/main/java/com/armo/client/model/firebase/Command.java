package com.armo.client.model.firebase;

import com.armo.client.utils.Constants;
import com.google.gson.annotations.SerializedName;


public abstract class Command {


    @SerializedName(Constants.FIREBASE_COMMAND_ACTION)
    public int action;
    @SerializedName(Constants.FIREBASE_COMMAND_TIMESTAMP)
    public long timeStamp;


    public Command(int action) {
        this.action = action;
        timeStamp = System.currentTimeMillis() / 1000;
    }
}
