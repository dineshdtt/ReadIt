package in.tts.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.perf.metrics.AddTrace;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

import de.hdodenhof.circleimageview.CircleImageView;
import in.tts.R;
import in.tts.fragments.LoginFragment;
import in.tts.model.PrefManager;
import in.tts.model.User;
import in.tts.utils.CommonMethod;

import static java.security.AccessController.getContext;

public class SettingActivity extends AppCompatActivity {

    private RelativeLayout rlLogout;
    private ProgressBar pbData, pbStorage;
    private TextView tvUsed, tvFree, tvName;
    private Button mBtnLogin;
    private CircleImageView mCivUpeProfile;
    private float totalSpace, occupiedSpace, freeSpace;
    private DecimalFormat outputFormat;
    private StatFs statFs;
    private User user;

    @Override
    @AddTrace(name = "onCreateSettingActivity", enabled = true)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_setting);
            CommonMethod.setAnalyticsData(SettingActivity.this, "MainTab", "Setting User", null);

            if (getSupportActionBar() != null) {
                CommonMethod.toSetTitle(getSupportActionBar(), SettingActivity.this, getString(R.string.str_title_settings));
            } else {
                getSupportActionBar().setTitle(R.string.app_name);
            }

            statFs = new StatFs(Environment.getDataDirectory().getAbsolutePath());

            rlLogout = findViewById(R.id.rlLogout);

            pbData = findViewById(R.id.pbSetting1);
            pbStorage = findViewById(R.id.pbSetting2);

            tvFree = findViewById(R.id.tvFree);
            tvFree.setVisibility(View.GONE);

            tvUsed = findViewById(R.id.tvUsed);
            tvUsed.setVisibility(View.GONE);

            mBtnLogin = findViewById(R.id.btnLoginSetting);

            mCivUpeProfile = findViewById(R.id.civProfile);
            tvName = findViewById(R.id.txtUpeChangePic);

            user = User.getUser(SettingActivity.this);

            Log.d("TAG", "user data: " + (user.getUsername() != null ? user.getUsername() : user.getEmail() != null ? user.getEmail() : "UserName") + user.getPicPath());
            tvName.setText(user.getUsername() != null ? user.getUsername() : user.getEmail() != null ? user.getEmail() : "UserName");
            if (user.getPicPath() != null) {
                Picasso.get()
                        .load(user.getPicPath())
                        .placeholder(R.drawable.ic_tab_guide)
                        .error(R.drawable.ic_tab_guide)
                        .centerCrop()
                        .resize(50, 50)
                        .into(mCivUpeProfile);
            }

            mBtnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(SettingActivity.this, LoginActivity.class).putExtra("LOGIN", "login"));
                }
            });

            totalSpace = getInternalStorageSpace();
            occupiedSpace = getInternalUsedSpace();
            freeSpace = getInternalFreeSpace();
            outputFormat = new DecimalFormat("#.##");

            if (null != pbStorage) {
                pbStorage.setMax((int) totalSpace);
                pbStorage.setProgress((int) occupiedSpace);
            }
            if (null != tvUsed) {
                tvUsed.setText(outputFormat.format(occupiedSpace) + " MB");
            }

            if (null != tvFree) {
                tvFree.setText(outputFormat.format(freeSpace) + " MB");
            }

            if (User.getUser(SettingActivity.this).getId() != null) {
                rlLogout.setVisibility(View.VISIBLE);
                mBtnLogin.setVisibility(View.GONE);
            } else {
                rlLogout.setVisibility(View.GONE);
                mBtnLogin.setVisibility(View.VISIBLE);
            }

            rlLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        doExit();
                    } catch (Exception | Error e) {
                        e.printStackTrace();
                        Crashlytics.logException(e);
                        FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
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

    public float getInternalStorageSpace() {
        return ((float) statFs.getBlockCount() * statFs.getBlockSize()) / 1048576;
    }

    public float getInternalFreeSpace() {
        return ((float) statFs.getAvailableBlocks() * statFs.getBlockSize()) / 1048576;
    }

    public float getInternalUsedSpace() {
        return ((((float) statFs.getBlockCount() * statFs.getBlockSize()) / 1048576) - (((float) statFs.getAvailableBlocks() * statFs.getBlockSize()) / 1048576));
    }

    private void doExit() {
        try {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setPositiveButton(getResources().getString(R.string.str_lbl_yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    toLogout();
                }
            });
            alertDialog.setNegativeButton(getResources().getString(R.string.str_lbl_no), null);
            alertDialog.setMessage(getResources().getString(R.string.str_lbl_logout_from_app));
            alertDialog.setTitle(getResources().getString(R.string.app_name));
            alertDialog.show();
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    private void toLogout() {
        try {
            FirebaseAuth.getInstance().signOut();
            new PrefManager(SettingActivity.this).toClearUserInfo();
            finish();
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            switch (item.getItemId()) {
                case android.R.id.home:
                    onBackPressed();
                    break;
                default:
                    return true;
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}