package ir.aliza.sherkatmanage.fgmSub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import ir.aliza.sherkatmanage.CompanyPaymentActivity
import ir.aliza.sherkatmanage.DataBase.AppDatabase
import ir.aliza.sherkatmanage.DataBase.EfficiencyDao
import ir.aliza.sherkatmanage.DataBase.Employee
import ir.aliza.sherkatmanage.DataBase.EmployeeDao
import ir.aliza.sherkatmanage.DataBase.EmployeeHarvestDao
import ir.aliza.sherkatmanage.DataBase.EmployeeInvestmentDao
import ir.aliza.sherkatmanage.R
import ir.aliza.sherkatmanage.adapter.SalaryShareholdersAdapter
import ir.aliza.sherkatmanage.databinding.FragmentSalaryShareholdersBinding

class SalaryShareholdersFragment : Fragment(), SalaryShareholdersAdapter.ShareholdersEvents {

    lateinit var binding: FragmentSalaryShareholdersBinding
    lateinit var employeeDao: EmployeeDao
    lateinit var employeeInvestmentDao: EmployeeInvestmentDao
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
        employeeInvestmentDao = AppDatabase.getDataBase(view.context).employeeInvestmentDao
        val shareholdersData = employeeDao.rankEmployee("سهام دار")
        binding.rcvShareholder.adapter = SalaryShareholdersAdapter(
            ArrayList(shareholdersData),
            employeeHarvestDao,
            employeeInvestmentDao,
            this
        )
        binding.rcvShareholder.layoutManager = LinearLayoutManager(context)

    }

    override fun onPaymentShareholdersClicked(employee: Employee, position: Int) {}

    override fun onPaymentShareholdersLongClicked(employee: Employee, position: Int) {}

    override fun onBtnInvestmentClick(
        employee: Employee,
        employeeInvestmentDao: EmployeeInvestmentDao,
        position: Int
    ) {
        val transaction =
            (activity as CompanyPaymentActivity).supportFragmentManager.beginTransaction()
        transaction.replace(
            R.id.layout_company_payment,
            SalaryShareholdersInvestmentFragment(
                employee,
                employeeInvestmentDao
            )
        )
            .addToBackStack(null)
            .commit()
    }

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