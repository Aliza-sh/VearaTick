package com.vearad.vearatick.adapter

import BottomMarginItemDecoration
import TopMarginItemDecoration
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kizitonwose.calendarview.utils.persian.PersianCalendar
import com.vearad.vearatick.DataBase.AppDatabase
import com.vearad.vearatick.DataBase.FinancialReport
import com.vearad.vearatick.DataBase.FinancialReportDao
import com.vearad.vearatick.R
import com.vearad.vearatick.databinding.ActivityCompanyFinancialReportBinding
import com.vearad.vearatick.databinding.ItemMonthBinding
import koleton.api.hideSkeleton
import koleton.api.loadSkeleton
import java.text.DecimalFormat

class AnnualReportAdapter(
    var dataAnnualReport: ArrayList<FinancialReport>,
    var bindingActivity: ActivityCompanyFinancialReportBinding,
) : RecyclerView.Adapter<AnnualReportAdapter.AnnualReportViewHolder>() {

    lateinit var bindingItemMonth: ItemMonthBinding
    lateinit var financialReportDao: FinancialReportDao

    inner class AnnualReportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(position: Int) {

            financialReportDao = AppDatabase.getDataBase(itemView.context).financialReportDao

            val item = dataAnnualReport[position]

            val calendar = PersianCalendar()
            if (calendar.persianYear == item.year && calendar.persianMonth + 1 == item.month)
                bindingItemMonth.viewMonth.setBackgroundColor(itemView.context.getColor(R.color.firoze))

            when (item.month) {
                1 -> bindingItemMonth.viewMonth.text = "فروردین"
                2 -> bindingItemMonth.viewMonth.text = "اردیبهشت"
                3 -> bindingItemMonth.viewMonth.text = "خرداد"
                4 -> bindingItemMonth.viewMonth.text = "تیر"
                5 -> bindingItemMonth.viewMonth.text = "مرداد"
                6 -> bindingItemMonth.viewMonth.text = "شهریور"
                7 -> bindingItemMonth.viewMonth.text = "مهر"
                8 -> bindingItemMonth.viewMonth.text = "آبان"
                9 -> bindingItemMonth.viewMonth.text = "آذر"
                10 -> bindingItemMonth.viewMonth.text = "دی"
                11 -> bindingItemMonth.viewMonth.text = "بهمن"
                12 -> bindingItemMonth.viewMonth.text = "اسفند"
            }

            bindingItemMonth.txtIncomeMonth.text = formatCurrency(item.income)
            bindingItemMonth.txtExpenseMonth.text = formatCurrency(item.expense)

            val profit = item.income!! - item.expense!!
            val newCompanyFinancialReport = FinancialReport(
                idFinancialReport = item.idFinancialReport,
                year = item.year,
                month = item.month,
                expense = item.expense,
                income = item.income,
                profit = profit
            )
            financialReportDao.update(newCompanyFinancialReport)

            if (profit > 0)
                bindingItemMonth.txtProfitMonth.text = "${formatCurrency(profit)} +"
            else if (profit < 0)
                bindingItemMonth.txtProfitMonth.text = "${formatCurrency(profit)} -"
            else
                bindingItemMonth.txtProfitMonth.text = "0"
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnnualReportViewHolder {
        bindingItemMonth =
            ItemMonthBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AnnualReportViewHolder(bindingItemMonth.root)
    }

    override fun onBindViewHolder(holder: AnnualReportViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return dataAnnualReport.size
    }

    fun updateData(year: Int, financialReportDao: FinancialReportDao) {
        dataAnnualReport.clear()
        val newDataAnnualReport =
            financialReportDao.getAllFinancialYearReportDao(year) as ArrayList<FinancialReport>
        dataAnnualReport.addAll(newDataAnnualReport)
        val newAdapter = AnnualReportAdapter(ArrayList(newDataAnnualReport), bindingActivity)

        bindingActivity.rcv.loadSkeleton()
        Handler(Looper.getMainLooper()).postDelayed({
            bindingActivity.rcv.adapter = newAdapter
            bindingActivity.rcv.layoutManager = LinearLayoutManager(bindingActivity.root.context)
            bindingActivity.rcv.hideSkeleton() // مخفی کردن سکلتون

        }, 500)

        topMargin()
        bottomMargin()

        //Log.v("logAdapter", "dataAnnualReport: "+ dataAnnualReport.toString())
        //Log.v("logAdapter", "onBind ")

    }

    private fun formatCurrency(value: Long?): String {
        val decimalFormat = DecimalFormat("#,###")
        return decimalFormat.format(value)
    }

    private fun topMargin() {
        val backMargin = -80
        val backItemDecoratio = TopMarginItemDecoration(backMargin)
        bindingActivity.rcv.addItemDecoration(backItemDecoratio)

        val topMargin = 80 // اندازه مارجین بالا را از منابع دریافت کنید
        val itemDecoratio = TopMarginItemDecoration(topMargin)
        bindingActivity.rcv.addItemDecoration(itemDecoratio)
    }

    private fun bottomMargin() {
        val backMargin = -120
        val backItemDecoratio = BottomMarginItemDecoration(backMargin)
        bindingActivity.rcv.addItemDecoration(backItemDecoratio)

        val bottomMargin = 120 // اندازه مارجین پایین را از منابع دریافت کنید
        val itemDecoration = BottomMarginItemDecoration(bottomMargin)
        bindingActivity.rcv.addItemDecoration(itemDecoration)
    }
}
