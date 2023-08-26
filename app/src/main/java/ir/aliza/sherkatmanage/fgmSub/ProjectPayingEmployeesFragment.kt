package ir.aliza.sherkatmanage.fgmSub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import ir.aliza.sherkatmanage.DataBase.Project
import ir.aliza.sherkatmanage.DataBase.ProjectDao
import ir.aliza.sherkatmanage.DataBase.SubTaskProject
import ir.aliza.sherkatmanage.DataBase.SubTaskProjectDao
import ir.aliza.sherkatmanage.R
import ir.aliza.sherkatmanage.adapter.SubTaskProjectAdapter
import ir.aliza.sherkatmanage.databinding.ActivityProAndEmpBinding
import ir.aliza.sherkatmanage.databinding.FragmentProjectPayingEmployeeBinding

class ProjectPayingEmployeesFragment(
    var project: Project,
    val projectDao: ProjectDao,
    val position: Int,
    val bindingActivityProAndEmp: ActivityProAndEmpBinding,
    val subTaskProjectDao: SubTaskProjectDao
) : Fragment(), SubTaskProjectAdapter.SubTaskEvent {

    lateinit var binding: FragmentProjectPayingEmployeeBinding
    lateinit var subTaskProjectAdapter: SubTaskProjectAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProjectPayingEmployeeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBackPressed()

        project = projectDao.getProject(project.idProject!!)!!

        binding.btnBck.setOnClickListener {
            parentFragmentManager.beginTransaction().detach(this@ProjectPayingEmployeesFragment)
                .replace(R.id.layout_pro_and_emp, ProjectInformationFragment(project,
                    project.watchDeadlineProject!!,project.dateDeadlineProject!!,subTaskProjectDao,projectDao,position,bindingActivityProAndEmp)).commit()
        }

        val subTaskProjectData = subTaskProjectDao.getSubTaskProject(project.idProject!!)
        subTaskProjectAdapter =
            SubTaskProjectAdapter(
                ArrayList(subTaskProjectData),
                this,
                project,
                projectDao,
                subTaskProjectDao,
            )
        binding.recyclerView.adapter = subTaskProjectAdapter

    }

    private fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    parentFragmentManager.beginTransaction().detach(this@ProjectPayingEmployeesFragment)
                        .replace(R.id.frame_layout_sub, ProjectFragment(bindingActivityProAndEmp))
                        .commit()
                }
            })
    }

    override fun onResume() {
        super.onResume()
        updateYourData()
    }

    private fun updateYourData() {
        project = projectDao.getProject(project.idProject!!)!!
    }

    override fun onSubTaskClicked(task: SubTaskProject, position: Int) {
        TODO("Not yet implemented")
    }

    override fun onSubTaskLongClicked(subTask: SubTaskProject, position: Int) {
        TODO("Not yet implemented")
    }
}