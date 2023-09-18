package ir.aliza.sherkatmanage.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.aliza.sherkatmanage.DataBase.EmployeeHarvest
import ir.aliza.sherkatmanage.databinding.ItemExpenseBinding
import java.text.DecimalFormat

class SalaryApprenticesHarvestAdapter(
    private val data: ArrayList<EmployeeHarvest>,
    private val salaryApprenticesHarvestEvents: SalaryApprenticesHarvestEvents,
) :
    RecyclerView.Adapter<SalaryApprenticesHarvestAdapter.SalaryApprenticesHarvestViewHolder>() {

    lateinit var binding: ItemExpenseBinding
    inner class SalaryApprenticesHarvestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val btnMenu = binding.btnMenu
        fun bindData(position: Int, clickListener: SalaryApprenticesHarvestEvents) {

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SalaryApprenticesHarvestViewHolder {
        binding = ItemExpenseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SalaryApprenticesHarvestViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: SalaryApprenticesHarvestViewHolder, position: Int) {
        holder.bindData(position, salaryApprenticesHarvestEvents)
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

    fun addSalaryApprenticesInvestment(newHarvest: EmployeeHarvest) {
        data.add(data.size, newHarvest)
        notifyItemInserted(data.size)
    }

    fun removeSalaryApprenticesInvestment(oldHarvest: EmployeeHarvest, oldPosition: Int) {
        data.remove(oldHarvest)
        notifyItemRemoved(oldPosition)
    }

    fun updateSalaryApprenticesInvestment(newHarvest: EmployeeHarvest, position: Int) {

        data.set(position, newHarvest)
        notifyItemChanged(position)

    }

    private fun formatCurrency(value: Long?): String {
        val decimalFormat = DecimalFormat("#,###")
        return decimalFormat.format(value) + " تومان"
    }
    interface SalaryApprenticesHarvestEvents {
        fun onApprenticesHarvestClicked(employeeHarvest: EmployeeHarvest, position: Int)
        fun onLongClickedApprenticesHarvest(employeeHarvest: EmployeeHarvest, position: Int)
        fun onMenuItemClick(employeeHarvest: EmployeeHarvest, position: Int)

    }
}
