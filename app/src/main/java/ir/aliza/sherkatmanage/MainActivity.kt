package ir.aliza.sherkatmanage

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import ir.aliza.sherkatmanage.DataBase.EmployeeDao
import ir.aliza.sherkatmanage.DataBase.ProjectDao
import ir.aliza.sherkatmanage.adapter.EmployeeAdapter
import ir.aliza.sherkatmanage.adapter.ProjectNearAdapter
import ir.aliza.sherkatmanage.databinding.ActivityMainBinding
import ir.aliza.sherkatmanage.fgmMain.AffairsFragment
import ir.aliza.sherkatmanage.fgmMain.ChatFragment
import ir.aliza.sherkatmanage.fgmMain.CompanyFragment
import ir.aliza.sherkatmanage.fgmMain.ProAndEmpFragment
import ir.aliza.sherkatmanage.fgmSub.PersonFragment

lateinit var employeeDao: EmployeeDao
lateinit var projectDao: ProjectDao
lateinit var projectAdapter: ProjectNearAdapter
lateinit var employeeAdapter: EmployeeAdapter

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firstRun()

        binding.bottomNavigation.setOnItemSelectedListener {

            when (it.itemId) {
                R.id.menu_home -> {
                    replaceFragment(CompanyFragment())
                }

                R.id.menu_pro_emp -> {
                    replaceFragment(ProAndEmpFragment())
                }

                R.id.menu_recruitment -> {
                    replaceFragment(AffairsFragment())
                }

                R.id.menu_chat -> {
                    replaceFragment(ChatFragment())
                }

                R.id.menu_person -> {
                    replaceFragment(PersonFragment())
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
        binding.bottomNavigation.selectedItemId = R.id.menu_home
    }
}