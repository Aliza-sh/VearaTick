package ir.aliza.sherkatmanage.Dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import ir.aliza.sherkatmanage.DataBase.SubTaskEmployeeTack
import ir.aliza.sherkatmanage.DataBase.SubTaskEmployeeTackDao
import ir.aliza.sherkatmanage.DataBase.TaskEmployee
import ir.aliza.sherkatmanage.DataBase.TaskEmployeeDao
import ir.aliza.sherkatmanage.adapter.SubTaskEmployeeTackAdapter
import ir.aliza.sherkatmanage.databinding.FragmentDialogDeleteSubtaskEmployeetaskBinding
import ir.aliza.sherkatmanage.databinding.FragmentTaskInformationBinding

class DeleteSubTaskEmployeeTaskDialogFragment(
    private val subTaskEmployee: SubTaskEmployeeTack,
    private val position: Int,
    val subTaskEmployeeTaskDao: SubTaskEmployeeTackDao,
    val subTaskEmployeeTaskAdapter: SubTaskEmployeeTackAdapter,
    val taskEmployeeDao: TaskEmployeeDao,
    val task: TaskEmployee,
    val binding1: FragmentTaskInformationBinding

) : DialogFragment() {

    lateinit var binding: FragmentDialogDeleteSubtaskEmployeetaskBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = AlertDialog.Builder(context)
        binding = FragmentDialogDeleteSubtaskEmployeetaskBinding.inflate(layoutInflater, null, false)
        dialog.setView(binding.root)
        dialog.setCancelable(true)
        binding.dialogBtnDeleteCansel.setOnClickListener {
            dismiss()
        }
        binding.dialogBtnDeleteSure.setOnClickListener {
            dismiss()
            deleteItem(subTaskEmployee, position)
        }

        return dialog.create()

    }

    fun deleteItem(subTaskEmployee: SubTaskEmployeeTack, position: Int) {
        subTaskEmployeeTaskAdapter.deleteSubTask(subTaskEmployee, position)
        subTaskEmployeeTaskDao.delete(subTaskEmployee)

        val tack1 = taskEmployeeDao.getSubTaskDay(task.idTask!!,task.day.toInt())

        var numberDonSubTaskEmployeeTask = tack1!!.numberDoneSubTaskEmployeeTask
        var numberSubTaskProjectEmployeeTask = tack1.numberSubTaskEmployeeTask

        if (numberSubTaskProjectEmployeeTask != 0 && numberDonSubTaskEmployeeTask != 0) {
            numberSubTaskProjectEmployeeTask = numberSubTaskProjectEmployeeTask!! - 1
            numberDonSubTaskEmployeeTask = numberDonSubTaskEmployeeTask!! - 1
        }

        val newTack = TaskEmployee(
            idTask = tack1.idTask,
            idEmployee = tack1.idEmployee,
            nameTask = tack1.nameTask,
            dayTask = tack1.dayTask,
            watchTask = tack1.watchTask,
            typeTask = tack1.typeTask,
            descriptionTask = tack1.descriptionTask,
            numberSubTaskEmployeeTask = numberSubTaskProjectEmployeeTask,
            numberDoneSubTaskEmployeeTask = numberDonSubTaskEmployeeTask,

            year = tack1.year,
            month = tack1.month,
            day = tack1.day

        )
        taskEmployeeDao.update(newTack)

        binding1.txtNumTask.text =
            numberDonSubTaskEmployeeTask.toString() + " از " + numberSubTaskProjectEmployeeTask.toString()
    }

}