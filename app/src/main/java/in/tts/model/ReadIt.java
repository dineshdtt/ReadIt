package in.tts.model;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.firebase.crash.FirebaseCrash;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.analytics.FirebaseAnalytics;

import android.content.Context;
import android.content.res.Configuration;
import android.support.multidex.MultiDex;
import android.util.Log;

//import cafe.adriel.androidaudioconverter.AndroidAudioConverter;
//import cafe.adriel.androidaudioconverter.callback.ILoadCallback;
import in.tts.R;
import io.fabric.sdk.android.Fabric;

public class ReadIt extends Application {

    public void onCreate() {
        super.onCreate();
        try {
            new FlurryAgent.Builder()
                    .withLogEnabled(true)
                    .build(this, getString(R.string.str_flurry_app));

            FirebaseAnalytics.getInstance(this);
            AppEventsLogger.activateApp(this);
            Fabric.with(this, new Crashlytics());

            final Fabric fabric = new Fabric.Builder(this)
                    .kits(new Crashlytics())
                    .debuggable(true)
                    .build();
            Fabric.with(fabric);

        } catch (Exception | Error e) {
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void onLowMemory() { super.onLowMemory(); }

    public void onTerminate() { super.onTerminate(); }

    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }
}
