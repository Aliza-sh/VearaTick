package com.vearad.vearatick.di

import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.util.Log
import androidx.work.WorkManager

class MyApp : Application() {

    lateinit var channels: ArrayList<NotificationChannel>

    @SuppressLint("ScheduleExactAlarm")
    override fun onCreate() {
        super.onCreate()

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

        val notificationProjectChannel = NotificationChannel(
            "projectNotif",
            "Project Notification",
            NotificationManager.IMPORTANCE_HIGH
        )
        channels.add(notificationProjectChannel)

        val notificationTaskChannel = NotificationChannel(
            "taskEmployeeNotif",
            "Task Notification",
            NotificationManager.IMPORTANCE_HIGH
        )
        channels.add(notificationTaskChannel)

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