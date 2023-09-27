package ir.aliza.sherkatmanage.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kizitonwose.calendarview.utils.persian.PersianCalendar
import ir.aliza.sherkatmanage.DataBase.MonthlyTax
import ir.aliza.sherkatmanage.R
import ir.aliza.sherkatmanage.databinding.ItemAnnualReportBinding
import java.text.DecimalFormat

class AnnualReportAdapter(
    private val data: ArrayList<MonthlyTax>,
) : RecyclerView.Adapter<AnnualReportAdapter.AnnualReportViewHolder>() {

    lateinit var binding: ItemAnnualReportBinding

    inner class AnnualReportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(position: Int) {

            val calendar = PersianCalendar()

            if (data[position].farvardin != 0.toLong()) {
                binding.txtTaxFarvardin.text = formatCurrency(data[position].farvardin)
                binding.viewFarvardin.setBackgroundColor(itemView.context.getColor(R.color.red_800))
            }

            if (data[position].ordibehesht != 0.toLong()) {
                binding.txtTaxOrdibehesht.text = formatCurrency(data[position].ordibehesht)
                binding.viewOrdibehesht.setBackgroundColor(itemView.context.getColor(R.color.red_800))
            }

            if (data[position].khordad != 0.toLong()) {
                binding.txtTaxKhordad.text = formatCurrency(data[position].khordad)
                binding.viewKhordad.setBackgroundColor(itemView.context.getColor(R.color.red_800))
            }

            if (data[position].tir != 0.toLong()) {
                binding.txtTaxTir.text = formatCurrency(data[position].tir)
                binding.viewTir.setBackgroundColor(itemView.context.getColor(R.color.red_800))
            }

            if (data[position].mordad != 0.toLong()) {
                binding.txtTaxMordad.text = formatCurrency(data[position].mordad)
                binding.viewMordad.setBackgroundColor(itemView.context.getColor(R.color.red_800))
            }

            if (data[position].shahriver != 0.toLong()) {
                binding.txtTaxShahriver.text = formatCurrency(data[position].shahriver)
                binding.viewShahriver.setBackgroundColor(itemView.context.getColor(R.color.red_800))
            }

            if (data[position].mehr != 0.toLong()) {
                binding.txtTaxMehr.text = formatCurrency(data[position].mehr)
                binding.viewMehr.setBackgroundColor(itemView.context.getColor(R.color.red_800))
            }

            if (data[position].aban != 0.toLong()) {
                binding.txtTaxAban.text = formatCurrency(data[position].aban)
                binding.viewAban.setBackgroundColor(itemView.context.getColor(R.color.red_800))
            }

            if (data[position].azar != 0.toLong()) {
                binding.txtTaxAzar.text = formatCurrency(data[position].azar)
                binding.viewAzar.setBackgroundColor(itemView.context.getColor(R.color.red_800))
            }

            if (data[position].day != 0.toLong()) {
                binding.txtTaxDay.text = formatCurrency(data[position].day)
                binding.viewDay.setBackgroundColor(itemView.context.getColor(R.color.red_800))
            }

            if (data[position].bahman != 0.toLong()) {
                binding.txtTaxBahman.text = formatCurrency(data[position].bahman)
                binding.viewBahman.setBackgroundColor(itemView.context.getColor(R.color.red_800))
            }

            if (data[position].esfand != 0.toLong()) {
                binding.txtTaxEsfand.text = formatCurrency(data[position].esfand)
                binding.viewEsfand.setBackgroundColor(itemView.context.getColor(R.color.red_800))
            }

            if (calendar.persianYear == data[position].year)
                when (calendar.persianMonth + 1) {
                    1 -> binding.viewFarvardin.setBackgroundColor(itemView.context.getColor(R.color.firoze))
                    2 -> binding.viewOrdibehesht.setBackgroundColor(itemView.context.getColor(R.color.firoze))
                    3 -> binding.viewKhordad.setBackgroundColor(itemView.context.getColor(R.color.firoze))
                    4 -> binding.viewTir.setBackgroundColor(itemView.context.getColor(R.color.firoze))
                    5 -> binding.viewMordad.setBackgroundColor(itemView.context.getColor(R.color.firoze))
                    6 -> binding.viewShahriver.setBackgroundColor(itemView.context.getColor(R.color.firoze))
                    7 -> binding.viewMehr.setBackgroundColor(itemView.context.getColor(R.color.firoze))
                    8 -> binding.viewAban.setBackgroundColor(itemView.context.getColor(R.color.firoze))
                    9 -> binding.viewAzar.setBackgroundColor(itemView.context.getColor(R.color.firoze))
                    10 -> binding.viewDay.setBackgroundColor(itemView.context.getColor(R.color.firoze))
                    11 -> binding.viewBahman.setBackgroundColor(itemView.context.getColor(R.color.firoze))
                    12 -> binding.viewEsfand.setBackgroundColor(itemView.context.getColor(R.color.firoze))
                }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnnualReportViewHolder {
        binding =
            ItemAnnualReportBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AnnualReportViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: AnnualReportViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    private fun formatCurrency(value: Long?): String {
        val decimalFormat = DecimalFormat("#,###")
        return decimalFormat.format(value) + " تومان"
    }

}