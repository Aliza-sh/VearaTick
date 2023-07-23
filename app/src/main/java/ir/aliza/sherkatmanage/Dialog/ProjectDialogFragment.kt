package ir.aliza.sherkatmanage.Dialog

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.fragment.app.DialogFragment
import ir.aliza.sherkatmanage.DataBase.Project
import ir.aliza.sherkatmanage.databinding.FragmentDialogDeleteItemProjectBinding
import ir.aliza.sherkatmanage.projectAdapter
import ir.aliza.sherkatmanage.projectDao

class ProjectDialogFragment(private val project: Project, private val position: Int) : DialogFragment() {

    lateinit var binding: FragmentDialogDeleteItemProjectBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = FragmentDialogDeleteItemProjectBinding.inflate(layoutInflater, null, false)
        val dialog = Dialog(binding.root.context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.Transparent.toArgb()))
        dialog.setCancelable(true)
        binding.dialogBtnDeleteCansel.setOnClickListener {
            dismiss()
        }
        binding.dialogBtnDeleteSure.setOnClickListener {
            dismiss()
            deleteItem(project ,position)
        }

        return dialog

    }

    fun deleteItem(project: Project, position: Int) {
        projectAdapter.removeProject(project, position)
        projectDao.delete(project)
    }

}