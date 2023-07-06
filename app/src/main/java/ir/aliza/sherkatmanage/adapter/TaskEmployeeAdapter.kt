package ir.aliza.sherkatmanage.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kizitonwose.calendarview.utils.persian.toPersianCalendar
import ir.aliza.sherkatmanage.DataBase.TaskEmployee
import ir.aliza.sherkatmanage.databinding.ItemTaskBinding
import org.threeten.bp.LocalDate

class TaskEmployeeAdapter(
    private val data: ArrayList<TaskEmployee>,
    private val tackEvent: TaskEvent,
    private val inDay: Int,
    private val date: LocalDate
) :

    RecyclerView.Adapter<TaskEmployeeAdapter.TaskViewHolder>() {

    lateinit var binding: ItemTaskBinding

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindData(position: Int) {

            val day = inDay + data[position].dayTask.toInt()

            val monthValue = day / 30 + date.monthValue
            val dayValue = (day % 30)

            binding.txtTack.text = data[position].nameTask
            binding.txtDescription.text = data[position].descriptionTask
            binding.txtTime.text =  dayValue.toString() + " " + date.withMonth(monthValue).toPersianCalendar().persianMonthName

            itemView.setOnClickListener {
                tackEvent.onTaskClicked(data[position], position , dayValue.toString() , date.withMonth(monthValue).toPersianCalendar().persianMonthName)
            }

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
        fun onTaskClicked(
            task: TaskEmployee,
            position: Int,
            day: String,
            monthName: String
        )
        fun onTaskLongClicked(task: TaskEmployee, position: Int)
    }

}