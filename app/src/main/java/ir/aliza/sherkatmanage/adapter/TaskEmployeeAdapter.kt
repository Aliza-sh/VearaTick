package ir.aliza.sherkatmanage.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import com.kizitonwose.calendarview.utils.persian.PersianCalendar
import com.kizitonwose.calendarview.utils.persian.withMonth
import ir.aliza.sherkatmanage.DataBase.AppDatabase
import ir.aliza.sherkatmanage.DataBase.Employee
import ir.aliza.sherkatmanage.DataBase.TaskEmployee
import ir.aliza.sherkatmanage.DataBase.TaskEmployeeDao
import ir.aliza.sherkatmanage.databinding.ItemTaskBinding

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

        fun bindData(position: Int) {
            val employeeDao = AppDatabase.getDataBase(itemView.context).employeeDao

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

                    val employee = employeeDao.getEmployee(data[position].idEmployee)
                    val numberTask = employee?.numberDoneTask

                    val newTask = TaskEmployee(
                        idTask = data[position].idTask,
                        idEmployee = data[position].idEmployee,
                        nameTask = data[position].nameTask,
                        dayTaskDeadline = data[position].dayTaskDeadline,
                        watchTaskDeadline = data[position].watchTaskDeadline,
                        descriptionTask = data[position].descriptionTask,
                        typeTask = data[position].typeTask,
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
                    val newEmployee = Employee(
                        idEmployee = employee!!.idEmployee,
                        name = employee.name,
                        family = employee.family,
                        age = employee.age,
                        gender = employee.gender,
                        cellularPhone = employee.cellularPhone,
                        homePhone = employee.homePhone,
                        address = employee.address,
                        specialty = employee.specialty,
                        skill = employee.skill,
                        imgEmployee = employee.imgEmployee,
                        numberDoneTask = numberTask!! + 1
                    )
                    taskEmployeeDao.update(newTask)
                    employeeDao.update(newEmployee)
                } else {

                    val employee = employeeDao.getEmployee(data[position].idEmployee)
                    var numberTask = employee?.numberDoneTask
                    if (numberTask == 0)
                        numberTask = 1

                    val newTask = TaskEmployee(
                        idTask = data[position].idTask,
                        idEmployee = data[position].idEmployee,
                        nameTask = data[position].nameTask,
                        dayTaskDeadline = data[position].dayTaskDeadline,
                        watchTaskDeadline = data[position].watchTaskDeadline,
                        descriptionTask = data[position].descriptionTask,
                        typeTask = data[position].typeTask,
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
                    val newEmployee = Employee(
                        idEmployee = employee!!.idEmployee,
                        name = employee.name,
                        family = employee.family,
                        age = employee.age,
                        gender = employee.gender,
                        cellularPhone = employee.cellularPhone,
                        homePhone = employee.homePhone,
                        address = employee.address,
                        specialty = employee.specialty,
                        skill = employee.skill,
                        imgEmployee = employee.imgEmployee,
                        numberDoneTask = numberTask!! - 1
                    )
                    taskEmployeeDao.update(newTask)
                    employeeDao.update(newEmployee)
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
        data.add(0, newTask)
        notifyItemInserted(0)
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