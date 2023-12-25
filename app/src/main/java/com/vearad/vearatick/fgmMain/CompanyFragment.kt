package com.vearad.vearatick.fgmMain

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.view.Window
import androidx.compose.ui.graphics.toArgb
import androidx.fragment.app.Fragment
import com.ghanshyam.graphlibs.Graph
import com.ghanshyam.graphlibs.GraphData
import com.vearad.vearatick.CHEKBUY
import com.vearad.vearatick.CompanyFinancialReportActivity
import com.vearad.vearatick.CompanyIncomeActivity
import com.vearad.vearatick.CompanyPaymentActivity
import com.vearad.vearatick.DataBase.AppDatabase
import com.vearad.vearatick.DataBase.CompanyExpensesDao
import com.vearad.vearatick.DataBase.CompanyReceiptDao
import com.vearad.vearatick.DataBase.EfficiencyDao
import com.vearad.vearatick.DataBase.EmployeeHarvestDao
import com.vearad.vearatick.DataBase.EmployeeInvestmentDao
import com.vearad.vearatick.DataBase.ProjectDao
import com.vearad.vearatick.DataBase.SubTaskProjectDao
import com.vearad.vearatick.MainActivity
import com.vearad.vearatick.PoolakeyActivity
import com.vearad.vearatick.ProAndEmpActivity
import com.vearad.vearatick.R
import com.vearad.vearatick.SHAREDVEARATICK
import com.vearad.vearatick.ShareholdersInvestmentActivity
import com.vearad.vearatick.databinding.FragmentCompanyBinding
import com.vearad.vearatick.databinding.FragmentDialogIncreaseCreditBinding
import com.vearad.vearatick.databinding.ItemProjectBinding
import com.vearad.vearatick.fgmSub.ProjectNumberFragment
import java.text.DecimalFormat


class CompanyFragment : Fragment() {

    lateinit var binding: FragmentCompanyBinding
    lateinit var bindingDialogIncreaseCredit: FragmentDialogIncreaseCreditBinding
    lateinit var bindingItemProject: ItemProjectBinding
    lateinit var projectDao: ProjectDao
    lateinit var efficiencyDao: EfficiencyDao
    lateinit var subTaskProjectDao: SubTaskProjectDao
    lateinit var companyReceiptDao: CompanyReceiptDao
    lateinit var companyExpensesDao: CompanyExpensesDao
    lateinit var employeeHarvestDao: EmployeeHarvestDao
    lateinit var employeeInvestmentDao: EmployeeInvestmentDao
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCompanyBinding.inflate(layoutInflater, container, false)
        bindingItemProject = ItemProjectBinding.inflate(layoutInflater, container, false)
        bindingDialogIncreaseCredit = FragmentDialogIncreaseCreditBinding.inflate(layoutInflater, container, false)
        return binding.root

    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireActivity().getSharedPreferences(SHAREDVEARATICK, Context.MODE_PRIVATE)

        if (sharedPreferences.getBoolean(CHEKBUY, false)== true) {
            binding.cardLock.visibility = GONE
        }

        binding.btnFinancial.setOnClickListener {
            if (sharedPreferences.getBoolean(CHEKBUY, false)== false) {
                showIncreaseCreditDialog()
            }
        }

        efficiencyDao = AppDatabase.getDataBase(view.context).efficiencyDao
        projectDao = AppDatabase.getDataBase(view.context).projectDao
        subTaskProjectDao = AppDatabase.getDataBase(view.context).subTaskProjectDao
        companyReceiptDao = AppDatabase.getDataBase(view.context).companyReceiptDao
        companyExpensesDao = AppDatabase.getDataBase(view.context).companyExpensesDao
        employeeHarvestDao = AppDatabase.getDataBase(view.context).employeeHarvestDao
        employeeInvestmentDao = AppDatabase.getDataBase(view.context).employeeInvestmentDao

        binding.btnInvestment.setOnClickListener {
            if (sharedPreferences.getBoolean(CHEKBUY, false) == false) {
                showIncreaseCreditDialog()
            }else {
                val intent = Intent(requireContext(), ShareholdersInvestmentActivity::class.java)
                startActivity(intent)
                activity?.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)

            }
        }

        val sumCompanyReceipt = companyReceiptDao.getReceiptSum()
        binding.txtReceipt.text = formatCurrency(sumCompanyReceipt.toLong())
        binding.btnIncome.setOnClickListener {
            if (sharedPreferences.getBoolean(CHEKBUY, false)== false) {
                showIncreaseCreditDialog()
            }else {
                val intent = Intent(requireContext(), CompanyIncomeActivity::class.java)
                startActivity(intent)
                activity?.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
            }
        }

        val sumCompanyExpenses = companyExpensesDao.getExpensesSum()
        val sumEmployeeHarvest = employeeHarvestDao.getHarvestSum()
        val sumTotalExpenses = sumCompanyExpenses + sumEmployeeHarvest
        binding.txtPayment.text = formatCurrency(sumTotalExpenses.toLong())
        binding.btnExpense.setOnClickListener {
            if (sharedPreferences.getBoolean(CHEKBUY, false)== false) {
                showIncreaseCreditDialog()
            }else {
                val intent = Intent(requireContext(), CompanyPaymentActivity::class.java)
                startActivity(intent)
                activity?.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
            }
        }

        var profit = sumCompanyReceipt - sumTotalExpenses
        if (profit > 0) {
            val value = formatCurrency(profit)
            binding.txtProfit.setTextColor(Color.parseColor("#0E7113"))
            binding.txtProfit.text = value + " +"
        } else if (profit < 0) {
            val value = formatCurrency(-profit)
            binding.txtProfit.setTextColor(Color.parseColor("#c62828"))
            binding.txtProfit.text = value + " -"
        } else {
            binding.txtProfit.text = "0"
        }

        val sumInvestment = employeeInvestmentDao.getInvestmentSum()
        var sumTotal = (sumInvestment + profit)
        if (sumTotal<0)
            sumTotal = 0
        binding.btnInvestment.text = formatCurrency(sumTotal)
        binding.btnFinancialReport.setOnClickListener {
            if (sharedPreferences.getBoolean(CHEKBUY, false)== false) {
                showIncreaseCreditDialog()
            }else {
                val intent = Intent(requireContext(), CompanyFinancialReportActivity::class.java)
                startActivity(intent)
                activity?.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)

            }
        }

        binding.progressEfficiencyPro.setPercent(efficiencyProject())
        binding.txtEfficiencyPro.text = efficiencyProject().toString() + "%"

        binding.progressEfficiencyEmpTask.setPercent(efficiencyEmployeeTack())
        binding.txtEfficiencyEmpTask.text = efficiencyEmployeeTack().toString() + "%"

        binding.progressEfficiencyEmpPresence.setPercent(efficiencyEmployeePresence())
        binding.txtEfficiencyEmpPresence.text = efficiencyEmployeePresence().toString() + "%"

        binding.btnEffPro.setOnClickListener {
            val intent = Intent(requireContext(), ProAndEmpActivity::class.java)
            intent.putExtra("itemClicked", 1)
            startActivity(intent)
            activity?.overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
        }

        binding.btnEffEmp.setOnClickListener {
            val intent = Intent(requireContext(), ProAndEmpActivity::class.java)
            intent.putExtra("itemClicked", 2)
            startActivity(intent)
            activity?.overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
        }

        binding.btnPreEmp.setOnClickListener {
            val intent = Intent(requireContext(), ProAndEmpActivity::class.java)
            intent.putExtra("itemClicked", 2)
            startActivity(intent)
            activity?.overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
        }

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
        val numSite = projectDao.getNumberProject("سایت",true).size
        val numFrontEnd = projectDao.getNumberProject("فرانت اند", true).size
        val numBackEnd = projectDao.getNumberProject("بک اند", true).size
        val numRobotic = projectDao.getNumberProject("رباتیک", true).size
        val numDsign = projectDao.getNumberProject("طراحی", true).size
        val numSeo = projectDao.getNumberProject("سئو", true).size
        val numTotalProject = projectDao.getAllDoneProject(true).size

        graph.setMinValue(0f)
        graph.setMaxValue(numTotalProject.toFloat())
        graph.setBackgroundShapeWidthInDp(20)
        graph.setForegroundShapeWidthInPx(50)
        graph.setShapeForegroundColor(Color.parseColor("#202020"))
        graph.setShapeBackgroundColor(Color.parseColor("#202020"))

        binding.txtTotalProject.text = " $numTotalProject"
        val data: MutableCollection<GraphData> = ArrayList()
        //اندروید
        data.add(GraphData(numAndroid.toFloat(), Color.parseColor("#97DAE4")))
        //سایت
        data.add(GraphData(numSite.toFloat(), Color.parseColor("#FE7D8B")))
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

    private fun showIncreaseCreditDialog() {
        val parent = bindingDialogIncreaseCredit.root.parent as? ViewGroup
        parent?.removeView(bindingDialogIncreaseCredit.root)
        val dialogBuilder =
            AlertDialog.Builder(bindingDialogIncreaseCredit.root.context)
        dialogBuilder.setView(bindingDialogIncreaseCredit.root)

        val alertDialog = dialogBuilder.create()
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(androidx.compose.ui.graphics.Color.Transparent.toArgb()))
        alertDialog.setCancelable(false)
        alertDialog.show()
        bindingDialogIncreaseCredit.dialogBtnDeleteCansel.setOnClickListener {
            alertDialog.dismiss()
        }
        bindingDialogIncreaseCredit.dialogBtnDeleteSure.setOnClickListener {

            val intent = Intent(requireContext(), PoolakeyActivity::class.java)
            startActivity(intent)

            alertDialog.dismiss()
        }
    }

}