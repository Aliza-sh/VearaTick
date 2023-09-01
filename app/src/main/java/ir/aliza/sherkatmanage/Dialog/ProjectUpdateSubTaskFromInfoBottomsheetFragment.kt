package ir.aliza.sherkatmanage.Dialog

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
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
import ir.aliza.sherkatmanage.DataBase.SubTaskProject
import ir.aliza.sherkatmanage.DataBase.SubTaskProjectDao
import ir.aliza.sherkatmanage.ProAndEmpActivity
import ir.aliza.sherkatmanage.R
import ir.aliza.sherkatmanage.adapter.SubTaskProjectAdapter
import ir.aliza.sherkatmanage.databinding.ActivityProAndEmpBinding
import ir.aliza.sherkatmanage.databinding.BottomsheetfragmentSubtaskProjectBinding
import ir.aliza.sherkatmanage.databinding.FragmentDialogDeadlineBinding
import ir.aliza.sherkatmanage.fgmSub.ProjectInformationFragment
import ir.aliza.sherkatmanage.fgmSub.ProjectSubTaskFragment

class ProjectUpdateSubTaskFromInfoBottomsheetFragment(
    val subTaskProjectDao: SubTaskProjectDao,
    val project: Project,
    val subTaskProjectAdapter: SubTaskProjectAdapter,
    val projectDao: ProjectDao,
    val position: Int,
    val bindingActivityProAndEmp: ActivityProAndEmpBinding,
    val subTaskProject: SubTaskProject
) : BottomSheetDialogFragment() {

    lateinit var binding: BottomsheetfragmentSubtaskProjectBinding
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
        binding = BottomsheetfragmentSubtaskProjectBinding.inflate(layoutInflater, container, false)
        bindingDialogView = FragmentDialogDeadlineBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setdata()

        binding.sheetBtnDone.setOnClickListener {
            addNewTask()
            onSubTaskToProject()
        }

        binding.btnCalendar.setOnClickListener {
            showDeadlineDialog()
        }

    }

    private fun setdata() {

        binding.edtNameTask.setText(subTaskProject.nameSubTask)

        if (subTaskProject.doneSubTask!!) {
            binding.txtDedlineDateTime.setText("پروژه تکمیل \nشده است")
            valueBtnNoDate = subTaskProject.noDeadlineSubTask!!
            valueWatch = subTaskProject.watchDeadlineSubTask!!
            valueCalendar = subTaskProject.dateDeadlineSubTask!!
            binding.btnCalendar.visibility = View.GONE
        } else {
            if (subTaskProject.noDeadlineSubTask!!) {
                valueBtnNoDate = subTaskProject.noDeadlineSubTask
                binding.txtDedlineDateTime.setText("پروژه ددلاین \nندارد")
            } else {
                valueWatch = subTaskProject.watchDeadlineSubTask!!
                valueCalendar = subTaskProject.dateDeadlineSubTask!!
                binding.txtDedlineDateTime.setText("${subTaskProject.watchDeadlineSubTask} \n${subTaskProject.dateDeadlineSubTask}")
            }
        }
        binding.edtVolumeTask.setText(subTaskProject.volumeTask.toString())
        binding.edtDescriptionTask.setText(subTaskProject.descriptionSubTask)
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
    fun onSubTaskToProject() {
        parentFragmentManager.beginTransaction()
            .detach(this@ProjectUpdateSubTaskFromInfoBottomsheetFragment)
            .replace(
                R.id.layout_pro_and_emp,
                ProjectInformationFragment(
                    project,
                    project.watchDeadlineProject!!,
                    project.dateDeadlineProject!!,
                    subTaskProjectDao,
                    projectDao,
                    position,bindingActivityProAndEmp
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
            val noDeadline = valueBtnNoDate
            val txtWatch = valueWatch
            val txtDate = valueCalendar
            val txtVolume = binding.edtVolumeTask.text.toString()

            val newSubTask = SubTaskProject(
                idSubTask = subTaskProject.idSubTask,
                idProject = project.idProject!!,
                nameSubTask = txtTask,
                noDeadlineSubTask = noDeadline,
                descriptionSubTask = txtDescription,
                watchDeadlineSubTask = txtWatch,
                dateDeadlineSubTask = txtDate,
                volumeTask = txtVolume.toInt(),
                doneSubTask = subTaskProject.doneSubTask
            )
            subTaskProjectDao.update(newSubTask)
            subTaskProjectAdapter.updateTask(newSubTask,position)

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