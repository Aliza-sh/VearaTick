package ir.aliza.sherkatmanage.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.aliza.sherkatmanage.DataBase.Task
import ir.aliza.sherkatmanage.databinding.ItemTaskBinding

class TaskAdapter(private val data: ArrayList<Task>, private val tackEvent: TaskEvent) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    lateinit var binding: ItemTaskBinding

    inner class TaskViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){

        fun bindData(position: Int) {
            itemView.setOnClickListener {
                tackEvent.onTaskClicked(data[adapterPosition], adapterPosition)
            }

            itemView.setOnLongClickListener {
                tackEvent.onTaskLongClicked(data[adapterPosition], adapterPosition)
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
        return 20
    }

    fun addTask(newTask: Task) {
        data.add(0, newTask)
        notifyItemInserted(0)
    }

    interface TaskEvent {
        fun onTaskClicked(task: Task, adapterPosition: Int)
        fun onTaskLongClicked(task: Task, adapterPosition: Int)
    }
}