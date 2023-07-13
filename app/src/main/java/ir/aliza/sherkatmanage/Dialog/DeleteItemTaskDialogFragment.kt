package ir.aliza.sherkatmanage.Dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import ir.aliza.sherkatmanage.DataBase.AppDatabase
import ir.aliza.sherkatmanage.DataBase.Employee
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

        val employeeDao = AppDatabase.getDataBase(binding.root.context).employeeDao
        val employee = employeeDao.getEmployee(task.idEmployee)
        var numberTask = employee?.numberDoneTask

        if (numberTask == 0)
            numberTask = 1

        val newEmployee = Employee(
            idEmployee = employee!!.idEmployee,
            name = employee.name,
            family = employee.family,
            age = employee.age,
            gender = employee.gender,
            cellularPhone = employee.cellularPhone,
            homePhone = employee.homePhone,
            address = employee.address,
            specialty = employee.specialty,
            skill = employee.skill,
            imgEmployee = employee.imgEmployee,
            numberDoneTask = numberTask!! - 1
        )
        employeeDao.update(newEmployee)

        taskAdapter.removeEmployee(task, position)
        taskEmployeeDao.delete(task)
    }

}