package com.vearad.vearatick.Dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.DialogFragment
import com.kizitonwose.calendarview.model.CalendarMonth
import com.vearad.vearatick.DataBase.Day
import com.vearad.vearatick.DataBase.EfficiencyDao
import com.vearad.vearatick.DataBase.EfficiencyEmployee
import com.vearad.vearatick.DataBase.Employee
import com.vearad.vearatick.R
import com.vearad.vearatick.databinding.FragmentDialogEntryExitBinding
import com.vearad.vearatick.dayDao

class EntryAndExitDialogFragment(
    val month: CalendarMonth,
    val employee: Employee,
    val tv: TextView,
    val efficiencyEmployeeDao: EfficiencyDao,
    val legendLayout: LinearLayout,
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
                    .toInt() &&
                binding.edtExitEmp.text.toString().toInt() <= 24 &&
                binding.edtEntryEpm.text.toString().toInt() <= 24

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
                    numberDay = efficiencyEmployee.numberDay,
                    efficiencyMonthDuties = efficiencyEmployee.totalMonthDuties,
                    totalMonthWatch = efficiencyEmployee.totalMonthWatch
                )
                efficiencyEmployeeDao.update(newEfficiencyEmployee)

                if (binding.checkBox.isChecked)
                    clickAllDay(efficiencyEmployee, timeNew)

                dismiss()

            } else {
                Toast.makeText(context, "لطفا مقادیر را درست وارد کنید", Toast.LENGTH_SHORT).show()
            }
        }
        return dialog.create()
    }

    private fun clickAllDay(efficiencyEmployee: EfficiencyEmployee, time: Int) {

        legendLayout.children.map { it as TextView }
            .forEachIndexed { index, tv ->


                val newDay = Day(
                    idDay = ("${tv.id}${employee.idEmployee}").toLong(),
                    idEmployee = employee.idEmployee,
                    year = month.persianCalendar.persianYear.toString(),
                    month = month.persianCalendar.persianMonthName,
                    nameday = "${tv.text}",
                    entry = binding.edtEntryEpm.text.toString(),
                    exit = binding.edtExitEmp.text.toString()
                )
                dayDao.insertOrUpdateFood(newDay)

                val dayData =
                    dayDao.getDay(("${tv.id}${employee.idEmployee}").toLong())

                if ((dayData?.idDay) == ("${tv.id}${employee.idEmployee}").toLong() && dayData.idEmployee == employee.idEmployee) {
                    tv.setBackgroundColor(
                        ContextCompat.getColor(
                            tv.context, R.color.firoze
                        )
                    )
                }

            }

        val newEfficiencyEmployee = EfficiencyEmployee(
            idEfficiency = efficiencyEmployee.idEfficiency,
            idEmployee = efficiencyEmployee.idEmployee,
            mustWeekWatch = 7 * time,
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
            numberDay = efficiencyEmployee.numberDay,
            efficiencyMonthDuties = efficiencyEmployee.totalMonthDuties,
            totalMonthWatch = efficiencyEmployee.totalMonthWatch
        )
        efficiencyEmployeeDao.update(newEfficiencyEmployee)

    }
}