package com.vearad.vearatick.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vearad.vearatick.DataBase.CompanySkill
import com.vearad.vearatick.databinding.ItemSkillBinding

class CompanySkillAdapter(
    val data: ArrayList<CompanySkill>,
    val skill: CompanySkillEvent,
    ) :
    RecyclerView.Adapter<CompanySkillAdapter.CompanySkillViewHolder>() {

    lateinit var binding: ItemSkillBinding

    inner class CompanySkillViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val btnMenu = binding.btnMenuEntryExit
        fun bindData(position: Int, clickListener: CompanySkillEvent) {

            binding.txtNameSkill.text = data[position].nameCompanySkill
            binding.progressLimit4.progress = data[position].volumeSkill

            binding.btnMenuEntryExit.setOnClickListener {
                clickListener.onMenuItemClick(data[position], position)
            }

        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CompanySkillViewHolder {
        binding =
            ItemSkillBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return CompanySkillViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: CompanySkillViewHolder, position: Int) {
        holder.bindData(position, skill)
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

    fun removeEmployee(oldCompanySkill: CompanySkill, oldPosition: Int) {
        data.remove(oldCompanySkill)
        notifyItemRemoved(oldPosition)
    }

    interface CompanySkillEvent {
        fun onMenuItemClick(
            companySkill: CompanySkill,
            position: Int,
        )
    }

}