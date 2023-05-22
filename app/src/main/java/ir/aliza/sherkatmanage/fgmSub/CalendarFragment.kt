package ir.aliza.sherkatmanage.fgmSub

import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.nextMonth
import com.kizitonwose.calendar.core.previousMonth
import com.kizitonwose.calendar.sample.shared.generateFlights
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.kizitonwose.calendar.view.ViewContainer
import ir.aliza.sherkatmanage.DataBase.AppDatabase
import ir.aliza.sherkatmanage.R
import ir.aliza.sherkatmanage.adapter.InOutAdapter
import ir.aliza.sherkatmanage.databinding.CalendarHeaderBinding
import ir.aliza.sherkatmanage.databinding.FragmentCalendarBinding
import ir.aliza.sherkatmanage.databinding.ItemCalendarDayBinding
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

class CalendarFragment : Fragment() {

    lateinit var binding: FragmentCalendarBinding
    var selectedDate: LocalDate? = null
    lateinit var calendarAdapter: InOutAdapter

    @RequiresApi(Build.VERSION_CODES.O)
    private val month = LocalDate.now()

    @RequiresApi(Build.VERSION_CODES.O)
    private var monthValue = month.monthValue

    @RequiresApi(Build.VERSION_CODES.O)
    private val flight = generateFlights().groupBy { it.time.toLocalDate() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalendarBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val timeDao = AppDatabase.getDataBase(view.context).timeDao
        val timeData = timeDao.getAllTime()
        calendarAdapter = InOutAdapter(ArrayList())
        binding.rcvInOut.adapter = calendarAdapter
        calendarAdapter.notifyDataSetChanged()

        val daysOfWeek = daysOfWeek()
        var currentMonth = YearMonth.now()
        var startMonth = currentMonth.minusMonths(10)
        var endMonth = currentMonth.plusMonths(10)
        configureBinders(daysOfWeek)

        binding.exFiveCalendar.setup(startMonth, endMonth, DayOfWeek.TUESDAY)
        binding.exFiveCalendar.scrollToMonth(currentMonth)

        setmonth()

        binding.exFiveCalendar.monthScrollListener = { month ->

            selectedDate?.let {
                // Clear selection if we scroll to a new month.
                selectedDate = null
                binding.exFiveCalendar.notifyDateChanged(it)
                updateAdapterForDate(null)
            }
        }

        binding.exFiveNextMonthImage.setOnClickListener {
            nextMonth()
            binding.exFiveCalendar.findFirstVisibleMonth()?.let {
                binding.exFiveCalendar.smoothScrollToMonth(it.yearMonth.nextMonth)
            }
        }

        binding.exFivePreviousMonthImage.setOnClickListener {
            backmonth()
            binding.exFiveCalendar.findFirstVisibleMonth()?.let {
                binding.exFiveCalendar.smoothScrollToMonth(it.yearMonth.previousMonth)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)

    private fun nextMonth() {
        var months: String = ""

        monthValue++
        if (monthValue == 13)
            monthValue = 1

        if ("1" == monthValue.toString())
            months = "دی"
        if ("2" == monthValue.toString())
            months = "بهمن"
        if ("3" == monthValue.toString())
            months = "اسفند"
        if ("4" == monthValue.toString())
            months = "فروردین"
        if ("5" == monthValue.toString())
            months = "اردیبهشت"
        if ("6" == monthValue.toString())
            months = "خرداد"
        if ("7" == monthValue.toString())
            months = "تیر"
        if ("8" == monthValue.toString())
            months = "مرداد"
        if ("9" == monthValue.toString())
            months = "شهریور"
        if ("10" == monthValue.toString())
            months = "مهر"
        if ("11" == monthValue.toString())
            months = "آبان"
        if ("12" == monthValue.toString())
            months = "آذر"
        binding.txtYM.text = months
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setmonth() {
        val month = LocalDate.now()
        var months: String = ""
        if ("1" == month.monthValue.toString())
            months = "دی"
        if ("2" == month.monthValue.toString())
            months = "بهمن"
        if ("3" == month.monthValue.toString())
            months = "اسفند"
        if ("4" == month.monthValue.toString())
            months = "فروردین"
        if ("5" == month.monthValue.toString())
            months = "اردیبهشت"
        if ("6" == month.monthValue.toString())
            months = "خرداد"
        if ("7" == month.monthValue.toString())
            months = "تیر"
        if ("8" == month.monthValue.toString())
            months = "مرداد"
        if ("9" == month.monthValue.toString())
            months = "شهریور"
        if ("10" == month.monthValue.toString())
            months = "مهر"
        if ("11" == month.monthValue.toString())
            months = "آبان"
        if ("12" == month.monthValue.toString())
            months = "آذر"
        binding.txtYM.text = months

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun backmonth() {

        var months: String = ""

        monthValue--
        if (monthValue == 0)
            monthValue = 12

        if ("1" == monthValue.toString())
            months = "دی"
        if ("2" == monthValue.toString())
            months = "بهمن"
        if ("3" == monthValue.toString())
            months = "اسفند"
        if ("4" == monthValue.toString())
            months = "فروردین"
        if ("5" == monthValue.toString())
            months = "اردیبهشت"
        if ("6" == monthValue.toString())
            months = "خرداد"
        if ("7" == monthValue.toString())
            months = "تیر"
        if ("8" == monthValue.toString())
            months = "مرداد"
        if ("9" == monthValue.toString())
            months = "شهریور"
        if ("10" == monthValue.toString())
            months = "مهر"
        if ("11" == monthValue.toString())
            months = "آبان"
        if ("12" == monthValue.toString())
            months = "آذر"
        binding.txtYM.text = months

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateAdapterForDate(date: LocalDate?) {
        calendarAdapter.data.clear()
        //calendarAdapter.data.addAll(flights[date].orEmpty())
        calendarAdapter.notifyDataSetChanged()
    }

    private fun configureBinders(daysOfWeek: List<DayOfWeek>) {
        @RequiresApi(Build.VERSION_CODES.O)
        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay // Will be set when this container is bound.
            val binding1 = ItemCalendarDayBinding.bind(view)

            init {
                daysOfWeek.reversed()
                view.setOnClickListener {
                    if (day.position == DayPosition.MonthDate) {
                        if (selectedDate != day.date) {
                            val oldDate = selectedDate
                            selectedDate = day.date
                            binding.exFiveCalendar.notifyDateChanged(day.date)
                            oldDate?.let { binding.exFiveCalendar.notifyDateChanged(it) }
                            updateAdapterForDate(day.date)
                        }
                    }
                }
            }

            init {
                view.setOnLongClickListener {

                    var days: String = ""

                    if ("TUESDAY" == day.date.dayOfWeek.toString())
                        days = "شنبه"
                    if ("WEDNESDAY" == day.date.dayOfWeek.toString())
                        days = "یک شنبه"
                    if ("THURSDAY" == day.date.dayOfWeek.toString())
                        days = "دو شنبه"
                    if ("FRIDAY" == day.date.dayOfWeek.toString())
                        days = "سه شنبه"
                    if ("SATURDAY" == day.date.dayOfWeek.toString())
                        days = "چهار شنبه"
                    if ("SUNDAY" == day.date.dayOfWeek.toString())
                        days = " پنج شنبه"
                    if ("MONDAY" == day.date.dayOfWeek.toString())
                        days = "جمعه"

                    Toast.makeText(
                        context,
                        "$days کارمندت باید بیاد شرکت ",
                        Toast.LENGTH_SHORT
                    ).show()

                    binding1.viewDayMain.setBackgroundColor(view.context.getColor(R.color.firoze))
                    true
                }
            }
        }
        binding.exFiveCalendar.dayBinder = object : MonthDayBinder<DayViewContainer> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun create(view: View) = DayViewContainer(view)

            @RequiresApi(Build.VERSION_CODES.O)
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.day = data
                val context = container.binding1.root.context
                val textView = container.binding1.exFiveDayText
                val layout = container.binding1.exFiveDayLayout
                textView.text = data.date.dayOfMonth.toString()

                val dayMain = container.binding1.viewDayMain
                val daysub = container.binding1.viewDaySub
                dayMain.background = null
                daysub.background = null

                if (data.position == DayPosition.MonthDate) {

                    layout.setBackgroundResource(if (selectedDate == data.date) R.drawable.shape_selected_bg else 0)

                    val time = flight[data.date]
                    if (time != null) {
                        if (time.count() == 1) {
                            // daysub.setBackgroundColor(context.getColor(time[0].color))
                        } else {
                            // dayMain.setBackgroundColor(context.getColor(time[0].color))
                            // daysub.setBackgroundColor(context.getColor(time[1].color))
                        }
                    }
                } else {
                    textView.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.example_5_text_grey_light
                        )
                    )
                    layout.background = null
                }

            }
        }

        class MonthViewContainer(view: View) : ViewContainer(view) {
            val legendLayout = CalendarHeaderBinding.bind(view).legendLayout.root
        }

        binding.exFiveCalendar.monthHeaderBinder =
            object : MonthHeaderFooterBinder<MonthViewContainer> {
                override fun create(view: View) = MonthViewContainer(view)

                @RequiresApi(Build.VERSION_CODES.O)
                override fun bind(container: MonthViewContainer, data: CalendarMonth) {
                    // Setup each header day text if we have not done that already.
                    if (container.legendLayout.tag == null) {
                        container.legendLayout.tag = data.yearMonth
                        container.legendLayout.children.map { it as TextView }
                            .forEachIndexed { index, tv ->

                                when (daysOfWeek[index]) {
                                    DayOfWeek.SUNDAY -> tv.text = "شنبه"
                                    DayOfWeek.MONDAY -> tv.text = "یک شنبه"
                                    DayOfWeek.TUESDAY -> tv.text = "دو شنبه"
                                    DayOfWeek.WEDNESDAY -> tv.text = "سه شنبه"
                                    DayOfWeek.THURSDAY -> tv.text = "چهار شنبه"
                                    DayOfWeek.FRIDAY -> tv.text = "پنج شنبه"
                                    DayOfWeek.SATURDAY -> tv.text = "جمعه"
                                }

                                tv.setTextColor(
                                    ContextCompat.getColor(
                                        view!!.context,
                                        R.color.white
                                    )
                                )
                                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                            }
                    }
                }
            }
    }
}