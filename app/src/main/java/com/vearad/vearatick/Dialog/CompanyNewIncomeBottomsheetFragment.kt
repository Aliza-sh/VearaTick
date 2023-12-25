package com.vearad.vearatick.Dialog

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.vearad.vearatick.BottomSheetCallback
import com.vearad.vearatick.CompanyReceiptActivity
import com.vearad.vearatick.DataBase.AppDatabase
import com.vearad.vearatick.DataBase.CompanyReceipt
import com.vearad.vearatick.DataBase.CompanyReceiptDao
import com.vearad.vearatick.DataBase.FinancialReport
import com.vearad.vearatick.R
import com.vearad.vearatick.adapter.CompanyReceiptAdapter
import com.vearad.vearatick.databinding.BottomsheetfragmentCompanyNewReceiptBinding
import com.xdev.arch.persiancalendar.datepicker.CalendarConstraints
import com.xdev.arch.persiancalendar.datepicker.DateValidatorPointForward
import com.xdev.arch.persiancalendar.datepicker.MaterialDatePicker
import com.xdev.arch.persiancalendar.datepicker.MaterialPickerOnPositiveButtonClickListener
import com.xdev.arch.persiancalendar.datepicker.Month
import com.xdev.arch.persiancalendar.datepicker.calendar.PersianCalendar
import java.text.DecimalFormat


class CompanyNewIncomeBottomsheetFragment(
    val companyReceiptDao: CompanyReceiptDao,
    val companyReceiptAdapter: CompanyReceiptAdapter,
) : BottomSheetDialogFragment() {

    lateinit var binding: BottomsheetfragmentCompanyNewReceiptBinding
    var valueCalendar : PersianCalendar? = null
    private var isUpdating = false
    private var callback: BottomSheetCallback? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomsheetfragmentCompanyNewReceiptBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        valueCalendar = PersianCalendar()
        binding.txtDateReceipt.text = "${valueCalendar!!.year}/${valueCalendar!!.month + 1}/${valueCalendar!!.day - 1}"
        binding.btnCalendar.setOnClickListener {
            onCreateCalendar()
        }
        var formattedValue = "0"
        val decimalFormat = DecimalFormat("#,###")
        binding.edtReceipt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // قبل از تغییرات متنی
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // در هنگام تغییرات متنی
            }

            override fun afterTextChanged(s: Editable?) {
                // پس از تغییرات متنی
                if (isUpdating) {
                    return
                }

                isUpdating = true

                val input = s.toString().replace(",", "") // حذف ویرگول‌ها از ورودی
                val value = input.toLongOrNull()

                if (value != null) {
                    formattedValue = decimalFormat.format(value)
                    binding.txtReceipt.text = formatCurrency(value)
                }
                isUpdating = false
            }
        })
        binding.sheetBtnDone.setOnClickListener {
            addNewIncome(formattedValue)
        }
    }
    fun setCallback(callback: BottomSheetCallback) {
        this.callback = callback
    }
    fun onCompanyNewReceipt() {
        callback?.onConfirmButtonClicked()
    }

    fun onCreateCalendar() {

        val calendar = PersianCalendar()
        calendar.setPersian(1400, Month.FARVARDIN, 1)
        val start = calendar.timeInMillis
        calendar.setPersian(1500, Month.ESFAND, 29)
        val end = calendar.timeInMillis
        val openAt = PersianCalendar.getToday().timeInMillis
        val constraints = CalendarConstraints.Builder()
            .setStart(start)
            .setEnd(end)
            .setOpenAt(openAt)
            .setValidator(DateValidatorPointForward.from(start)).build()
        val datePicker = MaterialDatePicker.Builder
            .datePicker()
            .setTitleText("تاریخ را انتخاب کنید.")
            .setTheme(R.style.AppTheme_PersianCalendar)
            .setCalendarConstraints(constraints).build()
        datePicker.show((activity as CompanyReceiptActivity).supportFragmentManager, "aTag")
        datePicker.isCancelable

        datePicker.addOnPositiveButtonClickListener(
            object : MaterialPickerOnPositiveButtonClickListener<Long?> {
                @SuppressLint("SetTextI18n")
                override fun onPositiveButtonClick(selection: Long?) {
                    val date = PersianCalendar(selection!!)
                    valueCalendar = date
                    binding.txtDateReceipt.text = "${date.year}/${date.month + 1}/${date.day}"
                }
            }
        )

    }
    private fun formatCurrency(value: Long?): String {
        val decimalFormat = DecimalFormat("#,###")
        return decimalFormat.format(value) + " تومان"
    }
    private fun addNewIncome(formattedValue: String) {
        if (
            binding.edtReceipt.length() > 0 &&
            binding.txtDateReceipt.length() > 0 &&
            binding.dialogEdtTozih.length() > 0
        ) {
            val txtReceipt = binding.edtReceipt.text.toString()
            val txtDescription = binding.dialogEdtTozih.text.toString()


            val newReceipt = CompanyReceipt(
                companyReceipt = txtReceipt.toLong(),
                companyReceiptDescription = txtDescription,
                companyReceiptDate = "${valueCalendar?.year}/${valueCalendar?.month!! + 1}/${valueCalendar?.day}",
                monthCompanyReceipt = valueCalendar?.month!! + 1,
                yearCompanyReceipt = valueCalendar?.year!!
            )
            companyReceiptDao.insert(newReceipt)
            companyReceiptAdapter.addCompanyReceipt(newReceipt)
            onCompanyNewReceipt()
            onCompanyFinancialReport(txtReceipt)
            dismiss()
        } else {
            Toast.makeText(context, "لطفا همه مقادیر را وارد کنید", Toast.LENGTH_SHORT).show()
        }
    }

    lateinit var newCompanyFinancialReport: FinancialReport
    private fun onCompanyFinancialReport(income: String) {

        val financialReportDao = AppDatabase.getDataBase(binding.root.context).financialReportDao
        val financialReportYearAndMonth = financialReportDao.getFinancialReportYearAndMonthDao(valueCalendar!!.year , valueCalendar!!.month + 1)

        if (financialReportYearAndMonth != null) {
            val agoIncome = financialReportYearAndMonth.income
            val newIncome = agoIncome!!.toLong() + income.toLong()
            newCompanyFinancialReport = FinancialReport(
                idFinancialReport = financialReportYearAndMonth.idFinancialReport,
                year = financialReportYearAndMonth.year,
                month = financialReportYearAndMonth.month,
                expense = financialReportYearAndMonth.expense,
                income = newIncome,
                profit = financialReportYearAndMonth.profit
            )
            financialReportDao.update(newCompanyFinancialReport)
        } else {
            newCompanyFinancialReport = FinancialReport(
                year = valueCalendar!!.year,
                month = valueCalendar!!.month +1,
                income = income.toLong(),
            )
            financialReportDao.insert(newCompanyFinancialReport)
        }
    }

}