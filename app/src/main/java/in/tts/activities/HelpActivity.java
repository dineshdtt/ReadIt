package in.tts.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.perf.metrics.AddTrace;

import in.tts.R;
import in.tts.fragments.TextFragment;
import in.tts.utils.CommonMethod;

public class HelpActivity extends AppCompatActivity {

    @Override
    @AddTrace(name = "onCreateHelpActivity", enabled = true)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        CommonMethod.setAnalyticsData(HelpActivity.this, "MainTab", "Help", null);

        if (getSupportActionBar() != null) {
            CommonMethod.toSetTitle(getSupportActionBar(), HelpActivity.this, getString(R.string.str_title_help));
        }

        findViewById(R.id.rlFaq).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replacePage(1);
            }
        });

        findViewById(R.id.rlTermPolicy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replacePage(2);
            }
        });

        findViewById(R.id.rlAppInfo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replacePage(3);
            }
        });

    }

    public void replacePage(int i) {
        try {
            Fragment fragment = new TextFragment();
            Bundle bundle = new Bundle();
            bundle.putString("PAGE", i + "");
            fragment.setArguments(bundle);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.llHelp, fragment)
                    .addToBackStack(""+i)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commitAllowingStateLoss();
            CommonMethod.toCloseLoader();
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            CommonMethod.toCloseLoader();
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
        if (getSupportFragmentManager().findFragmentById(R.id.llHelp) == null){
            finish();
        }
//        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.llHelp);
//        Log.d("TAG", " countFragment " + currentFragment+ getSupportFragmentManager().getBackStackEntryCount());
//        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
//            getSupportFragmentManager().popBackStack();
//        } else {
//            finish();
//        }
    }
}
