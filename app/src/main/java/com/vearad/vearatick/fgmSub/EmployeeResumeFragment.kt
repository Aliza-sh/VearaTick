package com.vearad.vearatick.fgmSub

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import com.vearad.vearatick.DataBase.Employee
import com.vearad.vearatick.DataBase.EmployeeDao
import com.vearad.vearatick.MainActivity
import com.vearad.vearatick.R
import com.vearad.vearatick.databinding.FragmentEmployeeResumeBinding

class EmployeeResumeFragment(
    val companyEmployeeResume: Employee,
    val companyEmployeeResumeDao: EmployeeDao
) : Fragment() {
    lateinit var binding: FragmentEmployeeResumeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            FragmentEmployeeResumeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firstRun(view)
        onBackPressed()

        binding.btnBck.setOnClickListener {
            parentFragmentManager.popBackStack();
        }

        binding.btnEmployeeSkill.setOnClickListener {
            btnEmployeeSkill(view)
        }
        binding.btnEmployeeResume.setOnClickListener {
            btnEmployeeResume(view)
        }
        binding.btnEmployeeCharacter.setOnClickListener {
            btnEmployeeCharacter(view)
        }
    }

    private fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    parentFragmentManager.popBackStack()
                }
            })
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = (activity as MainActivity).supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_emp_info, fragment)
            .commit()
    }

    private fun firstRun(view: View) {
        btnEmployeeSkill(view)
        replaceFragment(CompanySkillFragment())
    }

    private fun btnEmployeeSkill(view: View) {

        binding.icEmployeeSkill.backgroundTintList =
            ContextCompat.getColorStateList(view.context, R.color.firoze)
        binding.txtEmployeeSkill.setTextColor(Color.parseColor("#E600ADB5"))
        binding.txtEmployeeSkill.textSize = 11f
        var layoutParamsCompanySkill = binding.btnEmployeeSkill.layoutParams
        layoutParamsCompanySkill.height = resources.getDimensionPixelSize(R.dimen.size_70)
        binding.btnEmployeeSkill.layoutParams = layoutParamsCompanySkill
        ViewCompat.setElevation(
            binding.btnEmployeeSkill,
            resources.getDimension(R.dimen.elevation_10)
        )
        layoutParamsCompanySkill = binding.icEmployeeSkill.layoutParams
        layoutParamsCompanySkill.width = resources.getDimensionPixelSize(R.dimen.size_40)
        layoutParamsCompanySkill.height = resources.getDimensionPixelSize(R.dimen.size_40)
        binding.icEmployeeSkill.layoutParams = layoutParamsCompanySkill

        binding.icEmployeeResume.backgroundTintList =
            ContextCompat.getColorStateList(view.context, R.color.blacke)
        binding.txtEmployeeResume.setTextColor(Color.parseColor("#FFFFFF"))
        binding.txtEmployeeResume.textSize = 9f
        var layoutParamsCompanyResume = binding.btnEmployeeResume.layoutParams
        layoutParamsCompanyResume.height = resources.getDimensionPixelSize(R.dimen.size_60)
        binding.btnEmployeeResume.layoutParams = layoutParamsCompanyResume
        ViewCompat.setElevation(
            binding.btnEmployeeResume,
            resources.getDimension(R.dimen.elevation_5)
        )
        layoutParamsCompanyResume = binding.icEmployeeResume.layoutParams
        layoutParamsCompanyResume.width = resources.getDimensionPixelSize(R.dimen.size_35)
        layoutParamsCompanyResume.height = resources.getDimensionPixelSize(R.dimen.size_35)
        binding.icEmployeeResume.layoutParams = layoutParamsCompanyResume

        binding.icEmployeeCharacter.backgroundTintList =
            ContextCompat.getColorStateList(view.context, R.color.blacke)
        binding.txtEmployeeCharacter.setTextColor(Color.parseColor("#FFFFFF"))
        binding.txtEmployeeCharacter.textSize = 9f
        var layoutParamsEmployeeResume = binding.btnEmployeeCharacter.layoutParams
        layoutParamsEmployeeResume.height = resources.getDimensionPixelSize(R.dimen.size_60)
        binding.btnEmployeeCharacter.layoutParams = layoutParamsEmployeeResume
        ViewCompat.setElevation(
            binding.btnEmployeeCharacter,
            resources.getDimension(R.dimen.elevation_5)
        )
        layoutParamsEmployeeResume = binding.icEmployeeCharacter.layoutParams
        layoutParamsEmployeeResume.width = resources.getDimensionPixelSize(R.dimen.size_35)
        layoutParamsEmployeeResume.height = resources.getDimensionPixelSize(R.dimen.size_35)
        binding.icEmployeeCharacter.layoutParams = layoutParamsEmployeeResume

        replaceFragment(CompanySkillFragment())
    }

    private fun btnEmployeeResume(view: View) {
        binding.icEmployeeResume.backgroundTintList =
            ContextCompat.getColorStateList(view.context, R.color.firoze)
        binding.txtEmployeeResume.setTextColor(Color.parseColor("#E600ADB5"))
        binding.txtEmployeeResume.textSize = 11f
        var layoutParamsCompanyResume = binding.btnEmployeeResume.layoutParams
        layoutParamsCompanyResume.height = resources.getDimensionPixelSize(R.dimen.size_70)
        binding.btnEmployeeResume.layoutParams = layoutParamsCompanyResume
        ViewCompat.setElevation(
            binding.btnEmployeeResume,
            resources.getDimension(R.dimen.elevation_10)
        )
        layoutParamsCompanyResume = binding.icEmployeeResume.layoutParams
        layoutParamsCompanyResume.width = resources.getDimensionPixelSize(R.dimen.size_40)
        layoutParamsCompanyResume.height = resources.getDimensionPixelSize(R.dimen.size_40)
        binding.icEmployeeResume.layoutParams = layoutParamsCompanyResume

        binding.icEmployeeSkill.backgroundTintList =
            ContextCompat.getColorStateList(view.context, R.color.blacke)
        binding.txtEmployeeSkill.setTextColor(Color.parseColor("#FFFFFF"))
        binding.txtEmployeeSkill.textSize = 9f
        var layoutParamsCompanySkill = binding.btnEmployeeSkill.layoutParams
        layoutParamsCompanySkill.height = resources.getDimensionPixelSize(R.dimen.size_60)
        binding.btnEmployeeSkill.layoutParams = layoutParamsCompanySkill
        ViewCompat.setElevation(
            binding.btnEmployeeSkill,
            resources.getDimension(R.dimen.elevation_5)
        )
        layoutParamsCompanySkill = binding.icEmployeeSkill.layoutParams
        layoutParamsCompanySkill.width = resources.getDimensionPixelSize(R.dimen.size_35)
        layoutParamsCompanySkill.height = resources.getDimensionPixelSize(R.dimen.size_35)
        binding.icEmployeeSkill.layoutParams = layoutParamsCompanySkill

        binding.icEmployeeCharacter.backgroundTintList =
            ContextCompat.getColorStateList(view.context, R.color.blacke)
        binding.txtEmployeeCharacter.setTextColor(Color.parseColor("#FFFFFF"))
        binding.txtEmployeeCharacter.textSize = 9f
        var layoutParamsEmployeeResume = binding.btnEmployeeCharacter.layoutParams
        layoutParamsEmployeeResume.height = resources.getDimensionPixelSize(R.dimen.size_60)
        binding.btnEmployeeCharacter.layoutParams = layoutParamsEmployeeResume
        ViewCompat.setElevation(
            binding.btnEmployeeCharacter,
            resources.getDimension(R.dimen.elevation_5)
        )
        layoutParamsEmployeeResume = binding.icEmployeeCharacter.layoutParams
        layoutParamsEmployeeResume.width = resources.getDimensionPixelSize(R.dimen.size_35)
        layoutParamsEmployeeResume.height = resources.getDimensionPixelSize(R.dimen.size_35)
        binding.icEmployeeCharacter.layoutParams = layoutParamsEmployeeResume

        replaceFragment(CompanyResumeFragment())
    }

    private fun btnEmployeeCharacter(view: View) {
        binding.icEmployeeCharacter.backgroundTintList =
            ContextCompat.getColorStateList(view.context, R.color.firoze)
        binding.txtEmployeeCharacter.setTextColor(Color.parseColor("#E600ADB5"))
        binding.txtEmployeeCharacter.textSize = 11f
        var layoutParamsEmployeeResume = binding.btnEmployeeCharacter.layoutParams
        layoutParamsEmployeeResume.height = resources.getDimensionPixelSize(R.dimen.size_70)
        binding.btnEmployeeCharacter.layoutParams = layoutParamsEmployeeResume
        ViewCompat.setElevation(
            binding.btnEmployeeCharacter,
            resources.getDimension(R.dimen.elevation_10)
        )
        layoutParamsEmployeeResume = binding.icEmployeeCharacter.layoutParams
        layoutParamsEmployeeResume.width = resources.getDimensionPixelSize(R.dimen.size_40)
        layoutParamsEmployeeResume.height = resources.getDimensionPixelSize(R.dimen.size_40)
        binding.icEmployeeCharacter.layoutParams = layoutParamsEmployeeResume

        binding.icEmployeeSkill.backgroundTintList =
            ContextCompat.getColorStateList(view.context, R.color.blacke)
        binding.txtEmployeeSkill.setTextColor(Color.parseColor("#FFFFFF"))
        binding.txtEmployeeSkill.textSize = 9f
        var layoutParamsCompanySkill = binding.btnEmployeeSkill.layoutParams
        layoutParamsCompanySkill.height = resources.getDimensionPixelSize(R.dimen.size_60)
        binding.btnEmployeeSkill.layoutParams = layoutParamsCompanySkill
        ViewCompat.setElevation(
            binding.btnEmployeeSkill,
            resources.getDimension(R.dimen.elevation_5)
        )
        layoutParamsCompanySkill = binding.icEmployeeSkill.layoutParams
        layoutParamsCompanySkill.width = resources.getDimensionPixelSize(R.dimen.size_35)
        layoutParamsCompanySkill.height = resources.getDimensionPixelSize(R.dimen.size_35)
        binding.icEmployeeSkill.layoutParams = layoutParamsCompanySkill

        binding.icEmployeeResume.backgroundTintList =
            ContextCompat.getColorStateList(view.context, R.color.blacke)
        binding.txtEmployeeResume.setTextColor(Color.parseColor("#FFFFFF"))
        binding.txtEmployeeResume.textSize = 9f
        var layoutParamsCompanyResume = binding.btnEmployeeResume.layoutParams
        layoutParamsCompanyResume.height = resources.getDimensionPixelSize(R.dimen.size_60)
        binding.btnEmployeeResume.layoutParams = layoutParamsCompanyResume
        ViewCompat.setElevation(
            binding.btnEmployeeResume,
            resources.getDimension(R.dimen.elevation_5)
        )
        layoutParamsCompanyResume = binding.icEmployeeResume.layoutParams
        layoutParamsCompanyResume.width = resources.getDimensionPixelSize(R.dimen.size_35)
        layoutParamsCompanyResume.height = resources.getDimensionPixelSize(R.dimen.size_35)
        binding.icEmployeeResume.layoutParams = layoutParamsCompanyResume

        replaceFragment(CompanyEmployeeResumeFragment())
    }
}