package com.vearad.vearatick.fgmSub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.vearad.vearatick.DataBase.AppDatabase
import com.vearad.vearatick.DataBase.Project
import com.vearad.vearatick.DataBase.ProjectDao
import com.vearad.vearatick.DataBase.SubTaskProject
import com.vearad.vearatick.DataBase.SubTaskProjectDao
import com.vearad.vearatick.DataBase.TeamProject
import com.vearad.vearatick.DataBase.TeamSubTask
import com.vearad.vearatick.R
import com.vearad.vearatick.databinding.ActivityProAndEmpBinding
import com.vearad.vearatick.databinding.FragmentProjectAddNewTeamBinding
import com.vearad.vearatick.databinding.ItemAddEmployeeToProjectBinding

class ProjectSubTaskAddNewTeamFromInfoFragment(
    val project: Project,
    val subTaskProject: SubTaskProject,
    val position: Int,
    val bindingActivityProAndEmp: ActivityProAndEmpBinding,
    val subTaskProjectDao: SubTaskProjectDao,
    val projectDao: ProjectDao,
) : Fragment() {

    lateinit var binding: FragmentProjectAddNewTeamBinding
    lateinit var bindingItemAddEmployeeToProject: ItemAddEmployeeToProjectBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProjectAddNewTeamBinding.inflate(layoutInflater, null, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        onBackPressed()

        binding.btnBck.setOnClickListener {
            parentFragmentManager.beginTransaction().detach(this@ProjectSubTaskAddNewTeamFromInfoFragment)
                .replace(
                    R.id.layout_pro_and_emp, ProjectInformationFragment(
                        project,
                        subTaskProjectDao,
                        projectDao,
                        position,
                        bindingActivityProAndEmp
                    )
                ).commit()
        }

        val teamSubTaskProjectDao = AppDatabase.getDataBase(view.context).teamProjectDao
        val data = teamSubTaskProjectDao.getListTeamProject(project.idProject!!)

        val addNewPersonToProjectAdapter =
            object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): RecyclerView.ViewHolder {
                    bindingItemAddEmployeeToProject = ItemAddEmployeeToProjectBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                    return object : RecyclerView.ViewHolder(bindingItemAddEmployeeToProject.root) {}
                }

                override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

                    val employeeDao = AppDatabase.getDataBase(holder.itemView.context).employeeDao
                    val teamSubTaskDao =
                        AppDatabase.getDataBase(holder.itemView.context).teamSubTaskDao
                    val employee = employeeDao.getEmployee(data[position].idEmployee!!)
                    val teamSubTask = teamSubTaskDao.getTeamSubTask(
                        project.idProject,
                        subTaskProject.idSubTask!!
                    )
                    val employeeTeamSubTask = teamSubTaskDao.getEmployeeTeamSubTask(
                        data[position].idEmployee!!,
                        project.idProject,
                        subTaskProject.idSubTask
                    )

                    if (employee!!.gender == "زن") {
                        bindingItemAddEmployeeToProject.imgprn2.setImageResource(R.drawable.img_matter)
                    }

                    bindingItemAddEmployeeToProject.txtNameEmployee.text =
                        employee.name + " " + employee.family
                    bindingItemAddEmployeeToProject.txtJobEmployee.text =
                        employee.specialty

                    if (teamSubTask?.idSubTask != null && employeeTeamSubTask != null) {
                        if ( subTaskProject.idSubTask == teamSubTask.idSubTask && employeeTeamSubTask.idEmployee == employee!!.idEmployee) {
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
            employee!!.idEmployee!!,
            project.idProject!!,
            subTaskProject.idSubTask!!
        )

        if (b) {
            val newTeamProject = TeamSubTask(
                idProject = project.idProject,
                idSubTask = subTaskProject.idSubTask,
                idEmployee = employee.idEmployee,

            )
            teamSubTaskDao.insert(newTeamProject)
        } else {
            val newTeamProject = TeamSubTask(
                idTeam = employeeTeamProject?.idTeam,
                idProject = project.idProject,
                idSubTask = subTaskProject.idSubTask,
                idEmployee = employee.idEmployee,

            )
            teamSubTaskDao.delete(newTeamProject)
        }
    }

    private fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    parentFragmentManager.beginTransaction()
                        .detach(this@ProjectSubTaskAddNewTeamFromInfoFragment)
                        .replace(
                            R.id.layout_pro_and_emp,
                            ProjectInformationFragment(
                                project,
                                subTaskProjectDao,
                                projectDao,
                                position,
                                bindingActivityProAndEmp
                            )
                        )
                        .commit()
                }
            }
        )
    }
}