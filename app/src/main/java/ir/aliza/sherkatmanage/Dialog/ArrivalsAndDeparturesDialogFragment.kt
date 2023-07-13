package ir.aliza.sherkatmanage.Dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import ir.aliza.sherkatmanage.DataBase.EfficiencyDao
import ir.aliza.sherkatmanage.DataBase.EfficiencyEmployee
import ir.aliza.sherkatmanage.DataBase.Time
import ir.aliza.sherkatmanage.MainActivity
import ir.aliza.sherkatmanage.R
import ir.aliza.sherkatmanage.databinding.FragmentDialogArrivalsAndDeparturesBinding
import ir.aliza.sherkatmanage.databinding.ItemCalendarDayBinding
import ir.aliza.sherkatmanage.timeDao

class ArrivalsAndDeparturesDialogFragment(
    var binding1: ItemCalendarDayBinding,
    val idEmployee: Int,
    val year: String,
    val month: String,
    val day: Int,
    val efficiencyEmployeeDao: EfficiencyDao

    ) : DialogFragment() {

    lateinit var binding: FragmentDialogArrivalsAndDeparturesBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = AlertDialog.Builder(context)

        binding = FragmentDialogArrivalsAndDeparturesBinding.inflate(layoutInflater, null, false)

        dialog.setView(binding.root)
        dialog.setCancelable(true)
        binding.dialogBtnSure.setOnClickListener {
            val dialog = DoneEntryDialogFragment(
                binding1,
                idEmployee,
                year,
                month,
                day,
                efficiencyEmployeeDao
            )
            dialog.show((activity as MainActivity).supportFragmentManager, null)
            dialog.isCancelable = false
            dismiss()
        }
        binding.dialogBtnCansel.setOnClickListener {

            val timeData = timeDao.getTime(idEmployee, day)

            val newTime = Time(
                timeData?.idTime,
                idEmployee = idEmployee,
                year = year,
                month = month,
                day = day.toString(),
                arrival = false,
                entry = "0",
                exit = "0"
            )
            val efficiencyEmployee = efficiencyEmployeeDao.getEfficiencyEmployee(idEmployee)
            val numberDay = efficiencyEmployee?.numberDay

            if (day.toString() == timeData?.day) {

                var time = timeData.exit.toInt() - timeData.entry.toInt()
                val timeAgo = efficiencyEmployee?.totalWeekWatch!! - time

                val newEfficiencyEmployee = EfficiencyEmployee(
                    idEfficiency = efficiencyEmployee.idEfficiency,
                    idEmployee = idEmployee,
                    mustWeekWatch = efficiencyEmployee.mustWeekWatch,
                    totalWeekWatch = timeAgo,
                    numberDay = efficiencyEmployee.numberDay,
                    totalWatch = efficiencyEmployee.totalWatch,
                    efficiencyWeekPresence = efficiencyEmployee.efficiencyWeekPresence,
                    efficiencyTotalPresence = efficiencyEmployee.efficiencyTotalPresence,
                    totalWeekDuties = efficiencyEmployee.totalWeekDuties,
                    totalMonthDuties = efficiencyEmployee.totalMonthDuties,
                    totalDuties = efficiencyEmployee.totalDuties,
                    efficiencyWeekDuties = efficiencyEmployee.efficiencyWeekDuties,
                    efficiencyTotalDuties = efficiencyEmployee.efficiencyTotalDuties,
                    efficiencyTotal = efficiencyEmployee.efficiencyTotal,
                    totalMonthWatch = efficiencyEmployee.totalMonthWatch
                )
                efficiencyEmployeeDao.update(newEfficiencyEmployee)
                timeDao.update(newTime)
                //inOutAdapter.updateInOut(newTime, 0)
                binding1.viewDaySub.setBackgroundColor(it.context.getColor(R.color.red_800))
            } else {
                val newEfficiencyEmployee = EfficiencyEmployee(
                    idEfficiency = efficiencyEmployee?.idEfficiency,
                    idEmployee = idEmployee,
                    mustWeekWatch = efficiencyEmployee?.mustWeekWatch,
                    numberDay = numberDay!! + 1,
                    totalWeekWatch = efficiencyEmployee.totalWeekWatch,
                    totalWatch = efficiencyEmployee.totalWatch,
                    efficiencyWeekPresence = efficiencyEmployee.efficiencyWeekPresence,
                    efficiencyTotalPresence = efficiencyEmployee.efficiencyTotalPresence,
                    totalWeekDuties = efficiencyEmployee.totalWeekDuties,
                    totalMonthDuties = efficiencyEmployee.totalMonthDuties,
                    totalDuties = efficiencyEmployee.totalDuties,
                    efficiencyWeekDuties = efficiencyEmployee.efficiencyWeekDuties,
                    efficiencyTotalDuties = efficiencyEmployee.efficiencyTotalDuties,
                    efficiencyTotal = efficiencyEmployee.efficiencyTotal,
                    totalMonthWatch = efficiencyEmployee.totalMonthWatch
                )
                efficiencyEmployeeDao.update(newEfficiencyEmployee)
                timeDao.insert(newTime)
                binding1.viewDaySub.setBackgroundColor(it.context.getColor(R.color.red_800))
            }

            dismiss()
        }

        return dialog.create()

    }

}