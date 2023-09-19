package ir.aliza.sherkatmanage.Dialog

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.fragment.app.DialogFragment
import ir.aliza.sherkatmanage.DataBase.AppDatabase
import ir.aliza.sherkatmanage.DataBase.EfficiencyDao
import ir.aliza.sherkatmanage.DataBase.EfficiencyEmployee
import ir.aliza.sherkatmanage.DataBase.Employee
import ir.aliza.sherkatmanage.R
import ir.aliza.sherkatmanage.databinding.ActivityProAndEmpBinding
import ir.aliza.sherkatmanage.databinding.FragmentDialogDeleteItemEmployeeBinding
import ir.aliza.sherkatmanage.employeeAdapter
import ir.aliza.sherkatmanage.employeeDao
import ir.aliza.sherkatmanage.fgmSub.EmployeeFragment
import ir.aliza.sherkatmanage.fgmSub.EmployeeInformationFragment

class EmployeeDeleteDialogFragment(
    private val employee: Employee,
    private val position: Int,
    val bindingActivityProAndEmpBinding: ActivityProAndEmpBinding,
    val employeeInformationFragment: EmployeeInformationFragment
) :
    DialogFragment() {

    lateinit var binding: FragmentDialogDeleteItemEmployeeBinding
    lateinit var efficiencyDao: EfficiencyDao

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = FragmentDialogDeleteItemEmployeeBinding.inflate(layoutInflater, null, false)
        val dialog = Dialog(binding.root.context)
        binding.txtTitle.text = "میخوای ${employee.name}رو اخراج کنی ؟"
        efficiencyDao = AppDatabase.getDataBase(binding.root.context).efficiencyDao
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.Transparent.toArgb()))
        dialog.setCancelable(true)
        binding.dialogBtnDeleteCansel.setOnClickListener {
            dismiss()
        }
        binding.dialogBtnDeleteSure.setOnClickListener {
            deleteItem(employee, position)
            onBackPressed()
            dismiss()
        }

        return dialog
    }

    fun onBackPressed() {
        parentFragmentManager.beginTransaction().detach(employeeInformationFragment)
            .replace(R.id.frame_layout_sub, EmployeeFragment(bindingActivityProAndEmpBinding)).commit()
    }

    fun deleteItem(employee: Employee, position: Int) {

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
        employeeAdapter.removeEmployee(employee, position)
        employeeDao.delete(employee)
    }

}