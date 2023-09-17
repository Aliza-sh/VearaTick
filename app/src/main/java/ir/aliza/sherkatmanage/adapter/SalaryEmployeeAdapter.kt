package ir.aliza.sherkatmanage.adapter

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import ir.aliza.sherkatmanage.DataBase.EfficiencyDao
import ir.aliza.sherkatmanage.DataBase.Employee
import ir.aliza.sherkatmanage.R
import ir.aliza.sherkatmanage.databinding.ItemPaymentEmployeesBinding

class SalaryEmployeeAdapter(
    private val data: ArrayList<Employee>,
    private val employeeEvents: EmployeeEvents,
    private val efficiencyEmployeeDao: EfficiencyDao
) :
    RecyclerView.Adapter<SalaryEmployeeAdapter.EmployeeViewHolder>() {

    lateinit var binding: ItemPaymentEmployeesBinding

    inner class EmployeeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindData(position: Int) {



                binding.txtRank.text = "کارمند"
                val shape = GradientDrawable()
                shape.shape = GradientDrawable.RECTANGLE
                shape.cornerRadii = floatArrayOf(40f, 40f, 40f, 40f, 40f, 40f, 40f, 40f)
                shape.setStroke(
                    5,
                    ContextCompat.getColor(binding.root.context, R.color.blue_dark_rank)
                )
                shape.setColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.blue_light_rank
                    )
                )
                binding.txtRank.setTextColor(android.graphics.Color.parseColor("#215DAD"))
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
        binding = ItemPaymentEmployeesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
    interface EmployeeEvents {
        fun onEmployeeClicked(employee: Employee, position: Int)
        fun onEmployeeLongClicked(employee: Employee, position: Int)
    }
}
