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
import com.vearad.vearatick.databinding.BottomsheetfragmentCompanyNewSkillBinding


class CompanyUpdateSkillBottomsheetFragment(
    val companySkillDao: CompanySkillDao,
    val onClickCompanySkill: CompanySkill?,
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
        setdata()

        binding.sheetBtnDone.setOnClickListener {
            addNewSkill()
        }
    }

    private fun setdata() {
        binding.edtNameSkill.setText(onClickCompanySkill!!.nameCompanySkill)
        binding.edtVolumeSkill.setText(onClickCompanySkill.volumeSkill.toString())
       binding.title.text = "مهارت را ویرایش کنید."
    }

    fun setCallback(callback: BottomSheetCallback) {
        this.callback = callback
    }

    fun onCompanyNewSkill() {
        callback?.onConfirmButtonClicked()
    }

    private fun addNewSkill() {
        if (
            binding.edtNameSkill.length() > 0 &&
            binding.edtVolumeSkill.length() > 0
        ) {
            val txtName = binding.edtNameSkill.text.toString()
            val txtVolune = binding.edtVolumeSkill.text.toString()


            val newSkill = CompanySkill(
                idCompanySkill = onClickCompanySkill!!.idCompanySkill,
                nameCompanySkill = txtName,
                volumeSkill = txtVolune.toInt()
            )
            companySkillDao.update(newSkill)
            onCompanyNewSkill()
            dismiss()
        } else {
            Toast.makeText(context, "لطفا همه مقادیر را وارد کنید", Toast.LENGTH_SHORT).show()
        }
    }

}