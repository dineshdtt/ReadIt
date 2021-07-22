package in.tts.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.firebase.crash.FirebaseCrash;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;

import in.tts.R;
import in.tts.activities.PdfShowingActivity;
import in.tts.utils.CommonMethod;
import in.tts.utils.FilePath;
import in.tts.utils.RealPathUtil;
import in.tts.utils.ToCheckFileExists;

public class BrowsePdfFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    public BrowsePdfFragment() {
        // Required empty public constructor
    }


    public static BrowsePdfFragment newInstance() {
        return new BrowsePdfFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_browse_pdf, container, false);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            Objects.requireNonNull(getActivity()).findViewById(R.id.btnGetPdf).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        CommonMethod.toCallLoader(getContext(), "Loading...");
                        Intent intent = new Intent();
//                        intent.setType("file/pdf");
                        intent.setType("application/pdf");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select PDF"), 201);
                        CommonMethod.toCloseLoader();
                    } catch (Exception | Error e) {
                        e.printStackTrace();
                        FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                        Crashlytics.logException(e);
                        FirebaseCrash.report(e);
                        CommonMethod.toCloseLoader();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            Log.d("TAG ", " Bro onActivityResult " + requestCode + ":" + resultCode + ":" + data.getExtras() + ": " + data.getData() + ":   " + data.getData().getPath());
            String path = FilePath.getPath(getContext(), data.getData());
            Log.d("TAG", "File Name " + path);
            if (path != null) {
                try {
                    CommonMethod.toCallLoader(getContext(), "Loading...");
                    Intent intent = new Intent(getContext(), PdfShowingActivity.class);
                    intent.putExtra("file", path);
                    getContext().startActivity(intent);
                    CommonMethod.toCloseLoader();
                } catch (Exception | Error e) {
                    e.printStackTrace();
                    FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                    Crashlytics.logException(e);
                    FirebaseCrash.report(e);
                    CommonMethod.toDisplayToast(getContext(), "Unable to fetch Pdf");
                }
            } else {
                CommonMethod.toDisplayToast(getContext(), "Select File is not Pdf");
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
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
        Log.d("Tag", "tab21 setLoadData ");
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}