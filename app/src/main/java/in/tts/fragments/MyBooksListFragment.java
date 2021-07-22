package in.tts.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.firebase.crash.FirebaseCrash;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import in.tts.R;
import in.tts.adapters.PdfListAdapter;
import in.tts.model.PrefManager;

public class MyBooksListFragment extends Fragment {

    private ArrayList<String> file;
    private PdfListAdapter pdfListAdapter;
    private RecyclerView recyclerView;
    private LinearLayout llCustom_loader;

    private PrefManager prefManager;

    public boolean status = false;
    private OnFragmentInteractionListener mListener;


    public MyBooksListFragment() {
    }

    public static MyBooksListFragment newInstance() {
        return new MyBooksListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_books_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            file = new ArrayList<>();
            recyclerView = Objects.requireNonNull(getActivity()).findViewById(R.id.rvList);
            llCustom_loader = Objects.requireNonNull(getActivity()).findViewById(R.id.llCustom_loader);
            prefManager = new PrefManager(getContext());

        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // // CommonMethod.toReleaseMemory();
    }

    private void fn_permission() {
        try {
            if ((ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                toGetData();
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    public void toGetData() {
        try {
            if (getActivity() != null) {
                recyclerView.setHasFixedSize(true);
                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                recyclerView.setLayoutManager(layoutManager);
                if (!status) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (prefManager.toGetPDFList() != null && prefManager.toGetPDFList().size() != 0) {
                                Log.d("TAG", " count 10: " + prefManager.toGetPDFList().size());
                                file = prefManager.toGetPDFList();
                                llCustom_loader.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                            }

//                            pdfListAdapter = new PdfListAdapter(getActivity(), file);
                            recyclerView.setAdapter(pdfListAdapter);
                            pdfListAdapter.notifyDataSetChanged();

                            status = true;
                        }
                    }, 100);
                }
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    public void getFile(final File dir) {
        try {
//            Log.d("TAGCount", "START : " + start);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    File listFile[] = dir.listFiles();
                    if (listFile != null && listFile.length > 0) {
                        for (int i = 0; i < listFile.length; i++) {
                            if (listFile[i].isDirectory()) {
                                getFile(listFile[i]);
                            } else {
                                boolean booleanpdf = false;
                                if (listFile[i].getName().endsWith(".pdf")) {
                                    for (int j = 0; j < file.size(); j++) {
                                        if (file.get(j).equals(listFile[i].getPath())) {
                                            booleanpdf = true;
//                                        } else {
                                        }
                                    }
                                    if (booleanpdf) {
                                        booleanpdf = false;
                                    } else {
                                        if (!file.contains(listFile[i].getPath())) {
                                            Log.d("TAG", " PDF_File" + listFile[i].getPath());
                                            file.add(listFile[i].getPath());
                                        }
                                        pdfListAdapter.notifyDataSetChanged();
                                        pdfListAdapter.notifyItemRangeInserted(pdfListAdapter.getItemCount(), file.size());
                                        pdfListAdapter.notifyItemInserted(pdfListAdapter.getItemCount());
                                        pdfListAdapter.notifyItemRangeChanged(pdfListAdapter.getItemCount(), 1);
                                        pdfListAdapter.notifyDataSetChanged();
//                                        if (file.size() == 1) {
//                                    Log.d("TAG", " count 1: " + pdfListAdapter.getItemCount() + ":" + file.size());
//                                            toSetGone();
//                                } else {
//                                    Log.d("TAG", " count 2: " + pdfListAdapter.getItemCount() + ":" + file.size());
//                                        }
                                    }
                                }
                            }

                        }
                    }
                    toSetGone();
//                    if (i == 0){
//                        if (myBooksListFragment != null) {
//                            myBooksListFragment.toSetGone();
//                        }
//                    }
                }
            });
//            Log.d("TAG", " count 40: " + pdfListAdapter.getItemCount() + ":" + file.size());
            status = true;
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    public void toSetGone() {
        llCustom_loader.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setLoadData() {
//        // // CommonMethod.toDisplayToast(getContext(), status +"");
//        if (!status) {
        fn_permission();
//        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

//    private class toGet extends AsyncTask<Void, Void, Void> {
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            getFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath()));
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            long elapsed = System.currentTimeMillis() - start;
//            System.out.println("total time (ms) : " + elapsed);
//            recyclerView.setAdapter(pdfListAdapter);
//            pdfListAdapter.notifyDataSetChanged();
//            status = true;
//            llCustom_loader.setVisibility(View.GONE);
//        }
//    }

    @Override
    public void onPause() {
        super.onPause();
        // // CommonMethod.toReleaseMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // CommonMethod.toReleaseMemory();
    }

    @Override
    public void onResume() {
        super.onResume();
        // CommonMethod.toReleaseMemory();
    }

    @Override
    public void onStop() {
        super.onStop();
        // CommonMethod.toReleaseMemory();
    }
}