package com.vearad.vearatick

import BottomMarginItemDecoration
import TopMarginItemDecoration
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.xdev.arch.persiancalendar.datepicker.calendar.PersianCalendar
import com.vearad.vearatick.DataBase.AppDatabase
import com.vearad.vearatick.DataBase.FinancialReport
import com.vearad.vearatick.DataBase.FinancialReportDao
import com.vearad.vearatick.adapter.AnnualReportAdapter
import com.vearad.vearatick.databinding.ActivityCompanyFinancialReportBinding
import koleton.api.hideSkeleton
import koleton.api.loadSkeleton

class CompanyFinancialReportActivity : AppCompatActivity() {

    lateinit var binding: ActivityCompanyFinancialReportBinding
    lateinit var financialReportDao: FinancialReportDao
    lateinit var financialReportData: List<FinancialReport>
    private var currentYearIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompanyFinancialReportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnBck.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        financialReportDao = AppDatabase.getDataBase(applicationContext).financialReportDao
        //Log.v("logActivity", " onCreate")
//        val sharedPreferences = getSharedPreferences("Aliza", Context.MODE_PRIVATE)
//        if (sharedPreferences.getBoolean("firstRun", true)) {
//            firstRun()
//            sharedPreferences.edit().putBoolean("firstRun", false).apply()
//        }
        if (financialReportDao.getAllListFinancialReportDao().isEmpty()) {
                        firstRun()
        }
        //Log.v("logActivity", currentYearIndex.toString())
        val listYear = financialReportDao.getDistinctData()
        financialReportData =
            financialReportDao.getAllFinancialYearReportDao(listYear[currentYearIndex])
        //Log.v("logActivity","firstRun: "+ listYear[currentYearIndex].toString())
        //Log.v("logActivity", financialReportData.toString())

        val adapter = AnnualReportAdapter(
            ArrayList(financialReportData),
            binding
        )

        binding.rcv.loadSkeleton()
        Handler(Looper.getMainLooper()).postDelayed({
            binding.rcv.adapter = adapter
            binding.rcv.layoutManager = LinearLayoutManager(applicationContext)
            binding.rcv.hideSkeleton() // مخفی کردن سکلتون

        }, 500)

        topMargin()
        bottomMargin()

        if (financialReportData.isNotEmpty())
            binding.txtYear.text = listYear[currentYearIndex].toString()
        else
            binding.txtYear.text = PersianCalendar().year.toString()

        binding.btnPrevious.setOnClickListener {
            if (currentYearIndex > 0) {
                currentYearIndex--
                binding.txtYear.text = listYear[currentYearIndex].toString()
                adapter.updateData(listYear[currentYearIndex], financialReportDao)
                //Log.v("logActivity","btnPrevious: "+ currentYearIndex.toString())
                //Log.v("logActivity","btnPrevious: "+ listYear[currentYearIndex].toString())

            }
        }

        binding.btnNext.setOnClickListener {
            if (currentYearIndex < listYear.size - 1) {
                currentYearIndex++
                binding.txtYear.text = listYear[currentYearIndex].toString()
                adapter.updateData(listYear[currentYearIndex], financialReportDao)
                //Log.v("logActivity","btnNext: "+ currentYearIndex.toString())
                //Log.v("logActivity", "btnNext: "+listYear[currentYearIndex].toString())

            }

        }

    }

    private fun firstRun() {

            val newCompanyFinancialReport = FinancialReport(
                year = PersianCalendar().year,
                month = PersianCalendar().month + 1,
            )
            financialReportDao.insert(newCompanyFinancialReport)

    }

    private fun topMargin() {
        val topMargin = 80 // اندازه مارجین بالا را از منابع دریافت کنید
        val itemDecoratio = TopMarginItemDecoration(topMargin)
        binding.rcv.addItemDecoration(itemDecoratio)
    }

    private fun bottomMargin() {
        val bottomMargin = 120 // اندازه مارجین پایین را از منابع دریافت کنید
        val itemDecoration = BottomMarginItemDecoration(bottomMargin)
        binding.rcv.addItemDecoration(itemDecoration)
    }
}
