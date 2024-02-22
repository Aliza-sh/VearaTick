package com.vearad.vearatick.di

import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.util.Log
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.vearad.vearatick.workers.AutomaticPresenceWorker
import com.vearad.vearatick.workers.NotificationPresenceWorker
import com.vearad.vearatick.workers.NotificationProjectWorker
import com.vearad.vearatick.workers.NotificationTaskEmployeeWorker
import java.util.Calendar
import java.util.concurrent.TimeUnit

class MyApp : Application() {

    lateinit var workManager: WorkManager
    lateinit var channels: ArrayList<NotificationChannel>

    @SuppressLint("ScheduleExactAlarm")
    override fun onCreate() {
        super.onCreate()

        workManager = WorkManager.getInstance(this)
        workerPresence()
        workerProject()
        workerTaskEmployee()
        workerAutomaticPresence()
        onCreateNotifications()
    }

    private fun workerPresence() {
        val notificationPresenceWorker = PeriodicWorkRequestBuilder<NotificationPresenceWorker>(
            1, TimeUnit.HOURS,
            15,TimeUnit.MINUTES
        )
            .build()
        workManager.enqueueUniquePeriodicWork(
            "PresenceWorker",
            ExistingPeriodicWorkPolicy.REPLACE,
            notificationPresenceWorker
        )
    }

    private fun workerProject() {
        val targetHours = arrayOf(13, 19)
        for (hour in targetHours) {
            Log.v("ProjectWorker", "$hour: ${calculateTimeDifferenceInMillis(hour,15)}")
            val notificationProjectWorker =
                    OneTimeWorkRequest.Builder(NotificationProjectWorker::class.java)
                        .setInitialDelay(calculateTimeDifferenceInMillis(hour,15), TimeUnit.MILLISECONDS)
                        .build()
                workManager.enqueueUniqueWork(
                    "ProjectWorker$hour",
                    ExistingWorkPolicy.REPLACE,
                    notificationProjectWorker
                )
        }
    }
    private fun workerTaskEmployee() {
        val targetHours = arrayOf(10,15)

        for (hour in targetHours) {
            Log.v("TaskWorker", "$hour: ${calculateTimeDifferenceInMillis(hour,15)}")
            val notificationTaskWorker =
                OneTimeWorkRequest.Builder(NotificationTaskEmployeeWorker::class.java)
                    .setInitialDelay(calculateTimeDifferenceInMillis(hour,15), TimeUnit.MILLISECONDS)
                    .build()
            workManager.enqueueUniqueWork(
                "TaskWorker$hour",
                ExistingWorkPolicy.REPLACE,
                notificationTaskWorker
            )
        }
    }
    private fun workerAutomaticPresence() {

            Log.v("AutoPresenceWorker", "22: ${calculateTimeDifferenceInMillis(22, 15)}")
            val notificationTaskWorker =
                OneTimeWorkRequest.Builder(AutomaticPresenceWorker::class.java)
                    .setInitialDelay(calculateTimeDifferenceInMillis(22, 15), TimeUnit.MILLISECONDS)
                    .build()
            workManager.enqueueUniqueWork(
                "AutomaticPresence",
                ExistingWorkPolicy.REPLACE,
                notificationTaskWorker
            )

    }
    fun calculateTimeDifferenceInMillis(targetHour: Int, targetMinute: Int): Long {
        val currentTime = Calendar.getInstance()
        val targetTime = Calendar.getInstance()

        targetTime.set(Calendar.HOUR_OF_DAY, targetHour)
        targetTime.set(Calendar.MINUTE, targetMinute)
        targetTime.set(Calendar.SECOND, 0)

        if (targetTime.before(currentTime)) {
            // If the target time is in the past, add one day
            targetTime.add(Calendar.DAY_OF_MONTH, 1)
        }

        return targetTime.timeInMillis - currentTime.timeInMillis
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