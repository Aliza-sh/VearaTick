package ir.aliza.sherkatmanage.fgmSub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import ir.aliza.sherkatmanage.DataBase.AppDatabase
import ir.aliza.sherkatmanage.DataBase.Project
import ir.aliza.sherkatmanage.Dialog.ProjectDialogFragment
import ir.aliza.sherkatmanage.MainActivity
import ir.aliza.sherkatmanage.R
import ir.aliza.sherkatmanage.adapter.ProjectNearAdapter
import ir.aliza.sherkatmanage.databinding.FragmentProjectBinding
import ir.aliza.sherkatmanage.projectAdapter
import ir.aliza.sherkatmanage.projectDao

class ProjectFragment : Fragment(), ProjectNearAdapter.ProjectNearEvents {

    lateinit var binding: FragmentProjectBinding

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

        projectDao = AppDatabase.getDataBase(view.context).projectDao
        val projectNearData = projectDao.getAllProject()
        projectAdapter = ProjectNearAdapter(ArrayList(projectNearData), this)
        binding.recyclerViewProject.adapter = projectAdapter
        binding.recyclerViewProject.layoutManager = GridLayoutManager(context, 2)

        onFabClicked()
    }

    private fun showAllData() {

    }

    fun onFabClicked() {
        binding.btnFabPro.setOnClickListener {
            val transaction = (activity as MainActivity).supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_layout_main, NewProjectFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onProjectClicked(project: Project) {
        val transaction = (activity as MainActivity).supportFragmentManager.beginTransaction()
        transaction.add(R.id.frame_layout_main, ProjectInformationFragment())
            .addToBackStack(null)
            .commit()
    }

    override fun onProjectLongClicked(project: Project, position: Int) {
        val dialog = ProjectDialogFragment(project, position)
        dialog.show((activity as MainActivity).supportFragmentManager, null)
    }

//    override fun onEmployeeClicked(employee: Employee) {
//        val transaction = (activity as MainActivity).supportFragmentManager.beginTransaction()
//        transatction.replace(R.id.frame_layout_main, EmployeeStatisticsFragment())
//            .addToBackStack(null)
//            .commit()
//
//
//    }
//
//    override fun onEmployeeLongClicked(employee: Employee, position: Int) {
//        val dialog = EmployeeDialogFragment(employee, position)
//        dialog.show(parentFragmentManager, null)
//    }

}