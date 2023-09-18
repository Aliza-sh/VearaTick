package ir.aliza.sherkatmanage.fgmSub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import ir.aliza.sherkatmanage.CompanyPaymentActivity
import ir.aliza.sherkatmanage.DataBase.AppDatabase
import ir.aliza.sherkatmanage.DataBase.Employee
import ir.aliza.sherkatmanage.DataBase.EmployeeDao
import ir.aliza.sherkatmanage.DataBase.EmployeeHarvestDao
import ir.aliza.sherkatmanage.R
import ir.aliza.sherkatmanage.adapter.SalaryEmployeeAdapter
import ir.aliza.sherkatmanage.databinding.FragmentSalaryEmployeeBinding

class SalaryEmployeeFragment : Fragment(), SalaryEmployeeAdapter.EmployeeEvents {

    lateinit var binding: FragmentSalaryEmployeeBinding
    lateinit var employeeDao: EmployeeDao
    lateinit var employeeHarvestDao: EmployeeHarvestDao
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSalaryEmployeeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        employeeDao = AppDatabase.getDataBase(view.context).employeeDao
        employeeHarvestDao = AppDatabase.getDataBase(view.context).employeeHarvestDao
        val employeeData = employeeDao.rankEmployee("کارمند")
        binding.rcvEmployee.adapter = SalaryEmployeeAdapter(ArrayList(employeeData),employeeHarvestDao,this)
        binding.rcvEmployee.layoutManager = LinearLayoutManager(context)

    }

    override fun onEmployeeClicked(employee: Employee, position: Int) {}

    override fun onEmployeeLongClicked(employee: Employee, position: Int) {}

    override fun onBtnHarvestClick(
        employee: Employee,
        employeeInvestmentDao: EmployeeHarvestDao,
        position: Int
    ) {
        val transaction =
            (activity as CompanyPaymentActivity).supportFragmentManager.beginTransaction()
        transaction.replace(
            R.id.layout_company_payment,
            SalaryEmployeeHarvestFragment(
                employee,
                employeeHarvestDao
            )
        )
            .addToBackStack(null)
            .commit()
    }

}