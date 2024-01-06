package com.vearad.vearatick

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.vearad.vearatick.adapter.CompanyEventAdapter
import com.vearad.vearatick.databinding.ActivityLoginStep24Binding
import com.vearad.vearatick.model.Events
import com.vearad.vearatick.model.LoginData
import com.vearad.vearatick.model.LoginResponse
import com.vearad.vearatick.model.UserResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.net.MalformedURLException
import java.time.LocalDate

class LoginStep24Activity : AppCompatActivity() {

    lateinit var binding: ActivityLoginStep24Binding
    var emailError: String? = null
    private var snackbar: Snackbar? = null
    var goFromEvent: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginStep24Binding.inflate(layoutInflater)
        setContentView(binding.root)
        goFromEvent = intent.getBooleanExtra("GOFROMEVENT", false)

        binding.btnBck.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)

        }

        binding.btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterStep24Activity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
        }

        binding.btnBck.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
        }

        /*binding.tilPassword.editText?.addTextChangedListener {
            if (it!!.length < 8) {
                binding.tilPassword.error = "رمز عبور باید حداقل 8 حرف باشد."
            } else
                binding.tilPassword.error = null

            snackbar?.dismiss()
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

            snackbar?.dismiss()

        }*/


        binding.btnDone.setOnClickListener {
            login()
        }

    }
    fun login() {
        if (
            binding.edtEmail.length() > 0 &&
            binding.edtPassword.length() > 0
        ) {
            val txtEmail = binding.edtEmail.text.toString()
            val txtPassword = binding.edtPassword.text.toString()

            binding.loading.visibility = VISIBLE
            loginApi(
                txtEmail,
                txtPassword,
            )

        } else
            Toast.makeText(applicationContext, "لطفا همه مقادیر را وارد کنید", Toast.LENGTH_SHORT)
                .show()
    }
    fun loginApi(
        email: String,
        password: String,
    ) {

        // 6. ساختن شیء SignupRequest با اطلاعات ورودی
        val loginData = LoginData(
            email,
            password,
        )
        //  ارسال درخواست ثبت نام با استفاده از شیء ApiService
        val call = apiService.loginUser(loginData)
        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                Log.v("loginapp", "______________________________________")
                Log.v("loginapp", "code: ${response.code()}")
                Log.v("loginapp", "isSuccessful: ${response.isSuccessful}")
                Log.v("loginapp", "______________________________________")

                if (response.isSuccessful) {
                    val accessToken = response.body()?.data?.access_token
                    val refreshToken = response.body()?.data?.refresh_token

                    Log.v("loginapp", "______________________________________")
                    Log.v("loginapp", "accessToken: ${accessToken}")
                    Log.v("loginapp", "refreshToken: ${refreshToken}")
                    Log.v("loginapp", "______________________________________")

                    getUser(response.body())

                } else if (response.code() == 422) {

                    val errorBody = response.errorBody()?.string()
                    val gson = Gson()
                    val registerResponse = gson.fromJson(errorBody, LoginResponse::class.java)

                    val errors = registerResponse?.errors
                    val authFailed = registerResponse?.authFailed

                    Log.v("loginapp", "______________________________________")
                    Log.v("loginapp", "success: ${response.body()?.success}")
                    Log.v("loginapp", "errorBody: ${response.errorBody().toString()}")
                    Log.v("loginapp", "registerResponse: ${registerResponse.toString()}")
                    Log.v("loginapp", "registerResponse.success: ${registerResponse.success}")
                    Log.v("loginapp", "errors: ${errors.toString()}")
                    Log.v("loginapp", "authFailed: ${authFailed.toString()}")
                    Log.v("loginapp", "______________________________________")

                    if (errors != null || authFailed != null) {
                        // Access the error messages

                        //val nameErrors = errors.name
                        //val usernameErrors = errors.username
                        val emailErrors = errors?.email
                        val passwordErrors = errors?.password
                        val authFailedErrors = authFailed?.message

                        Log.v("loginapp", "______________________________________")
                        //Log.v("loginapp", "nameErrors: ${nameErrors}")
                        // Log.v("loginapp", "usernameErrors: ${usernameErrors}")
                        Log.v("loginapp", "emailErrors: ${emailErrors}")
                        Log.v("loginapp", "passwordErrors: ${passwordErrors}")
                        Log.v("loginapp", "authFailedErrors: ${authFailedErrors}")
                        Log.v("loginapp", "______________________________________")

                        setError(
                            emailErrors,
                            email,
                            passwordErrors,
                            authFailedErrors,
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

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                // خطا در ارتباط با سرور
                Log.v("loginapp", "______________________________________")
                Log.e("loginapp", "brar: ${t.message}")
                Log.e("loginapp", "t: ${t}")
                Log.v("loginapp", "______________________________________")
                Toast.makeText(
                    applicationContext,
                    "اتصال اینترنت خود را بررسی کنید.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun getUser(loginResponse: LoginResponse?) {

        val accessToken = loginResponse?.data?.access_token
        val expires = loginResponse?.data?.expires_in

        val call = apiService.getUser("user/", "Bearer ${accessToken}")
        call.enqueue(object : Callback<UserResponse>, CompanyEventAdapter.CompanyEventEvent {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    //companyEventData = response.body()!!.event
                    Log.v("loginapp", "response: ${response}")
                    Log.v("loginapp", "call: ${call}")

                    val userData: UserResponse? = response.body()

                    if (userData != null) {
                        // Process the events data as needed
                        Log.v("loginapp", "username: ${userData?.user?.username}")
                        val user = userData?.user?.username

                        setSharedPreferences(user,accessToken,expires)

                        if (goFromEvent) {
                            val intent = Intent(applicationContext, MainActivity::class.java)
                            startActivity(intent)
                            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
                        } else {
                            goFromEvent = false
                            goToMiniSite(user)
                        }

                    } else {
                        // Handle the case where the response body is null
                    }
                    // Process the data and update the UI
                } else {
                    // Handle unsuccessful response
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Log.e("RequestError", "Error: ${t.message}")
                // Handle the error
            }

            override fun onEventClicked(companyEvent: Events.Event, position: Int) {
                TODO("Not yet implemented")
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

    private fun goToMiniSite(user: String?) {
        Log.v("loginapp", "Here: ${user}")

       /* var createEventUrl = "http://192.168.1.105:8081/login"
        if (user != null) {
            createEventUrl =
                "http://192.168.1.105:8081/${user}/admin/minisite-panel"
        }*/

        var createEventUrl = "https://step24.ir/login"
        if (user != null) {
            createEventUrl =
                "https://step24.ir/${user}/admin/minisite-panel"
        }

        try {
            val modifiedUrl = Uri.parse(createEventUrl)
                .buildUpon()
                .appendQueryParameter("appOrigin", "android")
                .build()

            val intentMainActivity = Intent(applicationContext, MainActivity::class.java)
            startActivity(intentMainActivity)

            val intent = Intent(Intent.ACTION_VIEW, modifiedUrl)
            startActivity(intent)
        } catch (e: MalformedURLException) {
            // Handle URL exception
        } catch (e: IOException) {
            // Handle connection exception
        }
    }

    private fun setError(
        emailErrors: List<String>?,
        email: String,
        passwordErrors: List<String>?,
        authFailedErrors: String?
    ) {
        binding.loading.visibility = GONE

        if (emailErrors != null) {
            emailError = email
            binding.tilEmail.error =
                emailErrors.toString().replace("[", "").replace("]", "") + "."
        } else {
            binding.tilEmail.error = null
        }

        if (authFailedErrors != null) {
            val snackbar = Snackbar.make(binding.root, "$authFailedErrors", Snackbar.LENGTH_LONG).setBackgroundTint(Color.parseColor("#FFFFFF"))
                .setTextColor(Color.parseColor("#000000"))
                .setActionTextColor(Color.parseColor("#E600ADB5"))
            snackbar.show()
        }

        if (passwordErrors != null) {
            binding.tilPassword.error =
                passwordErrors.toString().replace("[", "").replace("]", "")
        } else {
            binding.tilPassword.error = null
        }

    }


    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
    }
}