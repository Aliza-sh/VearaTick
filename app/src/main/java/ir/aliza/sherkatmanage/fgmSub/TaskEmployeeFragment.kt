package ir.aliza.sherkatmanage.fgmSub

import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakewharton.threetenabp.AndroidThreeTen
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import com.kizitonwose.calendarview.utils.persian.*
import com.kizitonwose.calendarview.utils.yearMonth
import ir.aliza.sherkatmanage.DataBase.AppDatabase
import ir.aliza.sherkatmanage.DataBase.Employee
import ir.aliza.sherkatmanage.DataBase.TaskEmployee
import ir.aliza.sherkatmanage.Dialog.DeleteItemTaskDialogFragment
import ir.aliza.sherkatmanage.Dialog.TaskBottomsheetFragment
import ir.aliza.sherkatmanage.MainActivity
import ir.aliza.sherkatmanage.R
import ir.aliza.sherkatmanage.adapter.TaskEmployeeAdapter
import ir.aliza.sherkatmanage.databinding.FragmentTaskBinding
import ir.aliza.sherkatmanage.databinding.ItemCalendarDayTaskBinding
import ir.aliza.sherkatmanage.taskAdapter
import ir.aliza.sherkatmanage.taskEmployeeDao
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.YearMonth

class TaskEmployeeFragment(val employee: Employee) : Fragment(), TaskEmployeeAdapter.TaskEvent {

    var selectedDate = LocalDate.now()
    lateinit var binding: FragmentTaskBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTaskBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AndroidThreeTen.init(view.context)

        taskEmployeeDao = AppDatabase.getDataBase(view.context).TaskDao

        val this1 = this
        calendarViewCreated(this1)

        binding.btnFabTack.setOnClickListener {
            val bottomsheet = TaskBottomsheetFragment(
                employee,
                selectedDate.toPersianCalendar().persianYear,
                selectedDate.toPersianCalendar().persianMonthName,
                selectedDate.toPersianCalendar().persianDay
            )
            bottomsheet.show(parentFragmentManager, null)
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun calendarViewCreated(this1: TaskEmployeeFragment) {

        class DayViewContainer(view: View) : ViewContainer(view) {
            val binding1 = ItemCalendarDayTaskBinding.bind(view)
            val dayText = binding1.exSevenDayText
            val dateText = binding1.exSevenDateText
            val selectedView = binding1.exSevenSelectedView

            lateinit var day: CalendarDay

            init {

                view.setOnClickListener {

                    val taskDay = taskEmployeeDao.getTaskDay(
                        employee.idEmployee!!,
                        day.persianCalendar.persianDay
                    )

                    val taskData = taskEmployeeDao.getAllTaskInInDay(
                        employee.idEmployee,
                        selectedDate.toPersianCalendar().persianYear.toString(),
                        selectedDate.toPersianCalendar().persianMonthName,
                        day.persianCalendar.persianDay.toString()
                    )

                    if (day.owner == DayOwner.THIS_MONTH) {
                        if (selectedDate != day.date) {
                            val oldDate = selectedDate
                            selectedDate = day.date
                            binding.exSevenCalendar.notifyDateChanged(day.date)
                            oldDate?.let { binding.exSevenCalendar.notifyDateChanged(it) }

                            if (taskDay != null) {

                                if (taskDay.day == selectedDate.toPersianCalendar().persianDay.toString()) {

                                    taskAdapter =
                                        TaskEmployeeAdapter(ArrayList(taskData), this1,day.persianCalendar.persianDay,day.date)
                                    binding.recyclerViewDuties.adapter = taskAdapter
                                    binding.recyclerViewDuties.layoutManager =
                                        LinearLayoutManager(context)

                                } else {

                                    if (binding.recyclerViewDuties.size != 0)
                                        taskAdapter.clearAll()

                                }
                            }
                        }
                    }
                }
            }

            fun bind(day: CalendarDay) {

                val persianlCalendar = day.date.yearMonth.persianlCalendar()
                val monthTitle =
                    "${persianlCalendar.persianMonthName} ${
                        persianlCalendar.persianYear.toString().persianNumbers()
                    }"
                binding.txtYM.text = monthTitle

                this.day = day
                dateText.text = day.persianCalendar.persianDay.toString().persianNumbers()
                dayText.text = day.persianCalendar.persianWeekDayName.persianNumbers()

                val taskInDay = taskEmployeeDao.getAllTaskInDay(
                    employee.idEmployee!!,
                    day.persianCalendar.persianYear.toString(),
                    day.persianCalendar.persianMonthName,
                    day.persianCalendar.persianDay.toString()
                )

                if (taskInDay != null && taskInDay.day == day.persianCalendar.persianDay.toString()) {

                    binding1.exSevenDateText.setTextColor(
                        ContextCompat.getColor(
                            view.context,
                            R.color.firoze
                        )
                    )
                    binding1.exSevenDayText.setTextColor(
                        ContextCompat.getColor(
                            view.context,
                            R.color.firoze
                        )
                    )

                }


                val colorRes = if (day.date == selectedDate) {
                    R.color.firoze
                } else {
                    R.color.gray
                }
                binding1.exSevenDateText.setTextColor(
                    ContextCompat.getColor(
                        view.context,
                        colorRes
                    )
                )

                binding1.exSevenSelectedView.isVisible =
                    day.persianCalendar.persianDay == selectedDate.toPersianCalendar().persianDay

            }
        }

        binding.exSevenCalendar.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: CalendarDay) = container.bind(day)
        }

        val currentMonth = YearMonth.now()
        // Value for firstDayOfWeek does not matter since inDates and outDates are not generated.
        binding.exSevenCalendar.setup(
            currentMonth.minusMonths(12),
            currentMonth.plusMonths(12),
            DayOfWeek.SATURDAY
        )
        binding.exSevenCalendar.scrollToDate(LocalDate.now())

    }

    override fun onTaskClicked(
        task: TaskEmployee,
        position: Int,
        day: String,
        monthName: String
    ) {
        val transaction = (activity as MainActivity).supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_layout_main, TaskEmployeeInformationFragment(task,day,monthName))
            .addToBackStack(null)
            .commit()
    }

    override fun onTaskLongClicked(task: TaskEmployee, position: Int) {
        val dialog = DeleteItemTaskDialogFragment(task, position)
        dialog.show((activity as MainActivity).supportFragmentManager, null)
    }

}