package com.vearad.vearatick.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.vearad.vearatick.model.db.Employee
import com.vearad.vearatick.R
import com.vearad.vearatick.databinding.ItemEmployeeResumeBinding

class CompanyEmployeeResumeAdapter(
    val data: ArrayList<Employee>,
    val employeeResume: EmployeeResumeEvent,
) :
    RecyclerView.Adapter<CompanyEmployeeResumeAdapter.CompanyEmployeeResumeViewHolder>() {

    lateinit var binding: ItemEmployeeResumeBinding

    inner class CompanyEmployeeResumeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @SuppressLint("SetTextI18n")
        fun bindData(position: Int, clickListener: EmployeeResumeEvent) {

            binding.txtNameEmp.text = data[position].name + data[position].family

            if (data[position].imagePath != "") {
                Glide.with(itemView)
                    .load(data[position].imagePath)
                    .apply(RequestOptions.circleCropTransform())
                    .into(binding.imgprn)
            } else
                if (data[position].gender == "زن") {
                    binding.imgprn.setImageResource(R.drawable.img_matter)
                }


            itemView.setOnClickListener {
                employeeResume.onResumeClicked(data[position], position)
            }

//            binding.btnMenuEntryExit.setOnClickListener {
//                clickListener.onMenuItemClick(data[position], position)
//            }

        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CompanyEmployeeResumeViewHolder {
        binding =
            ItemEmployeeResumeBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return CompanyEmployeeResumeViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: CompanyEmployeeResumeViewHolder, position: Int) {
        holder.bindData(position, employeeResume)
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

    interface EmployeeResumeEvent {
        fun onResumeClicked(
            companyEmployeeResume: Employee,
            position: Int,
        )
//        fun onMenuItemClick(
//            companyEmployeeResume: Employee,
//            position: Int,
//        )
    }

}