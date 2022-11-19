package com.example.callsmanager;

import static android.telephony.TelephonyManager.EXTRA_INCOMING_NUMBER;
import static android.telephony.TelephonyManager.EXTRA_STATE;
import static android.telephony.TelephonyManager.EXTRA_STATE_OFFHOOK;
import static android.telephony.TelephonyManager.EXTRA_STATE_RINGING;
import static androidx.core.app.NotificationManagerCompat.IMPORTANCE_DEFAULT;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationChannelCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class IncomingCallsBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (shouldSkipIntent(intent)) {
            return;
        }

        String event = intent.getExtras().getString(EXTRA_STATE);
        String number = intent.getExtras().getString(EXTRA_INCOMING_NUMBER);

        if (!shouldPromptContactNotification(event, number)) {
            return;
        }

        promptContactNotification(context, event, number);
    }

    private boolean shouldSkipIntent(Intent intent) {
        return intent.getExtras().getString(EXTRA_INCOMING_NUMBER) == null;
    }

    private boolean shouldPromptContactNotification(String event, String number) {
        // if in contacts already
        // if we already showed notification

        if (!event.equals(EXTRA_STATE_RINGING) && !event.equals(EXTRA_STATE_OFFHOOK)) {
            return false;
        }

        return true;
    }

    private static final String NOTIFICATION_CHANNEL_ID = "CallsManager";

    private void promptContactNotification(Context context, String event, String number) {
        // Yes, No, Never
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        if (notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID) == null) {
            NotificationChannelCompat channel = new NotificationChannelCompat.Builder(NOTIFICATION_CHANNEL_ID, IMPORTANCE_DEFAULT)
                    .setVibrationEnabled(true)
                    .setName("Calls Manager")
                    .setDescription("New call from " + number)
                    .build();

            notificationManager.createNotificationChannel(channel);
        }


        // post request to server to save the call
//        get server url from shared preferences
        String serverUrl = context.getSharedPreferences("sharedPrefsSeverUrl", Context.MODE_PRIVATE).getString("sharedPrefsSeverUrl", "");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, serverUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                    }
                }


                ){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("number", number);
                params.put("event", event);
                return params;
            }
        };

        int socketTimeout = 120000;// 2 minutes
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);

        new Volley().newRequestQueue(context).add(stringRequest);



        final int notificationId = new Random().nextInt();

        Intent callReceiverDiscard = new Intent(context, CallNotificationActionService.class);
        callReceiverDiscard.putExtra("notificationId", notificationId);
        callReceiverDiscard.putExtra("action", "DISCARD");
        PendingIntent discardIntent = PendingIntent.getService(
                context, new Random().nextInt(), callReceiverDiscard,  PendingIntent.FLAG_MUTABLE);

        Intent callReceiverOk = new Intent(context, CallNotificationActionService.class);
        callReceiverOk.putExtra("notificationId", notificationId);
        callReceiverOk.putExtra("action", "OK");
        PendingIntent okIntent = PendingIntent.getService(
                context, new Random().nextInt(), callReceiverOk,  PendingIntent.FLAG_MUTABLE);

        Notification notification = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setTicker("New call from " + number)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText("New call from " + number)
//                .addAction(0, "No", discardIntent)
//                .addAction(0, "Yes", okIntent)
                .build();

        notificationManager.notify(notificationId, notification);
    }
}
