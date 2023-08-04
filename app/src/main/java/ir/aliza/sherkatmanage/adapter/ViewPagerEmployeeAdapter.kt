package ir.aliza.sherkatmanage.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import ir.aliza.sherkatmanage.DataBase.EfficiencyDao
import ir.aliza.sherkatmanage.DataBase.Employee
import ir.aliza.sherkatmanage.fgmSub.EmployeeCalendarFragment
import ir.aliza.sherkatmanage.fgmSub.EmployeeStatisticsFragment
import ir.aliza.sherkatmanage.fgmSub.EmployeeTaskFragment

class ViewPagerEmployeeAdapter(
    val employee: Employee,
    fragment: Fragment,
    val efficiencyEmployeeDao: EfficiencyDao
) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> EmployeeStatisticsFragment(employee,efficiencyEmployeeDao)

            1 -> EmployeeCalendarFragment(employee,efficiencyEmployeeDao)

            2 -> EmployeeTaskFragment(employee,efficiencyEmployeeDao)

            else -> Fragment()
        }
    }
}