package ir.aliza.sherkatmanage.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.kizitonwose.calendarview.utils.persian.PersianCalendar
import com.kizitonwose.calendarview.utils.persian.withMonth
import ir.aliza.sherkatmanage.DataBase.AppDatabase
import ir.aliza.sherkatmanage.DataBase.EfficiencyEmployee
import ir.aliza.sherkatmanage.DataBase.Employee
import ir.aliza.sherkatmanage.DataBase.TaskEmployee
import ir.aliza.sherkatmanage.DataBase.TaskEmployeeDao
import ir.aliza.sherkatmanage.databinding.ItemTaskBinding
import org.joda.time.DateTime
import org.joda.time.Days

class TaskEmployeeAdapter(
    private val data: ArrayList<TaskEmployee>,
    private val tackEvent: TaskEvent,
    private val inDay: Int,
    val taskEmployeeDao: TaskEmployeeDao,
    val employee: Employee
) :

    RecyclerView.Adapter<TaskEmployeeAdapter.TaskViewHolder>() {

    lateinit var binding: ItemTaskBinding

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @RequiresApi(Build.VERSION_CODES.O)
        fun bindData(position: Int) {
            val efficiencyEmployeeDao = AppDatabase.getDataBase(itemView.context).efficiencyDao

            val date = PersianCalendar()
            val day = inDay + data[position].dayTaskDeadline
            val monthValue = day / 30 + date.persianMonth
            val dayValue = (day % 30)

            binding.txtTack.text = data[position].nameTask
            binding.txtDescription.text = data[position].descriptionTask
            binding.txtTime.text =
                dayValue.toString() + " " + date.withMonth(monthValue).persianMonthName

            if (data[position].doneTask != null) {
                binding.ckbDoneTaskEmployee.isChecked = data[position].doneTask == true
            }

            binding.ckbDoneTaskEmployee.setOnCheckedChangeListener() { compoundButton: CompoundButton, b: Boolean ->

                if (b) {

                    val efficiencyEmployee =
                        efficiencyEmployeeDao.getEfficiencyEmployee(data[position].idEmployee)

                    val newTask = TaskEmployee(
                        idTask = data[position].idTask,
                        idEmployee = data[position].idEmployee,
                        nameTask = data[position].nameTask,
                        dayTaskDeadline = data[position].dayTaskDeadline,
                        watchTaskDeadline = data[position].watchTaskDeadline,
                        descriptionTask = data[position].descriptionTask,
                        volumeTask = data[position].volumeTask,
                        doneTask = b,
                        yearDoneTask = date.persianYear,
                        monthDoneTask = date.persianMonth,
                        dayDoneTask = date.persianDay,
                        watchDoneTask = date.time.hours,
                        yearCreation = data[position].yearCreation,
                        monthCreation = data[position].monthCreation,
                        dayCreation = data[position].dayCreation,
                        watchCreation = data[position].watchCreation,
                    )
                    taskEmployeeDao.update(newTask)

                    val task = taskEmployeeDao.getInTaskDay(data[position].idTask!!,data[position].dayCreation)
                    var efficiencyWeekDuties = 0
                    var gradeDuties = data[position].volumeTask
                    val startDate = DateTime(task!!.yearCreation, task.monthCreation, task.dayCreation, 0, 0, 0)
                    val endDate = DateTime(task.yearDoneTask!!, task.monthDoneTask!!, task.dayDoneTask!!, 0, 0, 0)
                    var daysBetween = Days.daysBetween(startDate, endDate).days
                    if (daysBetween == 0)
                        daysBetween = 1

                    if (data[position].dayTaskDeadline <= 1) {
                        val task = taskEmployeeDao.getInTaskDay(data[position].idTask!!,data[position].dayCreation)
                        val watchTaskDeadline = task!!.watchTaskDeadline
                        val watchDoneTask = task.watchDoneTask
                        efficiencyWeekDuties = ((watchTaskDeadline.toDouble() / watchDoneTask!!) * 100).toInt()

                    } else {
                        val task = taskEmployeeDao.getInTaskDay(data[position].idTask!!,data[position].dayCreation)
                        val dayTaskDeadline = task!!.dayTaskDeadline
                        efficiencyWeekDuties = ((dayTaskDeadline.toDouble() / daysBetween)* 100).toInt()
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

                } else {

                    val efficiencyEmployee =
                        efficiencyEmployeeDao.getEfficiencyEmployee(data[position].idEmployee)

                    val task = taskEmployeeDao.getInTaskDay(data[position].idTask!!,data[position].dayCreation)
                    var efficiencyWeekDuties = 0
                    var gradeDuties = data[position].volumeTask
                    val startDate = DateTime(task!!.yearCreation, task.monthCreation, task.dayCreation, 0, 0, 0)
                    val endDate = DateTime(task.yearDoneTask!!, task.monthDoneTask!!, task.dayDoneTask!!, 0, 0, 0)
                    var daysBetween = Days.daysBetween(startDate, endDate).days
                    if (daysBetween == 0)
                        daysBetween = 1

                    if (data[position].dayTaskDeadline <= 1) {
                        val watchTaskDeadline = task.watchTaskDeadline
                        val watchDoneTask = task.watchDoneTask
                        efficiencyWeekDuties = ((watchTaskDeadline.toDouble() / watchDoneTask!!) * 100).toInt()

                    } else {
                        val dayTaskDeadline = task.dayTaskDeadline
                        efficiencyWeekDuties = ((dayTaskDeadline.toDouble() / daysBetween)* 100).toInt()
                    }

                    val newTask = TaskEmployee(
                        idTask = data[position].idTask,
                        idEmployee = data[position].idEmployee,
                        nameTask = data[position].nameTask,
                        dayTaskDeadline = data[position].dayTaskDeadline,
                        watchTaskDeadline = data[position].watchTaskDeadline,
                        descriptionTask = data[position].descriptionTask,
                        volumeTask = data[position].volumeTask,
                        doneTask = b,
                        yearDoneTask = 0,
                        monthDoneTask = 0,
                        dayDoneTask = 0,
                        watchDoneTask = 0,
                        yearCreation = data[position].yearCreation,
                        monthCreation = data[position].monthCreation,
                        dayCreation = data[position].dayCreation,
                        watchCreation = data[position].watchCreation,
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
                }
            }
//            itemView.setOnClickListener {
//                tackEvent.onTaskClicked(data[position], position , dayValue.toString() , date.withMonth(monthValue).toPersianCalendar().persianMonthName)
//            }

            itemView.setOnLongClickListener {
                tackEvent.onTaskLongClicked(data[position], position)
                true
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding.root)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bindData(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun addTask(newTask: TaskEmployee) {
        data.add(data.size, newTask)
        notifyItemInserted(data.size)
    }

    fun removeEmployee(oldTask: TaskEmployee, oldPosition: Int) {
        data.remove(oldTask)
        notifyItemRemoved(oldPosition)
    }

    fun clearAll() {
        data.clear()
        notifyDataSetChanged()
    }

    interface TaskEvent {
        //        fun onTaskClicked(
//            task: TaskEmployee,
//            position: Int,
//            day: String,
//            monthName: String
//        )
        fun onTaskLongClicked(task: TaskEmployee, position: Int)
    }

}