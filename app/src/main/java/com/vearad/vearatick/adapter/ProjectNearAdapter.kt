package com.vearad.vearatick.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.threetenabp.AndroidThreeTen
import com.kizitonwose.calendarview.utils.persian.toPersianCalendar
import com.vearad.vearatick.DataBase.AppDatabase
import com.vearad.vearatick.DataBase.Project
import com.vearad.vearatick.DataBase.ProjectDao
import com.vearad.vearatick.databinding.ItemProjectBinding
import org.joda.time.DateTime
import org.joda.time.Days
import org.threeten.bp.LocalDate

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
            AndroidThreeTen.init(itemView.context)

            val teamProjectDao = AppDatabase.getDataBase(itemView.context).teamProjectDao

            if (data[position].idProject != null) {
                val teamProjectData = teamProjectDao.getListTeamProject(data[position].idProject!!)
                val teamProjectAdapter = AvatarTeamProjectAdapter(ArrayList(teamProjectData))
                binding.recyclerView.adapter = teamProjectAdapter
            }

            /*if (data[position].typeProject == "سایت")
                binding.imgProject.setImageResource(R.drawable.img_site)
            if (data[position].typeProject == "بک اند")
                binding.imgProject.setImageResource(R.drawable.img_backend)
            else if (data[position].typeProject == "فرانت اند")
                binding.imgProject.setImageResource(R.drawable.img_frontend)
            else if (data[position].typeProject == "رباتیک")
                binding.imgProject.setImageResource(R.drawable.img_robotic)
            else if (data[position].typeProject == "طراحی")
                binding.imgProject.setImageResource(R.drawable.img_designing)
            else if (data[position].typeProject == "سئو")
                binding.imgProject.setImageResource(R.drawable.img_seo)*/

            binding.txtNamePro.text = data[position].nameProject

            if (data[position].doneProject!!){
                binding.imgProcess.visibility = INVISIBLE
                binding.imgComplete.visibility = VISIBLE

                val shape = GradientDrawable()
                shape.shape = GradientDrawable.RECTANGLE
                shape.cornerRadii = floatArrayOf(40f, 40f, 40f, 40f, 40f, 40f, 40f, 40f)
                shape.setColor(
                    Color.parseColor("#60AA83")
                )
                binding.txtDatePro.background = shape
                binding.txtDatePro.text = data[position].valueCalendar
                binding.viewComplete.visibility = VISIBLE
                if (data[position].noDeadlineProject!!) {
                    binding.txtDatePro.text = "پروژه ددلاین \nندارد"
                    binding.viewComplete.visibility = INVISIBLE
                }
            }
            else {
                if (!data[position].noDeadlineProject!!) {
                    val today = LocalDate.now().toPersianCalendar()
                    val startDate =
                        DateTime(today.persianYear, today.persianMonth , today.persianDay, 0, 0, 0)
                    val endDate = DateTime(
                        data[position].yearCreation,
                        data[position].monthCreation,
                        data[position].dayCreation,
                        0,
                        0,
                        0
                    )
                    var daysBetween = Days.daysBetween(startDate, endDate).days

                    if (daysBetween > 0) {
                        val shape = GradientDrawable()
                        shape.shape = GradientDrawable.RECTANGLE
                        shape.cornerRadii = floatArrayOf(40f, 40f, 40f, 40f, 40f, 40f, 40f, 40f)
                        shape.setColor(
                            Color.parseColor("#E600ADB5")
                        )
                        binding.txtDatePro.background = shape
                        binding.txtDatePro.text = data[position].valueCalendar
                    }
                    else if (daysBetween == 0) {
                        binding.txtDatePro.text = "امروز"
                        val shape = GradientDrawable()
                        shape.shape = GradientDrawable.RECTANGLE
                        shape.cornerRadii = floatArrayOf(40f, 40f, 40f, 40f, 40f, 40f, 40f, 40f)
                        shape.setColor(
                            Color.parseColor("#FCCD66")
                        )
                        binding.txtDatePro.background = shape
                    }
                    else {
                        val shape = GradientDrawable()
                        shape.shape = GradientDrawable.RECTANGLE
                        shape.cornerRadii = floatArrayOf(40f, 40f, 40f, 40f, 40f, 40f, 40f, 40f)
                        shape.setColor(
                            Color.parseColor("#c62828")
                        )
                        binding.txtDatePro.background = shape
                        binding.txtDatePro.text = "مهلت پروژه\n به اتمام رسید"
                    }
                } else {
                    binding.txtDatePro.text = "پروژه ددلاین \nندارد"
                    val shape = GradientDrawable()
                    shape.shape = GradientDrawable.RECTANGLE
                    shape.cornerRadii = floatArrayOf(40f, 40f, 40f, 40f, 40f, 40f, 40f, 40f)
                    shape.setColor(
                        Color.parseColor("#929292")
                    )
                    binding.txtDatePro.background = shape
                }
            }

            val subTaskProjectDao = AppDatabase.getDataBase(itemView.context).subTaskProjectDao
            if (data[position].idProject!=null) {
                val totalVolumeProject =
                    subTaskProjectDao.getTotalVolumeTaskSum(data[position].idProject!!)
                val doneVolumeProject =
                    subTaskProjectDao.getDoneVolumeTaskSum(data[position].idProject!!, true)
                var efficiencyProject = 0

                if (doneVolumeProject != null)
                    efficiencyProject =
                        ((doneVolumeProject.toDouble() / totalVolumeProject) * 100).toInt()

                binding.progressLimit4.progress = efficiencyProject
                binding.txtProg.text = "$efficiencyProject%"
            }
            itemView.setOnClickListener {
                projectNearEvents.onProjectClicked(
                    data[position],
                    position,
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
        )

        fun onProjectLongClicked(project: Project, position: Int)
    }
}