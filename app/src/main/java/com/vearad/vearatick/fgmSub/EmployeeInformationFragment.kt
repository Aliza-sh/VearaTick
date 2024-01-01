package com.vearad.vearatick.fgmSub

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.Window
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.PopupMenu
import androidx.activity.OnBackPressedCallback
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.kizitonwose.calendarview.utils.persian.PersianCalendar
import com.vearad.vearatick.DataBase.AppDatabase
import com.vearad.vearatick.DataBase.EfficiencyDao
import com.vearad.vearatick.DataBase.EfficiencyEmployee
import com.vearad.vearatick.DataBase.Employee
import com.vearad.vearatick.DataBase.EmployeeDao
import com.vearad.vearatick.ProAndEmpActivity
import com.vearad.vearatick.R
import com.vearad.vearatick.databinding.ActivityProAndEmpBinding
import com.vearad.vearatick.databinding.FragmentDialogDeleteItemEmployeeBinding
import com.vearad.vearatick.databinding.FragmentEmployeeInformationBinding
import koleton.api.hideSkeleton
import koleton.api.loadSkeleton
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

class EmployeeInformationFragment(
    var employee: Employee,
    val efficiencyEmployeeDao: EfficiencyDao,
    val position: Int,
    val employeeDao: EmployeeDao,
    val bindingActivityProAndEmpBinding: ActivityProAndEmpBinding,
    val goToEmployeeTaskFragment: Boolean,
) : Fragment() {

    lateinit var binding: FragmentEmployeeInformationBinding
    lateinit var bindingDialogDeleteItemEmployee: FragmentDialogDeleteItemEmployeeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEmployeeInformationBinding.inflate(layoutInflater, container, false)
        bindingDialogDeleteItemEmployee = FragmentDialogDeleteItemEmployeeBinding.inflate(layoutInflater, null, false)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        onBackPressed()
        firstRun(view)
        setData(employee, view)
        setTitleEmployee(view)

        goTo(goToEmployeeTaskFragment,view)

        binding.btnStatistics.setOnClickListener {
            btnStatistics(view)
        }
        binding.btnCalendar.setOnClickListener {
            btnCalendar(view)
        }
        binding.btnTask.setOnClickListener {
            btnTask(view)
        }


        val popupMenu = PopupMenu(this.context, binding.btnMenuEmployee)
        onMenuClicked(popupMenu)

        binding.btnBack.setOnClickListener {
            parentFragmentManager.beginTransaction().detach(this@EmployeeInformationFragment)
                .replace(R.id.frame_layout_sub, EmployeeFragment(bindingActivityProAndEmpBinding))
                .commit()
        }
    }

    override fun onResume() {
        super.onResume()
        setData(employee, binding.root.rootView)
    }

    private fun goTo(goToEmployeeTaskFragment: Boolean, view: View) {
        if (goToEmployeeTaskFragment)
            btnTask(view)
    }

    private fun setTitleEmployee(view: View) {

        val taskDao = AppDatabase.getDataBase(view.context).taskDao
        val timeDao = AppDatabase.getDataBase(view.context).timeDao
        val dayDao = AppDatabase.getDataBase(view.context).dayDao
        val today = PersianCalendar()

        val taskInWeekEmployee = taskDao.getTaskInWeek(employee.idEmployee!!, today.persianDay)
        val taskTodayEmployee = taskDao.getTaskToday(employee.idEmployee!!, today.persianDay)
        val taskTomorrowEmployee = taskDao.getTaskTomorrow(employee.idEmployee!!, today.persianDay)
        val taskPastEmployee = taskDao.getTaskPast(employee.idEmployee!!, today.persianDay)
        val taskDoneEmployee = taskDao.getDoneTask(employee.idEmployee!!)
        val timeEmployee = timeDao.getAllArrivalDay(
            employee.idEmployee!!,
            today.persianYear.toString(),
            today.persianMonthName,
            today.persianDay.toString()
        )
        val dayEmployee = dayDao.getAllNameDay(
            employee.idEmployee!!,
            today.persianYear.toString(),
            today.persianMonthName,
            today.persianWeekDayName.toString()
        )
        val efficiencyEmployee = efficiencyEmployeeDao.getEfficiencyEmployee(employee.idEmployee!!)
        val employeeNew = employeeDao.useEmployee(
            employee.idEmployee!!,
            today.persianDay,
            today.persianMonth,
            today.persianYear
        )
        Log.v("employeeNew", "employeeNew: ${employeeNew}")
        Log.v("employeeNew", "Here: ${today.persianDay}")
        Log.v("employeeNew", "Here: ${today.persianMonth}")
        Log.v("employeeNew", "Here: ${today.persianYear}")

        val efficiencyNewEmployee = efficiencyEmployeeDao.isAllColumnsNonZero(employee.idEmployee!!)
        Log.v("employeeNew", "efficiencyNewEmployee: ${efficiencyNewEmployee}")

        if (employeeNew && efficiencyNewEmployee) {
            binding.txtTitle.text = "این کارمند تازه استخدام شده"
        } else {
            if (dayEmployee!= null && timeEmployee == null )
                binding.txtTitle.text = "دیر شد نیومده یه خبر بگیر ازش"
            else {
                if (taskPastEmployee)
                    binding.txtTitle.text = "از موعد تحویل تسکش گذشته"
                else if (taskTodayEmployee)
                    binding.txtTitle.text = "امروز موعد تحویل تسک شه"
                else if (taskTomorrowEmployee)
                    binding.txtTitle.text = "فردا باید تسکش رو تحویل بده"
                else if (taskInWeekEmployee)
                    binding.txtTitle.text = "این هفته باید تسک تحویل بده"
                else {
                    val efficiencyWeekPresence = efficiencyEmployee!!.efficiencyWeekPresence
                    val efficiencyTotalDuties = efficiencyEmployee.efficiencyTotalDuties

                    if (efficiencyWeekPresence < 0)
                        binding.txtTitle.text = "حضور درست حسابی تو شرکت نداشته"
                    else if (taskDoneEmployee && efficiencyTotalDuties < 50)
                        binding.txtTitle.text = "تسک هاش رو درست حسابی انجام نداده"
                    else
                        binding.txtTitle.text = "این کارمند کارش درسته"
                }
            }
        }
    }

    private fun btnStatistics(view: View) {
        setEfficiency(view)
        binding.txtStatistics.setTextColor(Color.parseColor("#E600ADB5"))
        binding.viewStatistics.visibility = VISIBLE

        binding.txtCalendar.setTextColor(Color.parseColor("#FFFFFF"))
        binding.viewCalendar.visibility = INVISIBLE

        binding.txtTask.setTextColor(Color.parseColor("#FFFFFF"))
        binding.viewTask.visibility = INVISIBLE

        replaceFragment(EmployeeStatisticsFragment(employee, efficiencyEmployeeDao, position))
    }

    private fun btnCalendar(view: View) {
        setEfficiency(view)
        binding.txtCalendar.setTextColor(Color.parseColor("#E600ADB5"))
        binding.viewCalendar.visibility = VISIBLE

        binding.txtStatistics.setTextColor(Color.parseColor("#FFFFFF"))
        binding.viewStatistics.visibility = INVISIBLE

        binding.txtTask.setTextColor(Color.parseColor("#FFFFFF"))
        binding.viewTask.visibility = INVISIBLE

        replaceFragment(EmployeePresenceFragment(employee, efficiencyEmployeeDao, position))
    }

    private fun btnTask(view: View) {
        setEfficiency(view)
        binding.txtTask.setTextColor(Color.parseColor("#E600ADB5"))
        binding.viewTask.visibility = VISIBLE

        binding.txtStatistics.setTextColor(Color.parseColor("#FFFFFF"))
        binding.viewStatistics.visibility = INVISIBLE

        binding.txtCalendar.setTextColor(Color.parseColor("#FFFFFF"))
        binding.viewCalendar.visibility = INVISIBLE

        replaceFragment(
            EmployeeTaskFragment(
                employee,
                employeeDao,
                efficiencyEmployeeDao,
                position,
                bindingActivityProAndEmpBinding
            )
        )
    }

    private fun replaceFragment(fragment: Fragment) {
        var elapsedTime:Long = 0
        binding.loading.visibility = VISIBLE
        binding.frameLayoutEmp.visibility = GONE
        binding.tablayoutEmp.loadSkeleton()
        val startTime = System.currentTimeMillis()
        Log.v("EmployeeInformationFragment", "1")
        Log.v("EmployeeInformationFragment", "2")
        val transaction = (activity as ProAndEmpActivity).supportFragmentManager.beginTransaction()
        Log.v("EmployeeInformationFragment", "3")

        transaction.replace(R.id.frame_layout_emp, fragment).commit()
        Log.v("EmployeeInformationFragment", "4")
        Log.v("EmployeeInformationFragment", "5")
        val endTime = System.currentTimeMillis()
        elapsedTime = endTime - startTime
        Log.v("EmployeeInformationFragment", "elapsedTime: ${elapsedTime}")
        Log.v("EmployeeInformationFragment", "6")

        var grub = elapsedTime
        if (elapsedTime.toInt() == 0) {
            elapsedTime = 1
            grub = 1
        }
        elapsedTime *= 1000
        animationLoding(elapsedTime,grub)

        Handler(Looper.getMainLooper()).postDelayed({
            binding.tablayoutEmp.hideSkeleton()
            binding.loading.visibility = GONE
            binding.frameLayoutEmp.visibility = VISIBLE
        }, elapsedTime)

    }

    fun animationLoding(elapsedTime: Long, grub: Long) {
        val anim = AlphaAnimation(
            1f, 0f
        )
        anim.duration = (elapsedTime / grub)
        anim.fillAfter = true
        anim.repeatCount = ((elapsedTime / 1000).toInt() * grub).toInt()
        anim.repeatMode = Animation.REVERSE

        binding.splashAnimation.startAnimation(anim)
    }

    private fun firstRun(view: View) {
        btnStatistics(view)
        replaceFragment(
            EmployeeStatisticsFragment(
                employee,
                efficiencyEmployeeDao,
                position,
            )
        )
    }

    private fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    parentFragmentManager.beginTransaction()
                        .detach(this@EmployeeInformationFragment)
                        .replace(
                            R.id.frame_layout_sub,
                            EmployeeFragment(bindingActivityProAndEmpBinding)
                        ).commit()
                }
            })
    }

    private fun onMenuClicked(popupMenu: PopupMenu) {
        popupMenu.menuInflater.inflate(R.menu.menu_employee, popupMenu.menu)
        binding.btnMenuEmployee.setOnClickListener {
            popupMenu.show()
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_employee_edit -> {
                        val transaction =
                            (activity as ProAndEmpActivity).supportFragmentManager.beginTransaction()
                        transaction.replace(
                            R.id.layout_pro_and_emp,
                            EmployeeInfoUpdateFragment(
                                employee,
                                efficiencyEmployeeDao,
                                position,
                                employeeDao,
                                bindingActivityProAndEmpBinding,
                            )
                        )
                            .addToBackStack(null)
                            .commit()
                    }

                    R.id.menu_employee_delete -> {
                        showDeleteProjectDialog()
                    }
                }
                true
            }
        }
    }


    private fun updateYourData() {
        employee = employeeDao.getEmployee(employee.idEmployee!!)!!
    }

    @SuppressLint("SetTextI18n")
    private fun setData(employee: Employee, view: View) {
        val employee = employeeDao.getEmployee(employee.idEmployee!!)
        setEfficiency(view)
        binding.txtNameEmp.text = employee!!.name + " " + employee.family
        binding.txtSpecialtyEmp.text = employee.specialty


        if (employee.rank == "سهام دار") {

            binding.txtRank.text = "سهام دار"
            val shape = GradientDrawable()
            shape.shape = GradientDrawable.RECTANGLE
            shape.cornerRadii = floatArrayOf(40f, 40f, 40f, 0f, 0f, 40f, 40f, 40f)
            shape.setStroke(
                5,
                ContextCompat.getColor(binding.root.context, R.color.green_dark_rank)
            )
            shape.setColor(ContextCompat.getColor(binding.root.context, R.color.green_light_rank))
            binding.txtRank.setTextColor(android.graphics.Color.parseColor("#227158"))
            binding.txtRank.background = shape

        } else if (employee.rank == "کارمند") {

            binding.txtRank.text = "کارمند"
            val shape = GradientDrawable()
            shape.shape = GradientDrawable.RECTANGLE
            shape.cornerRadii = floatArrayOf(40f, 40f, 40f, 0f, 0f, 40f, 40f, 40f)
            shape.setStroke(
                5,
                ContextCompat.getColor(binding.root.context, R.color.blue_dark_rank)
            )
            shape.setColor(ContextCompat.getColor(binding.root.context, R.color.blue_light_rank))
            binding.txtRank.setTextColor(Color.parseColor("#215DAD"))
            binding.txtRank.background = shape

        } else if (employee.rank == "کارآموز") {
            binding.txtRank.text = "کارآموز"
            val shape = GradientDrawable()
            shape.shape = GradientDrawable.RECTANGLE
            shape.cornerRadii = floatArrayOf(40f, 40f, 40f, 0f, 0f, 40f, 40f, 40f)
            shape.setStroke(
                5,
                ContextCompat.getColor(binding.root.context, R.color.red_dark_rank)
            )
            shape.setColor(ContextCompat.getColor(binding.root.context, R.color.red_light_rank))
            binding.txtRank.setTextColor(Color.parseColor("#AF694C"))
            binding.txtRank.background = shape
        }

        if (employee.imagePath != null) {
            Glide.with(this)
                .load(employee.imagePath)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.btnInfoPrn)
        } else
            if (employee.gender == "زن") {
                binding.btnInfoPrn.setImageResource(R.drawable.img_matter)
            } else
                binding.btnInfoPrn.setImageResource(R.drawable.img_male)

    }

    private fun setEfficiency(view: View) {

        var allPresenceEmployee = 0
        var monthPresenceEmployee = 0
        var weekPresenceEmployee = 0

        var allVolumeTaskEmployee = 0
        var monthVolumeTaskEmployee = 0
        var weekVolumeTaskEmployee = 0
        var allEfficiencyTaskEmployee = 0
        var monthEfficiencyTaskEmployee = 0
        var weekEfficiencyTaskEmployee = 0

        var counterAll = 0
        var counterMonth = 0
        var counterWeek = 0

        var counterAllTask = 0
        var counterMonthTask = 0
        var counterWeekTask = 0

        var efficiencyAllPresenceEmployee = 0
        var efficiencyMonthPresenceEmployee = 0
        var efficiencyWeekPresenceEmployee = 0

        val presenceEmployeeDao = AppDatabase.getDataBase(view.context).timeDao
        val taskEmployeeDao = AppDatabase.getDataBase(view.context).taskDao

        val presencesEmployee = presenceEmployeeDao.getEmployeeAllTime(employee.idEmployee!!)
        val tasksEmployee = taskEmployeeDao.getEmployeeAllTask(employee.idEmployee!!)

        val efficiencyEmployee = efficiencyEmployeeDao.getEfficiencyEmployee(employee.idEmployee!!)
        val day = PersianCalendar()

        val currentDate = LocalDate.of(day.persianYear, day.persianMonth + 1, day.persianDay)
        // یافتن روز اول هفته
        val firstDayOfWeek =
            currentDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SATURDAY)).dayOfMonth
        var endDayOfWeek = firstDayOfWeek + 6
        if (endDayOfWeek > 31)
            if (day.persianMonth < 7)
                endDayOfWeek = 31
            else
                endDayOfWeek = 30

        for (presenceEmployee in presencesEmployee) {
            allPresenceEmployee += presenceEmployee.differenceTime!!
            counterAll++
            if (day.persianMonthName == presenceEmployee.month) {
                monthPresenceEmployee += presenceEmployee.differenceTime
//                counterMonth++
                if (presenceEmployee.day.toInt() in firstDayOfWeek..endDayOfWeek) {
                    weekPresenceEmployee += presenceEmployee.differenceTime
                    counterWeek++
                }
            }
        }

        efficiencyWeekPresenceEmployee =
            ((weekPresenceEmployee.toDouble() / efficiencyEmployee!!.mustWeekWatch.toDouble()) * 100).toInt()

        if (presencesEmployee.isEmpty())
            efficiencyAllPresenceEmployee = 0
        else
            efficiencyAllPresenceEmployee = efficiencyEmployee.efficiencyTotalPresence

        if (efficiencyEmployee.totalWeekWatch != weekPresenceEmployee) {
            efficiencyAllPresenceEmployee += efficiencyWeekPresenceEmployee

            Log.v("loginapp", "efficiencyAllPresenceEmployee: ${efficiencyAllPresenceEmployee}")
            Log.v("loginapp", "efficiencyWeekPresenceEmployee: ${efficiencyWeekPresenceEmployee}")
        }

        for (taskEmployee in tasksEmployee) {
            if (taskEmployee.doneTask == true) {
                allVolumeTaskEmployee += taskEmployee.volumeTask
                allEfficiencyTaskEmployee += taskEmployee.efficiencyTask!!
                counterAllTask++
                if (day.persianMonth == taskEmployee.monthCreation) {
                    monthVolumeTaskEmployee += taskEmployee.volumeTask
                    monthEfficiencyTaskEmployee += taskEmployee.efficiencyTask
                    counterMonthTask++
                    if (taskEmployee.dayDone in firstDayOfWeek..endDayOfWeek) {
                        weekVolumeTaskEmployee += taskEmployee.volumeTask
                        weekEfficiencyTaskEmployee += taskEmployee.efficiencyTask
                        counterWeekTask++
                        Log.v("loginapp", "firstDayOfWeek: ${firstDayOfWeek}")
                        Log.v("loginapp", "endDayOfWeek: ${endDayOfWeek}")
                        Log.v("loginapp", "firstDayOfWeek: ${taskEmployee.dayCreation}")
                    }
                }
            }
        }

        if (counterAllTask == 0)
            counterAllTask++
        if (counterMonthTask == 0)
            counterMonthTask++
        if (counterWeekTask == 0)
            counterWeekTask++


//            efficiencyWeekPresenceEmployee = weekPresenceEmployee / counterWeek
//            efficiencyMonthPresenceEmployee = monthPresenceEmployee / counterMonth
//            efficiencyAllPresenceEmployee = allPresenceEmployee / counterAll

        val newEfficiencyEmployee = EfficiencyEmployee(
            idEfficiency = efficiencyEmployee.idEfficiency,
            idEmployee = efficiencyEmployee.idEmployee,
            mustWeekWatch = efficiencyEmployee.mustWeekWatch,
            numberDay = efficiencyEmployee.numberDay,
            totalWeekWatch = weekPresenceEmployee,
            totalMonthWatch = monthPresenceEmployee,
            totalWatch = allPresenceEmployee,
            efficiencyWeekPresence = efficiencyWeekPresenceEmployee,
            efficiencyTotalPresence = efficiencyAllPresenceEmployee,
            efficiencyWeekDuties = weekEfficiencyTaskEmployee / counterWeekTask,
            efficiencyMonthDuties = monthEfficiencyTaskEmployee / counterMonthTask,
            efficiencyTotalDuties = allEfficiencyTaskEmployee / counterAllTask,
            totalWeekDuties = weekVolumeTaskEmployee,
            totalMonthDuties = monthVolumeTaskEmployee,
            totalDuties = allVolumeTaskEmployee,
            efficiencyTotal = efficiencyEmployee.efficiencyTotal
        )
        efficiencyEmployeeDao.update(newEfficiencyEmployee)

        val progress = efficiencyEmployeeDao.getEfficiencyEmployee(employee.idEmployee!!)
        val efficiencyTotalPresence = progress!!.efficiencyTotalPresence
        val efficiencyTotalDuties = progress.efficiencyTotalDuties
        val efficiencyTotal = (efficiencyTotalPresence + efficiencyTotalDuties) / 2

        if (efficiencyTotal > 100) {
            binding.prgTotalEmp.progress = 100F
            binding.prgTotalEmp.progressBarColor = Color.parseColor("#70AE84")

        } else if (efficiencyTotal in 1..100) {
            binding.prgTotalEmp.progress = efficiencyTotal.toFloat()
            binding.prgTotalEmp.progressBarColor = Color.parseColor("#E600ADB5")

        } else if (efficiencyTotal < 0) {
            binding.prgTotalEmp.progress = 100f
            binding.prgTotalEmp.progressBarColor = Color.parseColor("#FE7D8B")
        }

        setTitleEmployee(view)
    }

    private fun showDeleteProjectDialog() {

        val parent = bindingDialogDeleteItemEmployee.root.parent as? ViewGroup
        parent?.removeView(bindingDialogDeleteItemEmployee.root)
        val dialogBuilder = AlertDialog.Builder(bindingDialogDeleteItemEmployee.root.context)
        dialogBuilder.setView(bindingDialogDeleteItemEmployee.root)

        val alertDialog = dialogBuilder.create()
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(androidx.compose.ui.graphics.Color.Transparent.toArgb()))
        alertDialog.setCancelable(false)
        alertDialog.show()
        bindingDialogDeleteItemEmployee.dialogBtnDeleteCansel.setOnClickListener {
            alertDialog.dismiss()
        }
        bindingDialogDeleteItemEmployee.dialogBtnDeleteSure.setOnClickListener {

            deleteItem(employee, position)
            alertDialog.dismiss()
        }
    }

    fun deleteItem(employee: Employee ,position: Int) {

        val timeDao = AppDatabase.getDataBase(binding.root.context).timeDao
        val taskEmployeeDao = AppDatabase.getDataBase(binding.root.context).taskDao
        val dayDao = AppDatabase.getDataBase(binding.root.context).dayDao

        timeDao.deleteTimeByIdEmployee(employee.idEmployee!!)
        taskEmployeeDao.deleteTasksByidEmployee(employee.idEmployee!!)
        dayDao.deleteDayByIdEmployee(employee.idEmployee!!)
        employeeDao.delete(employee)
        parentFragmentManager.beginTransaction().detach(this@EmployeeInformationFragment)
            .replace(R.id.frame_layout_sub, EmployeeFragment(bindingActivityProAndEmpBinding))
            .commit()
    }

}