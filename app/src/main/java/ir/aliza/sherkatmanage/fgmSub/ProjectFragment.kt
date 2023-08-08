package ir.aliza.sherkatmanage.fgmSub

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import ir.aliza.sherkatmanage.DataBase.AppDatabase
import ir.aliza.sherkatmanage.DataBase.Project
import ir.aliza.sherkatmanage.DataBase.SubTaskProjectDao
import ir.aliza.sherkatmanage.Dialog.ProjectDialogFragment
import ir.aliza.sherkatmanage.ProAndEmpActivity
import ir.aliza.sherkatmanage.R
import ir.aliza.sherkatmanage.adapter.ProjectNearAdapter
import ir.aliza.sherkatmanage.databinding.ActivityProAndEmpBinding
import ir.aliza.sherkatmanage.databinding.FragmentProjectBinding
import ir.aliza.sherkatmanage.projectAdapter
import ir.aliza.sherkatmanage.projectDao


class ProjectFragment(val bindingActivityProAndEmp: ActivityProAndEmpBinding) : Fragment(), ProjectNearAdapter.ProjectNearEvents {

    lateinit var binding: FragmentProjectBinding
    lateinit var subTaskProjectDao: SubTaskProjectDao

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

        val ft = (activity as ProAndEmpActivity).supportFragmentManager.beginTransaction()
        if (Build.VERSION.SDK_INT >= 26) {
            ft.setReorderingAllowed(false)
        }
        ft.detach(this).attach(this).commit()

        projectDao = AppDatabase.getDataBase(view.context).projectDao
        val projectNearData = projectDao.getAllProject()
        projectAdapter = ProjectNearAdapter(ArrayList(projectNearData), this, projectDao)
        binding.recyclerViewProject.adapter = projectAdapter
        binding.recyclerViewProject.layoutManager = GridLayoutManager(context, 2)

        subTaskProjectDao = AppDatabase.getDataBase(view.context).subTaskEmployeeProjectDao

        onFabClicked()

    }

    private fun showAllData() {

    }

    fun onFabClicked() {
        bindingActivityProAndEmp.btnAdd.setOnClickListener {
            val transaction = (activity as ProAndEmpActivity).supportFragmentManager.beginTransaction()
            transaction.replace(R.id.layout_pro_and_emp, NewProjectFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onProjectClicked(project: Project,position: Int, day: String, monthName: String) {
        val transaction = (activity as ProAndEmpActivity).supportFragmentManager.beginTransaction()
        transaction.add(R.id.layout_pro_and_emp, ProjectInformationFragment(project,day,monthName,subTaskProjectDao,
            projectDao,position))
            .addToBackStack(null)
            .commit()
    }

    override fun onProjectLongClicked(project: Project, position: Int) {
        val dialog = ProjectDialogFragment(project, position)
        dialog.show((activity as ProAndEmpActivity).supportFragmentManager, null)
    }

}