package ir.aliza.sherkatmanage.Dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import ir.aliza.sherkatmanage.DataBase.TaskEmployee
import ir.aliza.sherkatmanage.databinding.FragmentDialogDeleteItemTaskBinding
import ir.aliza.sherkatmanage.taskAdapter
import ir.aliza.sherkatmanage.taskEmployeeDao

class DeleteItemTaskDialogFragment(private val task: TaskEmployee, private val position: Int) : DialogFragment() {

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
            deleteItem(task ,position)
        }

        return dialog.create()

    }

    fun deleteItem(task: TaskEmployee, position: Int) {
        taskAdapter.removeEmployee(task, position)
        taskEmployeeDao.delete(task)
    }

}