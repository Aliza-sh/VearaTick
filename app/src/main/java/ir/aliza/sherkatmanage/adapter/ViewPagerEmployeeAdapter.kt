package ir.aliza.sherkatmanage.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import ir.aliza.sherkatmanage.DataBase.EfficiencyDao
import ir.aliza.sherkatmanage.DataBase.Employee
import ir.aliza.sherkatmanage.DataBase.EmployeeDao
import ir.aliza.sherkatmanage.databinding.ActivityProAndEmpBinding
import ir.aliza.sherkatmanage.fgmSub.EmployeeCalendarFragment
import ir.aliza.sherkatmanage.fgmSub.EmployeeStatisticsFragment
import ir.aliza.sherkatmanage.fgmSub.EmployeeTaskFragment

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