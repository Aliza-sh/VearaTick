package com.vearad.vearatick.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vearad.vearatick.DataBase.Employee
import com.vearad.vearatick.databinding.ItemEventBinding

class CompanyEventAdapter(
    val data: ArrayList<Employee>,
    val companyEvent: CompanyEventEvent,
) :
    RecyclerView.Adapter<CompanyEventAdapter.CompanyEventViewHolder>() {

    lateinit var binding: ItemEventBinding

    inner class CompanyEventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @SuppressLint("SetTextI18n")
        fun bindData(position: Int, clickListener: CompanyEventEvent) {

            binding.txtDateEvent.text = "1402/5/6"
            binding.txtNameEvent.text = "دورهمی ویراد"
            binding.txtNumPerson.text = "20 نفر"

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

    fun removeEmployee(oldCompanyEmployeeResume: Employee, oldPosition: Int) {
        data.remove(oldCompanyEmployeeResume)
        notifyItemRemoved(oldPosition)
    }

    interface CompanyEventEvent {
        fun onEventClicked(
            companyEmployeeResume: Employee,
            position: Int,
        )
//        fun onMenuItemClick(
//            companyEmployeeResume: Employee,
//            position: Int,
//        )
    }

}