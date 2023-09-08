package ir.aliza.sherkatmanage.adapter

import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.kizitonwose.calendarview.utils.persian.PersianCalendar
import ir.aliza.sherkatmanage.DataBase.Employee
import ir.aliza.sherkatmanage.DataBase.TaskEmployee
import ir.aliza.sherkatmanage.DataBase.TaskEmployeeDao
import ir.aliza.sherkatmanage.R
import ir.aliza.sherkatmanage.databinding.ItemEmployeeTaskBinding
import org.joda.time.DateTime
import org.joda.time.Days

class TaskEmployeeAdapter(
    private val data: ArrayList<TaskEmployee>,
    private val tackEvent: TaskEvent,
    val taskEmployeeDao: TaskEmployeeDao,
    val employee: Employee,
    val today: PersianCalendar
) :

    RecyclerView.Adapter<TaskEmployeeAdapter.TaskEmployeeViewHolder>() {

    lateinit var binding: ItemEmployeeTaskBinding

    inner class TaskEmployeeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val btnMenuTask = binding.btnMenuTaskEmployee

        fun bindData(position: Int, clickListener: TaskEvent) {

            binding.txtTack.text = data[position].nameTask
            binding.txtDescription.text = data[position].descriptionTask

            if (data[position].doneTask!!) {
                binding.txtDedlineTaskEmployee.visibility = View.GONE
                binding.imgDone.visibility = View.VISIBLE
            } else {
                val startDate =
                    DateTime(today.persianYear, today.persianMonth, today.persianDay, 0, 0, 0)
                val endDate = DateTime(
                    data[position].yearCreation,
                    data[position].monthCreation,
                    data[position].dayCreation,
                    0,
                    0,
                    0
                )
                var daysBetween = Days.daysBetween(startDate, endDate).days

                if (daysBetween > 0)
                    binding.txtDedlineTaskEmployee.text = "$daysBetween روز دیگر باقیمانده است "
                else if (daysBetween == 0)
                    binding.txtDedlineTaskEmployee.text = "امروز باید تسک تحویل داده شه"
                else {
                    daysBetween = -daysBetween
                    binding.txtDedlineTaskEmployee.text = "$daysBetween روز از تحویل تسک گذشته "
                    val shape = GradientDrawable()
                    shape.shape = GradientDrawable.RECTANGLE
                    shape.cornerRadii = floatArrayOf(40f, 40f, 40f, 40f, 40f, 40f, 40f, 40f)
                    shape.setStroke(5, ContextCompat.getColor(binding.root.context, R.color.red_800))
                    binding.txtDedlineTaskEmployee.background = shape
                }
            }

            binding.btnMenuTaskEmployee.setOnClickListener {
                clickListener.onMenuItemClick(data[position], position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskEmployeeViewHolder {
        binding =
            ItemEmployeeTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskEmployeeViewHolder(binding.root)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: TaskEmployeeViewHolder, position: Int) {
        holder.bindData(position, tackEvent)
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
        fun onTaskClicked(
            task: TaskEmployee,
            position: Int,
            day: String,
            monthName: String
        )

        fun onTaskLongClicked(task: TaskEmployee, position: Int)
        fun onMenuItemClick(subTask: TaskEmployee, position: Int)
    }

}