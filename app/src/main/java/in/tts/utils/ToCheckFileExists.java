package in.tts.utils;

import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.firebase.crash.FirebaseCrash;

import java.io.File;
import java.util.ArrayList;

public class ToCheckFileExists {

    public static boolean isPDFFile(String fileName) {
        try {
            Log.d("TAG", " isPDFFile "+fileName+":"+ fileName.endsWith(".pdf")+":"+ new File(fileName.trim().replaceAll("%20", " ")).exists() );
            return fileName.endsWith(".pdf") && new File(fileName.trim().replaceAll("%20", " ")).exists();
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            return false;
        }
    }

    public static boolean singleFile(String fileName) {
        try {
            return new File(fileName.trim().replaceAll("%20", " ")).exists();
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            return false;
        }
    }

    public static ArrayList<String> fileList(final ArrayList<String> fileList) {
        try {
            for (int i = 0; i < fileList.size(); i++) {
                if (!new File(fileList.get(i).trim().replaceAll("%20", " ")).exists()) {
                    fileList.remove(i);
                }
            }
            return fileList;
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            return fileList;
        }
    }
}