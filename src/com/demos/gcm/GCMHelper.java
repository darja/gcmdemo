package com.demos.gcm;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.android.gcm.GCMRegistrar;

import java.util.Random;

public class GCMHelper {
    private static final long DEFAULT_BACKOFF = 10000;

    private Context mContext;
    private SharedPreferences mPreferences;

    private GCMHelper(Context context) {
        mContext = context.getApplicationContext();
        mPreferences = mContext.getSharedPreferences("session", Context.MODE_PRIVATE);
    }

    public void register() {
        GCMRegistrar.checkDevice(mContext);
        GCMRegistrar.checkManifest(mContext);
        String pushToken = GCMRegistrar.getRegistrationId(mContext);
        Log.v("RegistrationId=[%s]", pushToken);
        if (pushToken.equals("")) {
            GCMRegistrar.register(mContext, Consts.GCM_SENDER_ID);
        } else {
            Log.w("Already registered");
        }
    }

    public void unregister() {
        GCMRegistrar.unregister(mContext);
    }

    public synchronized void saveGCMToken(String gcmToken) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(PREF_GCM_TOKEN, gcmToken);
        editor.commit();
    }

    public synchronized void removeGCMToken() {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.remove(PREF_GCM_TOKEN);
        editor.commit();
    }

    public String getGCMToken() {
        String gcmToken = mPreferences.getString(PREF_GCM_TOKEN, null);
        Log.d("Stored GCM token: [%s]", gcmToken);
        return gcmToken;
    }


    public long getBackoffTime() {
        return mPreferences.getLong(PREF_GCM_BACKOFF, DEFAULT_BACKOFF);
    }

    public void updateBackoff(long backoff) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putLong(PREF_GCM_BACKOFF, backoff);
        editor.commit();
    }

    public void clearBackoff() {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.remove(PREF_GCM_BACKOFF);
        editor.commit();
    }

    public long generateRetryToken() {
        long retryToken = new Random(System.currentTimeMillis()).nextLong();

        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putLong(PREF_GCM_RETRY_TOKEN, retryToken);
        editor.commit();

        return retryToken;
    }

    public long getRetryToken() {
        return mPreferences.getLong(PREF_GCM_RETRY_TOKEN, 0);
    }


    private static volatile GCMHelper mInstance;

    public static GCMHelper getInstance(Context context) {
        if (mInstance == null) {
            synchronized (GCMHelper.class) {
                if (mInstance == null) {
                    mInstance = new GCMHelper(context);
                }
            }
        }
        return mInstance;
    }

    private static final String PREF_GCM_TOKEN = "gcm_token";
    private static final String PREF_GCM_BACKOFF = "backoff";
    private static final String PREF_GCM_RETRY_TOKEN = "retry_token";
}
