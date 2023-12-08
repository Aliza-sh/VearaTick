package com.vearad.vearatick.fgmSub

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.vearad.vearatick.DataBase.EfficiencyDao
import com.vearad.vearatick.DataBase.Employee
import com.vearad.vearatick.DataBase.EmployeeDao
import com.vearad.vearatick.Dialog.EmployeeDeleteDialogFragment
import com.vearad.vearatick.ProAndEmpActivity
import com.vearad.vearatick.R
import com.vearad.vearatick.databinding.ActivityProAndEmpBinding
import com.vearad.vearatick.databinding.FragmentEmployeeInformationBinding
import koleton.api.hideSkeleton
import koleton.api.loadSkeleton

class EmployeeInformationFragment(
    var employee: Employee,
    val efficiencyEmployeeDao: EfficiencyDao,
    val position: Int,
    val employeeDao: EmployeeDao,
    val bindingActivityProAndEmpBinding: ActivityProAndEmpBinding,
) : Fragment() {

    lateinit var binding: FragmentEmployeeInformationBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEmployeeInformationBinding.inflate(layoutInflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        onBackPressed()
        firstRun(view)
        setData(employee)

        binding.btnStatistics.setOnClickListener {
            btnStatistics(view)
        }
        binding.btnCalendar.setOnClickListener {
            btnCalendar(view)
        }
        binding.btnTask.setOnClickListener {
            btnTask(view)
        }


        val popupMenu = PopupMenu(this.context, binding.btnMenuEmployee)
        onMenuClicked(popupMenu)

        binding.btnBack.setOnClickListener {
            parentFragmentManager.beginTransaction().detach(this@EmployeeInformationFragment)
                .replace(R.id.frame_layout_sub, EmployeeFragment(bindingActivityProAndEmpBinding))
                .commit()
        }
    }

    private fun btnStatistics(view: View) {
        binding.txtStatistics.setTextColor(Color.parseColor("#E600ADB5"))
        binding.viewStatistics.visibility = VISIBLE

        binding.txtCalendar.setTextColor(Color.parseColor("#FFFFFF"))
        binding.viewCalendar.visibility = INVISIBLE

        binding.txtTask.setTextColor(Color.parseColor("#FFFFFF"))
        binding.viewTask.visibility = INVISIBLE

        replaceFragment(EmployeeStatisticsFragment(employee, efficiencyEmployeeDao, position))
    }

    private fun btnCalendar(view: View) {
        binding.txtCalendar.setTextColor(Color.parseColor("#E600ADB5"))
        binding.viewCalendar.visibility = VISIBLE

        binding.txtStatistics.setTextColor(Color.parseColor("#FFFFFF"))
        binding.viewStatistics.visibility = INVISIBLE

        binding.txtTask.setTextColor(Color.parseColor("#FFFFFF"))
        binding.viewTask.visibility = INVISIBLE

        replaceFragment(EmployeeCalendarFragment(employee, efficiencyEmployeeDao, position))
    }

    private fun btnTask(view: View) {
        binding.txtTask.setTextColor(Color.parseColor("#E600ADB5"))
        binding.viewTask.visibility = VISIBLE

        binding.txtStatistics.setTextColor(Color.parseColor("#FFFFFF"))
        binding.viewStatistics.visibility = INVISIBLE

        binding.txtCalendar.setTextColor(Color.parseColor("#FFFFFF"))
        binding.viewCalendar.visibility = INVISIBLE

        replaceFragment(
            EmployeeTaskFragment(
                employee,
                employeeDao,
                efficiencyEmployeeDao,
                position,
                bindingActivityProAndEmpBinding
            )
        )
    }

    private fun replaceFragment(fragment: Fragment) {
        var elapsedTime:Long = 0
        binding.tablayoutEmp.loadSkeleton()
       val thread = Thread{
            val startTime = System.currentTimeMillis()
            val transaction =
                (activity as ProAndEmpActivity).supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_layout_emp, fragment)
                .commit()
            val endTime = System.currentTimeMillis()
            elapsedTime = endTime - startTime
        }.start()

        Handler(Looper.getMainLooper()).postDelayed({
            binding.tablayoutEmp.hideSkeleton()
        }, elapsedTime)

    }

    private fun firstRun(view: View) {
        btnStatistics(view)
        replaceFragment(
            EmployeeStatisticsFragment(
                employee,
                efficiencyEmployeeDao,
                position,
            )
        )
    }

    private fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    parentFragmentManager.beginTransaction()
                        .detach(this@EmployeeInformationFragment)
                        .replace(
                            R.id.frame_layout_sub,
                            EmployeeFragment(bindingActivityProAndEmpBinding)
                        ).commit()
                }
            })
    }

    private fun onMenuClicked(popupMenu: PopupMenu) {
        popupMenu.menuInflater.inflate(R.menu.menu_employee, popupMenu.menu)
        binding.btnMenuEmployee.setOnClickListener {
            popupMenu.show()
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_employee_edit -> {
                        val transaction =
                            (activity as ProAndEmpActivity).supportFragmentManager.beginTransaction()
                        transaction.replace(
                            R.id.layout_pro_and_emp,
                            EmployeeInfoUpdateFragment(
                                employee,
                                efficiencyEmployeeDao,
                                position,
                                employeeDao,
                                bindingActivityProAndEmpBinding,
                            )
                        )
                            .addToBackStack(null)
                            .commit()
                    }

                    R.id.menu_employee_delete -> {
                        val dialog = EmployeeDeleteDialogFragment(
                            employee,
                            position,
                            bindingActivityProAndEmpBinding,
                            this
                        )
                        dialog.show((activity as ProAndEmpActivity).supportFragmentManager, null)
                    }
                }
                true
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateYourData()
        setData(employee)
    }

    private fun updateYourData() {
        employee = employeeDao.getEmployee(employee.idEmployee!!)!!
    }

    @SuppressLint("SetTextI18n")
    private fun setData(employee: Employee) {

        binding.txtNameEmp.text = employee.name + " " + employee.family
        binding.txtSpecialtyEmp.text = employee.specialty


        if (employee.rank == "سهام دار") {

            binding.txtRank.text = "سهام دار"
            val shape = GradientDrawable()
            shape.shape = GradientDrawable.RECTANGLE
            shape.cornerRadii = floatArrayOf(40f, 40f, 40f, 40f, 40f, 40f, 40f, 40f)
            shape.setStroke(
                5,
                ContextCompat.getColor(binding.root.context, R.color.green_dark_rank)
            )
            shape.setColor(ContextCompat.getColor(binding.root.context, R.color.green_light_rank))
            binding.txtRank.setTextColor(android.graphics.Color.parseColor("#227158"))
            binding.txtRank.background = shape

        } else if (employee.rank == "کارمند") {

            binding.txtRank.text = "کارمند"
            val shape = GradientDrawable()
            shape.shape = GradientDrawable.RECTANGLE
            shape.cornerRadii = floatArrayOf(40f, 40f, 40f, 40f, 40f, 40f, 40f, 40f)
            shape.setStroke(
                5,
                ContextCompat.getColor(binding.root.context, R.color.blue_dark_rank)
            )
            shape.setColor(ContextCompat.getColor(binding.root.context, R.color.blue_light_rank))
            binding.txtRank.setTextColor(android.graphics.Color.parseColor("#215DAD"))
            binding.txtRank.background = shape

        } else if (employee.rank == "کارآموز") {
            binding.txtRank.text = "کارآموز"
            val shape = GradientDrawable()
            shape.shape = GradientDrawable.RECTANGLE
            shape.cornerRadii = floatArrayOf(40f, 40f, 40f, 40f, 40f, 40f, 40f, 40f)
            shape.setStroke(
                5,
                ContextCompat.getColor(binding.root.context, R.color.red_dark_rank)
            )
            shape.setColor(ContextCompat.getColor(binding.root.context, R.color.red_light_rank))
            binding.txtRank.setTextColor(android.graphics.Color.parseColor("#AF694C"))
            binding.txtRank.background = shape
        }

        val progress = efficiencyEmployeeDao.getEfficiencyEmployee(employee.idEmployee!!)
        val efficiencyTotalPresence = progress!!.efficiencyTotalPresence
        val efficiencyTotalDuties = progress.efficiencyTotalDuties
        val efficiencyTotal = (efficiencyTotalPresence + efficiencyTotalDuties) / 2
        binding.prgTotalEmp.progress = efficiencyTotal.toFloat()
        if (employee.imagePath != "") {
            Glide.with(this)
                .load(employee.imagePath)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.btnInfoPrn)
        } else
            if (employee.gender == "زن") {
                binding.btnInfoPrn.setImageResource(R.drawable.img_matter)
            }
    }

}