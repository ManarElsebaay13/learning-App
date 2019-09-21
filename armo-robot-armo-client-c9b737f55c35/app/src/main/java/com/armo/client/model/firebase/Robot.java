package com.armo.client.model.firebase;

import com.armo.client.utils.Constants;
import com.google.gson.annotations.SerializedName;



public class Robot {



    @SerializedName(Constants.FIREBASE_DEVICE_NAME)
    public String name;
    @SerializedName(Constants.FIREBASE_DEVICE_STREAM_URL)
    public String streamUrl;


    public Robot() {
    }

    public Robot(String name, String streamUrl) {
        this.name = name;
        this.streamUrl = streamUrl;
    }
}
