package ir.aliza.sherkatmanage

import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import android.view.animation.LinearInterpolator
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ir.aliza.sherkatmanage.DataBase.AppDatabase
import ir.aliza.sherkatmanage.DataBase.CompanyReceipt
import ir.aliza.sherkatmanage.DataBase.CompanyReceiptDao
import ir.aliza.sherkatmanage.Dialog.CompanyNewReceiptBottomsheetFragment
import ir.aliza.sherkatmanage.Dialog.CompanyUpdateReceiptBottomsheetFragment
import ir.aliza.sherkatmanage.adapter.CompanyReceiptAdapter
import ir.aliza.sherkatmanage.databinding.ActivityCompanyReceiptBinding
import ir.aliza.sherkatmanage.databinding.FragmentDialogDeleteCompanyReceiptBinding

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
        bindingDialogDeleteCompanyReceipt =
            FragmentDialogDeleteCompanyReceiptBinding.inflate(layoutInflater)
        setContentView(binding.root)
        btnOnScroll()
        binding.btnBck.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        companyReceiptDao = AppDatabase.getDataBase(applicationContext).companyReceiptDao
        val companyReceiptData = companyReceiptDao.getAllCompanyReceipt()
        companyReceiptAdapter = CompanyReceiptAdapter(ArrayList(companyReceiptData), this)
        binding.recyclerView.adapter = companyReceiptAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(applicationContext)

        binding.btnFab.setOnClickListener {
            val bottomsheet = CompanyNewReceiptBottomsheetFragment(
                companyReceiptDao,
                companyReceiptAdapter,
            )
            bottomsheet.setStyle(R.style.BottomSheetStyle, R.style.BottomSheetDialogTheme)
            bottomsheet.setCallback(this)
            bottomsheet.show(supportFragmentManager, null)
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
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
                        val bottomsheet = CompanyUpdateReceiptBottomsheetFragment(
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
            companyReceiptDescription = onClickCompanyReceipt.companyReceiptDescription
        )
        companyReceiptDao.delete(newCompanyReceipt)
    }

    override fun onCompanyReceiptClicked(companyReceipt: CompanyReceipt, position: Int) {}

    override fun onCompanyReceiptLongClicked(companyReceipt: CompanyReceipt, position: Int) {}

}