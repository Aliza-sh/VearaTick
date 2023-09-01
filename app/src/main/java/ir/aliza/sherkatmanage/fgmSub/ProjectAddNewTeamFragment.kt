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
import ir.aliza.sherkatmanage.DataBase.Employee
import ir.aliza.sherkatmanage.DataBase.Project
import ir.aliza.sherkatmanage.DataBase.ProjectDao
import ir.aliza.sherkatmanage.DataBase.SubTaskProjectDao
import ir.aliza.sherkatmanage.DataBase.TeamProject
import ir.aliza.sherkatmanage.R
import ir.aliza.sherkatmanage.databinding.ActivityProAndEmpBinding
import ir.aliza.sherkatmanage.databinding.FragmentProjectAddNewTeamBinding
import ir.aliza.sherkatmanage.databinding.ItemAddEmployeeToProjectBinding

class ProjectAddNewTeamFragment(
    val project: Project,
    val subTaskProjectDao: SubTaskProjectDao,
    val projectDao: ProjectDao,
    val position: Int,
    val bindingActivityProAndEmp: ActivityProAndEmpBinding
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
            parentFragmentManager.beginTransaction().detach(this@ProjectAddNewTeamFragment)
                .replace(
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

        val employeeDao = AppDatabase.getDataBase(view.context).employeeDao
        val data = employeeDao.getAllEmployee()

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
                return   object : RecyclerView.ViewHolder(bindingItemAddEmployeeToProject.root) {}
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

                val employeeDao = AppDatabase.getDataBase(holder.itemView.context).employeeDao
                val teamProjectDao = AppDatabase.getDataBase(holder.itemView.context).teamProjectDao
                val employee = employeeDao.getEmployee(data[position].idEmployee!!)
                val teamProject = teamProjectDao.getTeamProject(project.idProject!!)
                val  employeeTeamProject = teamProjectDao.getEmployeeTeamProject(
                    data[position].idEmployee!!,
                    project.idProject
                )

                if (data[position].gender == "زن") {
                    bindingItemAddEmployeeToProject.imgprn2.setImageResource(R.drawable.img_matter)
                }

                bindingItemAddEmployeeToProject.txtNameEmployee.text = data[position].name + " " + data[position].family
                bindingItemAddEmployeeToProject.txtJobEmployee.text = data[position].specialty

                if (teamProject?.idProject != null && employeeTeamProject!=null) {
                    if (employeeTeamProject.idEmployee == employee!!.idEmployee) {
                        bindingItemAddEmployeeToProject.ckbEmployee.isChecked = true
                    }
                }

                bindingItemAddEmployeeToProject.ckbEmployee.setOnCheckedChangeListener { _: CompoundButton, b: Boolean ->

                    onAddNewTeam(b,holder.itemView,data[position])

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
        employee1: Employee,

        ) {

        val employeeDao = AppDatabase.getDataBase(itemView.context).employeeDao
        val teamProjectDao = AppDatabase.getDataBase(itemView.context).teamProjectDao
        val employee = employeeDao.getEmployee(employee1.idEmployee!!)
        val  employeeTeamProject = teamProjectDao.getEmployeeTeamProject(
            employee!!.idEmployee!!,
            project.idProject!!
        )

        if (b) {
            val newTeamProject = TeamProject(
                idProject = project.idProject,
                idEmployee = employee.idEmployee,
                nameEmployee = employee.name,
                familyEmployee = employee.family,
                genderEmployee = employee.gender,
                specialtyEmployee = employee.specialty
            )
            teamProjectDao.insert(newTeamProject)
        }
        else {
            val newTeamProject = TeamProject(
                idTeam = employeeTeamProject?.idTeam,
                idProject = project.idProject,
                idEmployee = employee.idEmployee,
                nameEmployee = employee.name,
                familyEmployee = employee.family,
                genderEmployee = employee.gender,
                specialtyEmployee = employee.specialty
            )
            teamProjectDao.delete(newTeamProject)
        }
    }

    private fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    parentFragmentManager.beginTransaction().detach(this@ProjectAddNewTeamFragment)
                        .replace(
                            R.id.layout_pro_and_emp,
                            ProjectInformationFragment(
                                project,
                                project.watchDeadlineProject!!,
                                project.dateDeadlineProject!!,
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