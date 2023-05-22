package ir.aliza.sherkatmanage.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import ir.aliza.sherkatmanage.fgmSub.EmployeeFragment
import ir.aliza.sherkatmanage.fgmSub.ProjectFragment

class ViewPagerCompanyAdapter(fragment: Fragment) :
    FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ProjectFragment()

            1 -> EmployeeFragment()

            else -> Fragment()
        }
    }
}