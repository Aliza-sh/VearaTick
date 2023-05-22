package ir.aliza.sherkatmanage.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import ir.aliza.sherkatmanage.databinding.ItemMissionBinding

class PagerAdapter(
    private val viewPager2: ViewPager2,
    private val sliderEvent: SliderEvent
) :
    RecyclerView.Adapter<PagerAdapter.PagerAdapterHolder>() {
    lateinit var binding: ItemMissionBinding

    inner class PagerAdapterHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindData(position: Int) {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerAdapterHolder {
        binding = ItemMissionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PagerAdapterHolder(binding.root)
    }

    override fun onBindViewHolder(holder: PagerAdapterHolder, position: Int) {
        holder.bindData(position)
    }

    override fun getItemCount(): Int {
        return 3
    }

    interface SliderEvent{
        fun onSliderClicked(movieId: Int)
    }

}