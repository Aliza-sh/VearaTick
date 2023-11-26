package com.vearad.vearatick.adapter

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.vearad.vearatick.DataBase.Employee
import com.vearad.vearatick.DataBase.EmployeeHarvestDao
import com.vearad.vearatick.R
import com.vearad.vearatick.databinding.ItemPaymentApprenticesBinding
import java.text.DecimalFormat

class SalaryApprenticesAdapter(
    private val dataEmployee: ArrayList<Employee>,
    private val employeeHarvestDao: EmployeeHarvestDao,
    private val apprenticesEvents: ApprenticesEvents,
) :
    RecyclerView.Adapter<SalaryApprenticesAdapter.ApprenticesViewHolder>() {

    lateinit var binding: ItemPaymentApprenticesBinding

    inner class ApprenticesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindData(position: Int, clickListener: ApprenticesEvents) {

            binding.txtRank.text = "کارآموز"
            val shape = GradientDrawable()
            shape.shape = GradientDrawable.RECTANGLE
            shape.cornerRadii = floatArrayOf(40f, 40f, 40f, 40f, 40f, 40f, 40f, 40f)
            shape.setStroke(
                5,
                ContextCompat.getColor(binding.root.context, R.color.red_dark_rank)
            )
            shape.setColor(ContextCompat.getColor(binding.root.context, R.color.red_light_rank))
            binding.txtRank.setTextColor(android.graphics.Color.parseColor("#AF694C"))
            binding.txtRank.background = shape
            binding.txtNameEmp.text =
                dataEmployee[position].name + " " + dataEmployee[position].family
            binding.txtSpecialtyEmp.text = dataEmployee[position].specialty

            if (dataEmployee[position].imagePath != "") {
                Glide.with(itemView)
                    .load(dataEmployee[position].imagePath)
                    .apply(RequestOptions.circleCropTransform())
                    .into(binding.imgDone)
            } else
                if (dataEmployee[position].gender == "زن") {
                    binding.imgDone.setImageResource(R.drawable.img_matter)
                }

            val sumEmployeeHarvest =
                employeeHarvestDao.getEmployeeHarvestSum(dataEmployee[position].idEmployee!!)
            binding.txtHarvest.text = formatCurrency(sumEmployeeHarvest.toLong())
            binding.btnHarvest.setOnClickListener {
                clickListener.onBtnHarvestClick(
                    dataEmployee[position],
                    employeeHarvestDao, position
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApprenticesViewHolder {
        binding = ItemPaymentApprenticesBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ApprenticesViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ApprenticesViewHolder, position: Int) {
        holder.bindData(position, apprenticesEvents)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return dataEmployee.size
    }

    private fun formatCurrency(value: Long?): String {
        val decimalFormat = DecimalFormat("#,###")
        return decimalFormat.format(value) + " تومان"
    }

    interface ApprenticesEvents {
        fun onApprenticesClicked(employee: Employee, position: Int)
        fun onApprenticesLongClicked(employee: Employee, position: Int)
        fun onBtnHarvestClick(
            employee: Employee,
            employeeInvestmentDao: EmployeeHarvestDao,
            position: Int
        )
    }
}
