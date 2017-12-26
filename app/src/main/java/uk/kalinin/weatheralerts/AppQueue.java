package uk.kalinin.weatheralerts;

import android.content.Context;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;

/**
 * Created by kal on 20/12/2017.
 */

//  Heavily inspired by android documentation
// https://developer.android.com/training/volley/requestqueue.html

public class AppQueue {
    private static AppQueue mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    private AppQueue(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();

    }

    public static synchronized AppQueue getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new AppQueue(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            Cache cache = new DiskBasedCache(mCtx.getApplicationContext().getCacheDir(), 1024 * 1024); // 1MB cap

            Network network = new BasicNetwork(new HurlStack());

            mRequestQueue = new RequestQueue(cache, network);

            mRequestQueue.start();
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().getCache().clear();
        getRequestQueue().add(req);
    }
}
