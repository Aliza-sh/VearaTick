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
import com.vearad.vearatick.model.db.EfficiencyDao
import com.vearad.vearatick.model.db.Employee
import com.vearad.vearatick.model.db.EmployeeDao
import com.vearad.vearatick.model.db.EmployeeHarvestDao
import com.vearad.vearatick.R
import com.vearad.vearatick.adapter.SalaryShareholdersAdapter
import com.vearad.vearatick.databinding.FragmentSalaryShareholdersBinding

class SalaryShareholdersFragment : Fragment(), SalaryShareholdersAdapter.ShareholdersEvents {

    lateinit var binding: FragmentSalaryShareholdersBinding
    lateinit var employeeDao: EmployeeDao
    lateinit var employeeHarvestDao: EmployeeHarvestDao
    lateinit var efficiencyEmployeeDao: EfficiencyDao

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSalaryShareholdersBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //efficiencyEmployeeDao = AppDatabase.getDataBase(view.context).efficiencyDao

        employeeDao = AppDatabase.getDataBase(view.context).employeeDao
        employeeHarvestDao = AppDatabase.getDataBase(view.context).employeeHarvestDao
        val shareholdersData = employeeDao.rankEmployee("سهام دار")
        binding.rcvShareholder.adapter = SalaryShareholdersAdapter(
            ArrayList(shareholdersData),
            employeeHarvestDao,
            this
        )
        binding.rcvShareholder.layoutManager = LinearLayoutManager(context)
        val topMargin = 130 // اندازه مارجین بالا را از منابع دریافت کنید
        val itemDecoration = TopMarginItemDecoration(topMargin)
        binding.rcvShareholder.addItemDecoration(itemDecoration)

    }

    override fun onPaymentShareholdersClicked(employee: Employee, position: Int) {}

    override fun onPaymentShareholdersLongClicked(employee: Employee, position: Int) {}

    override fun onBtnPaymentClick(
        employee: Employee,
        employeeHarvestDao: EmployeeHarvestDao,
        position: Int
    ) {
        val transaction =
            (activity as CompanyPaymentActivity).supportFragmentManager.beginTransaction()
        transaction.replace(
            R.id.layout_company_payment,
            SalaryShareholdersHarvestFragment(
                employee,
                employeeHarvestDao
            )
        )
            .addToBackStack(null)
            .commit()
    }
}