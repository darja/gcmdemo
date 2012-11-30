package com.demos.gcm;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMConstants;

public class GCMIntentService extends GCMBaseIntentService {
    private static final String KEY_MESSAGE = "message";
    private static final int REQUEST_CODE = 536;

    @Override
    protected void onMessage(Context context, Intent intent) {
        Log.v("Message received, [%s]", intent.getAction());
        Bundle extras = intent.getExtras();
        String message = extras.getString(KEY_MESSAGE);

        Log.v("Extras keys: [%s]", TextUtils.join(", ", extras.keySet().toArray(new String[extras.keySet().size()])));
        Log.v("Message: [%s]", message);

        Intent pushIntent = new Intent(context, PushActivity.class);
        pushIntent.putExtras(intent);

        PendingIntent pi = PendingIntent.getActivity(context, REQUEST_CODE, pushIntent, 0);

        NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        Notification n = new Notification(R.drawable.ic_notification, message, System.currentTimeMillis());
        n.setLatestEventInfo(context, message, "", pi);
        n.flags |= Notification.FLAG_AUTO_CANCEL;

        nm.notify(R.id.push_notification, n);
    }

    @Override
    protected void onError(Context context, String errorId) {
        Log.e("Error received [%s]", errorId);

        if (GCMConstants.ERROR_SERVICE_NOT_AVAILABLE.equals(errorId) ||
            GCMConstants.ERROR_AUTHENTICATION_FAILED.equals(errorId)) {
            GCMHelper gcm = GCMHelper.getInstance(context);

            long backoffTimeMs = gcm.getBackoffTime(); // Getting saved timeout
            long retryToken = gcm.generateRetryToken(); // Generating some kind of signature

            long nextAttempt = SystemClock.elapsedRealtime() + backoffTimeMs;
            Intent retryIntent = new Intent(GCMConstants.INTENT_FROM_GCM_LIBRARY_RETRY);
            retryIntent.putExtra("retry_token", retryToken);

            PendingIntent retryPendingIntent = PendingIntent.getBroadcast(context, 0, retryIntent, 0);
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            am.set(AlarmManager.ELAPSED_REALTIME, nextAttempt, retryPendingIntent);

            gcm.updateBackoff(backoffTimeMs * 2); // the next timeout will be twice more
        }
    }

    @Override
    protected void onRegistered(Context context, String registrationId) {
        Log.v("Push token registered in GCM [%s]", registrationId);

        GCMHelper gcm = GCMHelper.getInstance(context);
        gcm.saveGCMToken(registrationId);
        gcm.clearBackoff();

        // TODO Sending token on server can be here
    }

    @Override
    protected void onUnregistered(Context context, String registrationId) {
        Log.v("Push token unregistration [%s]", registrationId);

        if (TextUtils.isEmpty(registrationId)) {
            return;
        }

        GCMHelper gcm = GCMHelper.getInstance(context);
        gcm.removeGCMToken();
        gcm.clearBackoff();

        // TODO Sending remove token request to your server can be here
    }

    @Override
    protected String[] getSenderIds(Context context) {
        return new String[] {
            Consts.GCM_SENDER_ID
        };
    }
}
