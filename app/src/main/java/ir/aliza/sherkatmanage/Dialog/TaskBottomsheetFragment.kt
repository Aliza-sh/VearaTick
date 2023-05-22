package ir.aliza.sherkatmanage.Dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ir.aliza.sherkatmanage.databinding.BottomsheetfragmentTaskBinding

class TaskBottomsheetFragment() : BottomSheetDialogFragment() {

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
        binding.sheetBtnDone.setOnClickListener {

            dismiss()
            //addNewTask()

        }
    }
    /*private fun addNewTask() {
        if (
            binding.dialogEdtTask.length() > 0 &&
            binding.dialogEdtTask.length() > 0 &&
            binding.dialogEdtTozih.length() > 0
        ) {
            val txtTask = binding.dialogEdtTask.text.toString()
            val txtTime = binding.dialogEdtTime.text.toString()
            val txtTozih = binding.dialogEdtTozih.text.toString()

            val newTask = Employee(
                nameDuties = txtTask,
                timeDuties = txtTime,
                explanationDuties = txtTozih,
            )
            taskAdapter.addTask(newTask)
            employeeDao.insertEmployee(newTask)
            dismiss()
        } else {
            Toast.makeText(context, "لطفا همه مقادیر را وارد کنید", Toast.LENGTH_SHORT).show()
        }
    }*/


}