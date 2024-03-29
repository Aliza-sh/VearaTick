package com.vearad.vearatick.ui.fragmentssub

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
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kizitonwose.calendarview.utils.persian.PersianCalendar
import com.vearad.vearatick.R
import com.vearad.vearatick.adapter.SubTaskProjectAdapter
import com.vearad.vearatick.databinding.ActivityProAndEmpBinding
import com.vearad.vearatick.databinding.FragmentDialogDeleteSubtaskProjectBinding
import com.vearad.vearatick.databinding.FragmentProjectSubTaskBinding
import com.vearad.vearatick.databinding.ItemSubTaskBinding
import com.vearad.vearatick.model.db.AppDatabase
import com.vearad.vearatick.model.db.EfficiencyDao
import com.vearad.vearatick.model.db.Project
import com.vearad.vearatick.model.db.ProjectDao
import com.vearad.vearatick.model.db.SubTaskProject
import com.vearad.vearatick.model.db.SubTaskProjectDao
import com.vearad.vearatick.model.db.TaskEmployee
import com.vearad.vearatick.model.db.TeamSubTaskDao
import com.vearad.vearatick.ui.dialog.ProjectAddNewSubTaskBottomsheetFragment
import com.vearad.vearatick.ui.dialog.ProjectUpdateSubTaskFromSubTaskBottomsheetFragment
import org.joda.time.DateTime
import org.joda.time.Days

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
    lateinit var efficiencyEmployeeDao: EfficiencyDao
    lateinit var teamSubTaskDao: TeamSubTaskDao
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

        efficiencyEmployeeDao =
            AppDatabase.getDataBase(bindingItemSubTask.root.context).efficiencyDao
        teamSubTaskDao = AppDatabase.getDataBase(bindingItemSubTask.root.context).teamSubTaskDao

        binding.btnBck.setOnClickListener {
            parentFragmentManager.beginTransaction().detach(this@ProjectSubTaskFragment)
                .replace(
                    R.id.layout_pro_and_emp, ProjectInformationFragment(
                        project,
                        subTaskProjectDao,
                        projectDao,
                        position,
                        bindingActivityProAndEmp, false
                    )
                ).commit()
        }

        binding.btnFabTack.setOnClickListener {
            if (project.doneProject!=true)
            addNewTask()
            else
                Toast.makeText(view.context, "پروژه تکمیل شده است.", Toast.LENGTH_SHORT).show()
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

                isScrollingUp = dy <= 0
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
                                subTaskProjectDao,
                                projectDao,
                                position,
                                bindingActivityProAndEmp,
                                false)
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

    override fun onSubTaskClicked(task: SubTaskProject, position: Int) {}

    override fun onSubTaskLongClicked(subTask: SubTaskProject, position: Int) {}

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
                        bottomsheet.setStyle(
                            R.style.BottomSheetStyle,
                            R.style.BottomSheetDialogTheme
                        )
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
        val today = PersianCalendar()

        if (onClickSubTask.doneSubTask!!) {

            doneMenuItem.title = "تکمیل شد"

            bindingItemSubTask.txtDedlineSubTask.visibility = View.VISIBLE
            bindingItemSubTask.imgDone.visibility = View.GONE

            val newSubTask = SubTaskProject(
                idSubTask = onClickSubTask.idSubTask,
                idProject = onClickSubTask.idProject,
                nameSubTask = onClickSubTask.nameSubTask,
                doneSubTask = false,
                descriptionSubTask = onClickSubTask.descriptionSubTask,
                dayCreation = onClickSubTask.dayCreation,
                monthCreation = onClickSubTask.monthCreation,
                yearCreation = onClickSubTask.yearCreation,
                dayDeadline = onClickSubTask.dayDeadline,
                monthDeadline = onClickSubTask.monthDeadline,
                yearDeadline = onClickSubTask.yearDeadline,
                valueCalendar = onClickSubTask.valueCalendar,
                volumeTask = onClickSubTask.volumeTask,
                deadlineTask = onClickSubTask.deadlineTask,
                dayDone = 0,
                monthDone = 0,
                yearDone = 0
            )

            val project1 = projectDao.getProject(project.idProject!!)
            var numberDonSubTaskProject = project1!!.numberDoneSubTaskProject!!
            numberDonSubTaskProject -= 1

            val newProject = Project(
                idProject = project.idProject,
                nameProject = project.nameProject,
                noDeadlineProject = project.noDeadlineProject,
                dayCreation = project.dayCreation,
                monthCreation = project.monthCreation,
                yearCreation = project.yearCreation,
                typeProject = project.typeProject,
                valueCalendar = project.valueCalendar,
                deadlineTask = project.deadlineTask,
                doneProject = project.doneProject,
                descriptionProject = project.descriptionProject,
                numberSubTaskProject = project1.numberSubTaskProject,
                numberDoneSubTaskProject = numberDonSubTaskProject--,
                progressProject = project1.progressProject,
                budgetProject = project.budgetProject,
                totalVolumeProject = project1.totalVolumeProject,
                doneVolumeProject = project1.doneVolumeProject,
                settled = project.settled,
                urlProject = project1.urlProject
            )
            projectDao.update(newProject)

            val taskEmployeeDao = AppDatabase.getDataBase(binding.root.context).taskDao
            val employeeSubTaskProjects = teamSubTaskDao.getListTeamSubTask(
                project1.idProject!!,
                onClickSubTask.idSubTask!!
            )

            if (employeeSubTaskProjects.isNotEmpty()) {

                for (employeeSubTaskProject in employeeSubTaskProjects) {

                    val onClickTask = taskEmployeeDao.getEmployeeSTaskSProject(
                        employeeSubTaskProject.idEmployee!!,employeeSubTaskProject.idSubTask)

                    val newTask = TaskEmployee(
                        idTask = onClickTask!!.idTask,
                        idEmployee = onClickTask.idEmployee,
                        idTaskProject = onClickTask.idTaskProject,
                        nameTask = onClickTask.nameTask,
                        descriptionTask = onClickTask.descriptionTask,
                        volumeTask = onClickTask.volumeTask,
                        doneTask = false,
                        deadlineTask = onClickTask.deadlineTask,
                        yearDeadline = onClickTask.yearDeadline,
                        monthDeadline = onClickTask.monthDeadline,
                        dayDeadline = onClickTask.dayDeadline,
                        efficiencyTask = 0,
                        projectTask = onClickTask.projectTask,
                        dayCreation = onClickTask.dayCreation,
                        monthCreation = onClickTask.monthCreation,
                        yearCreation = onClickTask.yearCreation,
                        dayDone = 0,
                        monthDone = 0,
                        yearDone = 0
                    )
                    taskEmployeeDao.update(newTask)
                }
            }
            subTaskProjectDao.update(newSubTask)

        } else {

            doneMenuItem.title = "تکمیل نشد"

            bindingItemSubTask.txtDedlineSubTask.visibility = View.GONE
            bindingItemSubTask.imgDone.visibility = View.VISIBLE

            val newSubTask = SubTaskProject(
                idSubTask = onClickSubTask.idSubTask,
                idProject = onClickSubTask.idProject,
                nameSubTask = onClickSubTask.nameSubTask,
                doneSubTask = true,
                descriptionSubTask = onClickSubTask.descriptionSubTask,
                dayCreation = onClickSubTask.dayCreation,
                monthCreation = onClickSubTask.monthCreation,
                yearCreation = onClickSubTask.yearCreation,
                dayDeadline = onClickSubTask.dayDeadline,
                monthDeadline = onClickSubTask.monthDeadline,
                yearDeadline = onClickSubTask.yearDeadline,
                valueCalendar = onClickSubTask.valueCalendar,
                volumeTask = onClickSubTask.volumeTask,
                deadlineTask = onClickSubTask.deadlineTask,
                dayDone = today.persianDay,
                monthDone = today.persianMonth,
                yearDone = today.persianYear
            )
            subTaskProjectDao.update(newSubTask)

            val project1 = projectDao.getProject(project.idProject!!)
            var numberDonSubTaskProject = project1!!.numberDoneSubTaskProject!!
            numberDonSubTaskProject += 1

            val newProject = Project(
                idProject = project.idProject,
                nameProject = project.nameProject,
                noDeadlineProject = project.noDeadlineProject,
                dayCreation = project.dayCreation,
                monthCreation = project.monthCreation,
                yearCreation = project.yearCreation,
                typeProject = project.typeProject,
                valueCalendar = project.valueCalendar,
                deadlineTask = project.deadlineTask,
                doneProject = project.doneProject,
                descriptionProject = project.descriptionProject,
                numberSubTaskProject = project1.numberSubTaskProject,
                numberDoneSubTaskProject = numberDonSubTaskProject--,
                progressProject = project1.progressProject,
                budgetProject = project.budgetProject,
                totalVolumeProject = project1.totalVolumeProject,
                doneVolumeProject = project1.doneVolumeProject,
                settled = project.settled,
                urlProject = project1.urlProject
            )
            projectDao.update(newProject)


            val taskEmployeeDao = AppDatabase.getDataBase(binding.root.context).taskDao
            val employeeSubTaskProjects = teamSubTaskDao.getListTeamSubTask(
                project1.idProject!!,
                onClickSubTask.idSubTask!!
            )

            if (employeeSubTaskProjects.isNotEmpty()) {

                for (employeeSubTaskProject in employeeSubTaskProjects) {

                    val onClickTask = taskEmployeeDao.getEmployeeSTaskSProject(
                        employeeSubTaskProject.idEmployee!!,employeeSubTaskProject.idSubTask)
                    val startDate =
                        DateTime(
                            today.persianYear,
                            today.persianMonth +1,
                            today.persianDay,
                            0,
                            0,
                            0
                        )
                    val endDate = DateTime(
                        onClickSubTask.yearCreation,
                        onClickSubTask.monthCreation+1,
                        onClickSubTask.dayCreation,
                        0,
                        0,
                        0
                    )
                    val daysBetween = Days.daysBetween(startDate, endDate).days
                    var efficiencyWeekDuties = 0
                    val deadlineTask = onClickSubTask.deadlineTask

                    if (deadlineTask == 0) {
                        if (daysBetween == 0)
                            efficiencyWeekDuties += 100
                        else {
                            efficiencyWeekDuties = ((deadlineTask * 100) / deadlineTask).toInt()
                            efficiencyWeekDuties += 100
                        }
                    } else if (deadlineTask > 0) {
                        efficiencyWeekDuties = ((daysBetween * 100) / deadlineTask).toInt()
                        efficiencyWeekDuties += 100
                    } else if (deadlineTask < 0) {
                        efficiencyWeekDuties = ((daysBetween * 100) / -deadlineTask).toInt()
                        efficiencyWeekDuties += 100
                    }

                    val newTask = TaskEmployee(
                        idTask = onClickTask!!.idTask,
                        idEmployee = onClickTask.idEmployee,
                        idTaskProject = onClickTask.idTaskProject,
                        nameTask = onClickTask.nameTask,
                        descriptionTask = onClickTask.descriptionTask,
                        volumeTask = onClickTask.volumeTask,
                        doneTask = true,
                        deadlineTask = onClickTask.deadlineTask,
                        yearDeadline = onClickTask.yearDeadline,
                        monthDeadline = onClickTask.monthDeadline,
                        dayDeadline = onClickTask.dayDeadline,
                        efficiencyTask = efficiencyWeekDuties,
                        projectTask = onClickTask.projectTask,
                        dayCreation = onClickTask.dayCreation,
                        monthCreation = onClickTask.monthCreation,
                        yearCreation = onClickTask.yearCreation,
                        dayDone = today.persianDay,
                        monthDone = today.persianMonth,
                        yearDone = today.persianYear
                    )
                    taskEmployeeDao.update(newTask)
                }

            }
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

            val newTask = SubTaskProject(
                idSubTask = subTaskProject.idSubTask,
                idProject = subTaskProject.idProject,
                nameSubTask = subTaskProject.nameSubTask,
                descriptionSubTask = subTaskProject.descriptionSubTask,
                volumeTask = subTaskProject.volumeTask,
                doneSubTask = subTaskProject.doneSubTask,
                yearCreation = subTaskProject.yearCreation,
                monthCreation = subTaskProject.monthCreation,
                dayCreation = subTaskProject.dayCreation,
                deadlineTask = subTaskProject.deadlineTask,
                valueCalendar = subTaskProject.valueCalendar,
                dayDeadline = subTaskProject.dayDeadline,
                yearDeadline = subTaskProject.yearDeadline,
                monthDeadline = subTaskProject.monthDeadline,
                dayDone = subTaskProject.dayDone,
                monthDone = subTaskProject.monthDone,
                yearDone = subTaskProject.yearDone
            )
            subTaskProjectDao.delete(newTask)
            subTaskProjectAdapter.deleteSubTask(newTask, position)

        } else
            numberSubTaskProject = numberSubTaskProject!! - 1

        subTaskProjectAdapter.deleteSubTask(subTaskProject, position)
        subTaskProjectDao.delete(subTaskProject)

        val taskEmployeeDao = AppDatabase.getDataBase(binding.root.context).taskDao
        val onClickTask = taskEmployeeDao.getEmployeeTaskProject(subTaskProject.idSubTask!!)
        if (onClickTask != null) {

            val employeeSubTaskProjects = teamSubTaskDao.getListTeamSubTask(
                project1.idProject!!,
                subTaskProject.idSubTask!!
            )
            for (employeeSubTaskProject in employeeSubTaskProjects) {
                taskEmployeeDao.delete(onClickTask)
            }
        }

        val totalVolumeProject = project1.totalVolumeProject
        val doneVolumeProject = project1.doneVolumeProject
        val subDoneVolumeProject = doneVolumeProject!! - subTaskProject.volumeTask
        val subTotalVolumeProject = totalVolumeProject!! - subTaskProject.volumeTask
        var efficiencyProject = 0
        if (numberSubTaskProject != null)
            efficiencyProject =
                ((subDoneVolumeProject.toDouble() / subTotalVolumeProject) * 100).toInt()

        val newProject = Project(
            idProject = project1.idProject,
            nameProject = project1.nameProject,
            dayCreation = project1.dayCreation,
            monthCreation = project1.monthCreation,
            yearCreation = project1.yearCreation,
            valueCalendar = project1.valueCalendar,
            deadlineTask = project1.deadlineTask,
            doneProject = project.doneProject,
            typeProject = project1.typeProject,
            descriptionProject = project1.descriptionProject,
            numberSubTaskProject = numberSubTaskProject,
            numberDoneSubTaskProject = numberDonSubTaskProject,
            noDeadlineProject = project.noDeadlineProject,
            progressProject = efficiencyProject,
            budgetProject = project.budgetProject,
            doneVolumeProject = subDoneVolumeProject,
            totalVolumeProject = subTotalVolumeProject,
            settled = project.settled,
            urlProject = project1.urlProject
        )
        projectDao.update(newProject)
    }
}