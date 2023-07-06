package ir.aliza.sherkatmanage.Dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ir.aliza.sherkatmanage.DataBase.Employee
import ir.aliza.sherkatmanage.DataBase.TaskEmployee
import ir.aliza.sherkatmanage.R
import ir.aliza.sherkatmanage.databinding.BottomsheetfragmentTaskBinding
import ir.aliza.sherkatmanage.taskEmployeeDao

class TaskBottomsheetFragment(
    val employee: Employee,
    val year: Int,
    val monthName: String,
    val day: Int
) : BottomSheetDialogFragment() {

    lateinit var binding: BottomsheetfragmentTaskBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomsheetfragmentTaskBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val typeProject = listOf(
            "اندروید",
            "بک اند",
            "فرانت اند",
            "رباتیک",
            "سایت",
            "فتوشاپ"
        )

        val myAdapteredt = ArrayAdapter(requireContext(), R.layout.item_gender, typeProject)
        (binding.mainEdtTypeTask.editText as AutoCompleteTextView).setAdapter(
            myAdapteredt
        )

        binding.sheetBtnDone.setOnClickListener {

            addNewTask()

        }
    }
    private fun addNewTask() {
        if (
            binding.dialogEdtTask.length() > 0 &&
            binding.dialogEdtTask.length() > 0 &&
            binding.dialogEdtTozih.length() > 0
        ) {
            val txtTask = binding.dialogEdtTask.text.toString()
            val txtTime = binding.edtTimePro.text.toString()
            val txtDay = binding.edtDayProject.text.toString()
            val txtDescription = binding.dialogEdtTozih.text.toString()
            val txtTypeTask = binding.edtTypeTask.text.toString()

            val newTask = TaskEmployee(
                idEmployee = employee.idEmployee!!,
                nameTask = txtTask,
                dayTask = txtDay,
                watchTask = txtTime,
                descriptionTask = txtDescription,
                typeTask = txtTypeTask,

                year = year.toString(),
                month = monthName,
                day = day.toString()
            )
            taskEmployeeDao.insert(newTask)
            dismiss()
        } else {
            Toast.makeText(context, "لطفا همه مقادیر را وارد کنید", Toast.LENGTH_SHORT).show()
        }
    }


}