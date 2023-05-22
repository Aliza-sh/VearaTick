package ir.aliza.sherkatmanage.Dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import ir.aliza.sherkatmanage.DataBase.Employee
import ir.aliza.sherkatmanage.databinding.FragmentDialogDeleteItemEmployeeBinding
import ir.aliza.sherkatmanage.employeeAdapter
import ir.aliza.sherkatmanage.employeeDao

class EmployeeDialogFragment(private val employee: Employee, private val position: Int) : DialogFragment() {

    lateinit var binding: FragmentDialogDeleteItemEmployeeBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = AlertDialog.Builder(context)
        binding = FragmentDialogDeleteItemEmployeeBinding.inflate(layoutInflater, null, false)
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
        employeeDao.deleteEmployee(employee)
    }

}