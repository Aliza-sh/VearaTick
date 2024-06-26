package com.vearad.vearatick.ui.fragmentssub

import com.vearad.vearatick.utils.TopMarginItemDecoration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.vearad.vearatick.ui.activitysub.CompanyPaymentActivity
import com.vearad.vearatick.model.db.AppDatabase
import com.vearad.vearatick.model.db.Employee
import com.vearad.vearatick.model.db.EmployeeDao
import com.vearad.vearatick.model.db.EmployeeHarvestDao
import com.vearad.vearatick.R
import com.vearad.vearatick.adapter.SalaryApprenticesAdapter
import com.vearad.vearatick.databinding.FragmentSalaryApprenticesBinding

class SalaryApprenticesFragment : Fragment(), SalaryApprenticesAdapter.ApprenticesEvents {

    lateinit var binding: FragmentSalaryApprenticesBinding
    lateinit var employeeDao: EmployeeDao
    lateinit var employeeHarvestDao: EmployeeHarvestDao
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSalaryApprenticesBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        employeeDao = AppDatabase.getDataBase(view.context).employeeDao
        employeeHarvestDao = AppDatabase.getDataBase(view.context).employeeHarvestDao
        val employeeData = employeeDao.rankEmployee("کارآموز")
        binding.rcvApprentices.adapter = SalaryApprenticesAdapter(ArrayList(employeeData), employeeHarvestDao,this)
        binding.rcvApprentices.layoutManager = LinearLayoutManager(context)
        val topMargin = 130 // اندازه مارجین بالا را از منابع دریافت کنید
        val itemDecoration = TopMarginItemDecoration(topMargin)
        binding.rcvApprentices.addItemDecoration(itemDecoration)

    }
    override fun onApprenticesClicked(employee: Employee, position: Int) {}

    override fun onApprenticesLongClicked(employee: Employee, position: Int) {}

    override fun onBtnHarvestClick(
        employee: Employee,
        employeeInvestmentDao: EmployeeHarvestDao,
        position: Int
    ) {
        val transaction =
            (activity as CompanyPaymentActivity).supportFragmentManager.beginTransaction()
        transaction.replace(
            R.id.layout_company_payment,
            SalaryApprenticesHarvestFragment(
                employee,
                employeeHarvestDao
            )
        )
            .addToBackStack(null)
            .commit()
    }

}