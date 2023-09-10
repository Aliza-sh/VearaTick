package ir.aliza.sherkatmanage.fgmSub

import android.annotation.SuppressLint
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import ir.aliza.sherkatmanage.DataBase.EfficiencyDao
import ir.aliza.sherkatmanage.DataBase.Employee
import ir.aliza.sherkatmanage.DataBase.EmployeeDao
import ir.aliza.sherkatmanage.Dialog.EmployeeDeleteDialogFragment
import ir.aliza.sherkatmanage.ProAndEmpActivity
import ir.aliza.sherkatmanage.R
import ir.aliza.sherkatmanage.adapter.ViewPagerEmployeeAdapter
import ir.aliza.sherkatmanage.databinding.ActivityProAndEmpBinding
import ir.aliza.sherkatmanage.databinding.FragmentEmployeeInformationBinding

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
        setData(employee)

        val myAdapter = ViewPagerEmployeeAdapter(
            employee,
            this,
            efficiencyEmployeeDao,
            position,
            employeeDao,
            bindingActivityProAndEmpBinding
        )
        binding.viewpagerEmp.adapter = myAdapter
        binding.viewpagerEmp.offscreenPageLimit = 2

        val mediator = TabLayoutMediator(
            binding.tablayoutEmp,
            binding.viewpagerEmp,
            object : TabLayoutMediator.TabConfigurationStrategy {
                override fun onConfigureTab(tab: TabLayout.Tab, position: Int) {
                    when (position) {
                        0 -> tab.text = "آمار "

                        1 -> tab.text = "تقویم "

                        2 -> tab.text = "وظایف"

                    }
                }
            })
        mediator.attach()

        val popupMenu = PopupMenu(this.context, binding.btnMenuEmployee)
        onMenuClicked(popupMenu)

        binding.btnBack.setOnClickListener {
            parentFragmentManager.beginTransaction().detach(this@EmployeeInformationFragment)
                .replace(R.id.frame_layout_sub, EmployeeFragment(bindingActivityProAndEmpBinding))
                .commit()
        }
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
        val efficiencyTotalPresence =
            progress!!.efficiencyWeekPresence!! + progress.efficiencyTotalPresence!!
        val efficiencyTotalDuties = progress.efficiencyWeekDuties!! +progress.efficiencyMonthDuties!! +progress.efficiencyTotalDuties!!
        val efficiencyTotal =
            (efficiencyTotalPresence + efficiencyTotalDuties) / 2
        binding.prgTotalEmp.progress = efficiencyTotal.toFloat()

        if (employee.gender == "زن") {
            binding.btnInfoPrn.setImageResource(R.drawable.img_matter)
        }
    }

}