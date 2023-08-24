package ir.aliza.sherkatmanage.Dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ir.aliza.sherkatmanage.DataBase.Project
import ir.aliza.sherkatmanage.DataBase.ProjectDao
import ir.aliza.sherkatmanage.DataBase.SubTaskProject
import ir.aliza.sherkatmanage.DataBase.SubTaskProjectDao
import ir.aliza.sherkatmanage.ProAndEmpActivity
import ir.aliza.sherkatmanage.R
import ir.aliza.sherkatmanage.adapter.SubTaskProjectAdapter
import ir.aliza.sherkatmanage.databinding.ActivityProAndEmpBinding
import ir.aliza.sherkatmanage.databinding.BottomsheetfragmentSubtaskBinding
import ir.aliza.sherkatmanage.fgmSub.ProjectInformationFragment

class SubTaskBottomsheetFragment(
    val subTaskProjectDao: SubTaskProjectDao,
    val project: Project,
    val subTaskProjectAdapter: SubTaskProjectAdapter,
    val projectDao: ProjectDao,
    val position: Int,
    val bindingActivityProAndEmp: ActivityProAndEmpBinding,
    val projectInformationFragment: ProjectInformationFragment,
    val watchProject: String,
    val dateProject: String,
) : BottomSheetDialogFragment() {

    lateinit var binding: BottomsheetfragmentSubtaskBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomsheetfragmentSubtaskBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.sheetBtnDone.setOnClickListener {
            addNewTask()
            onSubTaskToProject()
        }
    }

    fun onSubTaskToProject() {
        parentFragmentManager.beginTransaction().detach(projectInformationFragment)
            .replace(
                R.id.layout_pro_and_emp,
                ProjectInformationFragment(
                    project,
                    watchProject,
                    dateProject,
                    subTaskProjectDao,
                    projectDao,
                    position,
                    bindingActivityProAndEmp
                )
            ).commit()
    }

    private fun addNewTask() {
        if (
            binding.edtNameTask.length() > 0 &&
            binding.edtDescriptionTask.length() > 0
        ) {
            val txtTask = binding.edtNameTask.text.toString()
            val txtDescription = binding.edtDescriptionTask.text.toString()

            val newSubTask = SubTaskProject(

                idProject = project.idProject!!,
                nameSubTask = txtTask,
                descriptionTask = txtDescription,

                )
            subTaskProjectDao.insert(newSubTask)
            subTaskProjectAdapter.addTask(newSubTask)

            val project1 = projectDao.getProject(project.idProject)

            var numberSubTaskProject = project1!!.numberSubTaskProject

            val newProject = Project(
                idProject = project1.idProject,
                nameProject = project1.nameProject,
                dateProject = project1.dateProject,
                watchProject = project1.watchProject,
                typeProject = project1.typeProject,
                descriptionProject = project1.descriptionProject,
                numberSubTaskProject = numberSubTaskProject!! + 1,
                numberDoneSubTaskProject = project1.numberDoneSubTaskProject,

                year = project1.year,
                month = project1.month,
                day = project1.day

            )
            projectDao.update(newProject)

            val transaction = (activity as ProAndEmpActivity).supportFragmentManager.beginTransaction()
            transaction.replace(R.id.layout_pro_and_emp, ProjectInformationFragment(
                project,
                project1.watchProject!!,
                project1.dateProject!!,
                subTaskProjectDao,
                projectDao,
                position,
                bindingActivityProAndEmp
            ))
                .addToBackStack(null)
                .commit()
            dismiss()
        } else {
            Toast.makeText(context, "لطفا همه مقادیر را وارد کنید", Toast.LENGTH_SHORT).show()
        }
    }

}