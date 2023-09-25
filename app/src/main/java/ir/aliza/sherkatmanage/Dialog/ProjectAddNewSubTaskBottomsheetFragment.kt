package ir.aliza.sherkatmanage.Dialog

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
import ir.aliza.sherkatmanage.DataBase.Project
import ir.aliza.sherkatmanage.DataBase.ProjectDao
import ir.aliza.sherkatmanage.DataBase.SubTaskProject
import ir.aliza.sherkatmanage.DataBase.SubTaskProjectDao
import ir.aliza.sherkatmanage.ProAndEmpActivity
import ir.aliza.sherkatmanage.R
import ir.aliza.sherkatmanage.adapter.SubTaskProjectAdapter
import ir.aliza.sherkatmanage.databinding.ActivityProAndEmpBinding
import ir.aliza.sherkatmanage.databinding.BottomsheetfragmentSubtaskProjectBinding
import ir.aliza.sherkatmanage.fgmSub.ProjectSubTaskFragment
import org.joda.time.DateTime
import org.joda.time.Days

class ProjectAddNewSubTaskBottomsheetFragment(
    val subTaskProjectDao: SubTaskProjectDao,
    val project: Project,
    val subTaskProjectAdapter: SubTaskProjectAdapter,
    val projectDao: ProjectDao,
    val position: Int,
    val bindingActivityProAndEmp: ActivityProAndEmpBinding,
) : BottomSheetDialogFragment() {

    lateinit var binding: BottomsheetfragmentSubtaskProjectBinding

    var valueCalendar :PersianCalendar? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomsheetfragmentSubtaskProjectBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.sheetBtnDone.setOnClickListener {
            addNewTask()
        }

        binding.btnCalendar.setOnClickListener {
            onCreateCalendar()
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
        datePicker.isCancelable

        datePicker.addOnPositiveButtonClickListener(
            object : MaterialPickerOnPositiveButtonClickListener<Long?> {
                @SuppressLint("SetTextI18n")
                override fun onPositiveButtonClick(selection: Long?) {
                    val date = PersianCalendar(selection!!)
                    valueCalendar = date
                    binding.txtDedlineDateTime.text = "${date.year}/${date.month + 1}/${date.day}"
                }
            }
        )

    }
    fun onSubTaskToProject() {
        parentFragmentManager.beginTransaction().detach(this@ProjectAddNewSubTaskBottomsheetFragment)
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
            val txtVolume = binding.edtVolumeTask.text.toString()

            val today = com.kizitonwose.calendarview.utils.persian.PersianCalendar()
            val startDate =
                DateTime(today.persianYear, today.persianMonth , today.persianDay, 0, 0, 0)
            val endDate = DateTime(
                valueCalendar!!.year,
                valueCalendar!!.month,
                valueCalendar!!.day,
                0,
                0,
                0
            )
            var daysBetween = Days.daysBetween(startDate, endDate).days

            val newSubTask = SubTaskProject(

                idProject = project.idProject!!,
                nameSubTask = txtTask,
                descriptionSubTask = txtDescription,
                volumeTask = txtVolume.toInt(),
                dayDeadline = valueCalendar!!.day,
                monthDeadline = valueCalendar!!.month,
                yearDeadline = valueCalendar!!.year,
                dayCreation = today.persianDay,
                monthCreation = today.persianMonth,
                yearCreation = today.persianYear,
                valueCalendar = "${valueCalendar!!.year}/${valueCalendar!!.month + 1}/${valueCalendar!!.day}",
                deadlineTask = daysBetween
                )
            subTaskProjectDao.insert(newSubTask)
            subTaskProjectAdapter.addTask(newSubTask)

            val project1 = projectDao.getProject(project.idProject)

            val numberSubTaskProject = project1!!.numberSubTaskProject!! + 1
            val oldVolumeProject = project1.totalVolumeProject
            val sumVolumeProject = txtVolume.toInt() + oldVolumeProject!!.toInt()
            var efficiencyProject = 0

                efficiencyProject =
                    ((project1.doneVolumeProject!!.toDouble() / sumVolumeProject) * 100).toInt()


            val newProject = Project(
                idProject = project1.idProject,
                nameProject = project1.nameProject,
                noDeadlineProject = project1.noDeadlineProject,
                dayCreation = project1.dayCreation,
                monthCreation = project1.monthCreation,
                yearCreation = project1.yearCreation,
                valueCalendar = project1.valueCalendar,
                deadlineTask = project1.deadlineTask,
                doneProject = project1.doneProject,
                typeProject = project1.typeProject,
                descriptionProject = project1.descriptionProject,
                numberSubTaskProject = numberSubTaskProject,
                numberDoneSubTaskProject = project1.numberDoneSubTaskProject,
                progressProject = efficiencyProject,
                budgetProject = project.budgetProject,
                totalVolumeProject = sumVolumeProject.toInt(),
                doneVolumeProject = project1.doneVolumeProject,
                settled = project1.settled
            )
            projectDao.update(newProject)

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
            onSubTaskToProject()
        } else {
            Toast.makeText(context, "لطفا همه مقادیر را وارد کنید", Toast.LENGTH_SHORT).show()
        }
    }
}