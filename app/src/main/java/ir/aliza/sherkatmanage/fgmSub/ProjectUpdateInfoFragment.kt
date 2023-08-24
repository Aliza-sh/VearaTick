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
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import com.wdullaer.materialdatetimepicker.time.Timepoint
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

class ProjectUpdateInfoFragment(
    val project: Project,
    val position: Int,
    val projectDao: ProjectDao,
    val day: String,
    val monthName: String,
    val subTaskProjectDao: SubTaskProjectDao,
    val bindingActivityProAndEmp: ActivityProAndEmpBinding,
) : Fragment() {

    lateinit var binding: FragmentProjectUpdateInfoBinding
    lateinit var bindingDialogView: FragmentDialogDeadlineBinding

    var valueBtnNoDate = false
    var valueBtnWatch = false
    var valueBtnCalendar = false

    var valueWatch = ""
    var valueCalendar = ""

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

        setdata(project)

        binding.sheetBtnDone.setOnClickListener {
            addNewProject()
        }

        binding.btnBck.setOnClickListener {
            if (parentFragmentManager.backStackEntryCount > 0) {
                parentFragmentManager.popBackStack()
            }
        }

        binding.btnCalendar.setOnClickListener {
            showDeadlineDialog()
        }

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
                                day,
                                monthName,
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
            if (!valueBtnNoDate && !valueBtnWatch && !valueBtnCalendar) {
                bindingDialogView.btnNoDate.setBackgroundResource(R.drawable.shape_background_deadline_firoze)
                valueBtnNoDate = true

            } else {
                bindingDialogView.btnNoDate.setBackgroundResource(R.drawable.shape_background_deadline_blacke)
                valueBtnNoDate = false

            }
        }

        bindingDialogView.btnWatch.setOnClickListener {
            if (!valueBtnNoDate && !valueBtnWatch) {
                onCreatePicker()
            } else {
                bindingDialogView.btnWatch.setBackgroundResource(R.drawable.shape_background_deadline_blacke)
                bindingDialogView.txtWatch.text = "ساعت"
                bindingDialogView.txtWatch.textSize = 20f
                valueBtnWatch = false
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
            if (valueBtnWatch && !valueBtnCalendar)
                binding.txtDedlineDateTime.text = valueWatch
            if (!valueBtnWatch && valueBtnCalendar)
                binding.txtDedlineDateTime.text = valueCalendar
            if (valueBtnWatch && valueBtnCalendar)
                binding.txtDedlineDateTime.text = "$valueCalendar \n$valueWatch "

            alertDialog.dismiss()
        }

    }

    fun onProjectInfoUpdate() {
        parentFragmentManager.beginTransaction().detach(this@ProjectUpdateInfoFragment)
            .replace(
                R.id.layout_pro_and_emp,
                ProjectInformationFragment(
                    project,
                    day,
                    monthName,
                    subTaskProjectDao,
                    projectDao,
                    position,
                    bindingActivityProAndEmp
                )
            ).commit()
    }

    private fun setdata(project: Project) {

        binding.edtNamePro.setText(project.nameProject)

        if (project.noDeadlineProject!!)
            binding.txtDedlineDateTime.setText("پروژه ددلاین \nندارد")
        else
            binding.txtDedlineDateTime.setText("${project.dateProject} \n${project.watchProject}")

        binding.edtTypeProject.setText(project.typeProject)
        binding.edtDescriptionPro.setText(project.descriptionProject)

    }

    fun onCreatePicker() {

        val persianCalendar = com.kizitonwose.calendarview.utils.persian.PersianCalendar()

        val timePickerDialog = TimePickerDialog.newInstance(
            TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute, second ->
                valueWatch = "$hourOfDay:$minute"
                bindingDialogView.txtWatch.text = valueWatch
                bindingDialogView.txtWatch.textSize = 24f
                bindingDialogView.btnWatch.setBackgroundResource(R.drawable.shape_background_deadline_firoze)
                valueBtnWatch = true
            },

            true
        )
        timePickerDialog.isThemeDark = true
        timePickerDialog.setCancelText("بیخیال")
        timePickerDialog.setOkText("تایید")
        timePickerDialog.setTimeInterval(1, 1, 10)
        timePickerDialog.setInitialSelection(
            Timepoint(
                persianCalendar.time.hours,
                persianCalendar.time.minutes
            )
        )
        timePickerDialog.show(parentFragmentManager, "TimePickerDialog")

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
                    valueCalendar = date.toString()
                    bindingDialogView.txtCalendar.text = valueCalendar
                    bindingDialogView.txtCalendar.textSize = 22f
                    bindingDialogView.btnCalendar.setBackgroundResource(R.drawable.shape_background_deadline_firoze)
                    valueBtnCalendar = true
                }
            }
        )

    }

    private fun addNewProject() {
        if (
            binding.edtNamePro.length() > 0 &&
            binding.txtDedlineDateTime.length() > 0 &&
            binding.edtTypeProject.length() > 0 &&
            binding.edtDescriptionPro.length() > 0
        ) {
            val txtNamePro = binding.edtNamePro.text.toString()
            val noDeadline = valueBtnNoDate
            val txtWatch = valueWatch
            val txtDate = valueCalendar
            val txtTypeProject = binding.edtTypeProject.text.toString()
            val txtDescriptionPro = binding.edtDescriptionPro.text.toString()

            val newProject = Project(
                idProject = project.idProject,
                nameProject = txtNamePro,
                descriptionProject = txtDescriptionPro,
                noDeadlineProject = noDeadline,
                watchProject = txtWatch,
                dateProject = txtDate,
                typeProject = txtTypeProject,
                progressProject = project.progressProject,
                doneProject = project.doneProject,
                numberSubTaskProject = project.numberSubTaskProject,
                numberDoneSubTaskProject = project.numberDoneSubTaskProject,
                year = project.year,
                month = project.month,
                day = project.day,
            )

            projectAdapter.updateProject(newProject, position)
            projectDao.update(newProject)
            onProjectInfoUpdate()

        } else {
            Toast.makeText(context, "لطفا همه مقادیر را وارد کنید", Toast.LENGTH_SHORT).show()
        }
    }
}