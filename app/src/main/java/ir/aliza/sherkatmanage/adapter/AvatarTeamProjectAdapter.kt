package ir.aliza.sherkatmanage.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.aliza.sherkatmanage.DataBase.TeamProject
import ir.aliza.sherkatmanage.databinding.ItemAvatarSmallBinding

class AvatarTeamProjectAdapter(private val data: ArrayList<TeamProject>) :
    RecyclerView.Adapter<AvatarTeamProjectAdapter.AvatarNearViewHolder>() {

    lateinit var binding: ItemAvatarSmallBinding

    inner class AvatarNearViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {

        @SuppressLint("SetTextI18n")
        fun bindData(position: Int) {

//            binding.txtNameEmployee.text = data[position].nameEmployee + " " + data[position].familyEmployee
//            if (data[position].genderEmployee == "زن"){
//                binding.imgEmployee.setImageResource(R.drawable.img_matter);
//            }

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
        return 5
    }

}