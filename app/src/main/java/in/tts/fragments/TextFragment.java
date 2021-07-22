package in.tts.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.firebase.crash.FirebaseCrash;

import java.util.Objects;

import in.tts.BuildConfig;
import in.tts.R;
import in.tts.utils.CommonMethod;

public class TextFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    public TextFragment() {

    }

    public static TextFragment newInstance(String param1, String param2) {
        return new TextFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_text, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            TextView textView = Objects.requireNonNull(getActivity()).findViewById(R.id.tvData);
            if (getArguments() != null) {
                switch (Objects.requireNonNull(getArguments().getString("PAGE"))) {
                    case "1":
                        textView.setText(getResources().getString(R.string.str_faqs));
                        break;
                    case "2":
                        textView.setText(getResources().getString(R.string.str_terms_n_condition));
                        break;
                    default:
                        textView.setText(getString(R.string.app_name) + "\n\nVERSION_CODE: " + BuildConfig.VERSION_CODE + " \n\nVERSION_NAME: " + BuildConfig.VERSION_NAME);
                        break;
                }
            } else {
                textView.setText(getString(R.string.app_name) + "\n\nVERSION_CODE: " + BuildConfig.VERSION_CODE + " \n\nVERSION_NAME:" + BuildConfig.VERSION_NAME);
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            CommonMethod.toCloseLoader();
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
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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