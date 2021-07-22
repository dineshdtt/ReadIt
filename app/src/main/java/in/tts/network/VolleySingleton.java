package in.tts.network;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.Arrays;

@SuppressLint("StaticFieldLeak")
public class VolleySingleton {
    private static VolleySingleton mInstance;
    private RequestQueue mRequestQueue;
    private static Context mContext;

    private VolleySingleton(Context context) {
        mContext = context;
        mRequestQueue = getRequestQueue();
    }

    private RequestQueue getRequestQueue() {
        // If RequestQueue is null the initialize new RequestQueue
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }

        // Return RequestQueue
        return mRequestQueue;
    }

    public static synchronized VolleySingleton getInstance(Context context) {
        // If Instance is null then initialize new Instance
        if (mInstance == null) {
            mInstance = new VolleySingleton(context);
        }
        // Return MySingleton new Instance
        return mInstance;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        try {
            Log.d("Req_URL", " tag : " + tag + " : " + req.getUrl() + " : " + new String(req.getBody(), "UTF-8"));
            getRequestQueue().add(req).setTag(tag);
        } catch (Exception | Error e) {
            e.printStackTrace();
        }
    }

    public void cancelPendingRequests(Object tag) {
        if (this.mRequestQueue != null) {
            this.mRequestQueue.cancelAll(tag);
        }
    }

    public <T> void addToRequestQueue(Request<T> request) {
        // Add the specified request to the request queue
        getRequestQueue().add(request);
    }
}