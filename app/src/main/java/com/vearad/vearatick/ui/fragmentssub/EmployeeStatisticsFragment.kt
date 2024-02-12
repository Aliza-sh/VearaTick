package com.vearad.vearatick.ui.fragmentssub

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kizitonwose.calendarview.utils.persian.PersianCalendar
import com.vearad.vearatick.model.db.AppDatabase
import com.vearad.vearatick.model.db.EfficiencyDao
import com.vearad.vearatick.model.db.EfficiencyEmployee
import com.vearad.vearatick.model.db.Employee
import com.vearad.vearatick.model.db.TaskEmployee
import com.vearad.vearatick.model.db.TaskEmployeeDao
import com.vearad.vearatick.model.db.Time
import com.vearad.vearatick.model.db.TimeDao
import com.vearad.vearatick.databinding.FragmentEmployeeStatisticsBinding
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

class EmployeeStatisticsFragment(
    val employee: Employee,
    val efficiencyEmployeeDao: EfficiencyDao,
    val position: Int,
) :
    Fragment() {

    lateinit var binding: FragmentEmployeeStatisticsBinding
    lateinit var presenceEmployeeDao: TimeDao
    lateinit var taskEmployeeDao: TaskEmployeeDao
    lateinit var presencesEmployee: List<Time>
    lateinit var tasksEmployee: List<TaskEmployee>

    var allPresenceEmployee = 0
    var allPresenceEmployeeMustTime = 0
    var monthPresenceEmployee = 0
    var weekPresenceEmployeeDifferenceTime = 0
    var weekPresenceEmployeeMustTime = 0

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
            currentDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SATURDAY)).dayOfMonth
        var endDayOfWeek = firstDayOfWeek + 6
        if (endDayOfWeek > 31)
            if (day.persianMonth < 7)
                endDayOfWeek = 31
            else
                endDayOfWeek = 30

        for (presenceEmployee in presencesEmployee) {
            allPresenceEmployee += presenceEmployee.differenceTime!!
            allPresenceEmployeeMustTime += presenceEmployee.mustTime!!
            counterAll++
            if (day.persianMonthName == presenceEmployee.month) {
                monthPresenceEmployee += presenceEmployee.differenceTime
//                counterMonth++
                if (presenceEmployee.day.toInt() in firstDayOfWeek..endDayOfWeek) {
                    weekPresenceEmployeeDifferenceTime += presenceEmployee.differenceTime
                    weekPresenceEmployeeMustTime += presenceEmployee.mustTime!!
                    counterWeek++
                }
            }
        }

        efficiencyWeekPresenceEmployee = ((weekPresenceEmployeeDifferenceTime.toDouble() / weekPresenceEmployeeMustTime.toDouble()) * 100).toInt()
        efficiencyAllPresenceEmployee = ((allPresenceEmployee.toDouble() / allPresenceEmployeeMustTime.toDouble()) * 100).toInt()


        Log.v("efficiency", "efficiencyAllPresenceEmployee: ${efficiencyAllPresenceEmployee}")
        Log.v("efficiency", "efficiencyWeekPresenceEmployee: ${efficiencyWeekPresenceEmployee}")


        binding.txtWatchWeek.text = "$weekPresenceEmployeeDifferenceTime ساعت"
        binding.txtWatchMonth.text = "$monthPresenceEmployee ساعت"
        binding.txtWatchTotal.text = "$allPresenceEmployee ساعت"

        if (efficiencyAllPresenceEmployee > 100){
            binding.progressTotalPresence.setPercent(100)
            binding.progressTotalPresence.setProgressBarColor(Color.parseColor("#70AE84"))
            binding.txtTotalPresence.text = "+100 %"
            binding.txtTotalPresence.setTextColor(Color.parseColor("#70AE84"))

        } else if (efficiencyAllPresenceEmployee in 1..100){
            binding.progressTotalPresence.setPercent(efficiencyAllPresenceEmployee)
            binding.progressTotalPresence.setProgressBarColor(Color.parseColor("#E600ADB5"))
            binding.txtTotalPresence.text = "$efficiencyAllPresenceEmployee %"
            binding.txtTotalPresence.setTextColor(Color.parseColor("#E600ADB5"))

        }
        else if (efficiencyAllPresenceEmployee < 0){
            binding.progressTotalPresence.setPercent(0)
            binding.progressTotalPresence.setProgressBarColor(Color.parseColor("#FE7D8B"))
            binding.txtTotalPresence.text = "-0 %"
            binding.txtTotalPresence.setTextColor(Color.parseColor("#FE7D8B"))

        }

        for (taskEmployee in tasksEmployee) {
            if (taskEmployee.doneTask == true) {
                allVolumeTaskEmployee += taskEmployee.volumeTask
                allEfficiencyTaskEmployee += taskEmployee.efficiencyTask!!
                counterAllTask ++
                if (day.persianMonth == taskEmployee.monthCreation) {
                    monthVolumeTaskEmployee += taskEmployee.volumeTask
                    monthEfficiencyTaskEmployee += taskEmployee.efficiencyTask
                    counterMonthTask ++
                    if (taskEmployee.dayDone in firstDayOfWeek..endDayOfWeek) {
                        weekVolumeTaskEmployee += taskEmployee.volumeTask
                        weekEfficiencyTaskEmployee += taskEmployee.efficiencyTask
                        counterWeekTask ++
                        Log.v("loginapp", "firstDayOfWeek: ${firstDayOfWeek}")
                        Log.v("loginapp", "endDayOfWeek: ${endDayOfWeek}")
                        Log.v("loginapp", "firstDayOfWeek: ${taskEmployee.dayCreation}")
                    }
                }
            }
        }

        if (counterAllTask == 0)
            counterAllTask ++
        if (counterMonthTask == 0)
            counterMonthTask ++
        if (counterWeekTask == 0)
            counterWeekTask ++


//            efficiencyWeekPresenceEmployee = weekPresenceEmployee / counterWeek
//            efficiencyMonthPresenceEmployee = monthPresenceEmployee / counterMonth
//            efficiencyAllPresenceEmployee = allPresenceEmployee / counterAll

             val newEfficiencyEmployee = EfficiencyEmployee(
                idEfficiency = efficiencyEmployee!!.idEfficiency,
                idEmployee = efficiencyEmployee.idEmployee,
                mustWeekWatch = efficiencyEmployee.mustWeekWatch,
                numberDay = efficiencyEmployee.numberDay,
                totalWeekWatch = weekPresenceEmployeeDifferenceTime,
                totalMonthWatch = monthPresenceEmployee,
                totalWatch = allPresenceEmployee,
                efficiencyWeekPresence = efficiencyWeekPresenceEmployee,
                efficiencyTotalPresence = efficiencyAllPresenceEmployee,
                efficiencyWeekDuties = weekEfficiencyTaskEmployee/counterWeekTask,
                efficiencyMonthDuties = monthEfficiencyTaskEmployee/counterMonthTask,
                efficiencyTotalDuties = allEfficiencyTaskEmployee/counterAllTask,
                totalWeekDuties = weekVolumeTaskEmployee,
                totalMonthDuties = monthVolumeTaskEmployee,
                totalDuties = allVolumeTaskEmployee,
                efficiencyTotal = efficiencyEmployee.efficiencyTotal
            )
            efficiencyEmployeeDao.update(newEfficiencyEmployee)

            binding.txtTackWeek.text = efficiencyEmployee.totalWeekDuties.toString() + " تا"
            binding.txtTackMonth.text = efficiencyEmployee.totalMonthDuties.toString() + " تا"
            binding.txtTackTotal.text = efficiencyEmployee.totalDuties.toString() + " تا"

        setColorProgress(efficiencyEmployee)


        }

    private fun setColorProgress(efficiencyEmployee: EfficiencyEmployee) {


        if (efficiencyEmployee.efficiencyWeekDuties.toInt() > 100){
            val sign = "+"
            setColorAndProgresWeekDuties(100, Color.parseColor("#70AE84"), sign)
        } else if (efficiencyEmployee.efficiencyWeekDuties.toInt() in 1..100){
            val sign = ""
            setColorAndProgresWeekDuties(efficiencyEmployee.efficiencyWeekDuties,Color.parseColor("#E600ADB5"),sign)
        }
        else if ( efficiencyEmployee.efficiencyWeekDuties.toInt() < 0){
            val sign = "- "
            setColorAndProgresWeekDuties(0, Color.parseColor("#FE7D8B"), sign)
        }

        if (efficiencyEmployee.efficiencyMonthDuties.toInt() > 100){
            val sign = "+"
            setColorAndProgresMonthDuties(100, Color.parseColor("#70AE84"), sign)
        } else if (efficiencyEmployee.efficiencyMonthDuties.toInt() in 1..100){
            val sign = ""
            setColorAndProgresMonthDuties(efficiencyEmployee.efficiencyMonthDuties,Color.parseColor("#E600ADB5"),sign)
        }
        else if ( efficiencyEmployee.efficiencyMonthDuties.toInt() < 0){
            val sign = "- "
            setColorAndProgresMonthDuties(0, Color.parseColor("#FE7D8B"), sign)
        }

        if (efficiencyEmployee.efficiencyTotalDuties.toInt() > 100){
            val sign = "+"
            setColorAndProgresTotalDuties(100, Color.parseColor("#70AE84"), sign)
        } else if (efficiencyEmployee.efficiencyTotalDuties.toInt() in 1..100){
            val sign = ""
            setColorAndProgresTotalDuties(efficiencyEmployee.efficiencyTotalDuties,Color.parseColor("#E600ADB5"),sign)
        }
        else if ( efficiencyEmployee.efficiencyTotalDuties.toInt() < 0){
            val sign = "- "
            setColorAndProgresTotalDuties(0, Color.parseColor("#FE7D8B"), sign)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setColorAndProgresWeekDuties(
        efficiency: Int,
        parseColor: Int,
        sign: String
    ) {
        binding.progressWeekDuties.setPercent(efficiency)
        binding.progressWeekDuties.setProgressBarColor(parseColor)
        binding.txtWeekDuties.text = "$sign$efficiency %"
        binding.txtWeekDuties.setTextColor(parseColor)
    }

    @SuppressLint("SetTextI18n")
    private fun setColorAndProgresMonthDuties(
        efficiency: Int,
        parseColor: Int,
        sign: String
    ) {
        binding.progressMonthDuties.setPercent(efficiency)
        binding.progressMonthDuties.setProgressBarColor(parseColor)
        binding.txtMonthDuties.text = "$sign$efficiency %"
        binding.txtMonthDuties.setTextColor(parseColor)
    }

    @SuppressLint("SetTextI18n")
    private fun setColorAndProgresTotalDuties(
        efficiency: Int,
        parseColor: Int,
        sign: String
    ) {
        binding.progressTotalDuties.setPercent(efficiency)
        binding.progressTotalDuties.setProgressBarColor(parseColor)
        binding.txtTotalDuties.text = "$sign$efficiency %"
        binding.txtTotalDuties.setTextColor(parseColor)

    }
    }