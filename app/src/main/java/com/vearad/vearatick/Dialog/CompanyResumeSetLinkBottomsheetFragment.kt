package com.vearad.vearatick.Dialog

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.vearad.vearatick.BottomSheetCallback
import com.vearad.vearatick.DataBase.Project
import com.vearad.vearatick.DataBase.ProjectDao
import com.vearad.vearatick.databinding.BottomsheetfragmentCompanyResumeSetLinkBinding


class CompanyResumeSetLinkBottomsheetFragment(
    val companyResumeDao: ProjectDao,
    val onClickCompanyResume: Project?,
) : BottomSheetDialogFragment() {

    lateinit var binding: BottomsheetfragmentCompanyResumeSetLinkBinding
    private var callback: BottomSheetCallback? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            BottomsheetfragmentCompanyResumeSetLinkBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setdata()

        binding.tilUrlPro.editText?.addTextChangedListener {
            val isValidLink = Patterns.WEB_URL.matcher(it).matches()

            if (isValidLink) {
                binding.tilUrlPro.error = null
            } else {
                binding.tilUrlPro.error = "لطفا لینک را با پسوند https:// یا http:// وارد کنید."
            }
        }

        binding.sheetBtnDone.setOnClickListener {
            addNewSkill()
        }
    }

    private fun setdata() {
        if (onClickCompanyResume!!.urlProject != "")
        binding.edtUrlPro.setText(onClickCompanyResume.urlProject)
    }

    fun setCallback(callback: BottomSheetCallback) {
        this.callback = callback
    }

    fun onCompanyNewSkill() {
        callback?.onConfirmButtonClicked()
    }

    private fun addNewSkill() {
        if (
            binding.edtUrlPro.length() > 0
        ) {
            if (binding.edtUrlPro.text.toString().contains("https://") || binding.edtUrlPro.text.toString().contains("http://")) {

            val txtUrlPro = binding.edtUrlPro.text.toString()
            val newProject = Project(
                idProject = onClickCompanyResume!!.idProject,
                nameProject = onClickCompanyResume.nameProject,
                noDeadlineProject = onClickCompanyResume.noDeadlineProject,
                dayCreation = onClickCompanyResume.dayCreation,
                monthCreation = onClickCompanyResume.monthCreation,
                yearCreation = onClickCompanyResume.yearCreation,
                valueCalendar = onClickCompanyResume.valueCalendar,
                deadlineTask = onClickCompanyResume.deadlineTask,
                doneProject = onClickCompanyResume.doneProject,
                typeProject = onClickCompanyResume.typeProject,
                descriptionProject = onClickCompanyResume.descriptionProject,
                numberSubTaskProject = onClickCompanyResume.numberSubTaskProject,
                numberDoneSubTaskProject = onClickCompanyResume.numberDoneSubTaskProject,
                progressProject = onClickCompanyResume.progressProject,
                budgetProject = onClickCompanyResume.budgetProject,
                totalVolumeProject = onClickCompanyResume.totalVolumeProject,
                doneVolumeProject = onClickCompanyResume.doneVolumeProject,
                settled = onClickCompanyResume.settled,
                urlProject = txtUrlPro
            )
            companyResumeDao.update(newProject)
            onCompanyNewSkill()
            dismiss()
            } else {
                Toast.makeText(context, "لینک معتبر نیست.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "لطفا همه مقادیر را وارد کنید", Toast.LENGTH_SHORT).show()
        }
    }

}