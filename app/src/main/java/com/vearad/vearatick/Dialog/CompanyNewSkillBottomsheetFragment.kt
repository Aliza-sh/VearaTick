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
import com.vearad.vearatick.adapter.CompanySkillAdapter
import com.vearad.vearatick.databinding.BottomsheetfragmentCompanyNewSkillBinding
import java.util.Random

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
        binding.tilVolumeSkill.editText?.addTextChangedListener {
            if (it.toString() != "")
                if (it.toString().toInt() > 100) {
                    binding.tilVolumeSkill.error = "بالاتر از صد نداریم ها !!!"
                } else
                    binding.tilVolumeSkill.error = null

        }
        binding.sheetBtnDone.setOnClickListener {
            addNewSkill(view)
        }
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
            if (!existenceSkill) {
                val random = Random()
                val randomColorCode = generateRandomColorCode(random)

                val newSkill = CompanySkill(
                    nameCompanySkill = txtName,
                    volumeSkill = txtVolune.toInt(),
                    colorSkill = "#$randomColorCode"
                )
                companySkillDao.insert(newSkill)
                onCompanyNewCompanySkill()
                dismiss()
            }else
                Toast.makeText(context, "این مهارت قبلا ایجاد شده است.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "لطفا همه مقادیر را وارد کنید", Toast.LENGTH_SHORT).show()
        }
    }
    private fun generateRandomColorCode(random: Random): String {
        val minBrightness = 128
        var red = random.nextInt(128) + minBrightness
        var green = random.nextInt(128) + minBrightness
        var blue = random.nextInt(128) + minBrightness
        val alpha = random.nextInt(256)

        if (red > 255) red = 255
        if (green > 255) green = 255
        if (blue > 255) blue = 255

        return String.format("%02X%02X%02X%02X", alpha, red, green, blue)
    }
    fun setCallback(callback: BottomSheetCallback) {
        this.callback = callback
    }
    fun onCompanyNewCompanySkill() {
        callback?.onConfirmButtonClicked()
    }

}