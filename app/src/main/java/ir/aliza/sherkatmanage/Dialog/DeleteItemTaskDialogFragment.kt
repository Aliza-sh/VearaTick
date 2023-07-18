package ir.aliza.sherkatmanage.Dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import ir.aliza.sherkatmanage.DataBase.AppDatabase
import ir.aliza.sherkatmanage.DataBase.EfficiencyEmployee
import ir.aliza.sherkatmanage.DataBase.TaskEmployee
import ir.aliza.sherkatmanage.databinding.FragmentDialogDeleteItemTaskBinding
import ir.aliza.sherkatmanage.taskAdapter
import ir.aliza.sherkatmanage.taskEmployeeDao

class DeleteItemTaskDialogFragment(private val task: TaskEmployee, private val position: Int) :
    DialogFragment() {

    lateinit var binding: FragmentDialogDeleteItemTaskBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = AlertDialog.Builder(context)
        binding = FragmentDialogDeleteItemTaskBinding.inflate(layoutInflater, null, false)
        dialog.setView(binding.root)
        dialog.setCancelable(true)
        binding.dialogBtnDeleteCansel.setOnClickListener {
            dismiss()
        }

        binding.dialogBtnDeleteSure.setOnClickListener {
            dismiss()
            deleteItem(task, position)
        }

        return dialog.create()

    }

    fun deleteItem(task: TaskEmployee, position: Int) {

        val efficiencyEmployeeDao = AppDatabase.getDataBase(binding.root.context).efficiencyDao
        val efficiencyEmployee = efficiencyEmployeeDao.getEfficiencyEmployee(task.idEmployee)
        var numberTask = efficiencyEmployee?.totalWeekDuties

        if (numberTask == 0)
            numberTask = 1

        val newEfficiencyEmployee = EfficiencyEmployee(
            idEfficiency = efficiencyEmployee!!.idEfficiency,
            idEmployee = efficiencyEmployee.idEmployee,
            mustWeekWatch = efficiencyEmployee.mustWeekWatch,
            numberDay = efficiencyEmployee.numberDay,
            totalWeekWatch = efficiencyEmployee.totalWeekWatch,
            totalWatch = efficiencyEmployee.totalWatch,
            efficiencyWeekPresence = efficiencyEmployee.efficiencyWeekPresence,
            efficiencyTotalPresence = efficiencyEmployee.efficiencyTotalPresence,
            totalWeekDuties = numberTask!! - 1 ,
            totalMonthDuties = efficiencyEmployee.totalMonthDuties,
            totalDuties = efficiencyEmployee.totalDuties,
            efficiencyWeekDuties = efficiencyEmployee.efficiencyWeekDuties,
            efficiencyTotalDuties = efficiencyEmployee.efficiencyTotalDuties,
            efficiencyTotal = efficiencyEmployee.efficiencyTotal,
            totalMonthWatch = efficiencyEmployee.totalMonthWatch
        )
        efficiencyEmployeeDao.update(newEfficiencyEmployee)

        taskAdapter.removeEmployee(task, position)
        taskEmployeeDao.delete(task)
    }

}