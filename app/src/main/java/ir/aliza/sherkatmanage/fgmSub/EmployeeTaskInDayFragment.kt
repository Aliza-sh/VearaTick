package ir.aliza.sherkatmanage.fgmSub

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
import androidx.activity.OnBackPressedCallback
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kizitonwose.calendarview.utils.persian.PersianCalendar
import ir.aliza.sherkatmanage.DataBase.EfficiencyDao
import ir.aliza.sherkatmanage.DataBase.EfficiencyEmployee
import ir.aliza.sherkatmanage.DataBase.Employee
import ir.aliza.sherkatmanage.DataBase.EmployeeDao
import ir.aliza.sherkatmanage.DataBase.TaskEmployee
import ir.aliza.sherkatmanage.DataBase.TaskEmployeeDao
import ir.aliza.sherkatmanage.Dialog.EmployeeTaskBottomsheetFragment
import ir.aliza.sherkatmanage.Dialog.EmployeeUpdateTaskBottomsheetFragment
import ir.aliza.sherkatmanage.R
import ir.aliza.sherkatmanage.adapter.TaskEmployeeAdapter
import ir.aliza.sherkatmanage.databinding.ActivityProAndEmpBinding
import ir.aliza.sherkatmanage.databinding.FragmentDialogDeleteSubtaskProjectBinding
import ir.aliza.sherkatmanage.databinding.FragmentEmployeeTaskInDayBinding
import ir.aliza.sherkatmanage.databinding.ItemSubTaskBinding
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
    var numberDonSubTaskProject = 0

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

        binding.txtClickedDay.text =
            " ${selectedDate.persianWeekDayName} ${selectedDate.persianDay} ${selectedDate.persianMonthName} ${selectedDate.persianYear}"

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
                    this, taskEmployeeDao, employee, today
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

                isScrollingUp = dy < 0
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

    override fun onMenuItemClick(subTask: TaskEmployee, position: Int) {

        val onClickSubTask = taskEmployeeDao.getOnClickTaskEmployee(subTask.idTask!!)

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
    }

    private fun doneTask(onClickSubTask: TaskEmployee, doneMenuItem: MenuItem) {
        if (onClickSubTask.doneTask!!) {

            doneMenuItem.title = "تکمیل شد"

            bindingItemSubTask.txtDedlineSubTask.visibility = View.VISIBLE
            bindingItemSubTask.imgDone.visibility = View.GONE

            val efficiencyEmployee =
                efficiencyEmployeeDao.getEfficiencyEmployee(onClickSubTask.idEmployee)

            var efficiencyWeekDuties = 0
            var gradeDuties = onClickSubTask.volumeTask

            if (onClickSubTask.deadlineTask == 0) {
                efficiencyWeekDuties = 100
            } else {
                efficiencyWeekDuties = (onClickSubTask.deadlineTask!! * 100)
            }

            val newTask = TaskEmployee(
                idTask = onClickSubTask.idTask,
                idEmployee = onClickSubTask.idEmployee,
                nameTask = onClickSubTask.nameTask,
                descriptionTask = onClickSubTask.descriptionTask,
                volumeTask = onClickSubTask.volumeTask,
                doneTask = false,
                yearCreation = onClickSubTask.yearCreation,
                monthCreation = onClickSubTask.monthCreation,
                dayCreation = onClickSubTask.dayCreation,
                deadlineTask = 0,
            )
            taskEmployeeDao.update(newTask)

            val newEfficiencyEmployee = EfficiencyEmployee(
                idEfficiency = efficiencyEmployee!!.idEfficiency,
                idEmployee = efficiencyEmployee.idEmployee,
                mustWeekWatch = efficiencyEmployee.mustWeekWatch,
                numberDay = efficiencyEmployee.numberDay,
                totalWeekWatch = efficiencyEmployee.totalWeekWatch,
                totalWatch = efficiencyEmployee.totalWatch,
                efficiencyWeekPresence = efficiencyEmployee.efficiencyWeekPresence,
                efficiencyTotalPresence = efficiencyEmployee.efficiencyTotalPresence,
                totalWeekDuties = efficiencyEmployee.totalWeekDuties!! - gradeDuties,
                totalMonthDuties = efficiencyEmployee.totalMonthDuties,
                totalDuties = efficiencyEmployee.totalDuties,
                efficiencyWeekDuties = efficiencyEmployee.efficiencyWeekDuties!! - efficiencyWeekDuties,
                efficiencyTotalDuties = efficiencyEmployee.efficiencyTotalDuties,
                efficiencyTotal = efficiencyEmployee.efficiencyTotal,
                totalMonthWatch = efficiencyEmployee.totalMonthWatch
            )
            efficiencyEmployeeDao.update(newEfficiencyEmployee)

        } else {

            doneMenuItem.title = "تکمیل نشد"

            bindingItemSubTask.txtDedlineSubTask.visibility = View.GONE
            bindingItemSubTask.imgDone.visibility = View.VISIBLE

            val efficiencyEmployee =
                efficiencyEmployeeDao.getEfficiencyEmployee(onClickSubTask.idEmployee)

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
            var daysBetween = Days.daysBetween(startDate, endDate).days

            val newTask = TaskEmployee(
                idTask = onClickSubTask.idTask,
                idEmployee = onClickSubTask.idEmployee,
                nameTask = onClickSubTask.nameTask,
                descriptionTask = onClickSubTask.descriptionTask,
                volumeTask = onClickSubTask.volumeTask,
                doneTask = true,
                deadlineTask = daysBetween,
                yearCreation = onClickSubTask.yearCreation,
                monthCreation = onClickSubTask.monthCreation,
                dayCreation = onClickSubTask.dayCreation,
            )
            taskEmployeeDao.update(newTask)

            var efficiencyWeekDuties = 0
            var gradeDuties = onClickSubTask.volumeTask

            if (onClickSubTask.deadlineTask == 0) {
                efficiencyWeekDuties = 100
            } else {
                efficiencyWeekDuties = (onClickSubTask.deadlineTask!! * 100)
            }

            val newEfficiencyEmployee = EfficiencyEmployee(
                idEfficiency = efficiencyEmployee!!.idEfficiency,
                idEmployee = efficiencyEmployee.idEmployee,
                mustWeekWatch = efficiencyEmployee.mustWeekWatch,
                numberDay = efficiencyEmployee.numberDay,
                totalWeekWatch = efficiencyEmployee.totalWeekWatch,
                totalWatch = efficiencyEmployee.totalWatch,
                efficiencyWeekPresence = efficiencyEmployee.efficiencyWeekPresence,
                efficiencyTotalPresence = efficiencyEmployee.efficiencyTotalPresence,
                totalWeekDuties = efficiencyEmployee.totalWeekDuties!! + gradeDuties,
                totalMonthDuties = efficiencyEmployee.totalMonthDuties,
                totalDuties = efficiencyEmployee.totalDuties,
                efficiencyWeekDuties = efficiencyEmployee.efficiencyWeekDuties!! + efficiencyWeekDuties,
                efficiencyTotalDuties = efficiencyEmployee.efficiencyTotalDuties,
                efficiencyTotal = efficiencyEmployee.efficiencyTotal,
                totalMonthWatch = efficiencyEmployee.totalMonthWatch
            )
            efficiencyEmployeeDao.update(newEfficiencyEmployee)
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

        val efficiencyEmployee =
            efficiencyEmployeeDao.getEfficiencyEmployee(onClickSubTask.idEmployee)

        var efficiencyWeekDuties = 0
        var gradeDuties = onClickSubTask.volumeTask

        if (onClickSubTask.deadlineTask == 0) {
            efficiencyWeekDuties = 100
        } else {
            efficiencyWeekDuties = (onClickSubTask.deadlineTask!! * 100)
        }

        val newTask = TaskEmployee(
            idTask = onClickSubTask.idTask,
            idEmployee = onClickSubTask.idEmployee,
            nameTask = onClickSubTask.nameTask,
            descriptionTask = onClickSubTask.descriptionTask,
            volumeTask = onClickSubTask.volumeTask,
            doneTask = onClickSubTask.doneTask,
            yearCreation = onClickSubTask.yearCreation,
            monthCreation = onClickSubTask.monthCreation,
            dayCreation = onClickSubTask.dayCreation,
            deadlineTask = onClickSubTask.deadlineTask,
        )
        taskEmployeeDao.delete(newTask)
        taskEmployeeAdapter.removeEmployee(newTask, position)

        val newEfficiencyEmployee = EfficiencyEmployee(
            idEfficiency = efficiencyEmployee!!.idEfficiency,
            idEmployee = efficiencyEmployee.idEmployee,
            mustWeekWatch = efficiencyEmployee.mustWeekWatch,
            numberDay = efficiencyEmployee.numberDay,
            totalWeekWatch = efficiencyEmployee.totalWeekWatch,
            totalWatch = efficiencyEmployee.totalWatch,
            efficiencyWeekPresence = efficiencyEmployee.efficiencyWeekPresence,
            efficiencyTotalPresence = efficiencyEmployee.efficiencyTotalPresence,
            totalWeekDuties = efficiencyEmployee.totalWeekDuties!! - gradeDuties,
            totalMonthDuties = efficiencyEmployee.totalMonthDuties,
            totalDuties = efficiencyEmployee.totalDuties,
            efficiencyWeekDuties = efficiencyEmployee.efficiencyWeekDuties!! - efficiencyWeekDuties,
            efficiencyTotalDuties = efficiencyEmployee.efficiencyTotalDuties,
            efficiencyTotal = efficiencyEmployee.efficiencyTotal,
            totalMonthWatch = efficiencyEmployee.totalMonthWatch
        )
        efficiencyEmployeeDao.update(newEfficiencyEmployee)
    }
}