package ir.aliza.sherkatmanage.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.aliza.sherkatmanage.DataBase.Employee
import ir.aliza.sherkatmanage.R
import ir.aliza.sherkatmanage.databinding.ItemEmployeeBinding

class EmployeeAdapter(private val data: ArrayList<Employee>, private val employeeEvents: EmployeeEvents) :
    RecyclerView.Adapter<EmployeeAdapter.EmployeeViewHolder>() {

    lateinit var binding: ItemEmployeeBinding

    inner class EmployeeViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {

        @SuppressLint("SetTextI18n")
        fun bindData(position: Int) {

            binding.txtnameprn.text = data[position].name + " " + data[position].family
            binding.txttkhprn.text = data[position].specialty
            if (data[position].gender == "زن"){
                binding.imgprn.setImageResource(R.drawable.img_matter);
            }

            itemView.setOnClickListener {
                employeeEvents.onEmployeeClicked(data[position],position)
            }

            itemView.setOnLongClickListener {
                employeeEvents.onEmployeeLongClicked(data[position], position)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        binding = ItemEmployeeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

        data.set(position,newEmployee)
        notifyItemChanged(position)

    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newList: ArrayList<Employee>) {
        data.clear()
        data.addAll(newList)
        notifyDataSetChanged()
    }

    interface EmployeeEvents {
        fun onEmployeeClicked(employee: Employee,position: Int)
        fun onEmployeeLongClicked(employee: Employee, position: Int)
    }
}