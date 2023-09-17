package ir.aliza.sherkatmanage.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.aliza.sherkatmanage.DataBase.EmployeeInvestment
import ir.aliza.sherkatmanage.databinding.ItemExpenseBinding
import java.text.DecimalFormat

class SalaryShareholdersInvestmentAdapter(
    private val data: ArrayList<EmployeeInvestment>,
    private val salaryShareholdersInvestmentEvents: SalaryShareholdersInvestmentEvents,
) :
    RecyclerView.Adapter<SalaryShareholdersInvestmentAdapter.SalaryShareholdersInvestmentViewHolder>() {

    lateinit var binding: ItemExpenseBinding

    inner class SalaryShareholdersInvestmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val btnMenu = binding.btnMenu
        fun bindData(position: Int, clickListener: SalaryShareholdersInvestmentEvents) {

            binding.txtExpense.text = formatCurrency(data[position].investment)
            binding.txtDate.text = data[position].investmentDate.toString()
            binding.txtDescription.text = data[position].investmentDescription.toString()

            binding.btnMenu.setOnClickListener {
                clickListener.onMenuItemClick(data[position], position)
            }

            itemView.setOnClickListener {}
            itemView.setOnLongClickListener {true}
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SalaryShareholdersInvestmentViewHolder {
        binding = ItemExpenseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SalaryShareholdersInvestmentViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: SalaryShareholdersInvestmentViewHolder, position: Int) {
        holder.bindData(position, salaryShareholdersInvestmentEvents)
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

    fun addSalaryShareholdersInvestment(newSalaryShareholdersInvestment: EmployeeInvestment) {
        data.add(data.size, newSalaryShareholdersInvestment)
        notifyItemInserted(data.size)
    }

    fun removeSalaryShareholdersInvestment(oldSalaryShareholdersInvestment: EmployeeInvestment, oldPosition: Int) {
        data.remove(oldSalaryShareholdersInvestment)
        notifyItemRemoved(oldPosition)
    }

    fun updateSalaryShareholdersInvestment(newSalaryShareholdersInvestment: EmployeeInvestment, position: Int) {

        data.set(position, newSalaryShareholdersInvestment)
        notifyItemChanged(position)

    }

    private fun formatCurrency(value: Long?): String {
        val decimalFormat = DecimalFormat("#,###")
        return decimalFormat.format(value) + " تومان"
    }
    interface SalaryShareholdersInvestmentEvents {
        fun onShareholdersInvestmentClicked(employeeInvestment: EmployeeInvestment, position: Int)
        fun onLongClickedemployeeInvestment(employeeInvestment: EmployeeInvestment, position: Int)
        fun onMenuItemClick(employeeInvestment: EmployeeInvestment, position: Int)

    }
}
