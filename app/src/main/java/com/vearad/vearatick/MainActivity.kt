package com.vearad.vearatick

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.vearad.vearatick.DataBase.AppDatabase
import com.vearad.vearatick.DataBase.DayDao
import com.vearad.vearatick.DataBase.EmployeeDao
import com.vearad.vearatick.DataBase.TaskEmployeeDao
import com.vearad.vearatick.DataBase.TimeDao
import com.vearad.vearatick.adapter.EmployeeAdapter
import com.vearad.vearatick.adapter.EntryExitEmployeeAdapter
import com.vearad.vearatick.adapter.ProjectNearAdapter
import com.vearad.vearatick.adapter.TaskEmployeeAdapter
import com.vearad.vearatick.databinding.ActivityMainBinding
import com.vearad.vearatick.fgmMain.CompanyFragment
import com.vearad.vearatick.fgmMain.CompanyInformationFragment
import org.json.JSONException
import org.json.JSONObject

lateinit var employeeDao: EmployeeDao
lateinit var dayDao: DayDao
lateinit var timeDao: TimeDao
lateinit var taskEmployeeDao: TaskEmployeeDao
lateinit var projectAdapter: ProjectNearAdapter
lateinit var taskAdapter: TaskEmployeeAdapter
lateinit var inOutAdapter: EntryExitEmployeeAdapter
lateinit var employeeAdapter: EmployeeAdapter

const val SHAREDVEARATICK = "sharedVearatick"
const val CHEKBUY = "chekBuy"

const val SHAREDLOGINSTEP24 = "SharedLoginStep24"
const val LOGINSTEP24 = "loginStep24"

const val SHAREDMINISITE = "SharedMiniSite"
const val LOGINMINISITE = "loginMiniSite"

const val ACCESSTOKEN = "sharedAccessToken"
const val KEYACCESSTOKEN = "keyAccessToken"

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firstRun()

        val sharedPreferences = getSharedPreferences(SHAREDVEARATICK, Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean(CHEKBUY, true).apply()

        val intent = intent
        val data: Uri? = intent.data
        if (data != null) {
            val jsonData = data.getQueryParameter("jsonData")
            if (jsonData != null) {
                // Parse the JSON data and handle it accordingly
                try {
                    val jsonObject = JSONObject(jsonData)
                    val accessToken = jsonObject.getString("access_token")
                    val username = jsonObject.getString("username")
                    Log.v("loginapp", "accessToken: ${accessToken}")
                    Log.v("loginapp", "username: ${username}")
                    val sharedPreferencesLoginStep24 = getSharedPreferences(SHAREDLOGINSTEP24, Context.MODE_PRIVATE)
                    sharedPreferencesLoginStep24.edit().putString(LOGINSTEP24, username).apply()

                    val sharedPreferencesAccessToken = getSharedPreferences(ACCESSTOKEN, Context.MODE_PRIVATE)
                    sharedPreferencesAccessToken.edit().putString(KEYACCESSTOKEN, accessToken).apply()
                    // Handle the retrieved data
                } catch (e: JSONException) {
                    // Handle JSON parsing error
                }
            }
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
            true
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
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