package com.vearad.vearatick.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.vearad.vearatick.DataBase.EfficiencyDao
import com.vearad.vearatick.DataBase.Employee
import com.vearad.vearatick.DataBase.EmployeeDao
import com.vearad.vearatick.databinding.ActivityProAndEmpBinding
import com.vearad.vearatick.fgmSub.EmployeeCalendarFragment
import com.vearad.vearatick.fgmSub.EmployeeStatisticsFragment
import com.vearad.vearatick.fgmSub.EmployeeTaskFragment

class ViewPagerEmployeeAdapter(
    val employee: Employee,
    fragment: Fragment,
    val efficiencyEmployeeDao: EfficiencyDao,
    val position: Int,
    val employeeDao: EmployeeDao,
    val bindingActivityProAndEmpBinding: ActivityProAndEmpBinding,

    ) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> EmployeeStatisticsFragment(employee, efficiencyEmployeeDao, position)

            1 -> EmployeeCalendarFragment(employee, efficiencyEmployeeDao, position)

            2 -> EmployeeTaskFragment(
                employee,
                employeeDao,
                efficiencyEmployeeDao,
                position,
                bindingActivityProAndEmpBinding
            )

            else -> Fragment()
        }
    }
}