package ir.aliza.sherkatmanage.Dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import ir.aliza.sherkatmanage.DataBase.Project
import ir.aliza.sherkatmanage.DataBase.ProjectDao
import ir.aliza.sherkatmanage.DataBase.SubTaskProject
import ir.aliza.sherkatmanage.DataBase.SubTaskProjectDao
import ir.aliza.sherkatmanage.adapter.SubTaskProjectAdapter
import ir.aliza.sherkatmanage.databinding.FragmentDialogDeleteSubtaskProjectBinding
import ir.aliza.sherkatmanage.databinding.FragmentProjectInformationBinding

class DeleteSubTaskProjectDialogFragment(
    private val subTaskProject: SubTaskProject,
    private val position: Int,
    val subTaskProjectDao: SubTaskProjectDao,
    val subTaskProjectAdapter: SubTaskProjectAdapter,
    val projectDao: ProjectDao,
    val project: Project,
    val binding1: FragmentProjectInformationBinding
) : DialogFragment() {

    lateinit var binding: FragmentDialogDeleteSubtaskProjectBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = AlertDialog.Builder(context)
        binding = FragmentDialogDeleteSubtaskProjectBinding.inflate(layoutInflater, null, false)
        dialog.setView(binding.root)
        dialog.setCancelable(true)
        binding.dialogBtnDeleteCansel.setOnClickListener {
            dismiss()
        }
        binding.dialogBtnDeleteSure.setOnClickListener {
            dismiss()
            deleteItem(subTaskProject, position)
        }

        return dialog.create()

    }

    fun deleteItem(subTaskProject: SubTaskProject, position: Int) {
        subTaskProjectAdapter.deleteSubTask(subTaskProject, position)
        subTaskProjectDao.delete(subTaskProject)

        val project1 = projectDao.getProject(project.idProject!!)

        var numberDonSubTaskProject = project1!!.numberDoneSubTaskProject
        var numberSubTaskProject = project1.numberSubTaskProject

        if (numberSubTaskProject != 0 && numberDonSubTaskProject != 0) {
            numberSubTaskProject = numberSubTaskProject!! - 1
            numberDonSubTaskProject = numberDonSubTaskProject!! - 1
        }

        val newProject = Project(
            idProject = project1.idProject,
            nameProject = project1.nameProject,
            dateProject = project1.dateProject,
            watchProject = project1.watchProject,
            typeProject = project1.typeProject,
            descriptionProject = project1.descriptionProject,
            numberSubTaskProject = numberSubTaskProject,
            numberDoneSubTaskProject = numberDonSubTaskProject,

            year = project.year,
            month = project.month,
            day = project.day

        )
        projectDao.update(newProject)

        binding1.txtNumTaskPro.text =
            numberDonSubTaskProject.toString() + " از " + numberSubTaskProject.toString()
    }

}