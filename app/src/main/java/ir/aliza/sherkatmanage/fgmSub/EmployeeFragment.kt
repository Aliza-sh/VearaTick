package ir.aliza.sherkatmanage.fgmSub

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import ir.aliza.sherkatmanage.DataBase.AppDatabase
import ir.aliza.sherkatmanage.DataBase.EfficiencyDao
import ir.aliza.sherkatmanage.DataBase.Employee
import ir.aliza.sherkatmanage.DataBase.EmployeeDao
import ir.aliza.sherkatmanage.MainActivity
import ir.aliza.sherkatmanage.ProAndEmpActivity
import ir.aliza.sherkatmanage.R
import ir.aliza.sherkatmanage.adapter.EmployeeAdapter
import ir.aliza.sherkatmanage.databinding.ActivityProAndEmpBinding
import ir.aliza.sherkatmanage.databinding.FragmentEmployeesBinding
import ir.aliza.sherkatmanage.employeeAdapter

class EmployeeFragment(val bindingActivityProAndEmpBinding: ActivityProAndEmpBinding) : Fragment(),
    EmployeeAdapter.EmployeeEvents {

    lateinit var binding: FragmentEmployeesBinding
    lateinit var efficiencyEmployeeDao: EfficiencyDao
    lateinit var employeeDao: EmployeeDao
    lateinit var employeeData: List<Employee>

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
        employeeData = employeeDao.getAllEmployee()
        employeeAdapter = EmployeeAdapter(ArrayList(employeeData), this, efficiencyEmployeeDao)
        binding.recyclerViewEmployee.adapter = employeeAdapter
        binding.recyclerViewEmployee.layoutManager = GridLayoutManager(context, 2)

        onFabClicked()
    }
    fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)
                }
            })
    }
    override fun onResume() {
        super.onResume()
        updateYourData()
    }
    private fun updateYourData() {
        employeeData = employeeDao.getAllEmployee()
        employeeAdapter = EmployeeAdapter(ArrayList(employeeData), this, efficiencyEmployeeDao)
        binding.recyclerViewEmployee.adapter = employeeAdapter
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
    override fun onEmployeeClicked(employee: Employee, position: Int) {
        val transaction = (activity as ProAndEmpActivity).supportFragmentManager.beginTransaction()
        transaction.replace(
            R.id.layout_pro_and_emp,
            EmployeeInformationFragment(
                employee,
                efficiencyEmployeeDao,
                position,
                employeeDao,
                bindingActivityProAndEmpBinding
            )
        )
            .addToBackStack(null)
            .commit()
    }
    override fun onEmployeeLongClicked(employee: Employee, position: Int) {}

}