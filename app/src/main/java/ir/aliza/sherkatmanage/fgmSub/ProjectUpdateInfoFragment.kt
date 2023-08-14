package ir.aliza.sherkatmanage.fgmSub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import ir.aliza.sherkatmanage.DataBase.Project
import ir.aliza.sherkatmanage.DataBase.ProjectDao
import ir.aliza.sherkatmanage.DataBase.SubTaskProjectDao
import ir.aliza.sherkatmanage.R
import ir.aliza.sherkatmanage.databinding.ActivityProAndEmpBinding
import ir.aliza.sherkatmanage.databinding.FragmentProjectUpdateInfoBinding
import ir.aliza.sherkatmanage.projectAdapter

class ProjectUpdateInfoFragment(
    val project: Project,
    val position: Int,
    val projectDao: ProjectDao,
    val day: String,
    val monthName: String,
    val subTaskProjectDao: SubTaskProjectDao,
    val bindingActivityProAndEmp: ActivityProAndEmpBinding,
) : Fragment() {

    lateinit var binding: FragmentProjectUpdateInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProjectUpdateInfoBinding.inflate(layoutInflater, null, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBackPressed()

        val typeProject = listOf(
            "اندروید",
            "بک اند",
            "فرانت اند",
            "رباتیک",
            "طراحی",
            "سئو"
        )

        val myAdapteredt = ArrayAdapter(requireContext(), R.layout.item_gender, typeProject)
        (binding.dialogMainEdtGdrperson.editText as AutoCompleteTextView).setAdapter(
            myAdapteredt
        )

        setdata(project)

        binding.sheetBtnDone.setOnClickListener {
            addNewProject()
            onProjectInfoUpdate()
        }

        binding.btnBck.setOnClickListener {
            if (parentFragmentManager.backStackEntryCount > 0) {
                parentFragmentManager.popBackStack()
            }
        }
    }

    fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    parentFragmentManager.beginTransaction().detach(this@ProjectUpdateInfoFragment)
                        .attach(
                            ProjectInformationFragment(
                                project,
                                day,
                                monthName,
                                subTaskProjectDao,
                                projectDao,
                                position,
                                bindingActivityProAndEmp
                            )
                        ).commit()
                }
            })
    }

    fun onProjectInfoUpdate() {
        parentFragmentManager.beginTransaction().detach(this@ProjectUpdateInfoFragment)
            .replace(
                R.id.layout_pro_and_emp,
                ProjectInformationFragment(
                    project,
                    day,
                    monthName,
                    subTaskProjectDao,
                    projectDao,
                    position,
                    bindingActivityProAndEmp
                )
            ).commit()
    }

    private fun setdata(project: Project) {

        binding.edtNamePro.setText(project.nameProject)
        binding.edtDayProject.setText(project.dayProject.toString())
        binding.edtTimePro.setText(project.watchProject.toString())
        binding.edtTypeProject.setText(project.typeProject)
        binding.edtDescriptionPro.setText(project.descriptionProject)

    }

    private fun addNewProject() {
        if (
            binding.edtNamePro.length() > 0 &&
            binding.edtDayProject.length() > 0 &&
            binding.edtTimePro.length() > 0 &&
            binding.edtTypeProject.length() > 0 &&
            binding.edtDescriptionPro.length() > 0
        ) {
            val txtNamePro = binding.edtNamePro.text.toString()
            val txtDayProject = binding.edtDayProject.text.toString()
            val txtTime = binding.edtTimePro.text.toString()
            val txtTypeProject = binding.edtTypeProject.text.toString()
            val txtDescriptionPro = binding.edtDescriptionPro.text.toString()

            val newProject = Project(
                idProject = project.idProject,
                nameProject = txtNamePro,
                descriptionProject= txtDescriptionPro,
                watchProject = txtTime.toInt(),
                dayProject = txtDayProject.toInt(),
                typeProject = txtTypeProject,
                progressProject = project.progressProject,
                doneProject = project.doneProject,
                numberSubTaskProject = project.numberSubTaskProject,
                numberDoneSubTaskProject = project.numberDoneSubTaskProject,
                year = project.year,
                month = project.month,
                day = project.day,
            )

            projectAdapter.updateProject(newProject, position)
            projectDao.update(newProject)

        } else {
            Toast.makeText(context, "لطفا همه مقادیر را وارد کنید", Toast.LENGTH_SHORT).show()
        }
    }
}