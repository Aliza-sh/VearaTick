package ir.aliza.sherkatmanage.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.aliza.sherkatmanage.DataBase.AppDatabase
import ir.aliza.sherkatmanage.DataBase.Project
import ir.aliza.sherkatmanage.DataBase.ProjectDao
import ir.aliza.sherkatmanage.DataBase.SubTaskProject
import ir.aliza.sherkatmanage.DataBase.SubTaskProjectDao
import ir.aliza.sherkatmanage.databinding.ItemSubTaskBinding

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

            binding.txtTack.text = data[position].nameSubTask
            binding.txtDescription.text = data[position].descriptionSubTask

            if (data[position].doneSubTask!!) {
                binding.txtDedlineSubTask.visibility = View.GONE
                binding.imgDone.visibility = View.VISIBLE
            } else {
                if (data[position].noDeadlineSubTask!!) {
                    binding.txtDedlineSubTask.text = " ددلاین ندارد"
                } else {
                    if (data[position].dateDeadlineSubTask != "" && data[position].watchDeadlineSubTask == "")
                        binding.txtDedlineSubTask.text = data[position].dateDeadlineSubTask
                    else if (data[position].dateDeadlineSubTask == "" && data[position].watchDeadlineSubTask != "")
                        binding.txtDedlineSubTask.text =
                            "امروز" + "\n" + data[position].watchDeadlineSubTask
                    else if (data[position].dateDeadlineSubTask != "" && data[position].watchDeadlineSubTask != "")
                        binding.txtDedlineSubTask.text =
                            data[position].watchDeadlineSubTask + "\n" + data[position].dateDeadlineSubTask
                }
            }

            if (data[position].idSubTask != null) {
                val teamSubTaskDao = AppDatabase.getDataBase(itemView.context).teamSubTaskDao
                val  employeeTeamSubTask = teamSubTaskDao.getListTeamSubTask(
                    project.idProject!!,
                    idSubTask = data[position].idSubTask!!
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