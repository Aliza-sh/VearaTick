package ir.aliza.sherkatmanage.fgmSub

import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakewharton.threetenabp.AndroidThreeTen
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import com.kizitonwose.calendarview.utils.persian.*
import com.kizitonwose.calendarview.utils.yearMonth
import ir.aliza.sherkatmanage.DataBase.AppDatabase
import ir.aliza.sherkatmanage.DataBase.Task
import ir.aliza.sherkatmanage.DataBase.TaskDao
import ir.aliza.sherkatmanage.Dialog.TaskBottomsheetFragment
import ir.aliza.sherkatmanage.R
import ir.aliza.sherkatmanage.adapter.TaskAdapter
import ir.aliza.sherkatmanage.databinding.FragmentTaskBinding
import ir.aliza.sherkatmanage.databinding.ItemCalendarDayTaskBinding
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.YearMonth

class TaskFragment : Fragment(), TaskAdapter.TaskEvent {

    lateinit var binding: FragmentTaskBinding
    lateinit var taskDao: TaskDao
    lateinit var taskAdapter: TaskAdapter

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

        taskDao = AppDatabase.getDataBase(view.context).TaskDao

        val taskData = taskDao.getAllEmployee()
        taskAdapter = TaskAdapter(ArrayList(taskData), this)
        binding.recyclerViewDuties.adapter = taskAdapter
        binding.recyclerViewDuties.layoutManager = LinearLayoutManager(context)

        calendarViewCreated()

        binding.btnFabTack.setOnClickListener {
            val bottomsheet = TaskBottomsheetFragment()
            bottomsheet.show(parentFragmentManager, null)
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun calendarViewCreated() {

        var selectedDate: LocalDate? = null

        class DayViewContainer(view: View) : ViewContainer(view) {
            val binding1 = ItemCalendarDayTaskBinding.bind(view)
            val dayText = binding1.exSevenDayText
            val dateText = binding1.exSevenDateText
            val selectedView = binding1.exSevenSelectedView

            lateinit var day: CalendarDay

            init {

                view.setOnClickListener {

                    if (selectedDate != day.date) {
                        val oldDate = selectedDate
                        selectedDate = day.date
                        binding.exSevenCalendar.notifyDateChanged(day.date)
                        oldDate?.let { binding.exSevenCalendar.notifyDateChanged(it) }
                    }
                }
            }

            fun bind(day: CalendarDay) {

                val persianlCalendar = day.date.yearMonth.persianlCalendar()
                val monthTitle =
                    "${persianlCalendar.persianMonthName} ${persianlCalendar.persianYear.toString().persianNumbers()}"
                binding.txtYM.text = monthTitle

                this.day = day
                dateText.text = day.persianCalendar.persianDay.toString().persianNumbers()
                dayText.text = day.persianCalendar.persianWeekDayName.persianNumbers()

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
//                binding1.exSevenDayText.setTextColor(
//                    ContextCompat.getColor(
//                        view.context,
//                        colorRes
//                    )
//                )
                selectedView.isVisible = day.date == selectedDate

            }
        }

        binding.exSevenCalendar.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: CalendarDay) = container.bind(day)
        }

        val currentMonth = YearMonth.now()
        // Value for firstDayOfWeek does not matter since inDates and outDates are not generated.
        binding.exSevenCalendar.setup(currentMonth.minusMonths(10), currentMonth.plusMonths(10), DayOfWeek.SATURDAY)
        binding.exSevenCalendar.scrollToDate(LocalDate.now())

    }

    override fun onTaskClicked(task: Task, adapterPosition: Int) {

    }

    override fun onTaskLongClicked(task: Task, adapterPosition: Int) {

    }

}