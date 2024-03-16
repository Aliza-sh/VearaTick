package com.vearad.vearatick.receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.jakewharton.threetenabp.AndroidThreeTen
import com.kizitonwose.calendarview.utils.persian.PersianCalendar
import com.vearad.vearatick.model.db.AppDatabase
import com.vearad.vearatick.model.db.DayDao
import com.vearad.vearatick.model.db.EmployeeDao
import com.vearad.vearatick.model.db.Time
import com.vearad.vearatick.model.db.TimeDao
import java.util.Calendar

class AutomaticPresenceBroadcastReceiver : BroadcastReceiver() {

    lateinit var dayDao: DayDao
    lateinit var timeDao: TimeDao
    lateinit var employeeDao: EmployeeDao
    override fun onReceive(context: Context, intent: Intent) {
        presenceAuto(context)
        abortBroadcast()
        //alarmAutomaticPresence(context,intent)
    }
    private fun alarmAutomaticPresence(context: Context, intent: Intent) {
        AndroidThreeTen.init(context)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val pendingIntentAutomaticPresence =
            PendingIntent.getBroadcast(context, 1004, intent, PendingIntent.FLAG_IMMUTABLE)

        Log.v("alarmAutomaticPresence", "alarmAutomaticPresence: ${calculateTimeDifferenceInMillis(22,15)}")
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calculateTimeDifferenceInMillis(22,15),
            AlarmManager.INTERVAL_DAY,
            pendingIntentAutomaticPresence
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
    private fun presenceAuto(context: Context) {
        Log.v("alarmAutomaticPresence", "Here: go to BroadcastReceiver presenceAuto")
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

}