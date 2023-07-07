package ir.aliza.sherkatmanage.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.aliza.sherkatmanage.DataBase.SubTaskEmployeeTack
import ir.aliza.sherkatmanage.databinding.ItemSubTaskBinding

class SubTaskEmployeeTackAdapter(
    private val data: ArrayList<SubTaskEmployeeTack>,
    private val subTaskEvent: SubTaskEvent,
) :

    RecyclerView.Adapter<SubTaskEmployeeTackAdapter.SubTaskViewHolder>() {

    lateinit var binding: ItemSubTaskBinding

    inner class SubTaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindData(position: Int) {


            binding.txtTack.text = data[position].nameTask
            binding.txtDescription.text = data[position].descriptionTask

            itemView.setOnClickListener {
            }

            itemView.setOnLongClickListener {
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