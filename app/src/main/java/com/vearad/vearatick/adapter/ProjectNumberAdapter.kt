package com.vearad.vearatick.adapter

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.vearad.vearatick.DataBase.CompanySkill
import com.vearad.vearatick.DataBase.ProjectDao
import com.vearad.vearatick.databinding.ItemNumberProjectBinding

class ProjectNumberAdapter(
    val data: ArrayList<CompanySkill>,
    val projectDao: ProjectDao,
    ) :
    RecyclerView.Adapter<ProjectNumberAdapter.CompanySkillViewHolder>() {

    lateinit var binding: ItemNumberProjectBinding
    var default = false
    var numberColor = 0

    inner class CompanySkillViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindData(position: Int) {
            val shape = GradientDrawable()

            if (data[position].nameCompanySkill ==  "دسته بندی نشده" ) {
                binding.txtNameSkill.text = "دسته بندی نشده"
                shape.shape = GradientDrawable.RECTANGLE
                shape.cornerRadii = floatArrayOf(40f, 40f, 40f, 40f, 40f, 40f, 40f, 40f)
                shape.setColor(
                    Color.parseColor("#FFFFFF")
                )
                binding.colorPro.setBackgroundColor(Color.parseColor("#FFFFFF"))
                val numProject = projectDao.getNumberProject(data[position].nameCompanySkill).size
                binding.txtNumPro.text = numProject.toString()
                default = true

            }else {

                binding.txtNameSkill.text = data[position].nameCompanySkill
                if (default)
                    numberColor = position
                else
                    numberColor = position +1
                val colorId = itemView.context.resources.getIdentifier(
                    "color${numberColor}",
                    "color",
                    itemView.context.packageName
                )
                val color = ContextCompat.getColor(itemView.context, colorId)

                shape.shape = GradientDrawable.RECTANGLE
                shape.cornerRadii = floatArrayOf(40f, 40f, 40f, 40f, 40f, 40f, 40f, 40f)
                shape.setColor(
                    color
                )
                binding.colorPro.setBackgroundColor(color)

                val numProject = projectDao.getNumberProject(data[position].nameCompanySkill).size
                binding.txtNumPro.text = numProject.toString()
            }
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CompanySkillViewHolder {
        binding =
            ItemNumberProjectBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return CompanySkillViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: CompanySkillViewHolder, position: Int) {
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