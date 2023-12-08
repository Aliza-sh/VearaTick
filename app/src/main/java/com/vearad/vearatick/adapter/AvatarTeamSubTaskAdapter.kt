package com.vearad.vearatick.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.vearad.vearatick.DataBase.AppDatabase
import com.vearad.vearatick.DataBase.TeamSubTask
import com.vearad.vearatick.R
import com.vearad.vearatick.databinding.ItemAvatarSmallBinding

class AvatarTeamSubTaskAdapter(private val data: ArrayList<TeamSubTask>) :
    RecyclerView.Adapter<AvatarTeamSubTaskAdapter.AvatarNearViewHolder>() {

    lateinit var binding: ItemAvatarSmallBinding
    inner class AvatarNearViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun bindData(position: Int) {
            val employeeDao  = AppDatabase.getDataBase(itemView.context).employeeDao
            val employee = employeeDao.getEmployee(data[position].idEmployee!!)

            binding.txtNameEmployee.text = employee!!.name + " " + employee.family
            if (employee.imagePath != "") {
                Glide.with(itemView)
                    .load(employee.imagePath)
                    .apply(RequestOptions.circleCropTransform())
                    .into(binding.imgEmployee)
            } else
                if (employee.gender == "زن") {
                    binding.imgEmployee.setImageResource(R.drawable.img_matter)
                }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvatarNearViewHolder {
        binding = ItemAvatarSmallBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AvatarNearViewHolder(binding.root)
    }
    override fun onBindViewHolder(holder: AvatarNearViewHolder, position: Int) {
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