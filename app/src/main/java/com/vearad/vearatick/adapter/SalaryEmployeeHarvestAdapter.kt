package com.vearad.vearatick.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vearad.vearatick.model.db.EmployeeHarvest
import com.vearad.vearatick.databinding.ItemExpenseBinding
import java.text.DecimalFormat

class SalaryEmployeeHarvestAdapter(
    private val data: ArrayList<EmployeeHarvest>,
    private val salaryEmployeeHarvestEvents: SalaryEmployeeHarvestEvents,
) :
    RecyclerView.Adapter<SalaryEmployeeHarvestAdapter.SalaryEmployeeHarvestViewHolder>() {

    lateinit var binding: ItemExpenseBinding
    inner class SalaryEmployeeHarvestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val btnMenu = binding.btnMenu
        fun bindData(position: Int, clickListener: SalaryEmployeeHarvestEvents) {

            binding.txtExpense.text = formatCurrency(data[position].harvest)
            binding.txtDate.text = data[position].harvestDate.toString()
            binding.txtDescription.text = data[position].harvestDescription.toString()

            binding.btnMenu.setOnClickListener {
                clickListener.onMenuItemClick(data[position], position)
            }

            itemView.setOnClickListener {}
            itemView.setOnLongClickListener {true}
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SalaryEmployeeHarvestViewHolder {
        binding = ItemExpenseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SalaryEmployeeHarvestViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: SalaryEmployeeHarvestViewHolder, position: Int) {
        holder.bindData(position, salaryEmployeeHarvestEvents)
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

    fun addSalaryEmployeeInvestment(newHarvest: EmployeeHarvest) {
        data.add(data.size, newHarvest)
        notifyItemInserted(data.size)
    }

    fun removeSalaryEmployeeInvestment(oldHarvest: EmployeeHarvest, oldPosition: Int) {
        data.remove(oldHarvest)
        notifyItemRemoved(oldPosition)
    }

    fun updateSalaryEmployeeInvestment(newHarvest: EmployeeHarvest, position: Int) {

        data.set(position, newHarvest)
        notifyItemChanged(position)

    }

    private fun formatCurrency(value: Long?): String {
        val decimalFormat = DecimalFormat("#,###")
        return decimalFormat.format(value) + " تومان"
    }
    interface SalaryEmployeeHarvestEvents {
        fun onEmployeeHarvestClicked(employeeHarvest: EmployeeHarvest, position: Int)
        fun onLongClickedEmployeeHarvest(employeeHarvest: EmployeeHarvest, position: Int)
        fun onMenuItemClick(employeeHarvest: EmployeeHarvest, position: Int)

    }
}
