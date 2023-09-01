package ir.aliza.sherkatmanage.fgmSub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import ir.aliza.sherkatmanage.DataBase.AppDatabase
import ir.aliza.sherkatmanage.DataBase.Project
import ir.aliza.sherkatmanage.DataBase.ProjectDao
import ir.aliza.sherkatmanage.DataBase.SubTaskProject
import ir.aliza.sherkatmanage.DataBase.SubTaskProjectDao
import ir.aliza.sherkatmanage.DataBase.TeamProject
import ir.aliza.sherkatmanage.DataBase.TeamSubTask
import ir.aliza.sherkatmanage.R
import ir.aliza.sherkatmanage.databinding.ActivityProAndEmpBinding
import ir.aliza.sherkatmanage.databinding.FragmentProjectAddNewTeamBinding
import ir.aliza.sherkatmanage.databinding.ItemAddEmployeeToProjectBinding

class SubTaskAddNewTeamFromSubTaskFragment(
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
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentProjectAddNewTeamBinding.inflate(layoutInflater, null, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        onBackPressed()

        binding.btnBck.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .detach(this@SubTaskAddNewTeamFromSubTaskFragment).replace(
                    R.id.layout_pro_and_emp, ProjectInformationFragment(
                        project,
                        project.watchDeadlineProject!!,
                        project.dateDeadlineProject!!,
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
                        project.idProject!!, subTaskProject.idSubTask!!
                    )
                    val employeeTeamSubTask = teamSubTaskDao.getEmployeeTeamSubTask(
                        data[position].idEmployee!!, project.idProject, subTaskProject.idSubTask
                    )

                    if (data[position].genderEmployee == "زن") {
                        bindingItemAddEmployeeToProject.imgprn2.setImageResource(R.drawable.img_matter)
                    }

                    bindingItemAddEmployeeToProject.txtNameEmployee.text =
                        data[position].nameEmployee + " " + data[position].familyEmployee
                    bindingItemAddEmployeeToProject.txtJobEmployee.text =
                        data[position].specialtyEmployee

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
                nameEmployee = employee.name,
                familyEmployee = employee.family,
                genderEmployee = employee.gender,
                specialtyEmployee = employee.specialty
            )
            teamSubTaskDao.insert(newTeamProject)
        } else {
            val newTeamProject = TeamSubTask(
                idTeam = employeeTeamProject?.idTeam,
                idProject = project.idProject,
                idSubTask = subTaskProject.idSubTask,
                idEmployee = employee.idEmployee,
                nameEmployee = employee.name,
                familyEmployee = employee.family,
                genderEmployee = employee.gender,
                specialtyEmployee = employee.specialty
            )
            teamSubTaskDao.delete(newTeamProject)
        }
    }

    private fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    parentFragmentManager.beginTransaction()
                        .detach(this@SubTaskAddNewTeamFromSubTaskFragment).replace(
                            R.id.layout_pro_and_emp, ProjectSubTaskFragment(
                                project,
                                projectDao,
                                position,
                                bindingActivityProAndEmp,
                                subTaskProjectDao
                            )
                        ).commit()
                }
            }
        )
    }
}