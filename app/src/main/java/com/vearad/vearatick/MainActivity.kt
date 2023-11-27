package com.vearad.vearatick

import android.content.Context
import android.content.Intent
import android.os.Bundle
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

lateinit var employeeDao: EmployeeDao
lateinit var dayDao: DayDao
lateinit var timeDao: TimeDao
lateinit var taskEmployeeDao: TaskEmployeeDao
lateinit var projectAdapter: ProjectNearAdapter
lateinit var taskAdapter: TaskEmployeeAdapter
lateinit var inOutAdapter: EntryExitEmployeeAdapter
lateinit var employeeAdapter: EmployeeAdapter
const val CHEKBUY = "chekBuy"
const val SHAREDVEARATICK = "sharedVearatick"

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val sharedPreferences =
            getSharedPreferences(SHAREDVEARATICK, Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean(CHEKBUY, true).apply()

        employeeDao = AppDatabase.getDataBase(this).employeeDao

        firstRun()

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