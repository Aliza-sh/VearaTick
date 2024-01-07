package com.vearad.vearatick

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.vearad.vearatick.databinding.ActivityRegisterStep24Binding
import com.vearad.vearatick.model.RegisterData
import com.vearad.vearatick.model.RegisterResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate


class RegisterStep24Activity : AppCompatActivity() {

    lateinit var binding: ActivityRegisterStep24Binding
    private var userError: String? = null
    private var emailError: String? = null
    private var phoneError: String? = null
    private var firstRun: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterStep24Binding.inflate(layoutInflater)
        setContentView(binding.root)
        firstRun = intent.getBooleanExtra("FIRSTRUN", false)
        Log.v("firstRun", "RegisterStep24Activity: ${firstRun}")

        binding.btnBck.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)

        }

        binding.btnLogin.setOnClickListener {
            val intent = Intent(this, LoginStep24Activity::class.java)
            intent.putExtra("FIRSTRUN", firstRun)
            Log.v("firstRun", "RegisterStep24Activity: ${firstRun}")
            startActivity(intent)
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
        }

        /*binding.tilUser.editText?.addTextChangedListener {
            if (it.toString() == userError) {
                binding.tilUser.error = "این نام کاربری در دسترس نیست."
            } else
                binding.tilUser.error = null
        }

        binding.tilPassword.editText?.addTextChangedListener {
            if (it!!.length < 8) {
                binding.tilPassword.error = "رمز عبور باید حداقل 8 حرف باشد."
            } else
                binding.tilPassword.error = null
        }

        binding.tilNumberPhone.editText?.addTextChangedListener {
            val phoneNumber = it.toString().trim()
            val phoneNumberRegex = Regex("^09\\d{9}$")

            if (!phoneNumber.matches(phoneNumberRegex)) {
                binding.tilNumberPhone.error = "فرمت شماره معتبر نیست."
            } else if (it.toString() == phoneError)
                binding.tilNumberPhone.error = "حساب کاربری با این شماره از قبل ثبت شده است."
            else {
                binding.tilNumberPhone.error = null
            }
        }

        binding.tilEmail.editText?.addTextChangedListener {
            val email = it.toString().trim()
            val emailRegex = Regex("^\\w+([.-]?\\w+)*@\\w+([.-]?\\w+)*(\\.\\w{2,3})+$")

            if (!email.matches(emailRegex)) {
                binding.tilEmail.error = "فرمت ایمیل معتبر نیست."
            } else if (it.toString() == emailError)
                binding.tilEmail.error = "حساب کاربری با این ایمیل از قبل ثبت شده است."
            else {
                binding.tilEmail.error = null
            }
        }*/


        binding.btnDone.setOnClickListener {
            register()
        }
    }

    fun register() {
        if (
            binding.edtNameAndFamily.length() > 0 &&
            binding.edtUser.length() > 0 &&
            binding.edtEmail.length() > 0 &&
            binding.edtNumberPhone.length() > 0 &&
            binding.edtPassword.length() > 0 &&
            binding.edtPasswordConfirmation.length() > 0
        ) {
            val txtNameAndFamily = binding.edtNameAndFamily.text.toString()
            val txtUser = binding.edtUser.text.toString()
            val txtEmail = binding.edtEmail.text.toString()
            val txtNumberPhone = binding.edtNumberPhone.text.toString()
            val txtPassword = binding.edtPassword.text.toString()
            val txtPasswordConfirmation = binding.edtPasswordConfirmation.text.toString()

            binding.loading.visibility = VISIBLE
            registerApi(
                txtNameAndFamily,
                txtUser,
                txtEmail,
                txtNumberPhone,
                txtPassword,
                txtPasswordConfirmation
            )

        } else
            Toast.makeText(applicationContext, "لطفا همه مقادیر را وارد کنید", Toast.LENGTH_SHORT)
                .show()
    }

    fun registerApi(
        nameAndFamily: String,
        user: String,
        email: String,
        numberPhone: String,
        password: String,
        passwordConfirmation: String
    ) {
        // 6. ساختن شیء SignupRequest با اطلاعات ورودی
        val registrationData = RegisterData(
            nameAndFamily,
            user,
            email,
            numberPhone,
            password,
            passwordConfirmation
        )
        // 7. ارسال درخواست ثبت نام با استفاده از شیء ApiService
        val call = apiService.registerUser(registrationData)
        call.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                Log.v("loginapp", "______________________________________")
                Log.v("loginapp", "code: ${response.code()}")
                Log.v("loginapp", "isSuccessful: ${response.isSuccessful}")
                Log.v("loginapp", "______________________________________")

                if (response.isSuccessful) {
                    val accessToken = response.body()?.data?.access_token
                    val refreshToken = response.body()?.data?.refresh_token
                    val username = response.body()?.user?.username
                    val userId = response.body()?.user?.id
                    val expires = response.body()?.data?.expires_in

                    val intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)

                    Log.v("loginapp", "______________________________________")
                    Log.v("loginapp", "accessToken: ${accessToken}")
                    Log.v("loginapp", "refreshToken: ${refreshToken}")
                    Log.v("loginapp", "username: ${username}")
                    Log.v("loginapp", "userId: ${userId}")
                    Log.v("loginapp", "______________________________________")
                    setSharedPreferences(username,accessToken,expires)
                } else if (response.code() == 422) {

                    val errorBody = response.errorBody()?.string()
                    val gson = Gson()
                    val registerResponse = gson.fromJson(errorBody, RegisterResponse::class.java)

                    val errors = registerResponse?.errors

                    Log.v("loginapp", "______________________________________")
                    Log.v("loginapp", "success: ${response.body()?.success}")
                    Log.v("loginapp", "errorBody: ${response.errorBody().toString()}")
                    Log.v("loginapp", "registerResponse: ${registerResponse.toString()}")
                    Log.v("loginapp", "registerResponse.success: ${registerResponse.success}")
                    Log.v("loginapp", "errors: ${errors.toString()}")
                    Log.v("loginapp", "______________________________________")

                    if (errors != null) {
                        // Access the error messages

                        val nameErrors = errors.name
                        val usernameErrors = errors.username
                        val emailErrors = errors.email
                        val passwordErrors = errors.password
                        val phoneErrors = errors.phone

                        Log.v("loginapp", "______________________________________")
                        Log.v("loginapp", "nameErrors: ${nameErrors}")
                        Log.v("loginapp", "usernameErrors: ${usernameErrors}")
                        Log.v("loginapp", "emailErrors: ${emailErrors}")
                        Log.v("loginapp", "passwordErrors: ${passwordErrors}")
                        Log.v("loginapp", "phoneErrors: ${phoneErrors}")
                        Log.v("loginapp", "______________________________________")

                        setError(
                            nameErrors,
                            usernameErrors,
                            user,
                            emailErrors,
                            email,
                            passwordErrors,
                            phoneErrors,
                            numberPhone
                        )

                        // Log or handle the error messages as needed
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.v("loginapp", "______________________________________")
                    Log.v("loginapp", "errorBody: ${errorBody}")
                    // Handle other error cases
                    Log.v("loginapp", "response.message(): ${response.message()}")
                    Log.v("loginapp", "response.code(): ${response.code()}")
                    Log.v("loginapp", "______________________________________")
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                // خطا در ارتباط با سرور
                Log.v("loginapp", "______________________________________")
                Log.e("loginapp", "brar: ${t.message}")
                Log.e("loginapp", "t: ${t}")
                Log.v("loginapp", "______________________________________")
                Toast.makeText(applicationContext, "اتصال اینترنت خود را بررسی کنید.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setSharedPreferences(user: String?, accessToken: String?, expires: Int?) {
        val sharedPreferencesUser = getSharedPreferences(USER, Context.MODE_PRIVATE)
        sharedPreferencesUser.edit().putString(KEYUSER, user).apply()

        val sharedPreferencesAccessToken = getSharedPreferences(ACCESSTOKEN, Context.MODE_PRIVATE)
        sharedPreferencesAccessToken.edit().putString(KEYACCESSTOKEN, accessToken).apply()

        val sharedPreferencesFirstRun = getSharedPreferences(FIRSTRUN, Context.MODE_PRIVATE)
        sharedPreferencesFirstRun.edit().putBoolean(KEYUFIRSTRUN, true).apply()

        val expire = expires?.div((3600*24))
        Log.v("loginapp", "expires: ${expires}")
        Log.v("loginapp", "expire: ${expire}")

        // دریافت تاریخ کنونی
        val today = LocalDate.now()
        // اضافه کردن چهار روز به تاریخ کنونی
        val futureDate = today.plusDays(expire!!.toLong())
        Log.v("loginapp", "today: ${today}")
        Log.v("loginapp", "futureDate: ${futureDate}")

        val sharedPreferencesExpirationAccessToken = getSharedPreferences(EXPIRATIONACCESSTOKEN, Context.MODE_PRIVATE)
        sharedPreferencesExpirationAccessToken.edit().putInt(KEYEXPIRATIONACCESSTOKEN,
            futureDate.dayOfMonth
        ).apply()
    }

    private fun setError(
        nameErrors: List<String>?,
        usernameErrors: List<String>?,
        user: String,
        emailErrors: List<String>?,
        email: String,
        passwordErrors: List<String>?,
        phoneErrors: List<String>?,
        numberPhone: String
    ) {

        binding.loading.visibility = GONE

        if (nameErrors != null) {
            binding.tilNameAndFamily.error =
                nameErrors.toString().replace("[", "").replace("]", "") + "."
        } else {
            binding.tilNameAndFamily.error = null
        }

        if (usernameErrors != null) {
            userError = user
            binding.tilUser.error =
                usernameErrors.toString().replace("[", "").replace("]", "") + "."
        } else {
            binding.tilUser.error = null
        }

        if (emailErrors != null) {
            emailError = email
            binding.tilEmail.error =
                emailErrors.toString().replace("[", "").replace("]", "") + "."
        } else {
            binding.tilEmail.error = null
        }

        if (passwordErrors != null) {
            binding.tilPassword.error =
                passwordErrors.toString().replace("[", "").replace("]", "")
        } else {
            binding.tilPassword.error = null
        }

        if (phoneErrors != null) {
            phoneError = numberPhone
            binding.tilNumberPhone.error =
                phoneErrors.toString().replace("[", "").replace("]", "") + "."
        } else {
            binding.tilNumberPhone.error = null
        }
    }


    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
    }

}