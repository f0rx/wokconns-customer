package com.wokconns.customer;
/**
 * Created by VARUN on 01/01/19.
 */

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.wokconns.customer.interfaces.Const;
import com.wokconns.customer.preferences.SharedPrefrence;
import com.wokconns.customer.ui.activity.BaseActivity;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public static final String MyPREFERENCES = "MyPrefs";
    private static final String TAG = "MyFirebaseMsgService";
    SharedPrefrence prefrence;
    int i = 0;
    SharedPreferences sharedpreferences;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        prefrence = SharedPrefrence.getInstance(this);

        Log.e(TAG, "From: " + remoteMessage.getFrom());
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Message data payload: " + remoteMessage.getData());
        }

        if (remoteMessage.getData() != null) {
            if (remoteMessage.getData().containsKey(Const.TYPE)) {
                if (remoteMessage.getData().get(Const.TYPE).equalsIgnoreCase(Const.CHAT_NOTIFICATION)) {
                    sendNotification(getValue(remoteMessage.getData(), "body"), Const.CHAT_NOTIFICATION);
                } else if (remoteMessage.getData().get(Const.TYPE).equalsIgnoreCase(Const.TICKET_COMMENT_NOTIFICATION)) {
                    sendNotification(getValue(remoteMessage.getData(), "body"), Const.TICKET_COMMENT_NOTIFICATION);
                } else if (remoteMessage.getData().get(Const.TYPE).equalsIgnoreCase(Const.TICKET_STATUS_NOTIFICATION)) {
                    sendNotification(getValue(remoteMessage.getData(), "body"), Const.TICKET_STATUS_NOTIFICATION);
                } else if (remoteMessage.getData().get(Const.TYPE).equalsIgnoreCase(Const.WALLET_NOTIFICATION)) {
                    sendNotification(getValue(remoteMessage.getData(), "body"), Const.WALLET_NOTIFICATION);
                } else if (remoteMessage.getData().get(Const.TYPE).equalsIgnoreCase(Const.DECLINE_BOOKING_ARTIST_NOTIFICATION)) {
                    sendNotification(getValue(remoteMessage.getData(), "body"), Const.DECLINE_BOOKING_ARTIST_NOTIFICATION);
                } else if (remoteMessage.getData().get(Const.TYPE).equalsIgnoreCase(Const.START_BOOKING_ARTIST_NOTIFICATION)) {
                    sendNotification(getValue(remoteMessage.getData(), "body"), Const.START_BOOKING_ARTIST_NOTIFICATION);
                } else if (remoteMessage.getData().get(Const.TYPE).equalsIgnoreCase(Const.END_BOOKING_ARTIST_NOTIFICATION)) {
                    sendNotification(getValue(remoteMessage.getData(), "body"), Const.END_BOOKING_ARTIST_NOTIFICATION);
                } else if (remoteMessage.getData().get(Const.TYPE).equalsIgnoreCase(Const.ACCEPT_BOOKING_ARTIST_NOTIFICATION)) {
                    sendNotification(getValue(remoteMessage.getData(), "body"), Const.ACCEPT_BOOKING_ARTIST_NOTIFICATION);
                } else if (remoteMessage.getData().get(Const.TYPE).equalsIgnoreCase(Const.JOB_APPLY_NOTIFICATION)) {
                    sendNotification(getValue(remoteMessage.getData(), "body"), Const.JOB_APPLY_NOTIFICATION);
                } else if (remoteMessage.getData().get(Const.TYPE).equalsIgnoreCase(Const.BRODCAST_NOTIFICATION)) {
                    sendNotification(getValue(remoteMessage.getData(), "body"), Const.BRODCAST_NOTIFICATION);
                } else if (remoteMessage.getData().get(Const.TYPE).equalsIgnoreCase(Const.ADMIN_NOTIFICATION)) {
                    sendNotification(getValue(remoteMessage.getData(), "body"), Const.ADMIN_NOTIFICATION);
                } else {
                    sendNotification(getValue(remoteMessage.getData(), "body"), "");
                }
            }

        }

    }

    public String getValue(Map<String, String> data, String key) {
        try {
            if (data.containsKey(key))
                return data.get(key);
            else
                return getString(R.string.app_name);
        } catch (Exception ex) {
            ex.printStackTrace();
            return getString(R.string.app_name);
        }
    }

    @Override
    public void onNewToken(String token) {
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(Const.DEVICE_TOKEN, token);
        editor.commit();
        SharedPreferences userDetails = MyFirebaseMessagingService.this.getSharedPreferences("MyPrefs", MODE_PRIVATE);
        Log.d(TAG, "Refreshed token: " + token);

    }

    private void sendNotification(String messageBody, String tag) {

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(tag);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);

        Intent intent = new Intent(this, BaseActivity.class);
        intent.putExtra(Const.SCREEN_TAG, tag);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        String channelId = "Default";
        Uri defaultSoundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notification);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setSound(defaultSoundUri)
                /*.setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))*/
                .setContentText(messageBody).setAutoCancel(true).setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Default channel", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }
        manager.notify(0, builder.build());
    }


}

