package com.vearad.vearatick.Dialog

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.xdev.arch.persiancalendar.datepicker.CalendarConstraints
import com.xdev.arch.persiancalendar.datepicker.DateValidatorPointForward
import com.xdev.arch.persiancalendar.datepicker.MaterialDatePicker
import com.xdev.arch.persiancalendar.datepicker.MaterialPickerOnPositiveButtonClickListener
import com.xdev.arch.persiancalendar.datepicker.Month
import com.xdev.arch.persiancalendar.datepicker.calendar.PersianCalendar
import com.vearad.vearatick.DataBase.Project
import com.vearad.vearatick.DataBase.ProjectDao
import com.vearad.vearatick.DataBase.SubTaskProject
import com.vearad.vearatick.DataBase.SubTaskProjectDao
import com.vearad.vearatick.ProAndEmpActivity
import com.vearad.vearatick.R
import com.vearad.vearatick.adapter.SubTaskProjectAdapter
import com.vearad.vearatick.databinding.ActivityProAndEmpBinding
import com.vearad.vearatick.databinding.BottomsheetfragmentUpdeteSubtaskProjectBinding
import com.vearad.vearatick.fgmSub.ProjectSubTaskFragment
import org.joda.time.DateTime
import org.joda.time.Days

class ProjectUpdateSubTaskFromSubTaskBottomsheetFragment(
    val subTaskProjectDao: SubTaskProjectDao,
    val project: Project,
    val subTaskProjectAdapter: SubTaskProjectAdapter,
    val projectDao: ProjectDao,
    val position: Int,
    val bindingActivityProAndEmp: ActivityProAndEmpBinding,
    val subTaskProject: SubTaskProject
) : BottomSheetDialogFragment() {

    lateinit var binding: BottomsheetfragmentUpdeteSubtaskProjectBinding

    var valueCalendar = ""
    var valueDay = ""
    var valueMonth = ""
    var valueYear = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            BottomsheetfragmentUpdeteSubtaskProjectBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setdata()

        binding.sheetBtnDone.setOnClickListener {
            addNewTask()
            onSubTaskToProject()
        }

        binding.btnCalendar.setOnClickListener {
            onCreateCalendar()
        }

    }

    private fun setdata() {

        binding.edtNameTask.setText(subTaskProject.nameSubTask)

        if (subTaskProject.doneSubTask!!) {
            binding.txtDedlineDateTime.setText("پروژه تکمیل \nشده است")
            valueDay = subTaskProject.dayCreation.toString()
            valueMonth = subTaskProject.monthCreation.toString()
            valueYear = subTaskProject.yearCreation.toString()
            valueCalendar = subTaskProject.valueCalendar
            binding.btnCalendar.visibility = View.GONE
        } else {
            valueDay = subTaskProject.dayCreation.toString()
            valueMonth = subTaskProject.monthCreation.toString()
            valueYear = subTaskProject.yearCreation.toString()
            valueCalendar = subTaskProject.valueCalendar
            binding.txtDedlineDateTime.text = subTaskProject.valueCalendar
        }
        binding.edtVolumeTask.setText(subTaskProject.volumeTask.toString())
        binding.edtDescriptionTask.setText(subTaskProject.descriptionSubTask)
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
                    valueDay  = date.day.toString()
                    valueMonth= date.month.toString()
                    valueYear= date.year.toString()
                    binding.txtDedlineDateTime.text = valueCalendar
                }
            }
        )
    }

    fun onSubTaskToProject() {
        parentFragmentManager.beginTransaction()
            .detach(this@ProjectUpdateSubTaskFromSubTaskBottomsheetFragment)
            .replace(
                R.id.layout_pro_and_emp,
                ProjectSubTaskFragment(
                    project,
                    projectDao,
                    position,
                    bindingActivityProAndEmp,
                    subTaskProjectDao,
                )
            ).commit()
    }

    private fun addNewTask() {
        if (
            binding.edtNameTask.length() > 0 &&
            binding.edtDescriptionTask.length() > 0 &&
            binding.edtVolumeTask.length() > 0 &&
            binding.txtDedlineDateTime.length() > 0
        ) {
            val txtTask = binding.edtNameTask.text.toString()
            val txtDescription = binding.edtDescriptionTask.text.toString()
            val txtDate = valueCalendar
            val txtVolume = binding.edtVolumeTask.text.toString()

            val today = com.kizitonwose.calendarview.utils.persian.PersianCalendar()
            val startDate =
                DateTime(subTaskProject.yearCreation, subTaskProject.monthCreation , subTaskProject.dayCreation, 0, 0, 0)
            val endDate = DateTime(
                valueYear.toInt() ,
                valueMonth.toInt(),
                valueDay.toInt(),
                0,
                0,
                0
            )
            var daysBetween = Days.daysBetween(startDate, endDate).days

            val newSubTask = SubTaskProject(
                idSubTask = subTaskProject.idSubTask,
                idProject = project.idProject!!,
                nameSubTask = txtTask,
                descriptionSubTask = txtDescription,
                volumeTask = txtVolume.toInt(),
                doneSubTask = subTaskProject.doneSubTask,
                dayDeadline = valueDay.toInt(),
                monthDeadline = valueMonth.toInt(),
                yearDeadline = valueYear.toInt(),
                dayCreation = subTaskProject.dayCreation,
                monthCreation = subTaskProject.monthCreation,
                yearCreation = subTaskProject.yearCreation,
                valueCalendar = valueCalendar.toString(),
                deadlineTask = daysBetween,
                dayDone = subTaskProject.dayDone,
                monthDone = subTaskProject.monthDone,
                yearDone = subTaskProject.yearDone
            )
            subTaskProjectDao.update(newSubTask)
            subTaskProjectAdapter.updateTask(newSubTask, position)

            val transaction =
                (activity as ProAndEmpActivity).supportFragmentManager.beginTransaction()
            transaction.replace(
                R.id.layout_pro_and_emp, ProjectSubTaskFragment(
                    project,
                    projectDao,
                    position,
                    bindingActivityProAndEmp,
                    subTaskProjectDao,
                )
            )
                .addToBackStack(null)
                .commit()
            dismiss()
        } else {
            Toast.makeText(context, "لطفا همه مقادیر را وارد کنید", Toast.LENGTH_SHORT).show()
        }
    }
}