package ir.aliza.sherkatmanage.Dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ir.aliza.sherkatmanage.DataBase.SubTaskEmployeeTack
import ir.aliza.sherkatmanage.DataBase.SubTaskEmployeeTackDao
import ir.aliza.sherkatmanage.DataBase.TaskEmployee
import ir.aliza.sherkatmanage.DataBase.TaskEmployeeDao
import ir.aliza.sherkatmanage.adapter.SubTaskEmployeeTackAdapter
import ir.aliza.sherkatmanage.databinding.BottomsheetfragmentSubtaskEmployeetaskBinding
import ir.aliza.sherkatmanage.databinding.FragmentTaskInformationBinding

class SubTaskEmployeeTaskBottomsheetFragment(
    val subTaskProjectDao: SubTaskEmployeeTackDao,
    val task: TaskEmployee,
    val subTaskEmployeeTaskAdapter: SubTaskEmployeeTackAdapter,
    val taskEmployeeDao: TaskEmployeeDao,
    val binding1: FragmentTaskInformationBinding,
) : BottomSheetDialogFragment() {

    lateinit var binding: BottomsheetfragmentSubtaskEmployeetaskBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomsheetfragmentSubtaskEmployeetaskBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.sheetBtnDone.setOnClickListener {

            addNewTask()

        }
    }

    private fun addNewTask() {
        if (
            binding.edtNameTask.length() > 0 &&
            binding.edtDescriptionTask.length() > 0
        ) {
            val txtTask = binding.edtNameTask.text.toString()
            val txtDescription = binding.edtDescriptionTask.text.toString()

            val newSubTask = SubTaskEmployeeTack(

                idTask = task.idTask!!,
                nameSubTask = txtTask,
                descriptionSubTask = txtDescription,

                )
            subTaskProjectDao.insert(newSubTask)
            subTaskEmployeeTaskAdapter.addTask(newSubTask)

            val task1 = taskEmployeeDao.getTaskDay(task.idTask,task.day.toInt())

            var numberSubTaskEmployeeTask = task1!!.numberSubTaskEmployeeTask

            val newTack = TaskEmployee(
                idTask = task1.idTask,
                idEmployee = task1.idEmployee,
                nameTask = task1.nameTask,
                dayTask = task1.dayTask,
                watchTask = task1.watchTask,
                typeTask = task1.typeTask,
                descriptionTask = task1.descriptionTask,
                numberSubTaskEmployeeTask = numberSubTaskEmployeeTask!! + 1,
                numberDoneSubTaskEmployeeTask = task1.numberDoneSubTaskEmployeeTask,

                year = task1.year,
                month = task1.month,
                day = task1.day

            )
            taskEmployeeDao.update(newTack)
            binding1.txtNumTask.text =
                task1.numberDoneSubTaskEmployeeTask.toString() + " از " + numberSubTaskEmployeeTask.toString()

            dismiss()
        } else {
            Toast.makeText(context, "لطفا همه مقادیر را وارد کنید", Toast.LENGTH_SHORT).show()
        }
    }

}