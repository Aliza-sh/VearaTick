package ir.aliza.sherkatmanage.fgmSub

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.fragment.app.Fragment
import com.xdev.arch.persiancalendar.datepicker.CalendarConstraints
import com.xdev.arch.persiancalendar.datepicker.DateValidatorPointForward
import com.xdev.arch.persiancalendar.datepicker.MaterialDatePicker
import com.xdev.arch.persiancalendar.datepicker.MaterialPickerOnPositiveButtonClickListener
import com.xdev.arch.persiancalendar.datepicker.Month
import com.xdev.arch.persiancalendar.datepicker.calendar.PersianCalendar
import ir.aliza.sherkatmanage.DataBase.Project
import ir.aliza.sherkatmanage.DataBase.ProjectDao
import ir.aliza.sherkatmanage.DataBase.SubTaskProjectDao
import ir.aliza.sherkatmanage.ProAndEmpActivity
import ir.aliza.sherkatmanage.R
import ir.aliza.sherkatmanage.databinding.ActivityProAndEmpBinding
import ir.aliza.sherkatmanage.databinding.FragmentDialogDeadlineBinding
import ir.aliza.sherkatmanage.databinding.FragmentProjectUpdateInfoBinding
import ir.aliza.sherkatmanage.projectAdapter
import java.text.DecimalFormat

class ProjectUpdateInfoFragment(
    val project: Project,
    val position: Int,
    val projectDao: ProjectDao,
    val subTaskProjectDao: SubTaskProjectDao,
    val bindingActivityProAndEmp: ActivityProAndEmpBinding,
) : Fragment() {

    lateinit var binding: FragmentProjectUpdateInfoBinding
    lateinit var bindingDialogView: FragmentDialogDeadlineBinding

    var valueBtnNoDate = false
    var valueBtnCalendar = false

    var valueCalendar = ""
    var valueDay = 0
    var valueMonth = 0
    var valueYear = 0

    private var isUpdating = false
    var formattedValue = "0"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProjectUpdateInfoBinding.inflate(layoutInflater, null, false)
        bindingDialogView = FragmentDialogDeadlineBinding.inflate(layoutInflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBackPressed()
        setdata(project)

        val typeProject = listOf(
            "اندروید",
            "بک اند",
            "فرانت اند",
            "رباتیک",
            "طراحی",
            "سئو"
        )

        val myAdapteredt = ArrayAdapter(requireContext(), R.layout.item_gender, typeProject)
        (binding.dialogMainEdtGdrperson.editText as AutoCompleteTextView).setAdapter(
            myAdapteredt
        )

        binding.btnBck.setOnClickListener {
            if (parentFragmentManager.backStackEntryCount > 0) {
                parentFragmentManager.popBackStack()
            }
        }

        binding.btnCalendar.setOnClickListener {
            showDeadlineDialog()
        }

        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->

            when (checkedId) {

                R.id.btn_no_settlement -> {
                    binding.budget.visibility = View.GONE
                }

                R.id.btn_settlement -> {
                    binding.budget.visibility = View.VISIBLE
                }

            }
        }

        val decimalFormat = DecimalFormat("#,###")
        binding.edtBudget.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // قبل از تغییرات متنی
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // در هنگام تغییرات متنی
            }

            override fun afterTextChanged(s: Editable?) {
                // پس از تغییرات متنی
                if (isUpdating) {
                    return
                }

                isUpdating = true

                val input = s.toString().replace(",", "") // حذف ویرگول‌ها از ورودی
                val value = input.toLongOrNull()

                if (value != null) {
                    formattedValue = decimalFormat.format(value)
                    binding.edtBudget.setText(formattedValue)
                    binding.edtBudget.setSelection(formattedValue.length)
                    binding.txtBudget.text = formatCurrency(value)
                }
                isUpdating = false
            }
        })

        binding.sheetBtnDone.setOnClickListener {
            if (binding.btnSettlement.isChecked) {

                if (binding.edtBudget.length() > 0) {
                    addNewProject(formattedValue)
                } else {
                    Toast.makeText(context, "لطفا همه مقادیر را وارد کنید", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                formattedValue = "0"
                addNewProject(formattedValue)
            }
        }
    }
    private fun formatCurrency(value: Long?): String {
        val decimalFormat = DecimalFormat("#,###")
        return decimalFormat.format(value) + " تومان"
    }
    fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    parentFragmentManager.beginTransaction().detach(this@ProjectUpdateInfoFragment)
                        .attach(
                            ProjectInformationFragment(
                                project,
                                subTaskProjectDao,
                                projectDao,
                                position,
                                bindingActivityProAndEmp
                            )
                        ).commit()
                }
            })
    }
    private fun showDeadlineDialog() {

        val parent = bindingDialogView.root.parent as? ViewGroup
        parent?.removeView(bindingDialogView.root)
        val dialogBuilder = AlertDialog.Builder(bindingDialogView.root.context)
        dialogBuilder.setView(bindingDialogView.root)

        bindingDialogView.btnNoDate.setOnClickListener {
            if (!valueBtnNoDate && !valueBtnCalendar) {
                bindingDialogView.btnNoDate.setBackgroundResource(R.drawable.shape_background_deadline_firoze)
                valueBtnNoDate = true

            } else {
                bindingDialogView.btnNoDate.setBackgroundResource(R.drawable.shape_background_deadline_blacke)
                valueBtnNoDate = false

            }
        }

        bindingDialogView.btnCalendar.setOnClickListener {
            if (!valueBtnNoDate && !valueBtnCalendar) {
                onCreateCalendar()
            } else {
                bindingDialogView.txtCalendar.text = "تقویم"
                bindingDialogView.txtCalendar.textSize = 20f
                bindingDialogView.btnCalendar.setBackgroundResource(R.drawable.shape_background_deadline_blacke)
                valueBtnCalendar = false
            }
        }

        val alertDialog = dialogBuilder.create()
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.Transparent.toArgb()))
        alertDialog.setCancelable(false)
        alertDialog.show()
        bindingDialogView.dialogBtnCansel.setOnClickListener {
            alertDialog.dismiss()
        }
        bindingDialogView.dialogBtnSure.setOnClickListener {

            if (valueBtnNoDate)
                binding.txtDedlineDateTime.text = "پروژه ددلاین \n ندارد "
            else
                binding.txtDedlineDateTime.text = valueCalendar

            alertDialog.dismiss()
        }

    }
    fun onProjectInfoUpdate() {
        parentFragmentManager.beginTransaction()
            .detach(this@ProjectUpdateInfoFragment)
            .replace(
                R.id.layout_pro_and_emp,
                ProjectInformationFragment(
                    project,
                    subTaskProjectDao,
                    projectDao,
                    position,
                    bindingActivityProAndEmp
                )
            ).commit()
    }
    private fun setdata(project: Project) {

        binding.edtNamePro.setText(project.nameProject)

        if (project.budgetProject !="0"){
            formattedValue = project.budgetProject!!
            binding.btnSettlement.isChecked = true
            binding.budget.visibility = View.VISIBLE
            binding.edtBudget.setText(project.budgetProject)
            binding.txtBudget.text = project.budgetProject + " تومان"
        }else
            binding.btnNoSettlement.isChecked = true

        if (project.noDeadlineProject!!) {
            valueBtnNoDate = project.noDeadlineProject
            binding.txtDedlineDateTime.setText("پروژه ددلاین \nندارد")
        } else {
            valueCalendar = project.valueCalendar
            valueDay = project.dayCreation
            valueMonth = project.monthCreation
            valueYear = project.yearCreation
            binding.txtDedlineDateTime.setText(valueCalendar)

        }

        binding.edtTypeProject.setText(project.typeProject)
        binding.edtDescriptionPro.setText(project.descriptionProject)

    }
    fun onCreateCalendar() {

        val calendar = PersianCalendar()
        calendar.setPersian(1400, Month.FARVARDIN, 1)
        val start = calendar.timeInMillis
        calendar.setPersian(1500, Month.ESFAND, 29)
        val end = calendar.timeInMillis
        val openAt = PersianCalendar.getToday().timeInMillis
        val constraints = CalendarConstraints.Builder()
            .setStart(start)
            .setEnd(end)
            .setOpenAt(openAt)
            .setValidator(DateValidatorPointForward.from(start)).build()
        val datePicker = MaterialDatePicker.Builder
            .datePicker()
            .setTitleText("تاریخ را انتخاب کنید.")
            .setTheme(R.style.AppTheme_PersianCalendar)
            .setCalendarConstraints(constraints).build()
        datePicker.show((activity as ProAndEmpActivity).supportFragmentManager, "aTag")
        datePicker.isCancelable

        datePicker.addOnPositiveButtonClickListener(
            object : MaterialPickerOnPositiveButtonClickListener<Long?> {
                @SuppressLint("SetTextI18n")
                override fun onPositiveButtonClick(selection: Long?) {
                    val date = PersianCalendar(selection!!)
                    valueCalendar = "${date.year}/${date.month + 1}/${date.day}"
                    valueDay  = date.day
                    valueMonth= date.month
                    valueYear= date.year
                    binding.txtDedlineDateTime.text = valueCalendar
                    bindingDialogView.txtCalendar.textSize = 22f
                    bindingDialogView.btnCalendar.setBackgroundResource(R.drawable.shape_background_deadline_firoze)
                    valueBtnCalendar = true
                }
            }
        )

    }

    private fun addNewProject(formattedValue: String) {
        if (
            binding.edtNamePro.length() > 0 &&
            binding.txtDedlineDateTime.length() > 0 &&
            binding.edtTypeProject.length() > 0 &&
            binding.edtDescriptionPro.length() > 0
        ) {
            val txtNamePro = binding.edtNamePro.text.toString()
            val noDeadline = valueBtnNoDate
            val txtDate = valueCalendar
            val txtTypeProject = binding.edtTypeProject.text.toString()
            val txtDescriptionPro = binding.edtDescriptionPro.text.toString()
            val txtBudget = formattedValue

            val newProject = Project(
                idProject = project.idProject,
                nameProject = txtNamePro,
                descriptionProject = txtDescriptionPro,
                noDeadlineProject = noDeadline,
                yearCreation =  valueYear.toInt(),
                monthCreation = valueMonth.toInt(),
                dayCreation = valueDay.toInt(),
                valueCalendar = valueCalendar,
                typeProject = txtTypeProject,
                progressProject = project.progressProject,
                doneProject = project.doneProject,
                numberSubTaskProject = project.numberSubTaskProject,
                numberDoneSubTaskProject = project.numberDoneSubTaskProject,
                budgetProject = txtBudget,
            )

            projectAdapter.updateProject(newProject, position)
            projectDao.update(newProject)
            onProjectInfoUpdate()

        } else {
            Toast.makeText(context, "لطفا همه مقادیر را وارد کنید", Toast.LENGTH_SHORT).show()
        }
    }
}