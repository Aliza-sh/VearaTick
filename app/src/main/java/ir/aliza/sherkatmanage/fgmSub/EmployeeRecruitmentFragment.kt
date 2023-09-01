package ir.aliza.sherkatmanage.fgmSub

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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.yalantis.ucrop.UCrop
import ir.aliza.sherkatmanage.DataBase.EfficiencyDao
import ir.aliza.sherkatmanage.DataBase.EfficiencyEmployee
import ir.aliza.sherkatmanage.DataBase.Employee
import ir.aliza.sherkatmanage.R
import ir.aliza.sherkatmanage.databinding.ActivityProAndEmpBinding
import ir.aliza.sherkatmanage.databinding.FragmentEmployeeRecruitmentBinding
import ir.aliza.sherkatmanage.employeeAdapter
import ir.aliza.sherkatmanage.employeeDao
import java.io.File

private val PICK_IMAGE_REQUEST = 1

class EmployeeRecruitmentFragment(
    val bindingActivityProAndEmpBinding: ActivityProAndEmpBinding,
    val efficiencyEmployeeDao: EfficiencyDao
) : Fragment() {

    lateinit var binding: FragmentEmployeeRecruitmentBinding
    var imageUri: Uri? = null
    lateinit var imageBytes: ByteArray
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

        val myAdapteredt = ArrayAdapter(requireContext(), R.layout.item_gender, gender)
        (binding.dialogMainEdtGdrperson.editText as AutoCompleteTextView).setAdapter(
            myAdapteredt
        )

        binding.sheetBtnDone.setOnClickListener {
            addNewEmployee()
            onRecruitment()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == AppCompatActivity.RESULT_OK && data != null) {
            val selectedImageUri: Uri = data.data!!
            val options = UCrop.Options()
            options.setCircleDimmedLayer(true)
            options.setShowCropGrid(false)
            options.setCompressionQuality(100)
            options.setToolbarTitle("Crop Image")
            options.setStatusBarColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.black_light
                )
            )
            options.setToolbarColor(ContextCompat.getColor(requireActivity(), R.color.black_light))
            options.setActiveControlsWidgetColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.firoze
                )
            )
            options.setToolbarWidgetColor(ContextCompat.getColor(requireActivity(), R.color.white))
            options.setDimmedLayerColor(ContextCompat.getColor(requireActivity(), R.color.blacke))
            options.setToolbarCropDrawable(R.drawable.ic_crop)
            options.setFreeStyleCropEnabled(true)
            val destinationUri = Uri.fromFile(File(requireActivity().cacheDir, "avatar"))
            UCrop.of(selectedImageUri, destinationUri)
                .withAspectRatio(1f, 1f)
                .withOptions(options)
                .start(requireActivity(), 2)
        }
        imageUri = UCrop.getOutput(data!!)
        Toast.makeText(context, "$imageUri", Toast.LENGTH_SHORT).show()
        Glide.with(this)
            .load(UCrop.getOutput(data))
            .apply(RequestOptions.circleCropTransform())
            .into(binding.imgprn2)
    }

    private fun addNewEmployee() {
        if (
            binding.edtNameEpm.length() > 0 &&
            binding.edtFamEmp.length() > 0 &&
            binding.edtAgeEmp.length() > 0 &&
            binding.edtGenEmp.length() > 0 &&
            binding.edtNumEmp.length() > 0 &&
            binding.edtTakhasosEmp.length() > 0
        ) {
            val txtname = binding.edtNameEpm.text.toString()
            val txtFamily = binding.edtFamEmp.text.toString()
            val txtAge = binding.edtAgeEmp.text.toString()
            val txtGender = binding.edtGenEmp.text.toString()
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
            )

            if (imageUri != null) {
                val inputStream = context?.contentResolver?.openInputStream(imageUri!!)
                imageBytes = inputStream?.readBytes()!!

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
                    imgEmployee = imageBytes
                )
            }

            employeeAdapter.addEmployee(newEmployee)
            employeeDao.insert(newEmployee)

            val employee = employeeDao.getObjectAllEmployee(txtname, txtFamily, txtNumber.toLong())

            val newEfficiencyEmployee = EfficiencyEmployee(
                idEmployee = employee?.idEmployee!!,
            )
            efficiencyEmployeeDao.insert(newEfficiencyEmployee)

        } else {
            Toast.makeText(context, "لطفا همه مقادیر را وارد کنید", Toast.LENGTH_SHORT).show()
        }
    }
}
