package ir.aliza.sherkatmanage.Dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import ir.aliza.sherkatmanage.DataBase.Task
import ir.aliza.sherkatmanage.databinding.FragmentDialogDeleteItemTaskBinding
import ir.aliza.sherkatmanage.taskAdapter
import ir.aliza.sherkatmanage.taskDao

class DeleteItemTaskDialogFragment(private val task: Task, private val position: Int) : DialogFragment() {

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

    fun deleteItem(task: Task, position: Int) {
        taskAdapter.removeEmployee(task, position)
        taskDao.delete(task)
    }

}