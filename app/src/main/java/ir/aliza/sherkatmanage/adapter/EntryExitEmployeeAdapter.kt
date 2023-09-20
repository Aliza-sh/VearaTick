package ir.aliza.sherkatmanage.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import ir.aliza.sherkatmanage.DataBase.Day
import ir.aliza.sherkatmanage.DataBase.Time
import ir.aliza.sherkatmanage.databinding.ItemEntryExitBinding

class EntryExitEmployeeAdapter(
    val data: ArrayList<Time>,
    val entryExit: Day?,
    val nameDay: String,
    private val entryExitEmployee: EntryExitEmployeeEvent,
    val dayNoEntExtEmp: View,
    val dayEntEmp: View,
    val dayExtEmp: View
) :
    RecyclerView.Adapter<EntryExitEmployeeAdapter.EntryExitEmployeeViewHolder>() {

    lateinit var binding: ItemEntryExitBinding

    inner class EntryExitEmployeeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val btnMenu = binding.btnMenuEntryExit
        fun bindData(position: Int, clickListener: EntryExitEmployeeEvent) {

            binding.txtTimeOutMust.text = entryExit?.exit
            binding.txtTimeInMust.text = entryExit?.entry

            binding.txtTimeIn.text = data[position].entryAll.toString()
            binding.txtTimeOut.text = data[position].exitAll.toString()
            binding.itemDateText.text = "$nameDay \n ${data[position].day} ${data[position].month}"

            binding.btnMenuEntryExit.setOnClickListener {
                if (binding.txtTimeIn.text != "0")
                    clickListener.onMenuItemClick(data[position], position, dayNoEntExtEmp,dayEntEmp,dayExtEmp)
                else
                    Toast.makeText(itemView.context, "چیزی برای حذف نیست .", Toast.LENGTH_SHORT)
                        .show()

            }

        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EntryExitEmployeeViewHolder {
        binding =
            ItemEntryExitBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return EntryExitEmployeeViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: EntryExitEmployeeViewHolder, position: Int) {
        holder.bindData(position, entryExitEmployee)
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

    fun addInOut(inOut: Time) {
        data.add(data.size, inOut)
        notifyItemInserted(data.size)
    }

    fun removeInOut(oldInOut: Time, oldPosition: Int) {
        data.remove(oldInOut)
        notifyItemRemoved(oldPosition)
    }

    fun clearAll() {
        data.clear()
        notifyDataSetChanged()
    }

    fun updateInOut(newInOut: Time, position: Int) {

        data.set(position, newInOut)
        notifyItemChanged(position)

    }

    interface EntryExitEmployeeEvent {
        fun onMenuItemClick(
            time: Time,
            position: Int,
            dayNoEntExtEmp: View,
            dayEntEmp: View,
            dayExtEmp: View
        )
    }

}