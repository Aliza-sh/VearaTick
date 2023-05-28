package ir.aliza.sherkatmanage.fgmSub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import ir.aliza.sherkatmanage.DataBase.Project
import ir.aliza.sherkatmanage.R
import ir.aliza.sherkatmanage.databinding.FragmentNewProjectBinding
import ir.aliza.sherkatmanage.projectAdapter
import ir.aliza.sherkatmanage.projectDao

class NewProjectFragment : Fragment() {
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

        val gender = listOf(
            "اندروید",
            "بک اند",
            "فرانت",
            "رباتیک",
            "سایت",
            "فتوشاپ"
        )

        val myAdapteredt = ArrayAdapter(requireContext(), R.layout.item_gender, gender)
        (binding.dialogMainEdtGdrperson.editText as AutoCompleteTextView).setAdapter(
            myAdapteredt
        )

        binding.btnBck.setOnClickListener {
            onBackPressed()
        }
        binding.sheetBtnDone.setOnClickListener {
            addNewEmployee()
        }
    }

    fun onBackPressed() {
        if (parentFragmentManager.backStackEntryCount > 0) {
            parentFragmentManager.popBackStack()
        } else {
            onBackPressed()
        }
    }

    private fun addNewEmployee() {
        if (
            binding.edtNamePro.length() > 0 &&
            binding.edtDayProject.length() > 0 &&
            binding.edtTypeProject.length() > 0 &&
            binding.edtInfoPro.length() > 0 &&
            binding.edtTimePro.length() > 0
        ) {
            val txtname = binding.edtNamePro.text.toString()
            val txtDay = binding.edtDayProject.text.toString()
            val txtTime = binding.edtTimePro.text.toString()
            val txtType = binding.edtTypeProject.text.toString()
            val txtInfo = binding.edtInfoPro.text.toString()

            val newProject = Project(
                nameProject = txtname,
                dayProject = txtDay,
                watchProject = txtTime.toInt(),
                typeProject = txtType,
                informationProject = txtInfo
            )
            projectAdapter.addProject(newProject)
            projectDao.insert(newProject)
            onBackPressed()
        } else {
            Toast.makeText(context, "لطفا همه مقادیر را وارد کنید", Toast.LENGTH_SHORT).show()
        }
    }

}