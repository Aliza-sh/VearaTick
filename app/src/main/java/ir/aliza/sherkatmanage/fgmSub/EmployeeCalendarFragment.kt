package ir.aliza.sherkatmanage.fgmSub

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
import androidx.core.view.size
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
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import com.wdullaer.materialdatetimepicker.time.Timepoint
import ir.aliza.sherkatmanage.DataBase.AppDatabase
import ir.aliza.sherkatmanage.DataBase.Day
import ir.aliza.sherkatmanage.DataBase.EfficiencyDao
import ir.aliza.sherkatmanage.DataBase.EfficiencyEmployee
import ir.aliza.sherkatmanage.DataBase.Employee
import ir.aliza.sherkatmanage.DataBase.Time
import ir.aliza.sherkatmanage.R
import ir.aliza.sherkatmanage.adapter.EntryExitEmployeeAdapter
import ir.aliza.sherkatmanage.databinding.CalendarHeaderBinding
import ir.aliza.sherkatmanage.databinding.FragmentDialogDeleteItemEmployeeEntryExitBinding
import ir.aliza.sherkatmanage.databinding.FragmentDialogEmployeeDoneEntryExitBinding
import ir.aliza.sherkatmanage.databinding.FragmentDialogEmployeeEntryExitBinding
import ir.aliza.sherkatmanage.databinding.FragmentEmployeeCalendarBinding
import ir.aliza.sherkatmanage.databinding.ItemCalendarDayBinding
import ir.aliza.sherkatmanage.dayDao
import ir.aliza.sherkatmanage.inOutAdapter
import ir.aliza.sherkatmanage.timeDao
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
            val viewDaySub = bindingItemCalendarDay.dayEntExtEmp
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
                                        viewDaySub,
                                        day.persianCalendar.persianYear
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
                                        viewDaySub
                                    )
                                    binding.rcvInOut.adapter = inOutAdapter
                                } else {
                                    if (binding.rcvInOut.size != 0) inOutAdapter.clearAll()
                                    Toast.makeText(
                                        context, "اطلاعاتی ثبت نشده!!!", Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                if (binding.rcvInOut.size != 0) inOutAdapter.clearAll()
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
                        if (arrivalDay.arrival) container.viewDaySub.setBackgroundColor(
                            view.context.getColor(
                                R.color.green_700
                            )
                        )
                        else if (!arrivalDay.arrival) container.viewDaySub.setBackgroundColor(
                            context!!.getColor(R.color.red_800)
                        )
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
                                val dayData = dayDao.getDay(("${tv.id}${employee.idEmployee!!}").toLong())
                               if ((dayData?.idDay) == ("${tv.id}${employee.idEmployee}").toLong() && dayData.idEmployee == employee.idEmployee) {
                                    tv.setBackgroundColor(
                                        ContextCompat.getColor(
                                            view.context, R.color.firoze
                                        )
                                    )
                                }
                                tv.setOnClickListener {
                                    val dayData = dayDao.getDay(("${tv.id}${employee.idEmployee}").toLong())
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
                                            numberDay = efficiencyEmployee.numberDay
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
                    exit = "$valueHourExit"
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
                    efficiencyMonthDuties = efficiencyEmployee.totalMonthDuties

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
                    exit = valueHourExit
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
            efficiencyMonthDuties = efficiencyEmployee.totalMonthDuties

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
                bindingDialogEmployeeEntryExit.txtEntry.textSize = 24f
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
                bindingDialogEmployeeEntryExit.txtExit.textSize = 24f
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
        layout: View,
        year: Int
    ) {

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
                bindingDialogEmployeeDoneEntryExit.txtEntry.text = " ورود"
                valueBtnDoneEntry = false
            }
        }

        bindingDialogEmployeeDoneEntryExit.btnExit.setOnClickListener {
            if (!valueBtnNoDate && !valueBtnDoneExit) {
                onCreatePickerDoneExit()
            } else {
                bindingDialogEmployeeDoneEntryExit.txtExit.text = " خروج"
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

            if (!valueBtnNoDate && !valueBtnDoneExit && !valueBtnDoneEntry)
                Toast.makeText(context, " لطفا تمام مقادیر را وارد کنید.", Toast.LENGTH_SHORT)
                    .show()
            else if (valueHourDoneExit.toInt() < valueHourDoneEntry.toInt())
                Toast.makeText(
                    context,
                    " چطور میشه که ساعت خروج قبل ساعت ورود باشه.",
                    Toast.LENGTH_SHORT
                ).show()
            else if (valueBtnNoDate) {

                val timeData = timeDao.getTime(idEmployee!!, day)
                val newTime = Time(
                    timeData?.idTime,
                    idEmployee = idEmployee,
                    year = year.toString(),
                    month = month,
                    day = day.toString(),
                    arrival = false,
                    entry = 0,
                    exit = 0
                )
                val efficiencyEmployee = efficiencyEmployeeDao.getEfficiencyEmployee(idEmployee)
                val numberDay = efficiencyEmployee?.numberDay
                if (day.toString() == timeData?.day) {

                    var time = timeData.exit.toInt() - timeData.entry.toInt()
                    val timeAgo = efficiencyEmployee?.totalWeekWatch!! - time
                    val newEfficiencyEmployee = EfficiencyEmployee(
                        idEfficiency = efficiencyEmployee.idEfficiency,
                        idEmployee = idEmployee,
                        mustWeekWatch = efficiencyEmployee.mustWeekWatch,
                        totalWeekWatch = timeAgo,
                        numberDay = efficiencyEmployee.numberDay,
                        totalWatch = efficiencyEmployee.totalWatch,
                        efficiencyWeekPresence = efficiencyEmployee.efficiencyWeekPresence,
                        efficiencyTotalPresence = efficiencyEmployee.efficiencyTotalPresence,
                        totalWeekDuties = efficiencyEmployee.totalWeekDuties,
                        totalMonthDuties = efficiencyEmployee.totalMonthDuties,
                        totalDuties = efficiencyEmployee.totalDuties,
                        efficiencyWeekDuties = efficiencyEmployee.efficiencyWeekDuties,
                        efficiencyTotalDuties = efficiencyEmployee.efficiencyTotalDuties,
                        efficiencyTotal = efficiencyEmployee.efficiencyTotal,
                        totalMonthWatch = efficiencyEmployee.totalMonthWatch
                    )
                    efficiencyEmployeeDao.update(newEfficiencyEmployee)
                    timeDao.update(newTime)
                    //inOutAdapter.updateInOut(newTime, 0)
                    layout.setBackgroundColor(it.context.getColor(R.color.red_800))
                } else {
                    val newEfficiencyEmployee = EfficiencyEmployee(
                        idEfficiency = efficiencyEmployee?.idEfficiency,
                        idEmployee = idEmployee,
                        mustWeekWatch = efficiencyEmployee?.mustWeekWatch,
                        numberDay = numberDay!! + 1,
                        totalWeekWatch = efficiencyEmployee.totalWeekWatch,
                        totalWatch = efficiencyEmployee.totalWatch,
                        efficiencyWeekPresence = efficiencyEmployee.efficiencyWeekPresence,
                        efficiencyTotalPresence = efficiencyEmployee.efficiencyTotalPresence,
                        totalWeekDuties = efficiencyEmployee.totalWeekDuties,
                        totalMonthDuties = efficiencyEmployee.totalMonthDuties,
                        totalDuties = efficiencyEmployee.totalDuties,
                        efficiencyWeekDuties = efficiencyEmployee.efficiencyWeekDuties,
                        efficiencyTotalDuties = efficiencyEmployee.efficiencyTotalDuties,
                        efficiencyTotal = efficiencyEmployee.efficiencyTotal,
                        totalMonthWatch = efficiencyEmployee.totalMonthWatch
                    )
                    efficiencyEmployeeDao.update(newEfficiencyEmployee)
                    timeDao.insert(newTime)
                    layout.setBackgroundColor(it.context.getColor(R.color.red_800))
                }

                alertDialog.dismiss()
            } else {

                val efficiencyEmployee = efficiencyEmployeeDao.getEfficiencyEmployee(idEmployee!!)
                val numberDay = efficiencyEmployee?.numberDay
                val timeData = timeDao.getTime(idEmployee, day)

                val newTime = Time(
                    timeData?.idTime,
                    idEmployee = idEmployee,
                    year = year.toString(),
                    month = month,
                    day = day.toString(),
                    arrival = true,
                    entry = valueHourDoneEntry,
                    exit = valueHourDoneExit
                )

                if (day.toString() == timeData?.day) {

                    var time = timeData.exit.toInt() - timeData.entry.toInt()
                    val timeAgo = efficiencyEmployee?.totalWeekWatch!! - time
                    val timeNew = valueHourDoneExit
                        .toInt() - valueHourDoneEntry.toInt()

                    time = timeNew + timeAgo

                    val newEfficiencyEmployee = EfficiencyEmployee(
                        idEfficiency = efficiencyEmployee.idEfficiency,
                        idEmployee = idEmployee,
                        mustWeekWatch = efficiencyEmployee.mustWeekWatch,
                        numberDay = efficiencyEmployee.numberDay,
                        totalWeekWatch = time,
                        totalWatch = efficiencyEmployee.totalWatch,
                        efficiencyWeekPresence = efficiencyEmployee.efficiencyWeekPresence,
                        efficiencyTotalPresence = efficiencyEmployee.efficiencyTotalPresence,
                        totalWeekDuties = efficiencyEmployee.totalWeekDuties,
                        totalMonthDuties = efficiencyEmployee.totalMonthDuties,
                        totalDuties = efficiencyEmployee.totalDuties,
                        efficiencyWeekDuties = efficiencyEmployee.efficiencyWeekDuties,
                        efficiencyTotalDuties = efficiencyEmployee.efficiencyTotalDuties,
                        efficiencyTotal = efficiencyEmployee.efficiencyTotal,
                        totalMonthWatch = efficiencyEmployee.totalMonthWatch,
                        efficiencyMonthDuties = efficiencyEmployee.totalMonthDuties
                    )
                    efficiencyEmployeeDao.update(newEfficiencyEmployee)

                    timeDao.update(newTime)
                    //inOutAdapter.updateInOut(newTime, 0)
                    layout.setBackgroundColor(it.context.getColor(R.color.green_700))

                } else {

                    val timeAgo = efficiencyEmployee?.totalWeekWatch!!
                    val timeNew = valueHourDoneExit
                        .toInt() - valueHourDoneEntry.toInt()

                    val time = timeNew + timeAgo

                    val newEfficiencyEmployee = EfficiencyEmployee(
                        idEfficiency = efficiencyEmployee.idEfficiency,
                        idEmployee = idEmployee,
                        mustWeekWatch = efficiencyEmployee.mustWeekWatch,
                        totalWeekWatch = time,
                        numberDay = numberDay!! + 1,
                        totalWatch = efficiencyEmployee.totalWatch,
                        efficiencyWeekPresence = efficiencyEmployee.efficiencyWeekPresence,
                        efficiencyTotalPresence = efficiencyEmployee.efficiencyTotalPresence,
                        totalWeekDuties = efficiencyEmployee.totalWeekDuties,
                        totalMonthDuties = efficiencyEmployee.totalMonthDuties,
                        totalDuties = efficiencyEmployee.totalDuties,
                        efficiencyWeekDuties = efficiencyEmployee.efficiencyWeekDuties,
                        efficiencyTotalDuties = efficiencyEmployee.efficiencyTotalDuties,
                        efficiencyTotal = efficiencyEmployee.efficiencyTotal,
                        totalMonthWatch = efficiencyEmployee.totalMonthWatch
                    )
                    efficiencyEmployeeDao.update(newEfficiencyEmployee)
                    timeDao.insert(newTime)
                    layout.setBackgroundColor(it.context.getColor(R.color.green_700))

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
                bindingDialogEmployeeDoneEntryExit.txtEntry.textSize = 24f
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
                bindingDialogEmployeeDoneEntryExit.txtExit.textSize = 24f
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

    override fun onMenuItemClick(onClicktime: Time, position: Int, layout: View) {

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
                        showDeleteEntryExitEmployeeDialog(onClicktime,layout)
                        true
                    }

                    else -> false
                }
            }
        }
    }
    private fun showDeleteEntryExitEmployeeDialog(onClicktime: Time, layout: View) {
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

            daleteEntryExit(onClicktime,layout)

            alertDialog.dismiss()
        }
    }
    private fun daleteEntryExit(onClicktime: Time, layout: View) {
        val newTime = Time(
            idTime = onClicktime.idTime,
            idEmployee = onClicktime.idEmployee,
            year = onClicktime.year,
            month = onClicktime.month,
            day = onClicktime.day,
            arrival = onClicktime.arrival,
            entry = onClicktime.entry,
            exit = onClicktime.exit
        )
        val efficiencyEmployee = efficiencyEmployeeDao.getEfficiencyEmployee(onClicktime.idEmployee)
        var time = onClicktime.exit.toInt() - onClicktime.entry.toInt()
        val timeAgo = efficiencyEmployee?.totalWeekWatch!! - time
        val newEfficiencyEmployee = EfficiencyEmployee(
            idEfficiency = efficiencyEmployee.idEfficiency,
            idEmployee = onClicktime.idEmployee,
            mustWeekWatch = efficiencyEmployee.mustWeekWatch,
            totalWeekWatch = timeAgo,
            numberDay = efficiencyEmployee.numberDay,
            totalWatch = efficiencyEmployee.totalWatch,
            efficiencyWeekPresence = efficiencyEmployee.efficiencyWeekPresence,
            efficiencyTotalPresence = efficiencyEmployee.efficiencyTotalPresence,
            totalWeekDuties = efficiencyEmployee.totalWeekDuties,
            totalMonthDuties = efficiencyEmployee.totalMonthDuties,
            totalDuties = efficiencyEmployee.totalDuties,
            efficiencyWeekDuties = efficiencyEmployee.efficiencyWeekDuties,
            efficiencyTotalDuties = efficiencyEmployee.efficiencyTotalDuties,
            efficiencyTotal = efficiencyEmployee.efficiencyTotal,
            totalMonthWatch = efficiencyEmployee.totalMonthWatch
        )
        efficiencyEmployeeDao.update(newEfficiencyEmployee)
        timeDao.delete(newTime)
        inOutAdapter.removeInOut(newTime, position)
        layout.setBackgroundColor(bindingDialogDeleteItemEmployeeEntryExit.root.context.getColor(R.color.blacke))
    }
}
