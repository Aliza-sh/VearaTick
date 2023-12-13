package com.vearad.vearatick.fgmSub

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.jakewharton.threetenabp.AndroidThreeTen
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import com.kizitonwose.calendarview.utils.next
import com.kizitonwose.calendarview.utils.persian.*
import com.kizitonwose.calendarview.utils.previous
import com.vearad.vearatick.DataBase.AppDatabase
import com.vearad.vearatick.DataBase.EfficiencyDao
import com.vearad.vearatick.DataBase.Employee
import com.vearad.vearatick.DataBase.EmployeeDao
import com.vearad.vearatick.ProAndEmpActivity
import com.vearad.vearatick.R
import com.vearad.vearatick.databinding.ActivityProAndEmpBinding
import com.vearad.vearatick.databinding.CalendarHeaderBinding
import com.vearad.vearatick.databinding.FragmentEmployeeTaskBinding
import com.vearad.vearatick.databinding.ItemCalendarDayEmployeeTaskBinding
import com.vearad.vearatick.dayDao
import com.vearad.vearatick.taskEmployeeDao
import com.vearad.vearatick.timeDao
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.YearMonth

class EmployeeTaskFragment(
    val employee: Employee,
    val employeeDao: EmployeeDao,
    val efficiencyEmployeeDao: EfficiencyDao,
    val position: Int,
    val bindingActivityProAndEmpBinding: ActivityProAndEmpBinding,
    ) :
    Fragment() {

    private var selectedDate: LocalDate? = null
    lateinit var binding: FragmentEmployeeTaskBinding
    var oldLayout: LinearLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEmployeeTaskBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AndroidThreeTen.init(view.context)

        taskEmployeeDao = AppDatabase.getDataBase(view.context).taskDao

        calendarViewCreated(view)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun calendarViewCreated(view: View) {

        AndroidThreeTen.init(view.context)

        dayDao = AppDatabase.getDataBase(view.context).dayDao
        timeDao = AppDatabase.getDataBase(view.context).timeDao

        val currentMonth = YearMonth.now()
        binding.clrTaskEmp.setup(
            currentMonth.minusMonths(12),
            currentMonth.plusMonths(12),
            DayOfWeek.SATURDAY
        )
        binding.clrTaskEmp.scrollToDate(LocalDate.now())

        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay // Will be set when this container is bound.
            val bindingItemCalendarDayEmployeeTask = ItemCalendarDayEmployeeTaskBinding.bind(view)
            val numTaskEmp = bindingItemCalendarDayEmployeeTask.dayNumTaskEmp
            val textView = bindingItemCalendarDayEmployeeTask.txtDayEntExtEmp
            val layout = bindingItemCalendarDayEmployeeTask.lytDayEntExtEmp

            init {
                view.setOnClickListener {
                    if (day.owner == DayOwner.THIS_MONTH) {
                        if (selectedDate != day.date) {
                            val oldDate = selectedDate
                            selectedDate = day.date
                            binding.clrTaskEmp.notifyDateChanged(day.date)
                            oldDate?.let { binding.clrTaskEmp.notifyDateChanged(it) }

                            if (oldLayout != null && oldLayout != layout) {
                                oldLayout!!.setBackgroundResource(0)
                                layout.setBackgroundResource(R.drawable.shape_selected_bg)
                                oldLayout = layout
                            } else {
                                layout.setBackgroundResource(R.drawable.shape_selected_bg)
                                oldLayout = layout
                            }


                            val today = LocalDate.now().toPersianCalendar()

                            val transaction =
                                (activity as ProAndEmpActivity).supportFragmentManager.beginTransaction()
                            transaction.replace(
                                R.id.layout_pro_and_emp,
                                EmployeeTaskInDayFragment(
                                    employee,
                                    employeeDao,
                                    efficiencyEmployeeDao,
                                    employee.idEmployee!!,
                                    taskEmployeeDao,
                                    selectedDate!!.toPersianCalendar(),
                                    today
                                    ,bindingActivityProAndEmpBinding
                                )
                            )
                                .addToBackStack(null)
                                .commit()


                        }
                    }
                }
            }
        }

        binding.clrTaskEmp.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.day = day
                val textView = container.textView
                val layout = container.layout
                textView.text = day.persianCalendar.persianDay.toString().persianNumbers()
                val today = LocalDate.now().toPersianCalendar()
                if (day.owner == DayOwner.THIS_MONTH
                ) {
                    layout.setBackgroundResource(
                        if (selectedDate == day.date) R.drawable.shape_selected_bg
                        else if (day.persianCalendar.persianDay == today.persianDay) R.drawable.shape_selected_bg
                        else 0
                    )

                    val taskInDay = taskEmployeeDao.getAllTaskDayInMonth(
                        employee.idEmployee!!,
                        day.date.toPersianCalendar().persianYear,
                        day.date.toPersianCalendar().persianMonth,
                        day.date.toPersianCalendar().persianDay
                    )

                    if (selectedDate != day.date) {
                        if (taskInDay.isEmpty())
                        container.numTaskEmp.visibility = View.GONE
                        else{
                            container.numTaskEmp.visibility = View.VISIBLE
                            container.numTaskEmp.text = taskInDay.size.toString()
                        }
                    }

                } else {
                    textView.setTextColor(
                        ContextCompat.getColor(
                            view.context, R.color.example_5_text_grey_light
                        )
                    )
                    layout.background = null
                }
            }
        }
        class MonthViewContainer(view: View) : ViewContainer(view) {
            val legendLayout = CalendarHeaderBinding.bind(view).legendLayout.root
        }
        binding.clrTaskEmp.monthHeaderBinder =
            object : MonthHeaderFooterBinder<MonthViewContainer> {
                override fun create(view: View) = MonthViewContainer(view)

                @SuppressLint("SetTextI18n")
                override fun bind(container: MonthViewContainer, month: CalendarMonth) {
                    // Setup each header day text if we have not done that already.
                    if (container.legendLayout.tag == null) {
                        container.legendLayout.tag = month.yearMonth
                        container.legendLayout.children.map { it as TextView }
                            .forEachIndexed { index, tv ->
                                when (index) {
                                    0 -> tv.text =
                                        "\u0634\u0646\u0628\u0647" // Shanbeh
                                    1 -> tv.text =
                                        "\u06cc\u06a9\u200c\u0634\u0646\u0628\u0647"// Yekshanbeh
                                    2 -> tv.text =
                                        "\u062f\u0648\u0634\u0646\u0628\u0647"  // Doshanbeh
                                    3 -> tv.text =
                                        "\u0633\u0647\u200c\u0634\u0646\u0628\u0647"  // Sehshanbeh
                                    4 -> tv.text =
                                        "\u0686\u0647\u0627\u0631\u0634\u0646\u0628\u0647"  // Chaharshanbeh
                                    5 -> tv.text =
                                        "\u067e\u0646\u062c\u200c\u0634\u0646\u0628\u0647"  // Panjshanbeh
                                    6 -> tv.text =
                                        "\u062c\u0645\u0639\u0647" // jome
                                }

                            }
                        binding.clrTaskEmp.monthScrollListener = { month ->
                            val persianlCalendar = month.yearMonth.persianlCalendar()
                            val monthTitle = "${persianlCalendar.persianMonthName} ${
                                persianlCalendar.persianYear.toString().persianNumbers()
                            }"
                            binding.txtYM.text = monthTitle
                            selectedDate?.let {
                                // Clear selection if we scroll to a new month.
                                selectedDate = null
                                binding.clrTaskEmp.notifyDateChanged(it)
                            }
                        }
                        binding.exFiveNextMonthImage.setOnClickListener {
                            binding.clrTaskEmp.findFirstVisibleMonth()?.let {
                                binding.clrTaskEmp.smoothScrollToMonth(it.yearMonth.next)
                            }
                        }
                        binding.exFivePreviousMonthImage.setOnClickListener {
                            binding.clrTaskEmp.findFirstVisibleMonth()?.let {
                                binding.clrTaskEmp.smoothScrollToMonth(it.yearMonth.previous)
                            }
                        }
                    }
                }
            }
    }

}