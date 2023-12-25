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
import com.vearad.vearatick.databinding.ItemPaymentShareholdersBinding
import java.text.DecimalFormat

class SalaryShareholdersAdapter(
    private val dataEmployee: ArrayList<Employee>,
    private val employeeHarvestDao: EmployeeHarvestDao,
    private val shareholdersEvents: ShareholdersEvents,
) :
    RecyclerView.Adapter<SalaryShareholdersAdapter.PaymentShareholdersViewHolder>() {

    lateinit var binding: ItemPaymentShareholdersBinding

    inner class PaymentShareholdersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindData(position: Int, clickListener: ShareholdersEvents) {

            binding.txtRank.text = "سهام دار"
            val shape = GradientDrawable()
            shape.shape = GradientDrawable.RECTANGLE
            shape.cornerRadii = floatArrayOf(40f, 40f, 40f, 40f, 40f, 40f, 40f, 40f)
            shape.setStroke(
                5,
                ContextCompat.getColor(binding.root.context, R.color.green_dark_rank)
            )
            shape.setColor(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.green_light_rank
                )
            )
            binding.txtRank.setTextColor(android.graphics.Color.parseColor("#227158"))
            binding.txtRank.background = shape

            binding.txtNameEmp.text =
                dataEmployee[position].name + " " + dataEmployee[position].family
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
                clickListener.onBtnPaymentClick(
                    dataEmployee[position],
                    employeeHarvestDao, position
                )
            }


            itemView.setOnClickListener {}
            itemView.setOnLongClickListener { true }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PaymentShareholdersViewHolder {
        binding = ItemPaymentShareholdersBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PaymentShareholdersViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: PaymentShareholdersViewHolder, position: Int) {
        holder.bindData(position, shareholdersEvents)
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

    interface ShareholdersEvents {
        fun onPaymentShareholdersClicked(employee: Employee, position: Int)
        fun onPaymentShareholdersLongClicked(employee: Employee, position: Int)

        fun onBtnPaymentClick(
            employee: Employee,
            employeeInvestmentDao: EmployeeHarvestDao,
            position: Int
        )
    }
}
