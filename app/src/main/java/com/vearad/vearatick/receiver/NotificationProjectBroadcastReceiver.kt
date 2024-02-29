package com.vearad.vearatick.receiver

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.util.Log
import androidx.core.app.NotificationCompat
import com.jakewharton.threetenabp.AndroidThreeTen
import com.kizitonwose.calendarview.utils.persian.toPersianCalendar
import com.vearad.vearatick.R
import com.vearad.vearatick.model.db.AppDatabase
import com.vearad.vearatick.model.db.Project
import com.vearad.vearatick.model.db.ProjectDao
import com.vearad.vearatick.ui.activitymain.ProAndEmpActivity
import org.joda.time.DateTime
import org.joda.time.Days
import org.threeten.bp.LocalDate
import java.util.Calendar


class NotificationProjectBroadcastReceiver : BroadcastReceiver() {

    lateinit var projectDao: ProjectDao
    private val sharedPreferences: SharedPreferences? = null

    override fun onReceive(context: Context, intent: Intent) {
        AndroidThreeTen.init(context)
        presenceNotification(context)
        abortBroadcast()
        //alarmProject(context,intent)
    }
    private fun alarmProject(context: Context, intent: Intent) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val pendingIntentProject =
            PendingIntent.getBroadcast(context, 1002, intent, PendingIntent.FLAG_IMMUTABLE)

        val targetHours = arrayOf(12,18)
        for (hour in targetHours) {
            Log.v("alarmProject", "alarmProject: ${calculateTimeDifferenceInMillis(hour,15)}")
            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calculateTimeDifferenceInMillis(hour,15),
                AlarmManager.INTERVAL_DAY,
                pendingIntentProject
            )
        }
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
    private fun presenceNotification(context: Context) {
        Log.v("alarmProject", "Here: go to BroadcastReceiver Project")

        projectDao = AppDatabase.getDataBase(context).projectDao
        val today = LocalDate.now().toPersianCalendar()
        val projects = projectDao.getAllNoDoneProject(false,false)

        for (project in projects) {

            val startDate =
                DateTime(today.persianYear, today.persianMonth, today.persianDay, 0, 0, 0)
            val endDate = DateTime(
                project.yearCreation,
                project.monthCreation,
                project.dayCreation,
                0,
                0,
                0
            )
            var daysBetween = Days.daysBetween(startDate, endDate).days

            when(daysBetween){
                5-> createProjectNotification(project,context,5)
                2-> createProjectNotification(project,context,2)
                1-> createProjectTomorrowNotification(project,context)
                0-> createProjectTodayNotification(project,context)

            }
        }
    }
    private fun createProjectNotification(project: Project, context: Context, Deadline:Int) {

        val intent = Intent(context, ProAndEmpActivity::class.java)
        intent.putExtra("PROJECT", project?.idProject)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        val pendingIntent = PendingIntent.getActivity(context,1020,intent, PendingIntent.FLAG_IMMUTABLE)

        val notificationManager = context.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat
            .Builder(context, "projectNotif")
            .setSmallIcon(R.drawable.img_logo)
            .setLargeIcon(
                BitmapFactory.decodeResource(context.resources,
                    R.drawable.img_logo
                ))
            .setContentTitle("سرسید پروژه")
            .setContentText("مهلت پروژه ${project.nameProject} ${Deadline} روز است. ")
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(project.idProject!!, notification)

    }
    private fun createProjectTomorrowNotification(project: Project, context: Context) {
        val intent = Intent(context, ProAndEmpActivity::class.java)
        intent.putExtra("PROJECT", project?.idProject)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        val pendingIntent = PendingIntent.getActivity(context,1021,intent, PendingIntent.FLAG_IMMUTABLE)

        val notificationManager = context.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat
            .Builder(context, "projectNotif")
            .setSmallIcon(R.drawable.img_logo)
            .setLargeIcon(
                BitmapFactory.decodeResource(context.resources,
                    R.drawable.img_logo
                ))
            .setContentTitle("سرسید پروژه")
            .setContentText("مهلت پروژه ${project.nameProject} فردا به سر میرسد. ")
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(project.idProject!!, notification)
    }
    private fun createProjectTodayNotification(project: Project, context: Context) {
        val intent = Intent(context, ProAndEmpActivity::class.java)
        intent.putExtra("PROJECT", project?.idProject)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        val pendingIntent = PendingIntent.getActivity(context,1022,intent, PendingIntent.FLAG_IMMUTABLE)

        val notificationManager = context.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat
            .Builder(context, "projectNotif")
            .setSmallIcon(R.drawable.img_logo)
            .setLargeIcon(
                BitmapFactory.decodeResource(context.resources,
                    R.drawable.img_logo
                ))
            .setContentTitle("سرسید پروژه")
            .setContentText("مهلت پروژه ${project.nameProject} امروز است. ")
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(project.idProject!!, notification)
    }

}