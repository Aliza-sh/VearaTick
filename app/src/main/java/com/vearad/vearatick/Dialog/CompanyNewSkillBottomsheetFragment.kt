package com.vearad.vearatick.Dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.vearad.vearatick.BottomSheetCallback
import com.vearad.vearatick.DataBase.CompanySkill
import com.vearad.vearatick.DataBase.CompanySkillDao
import com.vearad.vearatick.adapter.CompanySkillAdapter
import com.vearad.vearatick.databinding.BottomsheetfragmentCompanyNewSkillBinding

class CompanyNewSkillBottomsheetFragment(
    val companySkillDao: CompanySkillDao,
    val companySkillAdapter: CompanySkillAdapter,

    ) : BottomSheetDialogFragment() {

    lateinit var binding: BottomsheetfragmentCompanyNewSkillBinding
    private var callback: BottomSheetCallback? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            BottomsheetfragmentCompanyNewSkillBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.sheetBtnDone.setOnClickListener {
            addNewSkill()
        }
    }
    private fun addNewSkill() {
        if (
            binding.edtNameSkill.length() > 0 &&
            binding.edtVolumeSkill.length() > 0
        ) {
            val txtName = binding.edtNameSkill.text.toString()
            val txtVolune = binding.edtVolumeSkill.text.toString()

            val newSkill = CompanySkill(
                nameCompanySkill = txtName,
                volumeSkill = txtVolune.toInt()
            )
            companySkillDao.insert(newSkill)
            onCompanyNewCompanySkill()
            dismiss()
        } else {
            Toast.makeText(context, "لطفا همه مقادیر را وارد کنید", Toast.LENGTH_SHORT).show()
        }
    }
    fun setCallback(callback: BottomSheetCallback) {
        this.callback = callback
    }
    fun onCompanyNewCompanySkill() {
        callback?.onConfirmButtonClicked()
    }

}