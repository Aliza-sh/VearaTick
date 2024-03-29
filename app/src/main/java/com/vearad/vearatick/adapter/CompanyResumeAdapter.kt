package com.vearad.vearatick.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.vearad.vearatick.model.db.Project
import com.vearad.vearatick.R
import com.vearad.vearatick.databinding.ItemResumeBinding

class CompanyResumeAdapter(
    val data: ArrayList<Project>,
    val resume: CompanyResumeEvent,
) :
    RecyclerView.Adapter<CompanyResumeAdapter.CompanyResumeViewHolder>() {

    lateinit var binding: ItemResumeBinding

    inner class CompanyResumeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val btnMenu = binding.btnMenuEntryExit
        fun bindData(position: Int, clickListener: CompanyResumeEvent) {

            binding.txtNamePro.text = data[position].nameProject

            /*if (data[position].typeProject == "بک اند")
                binding.imgProject.setImageResource(R.drawable.img_backend)
            else if (data[position].typeProject == "فرانت اند")
                binding.imgProject.setImageResource(R.drawable.img_frontend)
            else if (data[position].typeProject == "رباتیک")
                binding.imgProject.setImageResource(R.drawable.img_robotic)
            else if (data[position].typeProject == "طراحی")
                binding.imgProject.setImageResource(R.drawable.img_designing)
            else if (data[position].typeProject == "سئو")
                binding.imgProject.setImageResource(R.drawable.img_seo)*/

            if (data[position].urlProject != "")
                binding.icGithub.backgroundTintList =
                    ContextCompat.getColorStateList(itemView.context, R.color.firoze)

            itemView.setOnClickListener {
                resume.onResumeClicked(data[position], position)
            }

            binding.btnMenuEntryExit.setOnClickListener {
                clickListener.onMenuItemClick(data[position], position)
            }

        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CompanyResumeViewHolder {
        binding =
            ItemResumeBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return CompanyResumeViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: CompanyResumeViewHolder, position: Int) {
        holder.bindData(position, resume)
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

    fun removeEmployee(oldCompanyResume: Project, oldPosition: Int) {
        data.remove(oldCompanyResume)
        notifyItemRemoved(oldPosition)
    }

    interface CompanyResumeEvent {

        fun onResumeClicked(
            companyResume: Project,
            position: Int,
        )

        fun onMenuItemClick(
            companyResume: Project,
            position: Int,
        )
    }

}