package in.tts.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.perf.metrics.AddTrace;

import java.io.File;

import in.tts.R;
import in.tts.fragments.LoginFragment;
import in.tts.fragments.RegisterFragment;
import in.tts.utils.CommonMethod;
import in.tts.utils.ToGetPdfFiles;

public class LoginActivity extends AppCompatActivity {

    @Override
    @AddTrace(name = "onCreateLoginActivity", enabled = true)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        try {
            String Status = getIntent().getStringExtra("LOGIN");
            if (Status == null) {
                CommonMethod.setAnalyticsData(LoginActivity.this, "MainTab", "Login1", null);
                replaceMainTabsFragment(new LoginFragment());
            } else if (Status.toLowerCase().equals("login")) {
                CommonMethod.setAnalyticsData(LoginActivity.this, "MainTab", "Login2", null);
                replaceMainTabsFragment(new LoginFragment());
            } else if (Status.toLowerCase().equals("register")) {
                CommonMethod.setAnalyticsData(LoginActivity.this, "MainTab", "Register", null);
                replaceMainTabsFragment(new RegisterFragment());
            } else {
                CommonMethod.setAnalyticsData(LoginActivity.this, "MainTab", "Login3", null);
                replaceMainTabsFragment(new LoginFragment());
            }
            fn_permission();
            CommonMethod.toReleaseMemory();
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            CommonMethod.toReleaseMemory();
        }
    }

    private void fn_permission() {
        try {
            CommonMethod.toReleaseMemory();
            if ((ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(LoginActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                toLoadData();
            }
            if ((ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
            if ((ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            CommonMethod.toReleaseMemory();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fn_permission();
            } else {
                CommonMethod.toDisplayToast(LoginActivity.this, "Please allow the permission");
            }
        } else {
            fn_permission();
        }
    }

    private void toLoadData() {
        try {
            if (!ToGetPdfFiles.isRunning()) {
                ToGetPdfFiles.getFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath()), LoginActivity.this);
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            CommonMethod.toReleaseMemory();
        }
    }

    private void replaceMainTabsFragment(Fragment fragment) throws Exception, Error {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragmentrepalce, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commitAllowingStateLoss();
        CommonMethod.toReleaseMemory();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            Log.d("TAG", " LoginActivity onActivityResult : " + resultCode + ":" + requestCode + " :");
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentrepalce);
            fragment.onActivityResult(requestCode, resultCode, data);
            CommonMethod.toReleaseMemory();
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        CommonMethod.toReleaseMemory();
    }

    @Override
    public void onPause() {
        super.onPause();
        CommonMethod.toCloseLoader();
        CommonMethod.toReleaseMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CommonMethod.toCloseLoader();
        CommonMethod.toReleaseMemory();
    }
}