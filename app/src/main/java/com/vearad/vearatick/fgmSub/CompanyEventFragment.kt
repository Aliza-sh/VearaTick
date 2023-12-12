package com.vearad.vearatick.fgmSub

import BottomMarginItemDecoration
import CustomBottomMarginItemDecoration
import CustomTopMarginItemDecoration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.vearad.vearatick.DataBase.AppDatabase
import com.vearad.vearatick.DataBase.Employee
import com.vearad.vearatick.DataBase.EmployeeDao
import com.vearad.vearatick.adapter.CompanyEventAdapter
import com.vearad.vearatick.databinding.FragmentCompanyEventBinding

class CompanyEventFragment : Fragment(), CompanyEventAdapter.CompanyEventEvent {

    lateinit var binding: FragmentCompanyEventBinding
    lateinit var companyEventAdapter: CompanyEventAdapter
    lateinit var companyEmployeeResumeDao: EmployeeDao
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCompanyEventBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        companyEmployeeResumeDao = AppDatabase.getDataBase(view.context).employeeDao
        val companyEventData = companyEmployeeResumeDao.getAllEmployee()
        companyEventAdapter =
            CompanyEventAdapter(
                ArrayList(companyEventData),
                this,
            )
        binding.rcvEvent.layoutManager = GridLayoutManager(context, 2)
        binding.rcvEvent.adapter = companyEventAdapter

        topMargin()
        bottomMargin(companyEventData)

    }
    private fun topMargin() {
        val topMargin = 270 // اندازه مارجین بالا را از منابع دریافت کنید
        val itemDecoratio = CustomTopMarginItemDecoration(topMargin)
        binding.rcvEvent.addItemDecoration(itemDecoratio)
    }

    private fun bottomMargin(companyEventData: List<Employee>) {
        val itemCount = companyEventData.size // تعداد آیتم‌های موجود در لیست را دریافت کنید
        if (itemCount % 2 == 0) {
            val bottomMargin = 220 // اندازه مارجین پایین را از منابع دریافت کنید
            val itemDecoration = CustomBottomMarginItemDecoration(bottomMargin)
            binding.rcvEvent.addItemDecoration(itemDecoration)
        } else {
            val bottomMargin = 220 // اندازه مارجین پایین را از منابع دریافت کنید
            val itemDecoration = BottomMarginItemDecoration(bottomMargin)
            binding.rcvEvent.addItemDecoration(itemDecoration)
        }
    }

    override fun onEventClicked(companyEmployeeResume: Employee, position: Int) {

    }
}