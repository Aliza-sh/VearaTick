package ir.aliza.sherkatmanage.Dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import ir.aliza.sherkatmanage.DataBase.Time
import ir.aliza.sherkatmanage.MainActivity
import ir.aliza.sherkatmanage.R
import ir.aliza.sherkatmanage.databinding.FragmentDialogArrivalsAndDeparturesBinding
import ir.aliza.sherkatmanage.databinding.ItemCalendarDayBinding
import ir.aliza.sherkatmanage.timeDao

class ArrivalsAndDeparturesDialogFragment(
    val binding1: ItemCalendarDayBinding,
    val idEmployee: Int?,
    val yearMonth: String,
    val dayOfMonth: String
) : DialogFragment() {

    lateinit var binding: FragmentDialogArrivalsAndDeparturesBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = AlertDialog.Builder(context)
        val timeData = timeDao.getAllTime()

        binding = FragmentDialogArrivalsAndDeparturesBinding.inflate(layoutInflater, null, false)

        dialog.setView(binding.root)
        dialog.setCancelable(true)
        binding.dialogBtnSure.setOnClickListener {
            val dialog = DoneEntryDialogFragment(idEmployee, yearMonth, dayOfMonth, true)
            dialog.show((activity as MainActivity).supportFragmentManager, null)
            binding1.viewDaySub.setBackgroundColor(it.context.getColor(R.color.green_700))
            dismiss()
        }
        binding.dialogBtnCansel.setOnClickListener {

            val newTime = Time(
                idEmployee = idEmployee!!,
                yearMonth = yearMonth,
                day = dayOfMonth,
                arrival = false,
                entry = "",
                exit = ""
            )
            timeDao.insert(newTime)

            binding1.viewDaySub.setBackgroundColor(it.context.getColor(R.color.red_800))

            dismiss()
        }

        return dialog.create()

    }

}