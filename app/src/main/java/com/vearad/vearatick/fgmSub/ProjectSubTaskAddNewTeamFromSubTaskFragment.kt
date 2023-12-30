package com.vearad.vearatick.fgmSub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.vearad.vearatick.DataBase.AppDatabase
import com.vearad.vearatick.DataBase.Project
import com.vearad.vearatick.DataBase.ProjectDao
import com.vearad.vearatick.DataBase.SubTaskProject
import com.vearad.vearatick.DataBase.SubTaskProjectDao
import com.vearad.vearatick.DataBase.TaskEmployee
import com.vearad.vearatick.DataBase.TaskEmployeeDao
import com.vearad.vearatick.DataBase.TeamProject
import com.vearad.vearatick.DataBase.TeamSubTask
import com.vearad.vearatick.R
import com.vearad.vearatick.databinding.ActivityProAndEmpBinding
import com.vearad.vearatick.databinding.FragmentProjectAddNewTeamBinding
import com.vearad.vearatick.databinding.ItemAddEmployeeToProjectBinding

class ProjectSubTaskAddNewTeamFromSubTaskFragment(
    val project: Project,
    val subTaskProject: SubTaskProject,
    val position: Int,
    val bindingActivityProAndEmp: ActivityProAndEmpBinding,
    val subTaskProjectDao: SubTaskProjectDao,
    val projectDao: ProjectDao,
) : Fragment() {

    lateinit var binding: FragmentProjectAddNewTeamBinding
    lateinit var bindingItemAddEmployeeToProject: ItemAddEmployeeToProjectBinding
    lateinit var taskEmployeeDao: TaskEmployeeDao


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentProjectAddNewTeamBinding.inflate(layoutInflater, null, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        onBackPressed()
        taskEmployeeDao = AppDatabase.getDataBase(view.context).taskDao

        binding.btnBck.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .detach(this@ProjectSubTaskAddNewTeamFromSubTaskFragment).replace(
                    R.id.layout_pro_and_emp, ProjectInformationFragment(
                        project,
                        subTaskProjectDao,
                        projectDao,
                        position,
                        bindingActivityProAndEmp,false
                    )
                ).commit()
        }

        val teamSubTaskProjectDao = AppDatabase.getDataBase(view.context).teamProjectDao
        val data = teamSubTaskProjectDao.getListTeamProject(project.idProject!!)

        val addNewPersonToProjectAdapter =
            object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

                override fun onCreateViewHolder(
                    parent: ViewGroup, viewType: Int
                ): RecyclerView.ViewHolder {
                    bindingItemAddEmployeeToProject = ItemAddEmployeeToProjectBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                    return object : RecyclerView.ViewHolder(bindingItemAddEmployeeToProject.root) {}
                }

                override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

                    val employeeDao = AppDatabase.getDataBase(holder.itemView.context).employeeDao
                    val teamSubTaskDao =
                        AppDatabase.getDataBase(holder.itemView.context).teamSubTaskDao
                    val employee = employeeDao.getEmployee(data[position].idEmployee!!)
                    val teamSubTask = teamSubTaskDao.getTeamSubTask(
                        project.idProject, subTaskProject.idSubTask!!
                    )
                    val employeeTeamSubTask = teamSubTaskDao.getEmployeeTeamSubTask(
                        data[position].idEmployee!!, project.idProject, subTaskProject.idSubTask
                    )

                    if (employee!!.imagePath != null) {
                        Glide.with(holder.itemView)
                            .load(employee.imagePath)
                            .apply(RequestOptions.circleCropTransform())
                            .into(bindingItemAddEmployeeToProject.imgprn2)
                    } else
                        if (employee.gender == "زن")
                            bindingItemAddEmployeeToProject.imgprn2.setImageResource(R.drawable.img_matter)

                    bindingItemAddEmployeeToProject.txtNameEmployee.text =
                        employee.name + " " + employee.family
                    bindingItemAddEmployeeToProject.txtJobEmployee.text =
                        employee.specialty

                    if (teamSubTask?.idSubTask != null && employeeTeamSubTask != null) {
                        if (subTaskProject.idSubTask == teamSubTask.idSubTask && employeeTeamSubTask.idEmployee == employee!!.idEmployee) {
                            bindingItemAddEmployeeToProject.ckbEmployee.isChecked = true
                        }
                    }

                    bindingItemAddEmployeeToProject.ckbEmployee.setOnCheckedChangeListener { _: CompoundButton, b: Boolean ->

                        onAddNewTeam(b, holder.itemView, data[position])

                    }
                }

                override fun getItemId(position: Int): Long {
                    return position.toLong()
                }

                override fun getItemViewType(position: Int): Int {
                    return position
                }

                override fun getItemCount(): Int {
                    return data.size
                }
            }

        binding.recyclerView.adapter = addNewPersonToProjectAdapter

    }

    private fun onAddNewTeam(
        b: Boolean,
        itemView: View,
        employee1: TeamProject,

        ) {

        val employeeDao = AppDatabase.getDataBase(itemView.context).employeeDao
        val teamSubTaskDao = AppDatabase.getDataBase(itemView.context).teamSubTaskDao
        val employee = employeeDao.getEmployee(employee1.idEmployee!!)
        val employeeTeamProject = teamSubTaskDao.getEmployeeTeamSubTask(
            employee!!.idEmployee!!, project.idProject!!, subTaskProject.idSubTask!!
        )

        if (b) {
            val newTeamProject = TeamSubTask(
                idProject = project.idProject,
                idSubTask = subTaskProject.idSubTask,
                idEmployee = employee.idEmployee,
            )
            teamSubTaskDao.insert(newTeamProject)

            val newTask = TaskEmployee(
                idEmployee = employee.idEmployee!!,
                idTaskProject = subTaskProject.idSubTask,
                nameTask = subTaskProject.nameSubTask,
                descriptionTask = subTaskProject.descriptionSubTask,
                volumeTask = subTaskProject.volumeTask,
                yearDeadline = subTaskProject.yearDeadline,
                monthDeadline = subTaskProject.monthDeadline,
                dayDeadline = subTaskProject.dayDeadline,
                deadlineTask = subTaskProject.deadlineTask,
                projectTask = true,
                dayCreation = subTaskProject.dayCreation,
                monthCreation = subTaskProject.monthCreation,
                yearCreation = subTaskProject.yearCreation,
                        dayDone = 0,
                monthDone = 0,
                yearDone = 0
            )
            taskEmployeeDao.insert(newTask)

        } else {
            val newTeamProject = TeamSubTask(
                idTeam = employeeTeamProject?.idTeam,
                idProject = project.idProject,
                idSubTask = subTaskProject.idSubTask,
                idEmployee = employee.idEmployee,

                )
            teamSubTaskDao.delete(newTeamProject)

            val taskProjectEmployee =
                taskEmployeeDao.getEmployeeTaskProject(subTaskProject.idSubTask)
            taskEmployeeDao.delete(taskProjectEmployee!!)
        }
    }

    private fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    parentFragmentManager.beginTransaction()
                        .detach(this@ProjectSubTaskAddNewTeamFromSubTaskFragment).replace(
                            R.id.layout_pro_and_emp, ProjectSubTaskFragment(
                                project,
                                projectDao,
                                position,
                                bindingActivityProAndEmp,
                                subTaskProjectDao,
                            )
                        ).commit()
                }
            }
        )
    }
}