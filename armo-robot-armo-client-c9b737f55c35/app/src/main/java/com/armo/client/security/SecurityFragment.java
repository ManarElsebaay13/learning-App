package com.armo.client.security;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.armo.client.base.BaseFragment;
import com.armo.client.model.firebase.CloseCameraCommand;
import com.armo.client.model.firebase.OpenCameraCommand;
import com.armo.client.network.firebase.FirebaseClientHandler;
import com.armo.client.utils.Constants;
import com.armorobot.client.R;

import butterknife.OnClick;

public class SecurityFragment extends BaseFragment {


    public static SecurityFragment newInstance() {
        return new SecurityFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.security, container, false);
    }

    @OnClick(R.id.rearCamera)
    public void openRearCamera() {
        openCamera(0);
    }

    @OnClick(R.id.frontCamera)
    public void openFrontCamera() {
        openCamera(1);
    }

    private void openCamera(int cameraId) {
        FirebaseClientHandler.getInstance().sendCommand(new OpenCameraCommand(cameraId));

        String url = FirebaseClientHandler.getInstance().getStreamUrl();
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.enableUrlBarHiding()
                .setToolbarColor(ContextCompat.getColor(getContext(), R.color.colorPrimary))
                .setShowTitle(false)
                .build();

        customTabsIntent.intent.setData(Uri.parse(url));
        startActivityForResult(customTabsIntent.intent, Constants.REQUEST_CODE_OPEN_CAMERA);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE_OPEN_CAMERA) {
            FirebaseClientHandler.getInstance().sendCommand(new CloseCameraCommand());
        }
    }

    @Override
    public
    @StringRes
    int getTitle() {
        return R.string.security;
    }
}

