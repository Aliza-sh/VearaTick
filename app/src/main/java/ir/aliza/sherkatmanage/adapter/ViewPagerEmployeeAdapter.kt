package ir.aliza.sherkatmanage.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import ir.aliza.sherkatmanage.DataBase.Employee
import ir.aliza.sherkatmanage.fgmSub.CalendarFragment
import ir.aliza.sherkatmanage.fgmSub.StatisticsFragment
import ir.aliza.sherkatmanage.fgmSub.TaskEmployeeFragment

class ViewPagerEmployeeAdapter(val employee: Employee,fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> StatisticsFragment(employee)

            1 -> CalendarFragment(employee)

            2 -> TaskEmployeeFragment(employee)

            else -> Fragment()
        }
    }
}