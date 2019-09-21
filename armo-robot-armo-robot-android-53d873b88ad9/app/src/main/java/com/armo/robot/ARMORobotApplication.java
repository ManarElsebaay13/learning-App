package com.armo.robot;

import android.app.Application;

import com.armo.robot.network.firebase.FirebaseRobotHandler;
import com.armo.robot.utils.Utils;
import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;


public class ARMORobotApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Fabric.with(this, new Crashlytics());

    }
}
