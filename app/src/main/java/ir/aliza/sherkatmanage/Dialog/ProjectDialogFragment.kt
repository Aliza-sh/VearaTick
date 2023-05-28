package ir.aliza.sherkatmanage.Dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import ir.aliza.sherkatmanage.DataBase.Project
import ir.aliza.sherkatmanage.databinding.FragmentDialogDeleteItemProjectBinding
import ir.aliza.sherkatmanage.projectAdapter
import ir.aliza.sherkatmanage.projectDao

class ProjectDialogFragment(private val project: Project, private val position: Int) : DialogFragment() {

    lateinit var binding: FragmentDialogDeleteItemProjectBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = AlertDialog.Builder(context)
        binding = FragmentDialogDeleteItemProjectBinding.inflate(layoutInflater, null, false)
        dialog.setView(binding.root)
        dialog.setCancelable(true)
        binding.dialogBtnDeleteCansel.setOnClickListener {
            dismiss()
        }
        binding.dialogBtnDeleteSure.setOnClickListener {
            dismiss()
            deleteItem(project ,position)
        }

        return dialog.create()

    }

    fun deleteItem(project: Project, position: Int) {
        projectAdapter.removeProject(project, position)
        projectDao.delete(project)
    }

}