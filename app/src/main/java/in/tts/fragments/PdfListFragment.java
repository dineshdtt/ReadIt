package in.tts.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import in.tts.utils.CommonMethod;

public class PdfListFragment extends Fragment {

    private RecyclerView recyclerView;
    private PdfListAdapter pdfListAdapter;

    private TextView tv;
    private RelativeLayout rl;
    private ProgressBar mProgress;

    private ArrayList<String> list = new ArrayList<>();
    private ArrayList<String> subList;

    private int count = 0, extra = 0, i = 0, l=0;
    ;
    private int nextPage = 1, lastPage = 1;

    private boolean userScrolled = false;
    private RecyclerView.LayoutManager mLayoutManager;

    private boolean status = false;

    private OnFragmentInteractionListener mListener;

    public PdfListFragment() {
        // Required empty public constructor
    }

    public static PdfListFragment newInstance() {
        return new PdfListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pdf_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            CommonMethod.toReleaseMemory();

            tv = Objects.requireNonNull(getActivity()).findViewById(R.id.tv12);
            rl = Objects.requireNonNull(getActivity()).findViewById(R.id.rlLoader12);
            mProgress = Objects.requireNonNull(getActivity()).findViewById(R.id.circularProgressbar12);

            recyclerView = Objects.requireNonNull(getActivity()).findViewById(R.id.rvList12);
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setHasFixedSize(true);
            mLayoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(mLayoutManager);

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == 1) {
                        userScrolled = true;
                    }
                }

                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (userScrolled && mLayoutManager.getChildCount() + ((LinearLayoutManager) Objects.requireNonNull(recyclerView.getLayoutManager())).findFirstVisibleItemPosition() < mLayoutManager.getItemCount()) {
                        userScrolled = false;
                        if (nextPage <= lastPage) {
                            Log.d("TAG", " update3 " + nextPage + ":" + lastPage);
                            updateRecyclerView();
                        }
                    }
                }
            });

        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            CommonMethod.toCloseLoader();
            Crashlytics.logException(e);
        }
    }

    public void onButtonPressed(Uri uri) {
        try {
            if (mListener != null) {
                mListener.onFragmentInteraction(uri);
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            CommonMethod.toCloseLoader();
            Crashlytics.logException(e);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            CommonMethod.toReleaseMemory();
            if (context instanceof OnFragmentInteractionListener) {
                mListener = (OnFragmentInteractionListener) context;
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            CommonMethod.toCloseLoader();
            Crashlytics.logException(e);
        }
    }

    public void setLoadData() {
        try {
            if (!status) {
                Log.d("TAG", " count  100: " + list.size());
//                pdfListAdapter = new PdfListAdapter(getContext(), list);
                new toGet().execute();
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            CommonMethod.toCloseLoader();
            Crashlytics.logException(e);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class toGet extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            Log.d("TAG", " count  200:" + list.size() + values[0]);
            tv.setText(String.valueOf(values[0])+"%");
            mProgress.setProgress(values[0]);
//            if (values[0] <= 100) {
//                rl.setVisibility(View.GONE);
//            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Log.d("TAG", " count  300:" + list.size());
                if (new PrefManager(getContext()).toGetPDFList() != null && new PrefManager(getContext()).toGetPDFList().size() != 0) {
                    list = new PrefManager(getContext()).toGetPDFList();
                }
                Log.d("TAG", " count  400:" + list.size());

                while (i <= 100) {
                    try {
                        Thread.sleep(50);
                        publishProgress(i);
                        i = i + 10;
                        Log.d("TAG", " e1 " + i);
                    } catch (Exception e) {
                        Log.d("TAG", " e1" + e.getMessage());
                    }
                }

            } catch (Exception | Error e) {
                e.printStackTrace();
                FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                CommonMethod.toCloseLoader();
                Crashlytics.logException(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
//                pdfListAdapter = new PdfListAdapter(getContext(), list);
//                Log.d("TAG", " count  402:" + list.size() + ":" + pdfListAdapter.getItemCount());
//                recyclerView.setAdapter(pdfListAdapter);
//                pdfListAdapter.notifyDataSetChanged();
                if (list.size() > 0) {
                    populateRecyclerView();
                    Log.d("TAG", " count  402:" + list.size() + ":" + pdfListAdapter.getItemCount());
                    rl.setVisibility(View.GONE);
                }
                status = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("TAG", " count  405:" + list.size() + ":" + pdfListAdapter.getItemCount());
                        new toGetFilesCheck().execute();
                    }
                }, 2000);
                Log.d("TAG", " count  403:" + list.size() + ":" + pdfListAdapter.getItemCount());
            } catch (Exception | Error e) {
                e.printStackTrace();
                FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                CommonMethod.toCloseLoader();
                Crashlytics.logException(e);
            }
        }
    }

    private void populateRecyclerView() {
        try {
            if (list.size() > 20) {
                subList = new ArrayList<>();
                extra = list.size() % 20;
                lastPage = list.size() / 20;
                Log.d("TAG", " update11" + extra + ":" + lastPage + ":" + nextPage + ":" + count);
                for (int i = 0; i < 20; i++) {
                    subList.add(list.get(i));
                }
            } else {
                subList = list;
                Log.d("TAG", " update11" + extra + ":" + lastPage + ":" + nextPage + ":" + count);
            }
            pdfListAdapter = new PdfListAdapter(getContext(), subList);
            recyclerView.setAdapter(pdfListAdapter);
            pdfListAdapter.notifyDataSetChanged();
            count += 20;
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            CommonMethod.toReleaseMemory();
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
        CommonMethod.toReleaseMemory();
    }

    private void updateRecyclerView() {
        try {
            if (nextPage < lastPage) {
                l = count + 20;
                Log.d("TAG", "update count 1: " + count + ":" + l);
                for (int i = count; i < l; i++) {
                    Log.d("TAG", "update count 3: " + count + ":" + l);
                    subList.add(list.get(i));

                   new Handler().post(new Runnable() {
                        public void run() {
//                    recyclerView.post(new Runnable() {
//                        public void run() {
                            pdfListAdapter.notifyItemInserted(count);
                            pdfListAdapter.notifyItemRangeChanged(count, l);
                        }});
                }
                count += 20;
                nextPage++;
            } else {
                l = count + extra;
                Log.d("TAG", "update count 2: " + count + ":" + l);
                for (int i = count; i < l; i++) {
                    Log.d("TAG", "update count 4: " + count + ":" + l);
                    subList.add(list.get(i));
                    new Handler().post(new Runnable() {
                        public void run() {
//                    recyclerView.post(new Runnable() {
//                        public void run() {
                            pdfListAdapter.notifyItemInserted(count);
                            pdfListAdapter.notifyItemRangeChanged(count, l);
                        }
                    });
                }
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            CommonMethod.toReleaseMemory();
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class toGetFilesCheck extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onProgressUpdate(Integer... values) {
            Log.d("TAG", " count  500:" + list.size() + values[0]);
            tv.setText(String.valueOf(values[0])+ " %");
            mProgress.setProgress(values[0]);
            if (values[0] <= 100) {
                rl.setVisibility(View.GONE);
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (subList == null) {
                populateRecyclerView();
            }
            Log.d("TAG", " count  501:" + list.size() + ":" + pdfListAdapter.getItemCount());
            new PrefManager(getContext()).toSetPDFFileList(list);
//            pdfListAdapter.notifyDataSetChanged();
            if (rl.getVisibility() == View.VISIBLE) {
                rl.setVisibility(View.GONE);
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Log.d("TAG", " count  502:" + list.size() + ":" + pdfListAdapter.getItemCount());
                getFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath()));

//                while (i <= 190) {
//                    try {
//                        Log.d("TAG", " e2 " + i);
//                        Thread.sleep(50);
//                        publishProgress(i-100);
//                        i = +10;
//                    } catch (Exception e) {
//                        Log.d("TAG", " e2" + e.getMessage());
//                    }
//                }
                publishProgress(i);
            } catch (Exception | Error e) {
                e.printStackTrace();
                FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                CommonMethod.toCloseLoader();
                Crashlytics.logException(e);
            }
            return null;
        }
    }

    public void getFile(final File dir) {
        try {
            File listFile[] = dir.listFiles();
            if (listFile != null && listFile.length > 0) {
                for (int i = 0; i < listFile.length; i++) {
                    if (listFile[i].isDirectory()) {
                        getFile(listFile[i]);
                    } else {
                        boolean booleanpdf = false;
                        if (listFile[i].getName().endsWith(".pdf")) {
                            for (int j = 0; j < list.size(); j++) {
                                if (list.get(j).equals(listFile[i].getPath())) {
                                    booleanpdf = true;
                                }
                            }
                            if (booleanpdf) {
                                booleanpdf = false;
                            } else {
                                Log.d("TAG", " count  2:" + list.size() + ":" + pdfListAdapter.getItemCount());
                                if (!list.contains(listFile[i].getPath().trim().replaceAll("\\s", "%20"))) {
                                    list.add(listFile[i].getPath().trim().replaceAll("\\s", "%20"));
                                    Log.d("TAG", " count  3:" + list.size() + ":" + pdfListAdapter.getItemCount());
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            CommonMethod.toCloseLoader();
            Crashlytics.logException(e);
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            mListener = null;
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            CommonMethod.toCloseLoader();
            Crashlytics.logException(e);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        CommonMethod.toReleaseMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CommonMethod.toReleaseMemory();
    }

    @Override
    public void onResume() {
        super.onResume();
        CommonMethod.toReleaseMemory();
    }

    @Override
    public void onStop() {
        super.onStop();
        CommonMethod.toReleaseMemory();
    }
}