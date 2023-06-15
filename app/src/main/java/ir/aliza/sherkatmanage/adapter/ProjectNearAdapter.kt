package ir.aliza.sherkatmanage.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.aliza.sherkatmanage.DataBase.Project
import ir.aliza.sherkatmanage.databinding.ItemProjectBinding

class ProjectNearAdapter(private val data: ArrayList<Project>, private val projectNearEvents: ProjectNearEvents) :
    RecyclerView.Adapter<ProjectNearAdapter.ProjectNearViewHolder>() {

    lateinit var binding: ItemProjectBinding

    inner class ProjectNearViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {

        @SuppressLint("SetTextI18n")
        fun bindData(position: Int) {

            binding.txtNamePro.text = data[position].nameProject
            binding.txtTimePro.text = data[position].dayProject + " روز"

            itemView.setOnClickListener {
                projectNearEvents.onProjectClicked(data[position])
            }

            itemView.setOnLongClickListener {
                projectNearEvents.onProjectLongClicked(data[position], position)
                true
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectNearViewHolder {
        binding = ItemProjectBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProjectNearViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ProjectNearViewHolder, position: Int) {
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

    fun addProject(newProject: Project) {
        data.add(0, newProject)
        notifyItemInserted(0)
    }

    fun removeProject(oldProject: Project, oldPosition: Int) {
        data.remove(oldProject)
        notifyItemRemoved(oldPosition)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newList: ArrayList<Project>) {
        data.clear()
        data.addAll(newList)
        notifyDataSetChanged()
    }

    interface ProjectNearEvents {
        fun onProjectClicked(project: Project)
        fun onProjectLongClicked(project: Project, position: Int)
    }
}