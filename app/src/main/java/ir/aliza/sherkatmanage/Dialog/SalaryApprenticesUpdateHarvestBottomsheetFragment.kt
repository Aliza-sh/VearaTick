package ir.aliza.sherkatmanage.Dialog

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.xdev.arch.persiancalendar.datepicker.CalendarConstraints
import com.xdev.arch.persiancalendar.datepicker.DateValidatorPointForward
import com.xdev.arch.persiancalendar.datepicker.MaterialDatePicker
import com.xdev.arch.persiancalendar.datepicker.MaterialPickerOnPositiveButtonClickListener
import com.xdev.arch.persiancalendar.datepicker.Month
import com.xdev.arch.persiancalendar.datepicker.calendar.PersianCalendar
import ir.aliza.sherkatmanage.BottomSheetCallback
import ir.aliza.sherkatmanage.CompanyReceiptActivity
import ir.aliza.sherkatmanage.DataBase.Employee
import ir.aliza.sherkatmanage.DataBase.EmployeeHarvest
import ir.aliza.sherkatmanage.DataBase.EmployeeHarvestDao
import ir.aliza.sherkatmanage.R
import ir.aliza.sherkatmanage.databinding.BottomsheetfragmentCompanyNewReceiptBinding
import java.text.DecimalFormat


class SalaryApprenticesUpdateHarvestBottomsheetFragment(
    val employee: Employee,
    val employeeHarvestDao: EmployeeHarvestDao,
    val onClickApprenticesHarvest: EmployeeHarvest?,
    val position: Int,
) : BottomSheetDialogFragment() {

    lateinit var binding: BottomsheetfragmentCompanyNewReceiptBinding
    var valueCalendar: PersianCalendar? = null
    private var isUpdating = false
    private var callback: BottomSheetCallback? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            BottomsheetfragmentCompanyNewReceiptBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setdata()

        binding.btnCalendar.setOnClickListener {
            onCreateCalendar()
        }
        binding.text.text = "دریافتی شرکت رو آپدیت کن ."
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
            addNewReceipt(formattedValue)
        }
    }

    private fun setdata() {
        binding.edtReceipt.setText(onClickApprenticesHarvest!!.harvest.toString())
        binding.txtDateReceipt.setText(onClickApprenticesHarvest.harvestDate.toString())
        binding.dialogEdtTozih.setText(onClickApprenticesHarvest.harvestDescription)
        binding.txtReceipt.setText(formatCurrency(onClickApprenticesHarvest.harvest))
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

    private fun addNewReceipt(formattedValue: String) {
        if (
            binding.edtReceipt.length() > 0 &&
            binding.txtDateReceipt.length() > 0 &&
            binding.dialogEdtTozih.length() > 0
        ) {
            val txtReceipt = binding.edtReceipt.text.toString()
            val txtDescription = binding.dialogEdtTozih.text.toString()
            val txtDate = binding.txtDateReceipt.text


            val newEmployeeHarvest = EmployeeHarvest(
                idHarvest = onClickApprenticesHarvest!!.idHarvest,
                idEmployee = employee.idEmployee!!,
                harvest = txtReceipt.toLong(),
                harvestDescription = txtDescription,
                harvestDate = txtDate.toString()
            )
            employeeHarvestDao.update(newEmployeeHarvest)
            onCompanyNewReceipt()
            dismiss()
        } else {
            Toast.makeText(context, "لطفا همه مقادیر را وارد کنید", Toast.LENGTH_SHORT).show()
        }
    }

}