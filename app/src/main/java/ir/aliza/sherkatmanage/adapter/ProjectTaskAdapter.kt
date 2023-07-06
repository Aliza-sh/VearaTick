package ir.aliza.sherkatmanage.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.aliza.sherkatmanage.DataBase.TaskEmployee
import ir.aliza.sherkatmanage.databinding.ItemTaskProjectBinding

class ProjectTaskAdapter(private val data: ArrayList<TaskEmployee>) :
    RecyclerView.Adapter<ProjectTaskAdapter.ProjectTaskHolder>() {

    lateinit var binding: ItemTaskProjectBinding

    inner class ProjectTaskHolder(itemView: View):RecyclerView.ViewHolder(itemView){

        fun bindData(position: Int) {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectTaskHolder {
        binding = ItemTaskProjectBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProjectTaskHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ProjectTaskHolder, position: Int) {
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

    fun addTask(newTask: TaskEmployee) {
        data.add(0, newTask)
        notifyItemInserted(0)
    }

    interface ProjectTaskAdapter {
        fun onTaskClicked(task: TaskEmployee, adapterPosition: Int)
        fun onTaskLongClicked(task: TaskEmployee, adapterPosition: Int)
    }
}