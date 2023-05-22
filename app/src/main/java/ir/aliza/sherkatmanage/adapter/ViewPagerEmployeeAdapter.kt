package ir.aliza.sherkatmanage.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import ir.aliza.sherkatmanage.fgmSub.CalendarFragment
import ir.aliza.sherkatmanage.fgmSub.TaskFragment
import ir.aliza.sherkatmanage.fgmSub.StatisticsFragment

class ViewPagerEmployeeAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> StatisticsFragment()

            1 -> CalendarFragment()

            2 -> TaskFragment()

            else -> Fragment()
        }
    }
}