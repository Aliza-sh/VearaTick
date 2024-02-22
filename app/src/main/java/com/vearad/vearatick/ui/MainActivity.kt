package com.vearad.vearatick.ui

import ApiService
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.gson.GsonBuilder
import com.vearad.vearatick.R
import com.vearad.vearatick.adapter.EmployeeAdapter
import com.vearad.vearatick.adapter.EntryExitEmployeeAdapter
import com.vearad.vearatick.adapter.ProjectAdapter
import com.vearad.vearatick.adapter.TaskEmployeeAdapter
import com.vearad.vearatick.databinding.ActivityMainBinding
import com.vearad.vearatick.model.db.AppDatabase
import com.vearad.vearatick.model.db.DayDao
import com.vearad.vearatick.model.db.EmployeeDao
import com.vearad.vearatick.model.db.TaskEmployeeDao
import com.vearad.vearatick.model.db.TimeDao
import com.vearad.vearatick.ui.activitymain.ProAndEmpActivity
import com.vearad.vearatick.ui.activitysub.RegisterStep24Activity
import com.vearad.vearatick.ui.fragmentsmain.CompanyFragment
import com.vearad.vearatick.ui.fragmentsmain.CompanyInformationFragment
import com.vearad.vearatick.workers.AutomaticPresenceWorker
import com.vearad.vearatick.workers.NotificationPresenceWorker
import com.vearad.vearatick.workers.NotificationProjectWorker
import com.vearad.vearatick.workers.NotificationTaskEmployeeWorker
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Calendar
import java.util.concurrent.TimeUnit


lateinit var employeeDao: EmployeeDao
lateinit var dayDao: DayDao
lateinit var timeDao: TimeDao
lateinit var taskEmployeeDao: TaskEmployeeDao
lateinit var projectAdapter: ProjectAdapter
lateinit var taskAdapter: TaskEmployeeAdapter
lateinit var inOutAdapter: EntryExitEmployeeAdapter
lateinit var employeeAdapter: EmployeeAdapter
lateinit var apiService: ApiService

const val SHAREDEXPIRATIONSUBSCRIPTION = "SharedExpirationSubscription"
const val CHEKEXPIRATION = "ChekExpiration"

const val SHAREDLOGINSTEP24 = "SharedLoginStep24"
const val LOGINSTEP24 = "loginStep24"

const val SHAREDMINISITE = "SharedMiniSite"
const val LOGINMINISITE = "loginMiniSite"

const val ACCESSTOKEN = "sharedAccessToken"
const val KEYACCESSTOKEN = "keyAccessToken"

const val EXPIRATIONACCESSTOKEN = "sharedExpirationAccessToken"
const val KEYEXPIRATIONACCESSTOKEN = "keyExpirationAccessToken"

const val USER = "sharedUser"
const val KEYUSER = "keyUser"

const val FIRSTRUN = "FirstRun"
const val KEYUFIRSTRUN = "keyFirstRun"

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var workManager: WorkManager
    lateinit var channels: ArrayList<NotificationChannel>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        workManager = WorkManager.getInstance(this)
        firstRun()
        createApiService()

        if (ContextCompat.checkSelfPermission(applicationContext,  Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS),1)
        }

        employeeDao = AppDatabase.getDataBase(this).employeeDao

        binding.bottomNavigationMain.setOnItemSelectedListener {

            when (it.itemId) {
                R.id.menu_home -> {
                    replaceFragment(CompanyFragment())
                }

                R.id.menu_pro_emp -> {
                    val intent = Intent(this, ProAndEmpActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
                }

                R.id.menu_settings -> {

                    val sharedPreferencesFirstRun = getSharedPreferences(FIRSTRUN, Context.MODE_PRIVATE)
                    val firstRun = sharedPreferencesFirstRun.getBoolean(KEYUFIRSTRUN,false)

                    if (!firstRun){
                        val intent = Intent(this, RegisterStep24Activity::class.java)
                        val firstRun = true
                        intent.putExtra("FIRSTRUN", firstRun)
                        startActivity(intent)
                        android.util.Log.v("firstRun", "MainActivity: ${firstRun}")
                        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
                    }else {

                        replaceFragment(CompanyInformationFragment())

                        val anim = AlphaAnimation(
                            1f, 0f
                        )
                        anim.duration = 1000
                        anim.fillAfter = true
                        anim.repeatCount = 5
                        anim.repeatMode = Animation.REVERSE
                    }
                }
            }
            true
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                workerPresence()
                workerProject()
                workerTaskEmployee()
                workerAutomaticPresence()
                onCreateNotifications()

            } else {
                // کاربر مجوز را رد کرد
            }
        }
    }

    private fun workerPresence() {
        val notificationPresenceWorker = PeriodicWorkRequestBuilder<NotificationPresenceWorker>(
            1, TimeUnit.HOURS,
            15, TimeUnit.MINUTES
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
        val targetHours = arrayOf(22)

        for (hour in targetHours) {
            Log.v("AutomaticPresenceWorker", "$hour: ${calculateTimeDifferenceInMillis(hour,15)}")
            val notificationTaskWorker =
                OneTimeWorkRequest.Builder(AutomaticPresenceWorker::class.java)
                    .build()
            workManager.enqueueUniqueWork(
                "AutomaticPresence",
                ExistingWorkPolicy.REPLACE,
                notificationTaskWorker
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

    private fun createApiService() {

        // ایجاد شیء OkHttpClient با استفاده از HttpLoggingInterceptor برای نمایش لاگ‌ها
        val interceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        val gson = GsonBuilder().setLenient().create()
        //  ایجاد شیء Retrofit با تنظیمات مورد نیاز
        val retrofit = Retrofit.Builder()
            .baseUrl("https://step24.ir/api/") // آدرس پایه سرویس API
            .addConverterFactory(GsonConverterFactory.create(gson)) // تبدیل پاسخ‌ها به شیء با استفاده از Gson
            .client(client) // استفاده از OkHttpClient برای درخواست‌ها
            .build()

        //  ایجاد شیء ApiService با استفاده از شیء Retrofit
        apiService = retrofit.create(ApiService::class.java)

    }

    fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_layout_main, fragment)
            .commit()
    }

    fun firstRun() {
        replaceFragment(CompanyFragment())
        binding.bottomNavigationMain.selectedItemId = R.id.menu_home
    }
}