package ir.aliza.sherkatmanage.Dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import ir.aliza.sherkatmanage.DataBase.AppDatabase
import ir.aliza.sherkatmanage.DataBase.EfficiencyDao
import ir.aliza.sherkatmanage.DataBase.EfficiencyEmployee
import ir.aliza.sherkatmanage.DataBase.Employee
import ir.aliza.sherkatmanage.databinding.FragmentDialogDeleteItemEmployeeBinding
import ir.aliza.sherkatmanage.employeeAdapter
import ir.aliza.sherkatmanage.employeeDao

class EmployeeDialogFragment(private val employee: Employee, private val position: Int) : DialogFragment() {

    lateinit var binding: FragmentDialogDeleteItemEmployeeBinding
    lateinit var efficiencyDao: EfficiencyDao

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = AlertDialog.Builder(context)
        binding = FragmentDialogDeleteItemEmployeeBinding.inflate(layoutInflater, null, false)
        efficiencyDao = AppDatabase.getDataBase(binding.root.context).efficiencyDao

        dialog.setView(binding.root)
        dialog.setCancelable(true)
        binding.dialogBtnDeleteCansel.setOnClickListener {
            dismiss()
        }
        binding.dialogBtnDeleteSure.setOnClickListener {
            dismiss()
            deleteItem(employee ,position)
        }

        return dialog.create()

    }

    fun deleteItem(employee: Employee, position: Int) {
        employeeAdapter.removeEmployee(employee, position)
        employeeDao.delete(employee)

        val efficiencyEmployee = efficiencyDao.getEfficiencyEmployee(employee.idEmployee!!)
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
    }

}