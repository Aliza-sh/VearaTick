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
import ir.aliza.sherkatmanage.databinding.BottomsheetfragmentTaskEmployeeBinding
import ir.aliza.sherkatmanage.fgmSub.EmployeeTaskInDayFragment
import org.joda.time.DateTime
import org.joda.time.Days

class EmployeeTaskBottomsheetFragment(
    var employee: Employee,
    val employeeDao: EmployeeDao,
    val efficiencyEmployeeDao: EfficiencyDao,
    val position: Int,
    val taskEmployeeDao: TaskEmployeeDao,
    val selectedDate: PersianCalendar,
    val today: PersianCalendar,
    val bindingActivityProAndEmpBinding: ActivityProAndEmpBinding,

    ) : BottomSheetDialogFragment() {

    lateinit var binding: BottomsheetfragmentTaskEmployeeBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomsheetfragmentTaskEmployeeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.sheetBtnDone.setOnClickListener {
            addNewTask()
        }
    }

    fun onTaskToEmployee() {
        parentFragmentManager.beginTransaction().detach(this@EmployeeTaskBottomsheetFragment)
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
            val txtTask = binding.dialogEdtTask.text.toString()
            val txtDescription = binding.dialogEdtTozih.text.toString()
            val txtVolumeTask = binding.edtVolumeTask.text.toString()

            val startDate =
                DateTime(today.persianYear, today.persianMonth, today.persianDay, 0, 0, 0)
            val endDate = DateTime(
                selectedDate.persianYear,
                selectedDate.persianMonth,
                selectedDate.persianDay,
                0,
                0,
                0
            )
            var daysBetween = Days.daysBetween(startDate, endDate).days

            val newTask = TaskEmployee(
                idEmployee = employee.idEmployee!!,
                nameTask = txtTask,
                descriptionTask = txtDescription,
                volumeTask = txtVolumeTask.toInt(),
                yearCreation = selectedDate.persianYear,
                monthCreation = selectedDate.persianMonth,
                dayCreation = selectedDate.persianDay,
                deadlineTask = daysBetween
            )
            taskEmployeeDao.insert(newTask)
            onTaskToEmployee()
            dismiss()
        } else {
            Toast.makeText(context, "لطفا همه مقادیر را وارد کنید", Toast.LENGTH_SHORT).show()
        }
    }

}