package com.vearad.vearatick.fgmSub

import BottomMarginItemDecoration
import CustomBottomMarginItemDecoration
import CustomTopMarginItemDecoration
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.vearad.vearatick.DataBase.AppDatabase
import com.vearad.vearatick.DataBase.EfficiencyDao
import com.vearad.vearatick.DataBase.Employee
import com.vearad.vearatick.DataBase.EmployeeDao
import com.vearad.vearatick.MainActivity
import com.vearad.vearatick.ProAndEmpActivity
import com.vearad.vearatick.R
import com.vearad.vearatick.adapter.EmployeeAdapter
import com.vearad.vearatick.databinding.ActivityProAndEmpBinding
import com.vearad.vearatick.databinding.FragmentEmployeesBinding
import com.vearad.vearatick.employeeAdapter

class EmployeeFragment(val bindingActivityProAndEmpBinding: ActivityProAndEmpBinding) : Fragment(),
    EmployeeAdapter.EmployeeEvents {

    lateinit var binding: FragmentEmployeesBinding
    lateinit var efficiencyEmployeeDao: EfficiencyDao
    lateinit var employeeDao: EmployeeDao
    lateinit var employeeData: List<Employee>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEmployeesBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBackPressed()

        efficiencyEmployeeDao = AppDatabase.getDataBase(view.context).efficiencyDao

        employeeDao = AppDatabase.getDataBase(view.context).employeeDao
        employeeData = employeeDao.getAllEmployee()
        employeeAdapter = EmployeeAdapter(ArrayList(employeeData), this, efficiencyEmployeeDao)
        binding.recyclerViewEmployee.adapter = employeeAdapter
        binding.recyclerViewEmployee.layoutManager = GridLayoutManager(context, 2)
        val topMargin = 10 // اندازه مارجین بالا را از منابع دریافت کنید
        val itemDecoratio = CustomTopMarginItemDecoration(topMargin)
        binding.recyclerViewEmployee.addItemDecoration(itemDecoratio)
        binding.recyclerViewEmployee.addItemDecoration(itemDecoratio)
        val itemCount = employeeData.size // تعداد آیتم‌های موجود در لیست را دریافت کنید
        if (itemCount % 2 == 0) {
            val bottomMargin = 100 // اندازه مارجین پایین را از منابع دریافت کنید
            val itemDecoration = CustomBottomMarginItemDecoration(bottomMargin)
            binding.recyclerViewEmployee.addItemDecoration(itemDecoration)
        } else {
            val bottomMargin = 100 // اندازه مارجین پایین را از منابع دریافت کنید
            val itemDecoration = BottomMarginItemDecoration(bottomMargin)
            binding.recyclerViewEmployee.addItemDecoration(itemDecoration)
        }

        onFabClicked()
    }
    fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)
                }
            })
    }
    override fun onResume() {
        super.onResume()
        updateYourData()
    }
    private fun updateYourData() {
        employeeData = employeeDao.getAllEmployee()
        employeeAdapter = EmployeeAdapter(ArrayList(employeeData), this, efficiencyEmployeeDao)
        binding.recyclerViewEmployee.adapter = employeeAdapter
        val topMargin = 10 // اندازه مارجین بالا را از منابع دریافت کنید
        val itemDecoratio = CustomTopMarginItemDecoration(topMargin)
        binding.recyclerViewEmployee.addItemDecoration(itemDecoratio)
        val itemCount = employeeData.size // تعداد آیتم‌های موجود در لیست را دریافت کنید
        if (itemCount % 2 == 0) {
            val bottomMargin = 100 // اندازه مارجین پایین را از منابع دریافت کنید
            val itemDecoration = CustomBottomMarginItemDecoration(bottomMargin)
            binding.recyclerViewEmployee.addItemDecoration(itemDecoration)
        } else {
            val bottomMargin = 100 // اندازه مارجین پایین را از منابع دریافت کنید
            val itemDecoration = BottomMarginItemDecoration(bottomMargin)
            binding.recyclerViewEmployee.addItemDecoration(itemDecoration)
        }
    }
    fun onFabClicked() {
        bindingActivityProAndEmpBinding.btnAdd.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(
                R.id.layout_pro_and_emp,
                EmployeeRecruitmentFragment(bindingActivityProAndEmpBinding, efficiencyEmployeeDao)
            )
                .addToBackStack(null)
                .commit()
        }
    }
    override fun onEmployeeClicked(employee: Employee, position: Int) {
        val transaction = (activity as ProAndEmpActivity).supportFragmentManager.beginTransaction()
        transaction.replace(
            R.id.layout_pro_and_emp,
            EmployeeInformationFragment(
                employee,
                efficiencyEmployeeDao,
                position,
                employeeDao,
                bindingActivityProAndEmpBinding
            )
        )
            .addToBackStack(null)
            .commit()
    }
    override fun onEmployeeLongClicked(employee: Employee, position: Int) {}

}