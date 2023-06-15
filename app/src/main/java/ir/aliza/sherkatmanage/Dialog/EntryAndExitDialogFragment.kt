package ir.aliza.sherkatmanage.Dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.kizitonwose.calendarview.model.CalendarMonth
import ir.aliza.sherkatmanage.DataBase.Day
import ir.aliza.sherkatmanage.DataBase.Employee
import ir.aliza.sherkatmanage.R
import ir.aliza.sherkatmanage.databinding.FragmentDialogEntryExitBinding
import ir.aliza.sherkatmanage.dayDao

class EntryAndExitDialogFragment(
    val month: CalendarMonth,
    val employee: Employee,
    val tv: TextView
) : DialogFragment() {

    lateinit var binding: FragmentDialogEntryExitBinding


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = AlertDialog.Builder(context)
        binding = FragmentDialogEntryExitBinding.inflate(layoutInflater, null, false)

        val entry = binding.edtEntryEpm.text
        val exit = binding.edtExitEmp.text

        dialog.setView(binding.root)
        dialog.setCancelable(true)
        binding.dialogBtnSure.setOnClickListener {

            if (
                binding.edtEntryEpm.length() > 0 &&
                binding.edtExitEmp.length() > 0

            ) {

                    val newDay = Day(
                        idDay = ("${tv.id}${employee.idEmployee}").toLong(),
                        idEmployee = employee.idEmployee,
                        year = month.persianCalendar.persianYear.toString(),
                        month = month.persianCalendar.persianMonthName,
                        nameday = "${tv.text}",
                        entry = entry.toString(),
                        exit = exit.toString()
                    )

                    dayDao.insert(newDay)
                    tv.setBackgroundColor(
                        ContextCompat.getColor(
                            tv.context, R.color.firoze
                        )
                    )
                month.yearMonth

                dismiss()
            } else {
                Toast.makeText(context, "لطفا همه مقادیر را وارد کنید", Toast.LENGTH_SHORT).show()
            }
        }
        return dialog.create()
    }
}