package ir.aliza.sherkatmanage.Dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import ir.aliza.sherkatmanage.DataBase.Time
import ir.aliza.sherkatmanage.adapter.InOutAdapter
import ir.aliza.sherkatmanage.databinding.FragmentCalendarBinding
import ir.aliza.sherkatmanage.databinding.FragmentDialogDoneEntryBinding
import ir.aliza.sherkatmanage.inOutAdapter
import ir.aliza.sherkatmanage.timeDao

class DoneEntryDialogFragment(
    val idEmployee: Int?,
    val yearMonth: String,
    val dayOfMonth: String,
    val arrival: Boolean
) : DialogFragment() {

    lateinit var binding: FragmentDialogDoneEntryBinding
    lateinit var binding1: FragmentCalendarBinding


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = AlertDialog.Builder(context)
        binding = FragmentDialogDoneEntryBinding.inflate(layoutInflater, null, false)
        binding1 = FragmentCalendarBinding.inflate(layoutInflater, null, false)

        val entry = binding.edtEntryEpm.text
        val exit = binding.edtExitEmp.text

        inOutAdapter = InOutAdapter(ArrayList())
        binding1.rcvInOut.adapter = inOutAdapter

        dialog.setView(binding.root)
        dialog.setCancelable(true)

        binding.dialogBtnSure.setOnClickListener {

            val newTime = Time(
                idEmployee = idEmployee!!,
                yearMonth = yearMonth,
                day = dayOfMonth,
                arrival = true,
                entry = entry.toString(),
                exit = exit.toString()
            )

            timeDao.insert(newTime)
            inOutAdapter.addInOut(newTime)
            dismiss()
        }
        binding.dialogBtnCansel.setOnClickListener {
            dismiss()
        }

        return dialog.create()

    }

}