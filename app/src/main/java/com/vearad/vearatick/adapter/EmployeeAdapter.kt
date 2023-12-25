package com.vearad.vearatick.adapter

import android.annotation.SuppressLint
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.vearad.vearatick.DataBase.EfficiencyDao
import com.vearad.vearatick.DataBase.EfficiencyEmployee
import com.vearad.vearatick.DataBase.Employee
import com.vearad.vearatick.R
import com.vearad.vearatick.databinding.ItemEmployeeBinding

class EmployeeAdapter(
    private val data: ArrayList<Employee>,
    private val employeeEvents: EmployeeEvents,
    private val efficiencyEmployeeDao: EfficiencyDao
) :
    RecyclerView.Adapter<EmployeeAdapter.EmployeeViewHolder>() {

    lateinit var binding: ItemEmployeeBinding

    inner class EmployeeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindData(position: Int) {

            if (data[position].rank == "سهام دار") {

                binding.txtRank.text = "سهام دار"
                val shape = GradientDrawable()
                shape.shape = GradientDrawable.RECTANGLE
                shape.cornerRadii = floatArrayOf(40f, 40f, 40f, 40f, 40f, 40f, 40f, 40f)
                shape.setStroke(
                    5,
                    ContextCompat.getColor(binding.root.context, R.color.green_dark_rank)
                )
                shape.setColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.green_light_rank
                    )
                )
                binding.txtRank.setTextColor(android.graphics.Color.parseColor("#227158"))
                binding.txtRank.background = shape

            } else if (data[position].rank == "کارمند") {

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

            } else if (data[position].rank == "کارآموز") {
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
            }

            if (data[position].idEmployee != null) {

                val efficiencyEmployee =
                    efficiencyEmployeeDao.getEfficiencyEmployee(data[position].idEmployee!!)

                val progress = efficiencyEmployeeDao.getEfficiencyEmployee(data[position].idEmployee!!)
                val efficiencyTotalPresence = progress!!.efficiencyTotalPresence
                val efficiencyTotalDuties = progress.efficiencyTotalDuties
                val efficiencyTotal =
                    (efficiencyTotalPresence + efficiencyTotalDuties) / 2
                binding.progressCircular.progress = efficiencyTotal.toFloat()

                if (efficiencyEmployee!!.totalWeekWatch != 0 && efficiencyEmployee.mustWeekWatch != 0) {

                    var totalWeekWatch = efficiencyEmployee.totalWeekWatch
                    var mustWeekWatch = efficiencyEmployee.mustWeekWatch
                    var dahanSevisKon: Float = 0f
                    dahanSevisKon = totalWeekWatch!!.toFloat() / mustWeekWatch!!.toFloat()

                    val efficiencyWeekPresence = dahanSevisKon * 100

                    val newEfficiencyEmployee = EfficiencyEmployee(
                        idEfficiency = efficiencyEmployee.idEfficiency,
                        idEmployee = efficiencyEmployee.idEmployee,
                        mustWeekWatch = efficiencyEmployee.mustWeekWatch,
                        numberDay = efficiencyEmployee.numberDay,
                        totalWeekWatch = efficiencyEmployee.totalWeekWatch,
                        totalWatch = efficiencyEmployee.totalWatch,
                        efficiencyWeekPresence = efficiencyWeekPresence.toInt(),
                        efficiencyTotalPresence = efficiencyEmployee.efficiencyTotalPresence,
                        totalWeekDuties = efficiencyEmployee.totalWeekDuties,
                        totalMonthDuties = efficiencyEmployee.totalMonthDuties,
                        totalDuties = efficiencyEmployee.totalDuties,
                        efficiencyWeekDuties = efficiencyEmployee.efficiencyWeekDuties,
                        efficiencyTotalDuties = efficiencyEmployee.efficiencyTotalDuties,
                        efficiencyTotal = efficiencyEmployee.efficiencyTotal,
                        totalMonthWatch = efficiencyEmployee.totalMonthWatch,
                        efficiencyMonthDuties = efficiencyEmployee.efficiencyMonthDuties
                    )
                    efficiencyEmployeeDao.update(newEfficiencyEmployee)
                }

                binding.txtPresenceWeek.text =
                    efficiencyEmployee?.efficiencyWeekPresence.toString() + "%"

                binding.txtDutiesWeek.text =
                    efficiencyEmployee?.efficiencyWeekDuties.toString() + "%"
            }

            binding.txtnameprn.text = data[position].name + " " + data[position].family
            binding.txttkhprn.text = data[position].specialty

            if (data[position].imagePath != null) {
                Glide.with(itemView)
                    .load(data[position].imagePath)
                    .apply(RequestOptions.circleCropTransform())
                    .into(binding.imgprn)
            } else
                if (data[position].gender == "زن") {
                    binding.imgprn.setImageResource(R.drawable.img_matter)
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
