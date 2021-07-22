package in.tts.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.perf.metrics.AddTrace;

import in.tts.R;
import in.tts.activities.BrowserActivity;
import in.tts.utils.CommonMethod;

public class BrowserFragment extends Fragment {
    private EditText editText;
    private Button button;
    private ImageView ivBookmark1, ivBookmark2, ivBookmark3, ivRecent1, ivRecent2, ivRecent3, ivRecent4, ivRecent5, ivRecent6;

    public BrowserFragment() {
    }

    @Override
    @AddTrace(name = "onCreateBrowserFragment", enabled = true)
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        CommonMethod.toCallLoader(getContext(), "Please wait");
        return inflater.inflate(R.layout.fragment_browser, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        try {
            super.onActivityCreated(savedInstanceState);
            CommonMethod.setAnalyticsData(getContext(), "MainTab", "Browser", null);
            CommonMethod.toCloseLoader();
            CommonMethod.toReleaseMemory();

            editText = getActivity().findViewById(R.id.edtBrowser);
            button = getActivity().findViewById(R.id.btnSearch);
            // Bookmarks.........
            ivBookmark1 = getActivity().findViewById(R.id.wikipedia);
            ivBookmark2 = getActivity().findViewById(R.id.ivAmazone);
            ivBookmark3 = getActivity().findViewById(R.id.ivXerox);

            // Recent Tabs....
            ivRecent1 = getActivity().findViewById(R.id.ivRecent1);
            ivRecent2 = getActivity().findViewById(R.id.ivRecent2);
            ivRecent3 = getActivity().findViewById(R.id.ivRecent3);
            ivRecent4 = getActivity().findViewById(R.id.ivRecent4);
            ivRecent5 = getActivity().findViewById(R.id.ivRecent5);
            ivRecent6 = getActivity().findViewById(R.id.ivRecent6);

            // Bookmark page...................
            ivBookmark1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CommonMethod.toCallLoader(getContext(), "Please wait");
                    startActivity(
                            new Intent(getContext(), BrowserActivity.class)
                                    .putExtra("url", "https://www.wikipedia.org/"));
                    CommonMethod.toCloseLoader();
                }
            });

            ivBookmark2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CommonMethod.toCallLoader(getContext(), "Please wait");
                    startActivity(
                            new Intent(getContext(), BrowserActivity.class)
                                    .putExtra("url", "https://www.amazon.in"));
                    CommonMethod.toCloseLoader();
                }
            });

            ivBookmark3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CommonMethod.toCallLoader(getContext(), "Please wait");
                    startActivity(
                            new Intent(getContext(), BrowserActivity.class)
//                                .putExtra("url", "https://www.xerox.com/"));
                                    .putExtra("url", "http://drive.google.com/viewerng/viewer?embedded=true&url=https://saidnazulfiqar.files.wordpress.com/2008/04/cambridge-english-grammar-understanding-the-basics.pdf"));
                    CommonMethod.toCloseLoader();
                }
            });

            // Recent page ......

            ivRecent1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CommonMethod.toCallLoader(getContext(), "Please wait");
                    startActivity(
                            new Intent(getContext(), BrowserActivity.class)
                                    .putExtra("url", "https://www.facebook.com/"));
                    CommonMethod.toCloseLoader();
                }
            });

            ivRecent2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(
                            new Intent(getContext(), BrowserActivity.class)
                                    .putExtra("url", "https://www.wikipedia.org/"));
                }
            });

            ivRecent3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(
                            new Intent(getContext(), BrowserActivity.class)
                                    .putExtra("url", "https://www.blogger.com"));
                    CommonMethod.toCloseLoader();
                }
            });

            ivRecent4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(
                            new Intent(getContext(), BrowserActivity.class)
                                    .putExtra("url", "https://www.swiggy.com/"));
                    CommonMethod.toCloseLoader();
                }
            });

            ivRecent5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(
                            new Intent(getContext(), BrowserActivity.class)
                                    .putExtra("url", "http://dttminer.com/"));
                    CommonMethod.toCloseLoader();
                }
            });

            ivRecent6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(
                            new Intent(getContext(), BrowserActivity.class)
                                    .putExtra("url", "https://twitter.com/login?lang=en"));
                    CommonMethod.toCloseLoader();
                }
            });


            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CommonMethod.toCallLoader(getContext(), "Please wait");
                    if (editText != null && editText.getText().toString().trim().length() != 0) {
                        startActivity(
                                new Intent(getContext(), BrowserActivity.class)
                                        .putExtra("Data", editText.getText().toString().trim()));
                        CommonMethod.toDisplayToast(getContext(), "To " + editText.getText().toString().trim());
                    } else {
                        CommonMethod.toDisplayToast(getContext(), "To Data Found");
                    }
                    CommonMethod.toCloseLoader();
                }
            });
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            CommonMethod.toReleaseMemory();
        }
    }

    private OnFragmentInteractionListener mListener;

    public static BrowserFragment newInstance() {
        return new BrowserFragment();
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
            Crashlytics.logException(e);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setLoadData() {
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
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
    public void onStart() {
        super.onStart();
        CommonMethod.toReleaseMemory();
    }

    @Override
    public void onPause() {
        super.onPause();
        CommonMethod.toReleaseMemory();
        CommonMethod.toCloseLoader();
    }
}