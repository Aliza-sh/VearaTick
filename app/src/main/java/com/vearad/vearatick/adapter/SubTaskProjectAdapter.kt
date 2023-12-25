package com.vearad.vearatick.adapter

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.threetenabp.AndroidThreeTen
import com.kizitonwose.calendarview.utils.persian.toPersianCalendar
import com.vearad.vearatick.DataBase.AppDatabase
import com.vearad.vearatick.DataBase.Project
import com.vearad.vearatick.DataBase.ProjectDao
import com.vearad.vearatick.DataBase.SubTaskProject
import com.vearad.vearatick.DataBase.SubTaskProjectDao
import com.vearad.vearatick.R
import com.vearad.vearatick.databinding.ItemSubTaskBinding
import org.joda.time.DateTime
import org.joda.time.Days
import org.threeten.bp.LocalDate

class SubTaskProjectAdapter(
    private val data: ArrayList<SubTaskProject>,
    private val subTaskEvent: SubTaskEvent,
    val project: Project,
    val projectDao: ProjectDao,
    val subTaskProjectDao: SubTaskProjectDao,
) :

    RecyclerView.Adapter<SubTaskProjectAdapter.SubTaskProjectViewHolder>() {

    lateinit var binding: ItemSubTaskBinding

    inner class SubTaskProjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val btnMenuSubTaskProject = binding.btnMenuSubTaskProject

        fun bindData(position: Int, clickListener: SubTaskEvent) {
            AndroidThreeTen.init(itemView.context)

            binding.txtTack.text = data[position].nameSubTask
            binding.txtDescription.text = data[position].descriptionSubTask
            val today = LocalDate.now().toPersianCalendar()

            if (data[position].doneSubTask!!) {
                binding.txtDedlineSubTask.visibility = View.GONE
                binding.imgDone.visibility = View.VISIBLE
            }
            else {
                val startDate =
                    DateTime(today.persianYear, today.persianMonth , today.persianDay, 0, 0, 0)
                val endDate = DateTime(
                    data[position].yearDeadline,
                    data[position].monthDeadline,
                    data[position].dayDeadline,
                    0,
                    0,
                    0
                )
                var daysBetween = Days.daysBetween(startDate, endDate).days

                if (daysBetween > 0)
                    binding.txtDedlineSubTask.text = "$daysBetween روز دیگر باقیمانده است "
                else if (daysBetween == 0)
                    binding.txtDedlineSubTask.text = "امروز باید تسک تحویل داده شه"
                else {
                    daysBetween = -daysBetween
                    binding.txtDedlineSubTask.text = "$daysBetween روز از تحویل تسک گذشته "
                    val shape = GradientDrawable()
                    shape.shape = GradientDrawable.RECTANGLE
                    shape.cornerRadii = floatArrayOf(40f, 40f, 40f, 40f, 40f, 40f, 40f, 40f)
                    shape.setStroke(
                        5,
                        ContextCompat.getColor(binding.root.context, R.color.red_800)
                    )
                    binding.txtDedlineSubTask.background = shape
                }
            }

            if (data[position].idSubTask != null) {
                val teamSubTaskDao = AppDatabase.getDataBase(itemView.context).teamSubTaskDao
                val employeeTeamSubTask = teamSubTaskDao.getListTeamSubTask(
                    project.idProject!!,
                    data[position].idSubTask!!
                )

                val teamSubTaskAdapter = AvatarTeamSubTaskAdapter(ArrayList(employeeTeamSubTask))
                binding.rcvTeamSubTask.adapter = teamSubTaskAdapter
            }

            binding.btnMenuSubTaskProject.setOnClickListener {
                clickListener.onMenuItemClick(data[position], position)
            }
            binding.btnAddNewPerson.setOnClickListener {
                clickListener.onTeamSubTaskClick(data[position], project, position)
            }
            itemView.setOnLongClickListener {
                subTaskEvent.onSubTaskLongClicked(data[position], position)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubTaskProjectViewHolder {
        binding = ItemSubTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SubTaskProjectViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: SubTaskProjectViewHolder, position: Int) {
        holder.bindData(position, subTaskEvent)
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

    fun addTask(newTask: SubTaskProject) {
        data.add(data.size, newTask)
        notifyItemInserted(data.size)
    }

    fun updateTask(newTask: SubTaskProject, position: Int) {
        data.set(position, newTask)
        notifyItemChanged(position)
    }

    fun deleteSubTask(oldTask: SubTaskProject, oldPosition: Int) {
        data.remove(oldTask)
        notifyItemRemoved(oldPosition)
    }

    fun clearAll() {
        data.clear()
        notifyDataSetChanged()
    }

    interface SubTaskEvent {

        fun onSubTaskClicked(
            task: SubTaskProject,
            position: Int,
        )

        fun onSubTaskLongClicked(subTask: SubTaskProject, position: Int)
        fun onMenuItemClick(subTask: SubTaskProject, position: Int)
        fun onTeamSubTaskClick(subTask: SubTaskProject, project: Project, position: Int)

    }
}