package ir.aliza.sherkatmanage.fgmSub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import ir.aliza.sherkatmanage.DataBase.Employee
import ir.aliza.sherkatmanage.R
import ir.aliza.sherkatmanage.databinding.FragmentRecruitmentBinding
import ir.aliza.sherkatmanage.employeeAdapter
import ir.aliza.sherkatmanage.employeeDao

class RecruitmentFragment() : Fragment() {

    lateinit var binding: FragmentRecruitmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecruitmentBinding.inflate(layoutInflater, null, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val gender = listOf(
            "مرد",
            "زن"
        )

        val myAdapteredt = ArrayAdapter(requireContext(), R.layout.item_gender, gender)
        (binding.dialogMainEdtGdrperson.editText as AutoCompleteTextView).setAdapter(
            myAdapteredt
        )

        binding.sheetBtnDone.setOnClickListener {
            addNewEmployee()
        }
        binding.btnBck.setOnClickListener{
            onBackPressed()
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
            binding.edtNameEpm.length() > 0 &&
            binding.edtFamEmp.length() > 0 &&
            binding.edtAgeEmp.length() > 0 &&
            binding.edtAddressEmp.length() > 0 &&
            binding.edtGenEmp.length() > 0 &&
            binding.edtMaharatEmp.length() > 0 &&
            binding.edtNumEmp.length() > 0 &&
            binding.edtTakhasosEmp.length() > 0 &&
            binding.edtNumbhomeEmp.length() > 0 &&
            binding.edtTimeEmp.length() > 0
        ) {
            val txtname = binding.edtNameEpm.text.toString()
            val txtFamily = binding.edtFamEmp.text.toString()
            val txtAge = binding.edtAgeEmp.text.toString()
            val txtGender = binding.edtGenEmp.text.toString()
            val txtSpecialty = binding.edtTakhasosEmp.text.toString()
            val txtNumber = binding.edtNumEmp.text.toString()
            val txtNumberHome = binding.edtNumbhomeEmp.text.toString()
            val txtAddress = binding.edtAddressEmp.text.toString()
            val txtMaharat = binding.edtMaharatEmp.text.toString()
            val txtTime = binding.edtTimeEmp.text.toString()

            val newEmployee = Employee(
                name = txtname,
                family = txtFamily,
                age = txtAge.toInt(),
                gender = txtGender,
                Specialty = txtSpecialty,
                cellularPhone = txtNumber.toLong(),
                homePhone = txtNumberHome.toLong(),
                address = txtAddress
            )
            employeeAdapter.addEmployee(newEmployee)
            employeeDao.insertEmployee(newEmployee)
            onBackPressed()
        } else {
            Toast.makeText(context, "لطفا همه مقادیر را وارد کنید", Toast.LENGTH_SHORT).show()
        }
    }
}
