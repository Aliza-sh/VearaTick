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
import ir.aliza.sherkatmanage.DataBase.Task
import ir.aliza.sherkatmanage.R
import ir.aliza.sherkatmanage.databinding.BottomsheetfragmentTaskBinding
import ir.aliza.sherkatmanage.taskDao

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
            "فرانت",
            "رباتیک",
            "سایت",
            "فتوشاپ"
        )

        val myAdapteredt = ArrayAdapter(requireContext(), R.layout.item_gender, typeProject)
        (binding.dialogMainTypeProject.editText as AutoCompleteTextView).setAdapter(
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
            val txtTime = binding.edtTimeTask.text.toString()
            val txtTozih = binding.dialogEdtTozih.text.toString()

            val newTask = Task(
                idEmployee = employee.idEmployee!!,
                nameTask = txtTask,
                timeTask = txtTime,
                descriptionTask = txtTozih,
                year = year.toString(),
                month = monthName,
                day = day.toString()
            )
            taskDao.insert(newTask)
            dismiss()
        } else {
            Toast.makeText(context, "لطفا همه مقادیر را وارد کنید", Toast.LENGTH_SHORT).show()
        }
    }


}