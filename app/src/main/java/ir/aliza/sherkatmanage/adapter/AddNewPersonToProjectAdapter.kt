package ir.aliza.sherkatmanage.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.aliza.sherkatmanage.DataBase.Employee
import ir.aliza.sherkatmanage.R
import ir.aliza.sherkatmanage.databinding.ItemNewPersonToProjectBinding

class AddNewPersonToProjectAdapter(private val data: ArrayList<Employee>) :
    RecyclerView.Adapter<AddNewPersonToProjectAdapter.AddNewPersonToProjectViewHolder>() {

    lateinit var binding: ItemNewPersonToProjectBinding

    inner class AddNewPersonToProjectViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        @SuppressLint("SetTextI18n")
        fun bindData(position: Int) {

            if (data[position].gender == "زن"){
                binding.imgprn2.setImageResource(R.drawable.img_matter)
            }

            binding.txtNameEmployee.text = data[position].name + "" + data[position].family
            binding.txtJobEmployee.text = data[position].specialty
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AddNewPersonToProjectViewHolder {
        binding = ItemNewPersonToProjectBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AddNewPersonToProjectViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: AddNewPersonToProjectViewHolder, position: Int) {
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

}