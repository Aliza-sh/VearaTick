package ir.aliza.sherkatmanage.fgmSub

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.view.size
import androidx.fragment.app.Fragment
import com.jakewharton.threetenabp.AndroidThreeTen
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import com.kizitonwose.calendarview.utils.next
import com.kizitonwose.calendarview.utils.persian.*
import com.kizitonwose.calendarview.utils.previous
import ir.aliza.sherkatmanage.DataBase.AppDatabase
import ir.aliza.sherkatmanage.DataBase.Day
import ir.aliza.sherkatmanage.DataBase.EfficiencyDao
import ir.aliza.sherkatmanage.DataBase.EfficiencyEmployee
import ir.aliza.sherkatmanage.DataBase.Employee
import ir.aliza.sherkatmanage.Dialog.ArrivalsAndDeparturesDialogFragment
import ir.aliza.sherkatmanage.Dialog.EntryAndExitDialogFragment
import ir.aliza.sherkatmanage.MainActivity
import ir.aliza.sherkatmanage.R
import ir.aliza.sherkatmanage.adapter.InOutAdapter
import ir.aliza.sherkatmanage.databinding.CalendarHeaderBinding
import ir.aliza.sherkatmanage.databinding.FragmentCalendarBinding
import ir.aliza.sherkatmanage.databinding.FragmentDialogEntryExitBinding
import ir.aliza.sherkatmanage.databinding.ItemCalendarDayBinding
import ir.aliza.sherkatmanage.dayDao
import ir.aliza.sherkatmanage.inOutAdapter
import ir.aliza.sherkatmanage.timeDao
import org.threeten.bp.LocalDate
import org.threeten.bp.YearMonth
import java.time.DayOfWeek

class CalendarFragment(val employee: Employee, val efficiencyEmployeeDao: EfficiencyDao) : Fragment() {


    lateinit var binding: FragmentCalendarBinding
    lateinit var bindingFragmentDialogEntryExit: FragmentDialogEntryExitBinding
    private var selectedDate: LocalDate? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalendarBinding.inflate(layoutInflater, container, false)
        bindingFragmentDialogEntryExit = FragmentDialogEntryExitBinding.inflate(layoutInflater, null, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AndroidThreeTen.init(view.context)

        dayDao = AppDatabase.getDataBase(view.context).dayDao
        timeDao = AppDatabase.getDataBase(view.context).timeDao

        val currentMonth = YearMonth.now()
        binding.exFiveCalendar.setup(
            currentMonth.minusMonths(12),
            currentMonth.plusMonths(12),
            org.threeten.bp.DayOfWeek.SATURDAY
        )
        binding.exFiveCalendar.scrollToDate(LocalDate.now())

        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay // Will be set when this container is bound.
            val binding1 = ItemCalendarDayBinding.bind(view)
            val viewDaySub = binding1.viewDaySub
            val textView = binding1.exFiveDayText
            val layout = binding1.exFiveDayLayout

            init {

                view.setOnClickListener {
                    if (day.owner == DayOwner.THIS_MONTH) {
                        if (selectedDate != day.date) {
                            val oldDate = selectedDate
                            selectedDate = day.date
                            binding.exFiveCalendar.notifyDateChanged(day.date)
                            oldDate?.let { binding.exFiveCalendar.notifyDateChanged(it) }

                            val nameDay = dayDao.getAllNameDay(
                                employee.idEmployee!!,
                                day.persianCalendar.persianYear.toString(),
                                day.persianCalendar.persianMonthName,
                                selectedDate!!.toPersianCalendar().persianWeekDayName
                            )
                            //val timeAllData = timeDao.getTime(employee.idEmployee)
                            val timeDayData = timeDao.getDayTime(
                                employee.idEmployee,
                                day.persianCalendar.persianDay.toString()
                            )
                            val timeDay = timeDao.getTime(
                                employee.idEmployee,
                                selectedDate!!.toPersianCalendar().persianDay
                            )

                            val entryExit = dayDao.getAllEntryExit(
                                employee.idEmployee,
                                selectedDate!!.toPersianCalendar().persianYear.toString(),
                                selectedDate!!.toPersianCalendar().persianMonthName.toString(),
                                selectedDate!!.toPersianCalendar().persianWeekDayName.toString(),
                            )

                            if (nameDay?.nameday == selectedDate!!.toPersianCalendar().persianWeekDayName) {

                                if (timeDay?.day == selectedDate!!.toPersianCalendar().persianDay.toString()) {

                                    inOutAdapter = InOutAdapter(
                                        ArrayList(timeDayData),
                                        entryExit,
                                        day.persianCalendar.persianWeekDayName,
                                    )

                                    binding.rcvInOut.adapter = inOutAdapter
                                } else {

                                    if (binding.rcvInOut.size != 0)
                                        inOutAdapter.clearAll()

                                    Toast.makeText(
                                        context,
                                        "اطلاعاتی ثبت نشده!!!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                            } else {

                                if (binding.rcvInOut.size != 0)
                                    inOutAdapter.clearAll()

                                Toast.makeText(
                                    context,
                                    " این روز برایه کارمند انتخاب نشده!!!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        }
                    }
                }

                view.setOnLongClickListener {

                    if (day.owner == DayOwner.THIS_MONTH) {
                        if (selectedDate != day.date) {
                            val oldDate = selectedDate
                            selectedDate = day.date
                            binding.exFiveCalendar.notifyDateChanged(day.date)
                            oldDate?.let { binding.exFiveCalendar.notifyDateChanged(it) }

                            val nameDay = dayDao.getAllNameDay(
                                employee.idEmployee!!,
                                day.persianCalendar.persianYear.toString(),
                                day.persianCalendar.persianMonthName,
                                selectedDate!!.toPersianCalendar().persianWeekDayName
                            )

                            if (nameDay?.nameday == selectedDate!!.toPersianCalendar().persianWeekDayName) {
                                val dialog = ArrivalsAndDeparturesDialogFragment(
                                    binding1,
                                    employee.idEmployee,
                                    day.persianCalendar.persianYear.toString(),
                                    day.persianCalendar.persianMonthName,
                                    selectedDate!!.toPersianCalendar().persianDay,
                                    efficiencyEmployeeDao
                                )
                                dialog.isCancelable = false
                                dialog.show((activity as MainActivity).supportFragmentManager, null)
                            } else {
                                Toast.makeText(
                                    context,
                                    "این روز برایه کارمند انتخاب نشده!!!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                    true
                }
            }
        }

        binding.exFiveCalendar.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.day = day

                val textView = container.textView
                val layout = container.layout
                textView.text = day.persianCalendar.persianDay.toString().persianNumbers()

                val today = LocalDate.now()

                if (day.owner == DayOwner.THIS_MONTH) {

                    layout.setBackgroundResource(if (selectedDate == day.date) R.drawable.shape_selected_bg else if (day.persianCalendar.persianDay == today.toPersianCalendar().persianDay) R.drawable.shape_selected_bg else 0)

                    val arrivalDay = timeDao.getAllArrivalDay(
                        employee.idEmployee!!,
                        day.persianCalendar.persianYear.toString(),
                        day.persianCalendar.persianMonthName,
                        day.persianCalendar.persianDay.toString()
                    )

                    if (arrivalDay != null && arrivalDay.day == day.persianCalendar.persianDay.toString()) {

                        if (arrivalDay.arrival)
                            container.viewDaySub.setBackgroundColor(view.context.getColor(R.color.green_700))
                        else if (!arrivalDay.arrival)
                            container.viewDaySub.setBackgroundColor(context!!.getColor(R.color.red_800))
                    }

                } else {
                    textView.setTextColor(
                        ContextCompat.getColor(
                            view.context, R.color.example_5_text_grey_light
                        )
                    )
                    //layout.background = null
                }
            }
        }

        class MonthViewContainer(view: View) : ViewContainer(view) {
            val legendLayout = CalendarHeaderBinding.bind(view).legendLayout.root
        }

        binding.exFiveCalendar.monthHeaderBinder =
            object : MonthHeaderFooterBinder<MonthViewContainer> {
                override fun create(view: View) = MonthViewContainer(view)
                override fun bind(container: MonthViewContainer, month: CalendarMonth) {
                    // Setup each header day text if we have not done that already.
                    if (container.legendLayout.tag == null) {
                        container.legendLayout.tag = month.yearMonth
                        container.legendLayout.children.map { it as TextView }
                            .forEachIndexed { index, tv ->

                                val daysOfWeek = daysOfWeek()

                                when (daysOfWeek[index]) {

                                    DayOfWeek.SUNDAY -> tv.text =
                                        "\u0634\u0646\u0628\u0647" // Shanbeh
                                    DayOfWeek.MONDAY -> tv.text =
                                        "\u06cc\u06a9\u200c\u0634\u0646\u0628\u0647"// Yekshanbeh
                                    DayOfWeek.TUESDAY -> tv.text =
                                        "\u062f\u0648\u0634\u0646\u0628\u0647"  // Doshanbeh
                                    DayOfWeek.WEDNESDAY -> tv.text =
                                        "\u0633\u0647\u200c\u0634\u0646\u0628\u0647"  // Sehshanbeh
                                    DayOfWeek.THURSDAY -> tv.text =
                                        "\u0686\u0647\u0627\u0631\u0634\u0646\u0628\u0647"  // Chaharshanbeh
                                    DayOfWeek.FRIDAY -> tv.text =
                                        "\u067e\u0646\u062c\u200c\u0634\u0646\u0628\u0647"  // Panjshanbeh
                                    DayOfWeek.SATURDAY -> tv.text =
                                        "\u062c\u0645\u0639\u0647" // jome
                                }

                                val dayData =
                                    dayDao.getDay(("${tv.id}${employee.idEmployee}").toLong())

                                if ((dayData?.idDay) == ("${tv.id}${employee.idEmployee}").toLong() && dayData.idEmployee == employee.idEmployee) {
                                    tv.setBackgroundColor(
                                        ContextCompat.getColor(
                                            view.context, R.color.firoze
                                        )
                                    )
                                }

                                tv.setOnClickListener {

                                    val dayData =
                                        dayDao.getDay(("${tv.id}${employee.idEmployee}").toLong())

                                    if (dayData?.idDay != ("${tv.id}${employee.idEmployee}").toLong()) {
                                        val dialog = EntryAndExitDialogFragment(month, employee, tv,efficiencyEmployeeDao,container.legendLayout)
                                        dialog.show(
                                            (activity as MainActivity).supportFragmentManager,
                                            null
                                        )

                                    } else if (dayData.idDay == ("${tv.id}${employee.idEmployee}").toLong() && dayData.idEmployee == employee.idEmployee) {
                                        val newDay = employee.idEmployee?.let { it1 ->
                                            Day(
                                                idDay = ("${tv.id}${employee.idEmployee}").toLong(),
                                                idEmployee = it1,
                                                year = month.persianCalendar.persianYear.toString(),
                                                month = month.persianCalendar.persianMonthName,
                                                nameday = "${tv.text}",
                                            )
                                        }
                                        newDay?.let { it1 -> dayDao.delete(it1) }
                                        tv.setBackgroundColor(
                                            ContextCompat.getColor(
                                                tv.context, R.color.blacke
                                            )
                                        )

                                        val efficiencyEmployee = efficiencyEmployeeDao.getEfficiencyEmployee(employee.idEmployee!!)
                                        var time = dayData.exit!!.toInt() - dayData.entry!!.toInt()

                                        time = efficiencyEmployee?.mustWeekWatch!! - time

                                        val newEfficiencyEmployee = EfficiencyEmployee(
                                            idEfficiency = efficiencyEmployee.idEfficiency,
                                            idEmployee = employee.idEmployee,
                                            mustWeekWatch = time,
                                            totalWeekWatch = efficiencyEmployee.totalWeekWatch,
                                            efficiencyWeekPresence = efficiencyEmployee.efficiencyWeekPresence,
                                            totalWatch = efficiencyEmployee.totalWatch,
                                            efficiencyTotalPresence = efficiencyEmployee.efficiencyTotalPresence,
                                            totalWeekDuties = efficiencyEmployee.totalWeekDuties,
                                            totalMonthDuties = efficiencyEmployee.totalMonthDuties,
                                            totalDuties = efficiencyEmployee.totalDuties,
                                            efficiencyWeekDuties = efficiencyEmployee.efficiencyWeekDuties,
                                            efficiencyTotalDuties = efficiencyEmployee.efficiencyTotalDuties,
                                            efficiencyTotal = efficiencyEmployee.efficiencyTotal,
                                            numberDay = efficiencyEmployee.numberDay
                                        )
                                        efficiencyEmployeeDao.update(newEfficiencyEmployee)
                                    }

                                }
                            }

                        binding.exFiveCalendar.monthScrollListener = { month ->
                            val persianlCalendar = month.yearMonth.persianlCalendar()
                            val monthTitle = "${persianlCalendar.persianMonthName} ${
                                persianlCalendar.persianYear.toString().persianNumbers()
                            }"
                            binding.txtYM.text = monthTitle

                            selectedDate?.let {
                                // Clear selection if we scroll to a new month.
                                selectedDate = null
                                binding.exFiveCalendar.notifyDateChanged(it)
                            }
                        }

                        binding.exFiveNextMonthImage.setOnClickListener {
                            binding.exFiveCalendar.findFirstVisibleMonth()?.let {
                                binding.exFiveCalendar.smoothScrollToMonth(it.yearMonth.next)
                            }
                        }

                        binding.exFivePreviousMonthImage.setOnClickListener {
                            binding.exFiveCalendar.findFirstVisibleMonth()?.let {
                                binding.exFiveCalendar.smoothScrollToMonth(it.yearMonth.previous)
                            }
                        }

                    }
                }
            }
    }}
