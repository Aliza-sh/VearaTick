package com.vearad.vearatick.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.vearad.vearatick.fgmSub.SalaryApprenticesFragment
import com.vearad.vearatick.fgmSub.SalaryEmployeeFragment
import com.vearad.vearatick.fgmSub.SalaryShareholdersFragment

class ViewPagerPaymentEmployeesSalaryAdapter(
    fragment: Fragment,
    ) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> SalaryShareholdersFragment()

            1 -> SalaryEmployeeFragment()

            2 -> SalaryApprenticesFragment()

            else -> Fragment()
        }
    }
}