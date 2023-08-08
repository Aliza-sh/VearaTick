package ir.aliza.sherkatmanage.Dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import ir.aliza.sherkatmanage.DataBase.AppDatabase
import ir.aliza.sherkatmanage.DataBase.EfficiencyDao
import ir.aliza.sherkatmanage.DataBase.EfficiencyEmployee
import ir.aliza.sherkatmanage.DataBase.Employee
import ir.aliza.sherkatmanage.ProAndEmpActivity
import ir.aliza.sherkatmanage.R
import ir.aliza.sherkatmanage.databinding.ActivityProAndEmpBinding
import ir.aliza.sherkatmanage.databinding.FragmentDialogDeleteItemEmployeeBinding
import ir.aliza.sherkatmanage.employeeAdapter
import ir.aliza.sherkatmanage.employeeDao
import ir.aliza.sherkatmanage.fgmSub.EmployeeFragment

class EmployeeDialogFragment(private val employee: Employee, private val position: Int) : DialogFragment() {

    lateinit var binding: FragmentDialogDeleteItemEmployeeBinding
    lateinit var bindingActivityProAndEmpBinding: ActivityProAndEmpBinding
    lateinit var efficiencyDao: EfficiencyDao

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = AlertDialog.Builder(context)
        binding = FragmentDialogDeleteItemEmployeeBinding.inflate(layoutInflater, null, false)
        bindingActivityProAndEmpBinding = ActivityProAndEmpBinding.inflate(layoutInflater, null, false)
        efficiencyDao = AppDatabase.getDataBase(binding.root.context).efficiencyDao

        dialog.setView(binding.root)
        dialog.setCancelable(true)
        binding.dialogBtnDeleteCansel.setOnClickListener {
            dismiss()
        }
        binding.dialogBtnDeleteSure.setOnClickListener {
            deleteItem(employee ,position)
            val transaction = (activity as ProAndEmpActivity).supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_layout_sub, EmployeeFragment(bindingActivityProAndEmpBinding))
                .addToBackStack(null)
                .commit()
            dismiss()
        }

        return dialog.create()

    }

    fun deleteItem(employee: Employee, position: Int) {

        val efficiencyEmployee = efficiencyDao.getEfficiencyEmployee(position)
        val deleteDfficiencyEmployee = EfficiencyEmployee(
            idEfficiency = efficiencyEmployee!!.idEfficiency,
            idEmployee = efficiencyEmployee.idEmployee,
            mustWeekWatch = efficiencyEmployee.mustWeekWatch,
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
        efficiencyDao.delete(deleteDfficiencyEmployee)
        employeeAdapter.removeEmployee(employee, position)
        employeeDao.delete(employee)
    }

}