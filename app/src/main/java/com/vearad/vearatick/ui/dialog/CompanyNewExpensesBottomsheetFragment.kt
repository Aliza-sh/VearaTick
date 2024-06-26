package com.vearad.vearatick.ui.dialog

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.vearad.vearatick.utils.BottomSheetCallback
import com.vearad.vearatick.ui.activitysub.CompanyPaymentActivity
import com.vearad.vearatick.model.db.AppDatabase
import com.vearad.vearatick.model.db.CompanyExpenses
import com.vearad.vearatick.model.db.CompanyExpensesDao
import com.vearad.vearatick.model.db.FinancialReport
import com.vearad.vearatick.R
import com.vearad.vearatick.adapter.CompanyExpensesAdapter
import com.vearad.vearatick.databinding.BottomsheetfragmentCompanyNewExpensesBinding
import com.xdev.arch.persiancalendar.datepicker.CalendarConstraints
import com.xdev.arch.persiancalendar.datepicker.DateValidatorPointForward
import com.xdev.arch.persiancalendar.datepicker.MaterialDatePicker
import com.xdev.arch.persiancalendar.datepicker.MaterialPickerOnPositiveButtonClickListener
import com.xdev.arch.persiancalendar.datepicker.Month
import com.xdev.arch.persiancalendar.datepicker.calendar.PersianCalendar
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale


class CompanyNewExpensesBottomsheetFragment(
    val companyExpensesDao: CompanyExpensesDao,
    val companyExpensesAdapter: CompanyExpensesAdapter
) : BottomSheetDialogFragment() {

    lateinit var binding: BottomsheetfragmentCompanyNewExpensesBinding
    var valueCalendar: PersianCalendar? = null
    private var isUpdating = false
    private var callback: BottomSheetCallback? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            BottomsheetfragmentCompanyNewExpensesBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        valueCalendar = PersianCalendar()
        binding.txtDateReceipt.text = "${valueCalendar!!.year}/${valueCalendar!!.month + 1}/${valueCalendar!!.day}"
        binding.btnCalendar.setOnClickListener {
            onCreateCalendar()
        }
        var formattedValue = "0"
        val decimalFormatSymbols = DecimalFormatSymbols(Locale("fa", "IR"))
        decimalFormatSymbols.groupingSeparator = ','
        val decimalFormat = DecimalFormat("#,###",decimalFormatSymbols)
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
                    binding.edtReceipt.setText(formattedValue)
                    binding.edtReceipt.setSelection(formattedValue.length)
                    binding.txtReceipt.text = formatCurrency(value)
                }
                isUpdating = false
            }
        })
        binding.sheetBtnDone.setOnClickListener {
            addNewReceipt(formattedValue)
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
        datePicker.show((activity as CompanyPaymentActivity).supportFragmentManager, "aTag")
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
        val decimalFormatSymbols = DecimalFormatSymbols(Locale("fa", "IR"))
        decimalFormatSymbols.groupingSeparator = ','
        val decimalFormat = DecimalFormat("#,###",decimalFormatSymbols)
        return decimalFormat.format(value) + " تومان"
    }

    private fun addNewReceipt(formattedValue: String) {
        if (
            binding.edtReceipt.length() > 0 &&
            binding.txtDateReceipt.length() > 0 &&
            binding.dialogEdtTozih.length() > 0
        ) {
            var txtReceipt = binding.edtReceipt.text.toString()
            val txtDescription = binding.dialogEdtTozih.text.toString()

            txtReceipt = txtReceipt!!.replace(",", "")

            val newReceipt = CompanyExpenses(
                companyExpenses = txtReceipt.toLong(),
                companyExpensesDescription = txtDescription,
                companyExpensesDate = "${valueCalendar?.year}/${valueCalendar?.month!! + 1}/${valueCalendar?.day}",
                yearCompanyExpenses = valueCalendar?.year!!,
                monthCompanyExpenses = valueCalendar?.month!! + 1
            )
            companyExpensesDao.insert(newReceipt)
            companyExpensesAdapter.addCompanyExpenses(newReceipt)
            onCompanyNewReceipt()
            onCompanyFinancialReport(txtReceipt)
            dismiss()
        } else {
            Toast.makeText(context, "لطفا همه مقادیر را وارد کنید", Toast.LENGTH_SHORT).show()
        }
    }

    lateinit var newCompanyFinancialReport: FinancialReport
    private fun onCompanyFinancialReport(expense: String) {

        val financialReportDao = AppDatabase.getDataBase(binding.root.context).financialReportDao
        val financialReportYearAndMonth = financialReportDao.getFinancialReportYearAndMonthDao(valueCalendar!!.year , valueCalendar!!.month + 1)

        if (financialReportYearAndMonth != null) {
            val agoExpense = financialReportYearAndMonth.expense
            val newExpense = agoExpense!!.toLong() + expense.toLong()
            newCompanyFinancialReport = FinancialReport(
                idFinancialReport = financialReportYearAndMonth.idFinancialReport,
                year = financialReportYearAndMonth.year,
                month = financialReportYearAndMonth.month,
                expense = newExpense,
                income = financialReportYearAndMonth.income,
                profit = financialReportYearAndMonth.profit
            )
            financialReportDao.update(newCompanyFinancialReport)
        } else {
            newCompanyFinancialReport = FinancialReport(
                year = valueCalendar!!.year,
                month = valueCalendar!!.month + 1,
                expense = expense.toLong(),
            )
            financialReportDao.insert(newCompanyFinancialReport)
        }
    }
}