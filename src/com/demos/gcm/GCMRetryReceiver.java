package com.demos.gcm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import com.google.android.gcm.GCMConstants;

public class GCMRetryReceiver extends BroadcastReceiver {
    public static final String KEY_RETRY_TOKEN = "retry_token";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        GCMHelper session = GCMHelper.getInstance(context);

        if (GCMConstants.INTENT_FROM_GCM_LIBRARY_RETRY.equals(action)) {

            long expectedRetryToken = session.getRetryToken();
            long actualRetryToken = intent.getLongExtra(KEY_RETRY_TOKEN, 0);

            if (expectedRetryToken != actualRetryToken) {
                Log.w("Got invalid retry token, do nothing");
                return;
            }

            String gcmToken = session.getGCMToken();

            if (TextUtils.isEmpty(gcmToken)) {
                Log.i("Retrying last GCM registration");
                session.register();
            } else {
                Log.i("Retrying last GCM unregistration");
                session.unregister();
            }
        }
    }
}