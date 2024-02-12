package com.vearad.vearatick.fgmMain

import ApiService
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import com.google.android.material.snackbar.Snackbar
import com.vearad.vearatick.ACCESSTOKEN
import com.vearad.vearatick.BottomSheetCallback
import com.vearad.vearatick.DataBase.AppDatabase
import com.vearad.vearatick.DataBase.CompanyInfo
import com.vearad.vearatick.DataBase.CompanyInfoDao
import com.vearad.vearatick.dialog.CompanyInfoBottomsheetFragment
import com.vearad.vearatick.EXPIRATIONACCESSTOKEN
import com.vearad.vearatick.KEYACCESSTOKEN
import com.vearad.vearatick.KEYEXPIRATIONACCESSTOKEN
import com.vearad.vearatick.KEYUSER
import com.vearad.vearatick.LoginStep24Activity
import com.vearad.vearatick.MainActivity
import com.vearad.vearatick.R
import com.vearad.vearatick.USER
import com.vearad.vearatick.apiService
import com.vearad.vearatick.databinding.FragmentCompanyInformationBinding
import com.vearad.vearatick.fgmSub.CompanyEventFragment
import com.vearad.vearatick.fgmSub.CompanyResumeFragment
import com.vearad.vearatick.fgmSub.CompanySkillFragment
import com.vearad.vearatick.model.MiniSiteData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.net.MalformedURLException
import java.time.LocalDate

class CompanyInformationFragment : Fragment(), BottomSheetCallback {

    lateinit var binding: FragmentCompanyInformationBinding
    lateinit var companyInfoDao: CompanyInfoDao
    var companyInfo: CompanyInfo? = null
    private val PICK_IMAGE_REQUEST = 1
    var imageUri: Uri? = null
    var imagePath: String? = null

    var sharedPreferencesExpirationAccessToken: SharedPreferences? = null
    var sharedPreferencesUser: SharedPreferences? = null
    var sharedPreferencesAccessToken: SharedPreferences? = null
    var sharedPreferencesMiniSite: SharedPreferences? = null

    var expirationAccessToken = 0
    var user = ""
    var accessToken = ""
    var nameMiniSite = ""

    val SHAREDLOGINSTEP24 = "SharedLoginStep24"
    val LOGINSTEP24 = "loginStep24"
    var login = false

    val SHAREDMINISITE = "SharedMinisite"
    val KEYMINISITE = "keyMinisite"
    var idMiniSite = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCompanyInformationBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firstRun(view)
        setData(view)
        sharedPreferencesMiniSite =
            requireActivity().getSharedPreferences(SHAREDMINISITE, Context.MODE_PRIVATE)
        nameMiniSite =
            sharedPreferencesMiniSite?.getString(KEYMINISITE, "")!!

        sharedPreferencesExpirationAccessToken =
            requireActivity().getSharedPreferences(EXPIRATIONACCESSTOKEN, Context.MODE_PRIVATE)
        expirationAccessToken =
            sharedPreferencesExpirationAccessToken?.getInt(KEYEXPIRATIONACCESSTOKEN, 0)!!

        sharedPreferencesUser =
            requireActivity().getSharedPreferences(USER, Context.MODE_PRIVATE)
        user = sharedPreferencesUser?.getString(KEYUSER, "").toString()

        sharedPreferencesAccessToken =
            requireActivity().getSharedPreferences(ACCESSTOKEN, Context.MODE_PRIVATE)
        accessToken = sharedPreferencesAccessToken?.getString(KEYACCESSTOKEN, "").toString()

        getMiniSite(user, accessToken, apiService)

        val popupMenuInfoCompany = PopupMenu(this.context, binding.btnMenuCompany)
        onInfoCompanyClicked(popupMenuInfoCompany)

        val popupMenuPhoto = PopupMenu(this.context, binding.imgCom)
        onPhotoClicked(popupMenuPhoto)

        binding.btnCompanySkill.setOnClickListener {
            btnCompanySkill(view)
        }
        binding.btnCompanyResume.setOnClickListener {
            btnCompanyResume(view)
        }
        binding.btnEvent.setOnClickListener {
            btnEvent(view)
        }

    }

    private fun setData(view: View) {

        companyInfoDao = AppDatabase.getDataBase(view.context).companyInfoDao
        companyInfo = companyInfoDao.getCompanyInfoDao()

        if (companyInfo == null) {
            val bottomsheet = CompanyInfoBottomsheetFragment()
            bottomsheet.setStyle(
                R.style.BottomSheetStyle,
                R.style.BottomSheetDialogTheme
            )
            bottomsheet.setCallback(this)
            bottomsheet.show(parentFragmentManager, null)
            bottomsheet.setDismissable(false)

        } else {
            binding.nameCompany.text = companyInfo!!.nameCompany
            binding.locationCompany.text = companyInfo!!.addressCompany
            binding.numberCompany.text = companyInfo!!.phoneCompany
            binding.idGithaub.text = companyInfo!!.githubCompany
            binding.idLinkedin.text = companyInfo!!.linkedinCompany
            if (companyInfo!!.imagePath != null) {
                binding
                Glide.with(this)
                    .load(companyInfo!!.imagePath)
                    .apply(RequestOptions.circleCropTransform())
                    .into(binding.imgCom)
                binding.imgCom.setPadding(20, 20, 20, 20)
            } else
                binding.imgCom.setImageResource(R.drawable.img_add_photo)

        }
    }

    private fun onInfoCompanyClicked(popupMenu: PopupMenu) {
        popupMenu.menuInflater.inflate(
            R.menu.menu_edit_info_company_and_login_minisite,
            popupMenu.menu
        )
        binding.btnMenuCompany.setOnClickListener {
            popupMenu.show()

            var sharedPreferencesLoginStep24 =
                requireActivity().getSharedPreferences(SHAREDLOGINSTEP24, Context.MODE_PRIVATE)
            login = sharedPreferencesLoginStep24.getBoolean(LOGINSTEP24, false)

            /*val tapTargetSequence = tapTargetSequence(sharedPreferencesLoginStep24)
            if (login == false)
                tapTargetSequence?.start()*/

            val miniSite = popupMenu.menu.findItem(R.id.menu_login_minisite)
            if (nameMiniSite == "")
                miniSite.title = "ایجاد مینی سایت"
            else
                miniSite.title = "رفتن به مینی سایت"

            val login = popupMenu.menu.findItem(R.id.menu_logout_step24)
            if (accessToken == "")
                login.title = "ورود به step24"
            else
                login.title = "خروج از step24"


            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_edit_company -> {
                        //tapTargetSequence?.cancel()
                        val bottomsheet = CompanyInfoBottomsheetFragment()
                        bottomsheet.setStyle(
                            R.style.BottomSheetStyle,
                            R.style.BottomSheetDialogTheme
                        )
                        bottomsheet.setCallback(this)
                        bottomsheet.show(parentFragmentManager, null)
                        bottomsheet.setDismissable(true)
                    }

                    R.id.menu_login_minisite -> {

                        val today = LocalDate.now()
                        Log.v("loginapp", "today: ${today.dayOfMonth}")
                        Log.v("loginapp", "expirationAccessToken: ${expirationAccessToken}")
                        Log.v("loginapp", "user: ${user}")

                        if (expirationAccessToken <= today.dayOfMonth || user == "" || expirationAccessToken == 0) {
                            val intent = Intent(requireActivity(), LoginStep24Activity::class.java)
                            startActivity(intent)
                            requireActivity().overridePendingTransition(
                                R.anim.slide_from_left,
                                R.anim.slide_to_right
                            )
                        } else {
                            Log.v("nameMiniSite", "nameMiniSite: ${nameMiniSite}")
                            if (nameMiniSite != "" || nameMiniSite != null) {
                                Log.v("nameMiniSite", "nameMiniSite: ${nameMiniSite}")
                                goToMiniSite(user)
                            }
                        }

                    }

                    R.id.menu_logout_step24 -> {

                        if (accessToken == "") {
                            val goFromEvent = true
                            val intent = Intent(requireActivity(), LoginStep24Activity::class.java)
                            intent.putExtra("GOFROMEVENT", goFromEvent);
                            startActivity(intent)
                            requireActivity().overridePendingTransition(
                                R.anim.slide_from_left,
                                R.anim.slide_to_right
                            )
                        } else {
                            sharedPreferencesUser =
                                requireActivity().getSharedPreferences(USER, Context.MODE_PRIVATE)
                            sharedPreferencesAccessToken = requireActivity().getSharedPreferences(
                                ACCESSTOKEN,
                                Context.MODE_PRIVATE
                            )
                            val sharedPreferencesExpirationAccessToken =
                                requireActivity().getSharedPreferences(
                                    EXPIRATIONACCESSTOKEN,
                                    Context.MODE_PRIVATE
                                )

                            sharedPreferencesUser?.edit()?.putString(KEYUSER, "")?.apply()
                            sharedPreferencesAccessToken?.edit()?.putString(KEYACCESSTOKEN, "")
                                ?.apply()
                            sharedPreferencesExpirationAccessToken.edit()
                                .putInt(KEYEXPIRATIONACCESSTOKEN, 0).apply()

                            expirationAccessToken = 0
                            user = ""
                            accessToken = ""
                        }

                    }

                }
                true
            }
        }
    }

    private fun getMiniSite(user: String?, accessToken: String?, apiService: ApiService) {
        Log.v("loginapp", "user: ${user}")
        Log.v("loginapp", "accessToken: ${accessToken}")

        if (user != null && accessToken != null) {
            Log.v("loginapp", "Login")
            val call = apiService.getMiniSite(
                "admin/minisite/users/${user}/business",
                "Bearer $accessToken"
            )
            call.enqueue(object : Callback<MiniSiteData> {
                override fun onResponse(
                    call: Call<MiniSiteData>,
                    response: Response<MiniSiteData>
                ) {
                    Log.v("loginapp", "is onResponse")
                    Log.v("loginapp", "isSuccessful ${response.isSuccessful}")
                    Log.v("loginapp", "response ${response}")
                    if (response.isSuccessful) {
                        //companyEventData = response.body()!!.event

                        val miniSiteDataData: MiniSiteData? = response.body()
                        Log.v("loginapp", "miniSiteDataData: ${miniSiteDataData}")

                        if (miniSiteDataData?.business != null) {
                            // Process the events data as needed
                            idMiniSite = miniSiteDataData.business.namePer
                            binding.idMiniSite.text = idMiniSite

                            sharedPreferencesMiniSite?.edit()
                                ?.putString(KEYMINISITE, miniSiteDataData.business.namePer)!!
                                .apply()
                        } else {
                            // Handle the case where the response body is null
                            val snackbar = Snackbar.make(
                                binding.root,
                                "مینی سایت نداری!",
                                Snackbar.ANIMATION_MODE_SLIDE
                            )
                                .setAction("ساخت مینی سایت") {
                                    goToMiniSite(user)
                                }
                                .setBackgroundTint(Color.parseColor("#FFFFFF"))
                                .setTextColor(Color.parseColor("#000000"))
                                .setActionTextColor(Color.parseColor("#E600ADB5"))
                            val view = snackbar.view
                            val params = view.layoutParams as FrameLayout.LayoutParams
                            params.gravity = Gravity.TOP
                            view.layoutParams = params
                            snackbar.view.layoutDirection = View.LAYOUT_DIRECTION_RTL
                            snackbar.show()
                        }
                        // Process the data and update the UI
                    } else {
                        // Handle unsuccessful response
                        goToLogin()
                    }
                }

                override fun onFailure(call: Call<MiniSiteData>, t: Throwable) {
                    Log.e("RequestError", "Error: ${t.message}")
                    // Handle the error
                    goToLogin()
                }

            })
        } else
            goToLogin()

    }

    fun goToLogin() {
        val snackbar =
            Snackbar.make(binding.root, "نیاز به ورود به سایت است!", Snackbar.ANIMATION_MODE_SLIDE)
                .setAction("ورود") {
                    val goFromEvent = true
                    val intent = Intent(requireActivity(), LoginStep24Activity::class.java)
                    intent.putExtra("GOFROMEVENT", goFromEvent);
                    startActivity(intent)
                    requireActivity().overridePendingTransition(
                        R.anim.slide_from_left,
                        R.anim.slide_to_right
                    )
                }.setBackgroundTint(Color.parseColor("#FFFFFF"))
                .setTextColor(Color.parseColor("#000000"))
                .setActionTextColor(Color.parseColor("#E600ADB5"))
        val view = snackbar.view
        val params = view.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.TOP
        view.layoutParams = params
        snackbar.view.layoutDirection = View.LAYOUT_DIRECTION_RTL
        snackbar.show()
    }

    private fun goToMiniSite(user: String?) {

        var createEventUrl = "https://step24.ir/minisites"
        if (idMiniSite != "")
            createEventUrl = "https://step24.ir/${user}/admin/minisite-panel"

        try {
            val modifiedUrl = Uri.parse(createEventUrl)
                .buildUpon()
                .build()

            val intentMainActivity = Intent(requireContext(), MainActivity::class.java)
            startActivity(intentMainActivity)

            val intent = Intent(Intent.ACTION_VIEW, modifiedUrl)
            startActivity(intent)
        } catch (e: MalformedURLException) {
            // Handle URL exception
        } catch (e: IOException) {
            // Handle connection exception
        }
    }


    private fun tapTargetSequence(sharedPreferencesLoginStep24: SharedPreferences): TapTargetSequence? {
        val tapTargetSequence = TapTargetSequence(requireActivity())
            .targets(
                TapTarget.forView(
                    binding.btnMenuCompany,
                    "\n\nثبت نام در مینی سایت",
                    "شما تا کنون در step24 ثبت نام نکرده اید ابتدا ثبت نام نموده سپس مینی سایت خود را بسازید."
                )
                    .cancelable(true)
                    .textTypeface(Typeface.DEFAULT_BOLD)
                    .titleTextSize(20)
                    .descriptionTextColor(R.color.blacke)
                    .transparentTarget(true)
                    .targetCircleColor(R.color.firoze)
                    .titleTextColor(R.color.white)
                    .targetRadius(60)
                    .id(1)
            ).listener(object : TapTargetSequence.Listener {
                override fun onSequenceFinish() {
                    // دنباله Tap Target ها به پایان رسید
                }

                override fun onSequenceStep(
                    lastTarget: TapTarget?,
                    targetClicked: Boolean
                ) {
                    // مرحله جدید Tap Target در دنباله
                }

                override fun onSequenceCanceled(lastTarget: TapTarget?) {
                    // دنباله Tap Target ها لغو شد
                }
            })
        sharedPreferencesLoginStep24.edit().putBoolean(LOGINSTEP24, true).apply()

        return tapTargetSequence

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

            addPhoto()
        }
    }

    private fun addPhoto() {
        val newInfo = CompanyInfo(
            idCompanyInfo = companyInfo!!.idCompanyInfo,
            nameCompany = companyInfo!!.nameCompany,
            addressCompany = companyInfo!!.addressCompany,
            phoneCompany = companyInfo!!.phoneCompany,
            githubCompany = companyInfo!!.githubCompany,
            linkedinCompany = companyInfo!!.linkedinCompany,
            imagePath = imagePath
        )

        companyInfoDao.update(newInfo)
        onConfirmButtonClicked()
    }

    private fun deletePhoto() {
        val newInfo = CompanyInfo(
            idCompanyInfo = companyInfo!!.idCompanyInfo,
            nameCompany = companyInfo!!.nameCompany,
            addressCompany = companyInfo!!.addressCompany,
            phoneCompany = companyInfo!!.phoneCompany,
            githubCompany = companyInfo!!.githubCompany,
            linkedinCompany = companyInfo!!.linkedinCompany,
            imagePath = null
        )

        companyInfoDao.update(newInfo)
        onConfirmButtonClicked()
    }

    private fun btnCompanySkill(view: View) {

        binding.icCompanySkill.backgroundTintList =
            ContextCompat.getColorStateList(view.context, R.color.firoze)
        binding.txtCompanySkill.setTextColor(Color.parseColor("#E600ADB5"))
        binding.txtCompanySkill.textSize = 11f
        var layoutParamsCompanySkill = binding.btnCompanySkill.layoutParams
        layoutParamsCompanySkill.height = resources.getDimensionPixelSize(R.dimen.size_70)
        binding.btnCompanySkill.layoutParams = layoutParamsCompanySkill
        ViewCompat.setElevation(
            binding.btnCompanySkill,
            resources.getDimension(R.dimen.elevation_10)
        )
        layoutParamsCompanySkill = binding.icCompanySkill.layoutParams
        layoutParamsCompanySkill.width = resources.getDimensionPixelSize(R.dimen.size_40)
        layoutParamsCompanySkill.height = resources.getDimensionPixelSize(R.dimen.size_40)
        binding.icCompanySkill.layoutParams = layoutParamsCompanySkill

        binding.icCompanyResume.backgroundTintList =
            ContextCompat.getColorStateList(view.context, R.color.blacke)
        binding.txtCompanyResume.setTextColor(Color.parseColor("#FFFFFF"))
        binding.txtCompanyResume.textSize = 9f
        var layoutParamsCompanyResume = binding.btnCompanyResume.layoutParams
        layoutParamsCompanyResume.height = resources.getDimensionPixelSize(R.dimen.size_60)
        binding.btnCompanyResume.layoutParams = layoutParamsCompanyResume
        ViewCompat.setElevation(
            binding.btnCompanyResume,
            resources.getDimension(R.dimen.elevation_5)
        )
        layoutParamsCompanyResume = binding.icCompanyResume.layoutParams
        layoutParamsCompanyResume.width = resources.getDimensionPixelSize(R.dimen.size_35)
        layoutParamsCompanyResume.height = resources.getDimensionPixelSize(R.dimen.size_35)
        binding.icCompanyResume.layoutParams = layoutParamsCompanyResume

        binding.icEvent.backgroundTintList =
            ContextCompat.getColorStateList(view.context, R.color.blacke)
        binding.txtEvent.setTextColor(Color.parseColor("#FFFFFF"))
        binding.txtEvent.textSize = 9f
        var layoutParamsEmployeeResume = binding.btnEvent.layoutParams
        layoutParamsEmployeeResume.height = resources.getDimensionPixelSize(R.dimen.size_60)
        binding.btnEvent.layoutParams = layoutParamsEmployeeResume
        ViewCompat.setElevation(
            binding.btnEvent,
            resources.getDimension(R.dimen.elevation_5)
        )
        layoutParamsEmployeeResume = binding.icEvent.layoutParams
        layoutParamsEmployeeResume.width = resources.getDimensionPixelSize(R.dimen.size_35)
        layoutParamsEmployeeResume.height = resources.getDimensionPixelSize(R.dimen.size_35)
        binding.icEvent.layoutParams = layoutParamsEmployeeResume

        replaceFragment(CompanySkillFragment())
    }

    private fun btnCompanyResume(view: View) {
        binding.icCompanyResume.backgroundTintList =
            ContextCompat.getColorStateList(view.context, R.color.firoze)
        binding.txtCompanyResume.setTextColor(Color.parseColor("#E600ADB5"))
        binding.txtCompanyResume.textSize = 11f
        var layoutParamsCompanyResume = binding.btnCompanyResume.layoutParams
        layoutParamsCompanyResume.height = resources.getDimensionPixelSize(R.dimen.size_70)
        binding.btnCompanyResume.layoutParams = layoutParamsCompanyResume
        ViewCompat.setElevation(
            binding.btnCompanyResume,
            resources.getDimension(R.dimen.elevation_10)
        )
        layoutParamsCompanyResume = binding.icCompanyResume.layoutParams
        layoutParamsCompanyResume.width = resources.getDimensionPixelSize(R.dimen.size_40)
        layoutParamsCompanyResume.height = resources.getDimensionPixelSize(R.dimen.size_40)
        binding.icCompanyResume.layoutParams = layoutParamsCompanyResume

        binding.icCompanySkill.backgroundTintList =
            ContextCompat.getColorStateList(view.context, R.color.blacke)
        binding.txtCompanySkill.setTextColor(Color.parseColor("#FFFFFF"))
        binding.txtCompanySkill.textSize = 9f
        var layoutParamsCompanySkill = binding.btnCompanySkill.layoutParams
        layoutParamsCompanySkill.height = resources.getDimensionPixelSize(R.dimen.size_60)
        binding.btnCompanySkill.layoutParams = layoutParamsCompanySkill
        ViewCompat.setElevation(
            binding.btnCompanySkill,
            resources.getDimension(R.dimen.elevation_5)
        )
        layoutParamsCompanySkill = binding.icCompanySkill.layoutParams
        layoutParamsCompanySkill.width = resources.getDimensionPixelSize(R.dimen.size_35)
        layoutParamsCompanySkill.height = resources.getDimensionPixelSize(R.dimen.size_35)
        binding.icCompanySkill.layoutParams = layoutParamsCompanySkill

        binding.icEvent.backgroundTintList =
            ContextCompat.getColorStateList(view.context, R.color.blacke)
        binding.txtEvent.setTextColor(Color.parseColor("#FFFFFF"))
        binding.txtEvent.textSize = 9f
        var layoutParamsEmployeeResume = binding.btnEvent.layoutParams
        layoutParamsEmployeeResume.height = resources.getDimensionPixelSize(R.dimen.size_60)
        binding.btnEvent.layoutParams = layoutParamsEmployeeResume
        ViewCompat.setElevation(
            binding.btnEvent,
            resources.getDimension(R.dimen.elevation_5)
        )
        layoutParamsEmployeeResume = binding.icEvent.layoutParams
        layoutParamsEmployeeResume.width = resources.getDimensionPixelSize(R.dimen.size_35)
        layoutParamsEmployeeResume.height = resources.getDimensionPixelSize(R.dimen.size_35)
        binding.icEvent.layoutParams = layoutParamsEmployeeResume

        replaceFragment(CompanyResumeFragment())
    }

    private fun btnEvent(view: View) {
        binding.icEvent.backgroundTintList =
            ContextCompat.getColorStateList(view.context, R.color.firoze)
        binding.txtEvent.setTextColor(Color.parseColor("#E600ADB5"))
        binding.txtEvent.textSize = 11f
        var layoutParamsEmployeeResume = binding.btnEvent.layoutParams
        layoutParamsEmployeeResume.height = resources.getDimensionPixelSize(R.dimen.size_70)
        binding.btnEvent.layoutParams = layoutParamsEmployeeResume
        ViewCompat.setElevation(
            binding.btnEvent,
            resources.getDimension(R.dimen.elevation_10)
        )
        layoutParamsEmployeeResume = binding.icEvent.layoutParams
        layoutParamsEmployeeResume.width = resources.getDimensionPixelSize(R.dimen.size_40)
        layoutParamsEmployeeResume.height = resources.getDimensionPixelSize(R.dimen.size_40)
        binding.icEvent.layoutParams = layoutParamsEmployeeResume

        binding.icCompanySkill.backgroundTintList =
            ContextCompat.getColorStateList(view.context, R.color.blacke)
        binding.txtCompanySkill.setTextColor(Color.parseColor("#FFFFFF"))
        binding.txtCompanySkill.textSize = 9f
        var layoutParamsCompanySkill = binding.btnCompanySkill.layoutParams
        layoutParamsCompanySkill.height = resources.getDimensionPixelSize(R.dimen.size_60)
        binding.btnCompanySkill.layoutParams = layoutParamsCompanySkill
        ViewCompat.setElevation(
            binding.btnCompanySkill,
            resources.getDimension(R.dimen.elevation_5)
        )
        layoutParamsCompanySkill = binding.icCompanySkill.layoutParams
        layoutParamsCompanySkill.width = resources.getDimensionPixelSize(R.dimen.size_35)
        layoutParamsCompanySkill.height = resources.getDimensionPixelSize(R.dimen.size_35)
        binding.icCompanySkill.layoutParams = layoutParamsCompanySkill

        binding.icCompanyResume.backgroundTintList =
            ContextCompat.getColorStateList(view.context, R.color.blacke)
        binding.txtCompanyResume.setTextColor(Color.parseColor("#FFFFFF"))
        binding.txtCompanyResume.textSize = 9f
        var layoutParamsCompanyResume = binding.btnCompanyResume.layoutParams
        layoutParamsCompanyResume.height = resources.getDimensionPixelSize(R.dimen.size_60)
        binding.btnCompanyResume.layoutParams = layoutParamsCompanyResume
        ViewCompat.setElevation(
            binding.btnCompanyResume,
            resources.getDimension(R.dimen.elevation_5)
        )
        layoutParamsCompanyResume = binding.icCompanyResume.layoutParams
        layoutParamsCompanyResume.width = resources.getDimensionPixelSize(R.dimen.size_35)
        layoutParamsCompanyResume.height = resources.getDimensionPixelSize(R.dimen.size_35)
        binding.icCompanyResume.layoutParams = layoutParamsCompanyResume

        replaceFragment(CompanyEventFragment())
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = (activity as MainActivity).supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_com_info, fragment)
            .commit()
    }

    private fun firstRun(view: View) {
        btnCompanySkill(view)
        replaceFragment(CompanySkillFragment())
    }

    override fun onConfirmButtonClicked() {

        companyInfoDao = AppDatabase.getDataBase(binding.root.context).companyInfoDao
        companyInfo = companyInfoDao.getCompanyInfoDao()

        binding.nameCompany.text = companyInfo!!.nameCompany
        binding.locationCompany.text = companyInfo!!.addressCompany
        binding.numberCompany.text = companyInfo!!.phoneCompany
        binding.idGithaub.text = companyInfo!!.githubCompany
        binding.idLinkedin.text = companyInfo!!.linkedinCompany

        if (companyInfo!!.imagePath != null) {
            binding
            Glide.with(this)
                .load(companyInfo!!.imagePath)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.imgCom)
            binding.imgCom.setPadding(20, 20, 20, 20)
        } else {
            binding.imgCom.setImageResource(R.drawable.img_add_photo)
            binding.imgCom.setPadding(40, 40, 40, 40)
        }
    }

}