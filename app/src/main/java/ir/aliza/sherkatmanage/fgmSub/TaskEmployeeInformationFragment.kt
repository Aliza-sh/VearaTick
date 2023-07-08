package ir.aliza.sherkatmanage.fgmSub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ir.aliza.sherkatmanage.DataBase.AppDatabase
import ir.aliza.sherkatmanage.DataBase.SubTaskEmployeeTack
import ir.aliza.sherkatmanage.DataBase.SubTaskEmployeeTackDao
import ir.aliza.sherkatmanage.DataBase.TaskEmployee
import ir.aliza.sherkatmanage.Dialog.SubTaskEmployeeTaskBottomsheetFragment
import ir.aliza.sherkatmanage.adapter.SubTaskEmployeeTackAdapter
import ir.aliza.sherkatmanage.databinding.FragmentTaskInformationBinding
import ir.aliza.sherkatmanage.taskEmployeeDao

class TaskEmployeeInformationFragment(
    val task: TaskEmployee,
    val day: String,
    val monthName: String
) : Fragment() , SubTaskEmployeeTackAdapter.SubTaskEvent{

    lateinit var binding: FragmentTaskInformationBinding
    lateinit var subTaskEmployeeTaskAdapter: SubTaskEmployeeTackAdapter
    lateinit var subTaskEmployeeTaskDao: SubTaskEmployeeTackDao

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTaskInformationBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.txtNamePro.text = task.nameTask
        binding.txtDescription.text = task.descriptionTask
        binding.txtTime.text = task.watchTask + " : 00"
        binding.txtData.text = day + " " + monthName

        val tack1 = taskEmployeeDao.getTaskDay(task.idTask!!,task.day.toInt())
        binding.txtNumTask.text =
            tack1!!.numberDoneSubTaskEmployeeTask.toString() + " از " + tack1!!.numberSubTaskEmployeeTask.toString()

        subTaskEmployeeTaskDao = AppDatabase.getDataBase(view.context).subTaskEmployeeTackDao

        val subTaskEmployeeTaskData = subTaskEmployeeTaskDao.getSubTaskProject(task.idTask)

        subTaskEmployeeTaskAdapter =
            SubTaskEmployeeTackAdapter(
                ArrayList(subTaskEmployeeTaskData),
                this,
                task,
                subTaskEmployeeTaskDao,
                taskEmployeeDao,
                binding
            )
        binding.rcvTskPro.adapter = subTaskEmployeeTaskAdapter

        onFabClicked()

    }

    private fun onFabClicked() {

        binding.btnFabPro.setOnClickListener {
            val bottomsheet = SubTaskEmployeeTaskBottomsheetFragment(
                subTaskEmployeeTaskDao,
                task,
                subTaskEmployeeTaskAdapter,
                taskEmployeeDao,
                binding
            )
            bottomsheet.show(parentFragmentManager, null)

        }
    }


    override fun onSubTaskClicked(task: SubTaskEmployeeTack, position: Int) {
    }

    override fun onSubTaskLongClicked(subTask: SubTaskEmployeeTack, position: Int) {
//        val dialog = DeleteSubTaskProjectDialogFragment(
//            subTask,
//            position,
//            subTaskProjectDao,
//            subTaskProjectAdapter,
//            projectDao,
//            project,
//            binding
//        )
//        dialog.show((activity as MainActivity).supportFragmentManager, null)

    }

}