package ir.aliza.sherkatmanage.Dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.kizitonwose.calendarview.model.CalendarMonth
import ir.aliza.sherkatmanage.DataBase.Day
import ir.aliza.sherkatmanage.DataBase.EfficiencyDao
import ir.aliza.sherkatmanage.DataBase.EfficiencyEmployee
import ir.aliza.sherkatmanage.DataBase.Employee
import ir.aliza.sherkatmanage.R
import ir.aliza.sherkatmanage.databinding.FragmentDialogEntryExitBinding
import ir.aliza.sherkatmanage.dayDao

class EntryAndExitDialogFragment(
    val month: CalendarMonth,
    val employee: Employee,
    val tv: TextView,
    val efficiencyEmployeeDao: EfficiencyDao
) : DialogFragment() {

    lateinit var binding: FragmentDialogEntryExitBinding
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = AlertDialog.Builder(context)
        binding = FragmentDialogEntryExitBinding.inflate(layoutInflater, null, false)

        dialog.setView(binding.root)
        dialog.setCancelable(true)
        binding.dialogBtnSure.setOnClickListener {

            if (
                binding.edtEntryEpm.length() > 0 &&
                binding.edtExitEmp.length() > 0 &&
                binding.edtEntryEpm.text.toString().toInt() < binding.edtExitEmp.text.toString()
                    .toInt()

            ) {

                val efficiencyEmployee =
                    efficiencyEmployeeDao.getEfficiencyEmployee(employee.idEmployee!!)

                val timeAgo = efficiencyEmployee?.mustWeekWatch!!
                val timeNew =
                    binding.edtExitEmp.text.toString().toInt() - binding.edtEntryEpm.text.toString()
                        .toInt()
                val time = timeNew + timeAgo

                val newDay = Day(
                    idDay = ("${tv.id}${employee.idEmployee}").toLong(),
                    idEmployee = employee.idEmployee,
                    year = month.persianCalendar.persianYear.toString(),
                    month = month.persianCalendar.persianMonthName,
                    nameday = "${tv.text}",
                    entry = binding.edtEntryEpm.text.toString(),
                    exit = binding.edtExitEmp.text.toString()
                )

                dayDao.insert(newDay)
                tv.setBackgroundColor(
                    ContextCompat.getColor(
                        tv.context, R.color.firoze
                    )
                )

                val newEfficiencyEmployee = EfficiencyEmployee(
                    idEfficiency = efficiencyEmployee.idEfficiency,
                    idEmployee = efficiencyEmployee.idEmployee,
                    mustWeekWatch = time,
                    totalWeekWatch = efficiencyEmployee.totalWeekWatch,
                    efficiencyWeekPresence = efficiencyEmployee.efficiencyWeekPresence,
                    totalWatch = efficiencyEmployee.totalWatch,
                    efficiencyTotalPresence = efficiencyEmployee.efficiencyTotalPresence,
                    totalWeekDuties = efficiencyEmployee.totalWeekDuties,
                    totalMonthDuties = efficiencyEmployee.totalMonthDuties,
                    totalDuties = efficiencyEmployee.totalDuties,
                    efficiencyWeekDuties = efficiencyEmployee.efficiencyWeekDuties,
                    efficiencyTotalDuties = efficiencyEmployee.efficiencyTotalDuties,
                    efficiencyTotal = efficiencyEmployee.efficiencyTotal,
                    numberDay = efficiencyEmployee.numberDay
                    )
                efficiencyEmployeeDao.update(newEfficiencyEmployee)

                dismiss()

            } else {
                Toast.makeText(context, "لطفا مقادیر را درست وارد کنید", Toast.LENGTH_SHORT).show()
            }
        }
        return dialog.create()
    }
}