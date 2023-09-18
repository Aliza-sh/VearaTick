package ir.aliza.sherkatmanage.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.aliza.sherkatmanage.DataBase.EmployeeHarvest
import ir.aliza.sherkatmanage.databinding.ItemExpenseBinding
import java.text.DecimalFormat

class SalaryShareholdersHarvestAdapter(
    private val data: ArrayList<EmployeeHarvest>,
    private val salaryShareholdersHarvestEvents: SalaryShareholdersHarvestEvents,
) :
    RecyclerView.Adapter<SalaryShareholdersHarvestAdapter.SalaryShareholdersHarvestViewHolder>() {

    lateinit var binding: ItemExpenseBinding

    inner class SalaryShareholdersHarvestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val btnMenu = binding.btnMenu
        fun bindData(position: Int, clickListener: SalaryShareholdersHarvestEvents) {

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SalaryShareholdersHarvestViewHolder {
        binding = ItemExpenseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SalaryShareholdersHarvestViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: SalaryShareholdersHarvestViewHolder, position: Int) {
        holder.bindData(position, salaryShareholdersHarvestEvents)
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

    fun addSalaryShareholdersInvestment(newHarvest: EmployeeHarvest) {
        data.add(data.size, newHarvest)
        notifyItemInserted(data.size)
    }

    fun removeSalaryShareholdersInvestment(oldHarvest: EmployeeHarvest, oldPosition: Int) {
        data.remove(oldHarvest)
        notifyItemRemoved(oldPosition)
    }

    fun updateSalaryShareholdersInvestment(newHarvest: EmployeeHarvest, position: Int) {

        data.set(position, newHarvest)
        notifyItemChanged(position)

    }

    private fun formatCurrency(value: Long?): String {
        val decimalFormat = DecimalFormat("#,###")
        return decimalFormat.format(value) + " تومان"
    }
    interface SalaryShareholdersHarvestEvents {
        fun onShareholdersHarvestClicked(employeeHarvest: EmployeeHarvest, position: Int)
        fun onLongClickedShareholdersHarvest(employeeHarvest: EmployeeHarvest, position: Int)
        fun onMenuItemClick(employeeHarvest: EmployeeHarvest, position: Int)

    }
}
