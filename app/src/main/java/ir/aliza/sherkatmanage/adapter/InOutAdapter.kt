package ir.aliza.sherkatmanage.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.aliza.sherkatmanage.DataBase.Time
import ir.aliza.sherkatmanage.databinding.ItemInOutBinding

class InOutAdapter(val data: ArrayList<Time>) :
    RecyclerView.Adapter<InOutAdapter.CalendarViewHolder>() {

    lateinit var binding: ItemInOutBinding

    inner class CalendarViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {

        fun bindData(position: Int) {

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        binding = ItemInOutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
        return 5
    }

}