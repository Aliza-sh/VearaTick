package com.vearad.vearatick.workers

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
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
import java.util.concurrent.TimeUnit

class NotificationProjectWorker(val context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {
    lateinit var projectDao: ProjectDao
    override fun doWork(): Result {
        AndroidThreeTen.init(context)
        workerProject()
        Log.v("ProjectWorker", "Here: Project")
        return try {
            presenceNotification(context)
            Result.success()
        } catch (ex: Exception) {
            Result.failure()
        }

    }

    private fun workerProject() {
        val workManager = WorkManager.getInstance(context)
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
        Log.v("ProjectWorker", "Here: go to work Project")

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