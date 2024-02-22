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
import com.vearad.vearatick.model.db.EmployeeDao
import com.vearad.vearatick.model.db.Time
import com.vearad.vearatick.model.db.TimeDao
import com.vearad.vearatick.ui.activitymain.ProAndEmpActivity

class AutomaticPresenceWorker(val context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {
    lateinit var dayDao: DayDao
    lateinit var timeDao: TimeDao
    lateinit var employeeDao: EmployeeDao
    override fun doWork(): Result {
        Log.v("PresenceWorker", "Here: Presence")

        return try {
            presenceAuto(context)
            Result.success()
        } catch (ex: Exception) {
            Result.failure()
        }

    }

    private fun presenceAuto(context: Context) {
        Log.v("AutoPresenceWorker", "Here: go to work presenceAuto")
        employeeDao = AppDatabase.getDataBase(context).employeeDao
        dayDao = AppDatabase.getDataBase(context).dayDao
        timeDao = AppDatabase.getDataBase(context).timeDao
        val today = PersianCalendar()
        val employees = employeeDao.getAllEmployee()

        for (employee in employees) {
            Log.v("AutoPresenceWorker", "employee: $employee")

            val thisDay = timeDao.getAllArrivalDay(
                employee.idEmployee!!,
                today.persianYear.toString(),
                today.persianMonthName,
                today.persianDay.toString()
            )
            Log.v("AutoPresenceWorker", "thisDay: $thisDay")

            if (thisDay == null) {
                val nameDay = dayDao.getAllNameDay(
                    employee.idEmployee!!,
                    today.persianYear.toString(),
                    today.persianMonthName,
                    today.persianWeekDayName
                )
                Log.v("AutoPresenceWorker", "nameDay: $nameDay")
                if (nameDay?.nameday == today.persianWeekDayName) {
                    Log.v("AutoPresenceWorker", "nameDay: ${nameDay?.nameday}")
                    Log.v("AutoPresenceWorker", "persianWeekDayName: ${today.persianWeekDayName}")

                    val differenceTime = nameDay?.exit!!.toInt() - nameDay.entry!!.toInt()
                    val newTime = Time(
                        idEmployee = employee.idEmployee,
                        year = today.persianYear.toString(),
                        month = today.persianMonthName,
                        day = today.persianDay.toString(),
                        arrival = true,
                        entry = nameDay!!.entry!!.toInt(),
                        entryAll = "${nameDay!!.entry!!.toInt()}:00",
                        exit = nameDay!!.exit!!.toInt(),
                        exitAll = "${nameDay!!.exit!!.toInt()}:00",
                        differenceTime = differenceTime,
                        mustTime = differenceTime
                    )
                    timeDao.insert(newTime)
                }
            }
        }
    }

    private fun createPresenceNotification(i: Int, context: Context) {
        employeeDao = AppDatabase.getDataBase(context).employeeDao
        val employee = employeeDao.getEmployee(i)

        val intent = Intent(context, ProAndEmpActivity::class.java)
        intent.putExtra("IDEMPLOYEE", employee?.idEmployee)
        Log.v("PresenceWorker", "NotificationBroadcastReceiver: ${employee?.idEmployee}")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        val pendingIntent =
            PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_IMMUTABLE)

        val notificationManager =
            context.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat
            .Builder(context, "presenceNotif")
            .setSmallIcon(R.drawable.img_logo)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    context.resources,
                    R.drawable.img_logo
                )
            )
            .setContentTitle("غیبت کارمند")
            .setContentText("${employee!!.name} ${employee.family} هنوز در شرکت حاظر نشده است!!!")
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(i + 10, notification)

    }

}