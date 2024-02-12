package com.vearad.vearatick.ui.fragmentssub

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.kizitonwose.calendarview.utils.persian.PersianCalendar
import com.vearad.vearatick.R
import com.vearad.vearatick.databinding.ActivityProAndEmpBinding
import com.vearad.vearatick.databinding.FragmentEmployeeRecruitmentBinding
import com.vearad.vearatick.model.db.EfficiencyDao
import com.vearad.vearatick.model.db.EfficiencyEmployee
import com.vearad.vearatick.model.db.Employee
import com.vearad.vearatick.ui.employeeAdapter
import com.vearad.vearatick.ui.employeeDao


private val PICK_IMAGE_REQUEST = 1
private val PICK_CONTACT_REQUEST = 2
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

        val popupMenu = PopupMenu(this.context, binding.imgprn2)
        onPhotoClicked(popupMenu)

        binding.btnNumberPhone.setOnClickListener {
            selectContact()
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
                        ?.attach(EmployeeFragment(
                            bindingActivityProAndEmpBinding,
                            false,
                            0
                        ))?.commit()
                }
            })
    }

    fun onRecruitment() {
        parentFragmentManager.beginTransaction().detach(this@EmployeeRecruitmentFragment)
            .replace(R.id.frame_layout_sub, EmployeeFragment(
                bindingActivityProAndEmpBinding,
                false,
                0
            ))
            .commit()
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

    private fun selectContact() {
        val activity = activity ?: return

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.READ_CONTACTS),
                PICK_CONTACT_REQUEST
            )
        } else {
            val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
            startActivityForResult(intent, PICK_CONTACT_REQUEST)
        }
    }

    @SuppressLint("Range")
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PICK_IMAGE_REQUEST -> {
                    val selectedImageUri: Uri = data!!.data!!
                    imagePath = selectedImageUri
                    imageUri = selectedImageUri
                    Glide.with(this)
                        .load(selectedImageUri)
                        .apply(RequestOptions.circleCropTransform())
                        .into(binding.imgprn2)
                    binding.imgprn2.setPadding(20, 20, 20, 20)
                }
                PICK_CONTACT_REQUEST -> {

                    val contactData: Uri = data!!.data!!
                    val cur: Cursor =
                        requireActivity().getContentResolver().query(contactData, null, null, null, null)!!
                    if (cur.count > 0) { // thats mean some resutl has been found
                        if (cur.moveToNext()) {
                            val id =
                                cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID))
                            val name =
                                cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                            Log.e("Names", name)
                            if (cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))
                                    .toInt() > 0
                            ) {
                                val phones: Cursor = requireActivity().getContentResolver().query(
                                    CommonDataKinds.Phone.CONTENT_URI,
                                    null,
                                    CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                                    null,
                                    null
                                )!!
                                while (phones.moveToNext()) {
                                    val phoneNumber = phones.getString(phones.getColumnIndex(CommonDataKinds.Phone.NUMBER))
                                    binding.edtNumEmp.setText(phoneNumber)
                                }
                                phones.close()
                            }
                        }
                    }
                    cur.close()

                }
            }
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
            val txtMaharat = ""
            val today = PersianCalendar()

            if (txtNumberHome == "")
                txtNumberHome = "0"

            newEmployee = Employee(
                name = txtname,
                family = txtFamily,
                age = txtAge.toInt(),
                gender = txtGender,
                cellularPhone = txtNumber,
                homePhone = txtNumberHome,
                address = txtAddress,
                specialty = txtSpecialty,
                skill = txtMaharat,
                rank = txtRank,
                imagePath = null,
                dayUse = today.persianDay,
                monthUse = today.persianMonth,
                yearUse = today.persianYear
            )

            if (imageUri != null) {

                newEmployee = Employee(
                    name = txtname,
                    family = txtFamily,
                    age = txtAge.toInt(),
                    gender = txtGender,
                    cellularPhone = txtNumber,
                    homePhone = txtNumberHome,
                    address = txtAddress,
                    specialty = txtSpecialty,
                    skill = txtMaharat,
                    imagePath = imagePath.toString(),
                    rank = txtRank,
                    dayUse = today.persianDay,
                    monthUse = today.persianMonth,
                    yearUse = today.persianYear
                )
            }

            employeeAdapter.addEmployee(newEmployee)
            employeeDao.insert(newEmployee)

            val employee = employeeDao.getObjectAllEmployee(txtname, txtFamily, txtNumber)

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
