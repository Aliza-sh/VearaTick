package ir.aliza.sherkatmanage.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import ir.aliza.sherkatmanage.DataBase.Project
import ir.aliza.sherkatmanage.DataBase.ProjectDao
import ir.aliza.sherkatmanage.DataBase.SubTaskProject
import ir.aliza.sherkatmanage.DataBase.SubTaskProjectDao
import ir.aliza.sherkatmanage.R
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

        fun bindData(position: Int) {

            binding.txtTack.text = data[position].nameSubTask
            binding.txtDescription.text = data[position].descriptionSubTask

            if (data[position].doneSubTask!!) {
                binding.txtDedlineSubTask.visibility = View.GONE
                binding.imgDone.visibility = View.VISIBLE
            } else {
                if (data[position].noDeadlineSubTask!!) {
                    binding.txtDedlineSubTask.text = " "
                } else {
                    if (data[position].dateDeadlineSubTask != "" && data[position].watchDeadlineSubTask == "")
                        binding.txtDedlineSubTask.text = data[position].dateDeadlineSubTask
                    else if (data[position].dateDeadlineSubTask == "" && data[position].watchDeadlineSubTask != "")
                        binding.txtDedlineSubTask.text =
                            "امروز" + "\n" + data[position].watchDeadlineSubTask
                    else if (data[position].dateDeadlineSubTask != "" && data[position].watchDeadlineSubTask != "")
                        binding.txtDedlineSubTask.text =
                            data[position].dateDeadlineSubTask + "\n" + data[position].watchDeadlineSubTask
                }
            }

            val popupMenu = PopupMenu(itemView.context,binding.btnMenuSubTaskProject)
            popupMenu.menuInflater.inflate(R.menu.menu_task_project, popupMenu.menu)
            binding.btnMenuSubTaskProject.setOnClickListener {
                onMenuClicked(popupMenu, position,it)
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

    private fun onMenuClicked(popupMenu: PopupMenu, position: Int, view: View) {

            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {

                    R.id.menu_project_edit -> {
                        Toast.makeText(view.context, "jndljknas", Toast.LENGTH_SHORT).show()
                    }

                    R.id.menu_project_done -> {

                        if (data[position].doneSubTask!!) {

                            val menu = popupMenu.menu
                            val groupId = menu.getItem(0).groupId
                            menu.setGroupCheckable(groupId, true, true)
                            menu.findItem(groupId).title = "تکمیل شد"

                            binding.txtDedlineSubTask.visibility = View.VISIBLE
                            binding.imgDone.visibility = View.GONE

                            val newSubTask = SubTaskProject(
                                idSubTask=data[position].idSubTask,
                                idProject = data[position].idProject,
                                nameSubTask = data[position].nameSubTask,
                                noDeadlineSubTask = data[position].noDeadlineSubTask,
                                doneSubTask = false,
                                descriptionSubTask = data[position].descriptionSubTask,
                                watchDeadlineSubTask = data[position].watchDeadlineSubTask,
                                dateDeadlineSubTask = data[position].dateDeadlineSubTask,
                                volumeTask = data[position].volumeTask
                            )
                            subTaskProjectDao.update(newSubTask)
                            updateTask(newSubTask,position)

                        } else {

                            val menu = popupMenu.menu
                            val groupId = menu.getItem(0).groupId
                            menu.setGroupCheckable(groupId, true, true)
                            menu.findItem(groupId).title = "تکمیل نشد"

                            binding.txtDedlineSubTask.visibility = View.GONE
                            binding.imgDone.visibility = View.VISIBLE

                            val newSubTask = SubTaskProject(
                                idSubTask=data[position].idSubTask,
                                idProject = data[position].idProject,
                                nameSubTask = data[position].nameSubTask,
                                noDeadlineSubTask = data[position].noDeadlineSubTask,
                                doneSubTask = true,
                                descriptionSubTask = data[position].descriptionSubTask,
                                watchDeadlineSubTask = data[position].watchDeadlineSubTask,
                                dateDeadlineSubTask = data[position].dateDeadlineSubTask,
                                volumeTask = data[position].volumeTask
                            )
                            subTaskProjectDao.update(newSubTask)
                            updateTask(newSubTask,position)
                        }
                    }

                    R.id.menu_project_delete -> {

                    }
                }
                true
            }
            popupMenu.show()

    }


    interface SubTaskEvent {
        fun onSubTaskClicked(
            task: SubTaskProject,
            position: Int,
        )

        fun onSubTaskLongClicked(subTask: SubTaskProject, position: Int)
    }
}