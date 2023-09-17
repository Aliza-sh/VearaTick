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
import ir.aliza.sherkatmanage.adapter.SalaryApprenticesAdapter
import ir.aliza.sherkatmanage.adapter.SalaryEmployeeAdapter
import ir.aliza.sherkatmanage.databinding.FragmentSalaryApprenticesBinding

class SalaryApprenticesFragment : Fragment(), SalaryEmployeeAdapter.EmployeeEvents,
    SalaryApprenticesAdapter.EmployeeEvents {

    lateinit var binding: FragmentSalaryApprenticesBinding
    lateinit var employeeDao: EmployeeDao
    lateinit var efficiencyEmployeeDao: EfficiencyDao
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
        efficiencyEmployeeDao = AppDatabase.getDataBase(view.context).efficiencyDao
        val employeeData = employeeDao.rankEmployee("کارآموز")
        binding.rcvApprentices.adapter = SalaryApprenticesAdapter(ArrayList(employeeData), this, efficiencyEmployeeDao)
        binding.rcvApprentices.layoutManager = LinearLayoutManager(context)

    }

    override fun onEmployeeClicked(employee: Employee, position: Int) {
        TODO("Not yet implemented")
    }

    override fun onEmployeeLongClicked(employee: Employee, position: Int) {
        TODO("Not yet implemented")
    }

}