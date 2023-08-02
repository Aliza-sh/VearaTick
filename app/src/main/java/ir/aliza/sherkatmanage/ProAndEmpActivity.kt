package ir.aliza.sherkatmanage

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import ir.aliza.sherkatmanage.databinding.ActivityProAndEmpBinding
import ir.aliza.sherkatmanage.fgmMain.EmployeeFragment
import ir.aliza.sherkatmanage.fgmMain.ProjectFragment


class ProAndEmpActivity : AppCompatActivity() {

    lateinit var binding: ActivityProAndEmpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProAndEmpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firstRun()

        binding.btnProjrct.setOnClickListener() {
            binding.icProject.setColorFilter(ContextCompat.getColor(this, R.color.firoze), PorterDuff.Mode.SRC_IN)
            binding.txtProject.setTextColor(Color.parseColor("#E600ADB5"))

            binding.icEmployee.setColorFilter(ContextCompat.getColor(this, R.color.gray), PorterDuff.Mode.SRC_IN)
            binding.txtEmployee.setTextColor(Color.parseColor("#929292"))

            replaceFragment(ProjectFragment(binding))
        }
        binding.btnEmployee.setOnClickListener() {
            binding.icEmployee.setColorFilter(ContextCompat.getColor(this, R.color.firoze), PorterDuff.Mode.SRC_IN)
            binding.txtEmployee.setTextColor(Color.parseColor("#E600ADB5"))

            binding.icProject.setColorFilter(ContextCompat.getColor(this, R.color.gray), PorterDuff.Mode.SRC_IN)
            binding.txtProject.setTextColor(Color.parseColor("#929292"))

            replaceFragment(EmployeeFragment(binding))

        }


    }

    fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_layout_sub, fragment)
            .commit()
    }

    fun firstRun() {
        replaceFragment(ProjectFragment(binding))
        binding.icProject.setColorFilter(ContextCompat.getColor(this, R.color.firoze), PorterDuff.Mode.SRC_IN)
        binding.txtProject.setTextColor(Color.parseColor("#E600ADB5"))
    }
}