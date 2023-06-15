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

            binding.txtTack.text = data[position].nameTask
            binding.txtDescription.text = data[position].descriptionTask
            binding.txtTime.text = data[position].timeTask + " روز "

            itemView.setOnClickListener {
                tackEvent.onTaskClicked(data[position], position)
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

    fun addTask(newTask: Task) {
        data.add(0, newTask)
        notifyItemInserted(0)
    }
    fun removeEmployee(oldTask: Task, oldPosition: Int) {
        data.remove(oldTask)
        notifyItemRemoved(oldPosition)
    }
    interface TaskEvent {
        fun onTaskClicked(task: Task, position: Int)
        fun onTaskLongClicked(task: Task, position: Int)
    }
}