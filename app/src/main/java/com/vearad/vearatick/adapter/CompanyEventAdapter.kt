package com.vearad.vearatick.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vearad.vearatick.databinding.ItemEventBinding
import com.vearad.vearatick.model.Events
import java.time.LocalDate

class CompanyEventAdapter(
    val data: List<Events.Event>,
    val companyEvent: CompanyEventEvent,
) :
    RecyclerView.Adapter<CompanyEventAdapter.CompanyEventViewHolder>() {

    lateinit var binding: ItemEventBinding

    inner class CompanyEventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @SuppressLint("SetTextI18n")
        fun bindData(position: Int, clickListener: CompanyEventEvent) {

            val dateGregorian = LocalDate.parse(data[position].start_date)
            var year: Int = dateGregorian.year
            var month: Int = dateGregorian.monthValue
            var day: Int = dateGregorian.dayOfMonth

            binding.txtDateEvent.text = "$year/$month/$day"
            binding.txtNameEvent.text = data[position].name
            binding.txtNumPerson.text = data[position].attendanceCount.toString()

            itemView.setOnClickListener {
                companyEvent.onEventClicked(data[position], position)
            }

//            binding.btnMenuEntryExit.setOnClickListener {
//                clickListener.onMenuItemClick(data[position], position)
//            }

        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CompanyEventViewHolder {
        binding =
            ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return CompanyEventViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: CompanyEventViewHolder, position: Int) {
        holder.bindData(position, companyEvent)
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

    interface CompanyEventEvent {
        fun onEventClicked(
            companyEvent: Events.Event,
            position: Int,
        )
//        fun onMenuItemClick(
//            companyEmployeeResume: Employee,
//            position: Int,
//        )
    }

}