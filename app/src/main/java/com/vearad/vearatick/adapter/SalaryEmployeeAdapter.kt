package com.vearad.vearatick.adapter

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.vearad.vearatick.model.db.Employee
import com.vearad.vearatick.model.db.EmployeeHarvestDao
import com.vearad.vearatick.R
import com.vearad.vearatick.databinding.ItemPaymentEmployeesBinding
import java.text.DecimalFormat

class SalaryEmployeeAdapter(
    private val dataEmployee: ArrayList<Employee>,
    private val employeeHarvestDao: EmployeeHarvestDao,
    private val employeeEvents: EmployeeEvents,
) :
    RecyclerView.Adapter<SalaryEmployeeAdapter.EmployeeViewHolder>() {

    lateinit var binding: ItemPaymentEmployeesBinding

    inner class EmployeeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindData(position: Int, clickListener: EmployeeEvents) {

                binding.txtRank.text = "کارمند"
                val shape = GradientDrawable()
                shape.shape = GradientDrawable.RECTANGLE
                shape.cornerRadii = floatArrayOf(40f, 40f, 40f, 40f, 40f, 40f, 40f, 40f)
                shape.setStroke(
                    5,
                    ContextCompat.getColor(binding.root.context, R.color.blue_dark_rank)
                )
                shape.setColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.blue_light_rank
                    )
                )
                binding.txtRank.setTextColor(android.graphics.Color.parseColor("#215DAD"))
                binding.txtRank.background = shape

            binding.txtNameEmp.text = dataEmployee[position].name + " " + dataEmployee[position].family
            binding.txtSpecialtyEmp.text = dataEmployee[position].specialty

            if (dataEmployee[position].imagePath != null) {
                Glide.with(itemView)
                    .load(dataEmployee[position].imagePath)
                    .apply(RequestOptions.circleCropTransform())
                    .into(binding.btnInfoPrn)
            } else
                if (dataEmployee[position].gender == "زن") {
                    binding.btnInfoPrn.setImageResource(R.drawable.img_matter)
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        binding = ItemPaymentEmployeesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EmployeeViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
        holder.bindData(position,employeeEvents)
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
    interface EmployeeEvents {
        fun onEmployeeClicked(employee: Employee, position: Int)
        fun onEmployeeLongClicked(employee: Employee, position: Int)
        fun onBtnHarvestClick(
            employee: Employee,
            employeeInvestmentDao: EmployeeHarvestDao,
            position: Int
        )
    }
}
