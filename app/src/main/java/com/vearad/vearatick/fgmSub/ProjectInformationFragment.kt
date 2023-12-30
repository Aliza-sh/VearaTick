package com.vearad.vearatick.fgmSub

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
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
import com.vearad.vearatick.DataBase.AppDatabase
import com.vearad.vearatick.DataBase.CompanyReceipt
import com.vearad.vearatick.DataBase.EfficiencyDao
import com.vearad.vearatick.DataBase.FinancialReport
import com.vearad.vearatick.DataBase.Project
import com.vearad.vearatick.DataBase.ProjectDao
import com.vearad.vearatick.DataBase.SubTaskProject
import com.vearad.vearatick.DataBase.SubTaskProjectDao
import com.vearad.vearatick.DataBase.TaskEmployee
import com.vearad.vearatick.DataBase.TaskEmployeeDao
import com.vearad.vearatick.DataBase.TeamSubTaskDao
import com.vearad.vearatick.Dialog.ProjectUpdateSubTaskFromInfoBottomsheetFragment
import com.vearad.vearatick.ProAndEmpActivity
import com.vearad.vearatick.R
import com.vearad.vearatick.adapter.SubTaskProjectAdapter
import com.vearad.vearatick.adapter.TeamProjectAdapter
import com.vearad.vearatick.databinding.ActivityProAndEmpBinding
import com.vearad.vearatick.databinding.FragmentDialogCheckoutProjectBinding
import com.vearad.vearatick.databinding.FragmentDialogDeleteProjectBinding
import com.vearad.vearatick.databinding.FragmentDialogDeleteSubtaskProjectBinding
import com.vearad.vearatick.databinding.FragmentProjectInformationBinding
import com.vearad.vearatick.databinding.ItemSubTaskBinding
import com.vearad.vearatick.fgmMain.CompanyInformationFragment
import org.joda.time.DateTime
import org.joda.time.Days
import org.threeten.bp.LocalDate

class ProjectInformationFragment(
    var project: Project,
    val subTaskProjectDao: SubTaskProjectDao,
    val projectDao: ProjectDao,
    val position: Int,
    val bindingActivityProAndEmp: ActivityProAndEmpBinding,
    val goTo: Boolean,

    ) : Fragment(), SubTaskProjectAdapter.SubTaskEvent {

    lateinit var binding: FragmentProjectInformationBinding
    lateinit var bindingDialogDeleteSubtaskProject: FragmentDialogDeleteSubtaskProjectBinding
    lateinit var bindingDialogCheckoutProject: FragmentDialogCheckoutProjectBinding
    lateinit var bindingDialogDeleteProject: FragmentDialogDeleteProjectBinding
    lateinit var bindingItemSubTask: ItemSubTaskBinding
    lateinit var subTaskProjectAdapter: SubTaskProjectAdapter
    lateinit var efficiencyEmployeeDao: EfficiencyDao
    lateinit var teamSubTaskDao: TeamSubTaskDao
    lateinit var taskEmployeeDao: TaskEmployeeDao
    lateinit var newCompanyFinancialReport: FinancialReport

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
        bindingDialogDeleteProject =
            FragmentDialogDeleteProjectBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AndroidThreeTen.init(view.context)
        if (goTo)
            onBackCompanyResumePressed()
        else
            onBackPressed()
        taskEmployeeDao = AppDatabase.getDataBase(view.context).taskDao
        project = projectDao.getProject(project.idProject!!)!!
        efficiencyEmployeeDao =
            AppDatabase.getDataBase(bindingItemSubTask.root.context).efficiencyDao
        teamSubTaskDao = AppDatabase.getDataBase(bindingItemSubTask.root.context).teamSubTaskDao

        setData()

        if (goTo)
            binding.btnBck.setOnClickListener {
                parentFragmentManager.beginTransaction().detach(this@ProjectInformationFragment)
                    .replace(R.id.frame_layout_main, CompanyInformationFragment()).commit()
            }
        else
            binding.btnBck.setOnClickListener {
                parentFragmentManager.beginTransaction().detach(this@ProjectInformationFragment)
                    .replace(R.id.frame_layout_sub, ProjectFragment(bindingActivityProAndEmp))
                    .commit()
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

    private fun onBackCompanyResumePressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    parentFragmentManager.beginTransaction().detach(this@ProjectInformationFragment)
                        .replace(R.id.frame_layout_main, CompanyInformationFragment())
                        .commit()
                }
            })
    }

    @SuppressLint("SetTextI18n")
    fun setData() {

        binding.txtNamePro.text = project.nameProject
        binding.txtDescription.text = project.descriptionProject

        if (project.budgetProject != "0") {
            binding.txtBudget.text = project.budgetProject + " تومان"

        } else {
            binding.txtBudget.visibility = GONE
            binding.btnGoToSettlement.visibility = GONE
            binding.txtSettlement.visibility = GONE
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
            binding.txtDay.visibility = GONE
            binding.txt.visibility = GONE
            binding.txtTitleDedline.visibility = GONE
            binding.txtDone.visibility = VISIBLE
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

            binding.txtSettlement.text = "پروژه\nتسویه شد"
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
            val project = projectDao.getProject(project.idProject!!)
            val doneMenuItem = popupMenu.menu.findItem(R.id.menu_project_done)
            if (project?.doneProject!!) {
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
                    }

                    R.id.menu_project_delete -> {

                        showDeleteProjectDialog()

                    }
                }
                true
            }
        }
    }

    private fun doneProject(doneMenuItem: MenuItem) {

        val project = projectDao.getProject(project.idProject!!)!!
        var done = project.doneProject
        if (project.doneProject!!) {

            doneMenuItem.title = "تکمیل شد"
            done = false

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
                doneProject = done,
                descriptionProject = project.descriptionProject,
                numberSubTaskProject = project.numberSubTaskProject,
                numberDoneSubTaskProject = project.numberDoneSubTaskProject,
                progressProject = 0,
                budgetProject = project.budgetProject,
                doneVolumeProject = project.doneVolumeProject,
                totalVolumeProject = project.totalVolumeProject,
                settled = project.settled,
                urlProject = project.urlProject
            )
            projectDao.update(newProject)
            setViewDoneProject(done)

        } else {

            if (project.numberDoneSubTaskProject == project.numberSubTaskProject) {

                if (project.budgetProject == "0" || project.settled == true) {
                    doneMenuItem.title = "تکمیل نشد"
                    done = true

                    val totalVolumeProject =
                        subTaskProjectDao.getTotalVolumeTaskSum(project.idProject!!)
                    val doneVolumeProject =
                        subTaskProjectDao.getDoneVolumeTaskSum(project.idProject, true)
                    var efficiencyProject = 0
                    if (doneVolumeProject != null)
                        efficiencyProject =
                            ((doneVolumeProject.toDouble() / totalVolumeProject) * 100).toInt()
                    Log.v(
                        "loginapp",
                        "doneVolumeProject: ${project.doneVolumeProject!!.toDouble()}"
                    )
                    Log.v("loginapp", "totalVolumeProject: ${project.totalVolumeProject!!}")


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
                        doneProject = done,
                        descriptionProject = project.descriptionProject,
                        numberSubTaskProject = project.numberSubTaskProject,
                        numberDoneSubTaskProject = project.numberDoneSubTaskProject,
                        progressProject = efficiencyProject,
                        budgetProject = project.budgetProject,
                        doneVolumeProject = project.doneVolumeProject,
                        totalVolumeProject = project.totalVolumeProject,
                        settled = project.settled,
                        urlProject = project.urlProject
                    )
                    projectDao.update(newProject)
                    setViewDoneProject(done)

                    Toast.makeText(
                        binding.root.context,
                        " پروژه ${project.nameProject} تکمیل شد. ",
                        Toast.LENGTH_LONG
                    )
                        .show()
                } else {
                    Toast.makeText(
                        binding.root.context,
                        "پروژه تسویه حساب نشده است",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            } else {
                Toast.makeText(
                    binding.root.context,
                    "هنوز تسک های پروژه تکمیل نشده است",
                    Toast.LENGTH_LONG
                )
                    .show()
            }
        }
    }

    private fun setViewDoneProject(done: Boolean) {

        if (done) {
            binding.txtDay.visibility = View.GONE
            binding.txt.visibility = View.GONE
            binding.txtTitleDedline.visibility = View.GONE
            binding.txtNoDeadline.visibility = View.GONE

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

                binding.txtDone.visibility = GONE
                binding.txtTitleDedline.visibility = VISIBLE
                binding.txtDay.visibility = VISIBLE
                binding.txt.visibility = VISIBLE
                binding.txtNoDeadline.visibility = VISIBLE

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
                binding.txtDone.visibility = GONE
                binding.txtDay.visibility = GONE
                binding.txt.visibility = GONE
                binding.txtTitleDedline.visibility = VISIBLE
                binding.txtNoDeadline.visibility = VISIBLE

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

    }

    private fun showCheckoutDialog() {
        val parent = bindingDialogCheckoutProject.root.parent as? ViewGroup
        parent?.removeView(bindingDialogCheckoutProject.root)
        val dialogBuilder = AlertDialog.Builder(bindingDialogCheckoutProject.root.context)
        dialogBuilder.setView(bindingDialogCheckoutProject.root)

        val project = projectDao.getProject(project.idProject!!)!!

        if (project.settled == true)
            bindingDialogCheckoutProject.textView.text = "پروژه تسویه نشده است ؟!"
        else
            bindingDialogCheckoutProject.textView.text = "پروژه تسویه حساب شد ؟"

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
        var companyReceiptProject = companyReceiptDao.getReceiptProject(project.idProject!!)
        val project = projectDao.getProject(project.idProject!!)!!

        if (project.settled == true) {

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
                settled = false,
                urlProject = project.urlProject
            )
            projectDao.update(newProject)

            val newReceipt = CompanyReceipt(
                idCompanyReceipt = companyReceiptProject?.idCompanyReceipt,
                idProject = companyReceiptProject?.idProject,
                companyReceipt = budgetProject.toLong(),
                companyReceiptDescription = "بابت پروژه ${project.nameProject}",
                companyReceiptDate = "${today.persianYear}/${today.persianMonth + 1}/${today.persianDay}",
                monthCompanyReceipt = today.persianMonth + 1,
                yearCompanyReceipt = today.persianYear
            )
            companyReceiptDao.delete(newReceipt)
            onCompanyFinancialReport(budgetProject, today)
        } else {
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
                settled = true,
                urlProject = project.urlProject
            )
            projectDao.update(newProject)

            val newReceipt = CompanyReceipt(
                idProject = project.idProject,
                companyReceipt = budgetProject.toLong(),
                companyReceiptDescription = "بابت پروژه ${project.nameProject}",
                companyReceiptDate = "${today.persianYear}/${today.persianMonth + 1}/${today.persianDay}",
                monthCompanyReceipt = today.persianMonth + 1,
                yearCompanyReceipt = today.persianYear
            )
            companyReceiptDao.insert(newReceipt)
            onCompanyFinancialReport(budgetProject, today)
        }
        setViewCheckoutProject()
    }

    private fun showDeleteProjectDialog() {

        val parent = bindingDialogDeleteProject.root.parent as? ViewGroup
        parent?.removeView(bindingDialogDeleteProject.root)
        val dialogBuilder = AlertDialog.Builder(bindingDialogDeleteProject.root.context)
        dialogBuilder.setView(bindingDialogDeleteProject.root)

        val alertDialog = dialogBuilder.create()
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.Transparent.toArgb()))
        alertDialog.setCancelable(false)
        alertDialog.show()
        bindingDialogDeleteProject.dialogBtnDeleteCansel.setOnClickListener {
            alertDialog.dismiss()
        }
        bindingDialogDeleteProject.dialogBtnDeleteSure.setOnClickListener {

            deleteItem(project, position)
            alertDialog.dismiss()
        }
    }

    fun deleteItem(project: Project, position: Int) {
        val companyReceiptDao =
            AppDatabase.getDataBase(bindingDialogCheckoutProject.root.context).companyReceiptDao

        if (project.settled == true) {
            var companyReceiptProject = companyReceiptDao.getReceiptProject(project.idProject!!)
            val newReceipt = CompanyReceipt(
                idCompanyReceipt = companyReceiptProject!!.idCompanyReceipt,
                idProject = companyReceiptProject.idProject,
                companyReceipt = companyReceiptProject.companyReceipt,
                companyReceiptDescription = companyReceiptProject.companyReceiptDescription,
                companyReceiptDate = companyReceiptProject.companyReceiptDate,
                monthCompanyReceipt = companyReceiptProject.monthCompanyReceipt,
                yearCompanyReceipt = companyReceiptProject.yearCompanyReceipt
            )
        companyReceiptDao.delete(newReceipt)
        }

        subTaskProjectDao.deleteSubTasksByProjectId(project.idProject!!)
        projectDao.delete(project)
        parentFragmentManager.beginTransaction().detach(this@ProjectInformationFragment)
            .replace(R.id.frame_layout_sub, ProjectFragment(bindingActivityProAndEmp))
            .commit()
    }

    fun setViewCheckoutProject() {
        val project = projectDao.getProject(project.idProject!!)!!

        if (project.settled!!) {

            binding.txtSettlement.text = "پروژه\nتسویه شد"
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
        } else {
            binding.txtSettlement.text = "تسویه"
            val shape = GradientDrawable()
            shape.shape = GradientDrawable.RECTANGLE
            shape.cornerRadii = floatArrayOf(40f, 40f, 40f, 40f, 40f, 40f, 40f, 40f)
            shape.setStroke(
                5,
                ContextCompat.getColor(binding.root.context, R.color.blacke)
            )
            shape.setColor(ContextCompat.getColor(binding.root.context, R.color.blacke))
            binding.cardView50.background = shape
            binding.txtNoBudget.setTextColor(android.graphics.Color.parseColor("#202020"))
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
                        val project = projectDao.getProject(project.idProject!!)
                        if (project?.doneProject == false)
                            doneSubTask(onClickSubTask, doneMenuItem)
                        else
                            Toast.makeText(
                                context,
                                "پروژه تکمیل شده این کار مجاز نیست",
                                Toast.LENGTH_SHORT
                            ).show()
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

    private fun onCompanyFinancialReport(income: String, today: PersianCalendar) {

        val financialReportDao = AppDatabase.getDataBase(binding.root.context).financialReportDao
        val financialReportYearAndMonth = financialReportDao.getFinancialReportYearAndMonthDao(
            today.persianYear,
            today.persianMonth + 1
        )

        if (financialReportYearAndMonth != null) {
            val agoIncome = financialReportYearAndMonth.income
            val newIncome = agoIncome!!.toLong() + income.toLong()
            newCompanyFinancialReport = FinancialReport(
                idFinancialReport = financialReportYearAndMonth.idFinancialReport,
                year = financialReportYearAndMonth.year,
                month = financialReportYearAndMonth.month,
                expense = financialReportYearAndMonth.expense,
                income = newIncome,
                profit = financialReportYearAndMonth.profit
            )
            financialReportDao.update(newCompanyFinancialReport)
        } else {
            newCompanyFinancialReport = FinancialReport(
                year = today.persianYear,
                month = today.persianMonth + 1,
                income = income.toLong(),
            )
            financialReportDao.insert(newCompanyFinancialReport)
        }
    }

    private fun doneSubTask(onClickSubTask: SubTaskProject, doneMenuItem: MenuItem) {
        val today = PersianCalendar()
        if (onClickSubTask.doneSubTask!!) {

            doneMenuItem.title = "تکمیل شد"

            bindingItemSubTask.txtDedlineSubTask.visibility = VISIBLE
            bindingItemSubTask.imgDone.visibility = GONE

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
                urlProject = project.urlProject
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
                numberDoneSubTaskProject = numberDonSubTaskProject,
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
                        DateTime(today.persianYear, today.persianMonth, today.persianDay, 0, 0, 0)
                    val endDate = DateTime(
                        onClickSubTask.yearCreation,
                        onClickSubTask.monthCreation,
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
        setDataOnDone()
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