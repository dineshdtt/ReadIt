package in.tts.activities;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.firebase.crash.FirebaseCrash;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import in.tts.R;
import in.tts.network.VolleySingleton;
import in.tts.utils.CommonMethod;

public class Contact_us extends AppCompatActivity {

    private Button mBtnSave;

    private EditText mEditEmailId;
    private EditText mEditMessage;
    private EditText mEditMobileNo;
    private EditText mEditName;

    private TextView mTxtEmailIdError;
    private TextView mTxtMessageError;
    private TextView mTxtMobileNoError;
    private TextView mTxtNameError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        try {
            if (getSupportActionBar() != null) {
                CommonMethod.toSetTitle(getSupportActionBar(), Contact_us.this, getString(R.string.app_name));
            }

            mTxtNameError = findViewById(R.id.txtCuUserNameError);

            mTxtMobileNoError = findViewById(R.id.txtCuUserMobileNumberError);

            mTxtEmailIdError = findViewById(R.id.txtCuUserEmailError);

            mTxtMessageError = findViewById(R.id.txtCuUserMessageError);

            mEditName = findViewById(R.id.edtCuUserName);
            mEditMobileNo = findViewById(R.id.edtCuUserMobileNumber);

            mEditEmailId = findViewById(R.id.edtCuUserEmail);
            mEditMessage = findViewById(R.id.edtCuUserMessage);

            mBtnSave = findViewById(R.id.btnCuSave);
            mBtnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (validateName() && validateEmail() && validateMobile() && validateFeedBack()) {
                            checkInternetConnection();
                        }
                    } catch (Exception | Error e) {
                        e.printStackTrace();
                        FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                        CommonMethod.toCloseLoader();
                        Crashlytics.logException(e);
                        FirebaseCrash.report(e);
                    }
                }
            });


        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            CommonMethod.toCloseLoader();
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    private void checkInternetConnection() {
        try {
            if (CommonMethod.isOnline(Contact_us.this)) {
                new submitFeedBack().execute();
            } else {
                CommonMethod.toDisplayToast(Contact_us.this, getResources().getString(R.string.lbl_no_check_internet));
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
        }
    }

    public boolean validateEmail() {
        try {
            if (mEditEmailId.getText().toString().trim().length() == 0) {
                mTxtEmailIdError.setText(getResources().getString(R.string.str_field_cant_be_empty));
                mTxtEmailIdError.setVisibility(View.VISIBLE);
                return false;
            } else if (CommonMethod.isValidEmail(mEditEmailId.getText().toString().trim())) {
                mTxtEmailIdError.setText("");
                mTxtEmailIdError.setVisibility(View.GONE);
                return true;
            } else {
                mTxtEmailIdError.setText(getString(R.string.str_error_valid_email));
                mTxtEmailIdError.setVisibility(View.VISIBLE);
                return false;
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            CommonMethod.toCloseLoader();
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            return false;
        }
    }

    public boolean validateMobile() {
        try {
            if (mEditMobileNo.getText().toString().trim().length() == 0) {
                mTxtMobileNoError.setText(getResources().getString(R.string.str_field_cant_be_empty));
                mTxtMobileNoError.setVisibility(View.VISIBLE);
                return false;
            } else if (CommonMethod.isValidMobileNo(mEditMobileNo.getText().toString().trim())) {
                mTxtMobileNoError.setText("");
                mTxtMobileNoError.setVisibility(View.GONE);
                return true;
            } else if (mEditMobileNo.getText().toString().trim().length() != 10) {
                mTxtMobileNoError.setText(getString(R.string.str_error_valid_mobile));
                mTxtMobileNoError.setVisibility(View.VISIBLE);
                return false;
            } else {
                mTxtMobileNoError.setText(getString(R.string.str_error_valid_mobile));
                mTxtMobileNoError.setVisibility(View.VISIBLE);
                return false;
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            CommonMethod.toCloseLoader();
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            return false;
        }
    }

    public boolean validateName() {
        try {
            if (mEditName.getText().toString().trim().length() == 0) {
                mTxtNameError.setText(getResources().getString(R.string.str_field_cant_be_empty));
                mTxtNameError.setVisibility(View.VISIBLE);
                return false;
            } else {
                mTxtNameError.setVisibility(View.GONE);
                return true;
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            CommonMethod.toCloseLoader();
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            return false;
        }
    }

    public boolean validateFeedBack() {
        try {
            if (mEditMessage.getText().toString().trim().length() == 0) {
                mTxtMessageError.setText(getResources().getString(R.string.str_field_cant_be_empty));
                mTxtMessageError.setVisibility(View.VISIBLE);
                return false;
            } else {
                mTxtMessageError.setVisibility(View.GONE);
                return true;
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            CommonMethod.toCloseLoader();
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            return false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        toReset();
    }

    private void toReset() {
        try {
            if (mEditName != null) {
                mEditName.setText("");
            }

            if (mEditEmailId != null) {
                mEditEmailId.setText("");
            }

            if (mEditMobileNo != null) {
                mEditMobileNo.setText("");
            }

            if (mEditMessage != null) {
                mEditMessage.setText("");
            }


            if (mTxtNameError != null) {
                mTxtNameError.setVisibility(View.GONE);
            }

            if (mTxtEmailIdError != null) {
                mTxtEmailIdError.setVisibility(View.GONE);
            }

            if (mTxtMobileNoError != null) {
                mTxtMobileNoError.setVisibility(View.GONE);
            }
            if (mTxtMessageError != null) {
                mTxtMessageError.setVisibility(View.GONE);
            }

        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            CommonMethod.toCloseLoader();
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class submitFeedBack extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                VolleySingleton.getInstance(Contact_us.this)
                        .addToRequestQueue(
                                new StringRequest(Request.Method.POST,
                                        "http://vnoi.in/ttsApi/feedback.php",
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                try {
                                                    Log.d("TAG", "feedback Response " + response);
                                                    if (response != null) {
                                                        JSONObject obj = new JSONObject(response.trim());
                                                        if (obj != null) {
                                                            if (!obj.isNull("status")) {
                                                                if (obj.getString("status").trim().equals("1")) {
                                                                    Log.d("TAG", " success  ");
                                                                    toExit();
                                                                } else {
                                                                    Log.d("TAG", " failed ");
                                                                }
                                                            }
                                                        }
                                                    }
                                                } catch (Exception | Error e) {
                                                    e.printStackTrace();
                                                    FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                                                    Crashlytics.logException(e);
                                                    FirebaseCrash.report(e);
                                                }
                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Log.d("TAG", " error " + error.getMessage());
                                            }
                                        }
                                ) {
                                    @Override
                                    protected Map<String, String> getParams() {
                                        Map<String, String> params = new HashMap<String, String>();
                                        params.put("action", "feedback");
                                        params.put("name", mEditName.getText().toString().trim());
                                        params.put("email", mEditEmailId.getText().toString().trim());
                                        params.put("msg", mEditMessage.getText().toString().trim());
                                        params.put("mobile_no", mEditMobileNo.getText().toString().trim());
                                        return params;
                                    }
                                }
                                , "Feedback");
            } catch (Exception | Error e) {
                e.printStackTrace();
                FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                CommonMethod.toCloseLoader();
                Crashlytics.logException(e);
                FirebaseCrash.report(e);
            }
            return null;
        }
    }

    private void toExit() {
        try {
            CommonMethod.toDisplayToast(Contact_us.this, " Feedback Submitted Successfully ");
            toReset();
            finish();
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            CommonMethod.toCloseLoader();
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }
}