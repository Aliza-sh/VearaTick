package com.vearad.vearatick.fgmMain

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import com.vearad.vearatick.BottomSheetCallback
import com.vearad.vearatick.DataBase.AppDatabase
import com.vearad.vearatick.DataBase.CompanyInfo
import com.vearad.vearatick.DataBase.CompanyInfoDao
import com.vearad.vearatick.Dialog.CompanyInfoBottomsheetFragment
import com.vearad.vearatick.MainActivity
import com.vearad.vearatick.R
import com.vearad.vearatick.databinding.FragmentCompanyInformationBinding
import com.vearad.vearatick.fgmSub.CompanyEmployeeResumeFragment
import com.vearad.vearatick.fgmSub.CompanyResumeFragment
import com.vearad.vearatick.fgmSub.CompanySkillFragment

class CompanyInformationFragment : Fragment(), BottomSheetCallback {


    lateinit var binding: FragmentCompanyInformationBinding
    lateinit var companyInfoDao: CompanyInfoDao
    var companyInfo: CompanyInfo? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCompanyInformationBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firstRun(view)

        companyInfoDao = AppDatabase.getDataBase(view.context).companyInfoDao
        companyInfo = companyInfoDao.getCompanyInfoDao()

        if (companyInfo == null) {
            val bottomsheet = CompanyInfoBottomsheetFragment()
            bottomsheet.setStyle(
                R.style.BottomSheetStyle,
                R.style.BottomSheetDialogTheme
            )
            bottomsheet.setCallback(this)
            bottomsheet.show(parentFragmentManager, null)

        } else {
            binding.nameCompany.text = companyInfo!!.nameCompany
            binding.locationCompany.text = companyInfo!!.addressCompany
            binding.numberCompany.text = companyInfo!!.phoneCompany
            binding.idGithaub.text = companyInfo!!.githubCompany
            binding.idLinkedin.text = companyInfo!!.linkedinCompany
        }


        binding.btnCompanySkill.setOnClickListener {
            btnCompanySkill(view)
        }
        binding.btnCompanyResume.setOnClickListener {
            btnCompanyResume(view)
        }

        binding.btnMenu.setOnClickListener {
            val bottomsheet = CompanyInfoBottomsheetFragment()
            bottomsheet.setStyle(
                R.style.BottomSheetStyle,
                R.style.BottomSheetDialogTheme
            )
            bottomsheet.setCallback(this)
            bottomsheet.show(parentFragmentManager, null)
        }

//        binding.btnEmployeeResume.setOnClickListener {
//            btnEmployeeResume(view)
//        }


    }
    private fun replaceFragment(fragment: Fragment) {
        val transaction = (activity as MainActivity).supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_com_info, fragment)
            .commit()
    }

    private fun firstRun(view: View) {
        btnCompanySkill(view)
        replaceFragment(CompanySkillFragment())
    }

    private fun btnCompanySkill(view: View) {

        binding.icCompanySkill.backgroundTintList =
            ContextCompat.getColorStateList(view.context, R.color.firoze)
        binding.txtCompanySkill.setTextColor(Color.parseColor("#E600ADB5"))
        binding.txtCompanySkill.textSize = 11f
        var layoutParamsCompanySkill = binding.btnCompanySkill.layoutParams
        layoutParamsCompanySkill.height = resources.getDimensionPixelSize(R.dimen.size_70)
        binding.btnCompanySkill.layoutParams = layoutParamsCompanySkill
        ViewCompat.setElevation(
            binding.btnCompanySkill,
            resources.getDimension(R.dimen.elevation_10)
        )
        layoutParamsCompanySkill = binding.icCompanySkill.layoutParams
        layoutParamsCompanySkill.width = resources.getDimensionPixelSize(R.dimen.size_40)
        layoutParamsCompanySkill.height = resources.getDimensionPixelSize(R.dimen.size_40)
        binding.icCompanySkill.layoutParams = layoutParamsCompanySkill

        binding.icCompanyResume.backgroundTintList =
            ContextCompat.getColorStateList(view.context, R.color.blacke)
        binding.txtCompanyResume.setTextColor(Color.parseColor("#FFFFFF"))
        binding.txtCompanyResume.textSize = 9f
        var layoutParamsCompanyResume = binding.btnCompanyResume.layoutParams
        layoutParamsCompanyResume.height = resources.getDimensionPixelSize(R.dimen.size_60)
        binding.btnCompanyResume.layoutParams = layoutParamsCompanyResume
        ViewCompat.setElevation(
            binding.btnCompanyResume,
            resources.getDimension(R.dimen.elevation_5)
        )
        layoutParamsCompanyResume = binding.icCompanyResume.layoutParams
        layoutParamsCompanyResume.width = resources.getDimensionPixelSize(R.dimen.size_35)
        layoutParamsCompanyResume.height = resources.getDimensionPixelSize(R.dimen.size_35)
        binding.icCompanyResume.layoutParams = layoutParamsCompanyResume

        binding.icEmployeeResume.backgroundTintList =
            ContextCompat.getColorStateList(view.context, R.color.blacke)
        binding.txtEmployeeResume.setTextColor(Color.parseColor("#FFFFFF"))
        binding.txtEmployeeResume.textSize = 9f
        var layoutParamsEmployeeResume = binding.btnEmployeeResume.layoutParams
        layoutParamsEmployeeResume.height = resources.getDimensionPixelSize(R.dimen.size_60)
        binding.btnEmployeeResume.layoutParams = layoutParamsEmployeeResume
        ViewCompat.setElevation(
            binding.btnEmployeeResume,
            resources.getDimension(R.dimen.elevation_5)
        )
        layoutParamsEmployeeResume = binding.icEmployeeResume.layoutParams
        layoutParamsEmployeeResume.width = resources.getDimensionPixelSize(R.dimen.size_35)
        layoutParamsEmployeeResume.height = resources.getDimensionPixelSize(R.dimen.size_35)
        binding.icEmployeeResume.layoutParams = layoutParamsEmployeeResume

        replaceFragment(CompanySkillFragment())
    }

    private fun btnCompanyResume(view: View) {
        binding.icCompanyResume.backgroundTintList =
            ContextCompat.getColorStateList(view.context, R.color.firoze)
        binding.txtCompanyResume.setTextColor(Color.parseColor("#E600ADB5"))
        binding.txtCompanyResume.textSize = 11f
        var layoutParamsCompanyResume = binding.btnCompanyResume.layoutParams
        layoutParamsCompanyResume.height = resources.getDimensionPixelSize(R.dimen.size_70)
        binding.btnCompanyResume.layoutParams = layoutParamsCompanyResume
        ViewCompat.setElevation(
            binding.btnCompanyResume,
            resources.getDimension(R.dimen.elevation_10)
        )
        layoutParamsCompanyResume = binding.icCompanyResume.layoutParams
        layoutParamsCompanyResume.width = resources.getDimensionPixelSize(R.dimen.size_40)
        layoutParamsCompanyResume.height = resources.getDimensionPixelSize(R.dimen.size_40)
        binding.icCompanyResume.layoutParams = layoutParamsCompanyResume

        binding.icCompanySkill.backgroundTintList =
            ContextCompat.getColorStateList(view.context, R.color.blacke)
        binding.txtCompanySkill.setTextColor(Color.parseColor("#FFFFFF"))
        binding.txtCompanySkill.textSize = 9f
        var layoutParamsCompanySkill = binding.btnCompanySkill.layoutParams
        layoutParamsCompanySkill.height = resources.getDimensionPixelSize(R.dimen.size_60)
        binding.btnCompanySkill.layoutParams = layoutParamsCompanySkill
        ViewCompat.setElevation(
            binding.btnCompanySkill,
            resources.getDimension(R.dimen.elevation_5)
        )
        layoutParamsCompanySkill = binding.icCompanySkill.layoutParams
        layoutParamsCompanySkill.width = resources.getDimensionPixelSize(R.dimen.size_35)
        layoutParamsCompanySkill.height = resources.getDimensionPixelSize(R.dimen.size_35)
        binding.icCompanySkill.layoutParams = layoutParamsCompanySkill

        binding.icEmployeeResume.backgroundTintList =
            ContextCompat.getColorStateList(view.context, R.color.blacke)
        binding.txtEmployeeResume.setTextColor(Color.parseColor("#FFFFFF"))
        binding.txtEmployeeResume.textSize = 9f
        var layoutParamsEmployeeResume = binding.btnEmployeeResume.layoutParams
        layoutParamsEmployeeResume.height = resources.getDimensionPixelSize(R.dimen.size_60)
        binding.btnEmployeeResume.layoutParams = layoutParamsEmployeeResume
        ViewCompat.setElevation(
            binding.btnEmployeeResume,
            resources.getDimension(R.dimen.elevation_5)
        )
        layoutParamsEmployeeResume = binding.icEmployeeResume.layoutParams
        layoutParamsEmployeeResume.width = resources.getDimensionPixelSize(R.dimen.size_35)
        layoutParamsEmployeeResume.height = resources.getDimensionPixelSize(R.dimen.size_35)
        binding.icEmployeeResume.layoutParams = layoutParamsEmployeeResume

        replaceFragment(CompanyResumeFragment())
    }

    private fun btnEmployeeResume(view: View) {
        binding.icEmployeeResume.backgroundTintList =
            ContextCompat.getColorStateList(view.context, R.color.firoze)
        binding.txtEmployeeResume.setTextColor(Color.parseColor("#E600ADB5"))
        binding.txtEmployeeResume.textSize = 11f
        var layoutParamsEmployeeResume = binding.btnEmployeeResume.layoutParams
        layoutParamsEmployeeResume.height = resources.getDimensionPixelSize(R.dimen.size_70)
        binding.btnEmployeeResume.layoutParams = layoutParamsEmployeeResume
        ViewCompat.setElevation(
            binding.btnEmployeeResume,
            resources.getDimension(R.dimen.elevation_10)
        )
        layoutParamsEmployeeResume = binding.icEmployeeResume.layoutParams
        layoutParamsEmployeeResume.width = resources.getDimensionPixelSize(R.dimen.size_40)
        layoutParamsEmployeeResume.height = resources.getDimensionPixelSize(R.dimen.size_40)
        binding.icEmployeeResume.layoutParams = layoutParamsEmployeeResume

        binding.icCompanySkill.backgroundTintList =
            ContextCompat.getColorStateList(view.context, R.color.blacke)
        binding.txtCompanySkill.setTextColor(Color.parseColor("#FFFFFF"))
        binding.txtCompanySkill.textSize = 9f
        var layoutParamsCompanySkill = binding.btnCompanySkill.layoutParams
        layoutParamsCompanySkill.height = resources.getDimensionPixelSize(R.dimen.size_60)
        binding.btnCompanySkill.layoutParams = layoutParamsCompanySkill
        ViewCompat.setElevation(
            binding.btnCompanySkill,
            resources.getDimension(R.dimen.elevation_5)
        )
        layoutParamsCompanySkill = binding.icCompanySkill.layoutParams
        layoutParamsCompanySkill.width = resources.getDimensionPixelSize(R.dimen.size_35)
        layoutParamsCompanySkill.height = resources.getDimensionPixelSize(R.dimen.size_35)
        binding.icCompanySkill.layoutParams = layoutParamsCompanySkill

        binding.icCompanyResume.backgroundTintList =
            ContextCompat.getColorStateList(view.context, R.color.blacke)
        binding.txtCompanyResume.setTextColor(Color.parseColor("#FFFFFF"))
        binding.txtCompanyResume.textSize = 9f
        var layoutParamsCompanyResume = binding.btnCompanyResume.layoutParams
        layoutParamsCompanyResume.height = resources.getDimensionPixelSize(R.dimen.size_60)
        binding.btnCompanyResume.layoutParams = layoutParamsCompanyResume
        ViewCompat.setElevation(
            binding.btnCompanyResume,
            resources.getDimension(R.dimen.elevation_5)
        )
        layoutParamsCompanyResume = binding.icCompanyResume.layoutParams
        layoutParamsCompanyResume.width = resources.getDimensionPixelSize(R.dimen.size_35)
        layoutParamsCompanyResume.height = resources.getDimensionPixelSize(R.dimen.size_35)
        binding.icCompanyResume.layoutParams = layoutParamsCompanyResume

        replaceFragment(CompanyEmployeeResumeFragment())
    }

    override fun onConfirmButtonClicked() {
        binding.nameCompany.text = companyInfo!!.nameCompany
        binding.locationCompany.text = companyInfo!!.addressCompany
        binding.numberCompany.text = companyInfo!!.phoneCompany
        binding.idGithaub.text = companyInfo!!.githubCompany
        binding.idLinkedin.text = companyInfo!!.linkedinCompany
    }

}