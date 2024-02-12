package com.vearad.vearatick.ui.fragmentssub

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.vearad.vearatick.R
import com.vearad.vearatick.adapter.EmployeeAdapter
import com.vearad.vearatick.databinding.ActivityProAndEmpBinding
import com.vearad.vearatick.databinding.FragmentEmployeesBinding
import com.vearad.vearatick.model.db.AppDatabase
import com.vearad.vearatick.model.db.EfficiencyDao
import com.vearad.vearatick.model.db.Employee
import com.vearad.vearatick.model.db.EmployeeDao
import com.vearad.vearatick.ui.MainActivity
import com.vearad.vearatick.ui.activitymain.ProAndEmpActivity
import com.vearad.vearatick.ui.employeeAdapter
import com.vearad.vearatick.utils.BottomMarginItemDecoration
import com.vearad.vearatick.utils.CustomBottomMarginItemDecoration
import com.vearad.vearatick.utils.CustomTopMarginItemDecoration

class EmployeeFragment(
    val bindingActivityProAndEmpBinding: ActivityProAndEmpBinding,
    var goFromNotifToEmployeeFragment: Boolean,
    val idEmployee: Int
) : Fragment(),
    EmployeeAdapter.EmployeeEvents {

    lateinit var binding: FragmentEmployeesBinding
    lateinit var efficiencyEmployeeDao: EfficiencyDao
    lateinit var employeeDao: EmployeeDao
    lateinit var employeeData: List<Employee>
    private val goToEmployeeTaskFragment = false

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

        if (goFromNotifToEmployeeFragment) {
            val employee = employeeDao.getEmployee(idEmployee)
            onEmployeeNotification(employee!!, goFromNotifToEmployeeFragment)
        } else {

            employeeData = employeeDao.getAllEmployee()
            employeeAdapter =
                EmployeeAdapter(ArrayList(employeeData), this, efficiencyEmployeeDao)
            binding.recyclerViewEmployee.adapter = employeeAdapter
            binding.recyclerViewEmployee.layoutManager = GridLayoutManager(context, 2)
            val topMargin = 20 // اندازه مارجین بالا را از منابع دریافت کنید
            val itemDecoratio = CustomTopMarginItemDecoration(topMargin)
            binding.recyclerViewEmployee.addItemDecoration(itemDecoratio)
            binding.recyclerViewEmployee.addItemDecoration(itemDecoratio)
            val itemCount = employeeData.size // تعداد آیتم‌های موجود در لیست را دریافت کنید
            if (itemCount % 2 == 0) {
                val bottomMargin = 200 // اندازه مارجین پایین را از منابع دریافت کنید
                val itemDecoration = CustomBottomMarginItemDecoration(bottomMargin)
                binding.recyclerViewEmployee.addItemDecoration(itemDecoration)
            } else {
                val bottomMargin = 200 // اندازه مارجین پایین را از منابع دریافت کنید
                val itemDecoration = BottomMarginItemDecoration(bottomMargin)
                binding.recyclerViewEmployee.addItemDecoration(itemDecoration)
            }

            onFabClicked()
        }
    }
    fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)
                    requireActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
                }
            })
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

    fun onEmployeeNotification(employee: Employee, goFromNotifToEmployeeFragment: Boolean) {
        val transaction = (activity as ProAndEmpActivity).supportFragmentManager.beginTransaction()
        transaction.hide(this@EmployeeFragment)
        transaction.replace(
            R.id.layout_pro_and_emp,
            EmployeeInformationFragment(
                employee,
                efficiencyEmployeeDao,
                0,
                employeeDao,
                bindingActivityProAndEmpBinding,
                goToEmployeeTaskFragment, goFromNotifToEmployeeFragment
            )
        )
            .commit()
    }

    override fun onEmployeeClicked(employee: Employee, position: Int) {
        val transaction = (activity as ProAndEmpActivity).supportFragmentManager.beginTransaction()
        transaction.hide(this@EmployeeFragment)
        transaction.replace(
            R.id.layout_pro_and_emp,
            EmployeeInformationFragment(
                employee,
                efficiencyEmployeeDao,
                position,
                employeeDao,
                bindingActivityProAndEmpBinding,
                goToEmployeeTaskFragment,
                false
            )
        )
            .commit()
    }
    override fun onEmployeeLongClicked(employee: Employee, position: Int) {}

}