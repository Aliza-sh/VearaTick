package ir.aliza.sherkatmanage.Dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kizitonwose.calendarview.utils.persian.PersianCalendar
import ir.aliza.sherkatmanage.DataBase.EfficiencyDao
import ir.aliza.sherkatmanage.DataBase.Employee
import ir.aliza.sherkatmanage.DataBase.EmployeeDao
import ir.aliza.sherkatmanage.DataBase.TaskEmployee
import ir.aliza.sherkatmanage.DataBase.TaskEmployeeDao
import ir.aliza.sherkatmanage.R
import ir.aliza.sherkatmanage.databinding.ActivityProAndEmpBinding
import ir.aliza.sherkatmanage.databinding.BottomsheetfragmentUpdeteTaskEmployeeBinding
import ir.aliza.sherkatmanage.fgmSub.EmployeeTaskInDayFragment

class EmployeeUpdateTaskBottomsheetFragment(
    var employee: Employee,
    val employeeDao: EmployeeDao,
    val efficiencyEmployeeDao: EfficiencyDao,
    val position: Int,
    val taskEmployeeDao: TaskEmployeeDao,
    val selectedDate: PersianCalendar,
    val today: PersianCalendar,
    val bindingActivityProAndEmpBinding: ActivityProAndEmpBinding,
    val onClickSubTask: TaskEmployee,

    ) : BottomSheetDialogFragment() {

    lateinit var binding: BottomsheetfragmentUpdeteTaskEmployeeBinding

    var txtTask = ""
    var txtDescription = ""
    var txtVolumeTask = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomsheetfragmentUpdeteTaskEmployeeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setdata()

        binding.sheetBtnDone.setOnClickListener {
            addNewTask()
        }
    }
    private fun setdata() {
        binding.dialogEdtTask.setText(onClickSubTask.nameTask)
        binding.edtVolumeTask.setText(onClickSubTask.volumeTask.toString())
        binding.dialogEdtTozih.setText(onClickSubTask.descriptionTask)

        txtTask = onClickSubTask.nameTask
        txtDescription = onClickSubTask.descriptionTask
        txtVolumeTask = onClickSubTask.volumeTask.toString()
    }


    fun onTaskToEmployee() {
        parentFragmentManager.beginTransaction().detach(this@EmployeeUpdateTaskBottomsheetFragment)
            .replace(
                R.id.layout_pro_and_emp,
                EmployeeTaskInDayFragment(
                    employee,
                    employeeDao,
                    efficiencyEmployeeDao,
                    position,
                    taskEmployeeDao,
                    selectedDate,
                    today,
                    bindingActivityProAndEmpBinding
                )
            ).commit()
    }

    private fun addNewTask() {
        if (
            binding.dialogEdtTask.length() > 0 &&
            binding.edtVolumeTask.length() > 0 &&
            binding.dialogEdtTozih.length() > 0
        ) {
             txtTask = binding.dialogEdtTask.text.toString()
             txtDescription = binding.dialogEdtTozih.text.toString()
             txtVolumeTask = binding.edtVolumeTask.text.toString()

            val newTask = TaskEmployee(
                idTask = onClickSubTask.idTask,
                idEmployee = onClickSubTask.idEmployee,
                nameTask = txtTask,
                descriptionTask = txtDescription,
                volumeTask = txtVolumeTask.toInt(),
                yearCreation = onClickSubTask.yearCreation,
                monthCreation = onClickSubTask.monthCreation,
                dayCreation = onClickSubTask.dayCreation,
                doneTask = onClickSubTask.doneTask
            )
            taskEmployeeDao.update(newTask)
            onTaskToEmployee()
            dismiss()
        } else {
            Toast.makeText(context, "لطفا همه مقادیر را وارد کنید", Toast.LENGTH_SHORT).show()
        }
    }

}