package ir.aliza.sherkatmanage.Dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import ir.aliza.sherkatmanage.DataBase.EfficiencyDao
import ir.aliza.sherkatmanage.DataBase.EfficiencyEmployee
import ir.aliza.sherkatmanage.DataBase.Time
import ir.aliza.sherkatmanage.R
import ir.aliza.sherkatmanage.databinding.FragmentCalendarBinding
import ir.aliza.sherkatmanage.databinding.FragmentDialogDoneEntryBinding
import ir.aliza.sherkatmanage.databinding.ItemCalendarDayBinding
import ir.aliza.sherkatmanage.timeDao

class DoneEntryDialogFragment(
    var binding2: ItemCalendarDayBinding,
    val idEmployee: Int?,
    val year: String,
    val month: String,
    val day: Int,
    val efficiencyEmployeeDao: EfficiencyDao,
) : DialogFragment() {

    lateinit var binding: FragmentDialogDoneEntryBinding
    lateinit var binding1: FragmentCalendarBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = AlertDialog.Builder(context)
        binding = FragmentDialogDoneEntryBinding.inflate(layoutInflater, null, false)
        binding1 = FragmentCalendarBinding.inflate(layoutInflater, null, false)

        val timeData = timeDao.getTime(idEmployee!!, day)

        dialog.setView(binding.root)
        dialog.setCancelable(true)

        binding.dialogBtnSure.setOnClickListener {

            if (
                binding.edtEntryEpm.length() > 0 &&
                binding.edtExitEmp.length() > 0 &&
                binding.edtEntryEpm.text.toString().toInt() < binding.edtExitEmp.text.toString()
                    .toInt() &&
                binding.edtEntryEpm.text.toString().toInt() <= 24 &&
                binding.edtExitEmp.text.toString().toInt() <= 24

            ) {

                val efficiencyEmployee = efficiencyEmployeeDao.getEfficiencyEmployee(idEmployee)
                val numberDay = efficiencyEmployee?.numberDay

                val newTime = Time(
                    timeData?.idTime,
                    idEmployee = idEmployee,
                    year = year,
                    month = month,
                    day = day.toString(),
                    arrival = true,
                    entry = binding.edtEntryEpm.text.toString(),
                    exit = binding.edtExitEmp.text.toString()
                )

                if (day.toString() == timeData?.day) {

                    var time = timeData.exit.toInt() - timeData.entry.toInt()
                    val timeAgo = efficiencyEmployee?.totalWeekWatch!! - time
                    val timeNew = binding.edtExitEmp.text.toString()
                        .toInt() - binding.edtEntryEpm.text.toString().toInt()

                    time = timeNew + timeAgo

                    val newEfficiencyEmployee = EfficiencyEmployee(
                        idEfficiency = efficiencyEmployee.idEfficiency,
                        idEmployee = idEmployee,
                        mustWeekWatch = efficiencyEmployee.mustWeekWatch,
                        numberDay = efficiencyEmployee.numberDay,
                        totalWeekWatch = time,
                        totalWatch = efficiencyEmployee.totalWatch,
                        efficiencyWeekPresence = efficiencyEmployee.efficiencyWeekPresence,
                        efficiencyTotalPresence = efficiencyEmployee.efficiencyTotalPresence,
                        totalWeekDuties = efficiencyEmployee.totalWeekDuties,
                        totalMonthDuties = efficiencyEmployee.totalMonthDuties,
                        totalDuties = efficiencyEmployee.totalDuties,
                        efficiencyWeekDuties = efficiencyEmployee.efficiencyWeekDuties,
                        efficiencyTotalDuties = efficiencyEmployee.efficiencyTotalDuties,
                        efficiencyTotal = efficiencyEmployee.efficiencyTotal,
                        totalMonthWatch = efficiencyEmployee.totalMonthWatch,
                        efficiencyMonthDuties = efficiencyEmployee.totalMonthDuties
                    )
                    efficiencyEmployeeDao.update(newEfficiencyEmployee)

                    timeDao.update(newTime)
                    //inOutAdapter.updateInOut(newTime, 0)
                    binding2.viewDaySub.setBackgroundColor(it.context.getColor(R.color.green_700))

                } else {

                    val timeAgo = efficiencyEmployee?.totalWeekWatch!!
                    val timeNew = binding.edtExitEmp.text.toString()
                        .toInt() - binding.edtEntryEpm.text.toString().toInt()

                    val time = timeNew + timeAgo

                    val newEfficiencyEmployee = EfficiencyEmployee(
                        idEfficiency = efficiencyEmployee.idEfficiency,
                        idEmployee = idEmployee,
                        mustWeekWatch = efficiencyEmployee.mustWeekWatch,
                        totalWeekWatch = time,
                        numberDay = numberDay!! + 1,
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
                    binding2.viewDaySub.setBackgroundColor(it.context.getColor(R.color.green_700))

                }

                dismiss()
            } else {
                Toast.makeText(context, "لطفا همه مقادیر را وارد کنید", Toast.LENGTH_SHORT).show()
            }
        }
        return dialog.create()

    }

}