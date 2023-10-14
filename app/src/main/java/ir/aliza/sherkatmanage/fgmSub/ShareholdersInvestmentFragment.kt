package ir.aliza.sherkatmanage.fgmSub

import TopMarginItemDecoration
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import ir.aliza.sherkatmanage.DataBase.AppDatabase
import ir.aliza.sherkatmanage.DataBase.EfficiencyDao
import ir.aliza.sherkatmanage.DataBase.Employee
import ir.aliza.sherkatmanage.DataBase.EmployeeDao
import ir.aliza.sherkatmanage.DataBase.EmployeeHarvestDao
import ir.aliza.sherkatmanage.DataBase.EmployeeInvestmentDao
import ir.aliza.sherkatmanage.MainActivity
import ir.aliza.sherkatmanage.R
import ir.aliza.sherkatmanage.adapter.ShareholdersInvestmentAdapter
import ir.aliza.sherkatmanage.databinding.FragmentShareholdersInvestmentBinding

class ShareholdersInvestmentFragment : Fragment(),
    ShareholdersInvestmentAdapter.ShareholdersInvestmentEvents {

    lateinit var binding: FragmentShareholdersInvestmentBinding
    lateinit var employeeDao: EmployeeDao
    lateinit var efficiencyEmployeeDao: EfficiencyDao
    lateinit var employeeInvestmentDao: EmployeeInvestmentDao
    lateinit var employeeHarvestDao: EmployeeHarvestDao
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentShareholdersInvestmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBackPressed()
        binding.btnBck.setOnClickListener {
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
        }
        employeeDao = AppDatabase.getDataBase(view.context).employeeDao
        employeeInvestmentDao = AppDatabase.getDataBase(view.context).employeeInvestmentDao
        employeeHarvestDao = AppDatabase.getDataBase(view.context).employeeHarvestDao
        val shareholdersData = employeeDao.rankEmployee("سهام دار")
        binding.rcvShareholder.adapter = ShareholdersInvestmentAdapter(
            ArrayList(shareholdersData),
            employeeHarvestDao,
            employeeInvestmentDao,
            this
        )
        binding.rcvShareholder.layoutManager = LinearLayoutManager(context)
        val topMargin = 140 // اندازه مارجین بالا را از منابع دریافت کنید
        val itemDecoration = TopMarginItemDecoration(topMargin)
        binding.rcvShareholder.addItemDecoration(itemDecoration)
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

    override fun onPaymentShareholdersClicked(employee: Employee, position: Int) {}

    override fun onPaymentShareholdersLongClicked(employee: Employee, position: Int) {}

    override fun onBtnInvestmentClick(
        employee: Employee,
        employeeInvestmentDao: EmployeeInvestmentDao,
        position: Int
    ) {
        val transaction =
            (activity as MainActivity).supportFragmentManager.beginTransaction()
        transaction.replace(
            R.id.frame_layout_main2,
            SalaryShareholdersInvestmentFragment(
                employee,
                employeeInvestmentDao
            )
        )
            .addToBackStack(null)
            .commit()
    }
}