package com.vearad.vearatick

import android.app.AlarmManager
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.SystemClock
import android.util.Log

class MyApp : Application() {

    lateinit var channels: ArrayList<NotificationChannel>

    override fun onCreate() {
        super.onCreate()

        val intent = Intent(this, NotificationBroadcastReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 1001, intent, PendingIntent.FLAG_IMMUTABLE)
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        alarmManager.setInexactRepeating(
            AlarmManager.ELAPSED_REALTIME,
            SystemClock.elapsedRealtime(),
            AlarmManager.INTERVAL_HOUR,
            pendingIntent
        )

        onCreateNotifications()

    }

    private fun onCreateNotifications() {

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        channels = ArrayList<NotificationChannel>()

        val notificationPresenceChannel = NotificationChannel(
            "presenceNotif",
            "Presence Notification",
            NotificationManager.IMPORTANCE_HIGH
        )
        channels.add(notificationPresenceChannel)

        val notificationProjectAndTaskChannel = NotificationChannel(
            "projectAndTaskNotif",
            "Project and Task Notification",
            NotificationManager.IMPORTANCE_HIGH
        )
        channels.add(notificationProjectAndTaskChannel)

        val notificationEventChannel = NotificationChannel(
            "eventNotif",
            "Event Notification",
            NotificationManager.IMPORTANCE_HIGH
        )
        channels.add(notificationEventChannel)

        Log.v("channels", "Here: ${channels}")

        notificationManager.createNotificationChannels(channels)
    }

}