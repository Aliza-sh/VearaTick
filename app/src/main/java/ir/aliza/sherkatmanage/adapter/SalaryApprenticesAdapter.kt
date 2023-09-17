package ir.aliza.sherkatmanage.adapter

import android.annotation.SuppressLint
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import ir.aliza.sherkatmanage.DataBase.EfficiencyDao
import ir.aliza.sherkatmanage.DataBase.Employee
import ir.aliza.sherkatmanage.R
import ir.aliza.sherkatmanage.databinding.ItemPaymentApprenticesBinding

class SalaryApprenticesAdapter(
    private val data: ArrayList<Employee>,
    private val employeeEvents: EmployeeEvents,
    private val efficiencyEmployeeDao: EfficiencyDao
) :
    RecyclerView.Adapter<SalaryApprenticesAdapter.EmployeeViewHolder>() {

    lateinit var binding: ItemPaymentApprenticesBinding

    inner class EmployeeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindData(position: Int) {

                binding.txtRank.text = "کارآموز"
                val shape = GradientDrawable()
                shape.shape = GradientDrawable.RECTANGLE
                shape.cornerRadii = floatArrayOf(40f, 40f, 40f, 40f, 40f, 40f, 40f, 40f)
                shape.setStroke(
                    5,
                    ContextCompat.getColor(binding.root.context, R.color.red_dark_rank)
                )
                shape.setColor(ContextCompat.getColor(binding.root.context, R.color.red_light_rank))
                binding.txtRank.setTextColor(android.graphics.Color.parseColor("#AF694C"))
                binding.txtRank.background = shape
            binding.txtNameEmp.text = data[position].name + " " + data[position].family
            binding.txtSpecialtyEmp.text = data[position].specialty

            if (data[position].gender == "زن") {
                binding.btnInfoPrn.setImageResource(R.drawable.img_matter);
            }
            itemView.setOnClickListener {
                employeeEvents.onEmployeeClicked(data[position], position)
            }

            itemView.setOnLongClickListener {
                employeeEvents.onEmployeeLongClicked(data[position], position)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        binding = ItemPaymentApprenticesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EmployeeViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
        holder.bindData(position)
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

    fun addEmployee(newEmployee: Employee) {
        data.add(data.size, newEmployee)
        notifyItemInserted(data.size)
    }

    fun removeEmployee(oldEmployee: Employee, oldPosition: Int) {
        data.remove(oldEmployee)
        notifyItemRemoved(oldPosition)
    }

    fun updateEmployee(newEmployee: Employee, position: Int) {

        data.set(position, newEmployee)
        notifyItemChanged(position)

    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newList: ArrayList<Employee>) {
        data.clear()
        data.addAll(newList)
        notifyDataSetChanged()
    }

    interface EmployeeEvents {
        fun onEmployeeClicked(employee: Employee, position: Int)
        fun onEmployeeLongClicked(employee: Employee, position: Int)
    }
}
