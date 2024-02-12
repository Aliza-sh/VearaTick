package com.vearad.vearatick.ui.activitysub

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.vearad.vearatick.R
import com.vearad.vearatick.databinding.ActivityCompanyPaymentBinding
import com.vearad.vearatick.model.db.AppDatabase
import com.vearad.vearatick.ui.MainActivity
import com.vearad.vearatick.ui.fragmentssub.SalaryCompanyExpensesFragment
import com.vearad.vearatick.ui.fragmentssub.SalaryEmployeeSFragment
import java.text.DecimalFormat

class CompanyPaymentActivity : AppCompatActivity() {

    lateinit var binding: ActivityCompanyPaymentBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBack()
        binding = ActivityCompanyPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnBck.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
        }

        val companyReceiptDao = AppDatabase.getDataBase(applicationContext).companyExpensesDao
        val sumCompanyReceipt = companyReceiptDao.getExpensesSum()
        binding.txtExpenses.text = formatCurrency(sumCompanyReceipt.toLong())

        binding.btnCompanyExpenses.setOnClickListener {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.layout_company_payment, SalaryCompanyExpensesFragment())
                .commit()
        }

        val employeeHarvestDao = AppDatabase.getDataBase(applicationContext).employeeHarvestDao
        val sumEmployeeHarvest = employeeHarvestDao.getHarvestSum()
        binding.txtReceipt.text = formatCurrency(sumEmployeeHarvest.toLong())
        binding.btnCompanyEmployeesSalary.setOnClickListener {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.layout_company_payment, SalaryEmployeeSFragment())
                .commit()
        }
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

    private fun formatCurrency(value: Long?): String {
        val decimalFormat = DecimalFormat("#,###")
        return decimalFormat.format(value) + " تومان"
    }
}