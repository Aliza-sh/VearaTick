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
    var binding1: ItemCalendarDayBinding,
    val idEmployee: Int,
    val year: String,
    val month: String,
    val day: Int,
    val arrival: Boolean,

    ) : DialogFragment() {

    lateinit var binding: FragmentDialogArrivalsAndDeparturesBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = AlertDialog.Builder(context)
        val timeData = timeDao.getAllTime()

        binding = FragmentDialogArrivalsAndDeparturesBinding.inflate(layoutInflater, null, false)

        dialog.setView(binding.root)
        dialog.setCancelable(true)
        binding.dialogBtnSure.setOnClickListener {
            val dialog = DoneEntryDialogFragment(binding1, idEmployee, year, month, day, true)
            dialog.show((activity as MainActivity).supportFragmentManager, null)
            dialog.isCancelable = false
            dismiss()
        }
        binding.dialogBtnCansel.setOnClickListener {

            val timeData = timeDao.getTime(idEmployee, day)

            val newTime = Time(
                timeData?.idTime,
                idEmployee = idEmployee,
                year = year,
                month = month,
                day = day.toString(),
                arrival = false,
                entry = "0",
                exit = "0"
            )

            if (day.toString() == timeData?.day) {
                timeDao.update(newTime)
                //inOutAdapter.updateInOut(newTime, 0)
                binding1.viewDaySub.setBackgroundColor(it.context.getColor(R.color.red_800))
            } else {
                timeDao.insert(newTime)
                binding1.viewDaySub.setBackgroundColor(it.context.getColor(R.color.red_800))
            }

            dismiss()
        }

        return dialog.create()

    }

}