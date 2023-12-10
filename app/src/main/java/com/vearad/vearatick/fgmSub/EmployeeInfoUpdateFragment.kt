package com.vearad.vearatick.fgmSub

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.vearad.vearatick.DataBase.EfficiencyDao
import com.vearad.vearatick.DataBase.Employee
import com.vearad.vearatick.DataBase.EmployeeDao
import com.vearad.vearatick.R
import com.vearad.vearatick.databinding.ActivityProAndEmpBinding
import com.vearad.vearatick.databinding.FragmentEmployeeInfoUpdateBinding
import com.vearad.vearatick.employeeAdapter

private val PICK_IMAGE_REQUEST = 1

class EmployeeInfoUpdateFragment(
    val employee: Employee,
    val efficiencyEmployeeDao: EfficiencyDao,
    val position: Int,
    val employeeDao: EmployeeDao,
    val bindingActivityProAndEmpBinding: ActivityProAndEmpBinding,
) : Fragment() {

    lateinit var binding: FragmentEmployeeInfoUpdateBinding
    var imageUri: Uri? = null
    var imagePath: String? = null
    lateinit var imageBytes: ByteArray
    lateinit var newEmployee: Employee


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEmployeeInfoUpdateBinding.inflate(layoutInflater, null, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setdata(employee)
        onBackPressed()
        Handler(Looper.getMainLooper()).postDelayed({
            binding.loading2.visibility = GONE
            binding.layoutForm.visibility = VISIBLE
        }, 1000)

        val gender = listOf(
            "مرد",
            "زن"
        )

        val myAdapteredt = ArrayAdapter(requireContext(), R.layout.item_gender, gender)
        (binding.dialogMainEdtGdrperson.editText as AutoCompleteTextView).setAdapter(
            myAdapteredt
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

        val popupMenu = PopupMenu(this.context, binding.imgprn2)
        onPhotoClicked(popupMenu)

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
                    parentFragmentManager.beginTransaction().detach(this@EmployeeInfoUpdateFragment)
                        .replace(
                            R.id.layout_pro_and_emp,
                            EmployeeInformationFragment(
                                employee,
                                efficiencyEmployeeDao,
                                position,
                                employeeDao,
                                bindingActivityProAndEmpBinding,
                            )
                        ).commit()
                }
            })
    }

    fun onEmployeeInfoUpdate() {
        parentFragmentManager.beginTransaction().detach(this@EmployeeInfoUpdateFragment)
            .replace(
                R.id.layout_pro_and_emp,
                EmployeeInformationFragment(
                    employee,
                    efficiencyEmployeeDao,
                    position,
                    employeeDao,
                    bindingActivityProAndEmpBinding,
                )
            ).commit()
    }

    private fun setdata(employee: Employee) {
        binding.edtNameEpm.setText(employee.name)
        binding.edtFamEmp.setText(employee.family)
        binding.edtAgeEmp.setText(employee.age.toString())
        binding.edtAddressEmp.setText(employee.address)
        binding.edtGenEmp.setText(employee.gender)
        binding.edtRankEmp.setText(employee.rank)
        binding.edtNumEmp.setText(employee.cellularPhone.toString())
        binding.edtTakhasosEmp.setText(employee.specialty)
        binding.edtNumbhomeEmp.setText(employee.homePhone.toString())
        if (employee.imagePath != null) {
            imagePath = employee.imagePath
            Glide.with(this)
                .load(employee.imagePath)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.imgprn2)
            binding.imgprn2.setPadding(20, 20, 20, 20)
        } else
            if (employee.gender == "زن") {
                binding.imgprn2.setImageResource(R.drawable.img_matter)
            }
    }

    private fun onPhotoClicked(popupMenu: PopupMenu) {
        popupMenu.menuInflater.inflate(R.menu.menu_add_photo, popupMenu.menu)
        binding.imgprn2.setOnClickListener {
            popupMenu.show()
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_add_photo -> {
                        pickImage()
                    }

                    R.id.menu_delete_photo -> {
                        deletePhoto()
                    }
                }
                true
            }
        }
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
            imagePath = selectedImageUri.toString()
            imageUri = selectedImageUri
            Glide.with(this)
                .load(selectedImageUri)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.imgprn2)
            binding.imgprn2.setPadding(20, 20, 20, 20)
        }
    }
    private fun deletePhoto() {
        imageUri = null
        imagePath = null
        Glide.with(this)
            .load(R.drawable.img_add_photo)
            .into(binding.imgprn2)
        binding.imgprn2.setPadding(50, 50, 50, 50)

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

            if (txtNumberHome == "")
                txtNumberHome = "0"

            newEmployee = Employee(
                idEmployee = employee.idEmployee,
                name = txtname,
                family = txtFamily,
                age = txtAge.toInt(),
                gender = txtGender,
                cellularPhone = txtNumber.toLong(),
                homePhone = txtNumberHome.toLong(),
                address = txtAddress,
                specialty = txtSpecialty,
                skill = "",
                rank = txtRank,
                imagePath = null
            )

            if (imagePath != null) {

                newEmployee = Employee(
                    idEmployee = employee.idEmployee,
                    name = txtname,
                    family = txtFamily,
                    age = txtAge.toInt(),
                    gender = txtGender,
                    cellularPhone = txtNumber.toLong(),
                    homePhone = txtNumberHome.toLong(),
                    address = txtAddress,
                    specialty = txtSpecialty,
                    skill = "",
                    imagePath = imagePath.toString(),
                    rank = txtRank
                )
            }
            employeeAdapter.updateEmployee(newEmployee, position)
            employeeDao.update(newEmployee)
            onEmployeeInfoUpdate()

        } else {
            Toast.makeText(context, "لطفا همه مقادیر را وارد کنید", Toast.LENGTH_SHORT).show()
        }
    }
}


//    @Deprecated("Deprecated in Java")
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == AppCompatActivity.RESULT_OK && data != null) {
//            val selectedImageUri: Uri = data.data!!
//            val options = UCrop.Options()
//            options.setCircleDimmedLayer(true)
//            options.setShowCropGrid(false)
//            options.setCompressionQuality(100)
//            options.setToolbarTitle("Crop Image")
//            options.setStatusBarColor(
//                ContextCompat.getColor(
//                    requireActivity(),
//                    R.color.black_light
//                )
//            )
//            options.setToolbarColor(ContextCompat.getColor(requireActivity(), R.color.black_light))
//            options.setActiveControlsWidgetColor(
//                ContextCompat.getColor(
//                    requireActivity(),
//                    R.color.firoze
//                )
//            )
//            options.setToolbarWidgetColor(ContextCompat.getColor(requireActivity(), R.color.white))
//            options.setDimmedLayerColor(ContextCompat.getColor(requireActivity(), R.color.blacke))
//            options.setToolbarCropDrawable(R.drawable.ic_crop)
//            options.setFreeStyleCropEnabled(true)
//            val destinationUri = Uri.fromFile(File(requireActivity().cacheDir, "avatar"))
//            UCrop.of(selectedImageUri, destinationUri)
//                .withAspectRatio(1f, 1f)
//                .withOptions(options)
//                .start(requireActivity(), 2)
//        }
//        Log.v("imageUri", "data: ${data}")
//        imageUri = UCrop.getOutput(data!!)
//        Toast.makeText(context, "$imageUri", Toast.LENGTH_SHORT).show()
//        Glide.with(this)
//            .load(UCrop.getOutput(data))
//            .apply(RequestOptions.circleCropTransform())
//            .into(binding.imgprn2)
//    }