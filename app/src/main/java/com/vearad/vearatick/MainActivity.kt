package com.vearad.vearatick

import ApiService
import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.SystemClock
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.gson.GsonBuilder
import com.vearad.vearatick.DataBase.AppDatabase
import com.vearad.vearatick.DataBase.DayDao
import com.vearad.vearatick.DataBase.EmployeeDao
import com.vearad.vearatick.DataBase.TaskEmployeeDao
import com.vearad.vearatick.DataBase.TimeDao
import com.vearad.vearatick.adapter.EmployeeAdapter
import com.vearad.vearatick.adapter.EntryExitEmployeeAdapter
import com.vearad.vearatick.adapter.ProjectAdapter
import com.vearad.vearatick.adapter.TaskEmployeeAdapter
import com.vearad.vearatick.databinding.ActivityMainBinding
import com.vearad.vearatick.fgmMain.CompanyFragment
import com.vearad.vearatick.fgmMain.CompanyInformationFragment
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
                // کاربر مجوز را داده است
                // ذخیره وضعیت مجوز در حافظه
                val preferences = getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
                preferences.edit().putBoolean("has_post_notifications_permission", true).apply()

                val intent = Intent(this, NotificationBroadcastReceiver::class.java)
                val pendingIntent = PendingIntent.getBroadcast(this, 1001, intent, PendingIntent.FLAG_IMMUTABLE)
                val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

                alarmManager.setInexactRepeating(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime(),
                    AlarmManager.INTERVAL_HOUR,
                    pendingIntent
                )
            } else {
                // کاربر مجوز را رد کرد
            }
        }
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