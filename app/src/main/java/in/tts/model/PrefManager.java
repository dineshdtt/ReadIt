package in.tts.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.perf.metrics.AddTrace;

import org.json.JSONObject;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import in.tts.utils.CommonMethod;

public class PrefManager {

    // Shared preferences file name
    private static final String PREF_NAME = "androidhive-welcome";

    private static final String IMAGE_LIST = "IMAGE_LIST";
    private static final String IMAGE_LIST_INFO = "IMAGE_LIST_INFO";

    private static final String PDF_LIST = "PDF_LIST";
    private static final String PDF_LIST_INFO = "PDF_LIST_INFO";

    private static final String IMAGE_LIST_RECENT = "IMAGE_LIST_RECENT";
    private static final String IMAGE_LIST_INFO_RECENT = "IMAGE_LIST_INFO_RECENT";

    private static final String PDF_LIST_RECENT = "PDF_LIST_RECENT";
    private static final String PDF_LIST_INFO_RECENT = "PDF_LIST_INFO_RECENT";

    private static final String USER_INFO = "USER_INFO";
    private static final String USER_INFO_PREFERS = "USER_INFO_PREFERS";

    private static final String AUDIO_SETTING_INFO = "AUDIO_SETTING_INFO";
    private static final String AUDIO_SETTING_PREFERS = "AUDIO_SETTING_PREFERS";

    private static final String WEB_PREFERENCES = "WEB_PREFERENCES";
    private static final String WEB_LINKS = "WEB_LINKS";

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

//    public static String data;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context _context;
    public static boolean AddedRecentImage = false;
    public static boolean AddedRecentPDF = false;

    public PrefManager(Context context) {
        _context = context;
        pref = _context.getSharedPreferences(PREF_NAME, 0);
        editor = pref.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    @AddTrace(name = "onCallIsFirstTimeLaunch", enabled = true)
    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public void getUserInfo() {
        try {
            String us = _context.getSharedPreferences(USER_INFO_PREFERS, 0).getString(USER_INFO, null);
            if (us != null) {
                User user = User.getUser(_context);
                JSONObject userJSON = new JSONObject(us);
                Log.d("TAG", " getUserInfo : " + userJSON);
                if (!userJSON.isNull("email")) {
                    user.setEmail(userJSON.getString("email"));
                }
                if (!userJSON.isNull("mobile")) {
                    user.setMobile(userJSON.getString("mobile"));
                }

                if (!userJSON.isNull("token")) {
                    user.setToken(userJSON.getString("token"));
                }

                if (!userJSON.isNull("id")) {
                    user.setId(userJSON.getString("id"));
                }

                if (!userJSON.isNull("userName")) {
                    user.setUsername(userJSON.getString("userName"));
                }

                if (!userJSON.isNull("fullName")) {
                    user.setFullName(userJSON.getString("fullName"));
                }

                if (!userJSON.isNull("picPath")) {
                    user.setPicPath(userJSON.getString("picPath"));
                }
            }
        } catch (Exception | Error e) {
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
        }
    }

    public void setUserInfo() {
        try {
            SharedPreferences.Editor editor = _context.getSharedPreferences(USER_INFO_PREFERS, 0).edit();
            editor.putString(USER_INFO, new JSONObject(new Gson().toJson(User.getUser(_context))).toString());
            editor.apply();
            editor.commit();
        } catch (Exception | Error e) {
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
        }
    }

    public void toClearUserInfo() {
        try {
            SharedPreferences.Editor editor = _context.getSharedPreferences(USER_INFO_PREFERS, 0).edit();
            editor.clear();
            editor.apply();
            editor.commit();
        } catch (Exception | Error e) {
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
        }
    }

    public void getAudioSetting() {
        try {
            String Audio = _context.getSharedPreferences(AUDIO_SETTING_PREFERS, 0).getString(AUDIO_SETTING_INFO, null);
            AudioSetting audioSetting = AudioSetting.getAudioSetting(_context);
            if (Audio != null) {
                JSONObject audioJSON = new JSONObject(Audio);
                if (!audioJSON.isNull("VoiceSelection")) {
                    audioSetting.setVoiceSelection(audioJSON.getString("VoiceSelection"));
                } else {
                    audioSetting.setVoiceSelection("Female");
                }

                if (!audioJSON.isNull("LangSelection")) {
                    audioSetting.setLangSelection(audioJSON.getString("LangSelection"));
                } else {
                    audioSetting.setLangSelection(String.valueOf(Locale.US));
                }

                if (!audioJSON.isNull("AccentSelection")) {
                    audioSetting.setAccentSelection(audioJSON.getString("AccentSelection"));
                } else {
                    audioSetting.setAccentSelection("Locale.US");
                }

                if (!audioJSON.isNull("VoiceSpeed")) {
                    audioSetting.setVoiceSpeed(audioJSON.getInt("VoiceSpeed"));
                } else {
                    audioSetting.setVoiceSpeed(0);
                }
            } else {
                audioSetting.setVoiceSelection("Male");
                audioSetting.setLangSelection(String.valueOf(Locale.US));
                audioSetting.setVoiceSpeed(0);
                audioSetting.setAccentSelection("Locale.US");
            }
        } catch (Exception | Error e) {
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
        }
    }

    public void setAudioSetting() {
        try {
            SharedPreferences.Editor editor = _context.getSharedPreferences(AUDIO_SETTING_PREFERS, 0).edit();
            editor.putString(AUDIO_SETTING_INFO, new JSONObject(new Gson().toJson(AudioSetting.getAudioSetting(_context))).toString());
            editor.apply();
            editor.commit();
        } catch (Exception | Error e) {
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
        }
    }

    public ArrayList<String> toGetPDFList() {
        try {
            String us = _context.getSharedPreferences(PDF_LIST, 0).getString(PDF_LIST_INFO, null);
            if (us != null) {
                return new ArrayList<>(Arrays.asList(us.trim().replaceAll("\\s+", "").replace("[", "").replace("]", "").trim().split(",")));
            } else {
                return null;
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            return null;
        }
    }

    public void toSetPDFFileList(ArrayList<String> list) {
        try {
            SharedPreferences.Editor editor = _context.getSharedPreferences(PDF_LIST, 0).edit();
//            if (status) {
//                editor.clear();
//            }
            editor.putString(PDF_LIST_INFO, list.toString());
            editor.apply();
            editor.commit();
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    public ArrayList<String> toGetImageList() {
        try {
            String us = _context.getSharedPreferences(IMAGE_LIST, 0).getString(IMAGE_LIST_INFO, null);
            if (us != null) {
                return new ArrayList<>(Arrays.asList(us.trim().replaceAll("\\s+", "").replace("[", "").replace("]", "").split(",")));
            } else {
                return null;
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            return null;
        }
    }

    public void toSetImageFileList(ArrayList<String> list) {
        try {
            SharedPreferences.Editor editor = _context.getSharedPreferences(IMAGE_LIST, 0).edit();
            editor.putString(IMAGE_LIST_INFO, list.toString());
            editor.apply();
            editor.commit();
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    public ArrayList<String> toGetPDFListRecent() {
        try {
            String us = _context.getSharedPreferences(PDF_LIST_RECENT, 0).getString(PDF_LIST_INFO_RECENT, null);
            if (us != null) {
                return new ArrayList<>(Arrays.asList(us.trim().replaceAll("\\s+", "").replace("[", "").replace("]", "").trim().split(",")));
            } else {
                return null;
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            return null;
        }
    }

    public void toSetPDFFileListRecent(ArrayList<String> list) {
        try {
            SharedPreferences.Editor editor = _context.getSharedPreferences(PDF_LIST_RECENT, 0).edit();
            editor.putString(PDF_LIST_INFO_RECENT, list.toString());
            editor.apply();
            editor.commit();
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    public ArrayList<String> toGetImageListRecent() {
        try {
            String us = _context.getSharedPreferences(IMAGE_LIST_RECENT, 0).getString(IMAGE_LIST_INFO_RECENT, null);
            if (us != null) {
                return new ArrayList<>(Arrays.asList(us.trim().replaceAll("\\s+", "").replace("[", "").replace("]", "").trim().split(",")));
            } else {
                return null;
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            return null;
        }
    }

    public void toSetImageFileListRecent(ArrayList<String> list) {
        try {
            SharedPreferences.Editor editor = _context.getSharedPreferences(IMAGE_LIST_RECENT, 0).edit();
            editor.putString(IMAGE_LIST_INFO_RECENT, list.toString());
            editor.apply();
            editor.commit();
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    public ArrayList<String> populateSelectedSearch() {
        try {
            String s = _context.getSharedPreferences(WEB_PREFERENCES, 0).getString(WEB_LINKS, null);
            if (s != null) {
                return new ArrayList<>(Arrays.asList(s.trim().replaceAll("\\s+", "").replace("[", "").replace("]", "").split(",")));
            } else {
                return null;
            }
        } catch (Exception | Error e) {
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            return null;
        }
    }

    public void setSearchResult(List<String> selectedSearch) {
        try {
            SharedPreferences.Editor editor = _context.getSharedPreferences(WEB_PREFERENCES, 0).edit();
            editor.putString(WEB_LINKS, selectedSearch.toString());
            editor.apply();
            editor.commit();
        } catch (Exception | Error e) {
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
        }
    }

//    public ArrayList<String> populateSelectedSearch() {
//        try {
//            String s = _context.getSharedPreferences(WEB_PREFERENCES, 0).getString(WEB_LINKS, null);
//            if (s != null) {
//                return new ArrayList<>(Arrays.asList(s.trim().replaceAll("\\s+", "").replace("[", "").replace("]", "").split(",")));
//            } else {
//                return null;
//            }
//        } catch (Exception | Error e) {
//            Crashlytics.logException(e);
//            FirebaseCrash.report(e);
//            e.printStackTrace();
//            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
//            return null;
//        }
//    }
//
//    public void setSearchResult(List<String> selectedSearch) {
//        try {
//            SharedPreferences.Editor editor = _context.getSharedPreferences(WEB_PREFERENCES, 0).edit();
//            editor.putString(WEB_LINKS, selectedSearch.toString());
//            editor.apply();
//            editor.commit();
//        } catch (Exception | Error e) {
//            Crashlytics.logException(e);
//            FirebaseCrash.report(e);
//            e.printStackTrace();
//            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
//        }
//    }
}