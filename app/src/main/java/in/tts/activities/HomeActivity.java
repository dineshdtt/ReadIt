package in.tts.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.firebase.crash.FirebaseCrash;

import java.util.Arrays;

import in.tts.R;
import in.tts.classes.ToSetMore;
import in.tts.fragments.BrowserFragment;
import in.tts.fragments.GalleryFragment;
import in.tts.fragments.MainHomeFragment;
import in.tts.fragments.MakeYourOwnReadFragment;
import in.tts.fragments.PdfFragment;
import in.tts.model.PrefManager;
import in.tts.utils.CommonMethod;
import in.tts.utils.NonSwipeableViewPager;

public class HomeActivity extends AppCompatActivity implements
        BrowserFragment.OnFragmentInteractionListener,
        PdfFragment.OnFragmentInteractionListener,
        MainHomeFragment.OnFragmentInteractionListener,
        MakeYourOwnReadFragment.OnFragmentInteractionListener,
        GalleryFragment.OnFragmentInteractionListener {

    private NonSwipeableViewPager viewPager;
    private TabLayout tabLayout;

    private BrowserFragment tab1;
    private PdfFragment tab2;
    private MainHomeFragment tab3;
    private MakeYourOwnReadFragment tab4;
    private GalleryFragment tab5;

    private String[] tabHomeText = new String[]{"", "", "", "", ""};
    private int[] tabHomeIcon = new int[]{R.drawable.tab1, R.drawable.tab2, R.drawable.tab3, R.drawable.tab4, R.drawable.tab5};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_home);
            Log.d("TAG_Main", "  onCreate ha ");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("TAG_Main", "  onCreatehg ha ");
                            toBindData();
                            fn_permission();
                        }
                    });
                    Log.d("TAG_Main", "  setCurrentViewPagerItem ha ");
//                    setCurrentViewPagerItem(2);
                }
            }, 10);

        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    private void toBindData() {
        try {
            tabLayout = findViewById(R.id.tabsHome);
            viewPager = findViewById(R.id.nonSwipeableViewPagerHome);
            Log.d("TAG_Main", "  toBindData ha ");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            tab1 = BrowserFragment.newInstance();
                            tab2 = PdfFragment.newInstance();
                            tab3 = MainHomeFragment.newInstance();
                            tab4 = MakeYourOwnReadFragment.newInstance();
                            tab5 = GalleryFragment.newInstance();

                            SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
                            viewPager.setAdapter(adapter);
                            tabLayout.setupWithViewPager(viewPager);

                            tabLayout.getTabAt(0).setText(tabHomeText[0]).setIcon(tabHomeIcon[0]);
                            tabLayout.getTabAt(1).setText(tabHomeText[1]).setIcon(tabHomeIcon[1]);
                            tabLayout.getTabAt(2).setText(tabHomeText[2]).setIcon(tabHomeIcon[2]);
                            tabLayout.getTabAt(3).setText(tabHomeText[3]).setIcon(tabHomeIcon[3]);
                            tabLayout.getTabAt(4).setText(tabHomeText[4]).setIcon(tabHomeIcon[4]);


                            viewPager.setOffscreenPageLimit(5);
                            setCurrentViewPagerItem(1);
                        }
                    });
                }
            }, 50);
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    Log.d("TAG_Main", "  setCurrentViewPagerItem tab " + tab.getPosition());
                    setCurrentViewPagerItem(tab.getPosition());
                    CommonMethod.toReleaseMemory();
                    CommonMethod.setAnalyticsData(HomeActivity.this, "MainTab", "Page " + tab.getPosition() + 1, null);
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                    CommonMethod.toReleaseMemory();
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                    CommonMethod.toReleaseMemory();
                }
            });
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    // Adapter for the viewpager using FragmentPagerAdapter
    private class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public Fragment getItem(int position) {
            CommonMethod.toReleaseMemory();
            try {
                switch (position) {
                    case 0:
                        return tab1;
                    case 1:
                        return tab2;
                    case 2:
                        return tab3;
                    case 3:
                        return tab4;
                    case 4:
                        return tab5;
                    default:
                        return tab3;
                }
            } catch (Exception | Error e) {
                e.printStackTrace();
                FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                Crashlytics.logException(e);
                FirebaseCrash.report(e);
                return tab3;
            }
        }

        public int getCount() {
            return tabHomeText.length;
        }
    }

    private void fn_permission() {
        try {
            if ((ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(HomeActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
            if ((ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
            if ((ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
        CommonMethod.toReleaseMemory();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
//            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentrepalce);
//            Log.d("TAG", " Main onActivityResult " + resultCode + ":" + requestCode + " :"+ fragment.getClass().getName());
//            fragment.onActivityResult(requestCode, resultCode, data);
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
        CommonMethod.toReleaseMemory();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        try {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            Log.d("TAG", "Main onRequestPermissionsResult : " + requestCode + ":" + Arrays.toString(permissions) + ":" + Arrays.toString(grantResults));
            if (requestCode == 1) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fn_permission();
                } else {
                    CommonMethod.toDisplayToast(HomeActivity.this, "Please allow the permission");
                }
            } else {
                fn_permission();
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
        CommonMethod.toReleaseMemory();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try {
            getMenuInflater().inflate(R.menu.main_menu, menu);
            View menuCamera = menu.findItem(R.id.actionCamera).getActionView();
            menuCamera.findViewById(R.id.ivCameraMenu).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(HomeActivity.this, CameraActivity.class));
                }
            });
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
        CommonMethod.toReleaseMemory();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            ToSetMore.MenuOptions(HomeActivity.this, item);
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
        CommonMethod.toReleaseMemory();
        return super.onOptionsItemSelected(item);
    }

    private void toSetTitle(String title) {
        try {
            if (getSupportActionBar() != null) {
                getSupportActionBar().show();
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                getSupportActionBar().setDisplayShowTitleEnabled(true);

                if (title != null) {
                    if (title.length() > 0) {
                        getSupportActionBar().setTitle(CommonMethod.firstLetterCaps(title));
                    } else {
                        getSupportActionBar().setTitle(CommonMethod.firstLetterCaps(getResources().getString(R.string.app_name)));
                    }
                } else {
                    getSupportActionBar().setTitle(CommonMethod.firstLetterCaps(getResources().getString(R.string.app_name)));
                }
            }
            CommonMethod.toReleaseMemory();
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            CommonMethod.toReleaseMemory();
        }
        CommonMethod.toReleaseMemory();
    }

    public void onBackPressed() {
        try {
            if (tabLayout.getSelectedTabPosition() != 2) {
                Log.d("TAG_Main", "  setCurrentViewPagerItem backPress");
                setCurrentViewPagerItem(2);
            } else {
                doExit();
            }
            CommonMethod.toReleaseMemory();
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            CommonMethod.toReleaseMemory();
        }
        CommonMethod.toReleaseMemory();
    }

    public void setCurrentViewPagerItem(int i) {
        try {
            Log.d("TAG_Main", "  setCurrentViewPagerItem " + i);
            if (tabLayout != null) {
                tabLayout.getTabAt(i).select();
            }
            if (viewPager != null) {
                viewPager.setCurrentItem(i);
            }
            switch (i) {
                case 0:
                    toSetTitle(getString(R.string.str_title_browse_it));
                    tab1.setLoadData();
                    break;
                case 1:
                    toSetTitle(getString(R.string.str_title_docs));
                    tab2.setLoadData();
                    break;
                case 2:
                    toSetTitle(getString(R.string.app_name));
                    tab3.setLoadData();
                    break;
                case 3:
                    toSetTitle(getString(R.string.str_title_make_your_own_read));
                    tab4.setLoadData();
                    break;
                case 4:
                    toSetTitle(getString(R.string.str_title_images));
                    tab5.setLoadData();
                    break;
                default:
                    toSetTitle(getString(R.string.app_name));
                    tabLayout.getTabAt(2).select();
                    tab3.setLoadData();
                    break;
            }
            CommonMethod.toReleaseMemory();
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            CommonMethod.toReleaseMemory();
        }
        CommonMethod.toReleaseMemory();
    }

    private void doExit() {
        try {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setPositiveButton(getResources().getString(R.string.str_lbl_yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            alertDialog.setNegativeButton(getResources().getString(R.string.str_lbl_no), null);
            alertDialog.setMessage(getResources().getString(R.string.str_lbl_exit_from_app));
            alertDialog.setTitle(getResources().getString(R.string.app_name));
            alertDialog.show();
            CommonMethod.toReleaseMemory();
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            CommonMethod.toReleaseMemory();
        }
        CommonMethod.toReleaseMemory();
    }

    @Override
    protected void onPause() {
        super.onPause();
        CommonMethod.toReleaseMemory();
    }

    @Override
    protected void onStart() {
        super.onStart();
        CommonMethod.toReleaseMemory();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CommonMethod.toReleaseMemory();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }
}