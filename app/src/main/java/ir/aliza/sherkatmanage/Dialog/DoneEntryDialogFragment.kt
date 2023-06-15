package ir.aliza.sherkatmanage.Dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import ir.aliza.sherkatmanage.DataBase.Time
import ir.aliza.sherkatmanage.R
import ir.aliza.sherkatmanage.databinding.FragmentCalendarBinding
import ir.aliza.sherkatmanage.databinding.FragmentDialogDoneEntryBinding
import ir.aliza.sherkatmanage.databinding.ItemCalendarDayBinding
import ir.aliza.sherkatmanage.timeDao

class DoneEntryDialogFragment(
    var binding2: ItemCalendarDayBinding,
    val idEmployee: Int?,
    val year: String,
    val month: String,
    val day: Int,
    val arrival: Boolean
) : DialogFragment() {

    lateinit var binding: FragmentDialogDoneEntryBinding
    lateinit var binding1: FragmentCalendarBinding


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = AlertDialog.Builder(context)
        binding = FragmentDialogDoneEntryBinding.inflate(layoutInflater, null, false)
        binding1 = FragmentCalendarBinding.inflate(layoutInflater, null, false)

        val timeData = timeDao.getTime(idEmployee!!, day)

        val entry = binding.edtEntryEpm.text
        val exit = binding.edtExitEmp.text

        dialog.setView(binding.root)
        dialog.setCancelable(true)

        binding.dialogBtnSure.setOnClickListener {

            if (
                binding.edtEntryEpm.length() > 0 &&
                binding.edtExitEmp.length() > 0

            ) {

                val newTime = Time(
                    timeData?.idTime,
                    idEmployee = idEmployee,
                    year = year,
                    month = month,
                    day = day.toString(),
                    arrival = true,
                    entry = entry.toString(),
                    exit = exit.toString()
                )

                if (day.toString() == timeData?.day) {
                    timeDao.update(newTime)
                    //inOutAdapter.updateInOut(newTime, 0)
                    binding2.viewDaySub.setBackgroundColor(it.context.getColor(R.color.green_700))
                } else {
                    timeDao.insert(newTime)
                    binding2.viewDaySub.setBackgroundColor(it.context.getColor(R.color.green_700))
                }

                dismiss()
            } else {
                Toast.makeText(context, "لطفا همه مقادیر را وارد کنید", Toast.LENGTH_SHORT).show()
            }
        }
        return dialog.create()

    }

}