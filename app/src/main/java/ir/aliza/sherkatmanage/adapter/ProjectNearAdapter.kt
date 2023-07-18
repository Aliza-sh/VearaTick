package ir.aliza.sherkatmanage.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kizitonwose.calendarview.utils.persian.PersianCalendar
import com.kizitonwose.calendarview.utils.persian.withMonth
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

            val project = projectDao.getProject(data[position].idProject!!)
            val teamProjectDao = AppDatabase.getDataBase(itemView.context).teamProjectDao
            val teamProjectData = teamProjectDao.getListTeamProject(project!!.idProject!!)
            val teamProjectAdapter = TeamProjectAdapter(ArrayList(teamProjectData))
            binding.recyclerView.adapter = teamProjectAdapter

            val calendar = PersianCalendar()
            val inDay = calendar.persianDay

            val day = inDay + data[position].dayProject.toInt()

            val monthValue = day / 30 + calendar.persianMonth
            val dayValue = (day % 30)

            binding.txtNamePro.text = data[position].nameProject
            binding.txtTimePro.text =
                dayValue.toString() + " " + calendar.withMonth(monthValue).persianMonthName

            binding.progressLimit4.progress = data[position].progressProject!!
            binding.txtProg.text = data[position].progressProject!!.toString() + "%"

            itemView.setOnClickListener {
                projectNearEvents.onProjectClicked(
                    data[position],
                    dayValue.toString(),
                    calendar.withMonth(monthValue).persianMonthName
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
        fun onProjectClicked(project: Project, day: String, monthName: String)
        fun onProjectLongClicked(project: Project, position: Int)
    }
}