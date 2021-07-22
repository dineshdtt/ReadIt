package in.tts.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.crash.FirebaseCrash;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.perf.metrics.AddTrace;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import in.tts.R;
import in.tts.activities.HomeActivity;
import in.tts.activities.LoginActivity;
import in.tts.model.PrefManager;
import in.tts.model.User;
import in.tts.network.VolleySingleton;
import in.tts.utils.CommonMethod;

public class RegisterFragment extends Fragment {

    private LinearLayout llLoader;

    private EditText mEdtEmail, mEdtPassword, mEdtCnfPwd;
    private Button mBtnSignUp;
    private TextView mTvAlreadyLogin, mTvSkip;

    // Google
    private GoogleSignInClient mGoogleSignInClient;
    private RelativeLayout relativeLayoutGoogle;
    private GoogleSignInAccount account;

    // Facebook
    private RelativeLayout relativeLayoutFb;
    private CallbackManager callbackManager;

    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    private LoginManager mFbLoginManager;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    // User
    private User user;

    // Parameters
    private int RC_SIGN_IN = 123;

    public RegisterFragment() {
    }

    @Override

    @AddTrace(name = "onCreateRegisterFragment", enabled = true)
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            CommonMethod.toReleaseMemory();
            if (mAuth != null && mAuthListener != null) {
                mAuth.addAuthStateListener(mAuthListener);
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            CommonMethod.toCloseLoader();
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            CommonMethod.toReleaseMemory();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            CommonMethod.toReleaseMemory();
            if (mAuth != null && mAuthListener != null) {
                mAuth.removeAuthStateListener(mAuthListener);
            }

        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            CommonMethod.toCloseLoader();
            Crashlytics.logException(e);
            CommonMethod.toReleaseMemory();
            FirebaseCrash.report(e);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            CommonMethod.setAnalyticsData(getContext(), "MainTab", "Register", null);
            CommonMethod.toReleaseMemory();

            user = User.getUser(getContext());

            FacebookSdk.sdkInitialize(getContext());

            mEdtEmail = getActivity().findViewById(R.id.edtEmailIdReg);
            mEdtPassword = getActivity().findViewById(R.id.edtPasswordReg);
            mEdtCnfPwd = getActivity().findViewById(R.id.edtConfirmPasswordReg);

            mBtnSignUp = getActivity().findViewById(R.id.btnSignUpReg);

            mTvAlreadyLogin = getActivity().findViewById(R.id.txtAlreadyAccountReg);
            mTvSkip = getActivity().findViewById(R.id.txtSkipRegisterReg);

            mTvAlreadyLogin.setText(Html.fromHtml("<font color=#ccffffff>Already have an account? </font> <font color=#ffffff>Login!</font>"));

            // Google
            relativeLayoutGoogle = getActivity().findViewById(R.id.rlGoogleReg);

            //Facebook
            relativeLayoutFb = getActivity().findViewById(R.id.rlFacebookReg);

            llLoader = getActivity().findViewById(R.id.llCustom_loader500);
            //Get Firebase auth instance
            mAuth = FirebaseAuth.getInstance();
            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser account = firebaseAuth.getCurrentUser();
                    if (account != null) {
                        // User is signed in
                        // // Log.d("TAG", "onAuthStateChanged:signed_in:" + account.getUid());
                        user.setEmail(account.getEmail());
                        user.setId(account.getUid());
                        user.setUsername(account.getDisplayName());
                        user.setFullName(account.getDisplayName());
                        user.setPicPath(account.getPhotoUrl() != null ? account.getPhotoUrl().toString() : null);
                        user.setLoginFrom(1);
//                        toExit();
                    } else {
                        // User is signed out
                        // // Log.d("TAG", "onAuthStateChanged:signed_out");
                    }
                    // ...
                }
            };

            mEdtEmail.setOnEditorActionListener(new EditText.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_NEXT) {
                        if (validateEmail()) {
                            mEdtPassword.requestFocus();
                            return true;
                        } else {
                            mEdtEmail.requestFocus();
                            return false;
                        }
                    } else {
                        return false;
                    }
                }
            });

            mEdtPassword.setOnEditorActionListener(new EditText.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_NEXT) {
                        if (validatePassword()) {
                            mEdtCnfPwd.requestFocus();
                            return true;
                        } else {
                            mEdtPassword.requestFocus();
                            return false;
                        }
                    } else {
                        return false;
                    }
                }
            });

            mEdtCnfPwd.setOnEditorActionListener(new EditText.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_NEXT) {
                        if (validatePassword()) {
                            mBtnSignUp.requestFocus();
                            return true;
                        } else {
                            mEdtPassword.requestFocus();
                            return false;
                        }
                    } else {
                        return false;
                    }
                }
            });

            mBtnSignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (validateEmail() && validatePassword()) {
                            checkInternetConnection(1);
                            new Thread() {
                                @Override
                                public void run() {
                                    super.run();
                                    mAuth.signInWithEmailAndPassword(mEdtEmail.getText().toString(), mEdtPassword.getText().toString())
                                            .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {

                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    if (task.isSuccessful()) {
                                                        // // Log.d("TAG", " Login Successful2");
                                                    } else {
                                                        // // Log.d("TAG", " Login Failed ");
                                                    }
                                                }
                                            })
                                            .addOnCanceledListener(getActivity(), new OnCanceledListener() {
                                                @Override
                                                public void onCanceled() {
                                                    // // Log.d("TAG", " Login Cancel ");
                                                }
                                            });
                                }
                            };
                        }
                    } catch (Exception | Error e) {
                        CommonMethod.toCloseLoader();
                        e.printStackTrace();
                        FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                        Crashlytics.logException(e);
                        FirebaseCrash.report(e);
                    }
                }
            });

            mEdtEmail.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    validateEmail();
                }

                @Override
                public void afterTextChanged(Editable s) {
                    validateEmail();
                }
            });

            mEdtCnfPwd.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    validateCnfPassword();
                }

                @Override
                public void afterTextChanged(Editable s) {
                    validateCnfPassword();
                }
            });

            mEdtPassword.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    validatePassword();
                }

                @Override
                public void afterTextChanged(Editable s) {
                    validatePassword();
                }
            });

            //View
            mTvAlreadyLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        CommonMethod.toCallLoader(getContext(), "Loading....");
                        getContext().startActivity(new Intent(getContext(), LoginActivity.class).putExtra("LOGIN", "login"));
                        getActivity().finish();
                    } catch (Exception | Error e) {
                        e.printStackTrace();
                        FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                        CommonMethod.toCloseLoader();
                        Crashlytics.logException(e);
                        FirebaseCrash.report(e);
                    }
                }
            });

            mTvSkip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        llLoader.setVisibility(View.VISIBLE);
//                        CommonMethod.toCallLoader(getContext(), "Loading....");
                        getContext().startActivity(new Intent(getContext(), HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        getActivity().finish();
                        getActivity().overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                        CommonMethod.toCloseLoader();
                    } catch (Exception | Error e) {
                        e.printStackTrace();
                        FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                        CommonMethod.toCloseLoader();
                        Crashlytics.logException(e);
                        FirebaseCrash.report(e);
                    }
                }
            });

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();

            mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);

            relativeLayoutGoogle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        CommonMethod.toCallLoader(getContext(), "Login in with Facebook ");
                        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                        startActivityForResult(signInIntent, RC_SIGN_IN);
                        CommonMethod.toCloseLoader();
                    } catch (Exception | Error e) {
                        e.printStackTrace();
                        FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                        CommonMethod.toCloseLoader();
                        Crashlytics.logException(e);
                        FirebaseCrash.report(e);
                    }
                }
            });
            // Google

            //Facebook
            callbackManager = CallbackManager.Factory.create();

            mFbLoginManager = LoginManager.getInstance();

            accessTokenTracker = new AccessTokenTracker() {
                @Override
                protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {
                }
            };

            profileTracker = new ProfileTracker() {
                @Override
                protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {


                }
            };

            relativeLayoutFb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CommonMethod.toCallLoader(getContext(), "Login with Facebook ");
//                    if (accessTokenTracker.isTracking()) {
                    mFbLoginManager.logOut();
                    accessTokenTracker.stopTracking();
                    profileTracker.stopTracking();
//                        CommonMethod.toCloseLoader();
//                        CommonMethod.toDisplayToast(getContext(), " Click again  to login");
//                    } else {
                    accessTokenTracker.startTracking();
                    mFbLoginManager.logInWithReadPermissions(getActivity(), Arrays.asList("email", "public_profile"));//, "user_birthday"));
                    mFbLoginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            try {
                                AccessToken accessToken = loginResult.getAccessToken();
                                // // Log.d("TAG", "facebook:onSuccess:" + loginResult + ":" + accessToken);
                                handleFacebookAccessToken(loginResult.getAccessToken());
                                ProfileTracker profileTracker = new ProfileTracker() {
                                    @Override
                                    protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                                        if (currentProfile != null) {
                                            this.stopTracking();
                                            Profile.setCurrentProfile(currentProfile);
                                            user = User.getUser(getContext());
                                            user.setId(currentProfile.getId());
                                            user.setFullName(currentProfile.getFirstName() + " " + currentProfile.getMiddleName() + " " + currentProfile.getLastName());
                                            user.setUsername(currentProfile.getName());
                                            user.setLoginFrom(2);
                                            // // Log.d("TAG", " Pic Path : " + currentProfile.getProfilePictureUri(100, 100));
                                            user.setPicPath(currentProfile.getProfilePictureUri(1000, 1000).toString());
                                            CommonMethod.toCloseLoader();
                                            checkInternetConnection(3);
                                        }
                                    }
                                };
                                profileTracker.startTracking();
                            } catch (Exception | Error e) {
                                CommonMethod.toCloseLoader();
                                e.printStackTrace();
                                FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                                Crashlytics.logException(e);
                                FirebaseCrash.report(e);
//                                CommonMethod.toDisplayToast(getContext(), " Click again  to login");
                            }
                        }

                        @Override
                        public void onCancel() {
                            CommonMethod.toCloseLoader();
                        }

                        @Override
                        public void onError(FacebookException e) {
                            CommonMethod.toCloseLoader();
                        }
                    });
                }
            });

            accessTokenTracker.startTracking();
            profileTracker.startTracking();
            //Facebook

        } catch (Exception | Error e) {
            CommonMethod.toCloseLoader();
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            CommonMethod.toDisplayToast(getContext(), " Click again  to Register");
        }
    }

    private void checkInternetConnection(int i) {
        try {
            if (getContext() != null) {
                if (CommonMethod.isOnline(getContext())) {
                    switch (i) {
                        case 1:
                            new toRegister().execute();
                            break;
                        case 2:
                            new toGoogleLogin().execute();
                            break;
                        case 3:
                            new toFacebookLogin().execute();
                            break;
                        default:
                            CommonMethod.toDisplayToast(getContext(), " Unable to Login");
                            break;
                    }
                } else {
                    CommonMethod.toDisplayToast(getContext(), getResources().getString(R.string.lbl_no_check_internet));
                }
            } else {
                CommonMethod.toDisplayToast(getContext(), getResources().getString(R.string.lbl_no_check_internet));
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            CommonMethod.toCloseLoader();
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    public boolean validateEmail() {
        if (mEdtEmail.getText().toString().trim().length() == 0) {
            mEdtEmail.setError(getContext().getResources().getString(R.string.str_field_cant_be_empty));
            return false;
        } else if (CommonMethod.isValidEmail(mEdtEmail.getText().toString().trim())) {
            mEdtEmail.setError(null);
            return true;
        } else {
            mEdtEmail.setError(getString(R.string.str_error_valid_email));
            return false;
        }
    }

    public boolean validatePassword() {
        if (mEdtPassword.getText().toString().trim().length() == 0) {
            mEdtPassword.setError(getContext().getResources().getString(R.string.str_field_cant_be_empty));
            return false;
        } else if (mEdtPassword.getText().toString().trim().length() < 8) {
            mEdtPassword.setError(getString(R.string.str_error_minimum_8));
            return false;
        } else if (mEdtPassword.getText().toString().trim().length() > 15) {
            mEdtPassword.setError(getString(R.string.str_error_maximum_15));
            return false;
        } else if (CommonMethod.isValidPassword(mEdtPassword.getText().toString().trim())) {
            mEdtPassword.setError(getString(R.string.str_error_pswd));
            return false;
        } else {
            mEdtPassword.setError(null);
            return true;
        }
    }

    public boolean validateCnfPassword() {
        if (mEdtCnfPwd.getText().toString().trim().length() == 0) {
            mEdtCnfPwd.setError(getContext().getResources().getString(R.string.str_field_cant_be_empty));
            return false;
        } else if (mEdtCnfPwd.getText().toString().trim().length() < 8) {
            mEdtCnfPwd.setError(getString(R.string.str_error_minimum_8));
            return false;
        } else if (mEdtCnfPwd.getText().toString().trim().length() > 15) {
            mEdtCnfPwd.setError(getString(R.string.str_error_maximum_15));
            return false;
        } else if (CommonMethod.isValidPassword(mEdtCnfPwd.getText().toString().trim())) {
            mEdtCnfPwd.setError(getString(R.string.str_error_pswd));
            return false;
        } else {
            mEdtCnfPwd.setError(null);
            return true;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // Google
            if (requestCode == RC_SIGN_IN) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                handleSignInResult(task);
            }
            callbackManager.onActivityResult(requestCode, resultCode, data);
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            CommonMethod.toCloseLoader();
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    //Google
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            CommonMethod.toCallLoader(getContext(), "Login successful from Google");
            account = completedTask.getResult(ApiException.class);
            if (account != null) {
                firebaseAuthWithGoogle(account);
                user = User.getUser(getContext());
                user.setEmail(account.getEmail());
                user.setId(account.getId());
                user.setFcmToken(account.getIdToken());
                user.setUsername(account.getDisplayName());
                user.setPicPath(account.getPhotoUrl() != null ? account.getPhotoUrl().toString() : null);
                user.setFullName(account.getGivenName() + " " + account.getFamilyName());
                user.setLoginFrom(1);
                CommonMethod.toCloseLoader();
                checkInternetConnection(2);
            }
            CommonMethod.toCloseLoader();
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            CommonMethod.toCloseLoader();
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    // Called when successfully logged in
    private void toExit() {
        try {
            CommonMethod.toCloseLoader();
            CommonMethod.toDisplayToast(getContext(), "Login Successful");
            llLoader.setVisibility(View.VISIBLE);
//            CommonMethod.toCallLoader(getContext(), "Logging....");
            new PrefManager(getContext()).setUserInfo();
            startActivity(new Intent(getContext(), HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK));
            if (getActivity() != null) {
                getActivity().finish();
                getActivity().overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
            }
//            CommonMethod.toCloseLoader();
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            CommonMethod.toCloseLoader();
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        try {
            // // Log.d("TAG", "firebaseAuthWithGoogle:" + acct.getId());
            if (getActivity() != null) {
                AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
                mAuth.signInWithCredential(credential)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // // Log.d("TAG", "signInWithCredential:onComplete:" + task.isSuccessful());

                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    // Log.d("TAG", "signInWithCredential", task.getException());
//                                    Toast.makeText(getContext(), "Authentication failed.",
//                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            CommonMethod.toCloseLoader();
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    private void handleFacebookAccessToken(AccessToken token) {
        try {
            // // Log.d("TAG", "handleFacebookAccessToken:" + token);
            if (getActivity() != null) {
                AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
                mAuth.signInWithCredential(credential)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    // // Log.d("TAG", "signInWithCredential:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    // // Log.d("TAG", " FB user " + user.getUid());
//                            updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    // Log.d("TAG", "signInWithCredential:failure", task.getException());
//                                    Toast.makeText(getContext(), "Authentication failed.",
//                                            Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                                }

                                // ...
                            }
                        });
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            CommonMethod.toCloseLoader();
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        CommonMethod.toReleaseMemory();
    }

    private OnFragmentInteractionListener mListener;

    public static RegisterFragment newInstance() {
        return new RegisterFragment();
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

    private class toRegister extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                VolleySingleton.getInstance(getContext())
                        .addToRequestQueue(
                                new StringRequest(Request.Method.POST,
                                        "http://vnoi.in/ttsApi/register_login.php",
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                try {
                                                    // // Log.d("TAG", "Register Response " + response);
                                                    if (response != null) {
                                                        JSONObject obj = new JSONObject(response.trim());
                                                        if (obj != null) {
                                                            if (!obj.isNull("status")) {
                                                                if (obj.getString("status").trim().equals("1")) {
                                                                    // // Log.d("TAG", " Register success  ");
                                                                    user = User.getUser(getContext());
                                                                    user.setLoginFrom(3);
                                                                    if (!obj.isNull("id")) {
                                                                        user.setId(obj.getString("id"));
                                                                    }
//                                                                    user.setId(String.valueOf(System.currentTimeMillis()));
                                                                    user.setEmail(mEdtEmail.getText().toString());
                                                                    toExit();
                                                                } else {
                                                                    // // Log.d("TAG", " Register failed ");
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
                                                // // Log.d("TAG", " Register error " + error.getMessage());
                                            }
                                        }
                                ) {
                                    @Override
                                    protected Map<String, String> getParams() {
                                        Map<String, String> params = new HashMap<String, String>();
                                        params.put("action", "register");
                                        params.put("email", mEdtEmail.getText().toString());
                                        params.put("password", mEdtPassword.getText().toString());
                                        return params;
                                    }
                                }
                                , "REGISTER");
            } catch (Exception | Error e) {
                e.printStackTrace();
                FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                Crashlytics.logException(e);
                FirebaseCrash.report(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            CommonMethod.toCloseLoader();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            CommonMethod.toCallLoader(getContext(), "Authenticating user.....");
        }
    }

    //  Google details API fetching..............
    private class toGoogleLogin extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                VolleySingleton.getInstance(getContext())
                        .addToRequestQueue(
                                new StringRequest(Request.Method.POST,
                                        "http://vnoi.in/ttsApi/register_login.php",
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                try {
                                                    // // Log.d("TAG", "login Response " + response);
                                                    if (response != null) {
                                                        JSONObject obj = new JSONObject(response.trim());
                                                        if (obj != null) {
                                                            if (!obj.isNull("status")) {
                                                                if (obj.getString("status").trim().equals("1")) {
                                                                    // // Log.d("TAG", " login success  ");
                                                                    user = User.getUser(getContext());
                                                                    user.setLoginFrom(3);
                                                                    if (!obj.isNull("id")) {
                                                                        user.setId(obj.getString("id"));
                                                                    }
//                                                                    user.setId(String.valueOf(System.currentTimeMillis()));
                                                                    user.setEmail(mEdtEmail.getText().toString());
                                                                    toExit();
                                                                } else {
                                                                    // // Log.d("TAG", " login failed ");
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
                                                // // Log.d("TAG", " login error " + error.getMessage());
                                            }
                                        }
                                ) {
                                    @Override
                                    protected Map<String, String> getParams() {
                                        Map<String, String> params = new HashMap<String, String>();
                                        params.put("action", "google_details");
                                        params.put("email", account.getEmail());
                                        params.put("username", account.getDisplayName());
                                        params.put("name", account.getGivenName());
                                        params.put("pic_url", String.valueOf(account.getPhotoUrl()));
                                        // // Log.d("TAG", " glogin Params" + params);
                                        return params;
                                    }
                                }
                                , "google_details");
            } catch (Exception | Error e) {
                e.printStackTrace();
                FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                Crashlytics.logException(e);
                FirebaseCrash.report(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            CommonMethod.toCloseLoader();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            CommonMethod.toCallLoader(getContext(), "Authenticating user.....");
        }
    }

    //  facebook login details API fetching...................
    private class toFacebookLogin extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                VolleySingleton.getInstance(getContext())
                        .addToRequestQueue(
                                new StringRequest(Request.Method.POST,
                                        "http://vnoi.in/ttsApi/register_login.php",
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                try {
                                                    // // Log.d("TAG", "fb login Response " + response);
                                                    if (response != null) {
                                                        JSONObject obj = new JSONObject(response.trim());
                                                        if (obj != null) {
                                                            if (!obj.isNull("status")) {
                                                                if (obj.getString("status").trim().equals("1")) {
                                                                    // // Log.d("TAG", "fb login success  ");
                                                                    user = User.getUser(getContext());
                                                                    user.setLoginFrom(3);
                                                                    if (!obj.isNull("id")) {
                                                                        user.setId(obj.getString("id"));
                                                                    }
//                                                                    user.setId(String.valueOf(System.currentTimeMillis()));
                                                                    user.setEmail(mEdtEmail.getText().toString());
                                                                    toExit();
                                                                } else {
                                                                    // // Log.d("TAG", "fb login failed ");
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
                                                // // Log.d("TAG", "fb login error " + error.getMessage());
                                            }
                                        }
                                ) {

                                    @Override
                                    protected Map<String, String> getParams() {
                                        Map<String, String> params = new HashMap<String, String>();
                                        params.put("action", "fb_details");
                                        if (user.getEmail() != null) {
                                            params.put("email", user.getEmail());
                                        }
                                        if (user.getUsername() != null) {
                                            params.put("username", user.getUsername());
                                        }
                                        if (user.getPicPath() != null) {
                                            params.put("pic_url", user.getPicPath());
                                        }
                                        // // Log.d("TAG", " Fb params " + params);
                                        return params;
                                    }
                                }
                                , "fb_details");
            } catch (Exception | Error e) {
                e.printStackTrace();
                FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                Crashlytics.logException(e);
                FirebaseCrash.report(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            CommonMethod.toCloseLoader();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            CommonMethod.toCallLoader(getContext(), "Authenticating user.....");
        }
    }
}