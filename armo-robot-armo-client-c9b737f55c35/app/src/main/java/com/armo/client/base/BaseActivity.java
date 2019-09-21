package com.armo.client.base;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.armo.client.network.firebase.ConnectionListener;
import com.armo.client.network.firebase.FirebaseClientHandler;
import com.armorobot.client.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class BaseActivity extends AppCompatActivity implements ConnectionListener {

    protected CompositeDisposable compositeDisposable;

    @Nullable
    @BindView(R.id.toolbar)
    protected Toolbar toolbar;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setCancelable(true);
        FirebaseClientHandler.getInstance().addConnectionListener(this);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
        if (toolbar != null)
            setSupportActionBar(toolbar);
    }

    @Override
    public void onConnected() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        supportInvalidateOptionsMenu();
    }

    @Override
    public void onDisconnected() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        supportInvalidateOptionsMenu();
    }

    @Override
    public void onInvalidRobotScanned() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        supportInvalidateOptionsMenu();
        showToast(getString(R.string.invalid_robot_code));
    }

    protected void setupToolbar(boolean showHomeAsUp) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(showHomeAsUp);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.status_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_status:
                if (FirebaseClientHandler.getInstance().isConnected()) {
                    FirebaseClientHandler.getInstance().disconnect();
                } else {
                    IntentIntegrator intentIntegrator = new IntentIntegrator(this);
                    intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                    intentIntegrator.initiateScan();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_status);
        if (FirebaseClientHandler.getInstance().isConnected()) {
            item.setIcon(R.drawable.ic_connected);
        } else {
            item.setIcon(R.drawable.ic_disconnected);
        }
        return super.onPrepareOptionsMenu(menu);
    }


    protected void needToUnsubscribe(Disposable disposable) {
        if (compositeDisposable == null)
            compositeDisposable = new CompositeDisposable();

        compositeDisposable.add(disposable);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                FirebaseClientHandler.getInstance().connectToRobot(result.getContents());
                progressDialog.show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (compositeDisposable != null)
            compositeDisposable.clear();
        FirebaseClientHandler.getInstance().removeConnectionListener(this);

        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

    protected void showToast(String toastMessage) {
        Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show();
    }

}
