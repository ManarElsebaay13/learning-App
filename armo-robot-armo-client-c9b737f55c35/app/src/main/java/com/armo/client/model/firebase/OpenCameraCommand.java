package com.armo.client.model.firebase;

import com.armo.client.utils.Constants;
import com.google.gson.annotations.SerializedName;

public class OpenCameraCommand extends Command {


    @SerializedName(Constants.FIREBASE_CAMERA_ID)
    public int cameraId;

    public OpenCameraCommand(){
        this(0);
    }

    public OpenCameraCommand(int cameraId) {
        super(Constants.ACTION_OPEN_CAMERA);
        this.cameraId = cameraId;
    }
}
