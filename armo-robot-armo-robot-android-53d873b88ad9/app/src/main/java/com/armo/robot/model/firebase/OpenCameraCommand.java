package com.armo.robot.model.firebase;


import com.armo.robot.utils.Constants;
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
