package com.vearad.vearatick.fgmSub

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kizitonwose.calendarview.utils.persian.PersianCalendar
import com.vearad.vearatick.DataBase.AppDatabase
import com.vearad.vearatick.DataBase.EfficiencyDao
import com.vearad.vearatick.DataBase.EfficiencyEmployee
import com.vearad.vearatick.DataBase.Employee
import com.vearad.vearatick.DataBase.TaskEmployee
import com.vearad.vearatick.DataBase.TaskEmployeeDao
import com.vearad.vearatick.DataBase.Time
import com.vearad.vearatick.DataBase.TimeDao
import com.vearad.vearatick.databinding.FragmentEmployeeStatisticsBinding
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

class EmployeeStatisticsFragment(
    val employee: Employee,
    val efficiencyEmployeeDao: EfficiencyDao,
    val position: Int
) :
    Fragment() {

    lateinit var binding: FragmentEmployeeStatisticsBinding
    lateinit var presenceEmployeeDao: TimeDao
    lateinit var taskEmployeeDao: TaskEmployeeDao
    lateinit var presencesEmployee: List<Time>
    lateinit var tasksEmployee: List<TaskEmployee>

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

    var efficiencyAllPresenceEmployee = 0
    var efficiencyMonthPresenceEmployee = 0
    var efficiencyWeekPresenceEmployee = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEmployeeStatisticsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val day = PersianCalendar()

        presenceEmployeeDao = AppDatabase.getDataBase(view.context).timeDao
        taskEmployeeDao = AppDatabase.getDataBase(view.context).taskDao

        presencesEmployee = presenceEmployeeDao.getEmployeeAllTime(employee.idEmployee!!)
        tasksEmployee = taskEmployeeDao.getEmployeeAllTask(employee.idEmployee)

        val efficiencyEmployee = efficiencyEmployeeDao.getEfficiencyEmployee(employee.idEmployee)

        val currentDate = LocalDate.of(day.persianYear, day.persianMonth + 1, day.persianDay)
        // یافتن روز اول هفته
        val firstDayOfWeek =
            currentDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SATURDAY)).dayOfMonth - 1
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

        if (presencesEmployee.isNotEmpty()) {


            efficiencyWeekPresenceEmployee = ((weekPresenceEmployee.toDouble() / efficiencyEmployee!!.mustWeekWatch.toDouble()) * 100).toInt()

            efficiencyAllPresenceEmployee = efficiencyEmployee.efficiencyTotalPresence
            if (efficiencyEmployee.totalWeekWatch != weekPresenceEmployee)
                efficiencyAllPresenceEmployee += efficiencyWeekPresenceEmployee

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
                efficiencyWeekDuties = efficiencyEmployee.efficiencyWeekDuties,
                efficiencyMonthDuties = efficiencyEmployee.totalMonthDuties,
                efficiencyTotalDuties = efficiencyEmployee.efficiencyTotalDuties,
                totalWeekDuties = efficiencyEmployee.totalWeekDuties,
                totalMonthDuties = efficiencyEmployee.totalMonthDuties,
                totalDuties = efficiencyEmployee.totalDuties,
                efficiencyTotal = efficiencyEmployee.efficiencyTotal
            )
            efficiencyEmployeeDao.update(newEfficiencyEmployee)

            binding.txtWatchWeek.text = efficiencyEmployee.totalWeekWatch.toString() + " ساعت"
            binding.txtWatchMonth.text = efficiencyEmployee.totalMonthWatch.toString() + " ساعت"
            binding.txtWatchTotal.text = efficiencyEmployee.totalWatch.toString() + " ساعت"

            binding.progressTotalPresence.setPercent(efficiencyEmployee.efficiencyTotalPresence.toInt())
            binding.txtTotalPresence.text =
                efficiencyEmployee.efficiencyTotalPresence.toString() + " %"
        }

        for (taskEmployee in tasksEmployee) {
            if (taskEmployee.doneTask == true) {
                allVolumeTaskEmployee += taskEmployee.volumeTask
                allEfficiencyTaskEmployee += taskEmployee.efficiencyTask!!
                if (day.persianMonth == taskEmployee.monthCreation) {
                    monthVolumeTaskEmployee += taskEmployee.volumeTask
                    monthEfficiencyTaskEmployee += taskEmployee.efficiencyTask
                    if (firstDayOfWeek <= taskEmployee.dayCreation && taskEmployee.dayCreation <= endDayOfWeek) {
                        weekVolumeTaskEmployee += taskEmployee.volumeTask
                        weekEfficiencyTaskEmployee += taskEmployee.efficiencyTask
                    }
                }
            }
        }

        if (tasksEmployee.isNotEmpty()) {

//            efficiencyWeekPresenceEmployee = weekPresenceEmployee / counterWeek
//            efficiencyMonthPresenceEmployee = monthPresenceEmployee / counterMonth
//            efficiencyAllPresenceEmployee = allPresenceEmployee / counterAll

            val newEfficiencyEmployee = EfficiencyEmployee(
                idEfficiency = efficiencyEmployee!!.idEfficiency,
                idEmployee = efficiencyEmployee.idEmployee,
                mustWeekWatch = efficiencyEmployee.mustWeekWatch,
                numberDay = efficiencyEmployee.numberDay,
                totalWeekWatch = efficiencyEmployee.totalWeekWatch,
                totalMonthWatch = efficiencyEmployee.totalMonthWatch,
                totalWatch = efficiencyEmployee.totalWatch,
                efficiencyWeekPresence = efficiencyEmployee.efficiencyWeekPresence,
                efficiencyTotalPresence = efficiencyEmployee.efficiencyTotalPresence,
                efficiencyWeekDuties = weekEfficiencyTaskEmployee,
                efficiencyMonthDuties = monthEfficiencyTaskEmployee,
                efficiencyTotalDuties = allEfficiencyTaskEmployee,
                totalWeekDuties = weekVolumeTaskEmployee,
                totalMonthDuties = monthVolumeTaskEmployee,
                totalDuties = allVolumeTaskEmployee,
                efficiencyTotal = efficiencyEmployee.efficiencyTotal
            )
            efficiencyEmployeeDao.update(newEfficiencyEmployee)

            binding.txtTackWeek.text = efficiencyEmployee.totalWeekDuties.toString() + " تا"
            binding.txtTackMonth.text = efficiencyEmployee.totalMonthDuties.toString() + " تا"
            binding.txtTackTotal.text = efficiencyEmployee.totalDuties.toString() + " تا"

            binding.progressWeekDuties.setPercent(efficiencyEmployee.efficiencyWeekDuties.toInt())
            binding.txtWeekDuties.text = efficiencyEmployee.efficiencyWeekDuties.toString() + " %"
            binding.progressMonthDuties.setPercent(efficiencyEmployee.efficiencyMonthDuties.toInt())
            binding.txtMonthDuties.text = efficiencyEmployee.efficiencyMonthDuties.toString() + " %"
            binding.progressTotalDuties.setPercent(efficiencyEmployee.efficiencyTotalDuties.toInt())
            binding.txtTotalDuties.text = efficiencyEmployee.efficiencyTotalDuties.toString() + " %"
        }
    }

    override fun onResume() {
        super.onResume()
        presencesEmployee = presenceEmployeeDao.getEmployeeAllTime(employee.idEmployee!!)
        tasksEmployee = taskEmployeeDao.getEmployeeAllTask(employee.idEmployee)
        val efficiencyEmployee = efficiencyEmployeeDao.getEfficiencyEmployee(employee.idEmployee)

        binding.txtWatchWeek.text = efficiencyEmployee!!.totalWeekWatch.toString() + " ساعت"
        binding.txtWatchMonth.text = efficiencyEmployee.totalMonthWatch.toString() + " ساعت"
        binding.txtWatchTotal.text = efficiencyEmployee.totalWatch.toString() + " ساعت"

        binding.progressTotalPresence.setPercent(efficiencyEmployee.efficiencyTotalPresence.toInt())
        binding.txtTotalPresence.text =
            efficiencyEmployee.efficiencyTotalPresence.toString() + " %"

        binding.txtTackWeek.text = efficiencyEmployee.totalWeekDuties.toString() + " تا"
        binding.txtTackMonth.text = efficiencyEmployee.totalMonthDuties.toString() + " تا"
        binding.txtTackTotal.text = efficiencyEmployee.totalDuties.toString() + " تا"

        binding.progressWeekDuties.setPercent(efficiencyEmployee.efficiencyWeekDuties.toInt())
        binding.txtWeekDuties.text = efficiencyEmployee.efficiencyWeekDuties.toString() + " %"
        binding.progressMonthDuties.setPercent(efficiencyEmployee.efficiencyMonthDuties.toInt())
        binding.txtMonthDuties.text = efficiencyEmployee.efficiencyMonthDuties.toString() + " %"
        binding.progressTotalDuties.setPercent(efficiencyEmployee.efficiencyTotalDuties.toInt())
        binding.txtTotalDuties.text = efficiencyEmployee.efficiencyTotalDuties.toString() + " %"
    }
}