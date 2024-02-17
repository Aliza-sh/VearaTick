package com.vearad.vearatick.ui.fragmentssub

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import com.vearad.vearatick.R
import com.vearad.vearatick.adapter.ProjectAdapter
import com.vearad.vearatick.databinding.ActivityProAndEmpBinding
import com.vearad.vearatick.databinding.FragmentProjectBinding
import com.vearad.vearatick.model.db.AppDatabase
import com.vearad.vearatick.model.db.Project
import com.vearad.vearatick.model.db.ProjectDao
import com.vearad.vearatick.model.db.SubTaskProjectDao
import com.vearad.vearatick.ui.MainActivity
import com.vearad.vearatick.ui.activitymain.ProAndEmpActivity
import com.vearad.vearatick.ui.projectAdapter
import com.vearad.vearatick.utils.BottomMarginItemDecoration
import com.vearad.vearatick.utils.CustomBottomMarginItemDecoration
import com.vearad.vearatick.utils.CustomTopMarginItemDecoration


class ProjectFragment(
    val bindingActivityProAndEmp: ActivityProAndEmpBinding,
    val idNotifProject: Int
) : Fragment(),
    ProjectAdapter.ProjectNearEvents {

    lateinit var binding: FragmentProjectBinding
    lateinit var subTaskProjectDao: SubTaskProjectDao
    lateinit var projectDao: ProjectDao
    lateinit var projectNearData: List<Project>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProjectBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBackPressed()
        projectDao = AppDatabase.getDataBase(view.context).projectDao
        subTaskProjectDao = AppDatabase.getDataBase(view.context).subTaskProjectDao

        if (idNotifProject != 0) {
            val project = projectDao.getProject(idNotifProject)
            onProjectNotification(project!!)
        } else {

            projectNearData = projectDao.getAllProject()
            projectAdapter = ProjectAdapter(ArrayList(projectNearData), this, projectDao)
            val companySkillDao = AppDatabase.getDataBase(view.context).companySkillDao

            val tapTargetSequence = tapTargetSequence()
            if (companySkillDao.getAllListCompanySkillDao().isEmpty())
                tapTargetSequence?.start()

            binding.recyclerViewProject.adapter = projectAdapter
            binding.recyclerViewProject.layoutManager = GridLayoutManager(context, 2)
            val topMargin = 30 // اندازه مارجین بالا را از منابع دریافت کنید
            val itemDecoratio = CustomTopMarginItemDecoration(topMargin)
            binding.recyclerViewProject.addItemDecoration(itemDecoratio)

            val itemCount = projectNearData.size // تعداد آیتم‌های موجود در لیست را دریافت کنید
            if (itemCount % 2 == 0) {
                val bottomMargin = 200 // اندازه مارجین پایین را از منابع دریافت کنید
                val itemDecoration = CustomBottomMarginItemDecoration(bottomMargin)
                binding.recyclerViewProject.addItemDecoration(itemDecoration)
            } else {
                val bottomMargin = 200 // اندازه مارجین پایین را از منابع دریافت کنید
                val itemDecoration = BottomMarginItemDecoration(bottomMargin)
                binding.recyclerViewProject.addItemDecoration(itemDecoration)
            }

            onFabClicked()
        }
    }

    fun onProjectNotification(project: Project) {
        val transaction = (activity as ProAndEmpActivity).supportFragmentManager.beginTransaction()
        transaction.hide(this@ProjectFragment)
        transaction.replace(
            R.id.layout_pro_and_emp,
            ProjectInformationFragment(
                project,
                subTaskProjectDao,
                projectDao,
                0,
                bindingActivityProAndEmp,
                false
            )
        )
            .commit()
    }

    fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)
                    requireActivity().overridePendingTransition(
                        R.anim.slide_from_right,
                        R.anim.slide_to_left
                    )
                }
            })
    }

    private fun tapTargetSequence(): TapTargetSequence? {
        val tapTargetSequence = TapTargetSequence(requireActivity())
            .targets(
                TapTarget.forView(
                    bindingActivityProAndEmp.btnAdd,
                    "مهارتی ثبت نشده است.",
                    "لطفا به قسمت درباره شرکت رفته و مهارت های خود را ثبت کنید."
                )
                    .cancelable(true)
                    .textTypeface(Typeface.DEFAULT_BOLD)
                    .titleTextSize(20)
                    .descriptionTextColor(R.color.blacke)
                    .transparentTarget(true)
                    .targetCircleColor(R.color.firoze)
                    .titleTextColor(R.color.white)
                    .targetRadius(60)
                    .id(1)
            ).listener(object : TapTargetSequence.Listener {
                override fun onSequenceFinish() {
                    // دنباله Tap Target ها به پایان رسید
                }

                override fun onSequenceStep(
                    lastTarget: TapTarget?,
                    targetClicked: Boolean
                ) {
                    // مرحله جدید Tap Target در دنباله
                }

                override fun onSequenceCanceled(lastTarget: TapTarget?) {
                    // دنباله Tap Target ها لغو شد
                }
            })

        return tapTargetSequence

    }


    private fun updateYourData() {
        projectNearData = projectDao.getAllProject()
        projectAdapter = ProjectAdapter(ArrayList(projectNearData), this, projectDao)
        binding.recyclerViewProject.adapter = projectAdapter
        binding.recyclerViewProject.layoutManager = GridLayoutManager(context, 2)
        subTaskProjectDao = AppDatabase.getDataBase(binding.root.context).subTaskProjectDao
        val topMargin = 10 // اندازه مارجین بالا را از منابع دریافت کنید
        val itemDecoratio = CustomTopMarginItemDecoration(topMargin)
        binding.recyclerViewProject.addItemDecoration(itemDecoratio)
        val itemCount = projectNearData.size // تعداد آیتم‌های موجود در لیست را دریافت کنید
        if (itemCount % 2 == 0) {
            val bottomMargin = 150 // اندازه مارجین پایین را از منابع دریافت کنید
            val itemDecoration = CustomBottomMarginItemDecoration(bottomMargin)
            binding.recyclerViewProject.addItemDecoration(itemDecoration)
        } else {
            val bottomMargin = 150 // اندازه مارجین پایین را از منابع دریافت کنید
            val itemDecoration = BottomMarginItemDecoration(bottomMargin)
            binding.recyclerViewProject.addItemDecoration(itemDecoration)
        }
    }

    fun onFabClicked() {
        bindingActivityProAndEmp.btnAdd.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.add(
                R.id.layout_pro_and_emp,
                ProjectNewFragment(projectDao, bindingActivityProAndEmp)
            )
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onProjectClicked(project: Project, position: Int) {
        val transaction = (activity as ProAndEmpActivity).supportFragmentManager.beginTransaction()
        transaction.detach(this@ProjectFragment)
        transaction.add(
            R.id.layout_pro_and_emp, ProjectInformationFragment(
                project,
                subTaskProjectDao,
                projectDao,
                position,
                bindingActivityProAndEmp,
                false
            )
        )
            .addToBackStack(null)
            .commit()
    }

    override fun onProjectLongClicked(project: Project, position: Int) {}

}