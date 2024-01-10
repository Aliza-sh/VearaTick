package com.vearad.vearatick.fgmMain

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.Window
import android.widget.FrameLayout
import androidx.compose.ui.graphics.toArgb
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ghanshyam.graphlibs.Graph
import com.ghanshyam.graphlibs.GraphData
import com.google.android.material.snackbar.Snackbar
import com.vearad.vearatick.CHEKEXPIRATION
import com.vearad.vearatick.CompanyFinancialReportActivity
import com.vearad.vearatick.CompanyPaymentActivity
import com.vearad.vearatick.CompanyReceiptActivity
import com.vearad.vearatick.DataBase.AppDatabase
import com.vearad.vearatick.DataBase.CompanyExpensesDao
import com.vearad.vearatick.DataBase.CompanyReceiptDao
import com.vearad.vearatick.DataBase.CompanySkill
import com.vearad.vearatick.DataBase.EfficiencyDao
import com.vearad.vearatick.DataBase.EmployeeHarvestDao
import com.vearad.vearatick.DataBase.EmployeeInvestmentDao
import com.vearad.vearatick.DataBase.ProjectDao
import com.vearad.vearatick.DataBase.SubTaskProjectDao
import com.vearad.vearatick.MainActivity
import com.vearad.vearatick.PoolakeyActivity
import com.vearad.vearatick.ProAndEmpActivity
import com.vearad.vearatick.R
import com.vearad.vearatick.SHAREDEXPIRATIONSUBSCRIPTION
import com.vearad.vearatick.ShareholdersInvestmentActivity
import com.vearad.vearatick.adapter.CompanySkillAdapter
import com.vearad.vearatick.adapter.CompanySkillInCompanyFragmentAdapter
import com.vearad.vearatick.databinding.FragmentCompanyBinding
import com.vearad.vearatick.databinding.FragmentDialogIncreaseCreditBinding
import com.vearad.vearatick.databinding.ItemProjectBinding
import com.vearad.vearatick.fgmSub.ProjectNumberFragment
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class CompanyFragment : Fragment(), CompanySkillAdapter.CompanySkillEvent {

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
    lateinit var date: LocalDate
    lateinit var expirationDate: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCompanyBinding.inflate(layoutInflater, container, false)
        bindingItemProject = ItemProjectBinding.inflate(layoutInflater, container, false)
        bindingDialogIncreaseCredit =
            FragmentDialogIncreaseCreditBinding.inflate(layoutInflater, container, false)
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
        employeeInvestmentDao = AppDatabase.getDataBase(view.context).employeeInvestmentDao

        sharedPreferences = requireActivity().getSharedPreferences(
            SHAREDEXPIRATIONSUBSCRIPTION,
            Context.MODE_PRIVATE
        )

        expirationDate = sharedPreferences.getString(CHEKEXPIRATION, "").toString()
        Log.v("expirationDate", "expiration: ${expirationDate}")

        if (expirationDate != "") {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-d")
            date = LocalDate.parse(expirationDate, formatter)
            val today = LocalDate.now()
            Log.v("expirationDate", "date: ${date}")
            Log.v("expirationDate", "today: ${today}")

            if (today == date) {
                val snackbar =
                    Snackbar.make(binding.root, "اشتراک شما منقضی شده است!", Snackbar.LENGTH_LONG)
                        .setAction("خرید اشتراک") {
                            showIncreaseCreditDialog()
                        }.setBackgroundTint(Color.parseColor("#FFFFFF"))
                        .setTextColor(Color.parseColor("#000000"))
                        .setActionTextColor(Color.parseColor("#E600ADB5"))

                val view = snackbar.view
                val params = view.layoutParams as FrameLayout.LayoutParams
                params.gravity = Gravity.TOP
                view.layoutParams = params
                snackbar.view.layoutDirection = View.LAYOUT_DIRECTION_RTL
                snackbar.show()

            }


            if (today != date) {
                binding.cardLock.visibility = GONE
                binding.btnInvestment.setOnClickListener {
                    val intent =
                        Intent(requireContext(), ShareholdersInvestmentActivity::class.java)
                    startActivity(intent)
                    activity?.overridePendingTransition(
                        R.anim.slide_from_right,
                        R.anim.slide_to_left
                    )
                }

                val sumCompanyReceipt = companyReceiptDao.getReceiptSum()
                binding.txtReceipt.text = formatCurrency(sumCompanyReceipt.toLong())
                binding.btnIncome.setOnClickListener {
                    val intent = Intent(requireContext(), CompanyReceiptActivity::class.java)
                    startActivity(intent)
                    activity?.overridePendingTransition(
                        R.anim.slide_from_right,
                        R.anim.slide_to_left
                    )
                }

                val sumCompanyExpenses = companyExpensesDao.getExpensesSum()
                val sumEmployeeHarvest = employeeHarvestDao.getHarvestSum()
                val sumTotalExpenses = sumCompanyExpenses + sumEmployeeHarvest
                binding.txtPayment.text = formatCurrency(sumTotalExpenses.toLong())
                binding.btnExpense.setOnClickListener {
                    val intent = Intent(requireContext(), CompanyPaymentActivity::class.java)
                    startActivity(intent)
                    activity?.overridePendingTransition(
                        R.anim.slide_from_right,
                        R.anim.slide_to_left
                    )
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
                if (sumTotal < 0)
                    sumTotal = 0
                binding.btnInvestment.text = formatCurrency(sumTotal)
                binding.btnFinancialReport.setOnClickListener {
                    val intent =
                        Intent(requireContext(), CompanyFinancialReportActivity::class.java)
                    startActivity(intent)
                    activity?.overridePendingTransition(
                        R.anim.slide_from_right,
                        R.anim.slide_to_left
                    )
                }
            } else
                binding.cardLock.setOnClickListener {
                    showIncreaseCreditDialog()
                    Log.v("expirationDate", "Here: 2")

                }
        } else
            binding.cardLock.setOnClickListener {
                showIncreaseCreditDialog()
                Log.v("expirationDate", "Here: 1")

            }


        setProgressEfficiencyCompamy()

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
        progressNumProject(binding.progressNumProject, view)
    }

    @SuppressLint("SetTextI18n")
    private fun setProgressEfficiencyCompamy() {

        if (efficiencyProject() > 100) {
            binding.progressEfficiencyPro.setPercent(100)
            binding.txtEfficiencyPro.text = "100%+"

        } else if (efficiencyProject() in 1..100) {
            binding.progressEfficiencyPro.setPercent(efficiencyProject())
            binding.txtEfficiencyPro.text = "${efficiencyProject()}%"

        } else if (efficiencyProject() < 0) {
            binding.progressEfficiencyPro.setPercent(0)
            binding.txtEfficiencyPro.text = "0%-"
        }


        if (efficiencyEmployeeTack() > 100) {
            binding.progressEfficiencyEmpTask.setPercent(100)
            binding.txtEfficiencyEmpTask.text = "100%+"

        } else if (efficiencyEmployeeTack() in 1..100) {
            binding.progressEfficiencyEmpTask.setPercent(efficiencyProject())
            binding.txtEfficiencyEmpTask.text = "${efficiencyProject()}%"

        } else if (efficiencyEmployeeTack() < 0) {
            binding.progressEfficiencyEmpTask.setPercent(0)
            binding.txtEfficiencyEmpTask.text = "0%-"
        }

        if (efficiencyEmployeePresence() > 100) {
            binding.progressEfficiencyEmpPresence.setPercent(100)
            binding.txtEfficiencyEmpPresence.text = "100%+"

        } else if (efficiencyEmployeePresence() in 1..100) {
            binding.progressEfficiencyEmpPresence.setPercent(efficiencyProject())
            binding.txtEfficiencyEmpPresence.text = "${efficiencyProject()}%"

        } else if (efficiencyEmployeePresence() < 0) {
            binding.progressEfficiencyEmpPresence.setPercent(0)
            binding.txtEfficiencyEmpPresence.text = "0%-"
        }

    }

    override fun onResume() {
        super.onResume()
        sharedPreferences = requireActivity().getSharedPreferences(
            SHAREDEXPIRATIONSUBSCRIPTION,
            Context.MODE_PRIVATE
        )

        expirationDate = sharedPreferences.getString(CHEKEXPIRATION, "").toString()
        Log.v("expirationDate", "expiration: ${expirationDate}")

        if (expirationDate != "") {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-d")
            date = LocalDate.parse(expirationDate, formatter)
            val today = LocalDate.now()
            Log.v("expirationDate", "date: ${date}")
            Log.v("expirationDate", "today: ${today}")

            if (today != date) {
                binding.cardLock.visibility = GONE
                binding.btnInvestment.setOnClickListener {
                    val intent =
                        Intent(requireContext(), ShareholdersInvestmentActivity::class.java)
                    startActivity(intent)
                    activity?.overridePendingTransition(
                        R.anim.slide_from_right,
                        R.anim.slide_to_left
                    )
                }

                val sumCompanyReceipt = companyReceiptDao.getReceiptSum()
                binding.txtReceipt.text = formatCurrency(sumCompanyReceipt.toLong())
                binding.btnIncome.setOnClickListener {
                    val intent = Intent(requireContext(), CompanyReceiptActivity::class.java)
                    startActivity(intent)
                    activity?.overridePendingTransition(
                        R.anim.slide_from_right,
                        R.anim.slide_to_left
                    )
                }

                val sumCompanyExpenses = companyExpensesDao.getExpensesSum()
                val sumEmployeeHarvest = employeeHarvestDao.getHarvestSum()
                val sumTotalExpenses = sumCompanyExpenses + sumEmployeeHarvest
                binding.txtPayment.text = formatCurrency(sumTotalExpenses.toLong())
                binding.btnExpense.setOnClickListener {
                    val intent = Intent(requireContext(), CompanyPaymentActivity::class.java)
                    startActivity(intent)
                    activity?.overridePendingTransition(
                        R.anim.slide_from_right,
                        R.anim.slide_to_left
                    )
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
                if (sumTotal < 0)
                    sumTotal = 0
                binding.btnInvestment.text = formatCurrency(sumTotal)
                binding.btnFinancialReport.setOnClickListener {
                    val intent =
                        Intent(requireContext(), CompanyFinancialReportActivity::class.java)
                    startActivity(intent)
                    activity?.overridePendingTransition(
                        R.anim.slide_from_right,
                        R.anim.slide_to_left
                    )
                }
            } else
                binding.cardLock.setOnClickListener {
                    showIncreaseCreditDialog()
                    Log.v("expirationDate", "Here: 2")

                }
        }
    }

    private fun formatCurrency(value: Long?): String {
        val decimalFormat = DecimalFormat("#,###")
        return decimalFormat.format(value) + " تومان"
    }

    private fun progressNumProject(graph: Graph, view: View) {

        val companySkillDao = AppDatabase.getDataBase(view.context).companySkillDao
        val companySkillData = companySkillDao.getAllListCompanySkillDao()
        Log.v("companySkillData", companySkillData.toString())
        if (companySkillData.isEmpty())
            binding.emptyList.visibility = VISIBLE

        val companySkillInCompanyFragmentAdapter =
            CompanySkillInCompanyFragmentAdapter(ArrayList(companySkillData))
        binding.rcvSkill.layoutManager = LinearLayoutManager(context)
        binding.rcvSkill.adapter = companySkillInCompanyFragmentAdapter

        val numTotalProject = projectDao.getAllProject().size
        binding.txtTotalProject.text = numTotalProject.toString()
        val data: MutableCollection<GraphData> = ArrayList()

        for (skill in companySkillData) {
            Log.v("skill", skill.nameCompanySkill)
            val numProject = projectDao.getNumberProject(skill.nameCompanySkill).size
            data.add(GraphData(numProject.toFloat(), Color.parseColor(skill.colorSkill)))
            Log.v("skill", skill.colorSkill)
        }

        /*val numAndroid = projectDao.getNumberProject("اندروید").size
        val numSite = projectDao.getNumberProject("سایت").size
        val numFrontEnd = projectDao.getNumberProject("فرانت اند").size
        val numBackEnd = projectDao.getNumberProject("بک اند").size
        val numRobotic = projectDao.getNumberProject("رباتیک").size
        val numDsign = projectDao.getNumberProject("طراحی").size
        val numSeo = projectDao.getNumberProject("سئو").size
        val numTotalProject = projectDao.getAllProject().size*/

        graph.setMinValue(0f)
        graph.setMaxValue(numTotalProject.toFloat())
        graph.setBackgroundShapeWidthInDp(20)
        graph.setForegroundShapeWidthInPx(50)
        graph.setShapeForegroundColor(Color.parseColor("#202020"))
        graph.setShapeBackgroundColor(Color.parseColor("#202020"))
        graph.setData(data)

        /*binding.txtTotalProject.text = numTotalProject.toString()
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

        graph.setData(data)*/
    }

    private fun efficiencyProject(): Int {
        val numberProject = projectDao.getAllProject().size
        val sumProgressProject = projectDao.getColumnprogressProject()
        Log.v("loginapp", "numberProject: ${numberProject}")
        Log.v("loginapp", "sumProgressProject: ${sumProgressProject}")

        var sumAllProgressProject = sumProgressProject.sum()
        Log.v("loginapp", "sumAllProgressProject: ${sumAllProgressProject}")

        if (sumAllProgressProject != 0)
            sumAllProgressProject /= numberProject

        return sumAllProgressProject
    }

    private fun efficiencyEmployeeTack(): Int {

        val numberEmployee = efficiencyDao.getAllEfficiency().size
        val sumEfficiencyTotalDuties = efficiencyDao.getColumnEfficiencyTotalDuties()

        var sumEefficiencyEmployeeTack = sumEfficiencyTotalDuties.sum()

        if (sumEefficiencyEmployeeTack != 0)
            sumEefficiencyEmployeeTack /= numberEmployee

        return sumEefficiencyEmployeeTack
    }

    private fun efficiencyEmployeePresence(): Int {
        val numberEmployee = efficiencyDao.getAllEfficiency().size
        val sumEfficiencyTotalPresence = efficiencyDao.getColumnEfficiencyTotalPresence()

        var sumEefficiencyEmployeePresence = sumEfficiencyTotalPresence.sum()

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

    override fun onMenuItemClick(companySkill: CompanySkill, position: Int) {}

}