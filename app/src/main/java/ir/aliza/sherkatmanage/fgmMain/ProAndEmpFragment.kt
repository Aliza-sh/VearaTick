package ir.aliza.sherkatmanage.fgmMain

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import ir.aliza.sherkatmanage.R
import ir.aliza.sherkatmanage.adapter.ViewPagerCompanyAdapter
import ir.aliza.sherkatmanage.databinding.FragmentProAndEmpBinding

class ProAndEmpFragment : Fragment() {

    lateinit var binding: FragmentProAndEmpBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProAndEmpBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val myAdapter = ViewPagerCompanyAdapter(this)
        binding.viewpagerMain.adapter = myAdapter
        binding.viewpagerMain.offscreenPageLimit = 2

        val mediator = TabLayoutMediator(
            binding.tablayoutMain,
            binding.viewpagerMain,
            object : TabLayoutMediator.TabConfigurationStrategy {
                override fun onConfigureTab(tab: TabLayout.Tab, position: Int) {
                    when (position) {
                        0 -> {
                            tab.text = "پروژه ها "
                            tab.setIcon(R.drawable.ic_tack)
                        }
                        1 -> {tab.text = "کارکنان "
                            tab.setIcon(R.drawable.ic_employee)
                        }
                    }
                }
            })
        mediator.attach()
    }

}