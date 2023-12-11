package com.vearad.vearatick.fgmSub

import BottomMarginItemDecoration
import TopMarginItemDecoration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.vearad.vearatick.DataBase.EmployeeDao
import com.vearad.vearatick.adapter.CompanyEmployeeResumeAdapter
import com.vearad.vearatick.databinding.FragmentCompanyEventBinding

class CompanyEventFragment : Fragment() {

    lateinit var binding: FragmentCompanyEventBinding
    lateinit var companyEmployeeResumeAdapter: CompanyEmployeeResumeAdapter
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

//        companyEmployeeResumeDao = AppDatabase.getDataBase(view.context).employeeDao
//        val companyEmployeeResumeData = companyEmployeeResumeDao.getAllEmployee()
//        companyEmployeeResumeAdapter =
//            CompanyEmployeeResumeAdapter(
//                ArrayList(companyEmployeeResumeData),
//                this,
//            )
//        binding.rcvEmployeeResume.layoutManager = LinearLayoutManager(context)
//        binding.rcvEmployeeResume.adapter = companyEmployeeResumeAdapter
//        topMargin()
//        bottomMargin()

    }
    private fun topMargin() {
        val topMargin = 190 // اندازه مارجین بالا را از منابع دریافت کن
        val itemDecoratio = TopMarginItemDecoration(topMargin)
        //binding.rcvEmployeeResume.addItemDecoration(itemDecoratio)
    }

    private fun bottomMargin() {
        val bottomMargin = 220 // اندازه مارجین پایین را از منابع دریافت کنید
        val itemDecoration = BottomMarginItemDecoration(bottomMargin)
        //binding.rcvEmployeeResume.addItemDecoration(itemDecoration)
    }
}