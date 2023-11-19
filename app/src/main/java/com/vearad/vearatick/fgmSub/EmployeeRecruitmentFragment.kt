package com.vearad.vearatick.fgmSub

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.vearad.vearatick.DataBase.EfficiencyDao
import com.vearad.vearatick.DataBase.EfficiencyEmployee
import com.vearad.vearatick.DataBase.Employee
import com.vearad.vearatick.R
import com.vearad.vearatick.databinding.ActivityProAndEmpBinding
import com.vearad.vearatick.databinding.FragmentEmployeeRecruitmentBinding
import com.vearad.vearatick.employeeAdapter
import com.vearad.vearatick.employeeDao

private val PICK_IMAGE_REQUEST = 1

class EmployeeRecruitmentFragment(
    val bindingActivityProAndEmpBinding: ActivityProAndEmpBinding,
    val efficiencyEmployeeDao: EfficiencyDao
) : Fragment() {

    lateinit var binding: FragmentEmployeeRecruitmentBinding
    var imageUri: Uri? = null
    var imagePath: Uri? = null
    lateinit var newEmployee: Employee

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEmployeeRecruitmentBinding.inflate(layoutInflater, null, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBackPressed()

        val gender = listOf(
            "مرد",
            "زن"
        )

        val myAdapterGender = ArrayAdapter(requireContext(), R.layout.item_gender, gender)
        (binding.dialogMainEdtGdrperson.editText as AutoCompleteTextView).setAdapter(
            myAdapterGender
        )

        val rank = listOf(
            "سهام دار",
            "کارمند",
            "کارآموز"
        )

        val myAdapterRank = ArrayAdapter(requireContext(), R.layout.item_rank, rank)
        (binding.dialogMainEdtRankPerson.editText as AutoCompleteTextView).setAdapter(
            myAdapterRank
        )

        binding.sheetBtnDone.setOnClickListener {
            addNewEmployee()
        }

        binding.imgprn2.setOnClickListener {
            pickImage()
        }

        binding.btnBck.setOnClickListener {
            if (parentFragmentManager.backStackEntryCount > 0) {
                parentFragmentManager.popBackStack()
            }
        }
    }

    fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    fragmentManager?.beginTransaction()?.detach(this@EmployeeRecruitmentFragment)
                        ?.attach(EmployeeFragment(bindingActivityProAndEmpBinding))?.commit()
                }
            })
    }

    fun onRecruitment() {
        fragmentManager?.beginTransaction()?.detach(this@EmployeeRecruitmentFragment)
            ?.replace(R.id.frame_layout_sub, EmployeeFragment(bindingActivityProAndEmpBinding))
            ?.commit()
    }

    fun pickImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == AppCompatActivity.RESULT_OK && data != null) {
            val selectedImageUri: Uri = data.data!!
            imagePath = selectedImageUri
            imageUri = selectedImageUri
            Glide.with(this)
                .load(selectedImageUri)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.imgprn2)
        }
    }

    private fun addNewEmployee() {
        if (
            binding.edtNameEpm.length() > 0 &&
            binding.edtFamEmp.length() > 0 &&
            binding.edtAgeEmp.length() > 0 &&
            binding.edtGenEmp.length() > 0 &&
            binding.edtRankEmp.length() > 0 &&
            binding.edtNumEmp.length() > 0 &&
            binding.edtTakhasosEmp.length() > 0
        ) {
            val txtname = binding.edtNameEpm.text.toString()
            val txtFamily = binding.edtFamEmp.text.toString()
            val txtAge = binding.edtAgeEmp.text.toString()
            val txtGender = binding.edtGenEmp.text.toString()
            val txtRank = binding.edtRankEmp.text.toString()
            val txtSpecialty = binding.edtTakhasosEmp.text.toString()
            val txtNumber = binding.edtNumEmp.text.toString()
            var txtNumberHome = binding.edtNumbhomeEmp.text.toString()
            val txtAddress = binding.edtAddressEmp.text.toString()
            val txtMaharat = binding.edtMaharatEmp.text.toString()

            if (txtNumberHome == "")
                txtNumberHome = "0"

            newEmployee = Employee(
                name = txtname,
                family = txtFamily,
                age = txtAge.toInt(),
                gender = txtGender,
                cellularPhone = txtNumber.toLong(),
                homePhone = txtNumberHome.toLong(),
                address = txtAddress,
                specialty = txtSpecialty,
                skill = txtMaharat,
                rank = txtRank
            )

            if (imageUri != null) {

                newEmployee = Employee(
                    name = txtname,
                    family = txtFamily,
                    age = txtAge.toInt(),
                    gender = txtGender,
                    cellularPhone = txtNumber.toLong(),
                    homePhone = txtNumberHome.toLong(),
                    address = txtAddress,
                    specialty = txtSpecialty,
                    skill = txtMaharat,
                    imagePath = imagePath.toString(),
                    rank = txtRank
                )
            }

            employeeAdapter.addEmployee(newEmployee)
            employeeDao.insert(newEmployee)

            val employee = employeeDao.getObjectAllEmployee(txtname, txtFamily, txtNumber.toLong())

            val newEfficiencyEmployee = EfficiencyEmployee(
                idEmployee = employee?.idEmployee!!,
                mustWeekWatch = 0,
                numberDay = 0,
                totalWeekWatch = 0,
                totalWatch = 0,
                efficiencyWeekPresence = 0,
                efficiencyTotalPresence = 0,
                totalWeekDuties = 0,
                totalMonthDuties = 0,
                totalDuties = 0,
                efficiencyWeekDuties = 0,
                efficiencyTotalDuties = 0,
                efficiencyTotal = 0,
                totalMonthWatch = 0,
                efficiencyMonthDuties = 0,
            )

            efficiencyEmployeeDao.insert(newEfficiencyEmployee)
            onRecruitment()

        } else {
            Toast.makeText(context, "لطفا همه مقادیر را وارد کنید", Toast.LENGTH_SHORT).show()
        }
    }
}
