package com.vearad.vearatick.fgmSub

import BottomMarginItemDecoration
import TopMarginItemDecoration
import android.app.AlertDialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.PopupMenu
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.vearad.vearatick.BottomSheetCallback
import com.vearad.vearatick.DataBase.AppDatabase
import com.vearad.vearatick.DataBase.CompanySkill
import com.vearad.vearatick.DataBase.CompanySkillDao
import com.vearad.vearatick.dialog.CompanyNewSkillBottomsheetFragment
import com.vearad.vearatick.dialog.CompanyUpdateSkillBottomsheetFragment
import com.vearad.vearatick.R
import com.vearad.vearatick.adapter.CompanySkillAdapter
import com.vearad.vearatick.databinding.FragmentCompanySkillBinding
import com.vearad.vearatick.databinding.FragmentDialogDeleteSubtaskProjectBinding

class CompanySkillFragment : Fragment(), CompanySkillAdapter.CompanySkillEvent,
    BottomSheetCallback {

    lateinit var binding: FragmentCompanySkillBinding
    lateinit var bindingDialogDeleteSubtaskProject: FragmentDialogDeleteSubtaskProjectBinding
    lateinit var companySkillAdapter: CompanySkillAdapter
    lateinit var companySkillDao: CompanySkillDao
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCompanySkillBinding.inflate(layoutInflater, container, false)
        bindingDialogDeleteSubtaskProject =
            FragmentDialogDeleteSubtaskProjectBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        companySkillDao = AppDatabase.getDataBase(view.context).companySkillDao
        val companySkillData = companySkillDao.getAllListCompanySkillDao()

        companySkillAdapter =
            CompanySkillAdapter(
                ArrayList(companySkillData),
                this,
            )
        binding.rcvSkill.layoutManager = LinearLayoutManager(context)
        binding.rcvSkill.adapter = companySkillAdapter
        topMargin()
        bottomMargin()

        binding.btnAddCompanySkill.setOnClickListener {
            val numSkill = companySkillDao.getAllListCompanySkillDao().size
            if (numSkill < 50) {
                val bottomsheet = CompanyNewSkillBottomsheetFragment(
                    companySkillDao,
                    companySkillAdapter
                )
                bottomsheet.setStyle(
                    R.style.BottomSheetStyle,
                    R.style.BottomSheetDialogTheme
                )
                bottomsheet.setCallback(this)
                bottomsheet.show(parentFragmentManager, null)
            } else
                Toast.makeText(context, "سقف مچاز ایجاد مهارت ها 50 می باشد", Toast.LENGTH_SHORT)
                    .show()
        }

    }

    override fun onMenuItemClick(companySkill: CompanySkill, position: Int) {

        val onClickSkill = companySkillDao.getOnClickEmployeeHarvest(companySkill.idCompanySkill!!)
        val viewHolder =
            binding.rcvSkill.findViewHolderForAdapterPosition(position) as CompanySkillAdapter.CompanySkillViewHolder
        viewHolder.let { holder ->
            val btnMenuSubTaskProject = holder.btnMenu
            val popupMenu = PopupMenu(context, btnMenuSubTaskProject)
            popupMenu.inflate(R.menu.menu_employee)
            popupMenu.show()

            popupMenu.setOnMenuItemClickListener { item ->

                when (item.itemId) {

                    R.id.menu_employee_edit -> {
                        val bottomsheet = CompanyUpdateSkillBottomsheetFragment(
                            companySkillDao,
                            onClickSkill
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
                        showDeleteDialog(onClickSkill, position)
                        true
                    }

                    else -> false
                }
            }
        }

    }

    private fun showDeleteDialog(onClickSkill: CompanySkill?, position: Int) {

        val parent = bindingDialogDeleteSubtaskProject.root.parent as? ViewGroup
        parent?.removeView(bindingDialogDeleteSubtaskProject.root)
        val dialogBuilder = AlertDialog.Builder(bindingDialogDeleteSubtaskProject.root.context)
        dialogBuilder.setView(bindingDialogDeleteSubtaskProject.root)

        val alertDialog = dialogBuilder.create()
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.Transparent.toArgb()))
        alertDialog.setCancelable(false)
        alertDialog.show()
        bindingDialogDeleteSubtaskProject.dialogBtnDeleteCansel.setOnClickListener {
            alertDialog.dismiss()
        }
        bindingDialogDeleteSubtaskProject.dialogBtnDeleteSure.setOnClickListener {
            deleteItem(onClickSkill, position)
            alertDialog.dismiss()
        }
    }

    fun deleteItem(onClickSkill: CompanySkill?, position: Int) {
        val projectDao = AppDatabase.getDataBase(binding.root.context).projectDao
        projectDao.updateProjectsByType(onClickSkill!!.nameCompanySkill)
        companySkillDao.delete(onClickSkill)
        onCompanyDeleteSkill()
    }

    fun onCompanyDeleteSkill() {
        companySkillDao = AppDatabase.getDataBase(binding.root.context).companySkillDao
        val companySkillData = companySkillDao.getAllListCompanySkillDao()
        val companySkillAdapter =
            CompanySkillAdapter(
                ArrayList(companySkillData),
                this,
            )
        binding.rcvSkill.layoutManager = LinearLayoutManager(context)
        binding.rcvSkill.adapter = companySkillAdapter
    }

    override fun onConfirmButtonClicked() {
        companySkillDao = AppDatabase.getDataBase(binding.root.context).companySkillDao
        val companySkillData = companySkillDao.getAllListCompanySkillDao()
        val companySkillAdapter =
            CompanySkillAdapter(
                ArrayList(companySkillData),
                this,
            )
        binding.rcvSkill.layoutManager = LinearLayoutManager(context)
        binding.rcvSkill.adapter = companySkillAdapter

    }

    private fun topMargin() {
        val topMargin = 270 // اندازه مارجین بالا را از منابع دریافت کن
        val itemDecoratio = TopMarginItemDecoration(topMargin)
        binding.rcvSkill.addItemDecoration(itemDecoratio)
    }

    private fun bottomMargin() {
        val bottomMargin = 220 // اندازه مارجین پایین را از منابع دریافت کنید
        val itemDecoration = BottomMarginItemDecoration(bottomMargin)
        binding.rcvSkill.addItemDecoration(itemDecoration)
    }
}