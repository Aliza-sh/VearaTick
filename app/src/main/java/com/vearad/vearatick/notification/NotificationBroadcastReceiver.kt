package com.vearad.vearatick.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import androidx.core.app.NotificationCompat
import com.kizitonwose.calendarview.utils.persian.PersianCalendar
import com.vearad.vearatick.R
import com.vearad.vearatick.model.db.AppDatabase
import com.vearad.vearatick.model.db.DayDao
import com.vearad.vearatick.model.db.EmployeeDao
import com.vearad.vearatick.model.db.TimeDao
import com.vearad.vearatick.ui.activitymain.ProAndEmpActivity

class NotificationBroadcastReceiver : BroadcastReceiver() {

    lateinit var dayDao: DayDao
    lateinit var timeDao: TimeDao
    lateinit var employeeDao: EmployeeDao
    override fun onReceive(context: Context, intent: Intent) {
        // اجرای سرویس
        presenceNotification(context)
    }

    private fun presenceNotification(context: Context) {
        dayDao = AppDatabase.getDataBase(context).dayDao
        timeDao = AppDatabase.getDataBase(context).timeDao
        val today = PersianCalendar()
        val numberDay = dayDao.getAllDay().size

        for (i in 0..numberDay) {

            val dayEmployee = dayDao.getAllEntryExit(
                i,
                today.persianYear.toString(),
                today.persianMonthName,
                today.persianWeekDayName
            )
            Log.v("NotificationService", "dayEmployee: ${dayEmployee}")

            val timeEmployee = timeDao.getAllArrivalDay(
                i,
                today.persianYear.toString(),
                today.persianMonthName.toString(),
                today.persianDay.toString()
            )
            Log.v("NotificationService", "timeEmployee: ${timeEmployee}")

            if (timeEmployee == null && dayEmployee != null && dayEmployee.entry!!.toInt() <= today.time.hours) {

                Log.v("NotificationService", "hours: ${today.time.hours}")
                Log.v("NotificationService", "entry: ${dayEmployee?.entry}")

                createPresenceNotification(i,context)
            }
        }
    }
    private fun createPresenceNotification(i: Int,context: Context) {
        employeeDao = AppDatabase.getDataBase(context).employeeDao
        val employee = employeeDao.getEmployee(i)

        val intent = Intent(context, ProAndEmpActivity::class.java)
        intent.putExtra("IDEMPLOYEE", employee?.idEmployee)
        Log.v("notifemployee", "NotificationBroadcastReceiver: ${employee?.idEmployee}")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        val pendingIntent = PendingIntent.getActivity(context,1,intent, PendingIntent.FLAG_IMMUTABLE)

        val notificationManager = context.getSystemService(Service.NOTIFICATION_SERVICE) as  NotificationManager

        val notification = NotificationCompat
            .Builder(context, "presenceNotif")
            .setSmallIcon(R.drawable.img_logo)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources,
                R.drawable.img_logo
            ))
            .setContentTitle("غیبت کارمند")
            .setContentText("${employee!!.name} ${employee.family} هنوز در شرکت حاظر نشده است!!!")
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(i, notification)

    }
    
}