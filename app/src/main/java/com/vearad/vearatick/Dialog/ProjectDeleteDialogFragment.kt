package com.vearad.vearatick.Dialog

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.fragment.app.DialogFragment
import com.vearad.vearatick.DataBase.Project
import com.vearad.vearatick.DataBase.ProjectDao
import com.vearad.vearatick.R
import com.vearad.vearatick.databinding.ActivityProAndEmpBinding
import com.vearad.vearatick.databinding.FragmentDialogDeleteItemProjectBinding
import com.vearad.vearatick.fgmSub.ProjectFragment
import com.vearad.vearatick.fgmSub.ProjectInformationFragment
import com.vearad.vearatick.projectAdapter

class ProjectDeleteDialogFragment(
    private val project: Project,
    private val position: Int,
    val projectDao: ProjectDao,
    val bindingActivityProAndEmp: ActivityProAndEmpBinding,
    val projectInformationFragment: ProjectInformationFragment
) : DialogFragment() {

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
            deleteItem(project ,position)
            dismiss()
            onBackPressed()
        }

        return dialog
    }

    fun onBackPressed() {
        parentFragmentManager.beginTransaction().detach(projectInformationFragment)
            .replace(R.id.frame_layout_sub, ProjectFragment(bindingActivityProAndEmp)).commit()
    }

    fun deleteItem(project: Project, position: Int) {
        projectAdapter.removeProject(project, position)
        projectDao.delete(project)
    }

}