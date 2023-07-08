package ir.aliza.sherkatmanage.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import ir.aliza.sherkatmanage.DataBase.SubTaskEmployeeTack
import ir.aliza.sherkatmanage.DataBase.SubTaskEmployeeTackDao
import ir.aliza.sherkatmanage.DataBase.TaskEmployee
import ir.aliza.sherkatmanage.DataBase.TaskEmployeeDao
import ir.aliza.sherkatmanage.databinding.FragmentTaskInformationBinding
import ir.aliza.sherkatmanage.databinding.ItemSubTaskBinding

class SubTaskEmployeeTackAdapter(
    private val data: ArrayList<SubTaskEmployeeTack>,
    private val subTaskEvent: SubTaskEvent,
    val task: TaskEmployee,
    val subTaskProjectDao: SubTaskEmployeeTackDao,
    val taskEmployeeDao: TaskEmployeeDao,
    val binding1: FragmentTaskInformationBinding
) :

    RecyclerView.Adapter<SubTaskEmployeeTackAdapter.SubTaskViewHolder>() {

    lateinit var binding: ItemSubTaskBinding

    inner class SubTaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindData(position: Int) {


            binding.txtTack.text = data[position].nameSubTask
            binding.txtDescription.text = data[position].descriptionSubTask
            if (data[position].doneSubTask != null) {
                binding.checkBox.isChecked = data[position].doneSubTask == true
            }

            binding.checkBox.setOnCheckedChangeListener() { compoundButton: CompoundButton, b: Boolean ->

                val newSubTask = SubTaskEmployeeTack(
                    idSubTask = data[position].idSubTask,
                    idTask = task.idTask!!,
                    nameSubTask = data[position].nameSubTask,
                    descriptionSubTask = data[position].descriptionSubTask,
                    doneSubTask = b

                )
                subTaskProjectDao.update(newSubTask)

                val tack1 = taskEmployeeDao.getTaskDay(task.idTask,task.day.toInt())

                var numberDonSubTaskEmployeeTack = tack1!!.numberDoneSubTaskEmployeeTask
                if (numberDonSubTaskEmployeeTack == null)
                    numberDonSubTaskEmployeeTack = 0

                if (numberDonSubTaskEmployeeTack != null && b)
                    numberDonSubTaskEmployeeTack++
                if (numberDonSubTaskEmployeeTack != null && b == false)
                    numberDonSubTaskEmployeeTack--
                if (numberDonSubTaskEmployeeTack> tack1.numberSubTaskEmployeeTask!! || numberDonSubTaskEmployeeTack<0)
                    numberDonSubTaskEmployeeTack = tack1.numberSubTaskEmployeeTask

                val newTack = TaskEmployee(
                    idTask = tack1.idTask,
                    idEmployee = tack1.idEmployee,
                    nameTask = tack1.nameTask,
                    dayTask = tack1.dayTask,
                    watchTask = tack1.watchTask,
                    typeTask = tack1.typeTask,
                    descriptionTask = tack1.descriptionTask,
                    numberSubTaskEmployeeTask = tack1.numberSubTaskEmployeeTask,
                    numberDoneSubTaskEmployeeTask = tack1.numberDoneSubTaskEmployeeTask,

                    year = tack1.year,
                    month = tack1.month,
                    day = tack1.day

                )
                taskEmployeeDao.update(newTack)

                binding1.txtNumTask.text =
                    numberDonSubTaskEmployeeTack.toString() + " از " + tack1.numberSubTaskEmployeeTask

            }

            itemView.setOnClickListener {
            }

            itemView.setOnLongClickListener {
                subTaskEvent.onSubTaskLongClicked(data[position], position)
                true
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubTaskViewHolder {
        binding = ItemSubTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SubTaskViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: SubTaskViewHolder, position: Int) {
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

    fun addTask(newTask: SubTaskEmployeeTack) {
        data.add(0, newTask)
        notifyItemInserted(0)
    }

    fun removeEmployee(oldTask: SubTaskEmployeeTack, oldPosition: Int) {
        data.remove(oldTask)
        notifyItemRemoved(oldPosition)
    }

    fun clearAll() {
        data.clear()
        notifyDataSetChanged()
    }

    interface SubTaskEvent {
        fun onSubTaskClicked(
            task: SubTaskEmployeeTack,
            position: Int,
        )

        fun onSubTaskLongClicked(subTask: SubTaskEmployeeTack, position: Int)
    }

}