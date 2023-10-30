package com.vearad.vearatick.adapter

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.vearad.vearatick.DataBase.Employee
import com.vearad.vearatick.DataBase.EmployeeHarvestDao
import com.vearad.vearatick.DataBase.EmployeeInvestmentDao
import com.vearad.vearatick.R
import com.vearad.vearatick.databinding.ItemInvestmentShareholdersBinding
import java.text.DecimalFormat

class ShareholdersInvestmentAdapter(
    private val dataEmployee: ArrayList<Employee>,
    private val employeeHarvestDao: EmployeeHarvestDao,
    private val employeeInvestmentDao: EmployeeInvestmentDao,
    private val shareholdersInvestmentEvents: ShareholdersInvestmentEvents,
) :
    RecyclerView.Adapter<ShareholdersInvestmentAdapter.PaymentShareholdersViewHolder>() {

    lateinit var binding: ItemInvestmentShareholdersBinding

    inner class PaymentShareholdersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindData(position: Int, clickListener: ShareholdersInvestmentEvents) {

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
            if (dataEmployee[position].gender == "زن") {
                binding.btnInfoPrn.setImageResource(R.drawable.img_matter);
            }

            val sumEmployeeInvestment =
                employeeInvestmentDao.getEmployeeInvestmentSum(dataEmployee[position].idEmployee!!)
            binding.txtInvestment.text = formatCurrency(sumEmployeeInvestment.toLong())
            binding.btnInvestment.setOnClickListener {
                clickListener.onBtnInvestmentClick(
                    dataEmployee[position],
                    employeeInvestmentDao, position
                )
            }
            val sumEmployeeHarvest =
                employeeHarvestDao.getEmployeeHarvestSum(dataEmployee[position].idEmployee!!)

            var total = sumEmployeeInvestment - sumEmployeeHarvest

            if (total > 0) {
                val value = formatCurrency(total)
                binding.txtTotal.text = value + " +"
            } else if (total < 0) {
                total = -total
                val value = formatCurrency(total)
                binding.txtTotal.text = value + " -"
            } else {
                binding.txtTotal.text = "0"
            }

            itemView.setOnClickListener {}
            itemView.setOnLongClickListener { true }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PaymentShareholdersViewHolder {
        binding = ItemInvestmentShareholdersBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PaymentShareholdersViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: PaymentShareholdersViewHolder, position: Int) {
        holder.bindData(position, shareholdersInvestmentEvents)
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

    interface ShareholdersInvestmentEvents {
        fun onPaymentShareholdersClicked(employee: Employee, position: Int)
        fun onPaymentShareholdersLongClicked(employee: Employee, position: Int)
        fun onBtnInvestmentClick(
            employee: Employee,
            employeeInvestmentDao: EmployeeInvestmentDao,
            position: Int
        )

    }
}
