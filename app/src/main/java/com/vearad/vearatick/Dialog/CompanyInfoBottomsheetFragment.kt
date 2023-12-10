package com.vearad.vearatick.Dialog

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.vearad.vearatick.BottomSheetCallback
import com.vearad.vearatick.DataBase.AppDatabase
import com.vearad.vearatick.DataBase.CompanyInfo
import com.vearad.vearatick.DataBase.CompanyInfoDao
import com.vearad.vearatick.MainActivity
import com.vearad.vearatick.R
import com.vearad.vearatick.databinding.BottomsheetfragmentCompanyInfoBinding

class CompanyInfoBottomsheetFragment : BottomSheetDialogFragment() {
    private var isDismissable: Boolean = true

    lateinit var binding: BottomsheetfragmentCompanyInfoBinding
    lateinit var companyInfoDao: CompanyInfoDao
    var companyInfo: CompanyInfo? = null
    private var callback: BottomSheetCallback? = null
    private val PICK_IMAGE_REQUEST = 1
    var imageUri: Uri? = null
    var imagePath: String? = null
    lateinit var newInfo: CompanyInfo
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            BottomsheetfragmentCompanyInfoBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        companyInfoDao = AppDatabase.getDataBase(view.context).companyInfoDao
        companyInfo = companyInfoDao.getCompanyInfoDao()

        if (companyInfo != null)
            setdata()

        val popupMenu = PopupMenu(this.context, binding.imgCom)
        onPhotoClicked(popupMenu)

        binding.sheetBtnDone.setOnClickListener {
            if (companyInfo == null)
                addNewInfo()
            else {
                updateInfo()
            }

        }
    }
    override fun onStart() {
        super.onStart()
        dialog?.setCancelable(isDismissable)
        dialog?.setCanceledOnTouchOutside(isDismissable)
        //dialog?.window?.setDimAmount(if (isDismissable) 0.6f else 0f)
    }
    fun setDismissable(dismissable: Boolean) {
        isDismissable = dismissable
    }
    private fun setdata() {
        binding.edtNameCom.setText(companyInfo!!.nameCompany)
        binding.edtAddressCom.setText(companyInfo!!.addressCompany)
        binding.edtNumCom.setText(companyInfo!!.phoneCompany)
        binding.edtGithubCom.setText(companyInfo!!.githubCompany)
        binding.edtLinkdinCom.setText(companyInfo!!.linkedinCompany)
        if (companyInfo!!.imagePath != null) {
            imagePath = companyInfo!!.imagePath
            Glide.with(this)
                .load(companyInfo!!.imagePath)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.imgCom)
            binding.imgCom.setPadding(20, 20, 20, 20)
        }
    }
    private fun onPhotoClicked(popupMenu: PopupMenu) {
        popupMenu.menuInflater.inflate(R.menu.menu_add_photo, popupMenu.menu)
        binding.imgCom.setOnClickListener {
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
                .into(binding.imgCom)
            binding.imgCom.setPadding(20, 20, 20, 20)
        }
    }
    private fun deletePhoto() {
        imageUri = null
        imagePath = null
        Glide.with(this)
            .load(R.drawable.img_add_photo)
            .into(binding.imgCom)
        binding.imgCom.setPadding(50, 50, 50, 50)

    }


    private fun updateInfo() {
        if (
            binding.edtNameCom.length() > 0
        ) {
            val txtName = binding.edtNameCom.text.toString()
            val txtAddress = binding.edtAddressCom.text.toString()
            val txtNumber = binding.edtNumCom.text.toString()
            val txtGithub = binding.edtGithubCom.text.toString()
            val txtLinkedin = binding.edtLinkdinCom.text.toString()

            newInfo = CompanyInfo(
                idCompanyInfo = companyInfo!!.idCompanyInfo,
                nameCompany = txtName,
                addressCompany = txtAddress,
                phoneCompany = txtNumber,
                githubCompany = txtGithub,
                linkedinCompany = txtLinkedin,
                imagePath = imagePath
            )

            if (imageUri != null) {

                newInfo = CompanyInfo(
                    idCompanyInfo = companyInfo!!.idCompanyInfo,
                    nameCompany = txtName,
                    addressCompany = txtAddress,
                    phoneCompany = txtNumber,
                    githubCompany = txtGithub,
                    linkedinCompany = txtLinkedin,
                    imagePath = imagePath
                )

            }
            companyInfoDao.update(newInfo)
            onNewCompany()
            dismiss()
        } else {
            Toast.makeText(context, "لطفا همه مقادیر را وارد کنید", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addNewInfo() {
        if (
            binding.edtNameCom.length() > 0
        ) {
            val txtName = binding.edtNameCom.text.toString()
            val txtAddress = binding.edtAddressCom.text.toString()
            val txtNumber = binding.edtNumCom.text.toString()
            val txtGithub = binding.edtGithubCom.text.toString()
            val txtLinkedin = binding.edtLinkdinCom.text.toString()

            newInfo = CompanyInfo(
                nameCompany = txtName,
                addressCompany = txtAddress,
                phoneCompany = txtNumber,
                githubCompany = txtGithub,
                linkedinCompany = txtLinkedin,
                imagePath = null
            )

            if (imageUri != null) {

                newInfo = CompanyInfo(
                    nameCompany = txtName,
                    addressCompany = txtAddress,
                    phoneCompany = txtNumber,
                    githubCompany = txtGithub,
                    linkedinCompany = txtLinkedin,
                    imagePath = imagePath.toString()
                )

            }

            companyInfoDao.insert(newInfo)
            onNewCompany()
            dismiss()
        } else {
            Snackbar.make(binding.root, "لطفا همه مقادیر را وارد کنید!", Snackbar.LENGTH_INDEFINITE)
                .setAction("صرف نظر") {
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)
                    activity?.overridePendingTransition(
                        R.anim.slide_from_right,
                        R.anim.slide_to_left
                    )
                }
                .show()
        }
    }

    fun setCallback(callback: BottomSheetCallback) {
        this.callback = callback
    }

    fun onNewCompany() {
        callback?.onConfirmButtonClicked()
    }

}