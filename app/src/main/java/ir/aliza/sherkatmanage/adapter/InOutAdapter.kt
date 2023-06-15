package ir.aliza.sherkatmanage.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.aliza.sherkatmanage.DataBase.Day
import ir.aliza.sherkatmanage.DataBase.Time
import ir.aliza.sherkatmanage.databinding.FragmentDialogDoneEntryBinding
import ir.aliza.sherkatmanage.databinding.ItemInOutBinding

class InOutAdapter(val data: ArrayList<Time>, val entryExit: Day?, val nameDay: String) :
    RecyclerView.Adapter<InOutAdapter.CalendarViewHolder>() {

    lateinit var binding: ItemInOutBinding
    lateinit var binding1: FragmentDialogDoneEntryBinding

    inner class CalendarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindData(position: Int) {

            binding.txtTimeOutMust.text = entryExit?.exit
            binding.txtTimeInMust.text = entryExit?.entry

            binding.txtTimeIn.text = data[position].entry
            binding.txtTimeOut.text = data[position].exit
            binding.itemDateText.text = "$nameDay \n ${data[position].day} ${data[position].month}"

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        binding = ItemInOutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding1 =
            FragmentDialogDoneEntryBinding.inflate(LayoutInflater.from(parent.context), null, false)
        return CalendarViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
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

}