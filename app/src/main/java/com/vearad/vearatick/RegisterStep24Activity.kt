package com.vearad.vearatick

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.vearad.vearatick.databinding.ActivityRegisterStep24Binding


class RegisterStep24Activity : AppCompatActivity() {
    lateinit var binding: ActivityRegisterStep24Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterStep24Binding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBck.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)

        }

        binding.btnLogin.setOnClickListener {
            val intent = Intent(this, LoginStep24Activity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
        }

        binding.tilPassword.editText?.addTextChangedListener {
            if (it!!.length < 8 ){
                binding.tilPassword.error = "رمز عبور باید حداقل 8 حرف باشد."
            }else
                binding.tilPassword.error = null
        }

        binding.edtNumberPhone.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val phoneNumber = s.toString().trim()
                val phoneNumberRegex = Regex("^09\\d{9}$")

                if (!phoneNumber.matches(phoneNumberRegex)) {
                    binding.tilNumberPhone.error = "فرمت شماره معتبر نیست."
                } else {
                    binding.tilNumberPhone.error = null
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No implementation needed
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // No implementation needed
            }
        })

        binding.edtEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val email = s.toString().trim()
                val emailRegex = Regex("^\\w+([.-]?\\w+)*@\\w+([.-]?\\w+)*(\\.\\w{2,3})+$")

                if (!email.matches(emailRegex)) {
                    binding.tilEmail.error = "فرمت ایمیل معتبر نیست."
                } else {
                    binding.tilEmail.error = null
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No implementation needed
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // No implementation needed
            }
        })

    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
    }

}