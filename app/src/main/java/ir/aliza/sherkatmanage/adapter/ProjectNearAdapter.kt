package ir.aliza.sherkatmanage.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.aliza.sherkatmanage.DataBase.AppDatabase
import ir.aliza.sherkatmanage.DataBase.Project
import ir.aliza.sherkatmanage.DataBase.ProjectDao
import ir.aliza.sherkatmanage.databinding.ItemProjectBinding

class ProjectNearAdapter(
    private val data: ArrayList<Project>,
    private val projectNearEvents: ProjectNearEvents,
    val projectDao: ProjectDao
) :
    RecyclerView.Adapter<ProjectNearAdapter.ProjectNearViewHolder>() {

    lateinit var binding: ItemProjectBinding

    inner class ProjectNearViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @SuppressLint("SetTextI18n")
        fun bindData(position: Int) {

            val teamProjectDao = AppDatabase.getDataBase(itemView.context).teamProjectDao

            if (data[position].idProject != null) {
                val teamProjectData = teamProjectDao.getListTeamProject(data[position].idProject!!)
                val teamProjectAdapter = AvatarTeamProjectAdapter(ArrayList(teamProjectData))
                binding.recyclerView.adapter = teamProjectAdapter
            }

            binding.txtNamePro.text = data[position].nameProject

            if (data[position].noDeadlineProject!!) {
                binding.txtDatePro.text = " "
            } else {
                if (data[position].dateProject != "" && data[position].watchProject == "")
                    binding.txtDatePro.text = data[position].dateProject
                else if (data[position].dateProject == "" && data[position].watchProject != "")
                    binding.txtDatePro.text = "امروز" + "\n" + data[position].watchProject
                else if (data[position].dateProject != "" && data[position].watchProject != "")
                    binding.txtDatePro.text =
                        data[position].dateProject + "\n" + data[position].watchProject
            }

            binding.progressLimit4.progress = data[position].progressProject!!
            binding.txtProg.text = data[position].progressProject!!.toString() + "%"

            itemView.setOnClickListener {
                projectNearEvents.onProjectClicked(
                    data[position],
                    position,
                    data[position].dateProject!!,
                    data[position].watchProject!!
                )
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
        data.add(data.size, newProject)
        notifyItemInserted(data.size)
    }

    fun removeProject(oldProject: Project, oldPosition: Int) {
        data.remove(oldProject)
        notifyItemRemoved(oldPosition)
    }

    fun updateProject(newProject: Project, position: Int) {
        data.set(position, newProject)
        notifyItemChanged(position)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newList: ArrayList<Project>) {
        data.clear()
        data.addAll(newList)
        notifyDataSetChanged()
    }

    interface ProjectNearEvents {
        fun onProjectClicked(
            project: Project,
            position: Int,
            dateProject: String,
            watchProject: String
        )

        fun onProjectLongClicked(project: Project, position: Int)
    }
}