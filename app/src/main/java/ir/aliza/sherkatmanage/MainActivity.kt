package ir.aliza.sherkatmanage

import android.content.Intent
import android.os.Bundle
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import ir.aliza.sherkatmanage.DataBase.AppDatabase
import ir.aliza.sherkatmanage.DataBase.DayDao
import ir.aliza.sherkatmanage.DataBase.EmployeeDao
import ir.aliza.sherkatmanage.DataBase.TaskEmployeeDao
import ir.aliza.sherkatmanage.DataBase.TimeDao
import ir.aliza.sherkatmanage.adapter.EmployeeAdapter
import ir.aliza.sherkatmanage.adapter.InOutAdapter
import ir.aliza.sherkatmanage.adapter.ProjectNearAdapter
import ir.aliza.sherkatmanage.adapter.TaskEmployeeAdapter
import ir.aliza.sherkatmanage.databinding.ActivityMainBinding
import ir.aliza.sherkatmanage.fgmMain.CompanyFragment
import ir.aliza.sherkatmanage.fgmMain.PersonFragment

lateinit var employeeDao: EmployeeDao
lateinit var dayDao: DayDao
lateinit var timeDao: TimeDao
lateinit var taskEmployeeDao: TaskEmployeeDao
lateinit var projectAdapter: ProjectNearAdapter
lateinit var taskAdapter: TaskEmployeeAdapter
lateinit var inOutAdapter: InOutAdapter
lateinit var employeeAdapter: EmployeeAdapter

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
                }

                R.id.menu_settings -> {
                    replaceFragment(PersonFragment())

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

    fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_layout_main, fragment)
            .addToBackStack(null)
            .commit()
    }

    fun firstRun() {
        replaceFragment(CompanyFragment())
        binding.bottomNavigationMain.selectedItemId = R.id.menu_home
    }
}