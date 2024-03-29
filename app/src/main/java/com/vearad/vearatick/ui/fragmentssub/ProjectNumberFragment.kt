package com.vearad.vearatick.ui.fragmentssub

import com.vearad.vearatick.utils.BottomMarginItemDecoration
import com.vearad.vearatick.utils.CustomBottomMarginItemDecoration
import com.vearad.vearatick.utils.CustomTopMarginItemDecoration
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.vearad.vearatick.model.db.AppDatabase
import com.vearad.vearatick.model.db.CompanySkill
import com.vearad.vearatick.model.db.ProjectDao
import com.vearad.vearatick.ui.MainActivity
import com.vearad.vearatick.R
import com.vearad.vearatick.adapter.ProjectNumberAdapter
import com.vearad.vearatick.databinding.FragmentNumberProjectBinding

class ProjectNumberFragment(val projectDao: ProjectDao) : Fragment() {

    lateinit var binding:FragmentNumberProjectBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNumberProjectBinding.inflate(layoutInflater, container, false)

        return binding.root
    }
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBackPressed()

        binding.btnBck.setOnClickListener {
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
            activity?.overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
        }

        val companySkillDao = AppDatabase.getDataBase(view.context).companySkillDao
        var companySkillData = companySkillDao.getAllListCompanySkillDao()
        Log.v("companySkillData", companySkillData.toString())

        val numProjectDefault = projectDao.getNumberProject("دسته بندی نشده").size

        if (numProjectDefault > 0) {
            val manuallyAddedSkills = CompanySkill(
                idCompanySkill = 0,
                nameCompanySkill = "دسته بندی نشده",
                volumeSkill = 0
            )

            val companySkillList: MutableList<CompanySkill> = mutableListOf()
            companySkillList.add(manuallyAddedSkills)

            companySkillData = companySkillList + companySkillData
        }

        val ProjectNumberFAdapter = ProjectNumberAdapter(ArrayList(companySkillData),projectDao)
        val topMargin = 150 // اندازه مارجین بالا را از منابع دریافت کنید
        val itemDecoratio = CustomTopMarginItemDecoration(topMargin)
        binding.rcvNumPro.addItemDecoration(itemDecoratio)
        binding.rcvNumPro.layoutManager = GridLayoutManager(context,2)
        binding.rcvNumPro.adapter = ProjectNumberFAdapter
        val itemCount = companySkillData.size // تعداد آیتم‌های موجود در لیست را دریافت کنید
        if (itemCount % 2 == 0) {
            val bottomMargin = 100 // اندازه مارجین پایین را از منابع دریافت کنید
            val itemDecoration = CustomBottomMarginItemDecoration(bottomMargin)
            binding.rcvNumPro.addItemDecoration(itemDecoration)
        } else {
            val bottomMargin = 100 // اندازه مارجین پایین را از منابع دریافت کنید
            val itemDecoration = BottomMarginItemDecoration(bottomMargin)
            binding.rcvNumPro.addItemDecoration(itemDecoration)
        }

    }
    private fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)
                    activity?.overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
                }
            })
    }

}