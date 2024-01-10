package com.vearad.vearatick.Dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.vearad.vearatick.BottomSheetCallback
import com.vearad.vearatick.DataBase.AppDatabase
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
        binding.tilVolumeSkill.editText?.addTextChangedListener {
            if (it.toString() != "")
                if (it.toString().toInt() > 100) {
                    binding.tilVolumeSkill.error = "بالاتر از صد نداریم ها !!!."
                } else
                    binding.tilVolumeSkill.error = null

        }
        binding.sheetBtnDone.setOnClickListener {
            addNewSkill(view)
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

    private fun addNewSkill(view: View) {
        if (
            binding.edtNameSkill.length() > 0 &&
            binding.edtVolumeSkill.length() > 0 &&
            binding.edtVolumeSkill.text.toString().toInt() <= 100
        ) {
            val txtName = binding.edtNameSkill.text.toString()
            val txtVolune = binding.edtVolumeSkill.text.toString()
            val companySkillDao = AppDatabase.getDataBase(view.context).companySkillDao
            val existenceSkill = companySkillDao.checkSkillExists(txtName)
            var noChange = false

            if (txtName == onClickCompanySkill!!.nameCompanySkill)
                noChange = true

            if (!existenceSkill || noChange) {

            val newSkill = CompanySkill(
                idCompanySkill = onClickCompanySkill.idCompanySkill,
                nameCompanySkill = txtName,
                volumeSkill = txtVolune.toInt(),
                colorSkill = onClickCompanySkill.colorSkill
            )
            companySkillDao.update(newSkill)
            onCompanyNewSkill()
            dismiss()
            }else
                Toast.makeText(context, "این مهارت قبلا ایجاد شده است.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "لطفا همه مقادیر را وارد کنید", Toast.LENGTH_SHORT).show()
        }
    }

}