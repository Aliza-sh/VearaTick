package com.vearad.vearatick.ui.fragmentssub

import com.vearad.vearatick.utils.BottomMarginItemDecoration
import com.vearad.vearatick.utils.TopMarginItemDecoration
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.vearad.vearatick.utils.BottomSheetCallback
import com.vearad.vearatick.model.db.AppDatabase
import com.vearad.vearatick.model.db.Project
import com.vearad.vearatick.model.db.ProjectDao
import com.vearad.vearatick.dialog.CompanyResumeSetLinkBottomsheetFragment
import com.vearad.vearatick.ui.activitymain.ProAndEmpActivity
import com.vearad.vearatick.R
import com.vearad.vearatick.adapter.CompanyResumeAdapter
import com.vearad.vearatick.databinding.ActivityProAndEmpBinding
import com.vearad.vearatick.databinding.FragmentCompanyResumeBinding

class CompanyResumeFragment : Fragment(), CompanyResumeAdapter.CompanyResumeEvent,
    BottomSheetCallback {

    lateinit var binding: FragmentCompanyResumeBinding
    lateinit var bindingActivityProAndEmp: ActivityProAndEmpBinding
    lateinit var companyResumeAdapter: CompanyResumeAdapter
    lateinit var companyResumeDao: ProjectDao
    private var callback: BottomSheetCallback? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCompanyResumeBinding.inflate(layoutInflater, container, false)
        bindingActivityProAndEmp =
            ActivityProAndEmpBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        companyResumeDao = AppDatabase.getDataBase(view.context).projectDao
        val companyResumeData = companyResumeDao.getAllProject()
        companyResumeAdapter =
            CompanyResumeAdapter(
                ArrayList(companyResumeData),
                this,
            )
        binding.rcvResume.layoutManager = LinearLayoutManager(context)
        binding.rcvResume.adapter = companyResumeAdapter
        topMargin()
        bottomMargin()

    }

    private fun topMargin() {
        val topMargin = 270 // اندازه مارجین بالا را از منابع دریافت کن
        val itemDecoratio = TopMarginItemDecoration(topMargin)
        binding.rcvResume.addItemDecoration(itemDecoratio)
    }

    private fun bottomMargin() {
        val bottomMargin = 220 // اندازه مارجین پایین را از منابع دریافت کنید
        val itemDecoration = BottomMarginItemDecoration(bottomMargin)
        binding.rcvResume.addItemDecoration(itemDecoration)
    }

    override fun onResumeClicked(companyResume: Project, position: Int) {

        if (companyResume.urlProject == "") {
            val snackbar =
                Snackbar.make(binding.root, "لینک گیت هاب موجود نیست!", Snackbar.LENGTH_LONG)
                    .setAction("تنظیم لینک") {
                        val bottomsheet = CompanyResumeSetLinkBottomsheetFragment(
                            companyResumeDao,
                            companyResume
                        )
                        bottomsheet.setStyle(
                            R.style.BottomSheetStyle,
                            R.style.BottomSheetDialogTheme
                        )
                        bottomsheet.setCallback(this)
                        bottomsheet.show(parentFragmentManager, null)
                    }
            snackbar.view.layoutDirection = View.LAYOUT_DIRECTION_RTL
            snackbar.show()
        } else {
            val url = companyResume.urlProject
            // Intent برای باز کردن لینک در مرورگر
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
    }

    override fun onMenuItemClick(companyResume: Project, position: Int) {

        val onClickSkill = companyResumeDao.getProject(companyResume.idProject!!)
        val viewHolder =
            binding.rcvResume.findViewHolderForAdapterPosition(position) as CompanyResumeAdapter.CompanyResumeViewHolder
        viewHolder.let { holder ->
            val btnMenuSubTaskProject = holder.btnMenu
            val popupMenu = PopupMenu(context, btnMenuSubTaskProject)
            popupMenu.inflate(R.menu.menu_resume)
            popupMenu.show()

            popupMenu.setOnMenuItemClickListener { item ->

                when (item.itemId) {

                    R.id.menu_goto_pro -> {
                        val intent = Intent(requireContext(), ProAndEmpActivity::class.java)
                        startActivity(intent)
                        activity?.overridePendingTransition(
                            R.anim.slide_from_left,
                            R.anim.slide_to_right
                        )
//                        val subTaskProjectDao =
//                            AppDatabase.getDataBase(binding.root.context).subTaskProjectDao
//                        val transaction =
//                            (activity as MainActivity).supportFragmentManager.beginTransaction()
//                        transaction.add(
//                            R.id.frame_layout_main, ProjectInformationFragment(
//                                onClickSkill!!, subTaskProjectDao,
//                                companyResumeDao, position, bindingActivityProAndEmp, true
//                            )
//                        )
//                            .addToBackStack(null)
//                            .commit()
                        true
                    }

                    R.id.menu_edit_link -> {
                        val bottomsheet = CompanyResumeSetLinkBottomsheetFragment(
                            companyResumeDao,
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

                    else -> false
                }
            }
        }
    }

    fun setCallback(callback: BottomSheetCallback) {
        this.callback = callback
    }

    override fun onConfirmButtonClicked() {
        companyResumeDao = AppDatabase.getDataBase(binding.root.context).projectDao
        val companyResumeData = companyResumeDao.getAllProject()
        companyResumeAdapter =
            CompanyResumeAdapter(
                ArrayList(companyResumeData),
                this,
            )
        binding.rcvResume.layoutManager = LinearLayoutManager(context)
        binding.rcvResume.adapter = companyResumeAdapter
    }
}