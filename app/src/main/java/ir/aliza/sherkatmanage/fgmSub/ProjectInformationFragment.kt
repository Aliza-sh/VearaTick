package ir.aliza.sherkatmanage.fgmSub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ir.aliza.sherkatmanage.DataBase.AppDatabase
import ir.aliza.sherkatmanage.DataBase.Project
import ir.aliza.sherkatmanage.DataBase.ProjectDao
import ir.aliza.sherkatmanage.DataBase.SubTaskProject
import ir.aliza.sherkatmanage.DataBase.SubTaskProjectDao
import ir.aliza.sherkatmanage.Dialog.DeleteSubTaskProjectDialogFragment
import ir.aliza.sherkatmanage.Dialog.SubTaskBottomsheetFragment
import ir.aliza.sherkatmanage.MainActivity
import ir.aliza.sherkatmanage.ProAndEmpActivity
import ir.aliza.sherkatmanage.R
import ir.aliza.sherkatmanage.adapter.SubTaskProjectAdapter
import ir.aliza.sherkatmanage.adapter.TeamProjectAdapter
import ir.aliza.sherkatmanage.databinding.FragmentProjectInformationBinding
import ir.aliza.sherkatmanage.databinding.ItemAvatarBinding

class ProjectInformationFragment(
    val project: Project,
    val day: String,
    val monthName: String,
    val subTaskProjectDao: SubTaskProjectDao,
    val projectDao: ProjectDao
) : Fragment(), SubTaskProjectAdapter.SubTaskEvent {

    lateinit var binding: FragmentProjectInformationBinding
    lateinit var binding1: ItemAvatarBinding
    lateinit var subTaskProjectAdapter: SubTaskProjectAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProjectInformationBinding.inflate(layoutInflater, container, false)
        binding1 = ItemAvatarBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val project1 = projectDao.getProject(project.idProject!!)


        binding.txtNamePro.text = project.nameProject
        binding.txtDescription.text = project.descriptionProject
        binding.txtWatch.text = project.watchProject.toString() + " : 00"
        binding.txtDate.text = day + " " + monthName

        binding.progressPro.progress = project1!!.progressProject!!
        binding.txtProg.text = project1.progressProject!!.toString() + "%"

        binding.btnAddNewPerson.setOnClickListener {
            val transaction = (activity as ProAndEmpActivity).supportFragmentManager.beginTransaction()
            transaction.replace(R.id.layout_pro_and_emp, NewPersonAddToProjectFragment(project))
                .addToBackStack(null)
                .commit()
        }

        val teamProjectDao = AppDatabase.getDataBase(view.context).teamProjectDao
        val teamProjectData = teamProjectDao.getListTeamProject(project.idProject)
        val teamProjectAdapter = TeamProjectAdapter(ArrayList(teamProjectData))
        binding.rcvTeam.adapter = teamProjectAdapter

        binding.txtNumTaskPro.text =
            project1!!.numberDoneSubTaskProject.toString() + " از " + project1!!.numberSubTaskProject.toString()

        val subTaskProjectData = subTaskProjectDao.getSubTaskProject(project.idProject)
        subTaskProjectAdapter =
            SubTaskProjectAdapter(
                ArrayList(subTaskProjectData),
                this,
                project1,
                subTaskProjectDao,
                projectDao,
                binding
            )
        binding.rcvTskPro.adapter = subTaskProjectAdapter

        onFabClicked()

    }

    private fun onFabClicked() {

        binding.btnFabTack.setOnClickListener {
            val bottomsheet = SubTaskBottomsheetFragment(
                subTaskProjectDao,
                project,
                subTaskProjectAdapter,
                projectDao,
                binding
            )
            bottomsheet.show(parentFragmentManager, null)

        }
    }

    override fun onSubTaskClicked(task: SubTaskProject, position: Int) {
    }

    override fun onSubTaskLongClicked(subTask: SubTaskProject, position: Int) {
        val dialog = DeleteSubTaskProjectDialogFragment(
            subTask,
            position,
            subTaskProjectDao,
            subTaskProjectAdapter,
            projectDao,
            project,
            binding
        )
        dialog.show((activity as MainActivity).supportFragmentManager, null)

    }
}