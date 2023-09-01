package ir.aliza.sherkatmanage.fgmSub

import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.LinearInterpolator
import android.widget.PopupMenu
import androidx.activity.OnBackPressedCallback
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ir.aliza.sherkatmanage.DataBase.Project
import ir.aliza.sherkatmanage.DataBase.ProjectDao
import ir.aliza.sherkatmanage.DataBase.SubTaskProject
import ir.aliza.sherkatmanage.DataBase.SubTaskProjectDao
import ir.aliza.sherkatmanage.Dialog.ProjectAddNewSubTaskBottomsheetFragment
import ir.aliza.sherkatmanage.Dialog.ProjectUpdateSubTaskFromSubTaskBottomsheetFragment
import ir.aliza.sherkatmanage.R
import ir.aliza.sherkatmanage.adapter.SubTaskProjectAdapter
import ir.aliza.sherkatmanage.databinding.ActivityProAndEmpBinding
import ir.aliza.sherkatmanage.databinding.FragmentDialogDeleteSubtaskProjectBinding
import ir.aliza.sherkatmanage.databinding.FragmentProjectSubTaskBinding
import ir.aliza.sherkatmanage.databinding.ItemSubTaskBinding

class ProjectSubTaskFragment(
    var project: Project,
    val projectDao: ProjectDao,
    val position: Int,
    val bindingActivityProAndEmp: ActivityProAndEmpBinding,
    val subTaskProjectDao: SubTaskProjectDao
) : Fragment(), SubTaskProjectAdapter.SubTaskEvent {

    lateinit var binding: FragmentProjectSubTaskBinding
    lateinit var bindingItemSubTask: ItemSubTaskBinding
    lateinit var bindingDialogDeleteSubtaskProject: FragmentDialogDeleteSubtaskProjectBinding
    lateinit var subTaskProjectAdapter: SubTaskProjectAdapter
    private var isScrollingUp = false
    private var buttonAnimator: ObjectAnimator? = null
    var numberDonSubTaskProject = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProjectSubTaskBinding.inflate(layoutInflater, container, false)
        bindingItemSubTask = ItemSubTaskBinding.inflate(layoutInflater, container, false)
        bindingDialogDeleteSubtaskProject =
            FragmentDialogDeleteSubtaskProjectBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBackPressed()
        btnOnScroll()
        setData()

        binding.btnBck.setOnClickListener {
            parentFragmentManager.beginTransaction().detach(this@ProjectSubTaskFragment)
                .replace(
                    R.id.layout_pro_and_emp, ProjectInformationFragment(
                        project,
                        project.watchDeadlineProject!!,
                        project.dateDeadlineProject!!,
                        subTaskProjectDao,
                        projectDao,
                        position,
                        bindingActivityProAndEmp
                    )
                ).commit()
        }

        binding.btnFabTack.setOnClickListener {
            addNewTask()
        }

    }
    private fun setData() {
        project = projectDao.getProject(project.idProject!!)!!

        val subTaskProjectData = subTaskProjectDao.getSubTaskProject(project.idProject!!)
        subTaskProjectAdapter =
            SubTaskProjectAdapter(
                ArrayList(subTaskProjectData),
                this,
                project,
                projectDao,
                subTaskProjectDao
            )
        binding.recyclerView.adapter = subTaskProjectAdapter

    }
    private fun addNewTask() {
        val bottomsheet = ProjectAddNewSubTaskBottomsheetFragment(
            subTaskProjectDao,
            project,
            subTaskProjectAdapter,
            projectDao,
            position,
            bindingActivityProAndEmp
        )
        bottomsheet.setStyle(R.style.BottomSheetStyle, R.style.BottomSheetDialogTheme)
        bottomsheet.show(parentFragmentManager, null)
    }
    private fun btnOnScroll() {
        val layoutManager = LinearLayoutManager(context)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (isScrollingUp) {
                        animateButtonUp()
                    } else {
                        animateButtonDown()
                    }
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                isScrollingUp = dy < 0
            }
        })
    }
    private fun animateButtonDown() {
        if (binding.btnFabTack.translationY != binding.btnFabTack.height.toFloat()) {
            buttonAnimator?.cancel()
            buttonAnimator = ObjectAnimator.ofFloat(
                binding.btnFabTack,
                "translationY",
                binding.btnFabTack.height.toFloat()
            )
            buttonAnimator?.apply {
                duration = 200
                interpolator = LinearInterpolator()
                start()
            }
        }
    }
    private fun animateButtonUp() {
        if (binding.btnFabTack.translationY != 0f) {
            buttonAnimator?.cancel()
            buttonAnimator = ObjectAnimator.ofFloat(binding.btnFabTack, "translationY", 0f)
            buttonAnimator?.apply {
                duration = 200
                interpolator = LinearInterpolator()
                start()
            }
        }
    }
    private fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    parentFragmentManager
                        .beginTransaction()
                        .detach(this@ProjectSubTaskFragment)
                        .replace(
                            R.id.layout_pro_and_emp, ProjectInformationFragment(
                                project,
                                project.watchDeadlineProject!!,
                                project.dateDeadlineProject!!,
                                subTaskProjectDao,
                                projectDao,
                                position,
                                bindingActivityProAndEmp
                            )
                        )
                        .commit()
                }
            }
        )
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
    override fun onMenuItemClick(subTask: SubTaskProject, position: Int) {

        val onClickSubTask = subTaskProjectDao.getOnClickSubTaskProject(subTask.idSubTask!!)

        val viewHolder =
            binding.recyclerView.findViewHolderForAdapterPosition(position) as SubTaskProjectAdapter.SubTaskProjectViewHolder
        viewHolder.let { holder ->
            val btnMenuSubTaskProject = holder.btnMenuSubTaskProject
            val popupMenu = PopupMenu(context, btnMenuSubTaskProject)
            popupMenu.inflate(R.menu.menu_project)
            popupMenu.show()

            val doneMenuItem = popupMenu.menu.findItem(R.id.menu_project_done)
            if (onClickSubTask!!.doneSubTask!!) {
                doneMenuItem.title = "تکمیل نشد"
            } else {
                doneMenuItem.title = "تکمیل شد"
            }

            popupMenu.setOnMenuItemClickListener { item ->

                when (item.itemId) {

                    R.id.menu_project_edit -> {
                        val bottomsheet = ProjectUpdateSubTaskFromSubTaskBottomsheetFragment(
                            subTaskProjectDao,
                            project,
                            subTaskProjectAdapter,
                            projectDao,
                            position,
                            bindingActivityProAndEmp,
                            subTask
                        )
                        bottomsheet.setStyle(R.style.BottomSheetStyle, R.style.BottomSheetDialogTheme)
                        bottomsheet.show(parentFragmentManager, null)
                        true
                    }

                    R.id.menu_project_done -> {
                        doneSubTask(onClickSubTask, doneMenuItem)
                        setData()
                        true
                    }

                    R.id.menu_project_delete -> {
                        showDeleteDialog(onClickSubTask)
                        true
                    }

                    else -> false
                }
            }
        }
    }
    override fun onTeamSubTaskClick(subTask: SubTaskProject, project: Project, position: Int) {
        parentFragmentManager.beginTransaction().detach(this@ProjectSubTaskFragment)
            .replace(
                R.id.layout_pro_and_emp,
                ProjectSubTaskAddNewTeamFromSubTaskFragment(
                    project,
                    subTask,
                    position,
                    bindingActivityProAndEmp,
                    subTaskProjectDao,
                    projectDao
                )
            )
            .commit()
    }
    private fun doneSubTask(onClickSubTask: SubTaskProject, doneMenuItem: MenuItem) {
        if (onClickSubTask.doneSubTask!!) {

            doneMenuItem.title = "تکمیل شد"

            bindingItemSubTask.txtDedlineSubTask.visibility = View.VISIBLE
            bindingItemSubTask.imgDone.visibility = View.GONE

            val newSubTask = SubTaskProject (
                idSubTask = onClickSubTask.idSubTask,
                idProject = onClickSubTask.idProject,
                nameSubTask = onClickSubTask.nameSubTask,
                noDeadlineSubTask = onClickSubTask.noDeadlineSubTask,
                doneSubTask = false,
                descriptionSubTask = onClickSubTask.descriptionSubTask,
                watchDeadlineSubTask = onClickSubTask.watchDeadlineSubTask,
                dateDeadlineSubTask = onClickSubTask.dateDeadlineSubTask,
                volumeTask = onClickSubTask.volumeTask
            )
            subTaskProjectDao.update(newSubTask)

            val project1 = projectDao.getProject(project.idProject!!)
            numberDonSubTaskProject = project1!!.numberDoneSubTaskProject!!
            numberDonSubTaskProject--
            val numSubTask = project1.numberSubTaskProject
            var efficiencyProject = 0

            if (numSubTask != null) {
                efficiencyProject =
                    ((numberDonSubTaskProject.toDouble() / numSubTask) * 100).toInt()
            }

            val newProject = Project(
                idProject = project.idProject,
                nameProject = project.nameProject,
                dateDeadlineProject = project.dateDeadlineProject,
                watchDeadlineProject = project.watchDeadlineProject,
                typeProject = project.typeProject,
                descriptionProject = project.descriptionProject,
                noDeadlineProject = project.noDeadlineProject,
                numberSubTaskProject = project1.numberSubTaskProject,
                numberDoneSubTaskProject = numberDonSubTaskProject,
                progressProject = efficiencyProject,
                budgetProject = project.budgetProject
            )
            projectDao.update(newProject)

        } else {

            doneMenuItem.title = "تکمیل نشد"

            bindingItemSubTask.txtDedlineSubTask.visibility = View.GONE
            bindingItemSubTask.imgDone.visibility = View.VISIBLE

            val newSubTask = SubTaskProject(
                idSubTask = onClickSubTask.idSubTask,
                idProject = onClickSubTask.idProject,
                nameSubTask = onClickSubTask.nameSubTask,
                noDeadlineSubTask = onClickSubTask.noDeadlineSubTask,
                doneSubTask = true,
                descriptionSubTask = onClickSubTask.descriptionSubTask,
                watchDeadlineSubTask = onClickSubTask.watchDeadlineSubTask,
                dateDeadlineSubTask = onClickSubTask.dateDeadlineSubTask,
                volumeTask = onClickSubTask.volumeTask
            )
            subTaskProjectDao.update(newSubTask)

            val project1 = projectDao.getProject(project.idProject!!)
            numberDonSubTaskProject = project1!!.numberDoneSubTaskProject!!
            numberDonSubTaskProject++
            val numSubTask = project1.numberSubTaskProject
            var efficiencyProject = 0

            if (numSubTask != null)
                efficiencyProject = ((numberDonSubTaskProject.toDouble() / numSubTask) * 100).toInt()

            val newProject = Project(
                idProject = project.idProject,
                nameProject = project.nameProject,
                dateDeadlineProject = project.dateDeadlineProject,
                watchDeadlineProject = project.watchDeadlineProject,
                typeProject = project.typeProject,
                noDeadlineProject = project.noDeadlineProject,
                descriptionProject = project.descriptionProject,
                numberSubTaskProject = project1.numberSubTaskProject,
                numberDoneSubTaskProject = numberDonSubTaskProject,
                progressProject = efficiencyProject,
                budgetProject = project.budgetProject
            )
            projectDao.update(newProject)
        }
    }
    private fun showDeleteDialog(onClickSubTask: SubTaskProject) {

        val parent = bindingDialogDeleteSubtaskProject.root.parent as? ViewGroup
        parent?.removeView(bindingDialogDeleteSubtaskProject.root)
        val dialogBuilder = AlertDialog.Builder(bindingDialogDeleteSubtaskProject.root.context)
        dialogBuilder.setView(bindingDialogDeleteSubtaskProject.root)

        val alertDialog = dialogBuilder.create()
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.Transparent.toArgb()))
        alertDialog.setCancelable(false)
        alertDialog.show()
        bindingDialogDeleteSubtaskProject.dialogBtnDeleteCansel.setOnClickListener {
            alertDialog.dismiss()
        }
        bindingDialogDeleteSubtaskProject.dialogBtnDeleteSure.setOnClickListener {

            deleteItem(onClickSubTask, position)
            setData()
            alertDialog.dismiss()
        }
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
            dateDeadlineProject = project1.dateDeadlineProject,
            watchDeadlineProject = project1.watchDeadlineProject,
            typeProject = project1.typeProject,
            descriptionProject = project1.descriptionProject,
            numberSubTaskProject = numberSubTaskProject,
            numberDoneSubTaskProject = numberDonSubTaskProject,
            noDeadlineProject = project.noDeadlineProject,
            progressProject = efficiencyProject,
            budgetProject = project.budgetProject

        )
        projectDao.update(newProject)
    }
}