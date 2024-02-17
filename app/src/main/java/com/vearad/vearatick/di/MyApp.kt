package com.vearad.vearatick.di

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.SystemClock
import android.util.Log
import com.vearad.vearatick.notification.NotificationPresenceBroadcastReceiver
import com.vearad.vearatick.notification.NotificationProjectBroadcastReceiver
import java.util.Calendar

class MyApp : Application() {

    lateinit var channels: ArrayList<NotificationChannel>

    @SuppressLint("ScheduleExactAlarm")
    override fun onCreate() {
        super.onCreate()

        alarmPresence()
        alarmProject()
        onCreateNotifications()

    }
    private fun alarmPresence() {
        val intentPresence = Intent(this, NotificationPresenceBroadcastReceiver::class.java)
        val pendingIntentPresence =
            PendingIntent.getBroadcast(this, 1001, intentPresence, PendingIntent.FLAG_IMMUTABLE)
        val alarmManagerPresence = getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManagerPresence.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            SystemClock.elapsedRealtime(),
            AlarmManager.INTERVAL_HOUR,
            pendingIntentPresence
        )
    }
    private fun alarmProject() {
        val intentProject = Intent(this, NotificationProjectBroadcastReceiver::class.java)
        val pendingIntentProject =
            PendingIntent.getBroadcast(this, 1002, intentProject, PendingIntent.FLAG_IMMUTABLE)
        val alarmManagerProject = getSystemService(ALARM_SERVICE) as AlarmManager
        // تنظیم آلارم برای ساعت 1 ظهر
        val alarmTime1 = getAlarmTime(13, 15)
        Log.v("alarmTime", "Here: ${alarmTime1}")
        alarmManagerProject.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            alarmTime1,
            AlarmManager.INTERVAL_DAY,
            pendingIntentProject
        )
        // تنظیم آلارم برای ساعت 7 شب
        val alarmTime2 = getAlarmTime(19, 15)
        alarmManagerProject.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            alarmTime2,
            AlarmManager.INTERVAL_DAY,
            pendingIntentProject
        )
    }
    // تابع برای دریافت زمان به میلی ثانیه
    fun getAlarmTime(hour: Int, minute: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        return calendar.timeInMillis
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
            "taskNotif",
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