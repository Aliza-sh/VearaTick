package com.vearad.vearatick.workers

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.kizitonwose.calendarview.utils.persian.PersianCalendar
import com.vearad.vearatick.R
import com.vearad.vearatick.model.db.AppDatabase
import com.vearad.vearatick.model.db.DayDao
import com.vearad.vearatick.model.db.Employee
import com.vearad.vearatick.model.db.EmployeeDao
import com.vearad.vearatick.model.db.TimeDao
import com.vearad.vearatick.ui.activitymain.ProAndEmpActivity

class NotificationPresenceWorker(val context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {
    lateinit var dayDao: DayDao
    lateinit var timeDao: TimeDao
    lateinit var employeeDao: EmployeeDao
    override fun doWork(): Result {
        Log.v("PresenceWorker", "Here: Presence")

        return try {
            presenceNotification(context)
            Result.success()
        } catch (ex: Exception) {
            Log.v("PresenceWorker", "error: ${ex.message}")
            Result.failure()
        }

    }

    private fun presenceNotification(context: Context) {
        Log.v("PresenceWorker", "Here: go to work presence")

        dayDao = AppDatabase.getDataBase(context).dayDao
        timeDao = AppDatabase.getDataBase(context).timeDao
        employeeDao = AppDatabase.getDataBase(context).employeeDao
        val employees = employeeDao.getAllEmployee()
        val today = PersianCalendar()

        for (employee in employees) {

            val dayEmployee = dayDao.getAllEntryExit(
                employee.idEmployee!!,
                today.persianYear.toString(),
                today.persianMonthName,
                today.persianWeekDayName
            )
            Log.v("PresenceWorker", "dayEmployee: ${dayEmployee}")
            if (dayEmployee != null) {

                val timeEmployee = timeDao.getAllArrivalDay(
                    employee.idEmployee!!,
                    today.persianYear.toString(),
                    today.persianMonthName.toString(),
                    today.persianDay.toString()
                )
                Log.v("PresenceWorker", "timeEmployee: ${timeEmployee}")
                Log.v("PresenceWorker", "hours: ${today.time.hours}")
                Log.v("PresenceWorker", "entry: ${dayEmployee?.entry}")
                if (timeEmployee == null && dayEmployee.entry!!.toInt() <= today.time.hours) {

                    Log.v("PresenceWorker", "hours: ${today.time.hours}")
                    Log.v("PresenceWorker", "entry: ${dayEmployee?.entry}")

                    createPresenceNotification(employee, context)
                }
            }
        }
    }
    private fun createPresenceNotification(employee: Employee, context: Context) {

        val intent = Intent(context, ProAndEmpActivity::class.java)
        intent.putExtra("IDEMPLOYEE", employee.idEmployee)
        Log.v("PresenceWorker", "NotificationBroadcastReceiver: ${employee.idEmployee}")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        val pendingIntent = PendingIntent.getActivity(context,1,intent, PendingIntent.FLAG_IMMUTABLE)

        val notificationManager = context.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat
            .Builder(context, "presenceNotif")
            .setSmallIcon(R.drawable.img_logo)
            .setLargeIcon(
                BitmapFactory.decodeResource(context.resources,
                R.drawable.img_logo
            ))
            .setContentTitle("غیبت کارمند")
            .setContentText("${employee!!.name} ${employee.family} هنوز در شرکت حاظر نشده است!!!")
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(employee.idEmployee!! + 10, notification)

    }

}