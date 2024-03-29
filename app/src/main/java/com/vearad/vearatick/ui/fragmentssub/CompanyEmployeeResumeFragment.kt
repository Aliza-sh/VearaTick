package com.vearad.vearatick.ui.fragmentssub

import com.vearad.vearatick.utils.BottomMarginItemDecoration
import com.vearad.vearatick.utils.TopMarginItemDecoration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.vearad.vearatick.utils.BottomSheetCallback
import com.vearad.vearatick.model.db.AppDatabase
import com.vearad.vearatick.model.db.Employee
import com.vearad.vearatick.model.db.EmployeeDao
import com.vearad.vearatick.ui.MainActivity
import com.vearad.vearatick.R
import com.vearad.vearatick.adapter.CompanyEmployeeResumeAdapter
import com.vearad.vearatick.databinding.FragmentCompanyEmployeeResumeBinding

class CompanyEmployeeResumeFragment : Fragment(), CompanyEmployeeResumeAdapter.EmployeeResumeEvent {

    lateinit var binding: FragmentCompanyEmployeeResumeBinding
    lateinit var companyEmployeeResumeAdapter: CompanyEmployeeResumeAdapter
    lateinit var companyEmployeeResumeDao: EmployeeDao
    private var callback: BottomSheetCallback? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            FragmentCompanyEmployeeResumeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        companyEmployeeResumeDao = AppDatabase.getDataBase(view.context).employeeDao
        val companyEmployeeResumeData = companyEmployeeResumeDao.getAllEmployee()
        companyEmployeeResumeAdapter =
            CompanyEmployeeResumeAdapter(
                ArrayList(companyEmployeeResumeData),
                this,
            )
        binding.rcvEmployeeResume.layoutManager = LinearLayoutManager(context)
        binding.rcvEmployeeResume.adapter = companyEmployeeResumeAdapter
        topMargin()
        bottomMargin()

    }
    private fun topMargin() {
        val topMargin = 190 // اندازه مارجین بالا را از منابع دریافت کن
        val itemDecoratio = TopMarginItemDecoration(topMargin)
        binding.rcvEmployeeResume.addItemDecoration(itemDecoratio)
    }

    private fun bottomMargin() {
        val bottomMargin = 220 // اندازه مارجین پایین را از منابع دریافت کنید
        val itemDecoration = BottomMarginItemDecoration(bottomMargin)
        binding.rcvEmployeeResume.addItemDecoration(itemDecoration)
    }

    override fun onResumeClicked(companyEmployeeResume: Employee, position: Int) {
        val transaction = (activity as MainActivity).supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_layout_main2,
            com.vearad.vearatick.ui.fragmentssub.EmployeeResumeFragment(
                companyEmployeeResume,
                companyEmployeeResumeDao
            )
        )
            .addToBackStack(null)
            .commit()    }
}