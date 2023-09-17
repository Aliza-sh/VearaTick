package ir.aliza.sherkatmanage.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.aliza.sherkatmanage.DataBase.CompanyExpenses
import ir.aliza.sherkatmanage.databinding.ItemExpenseBinding
import java.text.DecimalFormat

class CompanyExpensesAdapter(
    private val data: ArrayList<CompanyExpenses>,
    private val companyExpensesEvents: CompanyExpensesEvents,
) :
    RecyclerView.Adapter<CompanyExpensesAdapter.CompanyExpensesViewHolder>() {

    lateinit var binding: ItemExpenseBinding

    inner class CompanyExpensesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val btnMenu = binding.btnMenu
        fun bindData(position: Int, clickListener: CompanyExpensesEvents) {

            binding.txtExpense.text = formatCurrency(data[position].companyExpenses)
            binding.txtDate.text = data[position].companyExpensesDate.toString()
            binding.txtDescription.text = data[position].companyExpensesDescription.toString()

            binding.btnMenu.setOnClickListener {
                clickListener.onMenuItemClick(data[position], position)
            }

            itemView.setOnClickListener {}
            itemView.setOnLongClickListener {true}
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompanyExpensesViewHolder {
        binding = ItemExpenseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CompanyExpensesViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: CompanyExpensesViewHolder, position: Int) {
        holder.bindData(position,companyExpensesEvents)
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

    fun addCompanyExpenses(newCompanyExpenses: CompanyExpenses) {
        data.add(data.size, newCompanyExpenses)
        notifyItemInserted(data.size)
    }

    fun removeCompanyExpenses(oldCompanyExpenses: CompanyExpenses, oldPosition: Int) {
        data.remove(oldCompanyExpenses)
        notifyItemRemoved(oldPosition)
    }

    fun updateCompanyExpenses(newCompanyExpenses: CompanyExpenses, position: Int) {

        data.set(position, newCompanyExpenses)
        notifyItemChanged(position)

    }
    private fun formatCurrency(value: Long?): String {
        val decimalFormat = DecimalFormat("#,###")
        return decimalFormat.format(value) + " تومان"
    }
    interface CompanyExpensesEvents {
        fun onCompanyExpensesClicked(companyExpenses: CompanyExpenses, position: Int)
        fun onCompanyExpensesLongClicked(companyExpenses: CompanyExpenses, position: Int)
        fun onMenuItemClick(companyExpenses: CompanyExpenses, position: Int)
    }
}
