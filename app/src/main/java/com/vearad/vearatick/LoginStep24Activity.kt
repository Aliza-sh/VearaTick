package com.vearad.vearatick

import ApiService
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.vearad.vearatick.databinding.ActivityLoginStep24Binding
import com.vearad.vearatick.model.LoginData
import com.vearad.vearatick.model.LoginResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginStep24Activity : AppCompatActivity() {

    lateinit var binding:ActivityLoginStep24Binding
    var emailError: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginStep24Binding.inflate(layoutInflater)
        setContentView(binding.root)

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

        binding.edtEmail.setText("nkkjhin@hi2.in")
        binding.edtPassword.setText("alialiali")

        binding.btnBck.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)

        }

        binding.tilPassword.editText?.addTextChangedListener {
            if (it!!.length < 8) {
                binding.tilPassword.error = "رمز عبور باید حداقل 8 حرف باشد."
            } else
                binding.tilPassword.error = null
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
        }


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
        // 3. ایجاد شیء OkHttpClient با استفاده از HttpLoggingInterceptor برای نمایش لاگ‌ها
        val interceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        val gson = GsonBuilder().setLenient().create()
        // 4. ایجاد شیء Retrofit با تنظیمات مورد نیاز
        val retrofit = Retrofit.Builder()
            .baseUrl("https://step24.ir/api/") // آدرس پایه سرویس API
            .addConverterFactory(GsonConverterFactory.create(gson)) // تبدیل پاسخ‌ها به شیء با استفاده از Gson
            .client(client) // استفاده از OkHttpClient برای درخواست‌ها
            .build()

        // 5. ایجاد شیء ApiService با استفاده از شیء Retrofit
        val apiService = retrofit.create(ApiService::class.java)

        // 6. ساختن شیء SignupRequest با اطلاعات ورودی
        val loginData = LoginData(
            email,
            password,
        )
        // 7. ارسال درخواست ثبت نام با استفاده از شیء ApiService
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
                    val username = response.body()?.user?.username
                    val userId = response.body()?.user?.id
                    val erorr = response.body()?.user?.id

                    Log.v("loginapp", "______________________________________")
                    Log.v("loginapp", "accessToken: ${accessToken}")
                    Log.v("loginapp", "refreshToken: ${refreshToken}")
                    Log.v("loginapp", "username: ${username}")
                    Log.v("loginapp", "userId: ${userId}")
                    Log.v("loginapp", "______________________________________")

                } else if (response.code() == 422) {

                    val errorBody = response.errorBody()?.string()
                    val gson = Gson()
                    val registerResponse = gson.fromJson(errorBody, LoginResponse::class.java)

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

                        //val nameErrors = errors.name
                        //val usernameErrors = errors.username
                        val emailErrors = errors.email
                        val passwordErrors = errors.password
                        //val phoneErrors = errors.phone

                        Log.v("loginapp", "______________________________________")
                        //Log.v("loginapp", "nameErrors: ${nameErrors}")
                       // Log.v("loginapp", "usernameErrors: ${usernameErrors}")
                        Log.v("loginapp", "emailErrors: ${emailErrors}")
                        Log.v("loginapp", "passwordErrors: ${passwordErrors}")
                       // Log.v("loginapp", "phoneErrors: ${phoneErrors}")
                        Log.v("loginapp", "______________________________________")

                        setError(
                            emailErrors,
                            email,
                            passwordErrors,
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
                Toast.makeText(applicationContext, "اتصال اینترنت خود را بررسی کنید.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setError(emailErrors: List<String>?, email: String, passwordErrors: List<String>?) {

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

    }


    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
    }
}