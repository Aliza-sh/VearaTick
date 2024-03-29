package com.vearad.vearatick.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.vearad.vearatick.model.db.AppDatabase
import com.vearad.vearatick.model.db.EfficiencyEmployee
import com.vearad.vearatick.model.db.TaskEmployee
import com.vearad.vearatick.databinding.FragmentDialogDeleteItemTaskBinding
import com.vearad.vearatick.ui.taskAdapter
import com.vearad.vearatick.ui.taskEmployeeDao

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