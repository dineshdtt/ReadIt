package in.tts.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.perf.metrics.AddTrace;

import java.io.File;

import in.tts.R;
import in.tts.model.PrefManager;
import in.tts.model.User;
import in.tts.utils.CommonMethod;
import in.tts.utils.ToGetImages;
import in.tts.utils.ToGetPdfFiles;

public class TutorialPageActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TextView skip;
    private int[] layouts = new int[]{R.layout.tutorial_1, R.layout.tutorial_2, R.layout.tutorial_3, R.layout.tutorial_5, R.layout.tutorial_4};
    private PrefManager prefManager;


    @Override
    @AddTrace(name = "onCreateTutorialPageActivityActivity", enabled = true)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_tutorial_page);

            prefManager = new PrefManager(this);
            if (!prefManager.isFirstTimeLaunch()) {
                launchHomeScreen();
                finish();
            }

            if (Build.VERSION.SDK_INT >= 21) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }

            viewPager = findViewById(R.id.view_pager);
            skip = findViewById(R.id.skip);
            skip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    launchHomeScreen();
                }
            });

            changeStatusBarColor();

            viewPager.setAdapter(new MyViewPagerAdapter());
            viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

            fn_permission();
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fn_permission();
//            } else {
//                CommonMethod.toDisplayToast(TutorialPageActivity.this,  "Please allow the permission");
            }
        } else {
            fn_permission();
        }
    }

    private void fn_permission() {
        try {
            if ((ContextCompat.checkSelfPermission(TutorialPageActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(TutorialPageActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                toLoadData();
            }
            if ((ContextCompat.checkSelfPermission(TutorialPageActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(TutorialPageActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
            if ((ContextCompat.checkSelfPermission(TutorialPageActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(TutorialPageActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    private void toLoadData() {
        if ((ContextCompat.checkSelfPermission(TutorialPageActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            try {
                if (!ToGetPdfFiles.isRunning()) {
                    ToGetPdfFiles.getFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath()), TutorialPageActivity.this);
                }
                if (!ToGetImages.isRunning()) {
                    ToGetImages.getAllShownImagesPath(TutorialPageActivity.this, TutorialPageActivity.this);
                }
            } catch (Exception | Error e) {
                e.printStackTrace();
                FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                Crashlytics.logException(e);
                FirebaseCrash.report(e);
            }
        }
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    private void launchHomeScreen() {
        try {
            CommonMethod.toCallLoader(TutorialPageActivity.this, "Loading...");
            prefManager.setFirstTimeLaunch(false);
            prefManager.getUserInfo();
            if (User.getUser(TutorialPageActivity.this).getId() != null) {
                startActivity(new Intent(TutorialPageActivity.this, HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();
            } else {
                startActivity(new Intent(TutorialPageActivity.this, LoginActivity.class).putExtra("LOGIN", "login"));
            }
            finish();
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            try {
                if (position == layouts.length - 1) {
                    skip.setVisibility(View.GONE);
                } else {
                    skip.setVisibility(View.VISIBLE);
                }
            } catch (Exception | Error e) {
                e.printStackTrace();
                FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                Crashlytics.logException(e);
                FirebaseCrash.report(e);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    private void changeStatusBarColor() throws Exception, Error {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
//        }
    }

    public void toChangeScreen(View view) {
        try {
            int current = getItem(+1);
            if (current < layouts.length) {
                viewPager.setCurrentItem(current);
            } else {
                launchHomeScreen();
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        MyViewPagerAdapter() {
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View view = null;
            try {
                layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                if (layoutInflater != null) {
                    view = layoutInflater.inflate(layouts[position], container, false);
                    container.addView(view);
                }
            } catch (Exception | Error e) {
                e.printStackTrace();
                FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                Crashlytics.logException(e);
                FirebaseCrash.report(e);
            }
            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CommonMethod.toCloseLoader();
    }
}