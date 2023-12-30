package com.vearad.vearatick

import android.content.Intent
import android.os.Bundle
import android.view.View.VISIBLE
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.vearad.vearatick.DataBase.AppDatabase
import com.vearad.vearatick.DataBase.EfficiencyDao
import com.vearad.vearatick.DataBase.Employee
import com.vearad.vearatick.DataBase.EmployeeDao
import com.vearad.vearatick.DataBase.EmployeeHarvestDao
import com.vearad.vearatick.DataBase.EmployeeInvestmentDao
import com.vearad.vearatick.adapter.ShareholdersInvestmentAdapter
import com.vearad.vearatick.databinding.ActivityShareholdersInvestmentBinding
import com.vearad.vearatick.fgmSub.SalaryShareholdersInvestmentFragment

class ShareholdersInvestmentActivity : AppCompatActivity(), ShareholdersInvestmentAdapter.ShareholdersInvestmentEvents {

    lateinit var binding: ActivityShareholdersInvestmentBinding
    lateinit var employeeDao: EmployeeDao
    lateinit var efficiencyEmployeeDao: EfficiencyDao
    lateinit var employeeInvestmentDao: EmployeeInvestmentDao
    lateinit var employeeHarvestDao: EmployeeHarvestDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBack()
        binding = ActivityShareholdersInvestmentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnBck.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)

        }
        employeeDao = AppDatabase.getDataBase(applicationContext).employeeDao
        employeeInvestmentDao = AppDatabase.getDataBase(applicationContext).employeeInvestmentDao
        employeeHarvestDao = AppDatabase.getDataBase(applicationContext).employeeHarvestDao
        val shareholdersData = employeeDao.rankEmployee("سهام دار")

        if (shareholdersData.isEmpty()) {
            binding.emptyList.visibility = VISIBLE
        }

        binding.btnGoToEmplooyes.setOnClickListener {
            val intent = Intent(applicationContext, ProAndEmpActivity::class.java)
            intent.putExtra("itemClicked", 2)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
        }

        val shareholdersInvestmentAdapter = ShareholdersInvestmentAdapter(
            ArrayList(shareholdersData),
            employeeHarvestDao,
            employeeInvestmentDao,
            this
        )
        binding.rcvShareholder.adapter = shareholdersInvestmentAdapter
        binding.rcvShareholder.layoutManager = LinearLayoutManager(applicationContext)
    }
    fun onBack() {
        onBackPressedDispatcher.addCallback(
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)

                }
            })
    }

    override fun onPaymentShareholdersClicked(employee: Employee, position: Int) {}

    override fun onPaymentShareholdersLongClicked(employee: Employee, position: Int) {}

    override fun onBtnInvestmentClick(
        employee: Employee,
        employeeInvestmentDao: EmployeeInvestmentDao,
        position: Int
    ) {
        val transaction =
            supportFragmentManager.beginTransaction()
        transaction.replace(
            R.id.frame_layout_shareholder,
            SalaryShareholdersInvestmentFragment(
                employee,
                employeeInvestmentDao
            )
        )
            .addToBackStack(null)
            .commit()
    }
}