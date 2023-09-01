package ir.aliza.sherkatmanage.Dialog

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.fragment.app.DialogFragment
import ir.aliza.sherkatmanage.DataBase.Project
import ir.aliza.sherkatmanage.DataBase.ProjectDao
import ir.aliza.sherkatmanage.DataBase.SubTaskProject
import ir.aliza.sherkatmanage.DataBase.SubTaskProjectDao
import ir.aliza.sherkatmanage.adapter.SubTaskProjectAdapter
import ir.aliza.sherkatmanage.databinding.FragmentDialogDeleteSubtaskProjectBinding

class DeleteSubTaskProjectDialogFragment(
    private val subTaskProject: SubTaskProject,
    private val position: Int,
    val subTaskProjectDao: SubTaskProjectDao,
    val subTaskProjectAdapter: SubTaskProjectAdapter,
    val projectDao: ProjectDao,
    val project: Project,
) : DialogFragment() {

    lateinit var binding: FragmentDialogDeleteSubtaskProjectBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        binding = FragmentDialogDeleteSubtaskProjectBinding.inflate(layoutInflater, null, false)
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
            deleteItem(subTaskProject, position)
        }

        return dialog
    }

    fun deleteItem(subTaskProject: SubTaskProject, position: Int) {

        val project1 = projectDao.getProject(project.idProject!!)
        var numberDonSubTaskProject = project1!!.numberDoneSubTaskProject
        var numberSubTaskProject = project1.numberSubTaskProject

        if (subTaskProject.doneSubTask!!) {
            numberSubTaskProject = numberSubTaskProject!! - 1
            numberDonSubTaskProject = numberDonSubTaskProject!! - 1
        } else
            numberSubTaskProject = numberSubTaskProject!! - 1

        subTaskProjectAdapter.deleteSubTask(subTaskProject, position)
        subTaskProjectDao.delete(subTaskProject)

        var efficiencyProject = 0

        if (numberSubTaskProject != null)
            efficiencyProject =
                ((numberDonSubTaskProject!!.toDouble() / numberSubTaskProject) * 100).toInt()

        val newProject = Project(
            idProject = project1.idProject,
            nameProject = project1.nameProject,
            noDeadlineProject = project1.noDeadlineProject,
            dateDeadlineProject = project1.dateDeadlineProject,
            watchDeadlineProject = project1.watchDeadlineProject,
            typeProject = project1.typeProject,
            descriptionProject = project1.descriptionProject,
            numberSubTaskProject = numberSubTaskProject,
            numberDoneSubTaskProject = numberDonSubTaskProject,
            progressProject = efficiencyProject,
            budgetProject = project1.budgetProject,
            doneProject = project1.doneProject
        )
        projectDao.update(newProject)
    }
}