package ir.aliza.sherkatmanage.fgmSub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import ir.aliza.sherkatmanage.DataBase.AppDatabase
import ir.aliza.sherkatmanage.DataBase.EfficiencyDao
import ir.aliza.sherkatmanage.DataBase.Employee
import ir.aliza.sherkatmanage.DataBase.EmployeeDao
import ir.aliza.sherkatmanage.adapter.SalaryEmployeeAdapter
import ir.aliza.sherkatmanage.databinding.FragmentSalaryEmployeeBinding

class SalaryEmployeeFragment : Fragment(), SalaryEmployeeAdapter.EmployeeEvents {

    lateinit var binding: FragmentSalaryEmployeeBinding
    lateinit var employeeDao: EmployeeDao
    lateinit var efficiencyEmployeeDao: EfficiencyDao
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
        efficiencyEmployeeDao = AppDatabase.getDataBase(view.context).efficiencyDao
        val employeeData = employeeDao.rankEmployee("کارمند")
        binding.rcvEmployee.adapter = SalaryEmployeeAdapter(ArrayList(employeeData), this, efficiencyEmployeeDao)
        binding.rcvEmployee.layoutManager = LinearLayoutManager(context)

    }

    override fun onEmployeeClicked(employee: Employee, position: Int) {
        TODO("Not yet implemented")
    }

    override fun onEmployeeLongClicked(employee: Employee, position: Int) {
        TODO("Not yet implemented")
    }

}