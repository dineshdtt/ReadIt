package in.tts.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.perf.metrics.AddTrace;

import java.io.File;

import in.tts.R;
import in.tts.model.PrefManager;
import in.tts.model.User;
import in.tts.utils.CommonMethod;
import in.tts.utils.ToCheckFileExists;
import in.tts.utils.ToGetPdfFiles;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    private PrefManager prefManager;

    @Override
    @AddTrace(name = "onCreateSplashActivity", enabled = true)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_splash);

            CommonMethod.setAnalyticsData(SplashActivity.this, "MainTab", "splash", null);

            prefManager = new PrefManager(SplashActivity.this);
            auth = FirebaseAuth.getInstance();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    prefManager.getUserInfo();
                    if (auth.getCurrentUser() != null) {
                        startActivity(new Intent(SplashActivity.this, HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    } else if (User.getUser(SplashActivity.this).getId() != null) {
                        startActivity(new Intent(SplashActivity.this, HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    } else {
                        if (prefManager.isFirstTimeLaunch()) {
                            startActivity(new Intent(SplashActivity.this, TutorialPageActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        } else {
                            startActivity(new Intent(SplashActivity.this, LoginActivity.class).putExtra("LOGIN", "login").setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        }
                    }
                    finish();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                }
            }, 3000);

            prefManager.getAudioSetting();
            if ((ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
                try {
                    if (!ToGetPdfFiles.isRunning()) {
                        ToGetPdfFiles.getFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath()), SplashActivity.this);
                    }
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            if (prefManager.toGetPDFListRecent() != null) {
                                if (prefManager.toGetPDFListRecent().size() > 0) {
                                    prefManager.toSetPDFFileListRecent(ToCheckFileExists.fileList(prefManager.toGetPDFListRecent()));
                                }
                            }

                            if (prefManager.toGetImageListRecent() != null) {
                                if (prefManager.toGetImageListRecent().size() > 0) {
                                    prefManager.toSetImageFileListRecent(ToCheckFileExists.fileList(prefManager.toGetImageListRecent()));
                                }
                            }

                            if (prefManager.toGetPDFList() != null) {
                                if (prefManager.toGetPDFList().size() > 0) {
                                    prefManager.toSetPDFFileList(ToCheckFileExists.fileList(prefManager.toGetPDFList()));
                                }
                            }

                            if (prefManager.toGetImageList() != null) {
                                if (prefManager.toGetImageList().size() > 0) {
                                    prefManager.toSetImageFileList(ToCheckFileExists.fileList(prefManager.toGetImageList()));
                                }
                            }
                        }
                    });
                } catch (Exception | Error e) {
                    e.printStackTrace();
                    FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                    Crashlytics.logException(e);
                    FirebaseCrash.report(e);
                }
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        CommonMethod.toReleaseMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CommonMethod.toReleaseMemory();
    }
}