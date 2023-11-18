package com.vearad.vearatick

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.vearad.vearatick.databinding.ActivityProAndEmpBinding
import com.vearad.vearatick.fgmSub.EmployeeFragment
import com.vearad.vearatick.fgmSub.ProjectFragment


class ProAndEmpActivity : AppCompatActivity() {

    lateinit var binding: ActivityProAndEmpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProAndEmpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val itemClicked = intent.getIntExtra("itemClicked", 1)
        when (itemClicked) {
            1 -> {
                goToProjectFragment()
            }

            2 -> {
                goToEmployeeFragment()
            }
        }

        binding.btnBck.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)

        }

        binding.btnProjrct.setOnClickListener() {
            binding.txtTitle.text = "پروژها"
            binding.icProject.setColorFilter(
                ContextCompat.getColor(this, R.color.firoze),
                PorterDuff.Mode.SRC_IN
            )
            binding.txtProject.setTextColor(Color.parseColor("#E600ADB5"))

            binding.icEmployee.setColorFilter(
                ContextCompat.getColor(this, R.color.gray),
                PorterDuff.Mode.SRC_IN
            )
            binding.txtEmployee.setTextColor(Color.parseColor("#929292"))

            replaceFragment(ProjectFragment(binding))
        }

        binding.btnEmployee.setOnClickListener() {
            binding.txtTitle.text = "کارکنان"
            binding.icEmployee.setColorFilter(
                ContextCompat.getColor(this, R.color.firoze),
                PorterDuff.Mode.SRC_IN
            )
            binding.txtEmployee.setTextColor(Color.parseColor("#E600ADB5"))

            binding.icProject.setColorFilter(
                ContextCompat.getColor(this, R.color.gray),
                PorterDuff.Mode.SRC_IN
            )
            binding.txtProject.setTextColor(Color.parseColor("#929292"))

            replaceFragment(EmployeeFragment(binding))
        }

    }
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
    }

    fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_layout_sub, fragment)
            .commit()
    }

    fun goToProjectFragment() {
        replaceFragment(ProjectFragment(binding))
        binding.icProject.setColorFilter(
            ContextCompat.getColor(this, R.color.firoze),
            PorterDuff.Mode.SRC_IN
        )
        binding.txtProject.setTextColor(Color.parseColor("#E600ADB5"))
    }

    fun goToEmployeeFragment() {
        replaceFragment(EmployeeFragment(binding))

        binding.txtTitle.text = "کارکنان"
        binding.icProject.setColorFilter(
            ContextCompat.getColor(this, R.color.gray),
            PorterDuff.Mode.SRC_IN
        )
        binding.txtProject.setTextColor(Color.parseColor("#929292"))
        binding.icEmployee.setColorFilter(
            ContextCompat.getColor(this, R.color.firoze),
            PorterDuff.Mode.SRC_IN
        )
        binding.txtEmployee.setTextColor(Color.parseColor("#E600ADB5"))
    }
}