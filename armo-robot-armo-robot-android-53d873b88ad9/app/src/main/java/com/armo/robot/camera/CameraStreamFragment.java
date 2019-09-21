/* Copyright 2013 Foxdog Studios Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.armo.robot.camera;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.armo.robot.R;
import com.armo.robot.base.BaseFragment;
import com.armo.robot.utils.Constants;

import butterknife.BindView;

import static com.armo.robot.R.id.camera;

public final class CameraStreamFragment extends BaseFragment
        implements SurfaceHolder.Callback {

    @BindView(camera)
    SurfaceView cameraSurfaceView;
    private boolean mRunning = false;
    private boolean mPreviewDisplayCreated = false;
    private SurfaceHolder mPreviewDisplay = null;
    private CameraStreamer mCameraStreamer = null;
    private int mCameraIndex = 0;


    public static CameraStreamFragment newInstance(int cameraId){
        Bundle args = new Bundle();
        args.putInt(Constants.EXTRA_CAMERA_ID, cameraId);
        CameraStreamFragment cameraStreamFragment = new CameraStreamFragment();
        cameraStreamFragment.setArguments(args);
        return cameraStreamFragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_camera, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(getArguments() != null){
            mCameraIndex = getArguments().getInt(Constants.EXTRA_CAMERA_ID);
        }

        mPreviewDisplay = cameraSurfaceView.getHolder();
        mPreviewDisplay.addCallback(this);

    }

    @Override
    public void onResume() {
        super.onResume();
        mRunning = true;
        tryStartCameraStreamer();
    }

    @Override
    public void onPause() {
        super.onPause();
        mRunning = false;
        ensureCameraStreamerStopped();
    }

    @Override
    public void surfaceChanged(final SurfaceHolder holder, final int format,
                               final int width, final int height) {
    }

    @Override
    public void surfaceCreated(final SurfaceHolder holder) {
        mPreviewDisplayCreated = true;
        tryStartCameraStreamer();
    }

    @Override
    public void surfaceDestroyed(final SurfaceHolder holder) {
        mPreviewDisplayCreated = false;
        ensureCameraStreamerStopped();
    }

    private void tryStartCameraStreamer() {
        if (mRunning && mPreviewDisplayCreated) {
            mCameraStreamer = new CameraStreamer(mCameraIndex, (mCameraIndex == 0), Constants.STREAM_PORT,
                    0, 100, mPreviewDisplay);
            mCameraStreamer.start();
        }
    }

    private void ensureCameraStreamerStopped() {
        if (mCameraStreamer != null) {
            mCameraStreamer.stop();
            mCameraStreamer = null;
        }
    }
}

