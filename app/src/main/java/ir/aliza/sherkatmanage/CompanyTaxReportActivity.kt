package ir.aliza.sherkatmanage

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import ir.aliza.sherkatmanage.DataBase.AppDatabase
import ir.aliza.sherkatmanage.DataBase.MonthlyTax
import ir.aliza.sherkatmanage.DataBase.MonthlyTaxDao
import ir.aliza.sherkatmanage.adapter.AnnualReportAdapter
import ir.aliza.sherkatmanage.databinding.ActivityCompanyTaxReportBinding

class CompanyTaxReportActivity : AppCompatActivity() {

    lateinit var binding: ActivityCompanyTaxReportBinding
    lateinit var monthlyTaxDao: MonthlyTaxDao
    private var currentYearIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompanyTaxReportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnBck.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val foodList = arrayListOf(
            MonthlyTax(
                year = 1402,
                farvardin = 2222222222222,
                ordibehesht = 8888,
                khordad = 222,
                tir = 0,
                mordad = 222,
                shahriver = 0,
                mehr = 0,
                aban = 0,
                azar = 222,
                day = 0,
                bahman = 0,
                esfand = 0,
            ),
            MonthlyTax(
                year = 1403,
                farvardin = 0,
                ordibehesht = 0,
                khordad = 3333,
                tir = 0,
                mordad = 3333,
                shahriver = 0,
                mehr = 20000,
                aban = 0,
                azar = 33,
                day = 0,
                bahman = 0,
                esfand = 0,
            ),
            MonthlyTax(
                year = 1404,
                farvardin = 0,
                ordibehesht = 33333,
                khordad = 4444444,
                tir = 0,
                mordad = 4444444,
                shahriver = 0,
                mehr = 0,
                aban = 0,
                azar = 44,
                day = 0,
                bahman = 0,
                esfand = 0,
            ),
            MonthlyTax(
                year = 1405,
                farvardin = 0,
                ordibehesht = 0,
                khordad = 5555,
                tir = 0,
                mordad = 55,
                shahriver = 0,
                mehr = 0,
                aban = 0,
                azar = 555555555555,
                day = 0,
                bahman = 0,
                esfand = 0,
            ),
        )

        monthlyTaxDao = AppDatabase.getDataBase(applicationContext).monthlyTaxDao
        val monthlyTaxData = monthlyTaxDao.getAllMonthlyTax()
        val adapter = AnnualReportAdapter(ArrayList(foodList))
        binding.viewPager.adapter = adapter

        binding.txtYear.text = foodList[currentYearIndex].year.toString()

        binding.btnPrevious.setOnClickListener {
            if (currentYearIndex > 0) {
                currentYearIndex--
                binding.viewPager.currentItem = currentYearIndex
                binding.txtYear.text = foodList[currentYearIndex].year.toString()
            }
        }

        binding.btnNext.setOnClickListener {
            if (currentYearIndex < foodList.size - 1) {
                currentYearIndex++
                binding.viewPager.currentItem = currentYearIndex
                binding.txtYear.text = foodList[currentYearIndex].year.toString()
            }

        }
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentYearIndex = position
                binding.txtYear.text = foodList[currentYearIndex].year.toString()
            }
        })
    }
}
