package com.wokconns.customer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.wokconns.customer.interfaces.Const
import com.wokconns.customer.preferences.SharedPrefs
import com.wokconns.customer.preferences.SharedPrefs.Companion.getInstance
import com.wokconns.customer.ui.activity.BaseActivity

class MyFirebaseMessagingService : FirebaseMessagingService() {
    lateinit var sharedPreferences: SharedPreferences
    var prefrence: SharedPrefs? = null
    var i = 0

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        prefrence = getInstance(this)

        Log.e(TAG, "From: " + remoteMessage.from)

        if (remoteMessage.data.isNotEmpty()) {
            Log.e(TAG, "Message data payload: " + remoteMessage.data)
        }

        if (remoteMessage.data.containsKey(Const.TYPE)) {
            when {
                remoteMessage.data[Const.TYPE].equals(Const.CHAT_NOTIFICATION,
                    ignoreCase = true) -> sendNotification(getValue(remoteMessage.data, "body"),
                    Const.CHAT_NOTIFICATION)
                remoteMessage.data[Const.TYPE].equals(Const.TICKET_COMMENT_NOTIFICATION,
                    ignoreCase = true) -> sendNotification(getValue(remoteMessage.data, "body"),
                    Const.TICKET_COMMENT_NOTIFICATION)
                remoteMessage.data[Const.TYPE].equals(Const.TICKET_STATUS_NOTIFICATION,
                    ignoreCase = true) -> sendNotification(getValue(remoteMessage.data, "body"),
                    Const.TICKET_STATUS_NOTIFICATION)
                remoteMessage.data[Const.TYPE].equals(Const.WALLET_NOTIFICATION,
                    ignoreCase = true) -> sendNotification(getValue(remoteMessage.data, "body"),
                    Const.WALLET_NOTIFICATION)
                remoteMessage.data[Const.TYPE].equals(Const.DECLINE_BOOKING_ARTIST_NOTIFICATION,
                    ignoreCase = true) -> sendNotification(getValue(remoteMessage.data, "body"),
                    Const.DECLINE_BOOKING_ARTIST_NOTIFICATION)
                remoteMessage.data[Const.TYPE].equals(Const.START_BOOKING_ARTIST_NOTIFICATION,
                    ignoreCase = true) -> sendNotification(getValue(remoteMessage.data, "body"),
                    Const.START_BOOKING_ARTIST_NOTIFICATION)
                remoteMessage.data[Const.TYPE].equals(Const.END_BOOKING_ARTIST_NOTIFICATION,
                    ignoreCase = true) -> sendNotification(getValue(remoteMessage.data, "body"),
                    Const.END_BOOKING_ARTIST_NOTIFICATION)
                remoteMessage.data[Const.TYPE].equals(Const.ACCEPT_BOOKING_ARTIST_NOTIFICATION,
                    ignoreCase = true) -> sendNotification(getValue(remoteMessage.data, "body"),
                    Const.ACCEPT_BOOKING_ARTIST_NOTIFICATION)
                remoteMessage.data[Const.TYPE].equals(Const.JOB_APPLY_NOTIFICATION,
                    ignoreCase = true) -> sendNotification(getValue(remoteMessage.data, "body"),
                    Const.JOB_APPLY_NOTIFICATION)
                remoteMessage.data[Const.TYPE].equals(Const.BRODCAST_NOTIFICATION,
                    ignoreCase = true) -> sendNotification(getValue(remoteMessage.data, "body"),
                    Const.BRODCAST_NOTIFICATION)
                remoteMessage.data[Const.TYPE].equals(Const.ADMIN_NOTIFICATION,
                    ignoreCase = true) -> sendNotification(getValue(remoteMessage.data, "body"),
                    Const.ADMIN_NOTIFICATION)
                else -> sendNotification(getValue(remoteMessage.data, "body"), "")
            }
        }
    }

    fun getValue(data: Map<String?, String?>, key: String?): String? {
        return try {
            if (data.containsKey(key)) data[key] else getString(R.string.app_name)
        } catch (ex: Exception) {
            ex.printStackTrace()
            getString(R.string.app_name)
        }
    }

    override fun onNewToken(token: String) {
        sharedPreferences = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(Const.DEVICE_TOKEN, token)
        editor.apply()
//        val userDetails = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE)
        Log.d(TAG, "Refreshed token: $token")
    }

    private fun sendNotification(messageBody: String?, tag: String) {
        val broadcastIntent = Intent()
        broadcastIntent.action = tag
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent)
        val intent = Intent(this, BaseActivity::class.java)

        intent.putExtra(Const.SCREEN_TAG, tag)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val channelId = "Wokconns_Channel"
        val defaultSoundUri =
            Uri.parse("android.resource://" + packageName + "/" + R.raw.notification)
        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(channelId, "Your Notifications",
                    NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.description = ""
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)
            notificationChannel.enableVibration(true)
            mNotificationManager.createNotificationChannel(notificationChannel)
        }

        // to diaplay notification in DND Mode
        // to diaplay notification in DND Mode
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = mNotificationManager.getNotificationChannel(channelId)
            channel.canBypassDnd()
        }

        val builder = NotificationCompat.Builder(this, channelId)
            .setAutoCancel(true)
            .setColor(ContextCompat.getColor(this, R.color.colorAccent))
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher_round))
            .setContentTitle(resources.getString(R.string.app_name))
            .setContentText(messageBody)
            .setContentIntent(pendingIntent)
            .setDefaults(Notification.DEFAULT_ALL)
            .setWhen(System.currentTimeMillis())
            .setSound(defaultSoundUri)

        mNotificationManager.notify(1000, builder.build())
    }

    companion object {
        const val MyPREFERENCES = "MyPrefs"
        private const val TAG = "MyFirebaseMsgService"
    }
}