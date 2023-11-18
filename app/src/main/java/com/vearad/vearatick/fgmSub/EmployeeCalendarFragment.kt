package com.vearad.vearatick.fgmSub

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
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
import com.vearad.vearatick.DataBase.Day
import com.vearad.vearatick.DataBase.EfficiencyDao
import com.vearad.vearatick.DataBase.EfficiencyEmployee
import com.vearad.vearatick.DataBase.Employee
import com.vearad.vearatick.DataBase.Time
import com.vearad.vearatick.R
import com.vearad.vearatick.adapter.EntryExitEmployeeAdapter
import com.vearad.vearatick.databinding.CalendarHeaderBinding
import com.vearad.vearatick.databinding.FragmentDialogDeleteItemEmployeeEntryExitBinding
import com.vearad.vearatick.databinding.FragmentDialogEmployeeDoneEntryExitBinding
import com.vearad.vearatick.databinding.FragmentDialogEmployeeEntryExitBinding
import com.vearad.vearatick.databinding.FragmentEmployeeCalendarBinding
import com.vearad.vearatick.databinding.ItemCalendarDayBinding
import com.vearad.vearatick.dayDao
import com.vearad.vearatick.inOutAdapter
import com.vearad.vearatick.timeDao
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import com.wdullaer.materialdatetimepicker.time.Timepoint
import org.threeten.bp.LocalDate
import org.threeten.bp.YearMonth

@Suppress("NAME_SHADOWING")
class EmployeeCalendarFragment(
    val employee: Employee, val efficiencyEmployeeDao: EfficiencyDao, val position: Int
) : Fragment(),
    EntryExitEmployeeAdapter.EntryExitEmployeeEvent {

    lateinit var binding: FragmentEmployeeCalendarBinding
    lateinit var bindingDialogEmployeeEntryExit: FragmentDialogEmployeeEntryExitBinding
    lateinit var bindingDialogEmployeeDoneEntryExit: FragmentDialogEmployeeDoneEntryExitBinding
    lateinit var bindingDialogDeleteItemEmployeeEntryExit: FragmentDialogDeleteItemEmployeeEntryExitBinding
    private var selectedDate: LocalDate? = null
    var oldLayout: LinearLayout? = null

    var valueHourEntry = ""
    var valueAllEntry = ""
    var valueHourExit = ""
    var valueAllExit = ""

    var valueBtnNoDate = false
    var valueBtnDoneEntry = false
    var valueBtnDoneExit = false
    var valueHourDoneEntry = 0
    var valueAllDoneEntry = ""
    var valueHourDoneExit = 0
    var valueHourDeleteDoneExit = 0
    var valueAllDoneExit = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentEmployeeCalendarBinding.inflate(layoutInflater, container, false)
        bindingDialogEmployeeEntryExit =
            FragmentDialogEmployeeEntryExitBinding.inflate(layoutInflater, null, false)
        bindingDialogEmployeeDoneEntryExit =
            FragmentDialogEmployeeDoneEntryExitBinding.inflate(layoutInflater, null, false)
        bindingDialogDeleteItemEmployeeEntryExit =
            FragmentDialogDeleteItemEmployeeEntryExitBinding.inflate(layoutInflater, null, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AndroidThreeTen.init(view.context)
        dayDao = AppDatabase.getDataBase(view.context).dayDao
        timeDao = AppDatabase.getDataBase(view.context).timeDao
        setData()

        val currentMonth = YearMonth.now()
        binding.clrEntExtEmp.setup(
            currentMonth.minusMonths(12),
            currentMonth.plusMonths(12),
            org.threeten.bp.DayOfWeek.SATURDAY
        )
        binding.clrEntExtEmp.scrollToDate(LocalDate.now())


        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay // Will be set when this container is bound.
            val bindingItemCalendarDay = ItemCalendarDayBinding.bind(view)
            val dayEntEmp = bindingItemCalendarDay.dayEntEmp
            val dayExtEmp = bindingItemCalendarDay.dayExtEmp
            val textView = bindingItemCalendarDay.txtDayEntExtEmp
            val layout = bindingItemCalendarDay.lytDayEntExtEmp

            init {
                view.setOnClickListener {
                    if (day.owner == DayOwner.THIS_MONTH) {
                        if (selectedDate != day.date) {
                            val oldDate = selectedDate
                            selectedDate = day.date
                            binding.clrEntExtEmp.notifyDateChanged(day.date)
                            oldDate?.let { binding.clrEntExtEmp.notifyDateChanged(it) }

                            if (oldLayout != null && oldLayout != layout) {
                                oldLayout!!.setBackgroundResource(0)
                                layout.setBackgroundResource(R.drawable.shape_selected_bg)
                                oldLayout = layout
                            } else {
                                layout.setBackgroundResource(R.drawable.shape_selected_bg)
                                oldLayout = layout
                            }

                            val nameDay = dayDao.getAllNameDay(
                                employee.idEmployee!!,
                                day.persianCalendar.persianYear.toString(),
                                day.persianCalendar.persianMonthName,
                                selectedDate!!.toPersianCalendar().persianWeekDayName
                            )
                            binding.btnFabEntExt.setOnClickListener {
                                if (nameDay?.nameday == selectedDate?.toPersianCalendar()?.persianWeekDayName) {
                                    showDoneEntryAndExitDialog(
                                        employee.idEmployee,
                                        day.persianCalendar.persianDay,
                                        day.persianCalendar.persianMonthName,
                                        day.persianCalendar.persianYear,
                                        dayEntEmp,
                                        dayExtEmp
                                    )
                                } else {
                                    Toast.makeText(
                                        context,
                                        "این روز برایه کارمند انتخاب نشده!!!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                            val timeDayData = timeDao.getDayTime(
                                employee.idEmployee, day.persianCalendar.persianDay.toString()
                            )
                            val timeDay = timeDao.getTime(
                                employee.idEmployee, selectedDate!!.toPersianCalendar().persianDay
                            )
                            val entryExit = dayDao.getAllEntryExit(
                                employee.idEmployee,
                                selectedDate!!.toPersianCalendar().persianYear.toString(),
                                selectedDate!!.toPersianCalendar().persianMonthName.toString(),
                                selectedDate!!.toPersianCalendar().persianWeekDayName.toString(),
                            )
                            if (nameDay?.nameday == selectedDate!!.toPersianCalendar().persianWeekDayName) {
                                if (timeDay?.day == selectedDate!!.toPersianCalendar().persianDay.toString()) {
                                    inOutAdapter = EntryExitEmployeeAdapter(
                                        ArrayList(timeDayData),
                                        entryExit,
                                        day.persianCalendar.persianWeekDayName,
                                        this@EmployeeCalendarFragment,
                                        dayEntEmp,
                                        dayExtEmp
                                    )
                                    binding.rcvInOut.adapter = inOutAdapter
                                } else {
                                    val entryExitList = arrayListOf(
                                        Time(
                                            idEmployee = employee.idEmployee,
                                            year = PersianCalendar().persianYear.toString(),
                                            month = PersianCalendar().persianMonthName.toString(),
                                            day = selectedDate!!.toPersianCalendar().persianDay.toString(),
                                            arrival = false,
                                            entry = 0,
                                            entryAll = "00:00",
                                            exit = 0,
                                            exitAll = "00:00"
                                        )
                                    )
                                    inOutAdapter = EntryExitEmployeeAdapter(
                                        ArrayList(entryExitList),
                                        entryExit,
                                        day.persianCalendar.persianWeekDayName,
                                        this@EmployeeCalendarFragment,
                                        dayEntEmp,
                                        dayExtEmp
                                    )
                                    binding.rcvInOut.adapter = inOutAdapter
                                    Toast.makeText(
                                        context, "اطلاعاتی ثبت نشده!!!", Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                val entryExitList = arrayListOf(
                                    Time(
                                        idEmployee = employee.idEmployee,
                                        year = PersianCalendar().persianYear.toString(),
                                        month = PersianCalendar().persianMonthName.toString(),
                                        day = selectedDate!!.toPersianCalendar().persianDay.toString(),
                                        arrival = false,
                                        entry = 0,
                                        entryAll = "00:00",
                                        exit = 0,
                                        exitAll = "00:00"
                                    )
                                )
                                inOutAdapter = EntryExitEmployeeAdapter(
                                    ArrayList(entryExitList),
                                    entryExit,
                                    day.persianCalendar.persianWeekDayName,
                                    this@EmployeeCalendarFragment,
                                    dayEntEmp,
                                    dayExtEmp
                                )
                                binding.rcvInOut.adapter = inOutAdapter
                                Toast.makeText(
                                    context,
                                    " این روز برایه کارمند انتخاب نشده!!!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
        }

        binding.clrEntExtEmp.dayBinder = object : DayBinder<DayViewContainer> {
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
                    val arrivalDay = timeDao.getAllArrivalDay(
                        employee.idEmployee!!,
                        day.persianCalendar.persianYear.toString(),
                        day.persianCalendar.persianMonthName,
                        day.persianCalendar.persianDay.toString()
                    )
                    if (arrivalDay != null && arrivalDay.day == day.persianCalendar.persianDay.toString()) {
                        if (arrivalDay.arrival) {
                            if (arrivalDay.exit == null || arrivalDay.exit == 0) {

                                container.dayEntEmp.setBackgroundColor(
                                    view.context.getColor(
                                        R.color.green_700
                                    )
                                )
                            } else {
                                container.dayEntEmp.setBackgroundColor(
                                    view.context.getColor(
                                        R.color.green_700
                                    )
                                )
                                container.dayExtEmp.setBackgroundColor(
                                    view.context.getColor(
                                        R.color.green_700
                                    )
                                )
                            }
                        } else {
                            container.dayExtEmp.setBackgroundColor(
                                context!!.getColor(R.color.red_800)
                            )
                            container.dayEntEmp.setBackgroundColor(
                                context!!.getColor(
                                    R.color.red_800
                                )
                            )
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
        binding.clrEntExtEmp.monthHeaderBinder =
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
                                val dayData =
                                    dayDao.getDay(("${tv.id}${employee.idEmployee!!}").toLong())
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
                                        showEntryAndExitDialog(tv, month, container.legendLayout)
                                    } else if (dayData.idDay == ("${tv.id}${employee.idEmployee}").toLong() && dayData.idEmployee == employee.idEmployee) {
                                        val newDay = employee.idEmployee.let { it1 ->
                                            Day(
                                                idDay = ("${tv.id}${employee.idEmployee}").toLong(),
                                                idEmployee = it1,
                                                year = month.persianCalendar.persianYear.toString(),
                                                month = month.persianCalendar.persianMonthName,
                                                nameday = "${tv.text}",
                                            )
                                        }
                                        newDay.let { it1 -> dayDao.delete(it1) }
                                        tv.setBackgroundColor(
                                            ContextCompat.getColor(
                                                tv.context, R.color.blacke
                                            )
                                        )
                                        val efficiencyEmployee =
                                            efficiencyEmployeeDao.getEfficiencyEmployee(employee.idEmployee!!)
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
                                            numberDay = efficiencyEmployee.numberDay,
                                            totalMonthWatch = efficiencyEmployee.totalMonthWatch,
                                            efficiencyMonthDuties = efficiencyEmployee.efficiencyMonthDuties
                                        )
                                        efficiencyEmployeeDao.update(newEfficiencyEmployee)
                                    }
                                }
                            }
                        binding.clrEntExtEmp.monthScrollListener = { month ->
                            val persianlCalendar = month.yearMonth.persianlCalendar()
                            val monthTitle = "${persianlCalendar.persianMonthName} ${
                                persianlCalendar.persianYear.toString().persianNumbers()
                            }"
                            binding.txtYM.text = monthTitle
                            selectedDate?.let {
                                // Clear selection if we scroll to a new month.
                                selectedDate = null
                                binding.clrEntExtEmp.notifyDateChanged(it)
                            }
                        }
                        binding.exFiveNextMonthImage.setOnClickListener {
                            binding.clrEntExtEmp.findFirstVisibleMonth()?.let {
                                binding.clrEntExtEmp.smoothScrollToMonth(it.yearMonth.next)
                            }
                        }
                        binding.exFivePreviousMonthImage.setOnClickListener {
                            binding.clrEntExtEmp.findFirstVisibleMonth()?.let {
                                binding.clrEntExtEmp.smoothScrollToMonth(it.yearMonth.previous)
                            }
                        }

                    }
                }
            }
    }

    private fun setData() {
        val today = PersianCalendar()
        val nameDay = dayDao.getAllNameDay(
            employee.idEmployee!!,
            today.persianYear.toString(),
            today.persianMonthName,
            today.persianWeekDayName
        )
        val timeDayData = timeDao.getDayTime(
            employee.idEmployee, today.persianDay.toString()
        )
        val timeDay = timeDao.getTime(
            employee.idEmployee, today.persianDay
        )
        val entryExit = dayDao.getAllEntryExit(
            employee.idEmployee,
            today.persianYear.toString(),
            today.persianMonthName.toString(),
            today.persianWeekDayName.toString(),
        )
        if (nameDay?.nameday == today.persianWeekDayName) {
            if (timeDay?.day == today.persianDay.toString()) {
                inOutAdapter = EntryExitEmployeeAdapter(
                    ArrayList(timeDayData),
                    entryExit,
                    today.persianWeekDayName,
                    this@EmployeeCalendarFragment,
                    binding.cardView1,
                    binding.cardView1,
                )
                binding.rcvInOut.adapter = inOutAdapter
            } else {
                val entryExitList = arrayListOf(
                    Time(
                        idEmployee = employee.idEmployee,
                        year = today.persianYear.toString(),
                        month = today.persianMonthName.toString(),
                        day = today.persianDay.toString(),
                        arrival = false,
                        entry = 0,
                        entryAll = "00:00",
                        exit = 0,
                        exitAll = "00:00"
                    )
                )
                inOutAdapter = EntryExitEmployeeAdapter(
                    ArrayList(entryExitList),
                    entryExit,
                    today.persianWeekDayName,
                    this@EmployeeCalendarFragment,
                    binding.cardView1,
                    binding.cardView1,
                )
                binding.rcvInOut.adapter = inOutAdapter
            }
        } else {
            val entryExitList = arrayListOf(
                Time(
                    idEmployee = employee.idEmployee,
                    year = today.persianYear.toString(),
                    month = today.persianMonthName.toString(),
                    day = today.persianDay.toString(),
                    arrival = false,
                    entry = 0,
                    entryAll = "00:00",
                    exit = 0,
                    exitAll = "00:00"
                )
            )
            inOutAdapter = EntryExitEmployeeAdapter(
                ArrayList(entryExitList),
                entryExit,
                today.persianWeekDayName,
                this@EmployeeCalendarFragment,
                binding.cardView1,
                binding.cardView1,
            )
            binding.rcvInOut.adapter = inOutAdapter
        }
    }

    private fun showEntryAndExitDialog(
        tv: TextView,
        month: CalendarMonth,
        legendLayout: LinearLayout
    ) {

        val parent = bindingDialogEmployeeEntryExit.root.parent as? ViewGroup
        parent?.removeView(bindingDialogEmployeeEntryExit.root)
        val dialogBuilder = AlertDialog.Builder(bindingDialogEmployeeEntryExit.root.context)
        dialogBuilder.setView(bindingDialogEmployeeEntryExit.root)
        bindingDialogEmployeeEntryExit.txtEntry.text = " ورود"
        bindingDialogEmployeeEntryExit.btnEntry.setBackgroundResource(R.drawable.shape_background_deadline_blacke)
        bindingDialogEmployeeEntryExit.txtExit.text = " خروج"
        bindingDialogEmployeeEntryExit.btnExit.setBackgroundResource(R.drawable.shape_background_deadline_blacke)
        bindingDialogEmployeeEntryExit.btnAllDay.isChecked = false

        if (valueHourEntry != ""){
            bindingDialogEmployeeEntryExit.txtEntry.text = "$valueHourEntry:00"
            bindingDialogEmployeeEntryExit.btnEntry.setBackgroundResource(R.drawable.shape_background_deadline_firoze)
        }
        if (valueHourExit != ""){
            bindingDialogEmployeeEntryExit.txtExit.text = "$valueHourExit:00"
            bindingDialogEmployeeEntryExit.btnExit.setBackgroundResource(R.drawable.shape_background_deadline_firoze)
        }

        bindingDialogEmployeeEntryExit.btnEntry.setOnClickListener {
            onCreatePickerEntry()
        }
        bindingDialogEmployeeEntryExit.btnExit.setOnClickListener {
            onCreatePickerExit()
        }
        val alertDialog = dialogBuilder.create()
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.Transparent.toArgb()))
        alertDialog.setCancelable(false)
        alertDialog.show()
        bindingDialogEmployeeEntryExit.dialogBtnCansel.setOnClickListener {
            alertDialog.dismiss()
        }
        bindingDialogEmployeeEntryExit.dialogBtnSure.setOnClickListener {

            if (valueAllEntry == "" && valueAllExit == "")
                Toast.makeText(context, " لطفا همه مقادیر را وارد کنید.", Toast.LENGTH_SHORT).show()
            else if (valueHourExit.toInt() < valueHourEntry.toInt())
                Toast.makeText(
                    context,
                    " چطور میشه که ساعت خروج قبل ساعت ورود باشه.",
                    Toast.LENGTH_SHORT
                ).show()
            else {
                val efficiencyEmployee =
                    efficiencyEmployeeDao.getEfficiencyEmployee(employee.idEmployee!!)

                val timeAgo = efficiencyEmployee?.mustWeekWatch!!
                val timeNew =
                    valueHourExit.toInt() - valueHourEntry.toInt()

                val time = timeNew + timeAgo

                val newDay = Day(
                    idDay = ("${tv.id}${employee.idEmployee}").toLong(),
                    idEmployee = employee.idEmployee,
                    year = month.persianCalendar.persianYear.toString(),
                    month = month.persianCalendar.persianMonthName,
                    nameday = "${tv.text}",
                    entry = "$valueHourEntry",
                    exit = "$valueHourExit",
                    entryAll = valueAllEntry,
                    exitAll = valueAllExit
                )
                dayDao.insert(newDay)
                tv.setBackgroundColor(
                    ContextCompat.getColor(
                        tv.context, R.color.firoze
                    )
                )
                val newEfficiencyEmployee = EfficiencyEmployee(
                    idEfficiency = efficiencyEmployee.idEfficiency,
                    idEmployee = efficiencyEmployee.idEmployee,
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
                    numberDay = efficiencyEmployee.numberDay,
                    efficiencyMonthDuties = efficiencyEmployee.totalMonthDuties,
                    totalMonthWatch = efficiencyEmployee.totalMonthWatch

                )
                efficiencyEmployeeDao.update(newEfficiencyEmployee)
                if (bindingDialogEmployeeEntryExit.btnAllDay.isChecked)
                    clickAllDay(efficiencyEmployee, timeNew, legendLayout, month)
                alertDialog.dismiss()

            }
        }
    }

    private fun clickAllDay(
        efficiencyEmployee: EfficiencyEmployee,
        time: Int,
        legendLayout: LinearLayout,
        month: CalendarMonth
    ) {

        legendLayout.children.map { it as TextView }
            .forEachIndexed { index, tv ->


                val newDay = Day(
                    idDay = ("${tv.id}${employee.idEmployee}").toLong(),
                    idEmployee = employee.idEmployee,
                    year = month.persianCalendar.persianYear.toString(),
                    month = month.persianCalendar.persianMonthName,
                    nameday = "${tv.text}",
                    entry = valueHourEntry,
                    exit = valueHourExit,
                    entryAll = valueAllEntry,
                    exitAll = valueAllExit
                )
                dayDao.insertOrUpdateFood(newDay)

                val dayData =
                    dayDao.getDay(("${tv.id}${employee.idEmployee}").toLong())

                if ((dayData?.idDay) == ("${tv.id}${employee.idEmployee}").toLong() && dayData.idEmployee == employee.idEmployee) {
                    tv.setBackgroundColor(
                        ContextCompat.getColor(
                            tv.context, R.color.firoze
                        )
                    )
                }

            }

        val newEfficiencyEmployee = EfficiencyEmployee(
            idEfficiency = efficiencyEmployee.idEfficiency,
            idEmployee = efficiencyEmployee.idEmployee,
            mustWeekWatch = 7 * time,
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
            numberDay = efficiencyEmployee.numberDay,
            efficiencyMonthDuties = efficiencyEmployee.totalMonthDuties,
            totalMonthWatch = efficiencyEmployee.totalMonthWatch

        )
        efficiencyEmployeeDao.update(newEfficiencyEmployee)
    }

    fun onCreatePickerEntry() {

        val persianCalendar = PersianCalendar()

        val timePickerDialog = TimePickerDialog.newInstance(
            { view, hourOfDay, minute, second ->
                valueHourEntry = "$hourOfDay"
                valueAllEntry = "$hourOfDay:$minute"
                bindingDialogEmployeeEntryExit.txtEntry.text = valueAllEntry
                bindingDialogEmployeeEntryExit.btnEntry.setBackgroundResource(R.drawable.shape_background_deadline_firoze)
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

    fun onCreatePickerExit() {

        val persianCalendar = PersianCalendar()
        val timePickerDialog = TimePickerDialog.newInstance(
            { view, hourOfDay, minute, second ->
                valueHourExit = "$hourOfDay"
                valueAllExit = "$hourOfDay:$minute"
                bindingDialogEmployeeEntryExit.txtExit.text = valueAllExit
                bindingDialogEmployeeEntryExit.btnExit.setBackgroundResource(R.drawable.shape_background_deadline_firoze)
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

    private fun showDoneEntryAndExitDialog(
        idEmployee: Int?,
        day: Int,
        month: String,
        year: Int,
        dayEntEmp: View,
        dayExtEmp: View
    ) {
        val dayOnClicke =
            timeDao.getAllArrivalDay(employee.idEmployee!!, year.toString(), month, day.toString())

        valueBtnNoDate = false
        valueBtnDoneEntry = false
        valueBtnDoneExit = false
        valueHourDoneEntry = 0
        valueAllDoneEntry = ""
        valueHourDoneExit = 0
        valueAllDoneExit = ""
        bindingDialogEmployeeDoneEntryExit.txtEntry.text = "ورود"
        bindingDialogEmployeeDoneEntryExit.txtExit.text = "خروج"
        bindingDialogEmployeeDoneEntryExit.btnNoDate.setBackgroundResource(R.drawable.shape_background_deadline_blacke)
        bindingDialogEmployeeDoneEntryExit.btnEntry.setBackgroundResource(R.drawable.shape_background_deadline_blacke)
        bindingDialogEmployeeDoneEntryExit.btnExit.setBackgroundResource(R.drawable.shape_background_deadline_blacke)

        if (dayOnClicke != null) {
            if (dayOnClicke.exit != 0) {
                valueBtnDoneEntry = true
                valueBtnDoneExit = true
                valueHourDoneEntry = dayOnClicke.entry
                valueAllDoneEntry = dayOnClicke.entryAll
                valueHourDoneExit = dayOnClicke.exit!!.toInt()
                valueAllDoneExit = dayOnClicke.exitAll.toString()
                bindingDialogEmployeeDoneEntryExit.txtEntry.text = valueAllDoneEntry
                bindingDialogEmployeeDoneEntryExit.txtExit.text = valueAllDoneExit
                bindingDialogEmployeeDoneEntryExit.btnEntry.setBackgroundResource(R.drawable.shape_background_deadline_firoze)
                bindingDialogEmployeeDoneEntryExit.btnExit.setBackgroundResource(R.drawable.shape_background_deadline_firoze)

            } else if (dayOnClicke.entry != 0) {
                valueBtnDoneEntry = true
                valueHourDoneEntry = dayOnClicke.entry
                valueAllDoneEntry = dayOnClicke.entryAll
                bindingDialogEmployeeDoneEntryExit.txtEntry.text = valueAllDoneEntry
                bindingDialogEmployeeDoneEntryExit.btnEntry.setBackgroundResource(R.drawable.shape_background_deadline_firoze)

            } else if (!dayOnClicke.arrival) {
                bindingDialogEmployeeDoneEntryExit.btnNoDate.setBackgroundResource(R.drawable.shape_background_deadline_firoze)
                valueBtnNoDate = true
            }

        }

        val parent = bindingDialogEmployeeDoneEntryExit.root.parent as? ViewGroup
        parent?.removeView(bindingDialogEmployeeDoneEntryExit.root)
        val dialogBuilder = AlertDialog.Builder(bindingDialogEmployeeDoneEntryExit.root.context)
        dialogBuilder.setView(bindingDialogEmployeeDoneEntryExit.root)

        bindingDialogEmployeeDoneEntryExit.btnNoDate.setOnClickListener {
            if (!valueBtnNoDate && !valueBtnDoneEntry && !valueBtnDoneExit) {
                bindingDialogEmployeeDoneEntryExit.btnNoDate.setBackgroundResource(R.drawable.shape_background_deadline_firoze)
                valueBtnNoDate = true
            } else {
                bindingDialogEmployeeDoneEntryExit.btnNoDate.setBackgroundResource(R.drawable.shape_background_deadline_blacke)
                valueBtnNoDate = false
            }
        }

        bindingDialogEmployeeDoneEntryExit.btnEntry.setOnClickListener {
            if (!valueBtnNoDate && !valueBtnDoneEntry) {
                onCreatePickerDoneEntry()
            } else {
                bindingDialogEmployeeDoneEntryExit.btnEntry.setBackgroundResource(R.drawable.shape_background_deadline_blacke)
                bindingDialogEmployeeDoneEntryExit.txtEntry.text = "ورود"
                valueBtnDoneEntry = false
            }
        }

        bindingDialogEmployeeDoneEntryExit.btnExit.setOnClickListener {
            if (!valueBtnNoDate && !valueBtnDoneExit) {
                onCreatePickerDoneExit()
            } else {
                valueHourDeleteDoneExit = valueHourDoneExit
                valueHourDoneExit = 0
                valueAllDoneExit = "00:00"
                bindingDialogEmployeeDoneEntryExit.txtExit.text = "خروج"
                bindingDialogEmployeeDoneEntryExit.btnExit.setBackgroundResource(R.drawable.shape_background_deadline_blacke)
                valueBtnDoneExit = false
            }
        }

        val alertDialog = dialogBuilder.create()
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.Transparent.toArgb()))
        alertDialog.setCancelable(false)
        alertDialog.show()
        bindingDialogEmployeeDoneEntryExit.dialogBtnCansel.setOnClickListener {
            alertDialog.dismiss()
        }
        bindingDialogEmployeeDoneEntryExit.dialogBtnSure.setOnClickListener {

            if (!valueBtnNoDate && !valueBtnDoneEntry)
                Toast.makeText(context, " لطفا تمام مقادیر را وارد کنید.", Toast.LENGTH_SHORT)
                    .show()
            else if (valueHourDoneExit != 0 && valueHourDoneExit.toInt() < valueHourDoneEntry.toInt())
                Toast.makeText(
                    context,
                    " چطور میشه که ساعت خروج قبل ساعت ورود باشه.",
                    Toast.LENGTH_SHORT
                ).show()
            else if (valueBtnNoDate) {

                val timeData =
                    timeDao.getAllArrivalDay(idEmployee!!, year.toString(), month, day.toString())
                val newTime = Time(
                    timeData?.idTime,
                    idEmployee = idEmployee,
                    year = year.toString(),
                    month = month,
                    day = day.toString(),
                    arrival = false,
                    entry = 0,
                    entryAll = "00:00",
                    exit = 0,
                    exitAll = "00:00",
                    differenceTime = 0
                )
                if (day.toString() == timeData?.day) {
                    timeDao.update(newTime)

                    if (valueBtnDoneExit) {
                        inOutAdapter.updateInOut(newTime, 0)
                        dayExtEmp.setBackgroundColor(it.context.getColor(R.color.red_800))
                        dayEntEmp.setBackgroundColor(
                            it.context.getColor(R.color.red_800)
                        )
                    }
                } else {
                    timeDao.insert(newTime)
                    dayExtEmp.setBackgroundColor(it.context.getColor(R.color.red_800))
                    dayEntEmp.setBackgroundColor(
                        it.context.getColor(R.color.red_800)
                    )
                }

                alertDialog.dismiss()
            } else {

                var timeData =
                    timeDao.getAllArrivalDay(idEmployee!!, year.toString(), month, day.toString())
                val differenceTime = valueHourDoneExit - valueHourDoneEntry
                val newTime = Time(
                    timeData?.idTime,
                    idEmployee = idEmployee,
                    year = year.toString(),
                    month = month,
                    day = day.toString(),
                    arrival = true,
                    entry = valueHourDoneEntry,
                    exit = valueHourDoneExit,
                    entryAll = valueAllDoneEntry,
                    exitAll = valueAllDoneExit,
                    differenceTime = differenceTime
                )

                if (day.toString() == timeData?.day) {
                    timeDao.update(newTime)

                    inOutAdapter.updateInOut(newTime, 0)
                    if (!valueBtnDoneExit) {
                        dayEntEmp.setBackgroundColor(it.context.getColor(R.color.green_700))
                        dayExtEmp.setBackgroundColor(
                            it.context.getColor(R.color.blacke)
                        )
                    } else {
                        dayEntEmp.setBackgroundColor(it.context.getColor(R.color.green_700))
                        dayExtEmp.setBackgroundColor(
                            it.context.getColor(R.color.green_700)
                        )
                    }

                } else {

                    timeDao.insert(newTime)

                    inOutAdapter.updateInOut(newTime, 0)

                    if (!valueBtnDoneExit) {
                        dayEntEmp.setBackgroundColor(it.context.getColor(R.color.green_700))
                        dayExtEmp.setBackgroundColor(
                            it.context.getColor(R.color.blacke)
                        )
                    } else {
                        dayEntEmp.setBackgroundColor(it.context.getColor(R.color.green_700))
                        dayExtEmp.setBackgroundColor(
                            it.context.getColor(R.color.green_700)
                        )
                    }


                }

                alertDialog.dismiss()
            }

        }
    }

    fun onCreatePickerDoneEntry() {

        val persianCalendar = PersianCalendar()

        val timePickerDialog = TimePickerDialog.newInstance(
            { view, hourOfDay, minute, second ->
                valueHourDoneEntry = hourOfDay
                valueAllDoneEntry = "$hourOfDay:$minute"
                bindingDialogEmployeeDoneEntryExit.txtEntry.text = valueAllDoneEntry
                bindingDialogEmployeeDoneEntryExit.txtEntry.textSize = 16f
                bindingDialogEmployeeDoneEntryExit.btnEntry.setBackgroundResource(R.drawable.shape_background_deadline_firoze)
                valueBtnDoneEntry = true
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

    fun onCreatePickerDoneExit() {

        val persianCalendar = PersianCalendar()

        val timePickerDialog = TimePickerDialog.newInstance(
            { view, hourOfDay, minute, second ->
                valueHourDoneExit = hourOfDay
                valueAllDoneExit = "$hourOfDay:$minute"
                bindingDialogEmployeeDoneEntryExit.txtExit.text = valueAllDoneExit
                bindingDialogEmployeeDoneEntryExit.txtExit.textSize = 16f
                bindingDialogEmployeeDoneEntryExit.btnExit.setBackgroundResource(R.drawable.shape_background_deadline_firoze)
                valueBtnDoneExit = true
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

    override fun onMenuItemClick(
        onClicktime: Time,
        position: Int,
        dayEntEmp: View,
        dayExtEmp: View
    ) {

        val viewHolder =
            binding.rcvInOut.findViewHolderForAdapterPosition(position) as EntryExitEmployeeAdapter.EntryExitEmployeeViewHolder
        viewHolder.let { holder ->
            val btnMenuEntryExitEmployee = holder.btnMenu
            val popupMenu = PopupMenu(context, btnMenuEntryExitEmployee)
            popupMenu.inflate(R.menu.menu_employee_entry_exit)
            popupMenu.show()

            popupMenu.setOnMenuItemClickListener { item ->

                when (item.itemId) {

                    R.id.menu_employee_entry_exit_delete -> {
                        showDeleteEntryExitEmployeeDialog(
                            onClicktime,
                            dayEntEmp,
                            dayExtEmp
                        )
                        true
                    }

                    else -> false
                }
            }
        }
    }

    private fun showDeleteEntryExitEmployeeDialog(
        onClicktime: Time,
        dayEntEmp: View,
        dayExtEmp: View
    ) {
        val parent = bindingDialogDeleteItemEmployeeEntryExit.root.parent as? ViewGroup
        parent?.removeView(bindingDialogDeleteItemEmployeeEntryExit.root)
        val dialogBuilder =
            AlertDialog.Builder(bindingDialogDeleteItemEmployeeEntryExit.root.context)
        dialogBuilder.setView(bindingDialogDeleteItemEmployeeEntryExit.root)

        val alertDialog = dialogBuilder.create()
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.Transparent.toArgb()))
        alertDialog.setCancelable(false)
        alertDialog.show()
        bindingDialogDeleteItemEmployeeEntryExit.dialogBtnDeleteCansel.setOnClickListener {
            alertDialog.dismiss()
        }
        bindingDialogDeleteItemEmployeeEntryExit.dialogBtnDeleteSure.setOnClickListener {

            daleteEntryExit(
                onClicktime,
                dayEntEmp,
                dayExtEmp
            )

            alertDialog.dismiss()
        }
    }

    private fun daleteEntryExit(
        onClicktime: Time,
        dayEntEmp: View,
        dayExtEmp: View
    ) {
        val newTime = Time(
            idTime = onClicktime.idTime,
            idEmployee = onClicktime.idEmployee,
            year = onClicktime.year,
            month = onClicktime.month,
            day = onClicktime.day,
            arrival = onClicktime.arrival,
            entry = onClicktime.entry,
            exit = onClicktime.exit,
            entryAll = onClicktime.entryAll,
            exitAll = onClicktime.exitAll,
            differenceTime = onClicktime.differenceTime
        )

        timeDao.delete(newTime)

        val newTime1 = Time(
            idTime = onClicktime.idTime,
            idEmployee = onClicktime.idEmployee,
            year = onClicktime.year,
            month = onClicktime.month,
            day = onClicktime.day,
            arrival = onClicktime.arrival,
            entry = 0,
            exit = 0,
            entryAll = "00:00",
            exitAll = "00:00",
            differenceTime = onClicktime.differenceTime
        )
        inOutAdapter.updateInOut(newTime1, 0)

        dayEntEmp.setBackgroundColor(
            bindingDialogDeleteItemEmployeeEntryExit.root.context.getColor(
                R.color.blacke
            )
        )
        dayExtEmp.setBackgroundColor(
            bindingDialogDeleteItemEmployeeEntryExit.root.context.getColor(
                R.color.blacke
            )
        )
    }
}