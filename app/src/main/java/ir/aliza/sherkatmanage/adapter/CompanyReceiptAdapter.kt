package ir.aliza.sherkatmanage.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.aliza.sherkatmanage.DataBase.CompanyReceipt
import ir.aliza.sherkatmanage.databinding.ItemExpenseBinding
import java.text.DecimalFormat

class CompanyReceiptAdapter(
    private val data: ArrayList<CompanyReceipt>,
    private val companyReceiptEvents: CompanyReceiptEvents,
) :
    RecyclerView.Adapter<CompanyReceiptAdapter.CompanyReceiptViewHolder>() {

    lateinit var binding: ItemExpenseBinding

    inner class CompanyReceiptViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val btnMenu = binding.btnMenu
        fun bindData(position: Int, clickListener: CompanyReceiptEvents) {

            binding.txtExpense.text = formatCurrency(data[position].companyReceipt)
            binding.txtDate.text = data[position].companyReceiptDate.toString()
            binding.txtDescription.text = data[position].companyReceiptDescription.toString()

            binding.btnMenu.setOnClickListener {
                clickListener.onMenuItemClick(data[position], position)
            }

            itemView.setOnClickListener {}
            itemView.setOnLongClickListener {true}
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompanyReceiptViewHolder {
        binding = ItemExpenseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CompanyReceiptViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: CompanyReceiptViewHolder, position: Int) {
        holder.bindData(position, companyReceiptEvents)
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

    fun addCompanyReceipt(newCompanyReceipt: CompanyReceipt) {
        data.add(data.size, newCompanyReceipt)
        notifyItemInserted(data.size)
    }

    fun removeCompanyReceipt(oldCompanyReceipt: CompanyReceipt, oldPosition: Int) {
        data.remove(oldCompanyReceipt)
        notifyItemRemoved(oldPosition)
    }

    fun updateCompanyReceipt(newCompanyReceipt: CompanyReceipt, position: Int) {

        data.set(position, newCompanyReceipt)
        notifyItemChanged(position)

    }

    private fun formatCurrency(value: Long?): String {
        val decimalFormat = DecimalFormat("#,###")
        return decimalFormat.format(value) + " تومان"
    }
    interface CompanyReceiptEvents {
        fun onCompanyReceiptClicked(companyReceipt: CompanyReceipt, position: Int)
        fun onCompanyReceiptLongClicked(companyReceipt: CompanyReceipt, position: Int)
        fun onMenuItemClick(companyReceipt: CompanyReceipt, position: Int)

    }
}
