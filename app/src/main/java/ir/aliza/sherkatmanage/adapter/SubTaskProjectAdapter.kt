package ir.aliza.sherkatmanage.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import ir.aliza.sherkatmanage.DataBase.Project
import ir.aliza.sherkatmanage.DataBase.ProjectDao
import ir.aliza.sherkatmanage.DataBase.SubTaskProject
import ir.aliza.sherkatmanage.DataBase.SubTaskProjectDao
import ir.aliza.sherkatmanage.databinding.FragmentProjectInformationBinding
import ir.aliza.sherkatmanage.databinding.ItemSubTaskBinding

class SubTaskProjectAdapter(
    private val data: ArrayList<SubTaskProject>,
    private val subTaskEvent: SubTaskEvent,
    val project: Project,
    val subTaskProjectDao: SubTaskProjectDao,
    val projectDao: ProjectDao,
    val binding1: FragmentProjectInformationBinding,
) :

    RecyclerView.Adapter<SubTaskProjectAdapter.SubTaskProjectViewHolder>() {

    lateinit var binding: ItemSubTaskBinding

    inner class SubTaskProjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindData(position: Int) {

            binding.txtTack.text = data[position].nameSubTask
            binding.txtDescription.text = data[position].descriptionTask
            if (data[position].doneSubTask != null) {
                binding.ckbDoneTaskEmployee.isChecked = data[position].doneSubTask == true
            }

            binding.ckbDoneTaskEmployee.setOnCheckedChangeListener() { compoundButton: CompoundButton, b: Boolean ->

                val newSubTask = SubTaskProject(
                    idSubTask = data[position].idSubTask,
                    idProject = project.idProject!!,
                    nameSubTask = data[position].nameSubTask,
                    descriptionTask = data[position].descriptionTask,
                    doneSubTask = b

                )
                subTaskProjectDao.update(newSubTask)

                val project1 = projectDao.getProject(project.idProject)

                var numberDonSubTaskProject = project1!!.numberDoneSubTaskProject
                if (numberDonSubTaskProject == null)
                    numberDonSubTaskProject = 0

                if (numberDonSubTaskProject != null && b)
                    numberDonSubTaskProject++
                if (numberDonSubTaskProject != null && b == false)
                    numberDonSubTaskProject--
                if (numberDonSubTaskProject> project1.numberSubTaskProject!! || numberDonSubTaskProject<0)
                    numberDonSubTaskProject = project1.numberSubTaskProject

                val newProject = Project(
                    idProject = project1.idProject,
                    nameProject = project1.nameProject,
                    dayProject = project1.dayProject,
                    watchProject = project1.watchProject,
                    typeProject = project1.typeProject,
                    descriptionProject = project1.descriptionProject,
                    numberSubTaskProject = project1.numberSubTaskProject,
                    numberDoneSubTaskProject = numberDonSubTaskProject,
                    progressProject = project1.progressProject,
                    year = project.year,
                    month = project.month,
                    day = project.day

                )
                projectDao.update(newProject)

                progressPro()

                binding1.txtNumTaskPro.text =
                    numberDonSubTaskProject.toString() + " از " + project1.numberSubTaskProject

                binding1.progressPro.progress = progressPro()
                binding1.txtProg.text = progressPro().toString() + "%"

            }

            itemView.setOnClickListener {
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

    fun addTask(newTask: SubTaskProject) {
        data.add(data.size, newTask)
        notifyItemInserted(data.size)
    }

    fun deleteSubTask(oldTask: SubTaskProject, oldPosition: Int) {
        data.remove(oldTask)
        notifyItemRemoved(oldPosition)
    }

    fun clearAll() {
        data.clear()
        notifyDataSetChanged()
    }

    private fun progressPro():Int {
        val project1 = projectDao.getProject(project.idProject!!)

        val numSubTask = project1!!.numberSubTaskProject
        val numDoneSubTask = project1.numberDoneSubTaskProject
        var efficiencyProject = 0

        if (numSubTask != null) {
            efficiencyProject = ((numDoneSubTask!!.toDouble() / numSubTask) * 100).toInt()

        }
        val newProject = Project(
            idProject = project1.idProject,
            nameProject = project1.nameProject,
            dayProject = project1.dayProject,
            watchProject = project1.watchProject,
            typeProject = project1.typeProject,
            descriptionProject = project1.descriptionProject,
            numberSubTaskProject = project1.numberSubTaskProject,
            numberDoneSubTaskProject = project1.numberDoneSubTaskProject,
            progressProject = efficiencyProject,
            year = project.year,
            month = project.month,
            day = project.day

        )
        projectDao.update(newProject)

        return efficiencyProject
    }

    interface SubTaskEvent {
        fun onSubTaskClicked(
            task: SubTaskProject,
            position: Int,
        )

        fun onSubTaskLongClicked(subTask: SubTaskProject, position: Int)
    }
}