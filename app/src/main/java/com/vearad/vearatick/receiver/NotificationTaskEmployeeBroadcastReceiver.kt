package com.vearad.vearatick.receiver

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import androidx.core.app.NotificationCompat
import com.jakewharton.threetenabp.AndroidThreeTen
import com.kizitonwose.calendarview.utils.persian.toPersianCalendar
import com.vearad.vearatick.R
import com.vearad.vearatick.model.db.AppDatabase
import com.vearad.vearatick.model.db.Employee
import com.vearad.vearatick.model.db.EmployeeDao
import com.vearad.vearatick.model.db.TaskEmployeeDao
import com.vearad.vearatick.ui.activitymain.ProAndEmpActivity
import org.joda.time.DateTime
import org.joda.time.Days
import org.threeten.bp.LocalDate
import java.util.Calendar

class NotificationTaskEmployeeBroadcastReceiver : BroadcastReceiver() {

    lateinit var employeeDao: EmployeeDao
    lateinit var taskEmployeeDao: TaskEmployeeDao
    override fun onReceive(context: Context, intent: Intent) {
        AndroidThreeTen.init(context)
        taskEmployeeNotification(context)
        //alarmTaskEmployee(context,intent)
    }
    private fun alarmTaskEmployee(context: Context, intent: Intent) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val pendingIntentTaskEmployee =
            PendingIntent.getBroadcast(context, 1003, intent, PendingIntent.FLAG_IMMUTABLE)

        val targetHours = arrayOf(10,15)
        for (hour in targetHours) {
            Log.v("alarmTaskEmployee", "alarmTaskEmployee: ${calculateTimeDifferenceInMillis(hour,15)}")
            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calculateTimeDifferenceInMillis(hour,15),
                AlarmManager.INTERVAL_DAY,
                pendingIntentTaskEmployee
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
    private fun taskEmployeeNotification(context: Context) {
        Log.v("alarmTaskEmployee", "Here: go to BroadcastReceiver Task")
        employeeDao = AppDatabase.getDataBase(context).employeeDao
        taskEmployeeDao = AppDatabase.getDataBase(context).taskDao
        val today = LocalDate.now().toPersianCalendar()
        val employees = employeeDao.getAllEmployee()

        for (employee in employees) {
            val taskEmployee = taskEmployeeDao.getOnClickTaskEmployee(employee.idEmployee!!)

            val startDate =
                DateTime(today.persianYear, today.persianMonth+1, today.persianDay, 0, 0, 0)
            val endDate = DateTime(
                taskEmployee!!.yearDeadline,
                taskEmployee.monthDeadline+1,
                taskEmployee.dayDeadline,
                0,
                0,
                0
            )
            var daysBetween = Days.daysBetween(startDate, endDate).days

            when(daysBetween){
                3-> createProjectNotification(employee,context,3)
                2-> createProjectNotification(employee,context,2)
                1-> createProjectTomorrowNotification(employee,context)
                0-> createProjectTodayNotification(employee,context)

            }
        }
    }
    private fun createProjectNotification(employee: Employee, context: Context, Deadline:Int) {

        val intent = Intent(context, ProAndEmpActivity::class.java)
        intent.putExtra("TASKEMPLOYEE", employee.idEmployee)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        val pendingIntent = PendingIntent.getActivity(context,1020,intent, PendingIntent.FLAG_IMMUTABLE)

        val notificationManager = context.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat
            .Builder(context, "taskEmployeeNotif")
            .setSmallIcon(R.drawable.img_logo)
            .setLargeIcon(
                BitmapFactory.decodeResource(context.resources,
                    R.drawable.img_logo
                ))
            .setContentTitle("سرسید تسک کارمند")
            .setContentText("مهلت تسک ${employee!!.name} ${employee.family} ${Deadline} روز است. ")
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(employee.idEmployee!!, notification)

    }
    private fun createProjectTomorrowNotification(employee: Employee, context: Context) {
        val intent = Intent(context, ProAndEmpActivity::class.java)
        intent.putExtra("TASKEMPLOYEE", employee.idEmployee)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        val pendingIntent = PendingIntent.getActivity(context,1021,intent, PendingIntent.FLAG_IMMUTABLE)

        val notificationManager = context.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat
            .Builder(context, "taskEmployeeNotif")
            .setSmallIcon(R.drawable.img_logo)
            .setLargeIcon(
                BitmapFactory.decodeResource(context.resources,
                    R.drawable.img_logo
                ))
            .setContentTitle("سرسید تسک کارمند")
            .setContentText("مهلت تسک ${employee!!.name} ${employee.family} فردا به سر میرسد. ")
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(employee.idEmployee!!, notification)
    }
    private fun createProjectTodayNotification(employee: Employee, context: Context) {
        val intent = Intent(context, ProAndEmpActivity::class.java)
        intent.putExtra("TASKEMPLOYEE", employee.idEmployee)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        val pendingIntent = PendingIntent.getActivity(context,1022,intent, PendingIntent.FLAG_IMMUTABLE)

        val notificationManager = context.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat
            .Builder(context, "taskEmployeeNotif")
            .setSmallIcon(R.drawable.img_logo)
            .setLargeIcon(
                BitmapFactory.decodeResource(context.resources,
                    R.drawable.img_logo
                ))
            .setContentTitle("سرسید تسک کارمند")
            .setContentText("مهلت تسک ${employee!!.name} ${employee.family} امروز است. ")
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(employee.idEmployee!!, notification)
    }

}