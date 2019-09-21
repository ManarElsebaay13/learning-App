package com.armo.robot;

import android.Manifest;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.armo.robot.base.BaseActivity;
import com.armo.robot.camera.CameraStreamFragment;
import com.armo.robot.model.firebase.Command;
import com.armo.robot.model.firebase.OpenCameraCommand;
import com.armo.robot.model.firebase.PlayVideoCommand;
import com.armo.robot.network.firebase.FirebaseRobotHandler;
import com.armo.robot.qrcode.QRCodeGenerator;
import com.armo.robot.utils.Constants;
import com.armo.robot.utils.OnCommandListener;
import com.armo.robot.utils.Utils;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import java.util.List;

import butterknife.BindView;
import io.reactivex.disposables.Disposable;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class HomeActivity extends BaseActivity implements OnCommandListener, YouTubePlayer.OnInitializedListener, EasyPermissions.PermissionCallbacks {


    @BindView(R.id.qrcode)
    ImageView qrCode;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;


    YouTubePlayer youTubePlayer;
    YouTubePlayerSupportFragment youTubePlayerFragment;
    String videoId;

    CameraStreamFragment cameraStreamFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        progressBar.setVisibility(View.VISIBLE);

        FirebaseRobotHandler.getInstance().setIpAddress(Utils.getIpAddress(this));

        FirebaseRobotHandler.getInstance().setOnCommandListener(this);

        FirebaseRobotHandler.getInstance().signin();

        requestCameraPermission();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FirebaseRobotHandler.getInstance().setOnCommandListener(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_status);
        if (FirebaseRobotHandler.getInstance().isClientConnected()) {
            item.setIcon(R.drawable.ic_connected);
            item.setEnabled(true);
        } else {
            item.setIcon(R.drawable.ic_disconnected);
            item.setEnabled(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_status) {
            FirebaseRobotHandler.getInstance().disconnect();
            progressBar.setVisibility(View.VISIBLE);
            item.setEnabled(false);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClientConnected() {
        supportInvalidateOptionsMenu();
        progressBar.setVisibility(View.GONE);
        qrCode.setVisibility(View.GONE);
        qrCode.setImageBitmap(null);
    }

    @Override
    public void onClientDisconnected() {
        supportInvalidateOptionsMenu();
        setupQRCode();
    }


    private void setupQRCode() {
        if (FirebaseRobotHandler.getInstance().isSignedIn()) {
            progressBar.setVisibility(View.VISIBLE);
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int width = displayMetrics.widthPixels;
            Disposable disposable =
                    QRCodeGenerator.getQRCode(FirebaseRobotHandler.getInstance().getRobotId(), (int) (width * 0.9))
                            .subscribe(this::onQRCodeCreated);

            needToUnsubscribe(disposable);
        }
    }

    void onQRCodeCreated(Bitmap bitmap) {
        progressBar.setVisibility(View.GONE);
        qrCode.setVisibility(View.VISIBLE);
        qrCode.setImageBitmap(bitmap);
    }

    @Override
    public void onCommand(Command command) {
        switch (command.action) {
            case Constants.ACTION_PLAY_VIDEO:
                playVideo(((PlayVideoCommand) command).videoId);
                break;
            case Constants.ACTION_STOP_VIDEO:
                stopVideo();
                break;
            case Constants.ACTION_OPEN_CAMERA:
                openCamera(((OpenCameraCommand) command).cameraId);
                break;
            case Constants.ACTION_CLOSE_CAMERA:
                stopCamera();
                break;
        }
    }

    private void playVideo(String videoId) {
        this.videoId = videoId;
        if (youTubePlayerFragment == null)
            youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);

        if ((fragment == null || !(fragment instanceof YouTubePlayerSupportFragment)) && !youTubePlayerFragment.isAdded()) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            if (fragment == null) {
                fragmentTransaction.add(R.id.fragment_container, youTubePlayerFragment, YouTubePlayerSupportFragment.class.getSimpleName());
            } else {
                fragmentTransaction.replace(R.id.fragment_container, youTubePlayerFragment, YouTubePlayerSupportFragment.class.getSimpleName());
            }
            fragmentTransaction.commitAllowingStateLoss();
        }
        if (youTubePlayer == null) {
            youTubePlayerFragment.initialize(Constants.YOUTUBE_API_KEY, this);
        } else {
            if (youTubePlayer.isPlaying())
                youTubePlayer.pause();
            youTubePlayer.loadVideo(videoId);
        }
    }

    private void stopVideo() {
        if (youTubePlayer != null) {
            youTubePlayer.pause();
            youTubePlayer.setFullscreen(false);
            youTubePlayer = null;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);
        if (fragment instanceof YouTubePlayerSupportFragment) {
            fragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss();
            youTubePlayerFragment = null;
        }
    }

    private void openCamera(int cameraId) {

        if (cameraStreamFragment != null)
            return;

        cameraStreamFragment = CameraStreamFragment.newInstance(cameraId);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (fragment == null) {
            fragmentTransaction.add(R.id.fragment_container, cameraStreamFragment, YouTubePlayerSupportFragment.class.getSimpleName());
        } else {
            fragmentTransaction.replace(R.id.fragment_container, youTubePlayerFragment, YouTubePlayerSupportFragment.class.getSimpleName());
        }
        fragmentTransaction.commitAllowingStateLoss();
    }

    private void stopCamera() {
        if (cameraStreamFragment == null)
            return;

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);
        if (fragment instanceof CameraStreamFragment) {
            fragmentManager.beginTransaction().remove(cameraStreamFragment).commitAllowingStateLoss();
            cameraStreamFragment = null;
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        if (!wasRestored) {
            this.youTubePlayer = youTubePlayer;
            this.youTubePlayer.setFullscreen(true);
            this.youTubePlayer.loadVideo(videoId);
        }

    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        showToast(getString(R.string.error_occurred));
    }

    @AfterPermissionGranted(Constants.RC_CAMERA_PERM)
    private void requestCameraPermission() {
        if (!EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)) {
            EasyPermissions.requestPermissions(this, getString(R.string.camera),
                    Constants.RC_CAMERA_PERM, Manifest.permission.CAMERA);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        finish();
    }

    @Override
    public void onBackPressed() {
        // Do nothing to disable back button
    }
}

