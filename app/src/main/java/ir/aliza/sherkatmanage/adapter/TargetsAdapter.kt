package ir.aliza.sherkatmanage.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.kizitonwose.calendarview.utils.persian.PersianCalendar
import com.kizitonwose.calendarview.utils.persian.withMonth
import ir.aliza.sherkatmanage.DataBase.Targets
import ir.aliza.sherkatmanage.DataBase.TargetsDao
import ir.aliza.sherkatmanage.databinding.BottomsheetfragmentAddNewTargetBinding
import ir.aliza.sherkatmanage.databinding.ItemFixedTargetsBinding
import ir.aliza.sherkatmanage.databinding.ItemTargetsBinding

class TargetsAdapter(
    val data: ArrayList<Targets>,
    private val viewPager2: ViewPager2,
    val parentFragmentManager: FragmentManager,
    val targetsDao: TargetsDao,
    val isFixedItemClicked: Boolean,
    private val sliderEvent: SliderEvent
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    lateinit var bindingTargets: ItemTargetsBinding
    lateinit var bindingFixedTargetsBinding: ItemFixedTargetsBinding
    lateinit var bindingBottomsheet: BottomsheetfragmentAddNewTargetBinding

    private val ITEM_TYPE_FIXED = 1
    private val ITEM_TYPE_DYNAMIC = 2

    inner class TargetsAdapterHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindData(position: Int) {

            bindingTargets.txtNameTarget.text = data[position - 1].nameTarget
            bindingTargets.txtDescripthonTarget.text = data[position - 1].descriptionTarget

            val calendar = PersianCalendar()
            val inDay = calendar.persianDay
            val day = inDay + data[position - 1].dateTarget!!
            val monthValue = day / 30 + calendar.persianMonth
            val dayValue = (day % 30)
            bindingTargets.txtDateTarget.text =
                dayValue.toString() + " " + calendar.withMonth(monthValue).persianMonthName

            itemView.setOnClickListener {
                sliderEvent.onSliderClicked(data[position - 1], position-1)
            }

            itemView.setOnLongClickListener {
                sliderEvent.onSliderLongClicked(data[position - 1], position-1)
                true
            }

        }
    }

    inner class FixedTargetsAdapterHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        bindingBottomsheet = BottomsheetfragmentAddNewTargetBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return when (viewType) {
            ITEM_TYPE_FIXED -> {
                bindingFixedTargetsBinding = ItemFixedTargetsBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                FixedTargetsAdapterHolder(bindingFixedTargetsBinding.root)
            }

            ITEM_TYPE_DYNAMIC -> {
                bindingTargets =
                    ItemTargetsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                TargetsAdapterHolder(bindingTargets.root)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            ITEM_TYPE_FIXED -> {
                holder.itemView.setOnClickListener {
                    val bottomSheetDialog = BottomSheetDialog(holder.itemView.context)
                    showBottomsheet(bottomSheetDialog)
                    bottomSheetDialog.setContentView(bindingBottomsheet.root)
                    bottomSheetDialog.show()

                }
            }

            ITEM_TYPE_DYNAMIC -> {
                val targetsAdapterHolder = holder as TargetsAdapterHolder
                targetsAdapterHolder.bindData(position)
            }
        }
    }

    private fun showBottomsheet(bottomSheetDialog: BottomSheetDialog) {

        bindingBottomsheet.sheetBtnDone.setOnClickListener {

            if (
                bindingBottomsheet.edtNameTerget.length() > 0 &&
                bindingBottomsheet.edtDayTarget.length() > 0 &&
                bindingBottomsheet.edtDescription.length() > 0
            ) {
                val txtNameTerget = bindingBottomsheet.edtNameTerget.text.toString()
                val txtDayTarget = bindingBottomsheet.edtDayTarget.text.toString()
                val txtDescriptionTarget = bindingBottomsheet.edtDescription.text.toString()

                val newTarget = Targets(
                    nameTarget = txtNameTerget,
                    dateTarget = txtDayTarget.toInt(),
                    descriptionTarget = txtDescriptionTarget,
                )
                targetsDao.insert(newTarget)
                addTarget(newTarget)
                bottomSheetDialog.dismiss()

            } else {
                Toast.makeText(it.context, "لطفا همه مقادیر را وارد کنید", Toast.LENGTH_SHORT)
                    .show()
            }

        }
    }

    override fun getItemCount(): Int {
        return if (isFixedItemClicked) data.size + 1 else 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) ITEM_TYPE_FIXED else ITEM_TYPE_DYNAMIC
    }

    fun addTarget(newTarget: Targets) {
        data.add(data.size, newTarget)

        notifyItemInserted(data.size + 1)
    }

    fun updateTarget(newTarget: Targets, position: Int) {
        data.set(position , newTarget)
        notifyItemChanged(position )
    }

    fun removeTarget(oldTarget: Targets, oldPosition: Int) {
        data.remove(oldTarget)
        notifyItemRemoved(oldPosition)
    }

    interface SliderEvent {
        fun onSliderClicked(target: Targets, position: Int)
        fun onSliderLongClicked(target: Targets, position: Int)
    }

}