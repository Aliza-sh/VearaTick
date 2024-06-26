package com.vearad.vearatick.adapter

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.vearad.vearatick.model.db.CompanyReceipt
import com.vearad.vearatick.R
import com.vearad.vearatick.databinding.ItemExpenseBinding
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

            if (data[position].idProject != null) {
                binding.txtProjectTaskEmployee.visibility = View.VISIBLE

                binding.txtProjectTaskEmployee.text = "درآمد پروژه"
                val shape = GradientDrawable()
                shape.shape = GradientDrawable.RECTANGLE
                shape.cornerRadii = floatArrayOf(40f, 40f, 40f, 40f, 40f, 40f, 40f, 40f)
                shape.setStroke(
                    5,
                    ContextCompat.getColor(binding.root.context, R.color.blue_dark_rank)
                )
                shape.setColor(ContextCompat.getColor(binding.root.context, R.color.blue_light_rank))
                binding.txtProjectTaskEmployee.setTextColor(android.graphics.Color.parseColor("#215DAD"))
                binding.txtProjectTaskEmployee.background = shape

            }

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
