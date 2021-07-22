package in.tts.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.firebase.crash.FirebaseCrash;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import in.tts.R;
import in.tts.adapters.CustomPagerAdapter;
import in.tts.adapters.HomePageRecentImages;
import in.tts.adapters.HomePageRecentPdf;
import in.tts.model.PrefManager;
import in.tts.utils.CommonMethod;

public class MainHomeFragment extends Fragment {

    private NestedScrollView nsv;

    // Main View Pager
    private ViewPager mViewPager;
    private TabLayout tabLayout;

    private ImageView imageView;
    private ImageView ivLeft, ivRight;

    private int currentImage = 0;
    private int mResources[] = {R.drawable.t1, R.drawable.t2, R.drawable.t3, R.drawable.t4, R.drawable.t5};

    //pdf
    private ArrayList<String> pdfFile;
    private HomePageRecentPdf pdfHomePage;

    private TextView tvHeaderPDF, tvSeeMorePDF, tvNoRecentPDF;
    private ViewPager vpRecentPDF;

    // images
    private ArrayList<String> imageFile;
    private HomePageRecentImages pdfHomePageImages;

    private TextView tvHeader, tvSeeMore, tvNoRecentImage;
    private ViewPager vpRecentImage;

    private boolean status = false;

    PrefManager prefManager;

    private OnFragmentInteractionListener mListener;

    public MainHomeFragment() {
    }

    public static MainHomeFragment newInstance() {
        return new MainHomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_home, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            // Log.d("TAG_Main", "onActivityCreated");
            CommonMethod.toReleaseMemory();
            toBindViews();

//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//            fn_permission();
//                }
//            }, 2000);
            CommonMethod.toReleaseMemory();
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    private void fn_permission() {
        try {
            // Log.d("TAG_Main", "fn_permission1");
            if ((ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                // Log.d("TAG_Main", "fn_permission2");
                ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                // Log.d("TAG_Main", "fn_permission3");
//                toSetDisplay();
                toSetData();

//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                new toSetSomeData().execute();
//                    }
//                }, 1000);
            }
            CommonMethod.toReleaseMemory();
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    private void toBindViews() throws Exception, Error {
        // Log.d("TAG_Main", "toBindViews");
        if (getActivity() != null) {
            prefManager = new PrefManager(getContext());

//            mLoading = getActivity().findViewById(R.id.progressBar100);

            nsv = getActivity().findViewById(R.id.nsv);

            imageView = getActivity().findViewById(R.id.imageView);

            ivLeft = getActivity().findViewById(R.id.imageViewLeft1);
            ivRight = getActivity().findViewById(R.id.imageViewRight1);

            tabLayout = getActivity().findViewById(R.id.tlHomePage);

            mViewPager = getActivity().findViewById(R.id.vpHomePage);

//            ll = getActivity().findViewById(R.id.llData);

            ivLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Increase Counter to move to next Image
                    if (currentImage == 0) {
                        currentImage = mResources.length - 1;
                        mViewPager.setCurrentItem(currentImage);
                    } else {
                        currentImage--;
                        mViewPager.setCurrentItem(currentImage);
                    }
                }
            });

            ivRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (currentImage == mResources.length - 1) {
                        currentImage = 0;
                        mViewPager.setCurrentItem(currentImage);
                    } else {
                        currentImage++;
                        mViewPager.setCurrentItem(currentImage);
                    }
                }
            });

            // Pdf

            tvHeaderPDF = Objects.requireNonNull(getActivity()).findViewById(R.id.tvRecentPdf);
            tvSeeMorePDF = Objects.requireNonNull(getActivity()).findViewById(R.id.tvSeeMorePdf);
            tvNoRecentPDF = Objects.requireNonNull(getActivity()).findViewById(R.id.tvNoRecentPDF);

            vpRecentPDF = Objects.requireNonNull(getActivity()).findViewById(R.id.vpRecentItemPdf);

            tvHeaderPDF.setText("Recent PDF");
            tvSeeMorePDF.setText("See More");

            tvSeeMorePDF.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        mListener.setCurrentViewPagerItem(1);
                    } catch (Exception | Error e) {
                        e.printStackTrace();
                        FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                        Crashlytics.logException(e);
                        FirebaseCrash.report(e);
                    }
                }
            });


            // images
            tvHeader = Objects.requireNonNull(getActivity()).findViewById(R.id.tvRecent);
            tvSeeMore = Objects.requireNonNull(getActivity()).findViewById(R.id.tvSeeMore);
            tvNoRecentImage = Objects.requireNonNull(getActivity()).findViewById(R.id.tvNoRecentImage);

            vpRecentImage = Objects.requireNonNull(getActivity()).findViewById(R.id.vpRecentItem);

            tvHeader.setText("Recent Images");
            tvSeeMore.setText("See More");

            tvSeeMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        mListener.setCurrentViewPagerItem(4);
                    } catch (Exception | Error e) {
                        e.printStackTrace();
                        FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                        Crashlytics.logException(e);
                        FirebaseCrash.report(e);
                    }
                }
            });


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
            // Log.d("TAG_Main", "onRequestPermissionsResult");
            if (requestCode == 1) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    new toSetSomeData().execute();
                    toSetDisplay();
                } else {
                    CommonMethod.toDisplayToast(getContext(), "Please allow the permission");
                }
            }
            CommonMethod.toReleaseMemory();
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    private void toSetData() {
        try {
            // Log.d("TAG_Main", "toSetData");
            if (!status) {
//                fn_permission();
//                mLoading.setVisibility(View.GONE);
                toSetDisplay();

            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    private void toSetDisplay() {
        try {
            // Log.d("TAG_Main", "toSetDisplay");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                toBindTopBanners();
                                toSetRecentPdf();
                                toSetRecentImages();
                            } catch (Exception | Error e) {
                                e.printStackTrace();
                                FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                                Crashlytics.logException(e);
                                FirebaseCrash.report(e);
                            }
                        }
                    });
                }
            }).start();
            status = true;
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    private void toSetRecentPdf() {
        try {
            // Log.d("TAG_Main", "toSetRecentPdf");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            pdfFile = new ArrayList<>();
//                            if (prefManager.toGetPDFListRecent() != null && prefManager.toGetPDFListRecent().size() != 0) {
//                                pdfFile = prefManager.toGetPDFListRecent();
//                                pdfHomePage = new HomePageRecentPdf(getContext(), pdfFile);
                            toBindRecentPdf();
//                    } else if (prefManager.toGetPDFList() != null) {
//                        pdfFile = prefManager.toGetPDFList();
//                        pdfHomePage = new HomePageRecentPdf(getContext(), pdfFile);
//                        toBindRecentPdf();
//                    } else {
//                        getFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath()));
//                            }
                        }
                    });
                }
            }, 100);
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    private void toSetRecentImages() {
        try {
            // Log.d("TAG_Main", "toSetRecentImages");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            if (prefManager.toGetImageListRecent() != null) {
//                                imageFile = prefManager.toGetImageListRecent();
//                                pdfHomePageImages = new HomePageRecentImages(getContext(), imageFile);
                            toBindRecentImages();
//                    } else if (prefManager.toGetImageList() != null) {
//                        imageFile = prefManager.toGetImageList();
//                        pdfHomePageImages = new HomePageRecentImages(getContext(), imageFile);
//                        toBindRecentImages();
//                    } else {
//                        getAllShownImagesPath();
//                    } else {

//                            }
                        }
                    });
                }
            }, 100);
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            toBindRecentPdf();
//            toBindRecentImages();
//
//            nsv.setVisibility(View.VISIBLE);
//            mLoading.setVisibility(View.GONE);
//        }
//    }

//    public void toSetSomeData() {
//        try {
//            if (getActivity() != null) {
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (prefManager.toGetPDFListRecent() != null) {
//                            pdfFile = prefManager.toGetPDFListRecent();
//                        } else if (prefManager.toGetPDFList() == null) {
//                            pdfFile = new ArrayList<>();
//                            getFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath()));
//                        } else {
//                            pdfFile = prefManager.toGetPDFList();
//                        }
//                        pdfHomePage = new HomePageRecentPdf(getContext(), pdfFile);
//                        toBindRecentPdf();
//
//                        if (prefManager.toGetImageListRecent() != null) {
//                            imageFile = prefManager.toGetImageListRecent();
//                        } else if (prefManager.toGetImageList() == null) {
//                            imageFile = new ArrayList<>();
//                            getAllShownImagesPath();
//                        } else {
//                            imageFile = prefManager.toGetImageList();
//                        }
//                        pdfHomePageImages = new HomePageRecentImages(getContext(), imageFile);
//                        toBindRecentImages();
//                        nsv.setVisibility(View.VISIBLE);
//                        mLoading.setVisibility(View.GONE);
//                    }
//                });
//                CommonMethod.toReleaseMemory();
//            }
//        } catch (Exception | Error e) {
//            e.printStackTrace();
//            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
//            Crashlytics.logException(e);
//            FirebaseCrash.report(e);
//        }
//    }

    private void toBindTopBanners() throws Exception, Error {
        // Log.d("TAG_Main", "toBindTopBanners");

        mViewPager.setAdapter(new CustomPagerAdapter(mResources, getContext()));
        tabLayout.setupWithViewPager(mViewPager);
        CommonMethod.toReleaseMemory();
    }

    private void toBindRecentPdf() {
        try {
            // Log.d("TAG_Main", "toBindRecentPdf");

            pdfFile = new ArrayList<>();
            if (prefManager.toGetPDFListRecent() != null && prefManager.toGetPDFListRecent().size() != 0) {
                pdfFile = prefManager.toGetPDFListRecent();
                pdfHomePage = new HomePageRecentPdf(getContext(), pdfFile);
            }

            if (pdfFile.size() != 0) {
                if (getContext() != null) {
                    vpRecentPDF.setClipToPadding(true);
                    vpRecentPDF.setOffscreenPageLimit(10);
                    vpRecentPDF.setPadding(CommonMethod.dpToPx(5, getActivity()), 0, CommonMethod.dpToPx(10, getActivity()), 0);
                    vpRecentPDF.setPageMargin(CommonMethod.dpToPx(10, getActivity()));
                    vpRecentPDF.setAdapter(pdfHomePage);
                    vpRecentPDF.setVisibility(View.VISIBLE);
                    tvNoRecentPDF.setVisibility(View.GONE);
                }
            } else {
                vpRecentPDF.setVisibility(View.GONE);
                tvNoRecentPDF.setVisibility(View.VISIBLE);
            }
            getActivity().findViewById(R.id.rlRecentItemPdfMain).setVisibility(View.VISIBLE);
            CommonMethod.toReleaseMemory();
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            CommonMethod.toReleaseMemory();
        }
    }

    private void toBindRecentImages() {
        try {
            // Log.d("TAG_Main", "toBindRecentImages");
            imageFile = new ArrayList<>();
            if (prefManager.toGetImageListRecent() != null) {
                imageFile = prefManager.toGetImageListRecent();
                pdfHomePageImages = new HomePageRecentImages(getContext(), imageFile);
            }
            // Log.d("TAG_Main", "toBindRecentImages1" + imageFile.size());
            if (imageFile.size() != 0) {
                if (getContext() != null) {
                    vpRecentImage.setClipToPadding(false);
                    vpRecentImage.setOffscreenPageLimit(10);
                    vpRecentImage.setPageMargin(CommonMethod.dpToPx(10, getActivity()));
                    vpRecentImage.setPadding(CommonMethod.dpToPx(5, getActivity()), 0, CommonMethod.dpToPx(10, getActivity()), 0);
                    vpRecentImage.setAdapter(pdfHomePageImages);
//                    pdfHomePageImages.notifyDataSetChanged();
                    vpRecentImage.setVisibility(View.VISIBLE);
                    tvNoRecentImage.setVisibility(View.GONE);

                }
            } else {
                vpRecentImage.setVisibility(View.GONE);
                tvNoRecentImage.setVisibility(View.VISIBLE);
            }
            getActivity().findViewById(R.id.rlRecentItemPdfMain).setVisibility(View.VISIBLE);
            CommonMethod.toReleaseMemory();
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            CommonMethod.toReleaseMemory();
        }
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            if (context instanceof OnFragmentInteractionListener) {
                mListener = (OnFragmentInteractionListener) context;
            }
            CommonMethod.toReleaseMemory();
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        CommonMethod.toReleaseMemory();
        mListener = null;
    }

    public void setLoadData() {
        // Log.d("TAG_Main", "setLoadData " + status + ":" + PrefManager.AddedRecentPDF + ":" + PrefManager.AddedRecentImage);
        if (!status) {
            fn_permission();
        }
        if (PrefManager.AddedRecentImage || PrefManager.AddedRecentPDF) {
            toSetData();
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);

        void setCurrentViewPagerItem(int i);

    }

    @Override
    public void onPause() {
        super.onPause();
        // Log.d("TAG_Main", "onPause");

        CommonMethod.toReleaseMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Log.d("TAG_Main", "onDestroy");

        CommonMethod.toReleaseMemory();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Log.d("TAG_Main", "onResume");
        if (PrefManager.AddedRecentImage || PrefManager.AddedRecentPDF) {
            toSetData();
        }
        CommonMethod.toReleaseMemory();
    }
}

//    private void toBindRecentPdf() {
//        try {
//            if (view != null) {
//                ll.removeView(view);
//            }
//            if (pdfFile.size() != 0) {
//                if (getContext() != null) {
//                    LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                    if (inflater != null) {
//                        if (mLoading != null && mLoading.getVisibility() == View.VISIBLE) {
//                            mLoading.setVisibility(View.GONE);
//                        }
//                        view = inflater.inflate(R.layout.layout_home_page_recent_items, null, false);
//
//                        TextView tvHeader = view.findViewById(R.id.tvRecent);
//                        TextView tvSeeMore = view.findViewById(R.id.tvSeeMore);
//
//                        ViewPager vpDeals = view.findViewById(R.id.vpRecentItem);
//
//                        tvHeader.setText("Recent PDF");
//                        tvSeeMore.setText("See More");
//
//                        tvSeeMore.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                try {
//                                    mListener.setCurrentViewPagerItem(1);
//                                } catch (Exception | Error e) {
//                                    e.printStackTrace();
//                                    FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
//                                    Crashlytics.logException(e);
//                                    FirebaseCrash.report(e);
//                                }
//                            }
//                        });
//
//                        vpDeals.setClipToPadding(true);
//                        vpDeals.setOffscreenPageLimit(10);
//                        vpDeals.setPadding(CommonMethod.dpToPx(5, getActivity()), 0, CommonMethod.dpToPx(10, getActivity()), 0);
//                        vpDeals.setPageMargin(CommonMethod.dpToPx(10, getActivity()));
//                        vpDeals.setAdapter(pdfHomePage);
//                        ll.addView(view);
//                    }
//                }
//            }
//            CommonMethod.toReleaseMemory();
//        } catch (Exception | Error e) {
//            e.printStackTrace();
//            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
//            Crashlytics.logException(e);
//            FirebaseCrash.report(e);
//            CommonMethod.toReleaseMemory();
//        }
//    }

//    public void getAllShownImagesPath() {
//        try {
//
//            String[] projection = new String[]{
//                    MediaStore.Images.ImageColumns._ID,
//                    MediaStore.Images.ImageColumns.DATA,
//                    MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
//                    MediaStore.Images.ImageColumns.DATE_TAKEN,
//                    MediaStore.Images.ImageColumns.MIME_TYPE
//            };
//            if (getActivity() != null) {
//                Cursor cursor =
//                        getActivity().getContentResolver()
//                                .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,
//                                        null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");
//                if (cursor != null) {
//                    while (cursor.moveToNext() && imageFile.size() < 11) {
//                        String imageLocation = cursor.getString(1);
//                        imageFile.add(imageLocation.replaceAll("\\s", "%20"));
//                        if (imageFile.size() == 1) {
//                            pdfHomePageImages = new HomePageRecentImages(getContext(), imageFile);
//                            toBindRecentImages();
//                        } else {
//                            pdfHomePageImages.setData(imageFile);
//                            pdfHomePageImages.notifyDataSetChanged();
//                        }
//                    }
//                }
//                if (cursor != null) {
//                    cursor.close();
//                }
//            }
//        } catch (Exception | Error e) {
//            e.printStackTrace();
//            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
//            Crashlytics.logException(e);
//            FirebaseCrash.report(e);
//        }
//    }

//    private void toBindRecentImages() {
//        try {
//            if (view1 != null) {
//                ll.removeView(view1);
//            }
//            if (imageFile.size() != 0) {
//                if (getContext() != null) {
//                    LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                    if (inflater != null) {
//                        if (mLoading != null && mLoading.getVisibility() == View.VISIBLE) {
//                            mLoading.setVisibility(View.GONE);
//                        }
//                        view1 = inflater.inflate(R.layout.layout_home_page_recent_items1, null, false);
//
//                        TextView tvHeader = view1.findViewById(R.id.tvRecent);
//                        TextView tvSeeMore = view1.findViewById(R.id.tvSeeMore);
//
//                        ViewPager vpDeals = view1.findViewById(R.id.vpRecentItem);
//
//                        tvHeader.setText("Recent Images");
//                        tvSeeMore.setText("See More");
//
//                        vpDeals.setClipToPadding(false);
//                        vpDeals.setOffscreenPageLimit(10);
//                        vpDeals.setPageMargin(CommonMethod.dpToPx(10, getActivity()));
//                        vpDeals.setPadding(CommonMethod.dpToPx(5, getActivity()), 0, CommonMethod.dpToPx(10, getActivity()), 0);
//                        vpDeals.setAdapter(pdfHomePageImages);
//                        pdfHomePageImages.notifyDataSetChanged();
//
//                        tvSeeMore.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                try {
//                                    mListener.setCurrentViewPagerItem(4);
//                                } catch (Exception | Error e) {
//                                    e.printStackTrace();
//                                    FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
//                                    Crashlytics.logException(e);
//                                    FirebaseCrash.report(e);
//                                }
//                            }
//                        });
//
//                        ll.addView(view1);
//                    }
//                }
//            }
//            CommonMethod.toReleaseMemory();
//        } catch (Exception | Error e) {
//            e.printStackTrace();
//            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
//            Crashlytics.logException(e);
//            FirebaseCrash.report(e);
//            CommonMethod.toReleaseMemory();
//        }
//    }

//    // To Bind views
//    public void getFile(final File dir) {
//        try {
//            File listFile[] = dir.listFiles();
//            if (listFile != null && listFile.length > 0) {
//                for (int i = 0; i < listFile.length; i++) {
//                    if (listFile[i].isDirectory()) {
//                        getFile(listFile[i]);
//                    } else {
//                        boolean booleanpdf = false;
//                        if (listFile[i].getName().endsWith(".pdf")) {
//                            for (int j = 0; j < pdfFile.size(); j++) {
//                                if (pdfFile.get(j).equals(listFile[i].getPath())) {
//                                    booleanpdf = true;
//                                } else {
//                                }
//                            }
//                            if (booleanpdf) {
//                                booleanpdf = false;
//                            } else {
//                                if (pdfFile.size() < 10) {
//                                    pdfFile.add(listFile[i].getPath());
//                                } else {
//                                    break;
//                                }
//                                if (pdfFile.size() == 1) {
//                                    pdfHomePage = new HomePageRecentPdf(getContext(), pdfFile);
//                                    toBindRecentPdf();
//                                } else {
//                                    pdfHomePage.setData(pdfFile);
//                                    pdfHomePage.notifyDataSetChanged();
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        } catch (Exception | Error e) {
//            e.printStackTrace();
//            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
//            Crashlytics.logException(e);
//            FirebaseCrash.report(e);
//        }
//    }
