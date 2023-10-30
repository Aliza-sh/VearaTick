package com.vearad.vearatick.fgmSub

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.vearad.vearatick.CompanyPaymentActivity
import com.vearad.vearatick.DataBase.EfficiencyDao
import com.vearad.vearatick.DataBase.EmployeeDao
import com.vearad.vearatick.adapter.ViewPagerPaymentEmployeesSalaryAdapter
import com.vearad.vearatick.databinding.FragmentSalaryEmployeesBinding

class SalaryEmployeeSFragment : Fragment(){

    lateinit var binding: FragmentSalaryEmployeesBinding
    lateinit var employeeDao: EmployeeDao
    lateinit var efficiencyEmployeeDao: EfficiencyDao
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSalaryEmployeesBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBackPressed()
        binding.btnBck.setOnClickListener {
            val intent = Intent(requireContext(), CompanyPaymentActivity::class.java)
            startActivity(intent)
        }
            val myAdapter = ViewPagerPaymentEmployeesSalaryAdapter(this)
            binding.viewpagerEmp.adapter = myAdapter
            binding.viewpagerEmp.offscreenPageLimit = 2
            val mediator = TabLayoutMediator(
                binding.tablayoutEmp,
                binding.viewpagerEmp,
                object : TabLayoutMediator.TabConfigurationStrategy {
                    override fun onConfigureTab(tab: TabLayout.Tab, position: Int) {
                        when (position) {
                            0 -> tab.text = "سهام داران"

                            1 -> tab.text = "کارمندان"

                            2 -> tab.text = "کارآموزان"

                        }
                    }
                })
            mediator.attach()
    }
    fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val intent = Intent(requireContext(), CompanyPaymentActivity::class.java)
                    startActivity(intent)
                }
            })
    }
}