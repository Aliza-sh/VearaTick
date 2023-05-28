package ir.aliza.sherkatmanage.fgmSub

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import ir.aliza.sherkatmanage.DataBase.Employee
import ir.aliza.sherkatmanage.MainActivity
import ir.aliza.sherkatmanage.R
import ir.aliza.sherkatmanage.adapter.ViewPagerEmployeeAdapter
import ir.aliza.sherkatmanage.databinding.FragmentEmployeeStatisticsBinding

class EmployeeStatisticsFragment(val employee: Employee) : Fragment() {

    lateinit var binding: FragmentEmployeeStatisticsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEmployeeStatisticsBinding.inflate(layoutInflater,container,false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val myAdapter = ViewPagerEmployeeAdapter(employee,this)
        binding.viewpagerEmp.adapter = myAdapter
        binding.viewpagerEmp.offscreenPageLimit = 2

        val mediator = TabLayoutMediator(
            binding.tablayoutEmp,
            binding.viewpagerEmp,
            object : TabLayoutMediator.TabConfigurationStrategy {
                override fun onConfigureTab(tab: TabLayout.Tab, position: Int) {
                    when (position) {
                        0 -> tab.text = "آمار "

                        1 -> tab.text = "تقویم "

                        2 -> tab.text = "وظایف"

                    }
                }
            })
        mediator.attach()

        binding.btnInfoPrn.setOnClickListener{
            val transaction = (activity as MainActivity).supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_layout_main, UpdateInfoEmployeeFragment(employee))
                .addToBackStack(null)
                .commit()
        }
        setData(employee)

    }

    @SuppressLint("SetTextI18n")
    private fun setData(employee: Employee) {

        binding.txtNameEmp.text = employee.name + employee.family
        binding.txtSpecialtyEmp.text = employee.specialty

        if (employee.gender == "زن"){
            binding.btnInfoPrn.setImageResource(R.drawable.img_matter)
        }


    }

}