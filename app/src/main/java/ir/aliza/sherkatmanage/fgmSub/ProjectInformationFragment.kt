package ir.aliza.sherkatmanage.fgmSub

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.jakewharton.threetenabp.AndroidThreeTen
import com.kizitonwose.calendarview.utils.persian.PersianCalendar
import com.kizitonwose.calendarview.utils.persian.toPersianCalendar
import ir.aliza.sherkatmanage.DataBase.AppDatabase
import ir.aliza.sherkatmanage.DataBase.CompanyReceipt
import ir.aliza.sherkatmanage.DataBase.EfficiencyDao
import ir.aliza.sherkatmanage.DataBase.EfficiencyEmployee
import ir.aliza.sherkatmanage.DataBase.Project
import ir.aliza.sherkatmanage.DataBase.ProjectDao
import ir.aliza.sherkatmanage.DataBase.SubTaskProject
import ir.aliza.sherkatmanage.DataBase.SubTaskProjectDao
import ir.aliza.sherkatmanage.DataBase.TeamSubTaskDao
import ir.aliza.sherkatmanage.Dialog.ProjectDeleteDialogFragment
import ir.aliza.sherkatmanage.Dialog.ProjectUpdateSubTaskFromInfoBottomsheetFragment
import ir.aliza.sherkatmanage.ProAndEmpActivity
import ir.aliza.sherkatmanage.R
import ir.aliza.sherkatmanage.adapter.SubTaskProjectAdapter
import ir.aliza.sherkatmanage.adapter.TeamProjectAdapter
import ir.aliza.sherkatmanage.databinding.ActivityProAndEmpBinding
import ir.aliza.sherkatmanage.databinding.FragmentDialogCheckoutProjectBinding
import ir.aliza.sherkatmanage.databinding.FragmentDialogDeleteSubtaskProjectBinding
import ir.aliza.sherkatmanage.databinding.FragmentProjectInformationBinding
import ir.aliza.sherkatmanage.databinding.ItemSubTaskBinding
import org.joda.time.DateTime
import org.joda.time.Days
import org.threeten.bp.LocalDate

class ProjectInformationFragment(
    var project: Project,
    val subTaskProjectDao: SubTaskProjectDao,
    val projectDao: ProjectDao,
    val position: Int,
    val bindingActivityProAndEmp: ActivityProAndEmpBinding
) : Fragment(), SubTaskProjectAdapter.SubTaskEvent {

    lateinit var binding: FragmentProjectInformationBinding
    lateinit var bindingDialogDeleteSubtaskProject: FragmentDialogDeleteSubtaskProjectBinding
    lateinit var bindingDialogCheckoutProject: FragmentDialogCheckoutProjectBinding
    lateinit var bindingItemSubTask: ItemSubTaskBinding
    lateinit var subTaskProjectAdapter: SubTaskProjectAdapter
    lateinit var efficiencyEmployeeDao: EfficiencyDao
    lateinit var teamSubTaskDao: TeamSubTaskDao

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProjectInformationBinding.inflate(layoutInflater, container, false)
        bindingItemSubTask = ItemSubTaskBinding.inflate(layoutInflater, container, false)
        bindingDialogDeleteSubtaskProject =
            FragmentDialogDeleteSubtaskProjectBinding.inflate(layoutInflater, container, false)
        bindingDialogCheckoutProject =
            FragmentDialogCheckoutProjectBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AndroidThreeTen.init(view.context)
        onBackPressed()

        project = projectDao.getProject(project.idProject!!)!!
        efficiencyEmployeeDao =
            AppDatabase.getDataBase(bindingItemSubTask.root.context).efficiencyDao
        teamSubTaskDao = AppDatabase.getDataBase(bindingItemSubTask.root.context).teamSubTaskDao

        setData()

        binding.btnBck.setOnClickListener {
            parentFragmentManager.beginTransaction().detach(this@ProjectInformationFragment)
                .replace(R.id.frame_layout_sub, ProjectFragment(bindingActivityProAndEmp)).commit()
        }

        binding.btnGoToSettlement.setOnClickListener {
            showCheckoutDialog()
        }

        binding.btnAddNewPerson.setOnClickListener {
            val transaction =
                (activity as ProAndEmpActivity).supportFragmentManager.beginTransaction()
            transaction.replace(
                R.id.layout_pro_and_emp,
                ProjectAddNewTeamFragment(
                    project,
                    subTaskProjectDao,
                    projectDao,
                    position,
                    bindingActivityProAndEmp
                )
            )
                .addToBackStack(null)
                .commit()
        }

        binding.btnSeeMoreSubTaskPro.setOnClickListener {
            val transaction =
                (activity as ProAndEmpActivity).supportFragmentManager.beginTransaction()
            transaction.replace(
                R.id.layout_pro_and_emp,
                ProjectSubTaskFragment(
                    project,
                    projectDao,
                    position,
                    bindingActivityProAndEmp,
                    subTaskProjectDao
                )
            )
                .addToBackStack(null)
                .commit()
        }

        val popupMenu = PopupMenu(this.context, binding.btnMenuProject)
        onMenuClicked(popupMenu)

    }

    @SuppressLint("SetTextI18n")
    fun setData() {

        binding.txtNamePro.text = project.nameProject
        binding.txtDescription.text = project.descriptionProject

        if (project.budgetProject != "0") {
            binding.txtBudget.text = project.budgetProject + " تومان"

        } else {
            binding.txtBudget.visibility = View.GONE
            binding.btnGoToSettlement.visibility = View.GONE
            binding.txtSettlement.visibility = View.GONE
            val shape = GradientDrawable()
            shape.shape = GradientDrawable.RECTANGLE
            shape.cornerRadii = floatArrayOf(40f, 40f, 40f, 40f, 40f, 40f, 40f, 40f)
            shape.setStroke(
                5,
                ContextCompat.getColor(binding.root.context, R.color.white)
            )
            shape.setColor(ContextCompat.getColor(binding.root.context, R.color.blacke))
            binding.cardView50.background = shape
            binding.txtNoBudget.text = "پروژه بودجه \nندارد"
        }
        if (project.doneProject!!) {
            binding.txtDay.visibility = View.GONE
            binding.txt.visibility = View.GONE
            binding.textView28.visibility = View.GONE
            binding.txtDone.visibility = View.VISIBLE
            val shape = GradientDrawable()
            shape.shape = GradientDrawable.RECTANGLE
            shape.cornerRadii = floatArrayOf(40f, 40f, 40f, 40f, 40f, 40f, 40f, 40f)
            shape.setStroke(
                5,
                ContextCompat.getColor(binding.root.context, R.color.green_700)
            )
            shape.setColor(ContextCompat.getColor(binding.root.context, R.color.blacke))
            binding.cardView5.background = shape

        } else {
            if (!project.noDeadlineProject!!) {
                val today = LocalDate.now().toPersianCalendar()
                val startDate =
                    DateTime(today.persianYear, today.persianMonth, today.persianDay, 0, 0, 0)
                val endDate = DateTime(
                    project.yearCreation,
                    project.monthCreation,
                    project.dayCreation,
                    0,
                    0,
                    0
                )
                var daysBetween = Days.daysBetween(startDate, endDate).days

                if (daysBetween > 0) {
                    binding.txtDay.text = "$daysBetween روز "
                    binding.txt.text = " دیگر باقیمانده است "
                    val shape = GradientDrawable()
                    shape.shape = GradientDrawable.RECTANGLE
                    shape.cornerRadii = floatArrayOf(40f, 40f, 40f, 40f, 40f, 40f, 40f, 40f)
                    shape.setStroke(
                        5,
                        ContextCompat.getColor(binding.root.context, R.color.firoze)
                    )
                    shape.setColor(ContextCompat.getColor(binding.root.context, R.color.blacke))
                    binding.cardView5.background = shape
                } else if (daysBetween == 0) {
                    val shape = GradientDrawable()
                    shape.shape = GradientDrawable.RECTANGLE
                    shape.cornerRadii = floatArrayOf(40f, 40f, 40f, 40f, 40f, 40f, 40f, 40f)
                    shape.setStroke(
                        5,
                        ContextCompat.getColor(binding.root.context, R.color.yelowe_light)
                    )
                    shape.setColor(ContextCompat.getColor(binding.root.context, R.color.blacke))
                    binding.cardView5.background = shape
                    binding.txtNoDeadline.text = " امروز باید تسک تحویل داده شه"
                    binding.txtNoDeadline.textSize = 18f
                } else {
                    daysBetween = -daysBetween
                    binding.txtDay.text = "$daysBetween روز "
                    binding.txt.text = " از تحویل پروژه گذشته"
                    val shape = GradientDrawable()
                    shape.shape = GradientDrawable.RECTANGLE
                    shape.cornerRadii = floatArrayOf(40f, 40f, 40f, 40f, 40f, 40f, 40f, 40f)
                    shape.setStroke(
                        5,
                        ContextCompat.getColor(binding.root.context, R.color.red_800)
                    )
                    shape.setColor(ContextCompat.getColor(binding.root.context, R.color.blacke))
                    binding.txtDay.setTextColor(android.graphics.Color.parseColor("#c62828"))
                    binding.cardView5.background = shape

                }
            } else {
                binding.txtDay.visibility = View.GONE
                binding.txt.visibility = View.GONE
                binding.txtNoDeadline.text = "پروژه ددلاین \nندارد"
                val shape = GradientDrawable()
                shape.shape = GradientDrawable.RECTANGLE
                shape.cornerRadii = floatArrayOf(40f, 40f, 40f, 40f, 40f, 40f, 40f, 40f)
                shape.setStroke(
                    5,
                    ContextCompat.getColor(binding.root.context, R.color.white)
                )
                shape.setColor(ContextCompat.getColor(binding.root.context, R.color.blacke))
                binding.cardView5.background = shape
            }
        }

        val totalVolumeProject = subTaskProjectDao.getTotalVolumeTaskSum(project.idProject!!)
        val doneVolumeProject = subTaskProjectDao.getDoneVolumeTaskSum(project.idProject!!, true)
        var efficiencyProject = 0

        if (doneVolumeProject != null)
            efficiencyProject = ((doneVolumeProject.toDouble() / totalVolumeProject) * 100).toInt()

        binding.progressPro.progress = efficiencyProject
        binding.txtProg.text = "$efficiencyProject%"

        binding.txtNumTaskPro.text =
            project.numberDoneSubTaskProject.toString() + " از " + project.numberSubTaskProject.toString()

        if (project.idProject != null) {

            val teamProjectDao = AppDatabase.getDataBase(binding.root.context).teamProjectDao
            val teamProjectData = teamProjectDao.getListTeamProject(project.idProject!!)
            val teamProjectAdapter = TeamProjectAdapter(ArrayList(teamProjectData))
            binding.rcvTeam.adapter = teamProjectAdapter

            val subTaskProjectData = subTaskProjectDao.getSubTaskProject(project.idProject!!)
            subTaskProjectAdapter =
                SubTaskProjectAdapter(
                    ArrayList(subTaskProjectData),
                    this,
                    project,
                    projectDao,
                    subTaskProjectDao,
                )
            binding.rcvTskPro.adapter = subTaskProjectAdapter

        }

    }

    @SuppressLint("SetTextI18n")
    fun setDataOnDone() {

        val project1 = projectDao.getProject(project.idProject!!)!!

        val numberDonSubTaskProject = project1.numberDoneSubTaskProject!!
        val totalVolumeProject = subTaskProjectDao.getTotalVolumeTaskSum(project1.idProject!!)
        val doneVolumeProject = subTaskProjectDao.getDoneVolumeTaskSum(project1.idProject, true)
        var efficiencyProject = 0

        if (project1.settled!!) {
            binding.textView160.visibility = View.GONE
            binding.txtBudget.visibility = View.GONE
            binding.txtSettlement.visibility = View.GONE
            binding.btnGoToSettlement.visibility = View.GONE
            binding.txtNoBudget.visibility = View.VISIBLE
            binding.txtNoBudget.text = "پروژه\nتسویه شد"
            val shape = GradientDrawable()
            shape.shape = GradientDrawable.RECTANGLE
            shape.cornerRadii = floatArrayOf(40f, 40f, 40f, 40f, 40f, 40f, 40f, 40f)
            shape.setStroke(
                5,
                ContextCompat.getColor(binding.root.context, R.color.green_700)
            )
            shape.setColor(ContextCompat.getColor(binding.root.context, R.color.blacke))
            binding.cardView50.background = shape
            binding.txtNoBudget.setTextColor(android.graphics.Color.parseColor("#2F8558"))
        }

        if (doneVolumeProject != null)
            efficiencyProject = ((doneVolumeProject.toDouble() / totalVolumeProject) * 100).toInt()

        binding.progressPro.progress = efficiencyProject
        binding.txtProg.text = "$efficiencyProject%"

        binding.txtNumTaskPro.text =
            numberDonSubTaskProject.toString() + " از " + project1.numberSubTaskProject.toString()

        val subTaskProjectData = subTaskProjectDao.getSubTaskProject(project1.idProject)
        subTaskProjectAdapter =
            SubTaskProjectAdapter(
                ArrayList(subTaskProjectData),
                this,
                project1,
                projectDao,
                subTaskProjectDao,
            )
        binding.rcvTskPro.adapter = subTaskProjectAdapter

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
        setDataOnDone()
        updateYourData()
        setData()
    }

    private fun updateYourData() {
        project = projectDao.getProject(project.idProject!!)!!
    }

    private fun onMenuClicked(popupMenu: PopupMenu) {

        popupMenu.menuInflater.inflate(R.menu.menu_project, popupMenu.menu)
        binding.btnMenuProject.setOnClickListener {
            popupMenu.show()

            val doneMenuItem = popupMenu.menu.findItem(R.id.menu_project_done)
            if (project.doneProject!!) {
                doneMenuItem.title = "تکمیل نشد"
            } else {
                doneMenuItem.title = "تکمیل شد"
            }
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
                                subTaskProjectDao,
                                bindingActivityProAndEmp
                            )
                        )
                            .addToBackStack(null)
                            .commit()
                    }

                    R.id.menu_project_done -> {
                        doneProject(doneMenuItem)
                        parentFragmentManager.beginTransaction()
                            .detach(this@ProjectInformationFragment)
                            .replace(
                                R.id.frame_layout_sub,
                                ProjectFragment(bindingActivityProAndEmp)
                            ).commit()
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

    private fun doneProject(doneMenuItem: MenuItem) {

        if (project.doneProject!!) {

            doneMenuItem.title = "تکمیل شد"

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
                doneProject = false,
                descriptionProject = project.descriptionProject,
                numberSubTaskProject = project.numberSubTaskProject,
                numberDoneSubTaskProject = project.numberDoneSubTaskProject,
                progressProject = project.progressProject,
                budgetProject = project.budgetProject,
                doneVolumeProject = project.doneVolumeProject,
                totalVolumeProject = project.totalVolumeProject,
                settled = project.settled
            )
            projectDao.update(newProject)

        } else {

            doneMenuItem.title = "تکمیل نشد"

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
                doneProject = true,
                descriptionProject = project.descriptionProject,
                numberSubTaskProject = project.numberSubTaskProject,
                numberDoneSubTaskProject = project.numberDoneSubTaskProject,
                progressProject = project.progressProject,
                budgetProject = project.budgetProject,
                doneVolumeProject = project.doneVolumeProject,
                totalVolumeProject = project.totalVolumeProject,
                settled = project.settled
            )
            projectDao.update(newProject)
            Toast.makeText(context, " پروژه ${project.nameProject} تکمیل شد. ", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onSubTaskClicked(task: SubTaskProject, position: Int) {}
    override fun onSubTaskLongClicked(subTask: SubTaskProject, position: Int) {}
    override fun onMenuItemClick(subTask: SubTaskProject, position: Int) {

        val onClickSubTask = subTaskProjectDao.getOnClickSubTaskProject(subTask.idSubTask!!)
        val viewHolder =
            binding.rcvTskPro.findViewHolderForAdapterPosition(position) as SubTaskProjectAdapter.SubTaskProjectViewHolder
        viewHolder.let { holder ->
            val btnMenuSubTaskProject = holder.btnMenuSubTaskProject
            val popupMenu = PopupMenu(context, btnMenuSubTaskProject)
            popupMenu.inflate(R.menu.menu_task_project)
            popupMenu.show()

            val doneMenuItem = popupMenu.menu.findItem(R.id.menu_task_project_done)
            if (onClickSubTask!!.doneSubTask!!) {
                doneMenuItem.title = "تکمیل نشد"
            } else {
                doneMenuItem.title = "تکمیل شد"
            }

            popupMenu.setOnMenuItemClickListener { item ->

                when (item.itemId) {

                    R.id.menu_task_project_edit -> {
                        val bottomsheet = ProjectUpdateSubTaskFromInfoBottomsheetFragment(
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

                    R.id.menu_task_project_done -> {
                        doneSubTask(onClickSubTask, doneMenuItem)
                        setDataOnDone()
                        true
                    }

                    R.id.menu_task_project_delete -> {
                        showDeleteDialog(onClickSubTask)
                        true
                    }

                    else -> false
                }
            }
        }

    }

    override fun onTeamSubTaskClick(subTask: SubTaskProject, project: Project, position: Int) {
        parentFragmentManager.beginTransaction().detach(this@ProjectInformationFragment)
            .replace(
                R.id.layout_pro_and_emp,
                ProjectSubTaskAddNewTeamFromInfoFragment(
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

    private fun showCheckoutDialog() {
        val parent = bindingDialogCheckoutProject.root.parent as? ViewGroup
        parent?.removeView(bindingDialogCheckoutProject.root)
        val dialogBuilder = AlertDialog.Builder(bindingDialogCheckoutProject.root.context)
        dialogBuilder.setView(bindingDialogCheckoutProject.root)

        val alertDialog = dialogBuilder.create()
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.Transparent.toArgb()))
        alertDialog.setCancelable(false)
        alertDialog.show()
        bindingDialogCheckoutProject.dialogBtnDeleteCansel.setOnClickListener {
            alertDialog.dismiss()
        }
        bindingDialogCheckoutProject.dialogBtnDeleteSure.setOnClickListener {

            checkoutProject()
            setDataOnDone()
            alertDialog.dismiss()
        }
    }

    private fun checkoutProject() {
        val companyReceiptDao =
            AppDatabase.getDataBase(bindingDialogCheckoutProject.root.context).companyReceiptDao
        var budgetProject = project.budgetProject
        budgetProject = budgetProject!!.replace(",", "")
        val today = PersianCalendar()

        val newProject = Project(
            idProject = project.idProject,
            nameProject = project.nameProject,
            dayCreation = project.dayCreation,
            monthCreation = project.monthCreation,
            yearCreation = project.yearCreation,
            valueCalendar = project.valueCalendar,
            deadlineTask = project.deadlineTask,
            doneProject = project.doneProject,
            typeProject = project.typeProject,
            descriptionProject = project.descriptionProject,
            numberSubTaskProject = project.numberSubTaskProject,
            numberDoneSubTaskProject = project.numberDoneSubTaskProject,
            noDeadlineProject = project.noDeadlineProject,
            progressProject = project.progressProject,
            budgetProject = project.budgetProject,
            doneVolumeProject = project.doneVolumeProject,
            totalVolumeProject = project.totalVolumeProject,
            settled = true
        )
        projectDao.update(newProject)

        val newReceipt = CompanyReceipt(
            companyReceipt = budgetProject.toLong(),
            companyReceiptDescription = "بابت پروژه ${project.nameProject}",
            companyReceiptDate = "${today.persianYear}/${today.persianMonth + 1}/${today.persianDay}"
        )
        companyReceiptDao.insert(newReceipt)
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
                settled = project.settled
            )
            projectDao.update(newProject)

            val employeeSubTaskProjects = teamSubTaskDao.getListTeamSubTask(
                project1.idProject!!,
                onClickSubTask.idSubTask!!
            )

            for (employeeSubTaskProject in employeeSubTaskProjects) {

                val efficiencyEmployee =
                    efficiencyEmployeeDao.getEfficiencyEmployee(employeeSubTaskProject.idEmployee!!)
                Toast.makeText(context, "$employeeSubTaskProject", Toast.LENGTH_LONG).show()

                val startDate =
                    DateTime(
                        onClickSubTask.yearDone!!,
                        onClickSubTask.monthDone!!,
                        onClickSubTask.dayDone!!,

                        0,
                        0,
                        0
                    )
                val endDate = DateTime(
                    onClickSubTask.yearDeadline,
                    onClickSubTask.monthDeadline,
                    onClickSubTask.dayDeadline,
                    0,
                    0,
                    0
                )
                val daysBetween = Days.daysBetween(startDate, endDate).days
                var efficiencyWeekDuties = 0
                val gradeDuties = onClickSubTask.volumeTask
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

                val newEfficiencyEmployee = EfficiencyEmployee(
                    idEfficiency = efficiencyEmployee!!.idEfficiency,
                    idEmployee = efficiencyEmployee.idEmployee,
                    mustWeekWatch = efficiencyEmployee.mustWeekWatch,
                    numberDay = efficiencyEmployee.numberDay,
                    totalWeekWatch = efficiencyEmployee.totalWeekWatch,
                    totalWatch = efficiencyEmployee.totalWatch,
                    efficiencyWeekPresence = efficiencyEmployee.efficiencyWeekPresence,
                    efficiencyTotalPresence = efficiencyEmployee.efficiencyTotalPresence,
                    totalWeekDuties = efficiencyEmployee.totalWeekDuties!! - gradeDuties,
                    totalMonthDuties = efficiencyEmployee.totalMonthDuties,
                    totalDuties = efficiencyEmployee.totalDuties,
                    efficiencyWeekDuties = efficiencyEmployee.efficiencyWeekDuties!! - efficiencyWeekDuties,
                    efficiencyTotalDuties = efficiencyEmployee.efficiencyTotalDuties,
                    efficiencyTotal = efficiencyEmployee.efficiencyTotal,
                    totalMonthWatch = efficiencyEmployee.totalMonthWatch
                )
                efficiencyEmployeeDao.update(newEfficiencyEmployee)
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
                numberDoneSubTaskProject = numberDonSubTaskProject,
                progressProject = project1.progressProject,
                budgetProject = project.budgetProject,
                totalVolumeProject = project1.totalVolumeProject,
                doneVolumeProject = project1.doneVolumeProject,
                settled = project.settled
            )
            projectDao.update(newProject)

            val employeeSubTaskProjects = teamSubTaskDao.getListTeamSubTask(
                project1.idProject!!,
                onClickSubTask.idSubTask!!
            )

            for (employeeSubTaskProject in employeeSubTaskProjects) {
                val efficiencyEmployee =
                    efficiencyEmployeeDao.getEfficiencyEmployee(employeeSubTaskProject.idEmployee!!)

                val startDate =
                    DateTime(
                        today.persianYear,
                        today.persianMonth,
                        today.persianDay,
                        0,
                        0,
                        0
                    )
                val endDate = DateTime(
                    onClickSubTask.yearDeadline,
                    onClickSubTask.monthDeadline,
                    onClickSubTask.dayDeadline,
                    0,
                    0,
                    0
                )
                val daysBetween = Days.daysBetween(startDate, endDate).days
                var efficiencyWeekDuties = 0
                val gradeDuties = onClickSubTask.volumeTask
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

                val newEfficiencyEmployee = EfficiencyEmployee(
                    idEfficiency = efficiencyEmployee!!.idEfficiency,
                    idEmployee = efficiencyEmployee.idEmployee,
                    mustWeekWatch = efficiencyEmployee.mustWeekWatch,
                    numberDay = efficiencyEmployee.numberDay,
                    totalWeekWatch = efficiencyEmployee.totalWeekWatch,
                    totalWatch = efficiencyEmployee.totalWatch,
                    efficiencyWeekPresence = efficiencyEmployee.efficiencyWeekPresence,
                    efficiencyTotalPresence = efficiencyEmployee.efficiencyTotalPresence,
                    totalWeekDuties = efficiencyEmployee.totalWeekDuties!! + gradeDuties,
                    totalMonthDuties = efficiencyEmployee.totalMonthDuties,
                    totalDuties = efficiencyEmployee.totalDuties,
                    efficiencyWeekDuties = efficiencyEmployee.efficiencyWeekDuties!! + efficiencyWeekDuties,
                    efficiencyTotalDuties = efficiencyEmployee.efficiencyTotalDuties,
                    efficiencyTotal = efficiencyEmployee.efficiencyTotal,
                    totalMonthWatch = efficiencyEmployee.totalMonthWatch
                )
                efficiencyEmployeeDao.update(newEfficiencyEmployee)

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
            setDataOnDone()
            alertDialog.dismiss()
        }
    }

    fun deleteItem(subTaskProject: SubTaskProject, position: Int) {

        val project1 = projectDao.getProject(project.idProject!!)
        val today = PersianCalendar()

        var numberDonSubTaskProject = project1!!.numberDoneSubTaskProject
        var numberSubTaskProject = project1.numberSubTaskProject

        if (subTaskProject.doneSubTask!!) {
            numberSubTaskProject = numberSubTaskProject!! - 1
            numberDonSubTaskProject = numberDonSubTaskProject!! - 1

            val employeeSubTaskProjects = teamSubTaskDao.getListTeamSubTask(
                project1.idProject!!,
                subTaskProject.idSubTask!!
            )
            for (employeeSubTaskProject in employeeSubTaskProjects) {

                val efficiencyEmployee =
                    efficiencyEmployeeDao.getEfficiencyEmployee(employeeSubTaskProject.idEmployee!!)

                val startDate =
                    DateTime(
                        subTaskProject.yearDone!!,
                        subTaskProject.monthDone!!,
                        subTaskProject.dayDone!!,
                        0, 0, 0)
                val endDate = DateTime(
                    subTaskProject.yearDeadline,
                    subTaskProject.monthDeadline,
                    subTaskProject.dayDeadline,
                    0,
                    0,
                    0
                )
                val daysBetween = Days.daysBetween(startDate, endDate).days
                var efficiencyWeekDuties = 0
                var gradeDuties = 0
                if (subTaskProject.doneSubTask) {
                    gradeDuties = subTaskProject.volumeTask
                    val deadlineTask = subTaskProject.deadlineTask

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
                }

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

                val newEfficiencyEmployee = EfficiencyEmployee(
                    idEfficiency = efficiencyEmployee!!.idEfficiency,
                    idEmployee = efficiencyEmployee.idEmployee,
                    mustWeekWatch = efficiencyEmployee.mustWeekWatch,
                    numberDay = efficiencyEmployee.numberDay,
                    totalWeekWatch = efficiencyEmployee.totalWeekWatch,
                    totalWatch = efficiencyEmployee.totalWatch,
                    efficiencyWeekPresence = efficiencyEmployee.efficiencyWeekPresence,
                    efficiencyTotalPresence = efficiencyEmployee.efficiencyTotalPresence,
                    totalWeekDuties = efficiencyEmployee.totalWeekDuties!! - gradeDuties,
                    totalMonthDuties = efficiencyEmployee.totalMonthDuties,
                    totalDuties = efficiencyEmployee.totalDuties,
                    efficiencyWeekDuties = efficiencyEmployee.efficiencyWeekDuties!! - efficiencyWeekDuties,
                    efficiencyTotalDuties = efficiencyEmployee.efficiencyTotalDuties,
                    efficiencyTotal = efficiencyEmployee.efficiencyTotal,
                    totalMonthWatch = efficiencyEmployee.totalMonthWatch
                )
                efficiencyEmployeeDao.update(newEfficiencyEmployee)
            }
        } else
            numberSubTaskProject = numberSubTaskProject!! - 1

        subTaskProjectAdapter.deleteSubTask(subTaskProject, position)
        subTaskProjectDao.delete(subTaskProject)

        val newProject = Project(
            idProject = project1.idProject,
            nameProject = project1.nameProject,
            dayCreation = project.dayCreation,
            monthCreation = project.monthCreation,
            yearCreation = project.yearCreation,
            valueCalendar = project.valueCalendar,
            deadlineTask = project.deadlineTask,
            doneProject = project.doneProject,
            typeProject = project1.typeProject,
            descriptionProject = project1.descriptionProject,
            numberSubTaskProject = numberSubTaskProject,
            numberDoneSubTaskProject = numberDonSubTaskProject,
            noDeadlineProject = project.noDeadlineProject,
            progressProject = project.progressProject,
            budgetProject = project.budgetProject,
            doneVolumeProject = project.doneVolumeProject,
            totalVolumeProject = project.totalVolumeProject,
            settled = project.settled
        )
        projectDao.update(newProject)
    }
}