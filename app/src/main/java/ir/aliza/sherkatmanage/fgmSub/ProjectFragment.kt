package ir.aliza.sherkatmanage.fgmSub

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import ir.aliza.sherkatmanage.DataBase.AppDatabase
import ir.aliza.sherkatmanage.DataBase.Project
import ir.aliza.sherkatmanage.DataBase.ProjectDao
import ir.aliza.sherkatmanage.DataBase.SubTaskProjectDao
import ir.aliza.sherkatmanage.MainActivity
import ir.aliza.sherkatmanage.ProAndEmpActivity
import ir.aliza.sherkatmanage.R
import ir.aliza.sherkatmanage.adapter.ProjectNearAdapter
import ir.aliza.sherkatmanage.databinding.ActivityProAndEmpBinding
import ir.aliza.sherkatmanage.databinding.FragmentProjectBinding
import ir.aliza.sherkatmanage.projectAdapter


class ProjectFragment(val bindingActivityProAndEmp: ActivityProAndEmpBinding) : Fragment(), ProjectNearAdapter.ProjectNearEvents {

    lateinit var binding: FragmentProjectBinding
    lateinit var subTaskProjectDao: SubTaskProjectDao
    lateinit var projectDao: ProjectDao
    lateinit var projectNearData: List<Project>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProjectBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBackPressed()

        projectDao = AppDatabase.getDataBase(view.context).projectDao
        projectNearData = projectDao.getAllProject()
        projectAdapter = ProjectNearAdapter(ArrayList(projectNearData), this, projectDao)
        binding.recyclerViewProject.adapter = projectAdapter
        binding.recyclerViewProject.layoutManager = GridLayoutManager(context, 2)

        subTaskProjectDao = AppDatabase.getDataBase(view.context).subTaskEmployeeProjectDao

        onFabClicked()
    }

    fun onBackPressed(){
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(requireContext(), MainActivity::class.java)
                startActivity(intent)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        updateYourData()
    }

    private fun updateYourData() {
        projectNearData = projectDao.getAllProject()
        projectAdapter = ProjectNearAdapter(ArrayList(projectNearData), this, projectDao)
        binding.recyclerViewProject.adapter = projectAdapter
        binding.recyclerViewProject.layoutManager = GridLayoutManager(context, 2)
        subTaskProjectDao = AppDatabase.getDataBase(binding.root.context).subTaskEmployeeProjectDao
    }

    fun onFabClicked() {
        bindingActivityProAndEmp.btnAdd.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.add(R.id.layout_pro_and_emp, NewProjectFragment(projectDao,bindingActivityProAndEmp))
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onProjectClicked(project: Project, position: Int, dateProject: String, watchProject: String) {
        val transaction = (activity as ProAndEmpActivity).supportFragmentManager.beginTransaction()
        transaction.add(R.id.layout_pro_and_emp, ProjectInformationFragment(project,watchProject,dateProject,subTaskProjectDao,
            projectDao,position,bindingActivityProAndEmp))
            .addToBackStack(null)
            .commit()
    }

    override fun onProjectLongClicked(project: Project, position: Int) {}

}