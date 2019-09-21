package com.armo.robot.utils;


public class Constants {

    public static final String YOUTUBE_API_KEY = "AIzaSyCJoc3GHTomJZiWdR732S3VLsVJXZmxsAM";

    // Firebase
    public static final String FIREBASE_CLIENTS = "clients";
    public static final String FIREBASE_ROBOTS = "robots";
    public static final String FIREBASE_COMMANDS = "commands";
    public static final String FIREBASE_CLIENT_COMMANDS = "clientCommands";
    public static final String FIREBASE_CLIENT_ID = "clientId";
    public static final String FIREBASE_COMMAND_ACTION = "action";
    public static final String FIREBASE_COMMAND_TIMESTAMP = "timeStamp";
    public static final String FIREBASE_DEVICE_NAME = "name";
    public static final String FIREBASE_DEVICE_STREAM_URL = "streamUrl";
    public static final String FIREBASE_CAMERA_ID = "cameraId";

    // Actions
    public static final int ACTION_PLAY_VIDEO = 1;
    public static final int ACTION_STOP_VIDEO = 2;
    public static final int ACTION_OPEN_CAMERA = 3;
    public static final int ACTION_CLOSE_CAMERA = 4;

    // Extras
    public static final String EXTRA_CAMERA_ID ="EXTRA_CAMERA_ID";

    // Permissions
    public static final int RC_CAMERA_PERM = 123;


    public static final int STREAM_PORT = 8880;

}
