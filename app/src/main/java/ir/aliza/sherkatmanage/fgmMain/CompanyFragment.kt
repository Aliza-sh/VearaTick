package ir.aliza.sherkatmanage.fgmMain

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ghanshyam.graphlibs.Graph
import com.ghanshyam.graphlibs.GraphData
import ir.aliza.sherkatmanage.CompanyPaymentActivity
import ir.aliza.sherkatmanage.CompanyReceiptActivity
import ir.aliza.sherkatmanage.DataBase.AppDatabase
import ir.aliza.sherkatmanage.DataBase.CompanyExpensesDao
import ir.aliza.sherkatmanage.DataBase.CompanyReceiptDao
import ir.aliza.sherkatmanage.DataBase.EfficiencyDao
import ir.aliza.sherkatmanage.DataBase.EmployeeHarvestDao
import ir.aliza.sherkatmanage.DataBase.ProjectDao
import ir.aliza.sherkatmanage.DataBase.SubTaskProjectDao
import ir.aliza.sherkatmanage.MainActivity
import ir.aliza.sherkatmanage.R
import ir.aliza.sherkatmanage.databinding.FragmentCompanyBinding
import ir.aliza.sherkatmanage.databinding.ItemProjectBinding
import ir.aliza.sherkatmanage.fgmSub.ProjectNumberFragment
import java.text.DecimalFormat


class CompanyFragment : Fragment() {

    lateinit var binding: FragmentCompanyBinding
    lateinit var bindingItemProject: ItemProjectBinding
    lateinit var projectDao: ProjectDao
    lateinit var efficiencyDao: EfficiencyDao
    lateinit var subTaskProjectDao: SubTaskProjectDao
    lateinit var companyReceiptDao: CompanyReceiptDao
    lateinit var companyExpensesDao: CompanyExpensesDao
    lateinit var employeeHarvestDao: EmployeeHarvestDao

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCompanyBinding.inflate(layoutInflater, container, false)
        bindingItemProject = ItemProjectBinding.inflate(layoutInflater, container, false)

        return binding.root

    }
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        efficiencyDao = AppDatabase.getDataBase(view.context).efficiencyDao
        projectDao = AppDatabase.getDataBase(view.context).projectDao
        subTaskProjectDao = AppDatabase.getDataBase(view.context).subTaskProjectDao
        companyReceiptDao = AppDatabase.getDataBase(view.context).companyReceiptDao
        companyExpensesDao = AppDatabase.getDataBase(view.context).companyExpensesDao
        employeeHarvestDao = AppDatabase.getDataBase(view.context).employeeHarvestDao

        val sumCompanyReceipt = companyReceiptDao.getReceiptSum()
        binding.txtReceipt.text = formatCurrency(sumCompanyReceipt.toLong())
        binding.btnReceipt.setOnClickListener {
            val intent = Intent(requireContext(), CompanyReceiptActivity::class.java)
            startActivity(intent)

        }

        val sumCompanyExpenses = companyExpensesDao.getExpensesSum()
        val sumEmployeeHarvest = employeeHarvestDao.getHarvestSum()
        val sumTotal = sumCompanyExpenses + sumEmployeeHarvest
        binding.txtPayment.text = formatCurrency(sumTotal.toLong())
        binding.btnPayment.setOnClickListener {
            val intent = Intent(requireContext(), CompanyPaymentActivity::class.java)
            startActivity(intent)
        }

        binding.progressEfficiencyPro.setPercent(efficiencyProject())
        binding.txtEfficiencyPro.text = efficiencyProject().toString() + "%"

        binding.progressEfficiencyEmpTask.setPercent(efficiencyEmployeeTack())
        binding.txtEfficiencyEmpTask.text = efficiencyEmployeeTack().toString() + "%"

        binding.progressEfficiencyEmpPresence.setPercent(efficiencyEmployeePresence())
        binding.txtEfficiencyEmpPresence.text = efficiencyEmployeePresence().toString() + "%"

        binding.btnSeeMoreNumPro.setOnClickListener {
            val transaction = (activity as MainActivity).supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_layout_main2, ProjectNumberFragment(projectDao))
                .addToBackStack(null)
                .commit()
        }

        progressNumProject(binding.progressNumProject)

    }
    private fun formatCurrency(value: Long?): String {
        val decimalFormat = DecimalFormat("#,###")
        return decimalFormat.format(value) + " تومان"
    }
    private fun progressNumProject(graph: Graph) {

        val numAndroid = projectDao.getNumberProject("اندروید", true).size
        val numFrontEnd = projectDao.getNumberProject("فرانت اند", true).size
        val numBackEnd = projectDao.getNumberProject("بک اند", true).size
        val numRobotic = projectDao.getNumberProject("رباتیک", true).size
        val numDsign = projectDao.getNumberProject("طراحی", true).size
        val numSeo = projectDao.getNumberProject("سئو", true).size
        val numTotalProject = projectDao.getAllDoneProject(true).size
        var devide = 0.04f

        if (numTotalProject in 11..19)
            devide = 0.1f
        if (numTotalProject in 20..50)
            devide = 0.3f
        if (numTotalProject in 51..100)
            devide = 0.5f
        if (numTotalProject in 101..500)
            devide = 0.7f
        if (numTotalProject in 501..1000)
            devide = 1f

        graph.setMinValue(0f)
        graph.setMaxValue(numTotalProject.toFloat())
        graph.setDevideSize(devide)
        graph.setBackgroundShapeWidthInDp(20)
        graph.setForegroundShapeWidthInPx(50)
        graph.setShapeForegroundColor(Color.parseColor("#202020"))
        graph.setShapeBackgroundColor(Color.parseColor("#202020"))

        binding.txtTotalProject.text = " $numTotalProject"
        val data: MutableCollection<GraphData> = ArrayList()
        //اندروید
        data.add(GraphData(numAndroid.toFloat(), Color.parseColor("#97DAE4")))
        //بک اند
        data.add(GraphData(numBackEnd.toFloat(), Color.parseColor("#4D7E68")))
        //فرانت اند
        data.add(GraphData(numFrontEnd.toFloat(), Color.parseColor("#F2E45B")))
        //رباتیک
        data.add(GraphData(numRobotic.toFloat(), Color.parseColor("#68A7B1")))
        //طراحی
        data.add(GraphData(numDsign.toFloat(), Color.parseColor("#6D9884")))
        //سئو
        data.add(GraphData(numSeo.toFloat(), Color.parseColor("#B6AA3B")))

        graph.setData(data)
    }

    private fun efficiencyProject(): Int {
        val numberProject = projectDao.getAllProject().size
        val sumProgressProject = projectDao.getColumnprogressProject()

        var sumAllProgressProject =
            sumProgressProject.sum()

        if (sumAllProgressProject != 0)
            sumAllProgressProject /= numberProject

        return sumAllProgressProject
    }

    private fun efficiencyEmployeeTack(): Int {

        val numberEmployee = efficiencyDao.getAllEfficiency().size
        val sumEfficiencyWeekDuties = efficiencyDao.getColumnEfficiencyWeekDuties()
        val sumEfficiencyMonthDuties = efficiencyDao.getColumnEfficiencyMonthDuties()
        val sumEfficiencyTotalDuties = efficiencyDao.getColumnEfficiencyTotalDuties()

        var sumEefficiencyEmployeeTack =
            sumEfficiencyWeekDuties.sum() + sumEfficiencyMonthDuties.sum() + sumEfficiencyTotalDuties.sum()

        if (sumEefficiencyEmployeeTack != 0)
            sumEefficiencyEmployeeTack /= numberEmployee

        return sumEefficiencyEmployeeTack
    }

    private fun efficiencyEmployeePresence(): Int {
        val numberEmployee = efficiencyDao.getAllEfficiency().size
        val sumEfficiencyWeekPresence = efficiencyDao.getColumnEfficiencyWeekPresence()
        val sumEfficiencyTotalPresence = efficiencyDao.getColumnEfficiencyTotalPresence()

        var sumEefficiencyEmployeePresence =
            sumEfficiencyWeekPresence.sum() + sumEfficiencyTotalPresence.sum()

        if (sumEefficiencyEmployeePresence != 0)
            sumEefficiencyEmployeePresence /= numberEmployee

        return sumEefficiencyEmployeePresence
    }

}