package com.vearad.vearatick.fgmSub

import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.LinearInterpolator
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.kizitonwose.calendarview.utils.persian.PersianCalendar
import com.vearad.vearatick.DataBase.EfficiencyDao
import com.vearad.vearatick.DataBase.Employee
import com.vearad.vearatick.DataBase.EmployeeDao
import com.vearad.vearatick.DataBase.TaskEmployee
import com.vearad.vearatick.DataBase.TaskEmployeeDao
import com.vearad.vearatick.Dialog.EmployeeTaskBottomsheetFragment
import com.vearad.vearatick.Dialog.EmployeeUpdateTaskBottomsheetFragment
import com.vearad.vearatick.R
import com.vearad.vearatick.adapter.TaskEmployeeAdapter
import com.vearad.vearatick.databinding.ActivityProAndEmpBinding
import com.vearad.vearatick.databinding.FragmentDialogDeleteSubtaskProjectBinding
import com.vearad.vearatick.databinding.FragmentEmployeeTaskInDayBinding
import com.vearad.vearatick.databinding.ItemSubTaskBinding
import org.joda.time.DateTime
import org.joda.time.Days

class EmployeeTaskInDayFragment(
    var employee: Employee,
    val employeeDao: EmployeeDao,
    val efficiencyEmployeeDao: EfficiencyDao,
    val position: Int,
    val taskEmployeeDao: TaskEmployeeDao,
    val selectedDate: PersianCalendar,
    val today: PersianCalendar,
    val bindingActivityProAndEmpBinding: ActivityProAndEmpBinding,

    ) : Fragment(), TaskEmployeeAdapter.TaskEvent {

    lateinit var binding: FragmentEmployeeTaskInDayBinding
    lateinit var bindingItemSubTask: ItemSubTaskBinding
    lateinit var bindingDialogDeleteSubtaskProject: FragmentDialogDeleteSubtaskProjectBinding
    lateinit var taskEmployeeAdapter: TaskEmployeeAdapter
    private var isScrollingUp = false
    private var buttonAnimator: ObjectAnimator? = null
    private var snackbar: Snackbar? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEmployeeTaskInDayBinding.inflate(layoutInflater, container, false)
        bindingItemSubTask = ItemSubTaskBinding.inflate(layoutInflater, container, false)
        bindingDialogDeleteSubtaskProject =
            FragmentDialogDeleteSubtaskProjectBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBackPressed()
        btnOnScroll()
        setData()

        val startDate =
            DateTime(today.persianYear, today.persianMonth, today.persianDay, 0, 0, 0)
        val endDate = DateTime(
            selectedDate.persianYear,
            selectedDate.persianMonth,
            selectedDate.persianDay,
            0,
            0,
            0
        )
        var daysBetween = Days.daysBetween(startDate, endDate).days

        binding.txtClickedDay.text =
            " ${selectedDate.persianWeekDayName} ${selectedDate.persianDay} ${selectedDate.persianMonthName} \n $daysBetween روز"

        binding.btnBck.setOnClickListener {
            parentFragmentManager.beginTransaction().detach(this@EmployeeTaskInDayFragment)
                .replace(
                    R.id.layout_pro_and_emp, EmployeeInformationFragment(
                        employee,
                        efficiencyEmployeeDao,
                        position,
                        employeeDao,
                        bindingActivityProAndEmpBinding
                    )
                ).commit()
        }

        binding.btnFabTack.setOnClickListener {
            addNewTask()
        }

    }

    private fun setData() {
        employee = employeeDao.getEmployee(employee.idEmployee!!)!!

        val taskDay = taskEmployeeDao.getTaskDay(
            employee.idEmployee!!,
            selectedDate.persianDay
        )
        if (taskDay != null) {
            val taskData = taskEmployeeDao.getAllTaskInInDay(
                taskDay.idEmployee,
                selectedDate.persianYear,
                selectedDate.persianMonth,
                selectedDate.persianDay
            )
            taskEmployeeAdapter =
                TaskEmployeeAdapter(
                    ArrayList(taskData),
                    this, taskEmployeeDao, employee, today, efficiencyEmployeeDao
                )
            binding.recyclerView.adapter = taskEmployeeAdapter
        }

    }

    private fun addNewTask() {
        val bottomsheet = EmployeeTaskBottomsheetFragment(
            employee,
            employeeDao,
            efficiencyEmployeeDao,
            position,
            taskEmployeeDao,
            selectedDate,
            today, bindingActivityProAndEmpBinding
        )
        bottomsheet.setStyle(R.style.BottomSheetStyle, R.style.BottomSheetDialogTheme)
        bottomsheet.show(parentFragmentManager, null)
    }

    private fun btnOnScroll() {
        val layoutManager = LinearLayoutManager(context)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (isScrollingUp) {
                        animateButtonUp()
                    } else {
                        animateButtonDown()
                    }
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                isScrollingUp = dy <= 0
            }
        })
    }

    private fun animateButtonDown() {
        if (binding.btnFabTack.translationY != binding.btnFabTack.height.toFloat()) {
            buttonAnimator?.cancel()
            buttonAnimator = ObjectAnimator.ofFloat(
                binding.btnFabTack,
                "translationY",
                binding.btnFabTack.height.toFloat()
            )
            buttonAnimator?.apply {
                duration = 200
                interpolator = LinearInterpolator()
                start()
            }
        }
    }

    private fun animateButtonUp() {
        if (binding.btnFabTack.translationY != 0f) {
            buttonAnimator?.cancel()
            buttonAnimator = ObjectAnimator.ofFloat(binding.btnFabTack, "translationY", 0f)
            buttonAnimator?.apply {
                duration = 200
                interpolator = LinearInterpolator()
                start()
            }
        }
    }

    private fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    parentFragmentManager
                        .beginTransaction()
                        .detach(this@EmployeeTaskInDayFragment)
                        .replace(
                            R.id.layout_pro_and_emp, EmployeeInformationFragment(
                                employee,
                                efficiencyEmployeeDao,
                                position,
                                employeeDao,
                                bindingActivityProAndEmpBinding
                            )
                        )
                        .commit()
                }
            }
        )
    }

    override fun onResume() {
        super.onResume()
        updateYourData()
    }

    private fun updateYourData() {
        employee = employeeDao.getEmployee(employee.idEmployee!!)!!
    }

    override fun onTaskClicked(task: TaskEmployee, position: Int, day: String, monthName: String) {}

    override fun onTaskLongClicked(task: TaskEmployee, position: Int) {}

    override fun onMenuItemClick(
        subTask: TaskEmployee,
        position: Int,
    ) {

        val onClickSubTask = taskEmployeeDao.getOnClickTaskEmployee(subTask.idTask!!)

        if (onClickSubTask!!.projectTask != true) {
            val viewHolder =
                binding.recyclerView.findViewHolderForAdapterPosition(position) as TaskEmployeeAdapter.TaskEmployeeViewHolder
            viewHolder.let { holder ->
                val btnMenuSubTaskProject = holder.btnMenuTask
                val popupMenu = PopupMenu(context, btnMenuSubTaskProject)
                popupMenu.inflate(R.menu.menu_task_employee)
                popupMenu.show()

                val doneMenuItem = popupMenu.menu.findItem(R.id.menu_task_emoloyee_done)
                if (onClickSubTask!!.doneTask!!) {
                    doneMenuItem.title = "تکمیل نشد"
                } else {
                    doneMenuItem.title = "تکمیل شد"
                }

                popupMenu.setOnMenuItemClickListener { item ->

                    when (item.itemId) {

                        R.id.menu_task_emoloyee_edit -> {
                            val bottomsheet = EmployeeUpdateTaskBottomsheetFragment(
                                employee,
                                employeeDao,
                                efficiencyEmployeeDao,
                                position,
                                taskEmployeeDao,
                                selectedDate, today,
                                bindingActivityProAndEmpBinding,
                                onClickSubTask
                            )
                            bottomsheet.setStyle(
                                R.style.BottomSheetStyle,
                                R.style.BottomSheetDialogTheme
                            )
                            bottomsheet.show(parentFragmentManager, null)
                            true
                        }

                        R.id.menu_task_emoloyee_done -> {
                            doneTask(onClickSubTask, doneMenuItem)
                            setData()
                            true
                        }

                        R.id.menu_task_emoloyee_delete -> {
                            showDeleteDialog(onClickSubTask)
                            true
                        }

                        else -> false
                    }
                }
            }
        }else{
            snackbar = Snackbar.make(binding.root, "عدم اتصال به اینترنت!", Snackbar.LENGTH_SHORT)
                .setAction("اتصال") {
                    snackbar?.dismiss()
                }
            snackbar?.show()
            Toast.makeText(context, "برای این کار باید به پروژه مربوطه مراجعه کنید.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun doneTask(onClickSubTask: TaskEmployee, doneMenuItem: MenuItem) {
        if (onClickSubTask.doneTask!!) {

            doneMenuItem.title = "تکمیل شد"

            bindingItemSubTask.txtDedlineSubTask.visibility = View.VISIBLE
            bindingItemSubTask.imgDone.visibility = View.GONE

            val startDate =
                DateTime(today.persianYear, today.persianMonth, today.persianDay, 0, 0, 0)
            val endDate = DateTime(
                onClickSubTask.yearCreation,
                onClickSubTask.monthCreation,
                onClickSubTask.dayCreation,
                0,
                0,
                0
            )
            val daysBetween = Days.daysBetween(startDate, endDate).days
            var efficiencyWeekDuties = 0
            val deadlineTask = onClickSubTask.deadlineTask

            if (deadlineTask == 0) {
                if (daysBetween == 0)
                    efficiencyWeekDuties += 100
                else {
                    efficiencyWeekDuties = ((deadlineTask * 100) / deadlineTask).toInt()
                    efficiencyWeekDuties += 100
                }
            } else if (deadlineTask > 0) {
                efficiencyWeekDuties = ((daysBetween * 100) / deadlineTask).toInt()
                efficiencyWeekDuties += 100
            } else if (deadlineTask < 0) {
                efficiencyWeekDuties = ((daysBetween * 100) / -deadlineTask).toInt()
                efficiencyWeekDuties += 100
            }

            val newTask = TaskEmployee(
                idTask = onClickSubTask.idTask,
                idEmployee = onClickSubTask.idEmployee,
                idTaskProject = onClickSubTask.idTaskProject,
                nameTask = onClickSubTask.nameTask,
                descriptionTask = onClickSubTask.descriptionTask,
                volumeTask = onClickSubTask.volumeTask,
                doneTask = false,
                yearCreation = onClickSubTask.yearCreation,
                monthCreation = onClickSubTask.monthCreation,
                dayCreation = onClickSubTask.dayCreation,
                deadlineTask = onClickSubTask.deadlineTask,
                efficiencyTask = 0
            )
            taskEmployeeDao.update(newTask)

        } else {

            doneMenuItem.title = "تکمیل نشد"

            bindingItemSubTask.txtDedlineSubTask.visibility = View.GONE
            bindingItemSubTask.imgDone.visibility = View.VISIBLE


            val startDate =
                DateTime(today.persianYear, today.persianMonth, today.persianDay, 0, 0, 0)
            val endDate = DateTime(
                onClickSubTask.yearCreation,
                onClickSubTask.monthCreation,
                onClickSubTask.dayCreation,
                0,
                0,
                0
            )
            val daysBetween = Days.daysBetween(startDate, endDate).days
            var efficiencyWeekDuties = 0
            val deadlineTask = onClickSubTask.deadlineTask

            if (deadlineTask == 0) {
                if (daysBetween == 0)
                    efficiencyWeekDuties += 100
                else {
                    efficiencyWeekDuties = ((deadlineTask * 100) / deadlineTask).toInt()
                    efficiencyWeekDuties += 100
                }
            } else if (deadlineTask > 0) {
                efficiencyWeekDuties = ((daysBetween * 100) / deadlineTask).toInt()
                efficiencyWeekDuties += 100
            } else if (deadlineTask < 0) {
                efficiencyWeekDuties = ((daysBetween * 100) / -deadlineTask).toInt()
                efficiencyWeekDuties += 100
            }

            val newTask = TaskEmployee(
                idTask = onClickSubTask.idTask,
                idEmployee = onClickSubTask.idEmployee,
                idTaskProject = onClickSubTask.idTaskProject,
                nameTask = onClickSubTask.nameTask,
                descriptionTask = onClickSubTask.descriptionTask,
                volumeTask = onClickSubTask.volumeTask,
                doneTask = true,
                deadlineTask = onClickSubTask.deadlineTask,
                yearCreation = onClickSubTask.yearCreation,
                monthCreation = onClickSubTask.monthCreation,
                dayCreation = onClickSubTask.dayCreation,
                efficiencyTask = efficiencyWeekDuties
            )
            taskEmployeeDao.update(newTask)

        }
    }

    private fun showDeleteDialog(onClickSubTask: TaskEmployee) {

        val parent = bindingDialogDeleteSubtaskProject.root.parent as? ViewGroup
        parent?.removeView(bindingDialogDeleteSubtaskProject.root)
        val dialogBuilder = AlertDialog.Builder(bindingDialogDeleteSubtaskProject.root.context)
        dialogBuilder.setView(bindingDialogDeleteSubtaskProject.root)

        val alertDialog = dialogBuilder.create()
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.Transparent.toArgb()))
        alertDialog.setCancelable(false)
        alertDialog.show()
        bindingDialogDeleteSubtaskProject.dialogBtnDeleteCansel.setOnClickListener {
            alertDialog.dismiss()
        }
        bindingDialogDeleteSubtaskProject.dialogBtnDeleteSure.setOnClickListener {

            deleteItem(onClickSubTask, position)
            setData()
            alertDialog.dismiss()
        }
    }

    fun deleteItem(onClickSubTask: TaskEmployee, position: Int) {

        val newTask = TaskEmployee(
            idTask = onClickSubTask.idTask,
            idEmployee = onClickSubTask.idEmployee,
            idTaskProject = onClickSubTask.idTaskProject,
            nameTask = onClickSubTask.nameTask,
            descriptionTask = onClickSubTask.descriptionTask,
            volumeTask = onClickSubTask.volumeTask,
            doneTask = onClickSubTask.doneTask,
            yearCreation = onClickSubTask.yearCreation,
            monthCreation = onClickSubTask.monthCreation,
            dayCreation = onClickSubTask.dayCreation,
            deadlineTask = onClickSubTask.deadlineTask,
            efficiencyTask = onClickSubTask.efficiencyTask
        )
        taskEmployeeDao.delete(newTask)
        taskEmployeeAdapter.removeEmployee(newTask, position)


    }
}