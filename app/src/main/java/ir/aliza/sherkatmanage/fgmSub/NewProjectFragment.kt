package ir.aliza.sherkatmanage.fgmSub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.kizitonwose.calendarview.utils.persian.PersianCalendar
import ir.aliza.sherkatmanage.DataBase.Project
import ir.aliza.sherkatmanage.DataBase.ProjectDao
import ir.aliza.sherkatmanage.R
import ir.aliza.sherkatmanage.databinding.ActivityProAndEmpBinding
import ir.aliza.sherkatmanage.databinding.FragmentNewProjectBinding
import ir.aliza.sherkatmanage.projectAdapter

class NewProjectFragment(
    val projectDao: ProjectDao,
    val bindingActivityProAndEmp: ActivityProAndEmpBinding,) : Fragment() {
    lateinit var binding: FragmentNewProjectBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewProjectBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBackPressed()

        val typeProject = listOf(
            "اندروید",
            "بک اند",
            "فرانت اند",
            "رباتیک",
            "طراحی",
            "سئو"
        )

        val myAdapteredt = ArrayAdapter(requireContext(), R.layout.item_gender, typeProject)
        (binding.dialogMainEdtGdrperson.editText as AutoCompleteTextView).setAdapter(
            myAdapteredt
        )

        binding.btnBck.setOnClickListener {
            if (parentFragmentManager.backStackEntryCount > 0) {
                parentFragmentManager.popBackStack()
            }
        }

        binding.sheetBtnDone.setOnClickListener {
            addNewProject()
            onNewProject()
        }
    }

    fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    fragmentManager?.beginTransaction()?.detach(this@NewProjectFragment)
                        ?.attach(ProjectFragment(bindingActivityProAndEmp))?.commit()
                }
            })
    }

    fun onNewProject() {
        fragmentManager?.beginTransaction()?.detach(this@NewProjectFragment)
            ?.replace(R.id.frame_layout_sub, ProjectFragment(bindingActivityProAndEmp))
            ?.commit()
    }

    private fun addNewProject() {
        if (
            binding.edtNamePro.length() > 0 &&
            binding.edtDayProject.length() > 0 &&
            binding.edtTypeProject.length() > 0 &&
            binding.edtInfoPro.length() > 0 &&
            binding.edtTimePro.length() > 0 &&
            binding.edtTimePro.text.toString().toInt() <= 24
        ) {
            val txtname = binding.edtNamePro.text.toString()
            val txtDay = binding.edtDayProject.text.toString()
            val txtTime = binding.edtTimePro.text.toString()
            val txtType = binding.edtTypeProject.text.toString()
            val txtDescription = binding.edtInfoPro.text.toString()
            val day = PersianCalendar()

            val newProject = Project(
                nameProject = txtname,
                dayProject = txtDay.toInt(),
                watchProject = txtTime.toInt(),
                typeProject = txtType,
                descriptionProject = txtDescription,

                year = day.persianYear,
                month = day.persianMonth,
                day = day.persianDay

            )
            projectAdapter.addProject(newProject)
            projectDao.insert(newProject)
            onBackPressed()
        } else {
            Toast.makeText(context, "لطفا همه مقادیر را وارد کنید", Toast.LENGTH_SHORT).show()
        }
    }

}