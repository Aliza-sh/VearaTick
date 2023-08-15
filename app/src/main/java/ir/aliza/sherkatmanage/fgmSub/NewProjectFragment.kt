package ir.aliza.sherkatmanage.fgmSub

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
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
import com.xdev.arch.persiancalendar.datepicker.*
import com.xdev.arch.persiancalendar.datepicker.calendar.PersianCalendar
import ir.aliza.sherkatmanage.DataBase.Project
import ir.aliza.sherkatmanage.DataBase.ProjectDao
import ir.aliza.sherkatmanage.ProAndEmpActivity
import ir.aliza.sherkatmanage.R
import ir.aliza.sherkatmanage.databinding.ActivityProAndEmpBinding
import ir.aliza.sherkatmanage.databinding.FragmentDialogDeadlineBinding
import ir.aliza.sherkatmanage.databinding.FragmentNewProjectBinding
import ir.aliza.sherkatmanage.projectAdapter

class NewProjectFragment(
    val projectDao: ProjectDao,
    val bindingActivityProAndEmp: ActivityProAndEmpBinding,
) : Fragment() {
    lateinit var binding: FragmentNewProjectBinding
    lateinit var bindingDialogView: FragmentDialogDeadlineBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewProjectBinding.inflate(layoutInflater, container, false)
        bindingDialogView = FragmentDialogDeadlineBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBackPressed()

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

        binding.sheetBtnDone.setOnClickListener {
            addNewProject()
            onNewProject()
        }

        binding.btnCalendar.setOnClickListener {
            showDeadlineDialog()
        }
    }

    private fun showDeadlineDialog() {

        var btnNoDate = false
        var btnWatch = false
        var btnCalendar = false

        val dialogBuilder = AlertDialog.Builder(bindingDialogView.root.context)
        dialogBuilder.setView(bindingDialogView.root)

        bindingDialogView.btnNoDate.setOnClickListener {

            if (!btnNoDate) {
                bindingDialogView.btnNoDate.setBackgroundResource(R.drawable.shape_background_deadline_firoze)
                btnNoDate = true

            } else {
                bindingDialogView.btnNoDate.setBackgroundResource(R.drawable.shape_background_deadline_blacke)
                btnNoDate = false

            }

        }

        bindingDialogView.btnWatch.setOnClickListener {
            if (!btnNoDate)
                bindingDialogView.btnWatch.setBackgroundResource(R.drawable.shape_background_deadline_firoze)
        }
        bindingDialogView.btnCalendar.setOnClickListener {
            if (!btnNoDate)
                bindingDialogView.btnCalendar.setBackgroundResource(R.drawable.shape_background_deadline_firoze)
        }

        val alertDialog = dialogBuilder.create()
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.Transparent.toArgb()))
        alertDialog.setCancelable(false)
        alertDialog.show()
        bindingDialogView.dialogBtnCansel.setOnClickListener {
            alertDialog.dismiss()
        }
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

        datePicker.addOnPositiveButtonClickListener(
            object : MaterialPickerOnPositiveButtonClickListener<Long?> {
                @SuppressLint("SetTextI18n")
                override fun onPositiveButtonClick(selection: Long?) {
                    val date = PersianCalendar(selection!!)
                    binding.txtDedlineDateTime.text = date.toString()
                }
            }
        )

    }

    fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    parentFragmentManager.beginTransaction().detach(this@NewProjectFragment)
                        .attach(ProjectFragment(bindingActivityProAndEmp)).commit()
                }
            })
    }

    fun onNewProject() {
        parentFragmentManager.beginTransaction().detach(this@NewProjectFragment)
            .replace(R.id.frame_layout_sub, ProjectFragment(bindingActivityProAndEmp))
            .commit()
    }

    private fun addNewProject() {
        if (
            binding.edtNamePro.length() > 0 &&
            //binding.edtDayProject.length() > 0 &&
            binding.edtTypeProject.length() > 0 &&
            binding.edtInfoPro.length() > 0
        //binding.edtTimePro.length() > 0 &&
        //binding.edtTimePro.text.toString().toInt() <= 24
        ) {
            val txtname = binding.edtNamePro.text.toString()
            //val txtDay = binding.edtDayProject.text.toString()
            //val txtTime = binding.edtTimePro.text.toString()
            val txtType = binding.edtTypeProject.text.toString()
            val txtDescription = binding.edtInfoPro.text.toString()
            val day = PersianCalendar()

            val newProject = Project(
                nameProject = txtname,
                dayProject = 2,
                watchProject = 3,
                typeProject = txtType,
                descriptionProject = txtDescription,

                year = day.year,
                month = day.month,
                day = day.day

            )
            projectAdapter.addProject(newProject)
            projectDao.insert(newProject)
            onBackPressed()
        } else {
            Toast.makeText(context, "لطفا همه مقادیر را وارد کنید", Toast.LENGTH_SHORT).show()
        }
    }

}