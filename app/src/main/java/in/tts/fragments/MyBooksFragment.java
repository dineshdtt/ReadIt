package in.tts.fragments;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics; import com.flurry.android.FlurryAgent; import com.google.firebase.crash.FirebaseCrash;

import java.util.ArrayList;

import in.tts.R;
import in.tts.adapters.PdfListAdapter;
import in.tts.model.PrefManager;
import in.tts.utils.CommonMethod;

public class MyBooksFragment extends Fragment {

    private TextView mTvLblRecent;
    private RecyclerView mRv;
    private PdfListAdapter pdfListAdapter;
    private ArrayList<String> list;
    private int count = 0, extra = 0;
    private int nextPage = 1, lastPage = 1;
    private LayoutManager mLayoutManager;
    private int pastVisibleItems;
    private int totalItemCount;
    private boolean userScrolled = true;
    private int visibleItemCount;
    private PrefManager prefManager;

    private OnFragmentInteractionListener mListener;

    public MyBooksFragment() {
        // Required empty public constructor
    }

    public static MyBooksFragment newInstance() {
        return new MyBooksFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        CommonMethod.toCloseLoader();
        return inflater.inflate(R.layout.fragment_my_books, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            CommonMethod.setAnalyticsData(getContext(), "DocTab", "Doc MyBooks", null);
            prefManager = new PrefManager(getContext());

            mTvLblRecent = getActivity().findViewById(R.id.txtRecent);
            mRv = getActivity().findViewById(R.id.rvPDFList);

            CommonMethod.toCloseLoader();

            mLayoutManager = new LinearLayoutManager(getContext());
            mRv.setHasFixedSize(true);
            mRv.setLayoutManager(mLayoutManager);
            mRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == 1) {
                        userScrolled = true;
                    }
                }

                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    pastVisibleItems = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                    Log.d("TAG", " update1 " + userScrolled + ":" + visibleItemCount + ":" + pastVisibleItems + ":" + totalItemCount);
                    if (userScrolled && visibleItemCount + pastVisibleItems < totalItemCount) {
                        Log.d("TAG", " update2 " + userScrolled + ":" + visibleItemCount + ":" + pastVisibleItems + ":" + totalItemCount);
                        userScrolled = false;
                        if (nextPage <= lastPage) {
                            Log.d("TAG", " update3 " + nextPage + ":" + lastPage);
                            updateRecyclerView();
                        }
                    }
                }
            });

//            AppPermissions.toCheckPermissionRead(getContext(), getActivity(), null, MyBooksFragment.this, null, false);

        } catch (Exception | Error e) {
            e.printStackTrace(); FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e); FirebaseCrash.report(e);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                toSetView();
            } else {
                Toast.makeText(getContext(), "Please allow the permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void toSetView() {
        try {
            if (prefManager.toGetPDFList() != null && prefManager.toGetPDFList().size() != 0) {
                populateRecyclerView();
            } else {
//                AppPermissions.toCheckPermissionRead(getContext(), getActivity(), null, MyBooksFragment.this);
            }
        } catch (Exception | Error e) {
            e.printStackTrace(); FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e); FirebaseCrash.report(e);
        }
    }

    private void populateRecyclerView() {
        try {
            if (prefManager.toGetPDFList().size() > 20) {
                list = new ArrayList<>();
                extra = prefManager.toGetPDFList().size() % 20;
                lastPage = prefManager.toGetPDFList().size() / 20;
                Log.d("TAG", " update11" + extra + ":" + lastPage + ":" + nextPage + ":" + count);
                for (int i = 0; i < 20; i++) {
                    list.add(prefManager.toGetPDFList().get(i));
                }
            } else {
                list = prefManager.toGetPDFList();
                Log.d("TAG", " update11" + extra + ":" + lastPage + ":" + nextPage + ":" + count);
            }
//            pdfListAdapter = new PdfListAdapter(getContext(), list);
            mRv.setAdapter(pdfListAdapter);
            pdfListAdapter.notifyDataSetChanged();
            count += 20;
        } catch (Exception | Error e) {
            e.printStackTrace(); FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            CommonMethod.toReleaseMemory();
            Crashlytics.logException(e); FirebaseCrash.report(e);
        }
        CommonMethod.toReleaseMemory();
    }

    private void updateRecyclerView() {
        try {
            int l;
            if (nextPage < lastPage) {
                l = this.count + 20;
                Log.d("TAG", "update count 1: " + count +":"+ l);
                for (int i = count; i < l; i++) {
                    Log.d("TAG", "update count 3: " + count +":"+ l);
                    list.add(prefManager.toGetPDFList().get(i));
                    pdfListAdapter.notifyItemInserted(count);
                    pdfListAdapter.notifyItemRangeChanged(count,l);
                }
                count += 20;
                nextPage++;
            } else {
                l = count + extra;
                Log.d("TAG", "update count 2: " + count +":"+ l);
                for (int i = count; i < l; i++) {
                    Log.d("TAG", "update count 4: " + count +":"+ l);
                    list.add(prefManager.toGetPDFList().get(i));
                    pdfListAdapter.notifyItemInserted(count);
                    pdfListAdapter.notifyItemRangeChanged(count,l);
                }
            }



        } catch (Exception | Error e) {
            e.printStackTrace(); FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            CommonMethod.toReleaseMemory();
            Crashlytics.logException(e); FirebaseCrash.report(e);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        CommonMethod.toReleaseMemory();
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
        } catch (Exception | Error e) {
            e.printStackTrace(); FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e); FirebaseCrash.report(e);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}