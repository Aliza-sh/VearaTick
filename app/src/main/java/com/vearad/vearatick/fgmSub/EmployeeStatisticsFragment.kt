package com.vearad.vearatick.fgmSub

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kizitonwose.calendarview.utils.persian.PersianCalendar
import com.vearad.vearatick.DataBase.EfficiencyDao
import com.vearad.vearatick.DataBase.EfficiencyEmployee
import com.vearad.vearatick.DataBase.Employee
import com.vearad.vearatick.databinding.FragmentEmployeeStatisticsBinding

class EmployeeStatisticsFragment(
    val employee: Employee,
    val efficiencyEmployeeDao: EfficiencyDao,
    val position: Int
) :
    Fragment() {

    lateinit var binding: FragmentEmployeeStatisticsBinding
    lateinit var efficiencyEmployee: EfficiencyEmployee


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

        efficiencyEmployee = efficiencyEmployeeDao.getEfficiencyEmployee(employee.idEmployee!!)!!
        val day = PersianCalendar()

        if (day.persianWeekDayName.toString() == "\u062c\u0645\u0639\u0647") {

            val weekWatch = efficiencyEmployee.totalWeekWatch
            var monthWatch = efficiencyEmployee.totalMonthWatch
            monthWatch = weekWatch!! + monthWatch!!

            val efficiencyWeekPresence = efficiencyEmployee.efficiencyWeekPresence
            var efficiencyTotalPresence = efficiencyEmployee.efficiencyTotalPresence
            efficiencyTotalPresence = efficiencyWeekPresence!! + efficiencyTotalPresence!!

            val newEfficiencyEmployee = EfficiencyEmployee(
                idEfficiency = efficiencyEmployee.idEfficiency,
                idEmployee = efficiencyEmployee.idEmployee,
                mustWeekWatch = efficiencyEmployee.mustWeekWatch,
                numberDay = efficiencyEmployee.numberDay,
                totalWatch = efficiencyEmployee.totalWatch,
                efficiencyWeekPresence = efficiencyEmployee.efficiencyWeekPresence,
                efficiencyTotalPresence = efficiencyTotalPresence,
                totalWeekDuties = efficiencyEmployee.totalWeekDuties,
                totalMonthDuties = efficiencyEmployee.totalMonthDuties,
                totalDuties = efficiencyEmployee.totalDuties,
                efficiencyWeekDuties = efficiencyEmployee.efficiencyWeekDuties,
                efficiencyTotalDuties = efficiencyEmployee.efficiencyTotalDuties,
                efficiencyTotal = efficiencyEmployee.efficiencyTotal,
                totalMonthWatch = monthWatch,
                totalWeekWatch = 0,
                efficiencyMonthDuties = efficiencyEmployee.totalMonthDuties,

                )
            efficiencyEmployeeDao.update(newEfficiencyEmployee)

        } else {

            efficiencyEmployee.totalMonthWatch =
                efficiencyEmployee.totalWeekWatch!! + efficiencyEmployee.totalMonthWatch!!
            efficiencyEmployee.efficiencyTotalPresence =
                efficiencyEmployee.efficiencyTotalPresence!! + efficiencyEmployee.efficiencyWeekPresence!!
        }

        if (day.persianDay == 30) {
            efficiencyEmployee.totalWatch = efficiencyEmployee.totalMonthWatch
            efficiencyEmployee.totalMonthWatch = 0

            val monthWatch = efficiencyEmployee.totalMonthWatch
            var totalWatch = efficiencyEmployee.totalWatch
            totalWatch = monthWatch!! + totalWatch!!

            val newEfficiencyEmployee = EfficiencyEmployee(
                idEfficiency = efficiencyEmployee.idEfficiency,
                idEmployee = efficiencyEmployee.idEmployee,
                mustWeekWatch = efficiencyEmployee.mustWeekWatch,
                numberDay = efficiencyEmployee.numberDay,
                efficiencyWeekPresence = efficiencyEmployee.efficiencyWeekPresence,
                efficiencyTotalPresence = efficiencyEmployee.efficiencyTotalPresence,
                totalWeekDuties = efficiencyEmployee.totalWeekDuties,
                totalMonthDuties = efficiencyEmployee.totalMonthDuties,
                totalDuties = efficiencyEmployee.totalDuties,
                efficiencyWeekDuties = efficiencyEmployee.efficiencyWeekDuties,
                efficiencyTotalDuties = efficiencyEmployee.efficiencyTotalDuties,
                efficiencyTotal = efficiencyEmployee.efficiencyTotal,
                totalMonthWatch = 0,
                totalWatch = totalWatch,
                totalWeekWatch = efficiencyEmployee.totalWeekWatch,
                efficiencyMonthDuties = efficiencyEmployee.totalMonthDuties
            )
            efficiencyEmployeeDao.update(newEfficiencyEmployee)
        } else {
            efficiencyEmployee.totalWatch =
                efficiencyEmployee.totalMonthWatch!! + efficiencyEmployee.totalWatch!!

        }

        binding.txtWatchWeek.text = efficiencyEmployee.totalWeekWatch.toString() + " ساعت"
        binding.txtWatchMonth.text = efficiencyEmployee.totalMonthWatch.toString() + " ساعت"
        binding.txtWatchTotal.text = efficiencyEmployee.totalWatch.toString() + " ساعت"

        binding.progressTotalPresence.setPercent(efficiencyEmployee.efficiencyTotalPresence!!.toInt())
        binding.txtTotalPresence.text =
            efficiencyEmployee.efficiencyTotalPresence!!.toString() + " %"

        if (day.persianWeekDayName.toString() == "\u062c\u0645\u0639\u0647") {
            val weekDuties = efficiencyEmployee.totalWeekDuties
            var monthDuties = efficiencyEmployee.totalMonthDuties
            monthDuties = weekDuties!! + monthDuties!!

            val efficiencyWeekDuties = efficiencyEmployee.efficiencyWeekDuties
            var efficiencyMonthDuties = efficiencyEmployee.efficiencyMonthDuties
            efficiencyMonthDuties = efficiencyWeekDuties!! + efficiencyMonthDuties!!

            val newEfficiencyEmployee = EfficiencyEmployee(
                idEfficiency = efficiencyEmployee.idEfficiency,
                idEmployee = efficiencyEmployee.idEmployee,
                mustWeekWatch = efficiencyEmployee.mustWeekWatch,
                numberDay = efficiencyEmployee.numberDay,
                totalWatch = efficiencyEmployee.totalWatch,
                efficiencyWeekPresence = efficiencyEmployee.efficiencyWeekPresence,
                efficiencyTotalPresence = efficiencyEmployee.efficiencyTotalPresence,
                totalWeekDuties = 0,
                totalMonthDuties = monthDuties,
                totalDuties = efficiencyEmployee.totalDuties,
                efficiencyWeekDuties = 0,
                efficiencyTotalDuties = efficiencyEmployee.efficiencyTotalDuties,
                efficiencyTotal = efficiencyEmployee.efficiencyTotal,
                totalMonthWatch = efficiencyEmployee.totalMonthWatch,
                totalWeekWatch = efficiencyEmployee.totalWeekWatch,
                efficiencyMonthDuties = efficiencyMonthDuties
            )
            efficiencyEmployeeDao.update(newEfficiencyEmployee)

        } else {
            efficiencyEmployee.totalMonthDuties =
                efficiencyEmployee.totalWeekDuties!! + efficiencyEmployee.totalMonthDuties!!

            efficiencyEmployee.efficiencyMonthDuties =
                efficiencyEmployee.efficiencyWeekDuties!! + efficiencyEmployee.efficiencyMonthDuties!!


        }

        if (day.persianDay == 30) {

            var totalDuties = efficiencyEmployee.totalDuties
            val monthDuties = efficiencyEmployee.totalMonthDuties
            totalDuties = monthDuties!! + totalDuties!!

            val efficiencyMonthDuties = efficiencyEmployee.efficiencyMonthDuties
            var efficiencyTotalDuties = efficiencyEmployee.efficiencyTotalDuties
            efficiencyTotalDuties = efficiencyTotalDuties!! + efficiencyMonthDuties!!

            val newEfficiencyEmployee = EfficiencyEmployee(
                idEfficiency = efficiencyEmployee.idEfficiency,
                idEmployee = efficiencyEmployee.idEmployee,
                mustWeekWatch = efficiencyEmployee.mustWeekWatch,
                numberDay = efficiencyEmployee.numberDay,
                totalWatch = efficiencyEmployee.totalWatch,
                efficiencyWeekPresence = efficiencyEmployee.efficiencyWeekPresence,
                efficiencyTotalPresence = efficiencyEmployee.efficiencyTotalPresence,
                totalWeekDuties = efficiencyEmployee.totalWeekDuties,
                totalMonthDuties = 0,
                totalDuties = totalDuties,
                efficiencyWeekDuties = efficiencyEmployee.efficiencyWeekDuties,
                efficiencyTotalDuties = efficiencyTotalDuties,
                efficiencyTotal = efficiencyEmployee.efficiencyTotal,
                totalMonthWatch = efficiencyEmployee.totalMonthWatch,
                totalWeekWatch = efficiencyEmployee.totalWeekWatch,
                efficiencyMonthDuties = 0

            )
            efficiencyEmployeeDao.update(newEfficiencyEmployee)
        } else {
            efficiencyEmployee.totalDuties =
                efficiencyEmployee.totalMonthDuties!! + efficiencyEmployee.totalDuties!!
            efficiencyEmployee.efficiencyTotalDuties =
                efficiencyEmployee.efficiencyTotalDuties!! + efficiencyEmployee.efficiencyMonthDuties!!
        }

        binding.txtTackWeek.text = efficiencyEmployee.totalWeekDuties.toString() + " تا"
        binding.txtTackMonth.text = efficiencyEmployee.totalMonthDuties.toString() + " تا"
        binding.txtTackTotal.text = efficiencyEmployee.totalDuties.toString() + " تا"

        binding.progressWeekDuties.setPercent(efficiencyEmployee.efficiencyWeekDuties!!.toInt())
        binding.txtWeekDuties.text = efficiencyEmployee.efficiencyWeekDuties!!.toString() + " %"
        binding.progressMonthDuties.setPercent(efficiencyEmployee.efficiencyMonthDuties!!.toInt())
        binding.txtMonthDuties.text =
            efficiencyEmployee.efficiencyMonthDuties!!.toString() + " %"
        binding.progressTotalDuties.setPercent(efficiencyEmployee.efficiencyTotalDuties!!.toInt())
        binding.txtTotalDuties.text =
            efficiencyEmployee.efficiencyTotalDuties!!.toString() + " %"
    }

    override fun onResume() {
        super.onResume()
        efficiencyEmployee = efficiencyEmployeeDao.getEfficiencyEmployee(employee.idEmployee!!)!!
    }
}