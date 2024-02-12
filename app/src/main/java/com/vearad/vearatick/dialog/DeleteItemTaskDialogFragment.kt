package com.vearad.vearatick.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.vearad.vearatick.DataBase.AppDatabase
import com.vearad.vearatick.DataBase.EfficiencyEmployee
import com.vearad.vearatick.DataBase.TaskEmployee
import com.vearad.vearatick.databinding.FragmentDialogDeleteItemTaskBinding
import com.vearad.vearatick.taskAdapter
import com.vearad.vearatick.taskEmployeeDao

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
            totalMonthWatch = efficiencyEmployee.totalMonthWatch,
            efficiencyMonthDuties = efficiencyEmployee.efficiencyMonthDuties
        )
        efficiencyEmployeeDao.update(newEfficiencyEmployee)

        taskAdapter.removeEmployee(task, position)
        taskEmployeeDao.delete(task)
    }

}