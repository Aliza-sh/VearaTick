package ir.aliza.sherkatmanage.fgmSub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import ir.aliza.sherkatmanage.DataBase.Project
import ir.aliza.sherkatmanage.DataBase.ProjectDao
import ir.aliza.sherkatmanage.DataBase.SubTaskProject
import ir.aliza.sherkatmanage.DataBase.SubTaskProjectDao
import ir.aliza.sherkatmanage.Dialog.DeleteSubTaskProjectDialogFragment
import ir.aliza.sherkatmanage.Dialog.ProjectDeleteDialogFragment
import ir.aliza.sherkatmanage.MainActivity
import ir.aliza.sherkatmanage.ProAndEmpActivity
import ir.aliza.sherkatmanage.R
import ir.aliza.sherkatmanage.adapter.SubTaskProjectAdapter
import ir.aliza.sherkatmanage.databinding.ActivityProAndEmpBinding
import ir.aliza.sherkatmanage.databinding.FragmentProjectInformationBinding

class ProjectInformationFragment(
    var project: Project,
    val watchProject: String,
    val dateProject: String,
    val subTaskProjectDao: SubTaskProjectDao,
    val projectDao: ProjectDao,
    val position: Int,
    val bindingActivityProAndEmp: ActivityProAndEmpBinding
) : Fragment(), SubTaskProjectAdapter.SubTaskEvent {

    lateinit var binding: FragmentProjectInformationBinding
    lateinit var subTaskProjectAdapter: SubTaskProjectAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProjectInformationBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBackPressed()

        project = projectDao.getProject(project.idProject!!)!!

        binding.txtNamePro.text = project.nameProject
        binding.txtDescription.text = project.descriptionProject

        val word = "تومان"
        val containsWord = project.budgetProject!!.contains(word, ignoreCase = true)
        if (containsWord) {
            binding.txtBudget.text = project.budgetProject

        } else {
            binding.txtBudget.visibility = View.GONE
            binding.btnGoToSettlement.visibility = View.GONE
            binding.txtSettlement.visibility = View.GONE
            binding.txtNoBudget.text = "پروژه بودجه \nندارد"
        }

        if (!project.noDeadlineProject!!) {

            if (project.dateProject != "" && project.watchProject!= ""){
                binding.txtWatch.text = watchProject
                binding.txtDate.text = dateProject
            }else if (project.dateProject != "" && project.watchProject == ""){
                binding.txtWatch.text = " ندارد"
                binding.txtDate.text = dateProject
            }else if (project.dateProject == "" && project.watchProject != ""){
                binding.txtWatch.text = watchProject
                binding.txtDate.text = " امروز"
            }

        } else {

            binding.imageView4.visibility = View.GONE
            binding.imageView3.visibility = View.GONE
            binding.txtDate.visibility = View.GONE
            binding.txtWatch.visibility = View.GONE
            binding.txtNoDeadline.text = "پروژه ددلاین \nندارد"

        }

        binding.btnBck.setOnClickListener {
            parentFragmentManager.beginTransaction().detach(this@ProjectInformationFragment)
                .replace(R.id.frame_layout_sub, ProjectFragment(bindingActivityProAndEmp)).commit()
        }

        binding.progressPro.progress = project.progressProject!!
        binding.txtProg.text = project.progressProject.toString() + "%"

//        binding.btnAddNewPerson.setOnClickListener {
//            val transaction =
//                (activity as ProAndEmpActivity).supportFragmentManager.beginTransaction()
//            transaction.replace(R.id.layout_pro_and_emp, NewPersonAddToProjectFragment(project))
//                .addToBackStack(null)
//                .commit()
//        }
//
//        if (project.idProject != null) {
//
//            val teamProjectDao = AppDatabase.getDataBase(view.context).teamProjectDao
//            val teamProjectData = teamProjectDao.getListTeamProject(project.idProject!!)
//            val teamProjectAdapter = TeamProjectAdapter(ArrayList(teamProjectData))
//            binding.rcvTeam.adapter = teamProjectAdapter
//
//            val subTaskProjectData = subTaskProjectDao.getSubTaskProject(project.idProject!!)
//            subTaskProjectAdapter =
//                SubTaskProjectAdapter(
//                    ArrayList(subTaskProjectData),
//                    this,
//                    project,
//                    subTaskProjectDao,
//                    projectDao,
//                    binding
//                )
//            binding.rcvTskPro.adapter = subTaskProjectAdapter

//        }

        binding.txtNumTaskPro.text =
            project!!.numberDoneSubTaskProject.toString() + " از " + project!!.numberSubTaskProject.toString()

        onFabClicked(project)

        val popupMenu = PopupMenu(this.context, binding.btnMenuProject)
        onMenuClicked(popupMenu)

    }

    private fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    parentFragmentManager.beginTransaction().detach(this@ProjectInformationFragment)
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

    private fun onMenuClicked(popupMenu: PopupMenu) {

        popupMenu.menuInflater.inflate(R.menu.menu_project, popupMenu.menu)
        binding.btnMenuProject.setOnClickListener {
            popupMenu.show()
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_project_edit -> {
                        val transaction =
                            (activity as ProAndEmpActivity).supportFragmentManager.beginTransaction()
                        transaction.replace(
                            R.id.layout_pro_and_emp,
                            ProjectUpdateInfoFragment(
                                project,
                                position,
                                projectDao,
                                watchProject,
                                dateProject,
                                subTaskProjectDao,
                                bindingActivityProAndEmp
                            )
                        )
                            .addToBackStack(null)
                            .commit()

                    }

                    R.id.menu_project_done -> {

                    }

                    R.id.menu_project_delete -> {

                        val dialog = ProjectDeleteDialogFragment(
                            project,
                            position,
                            projectDao,
                            bindingActivityProAndEmp,
                            this
                        )
                        dialog.show((activity as ProAndEmpActivity).supportFragmentManager, null)

                    }
                }
                true
            }
        }
    }

    private fun onFabClicked(project: Project) {

//        binding.btnFabTack.setOnClickListener {
//            val bottomsheet = SubTaskBottomsheetFragment(
//                subTaskProjectDao,
//                project,
//                subTaskProjectAdapter,
//                projectDao,
//                position,
//                bindingActivityProAndEmp,
//                this,
//                watchProject,
//                dateProject
//            )
//            bottomsheet.setStyle(R.style.BottomSheetStyle, R.style.BottomSheetDialogTheme)
//            bottomsheet.show((activity as MainActivity).supportFragmentManager, null)
//
//        }
    }

    override fun onSubTaskClicked(task: SubTaskProject, position: Int) {}

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