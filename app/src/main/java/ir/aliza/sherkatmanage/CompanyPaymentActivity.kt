package ir.aliza.sherkatmanage

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ir.aliza.sherkatmanage.DataBase.AppDatabase
import ir.aliza.sherkatmanage.databinding.ActivityCompanyPaymentBinding
import ir.aliza.sherkatmanage.fgmSub.SalaryCompanyExpensesFragment
import ir.aliza.sherkatmanage.fgmSub.SalaryEmployeeSFragment
import java.text.DecimalFormat

class CompanyPaymentActivity : AppCompatActivity() {

    lateinit var binding: ActivityCompanyPaymentBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompanyPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnBck.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
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
    private fun formatCurrency(value: Long?): String {
        val decimalFormat = DecimalFormat("#,###")
        return decimalFormat.format(value) + " تومان"
    }
}