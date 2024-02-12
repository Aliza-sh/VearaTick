package com.vearad.vearatick.ui.activitysub

import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import android.view.animation.LinearInterpolator
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vearad.vearatick.R
import com.vearad.vearatick.adapter.CompanyReceiptAdapter
import com.vearad.vearatick.databinding.ActivityCompanyReceiptBinding
import com.vearad.vearatick.databinding.FragmentDialogDeleteCompanyReceiptBinding
import com.vearad.vearatick.model.db.AppDatabase
import com.vearad.vearatick.model.db.CompanyReceipt
import com.vearad.vearatick.model.db.CompanyReceiptDao
import com.vearad.vearatick.model.db.FinancialReport
import com.vearad.vearatick.ui.MainActivity
import com.vearad.vearatick.ui.dialog.CompanyNewIncomeBottomsheetFragment
import com.vearad.vearatick.ui.dialog.CompanyUpdateIncomeBottomsheetFragment
import com.vearad.vearatick.utils.BottomSheetCallback

class CompanyReceiptActivity : AppCompatActivity(), CompanyReceiptAdapter.CompanyReceiptEvents,
    BottomSheetCallback {

    lateinit var binding: ActivityCompanyReceiptBinding
    lateinit var bindingDialogDeleteCompanyReceipt: FragmentDialogDeleteCompanyReceiptBinding
    private var isScrollingUp = false
    private var buttonAnimator: ObjectAnimator? = null
    lateinit var companyReceiptDao: CompanyReceiptDao
    lateinit var companyReceiptAdapter: CompanyReceiptAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompanyReceiptBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bindingDialogDeleteCompanyReceipt =
            FragmentDialogDeleteCompanyReceiptBinding.inflate(layoutInflater)
        btnOnScroll()
        binding.btnBck.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
        }

        companyReceiptDao = AppDatabase.getDataBase(applicationContext).companyReceiptDao
        val companyReceiptData = companyReceiptDao.getAllCompanyReceipt()
        companyReceiptAdapter = CompanyReceiptAdapter(ArrayList(companyReceiptData), this)
        binding.recyclerView.adapter = companyReceiptAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(applicationContext)

        binding.btnFab.setOnClickListener {
            val bottomsheet = CompanyNewIncomeBottomsheetFragment(
                companyReceiptDao,
                companyReceiptAdapter,
            )
            bottomsheet.setStyle(R.style.BottomSheetStyle, R.style.BottomSheetDialogTheme)
            bottomsheet.setCallback(this)
            bottomsheet.show(supportFragmentManager, null)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
    }
    override fun onConfirmButtonClicked() {
        companyReceiptDao = AppDatabase.getDataBase(applicationContext).companyReceiptDao
        val companyReceiptData = companyReceiptDao.getAllCompanyReceipt()
        companyReceiptAdapter = CompanyReceiptAdapter(ArrayList(companyReceiptData), this)
        binding.recyclerView.adapter = companyReceiptAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(applicationContext)
    }

    private fun btnOnScroll() {
        val layoutManager = LinearLayoutManager(applicationContext)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (isScrollingUp) {
                        if ((recyclerView.adapter?.itemCount ?: 0) > 0) {
                            animateButtonUp()
                        }
                    } else {
                        if ((recyclerView.adapter?.itemCount ?: 0) > 0) {
                            animateButtonDown()
                        }
                    }
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                isScrollingUp = dy <= 0
            }
        })
    }

    private fun animateButtonDown() {
        if (binding.btnFab.translationY != binding.btnFab.height.toFloat()) {
            buttonAnimator?.cancel()
            buttonAnimator = ObjectAnimator.ofFloat(
                binding.btnFab,
                "translationY",
                binding.btnBck.height.toFloat()
            )
            buttonAnimator?.apply {
                duration = 200
                interpolator = LinearInterpolator()
                start()
            }
        }
    }

    private fun animateButtonUp() {
        if (binding.btnFab.translationY != 0f) {
            buttonAnimator?.cancel()
            buttonAnimator = ObjectAnimator.ofFloat(binding.btnFab, "translationY", 0f)
            buttonAnimator?.apply {
                duration = 200
                interpolator = LinearInterpolator()
                start()
            }
        }
    }

    override fun onMenuItemClick(companyReceipt: CompanyReceipt, position: Int) {

        if (companyReceipt.idProject == null) {

            val onClickCompanyReceipt =
                companyReceiptDao.getCompanyReceipt(companyReceipt.idCompanyReceipt!!)

            val viewHolder =
                binding.recyclerView.findViewHolderForAdapterPosition(position) as CompanyReceiptAdapter.CompanyReceiptViewHolder
            viewHolder.let { holder ->
                val btnMenuCompanyReceip = holder.btnMenu
                val popupMenu = PopupMenu(applicationContext, btnMenuCompanyReceip)
                popupMenu.inflate(R.menu.menu_employee)
                popupMenu.show()

            popupMenu.setOnMenuItemClickListener { item ->

                when (item.itemId) {

                    R.id.menu_employee_edit -> {
                        val bottomsheet = CompanyUpdateIncomeBottomsheetFragment(
                            companyReceiptDao,
                            companyReceiptAdapter,
                            onClickCompanyReceipt,
                            position
                        )
                        bottomsheet.setStyle(
                            R.style.BottomSheetStyle,
                            R.style.BottomSheetDialogTheme
                        )
                        bottomsheet.setCallback(this)
                        bottomsheet.show(supportFragmentManager, null)
                        true
                    }

                    R.id.menu_employee_delete -> {
                        showDeleteDialog(onClickCompanyReceipt, position)
                        true
                    }

                    else -> false
                }
            }
            }
        } else
            Toast.makeText(
                binding.root.context,
                "برای تغییرات لازم به پروژه مربوطه مراجعه کنید",
                Toast.LENGTH_SHORT
            ).show()

    }

    private fun showDeleteDialog(onClickCompanyReceipt: CompanyReceipt?, position: Int) {
        val parent = bindingDialogDeleteCompanyReceipt.root.parent as? ViewGroup
        parent?.removeView(bindingDialogDeleteCompanyReceipt.root)
        val dialogBuilder = AlertDialog.Builder(bindingDialogDeleteCompanyReceipt.root.context)
        dialogBuilder.setView(bindingDialogDeleteCompanyReceipt.root)

        val alertDialog = dialogBuilder.create()
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.Transparent.toArgb()))
        alertDialog.setCancelable(false)
        alertDialog.show()
        bindingDialogDeleteCompanyReceipt.dialogBtnDeleteCansel.setOnClickListener {
            alertDialog.dismiss()
        }
        bindingDialogDeleteCompanyReceipt.dialogBtnDeleteSure.setOnClickListener {

            deleteItem(onClickCompanyReceipt, position)
            setData()
            alertDialog.dismiss()
        }
    }

    private fun setData() {
        val companyReceiptData = companyReceiptDao.getAllCompanyReceipt()
        companyReceiptAdapter = CompanyReceiptAdapter(ArrayList(companyReceiptData), this)
        binding.recyclerView.adapter = companyReceiptAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(applicationContext)
    }

    fun deleteItem(onClickCompanyReceipt: CompanyReceipt?, position: Int) {

        val newCompanyReceipt = CompanyReceipt(
            idCompanyReceipt = onClickCompanyReceipt!!.idCompanyReceipt,
            companyReceipt = onClickCompanyReceipt.companyReceipt,
            companyReceiptDate = onClickCompanyReceipt.companyReceiptDate,
            companyReceiptDescription = onClickCompanyReceipt.companyReceiptDescription,
            monthCompanyReceipt = onClickCompanyReceipt.monthCompanyReceipt,
            yearCompanyReceipt = onClickCompanyReceipt.yearCompanyReceipt
        )

        val financialReportDao = AppDatabase.getDataBase(binding.root.context).financialReportDao
        val financialReportYearAndMonth =
            financialReportDao.getFinancialReportYearAndMonthDao(onClickCompanyReceipt.yearCompanyReceipt, onClickCompanyReceipt.monthCompanyReceipt )

        val agoExpense = financialReportYearAndMonth!!.income
        val newExpense = (agoExpense!!.toLong() - onClickCompanyReceipt.companyReceipt!!.toLong())
        val newCompanyFinancialReport = FinancialReport(
            idFinancialReport = financialReportYearAndMonth.idFinancialReport,
            year = financialReportYearAndMonth.year,
            month = financialReportYearAndMonth.month,
            expense = financialReportYearAndMonth.expense,
            income =  newExpense,
            profit = financialReportYearAndMonth.profit
        )
        financialReportDao.update(newCompanyFinancialReport)
        companyReceiptDao.delete(newCompanyReceipt)
    }

    override fun onCompanyReceiptClicked(companyReceipt: CompanyReceipt, position: Int) {}

    override fun onCompanyReceiptLongClicked(companyReceipt: CompanyReceipt, position: Int) {}

}