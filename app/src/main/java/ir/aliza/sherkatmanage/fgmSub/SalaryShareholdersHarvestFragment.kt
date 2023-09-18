package ir.aliza.sherkatmanage.fgmSub

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
import ir.aliza.sherkatmanage.BottomSheetCallback
import ir.aliza.sherkatmanage.CompanyPaymentActivity
import ir.aliza.sherkatmanage.DataBase.Employee
import ir.aliza.sherkatmanage.DataBase.EmployeeHarvest
import ir.aliza.sherkatmanage.DataBase.EmployeeHarvestDao
import ir.aliza.sherkatmanage.Dialog.SalaryShareholdersNewHarvestBottomsheetFragment
import ir.aliza.sherkatmanage.Dialog.SalaryShareholdersUpdateHarvestBottomsheetFragment
import ir.aliza.sherkatmanage.R
import ir.aliza.sherkatmanage.adapter.SalaryShareholdersHarvestAdapter
import ir.aliza.sherkatmanage.databinding.FragmentDialogDeleteCompanyReceiptBinding
import ir.aliza.sherkatmanage.databinding.FragmentSalaryShareholdersHarvestBinding

class SalaryShareholdersHarvestFragment(
    val employee: Employee,
    val employeeHarvestDao: EmployeeHarvestDao
) : Fragment(), BottomSheetCallback, SalaryShareholdersHarvestAdapter.SalaryShareholdersHarvestEvents {

    lateinit var binding: FragmentSalaryShareholdersHarvestBinding
    lateinit var bindingDialogDeleteCompanyReceipt: FragmentDialogDeleteCompanyReceiptBinding
    lateinit var shareholdersHarvestAdapter: SalaryShareholdersHarvestAdapter
    private var isScrollingUp = false
    private var buttonAnimator: ObjectAnimator? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSalaryShareholdersHarvestBinding.inflate(layoutInflater, container, false)
        bindingDialogDeleteCompanyReceipt =
            FragmentDialogDeleteCompanyReceiptBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBackPressed()
        btnOnScroll()
        binding.btnBck.setOnClickListener {
            val intent = Intent(requireContext(), CompanyPaymentActivity::class.java)
            startActivity(intent)
        }
        val employeeHarvestData = employeeHarvestDao.getEmployeeHarvest(employee.idEmployee!!)
        shareholdersHarvestAdapter =
            SalaryShareholdersHarvestAdapter(ArrayList(employeeHarvestData), this)
        binding.recyclerView.adapter = shareholdersHarvestAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        binding.btnFab.setOnClickListener {
            val bottomsheet = SalaryShareholdersNewHarvestBottomsheetFragment(
                employeeHarvestDao,
                shareholdersHarvestAdapter,
                employee
            )
            bottomsheet.setStyle(R.style.BottomSheetStyle, R.style.BottomSheetDialogTheme)
            bottomsheet.setCallback(this)
            bottomsheet.show(parentFragmentManager, null)
        }
    }

    override fun onConfirmButtonClicked() {
        val employeeHarvestData = employeeHarvestDao.getEmployeeHarvest(employee.idEmployee!!)
        shareholdersHarvestAdapter =
            SalaryShareholdersHarvestAdapter(ArrayList(employeeHarvestData), this)
        binding.recyclerView.adapter = shareholdersHarvestAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
    }

    fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val intent = Intent(requireContext(), CompanyPaymentActivity::class.java)
                    startActivity(intent)
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

    override fun onShareholdersHarvestClicked(employeeHarvest: EmployeeHarvest, position: Int) {}

    override fun onLongClickedShareholdersHarvest(
        employeeHarvest: EmployeeHarvest,
        position: Int
    ) {
    }

    override fun onMenuItemClick(employeeHarvest: EmployeeHarvest, position: Int) {

        val onClickEmployeeHarvest =
            employeeHarvestDao.getOnClickEmployeeHarvest(employeeHarvest.idHarvest!!)

        val viewHolder =
            binding.recyclerView.findViewHolderForAdapterPosition(position) as SalaryShareholdersHarvestAdapter.SalaryShareholdersHarvestViewHolder
        viewHolder.let { holder ->
            val btnMenuCompanyExpenses = holder.btnMenu
            val popupMenu = PopupMenu(binding.root.context, btnMenuCompanyExpenses)
            popupMenu.inflate(R.menu.menu_employee)
            popupMenu.show()

            popupMenu.setOnMenuItemClickListener { item ->

                when (item.itemId) {

                    R.id.menu_employee_edit -> {
                        val bottomsheet = SalaryShareholdersUpdateHarvestBottomsheetFragment(
                            employee,
                            employeeHarvestDao,
                            onClickEmployeeHarvest,
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
                        showDeleteDialog(onClickEmployeeHarvest, position)
                        true
                    }

                    else -> false
                }
            }
        }
    }

    private fun showDeleteDialog(onClickEmployeeHarvest: EmployeeHarvest?, position: Int) {
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

            deleteItem(onClickEmployeeHarvest, position)
            setData()
            alertDialog.dismiss()
        }
    }

    private fun setData() {
        val employeeHarvestData = employeeHarvestDao.getEmployeeHarvest(employee.idEmployee!!)
        shareholdersHarvestAdapter =
            SalaryShareholdersHarvestAdapter(ArrayList(employeeHarvestData), this)
        binding.recyclerView.adapter = shareholdersHarvestAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
    }

    fun deleteItem(onClickEmployeeHarvest: EmployeeHarvest?, position: Int) {

        val newEmployeeHarvest = EmployeeHarvest(
            idHarvest = onClickEmployeeHarvest!!.idHarvest,
            idEmployee = onClickEmployeeHarvest.idEmployee,
            harvest = onClickEmployeeHarvest.harvest,
            harvestDate = onClickEmployeeHarvest.harvestDate,
            harvestDescription = onClickEmployeeHarvest.harvestDescription
        )
        employeeHarvestDao.delete(newEmployeeHarvest)

    }
}