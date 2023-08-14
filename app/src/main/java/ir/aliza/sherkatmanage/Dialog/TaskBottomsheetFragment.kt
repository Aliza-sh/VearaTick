package ir.aliza.sherkatmanage.Dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ir.aliza.sherkatmanage.DataBase.Employee
import ir.aliza.sherkatmanage.DataBase.TaskEmployee
import ir.aliza.sherkatmanage.databinding.BottomsheetfragmentTaskBinding
import ir.aliza.sherkatmanage.taskEmployeeDao

class TaskBottomsheetFragment(
    val employee: Employee,
    val year: Int,
    val month: Int,
    val day: Int,
    val watch: Int
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

        binding.sheetBtnDone.setOnClickListener {

            addNewTask()

        }
    }
    private fun addNewTask() {
        if (
            binding.dialogEdtTask.length() > 0 &&
            binding.edtTimePro.length() > 0 &&
            binding.dialogEdtTozih.length() > 0 &&
            binding.edtTimePro.text.toString().toInt() <= 24
        ) {
            val txtTask = binding.dialogEdtTask.text.toString()
            val txtTime = binding.edtTimePro.text.toString()
            val txtDay = binding.edtDayProject.text.toString()
            val txtDescription = binding.dialogEdtTozih.text.toString()
            val txtVolumeTask = binding.edtVolumeTask.text.toString()

            val newTask = TaskEmployee(
                idEmployee = employee.idEmployee!!,
                nameTask = txtTask,
                dayTaskDeadline = txtDay.toInt(),
                watchTaskDeadline = txtTime.toInt(),
                descriptionTask = txtDescription,
                volumeTask = txtVolumeTask.toInt(),

                yearCreation = year,
                monthCreation = month,
                dayCreation = day,
                watchCreation = watch
            )
            taskEmployeeDao.insert(newTask)
            dismiss()
        } else {
            Toast.makeText(context, "لطفا همه مقادیر را وارد کنید", Toast.LENGTH_SHORT).show()
        }
    }

}