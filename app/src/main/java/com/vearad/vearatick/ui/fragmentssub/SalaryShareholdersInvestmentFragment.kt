package com.vearad.vearatick.ui.fragmentssub

import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.LinearInterpolator
import android.widget.PopupMenu
import androidx.activity.OnBackPressedCallback
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vearad.vearatick.utils.BottomSheetCallback
import com.vearad.vearatick.model.db.Employee
import com.vearad.vearatick.model.db.EmployeeInvestment
import com.vearad.vearatick.model.db.EmployeeInvestmentDao
import com.vearad.vearatick.dialog.SalaryShareholdersNewInvestmentBottomsheetFragment
import com.vearad.vearatick.dialog.SalaryShareholdersUpdateInvestmentBottomsheetFragment
import com.vearad.vearatick.R
import com.vearad.vearatick.ui.activitysub.ShareholdersInvestmentActivity
import com.vearad.vearatick.adapter.SalaryShareholdersInvestmentAdapter
import com.vearad.vearatick.databinding.FragmentDialogDeleteCompanyReceiptBinding
import com.vearad.vearatick.databinding.FragmentSalaryShareholdersInvestmentBinding

class SalaryShareholdersInvestmentFragment(
    val employee: Employee,
    val employeeInvestmentDao: EmployeeInvestmentDao
) : Fragment(), BottomSheetCallback, SalaryShareholdersInvestmentAdapter.SalaryShareholdersInvestmentEvents {

    lateinit var binding: FragmentSalaryShareholdersInvestmentBinding
    lateinit var bindingDialogDeleteCompanyReceipt: FragmentDialogDeleteCompanyReceiptBinding
    lateinit var shareholdersInvestmentAdapter: SalaryShareholdersInvestmentAdapter
    private var isScrollingUp = false
    private var buttonAnimator: ObjectAnimator? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSalaryShareholdersInvestmentBinding.inflate(layoutInflater, container, false)
        bindingDialogDeleteCompanyReceipt = FragmentDialogDeleteCompanyReceiptBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBackPressed()
        btnOnScroll()
        binding.btnBck.setOnClickListener {
            val intent = Intent(requireContext(), ShareholdersInvestmentActivity::class.java)
            startActivity(intent)
            activity?.overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
        }
        val employeeInvestmentData = employeeInvestmentDao.getEmployeeInvestment(employee.idEmployee!!)
        shareholdersInvestmentAdapter = SalaryShareholdersInvestmentAdapter(ArrayList(employeeInvestmentData), this)
        binding.recyclerView.adapter = shareholdersInvestmentAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        binding.btnFab.setOnClickListener {
            val bottomsheet = SalaryShareholdersNewInvestmentBottomsheetFragment(
                employeeInvestmentDao,
                shareholdersInvestmentAdapter,
                employee
            )
            bottomsheet.setStyle(R.style.BottomSheetStyle, R.style.BottomSheetDialogTheme)
            bottomsheet.setCallback(this)
            bottomsheet.show(parentFragmentManager, null)
        }
    }
    override fun onConfirmButtonClicked() {
        val employeeInvestmentData = employeeInvestmentDao.getEmployeeInvestment(employee.idEmployee!!)
        shareholdersInvestmentAdapter = SalaryShareholdersInvestmentAdapter(ArrayList(employeeInvestmentData), this)
        binding.recyclerView.adapter = shareholdersInvestmentAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
    }
    fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val intent = Intent(requireContext(), ShareholdersInvestmentActivity::class.java)
                    startActivity(intent)
                    activity?.overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
                }
            })
    }
    private fun btnOnScroll() {
        val layoutManager = LinearLayoutManager(context)
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

    override fun onShareholdersInvestmentClicked(
        employeeInvestment: EmployeeInvestment,
        position: Int
    ) {}

    override fun onLongClickedemployeeInvestment(
        employeeInvestment: EmployeeInvestment,
        position: Int
    ) {}

    override fun onMenuItemClick(employeeInvestment: EmployeeInvestment, position: Int) {

        val onClickEmployeeInvestment = employeeInvestmentDao.getOnClickEmployeeInvestment(employeeInvestment.idInvestment!!)

        val viewHolder =
            binding.recyclerView.findViewHolderForAdapterPosition(position) as SalaryShareholdersInvestmentAdapter.SalaryShareholdersInvestmentViewHolder
        viewHolder.let { holder ->
            val btnMenuCompanyExpenses = holder.btnMenu
            val popupMenu = PopupMenu(binding.root.context, btnMenuCompanyExpenses)
            popupMenu.inflate(R.menu.menu_employee)
            popupMenu.show()

            popupMenu.setOnMenuItemClickListener { item ->

                when (item.itemId) {

                    R.id.menu_employee_edit -> {
                        val bottomsheet = SalaryShareholdersUpdateInvestmentBottomsheetFragment(
                            employee,
                            employeeInvestmentDao,
                            onClickEmployeeInvestment,
                            position
                        )
                        bottomsheet.setStyle(
                            R.style.BottomSheetStyle,
                            R.style.BottomSheetDialogTheme
                        )
                        bottomsheet.setCallback(this)
                        bottomsheet.show(parentFragmentManager, null)
                        true
                    }

                    R.id.menu_employee_delete -> {
                        showDeleteDialog(onClickEmployeeInvestment, position)
                        true
                    }

                    else -> false
                }
            }
        }
    }

    private fun showDeleteDialog(onClickEmployeeInvestment: EmployeeInvestment?, position: Int) {
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

            deleteItem(onClickEmployeeInvestment, position)
            setData()
            alertDialog.dismiss()
        }
    }

    private fun setData() {
        val employeeInvestmentData = employeeInvestmentDao.getEmployeeInvestment(employee.idEmployee!!)
        shareholdersInvestmentAdapter = SalaryShareholdersInvestmentAdapter(ArrayList(employeeInvestmentData), this)
        binding.recyclerView.adapter = shareholdersInvestmentAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
    }

    fun deleteItem(onClickEmployeeInvestment: EmployeeInvestment?, position: Int) {

        val newEmployeeInvestment = EmployeeInvestment(
            idInvestment = onClickEmployeeInvestment!!.idInvestment,
            idEmployee = onClickEmployeeInvestment.idEmployee,
            investment =  onClickEmployeeInvestment.investment,
            investmentDate = onClickEmployeeInvestment.investmentDate,
            investmentDescription = onClickEmployeeInvestment.investmentDescription
        )
        employeeInvestmentDao.delete(newEmployeeInvestment)
    }
}