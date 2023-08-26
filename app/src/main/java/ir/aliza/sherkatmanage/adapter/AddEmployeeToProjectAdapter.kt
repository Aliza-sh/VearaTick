package ir.aliza.sherkatmanage.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.aliza.sherkatmanage.DataBase.Employee
import ir.aliza.sherkatmanage.DataBase.EmployeeDao
import ir.aliza.sherkatmanage.DataBase.Project
import ir.aliza.sherkatmanage.DataBase.TeamProject
import ir.aliza.sherkatmanage.DataBase.TeamProjectDao
import ir.aliza.sherkatmanage.databinding.ItemAddEmployeeToProjectBinding

class AddEmployeeToProjectAdapter(private val data: ArrayList<Employee>, val project: Project) :
    RecyclerView.Adapter<AddEmployeeToProjectAdapter.AddEmployeeToProjectViewHolder>() {

    lateinit var binding: ItemAddEmployeeToProjectBinding
    lateinit var employeeDao: EmployeeDao
    lateinit var teamProjectDao: TeamProjectDao
    var employee: Employee? = null
    var teamProject: TeamProject? = null
    var employeeTeamProject: TeamProject? = null

    inner class AddEmployeeToProjectViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        @SuppressLint("SetTextI18n")
        fun bindData(position: Int) {

//            val employeeDao = AppDatabase.getDataBase(itemView.context).employeeDao
//            val teamProjectDao = AppDatabase.getDataBase(itemView.context).teamProjectDao
//            val employee = employeeDao.getEmployee(data[position].idEmployee!!)
//            val teamProject = teamProjectDao.getTeamProject(project.idProject!!)
//            val  employeeTeamProject = teamProjectDao.getEmployeeTeamProject(
//                data[position].idEmployee!!,
//                project.idProject
//            )
//
//            if (data[position].gender == "زن") {
//                binding.imgprn2.setImageResource(R.drawable.img_matter)
//            }
//
//            binding.txtNameEmployee.text = data[position].name + " " + data[position].family
//            binding.txtJobEmployee.text = data[position].specialty
//
//            if (teamProject?.idProject != null && employeeTeamProject!=null) {
//                if (employeeTeamProject.idEmployee == employee!!.idEmployee) {
//
//                    binding.ckbEmployee.isChecked = true
//
//                }
//            }
//
//            binding.ckbEmployee.setOnCheckedChangeListener() { compoundButton: CompoundButton, b: Boolean ->
//
//                if (b) {
//
//                    val newTeamProject = TeamProject(
//                        idProject = project.idProject,
//                        idEmployee = employee!!.idEmployee,
//                        nameEmployee = employee!!.name,
//                        familyEmployee = employee!!.family,
//                        genderEmployee = employee!!.gender
//                    )
//
//                    teamProjectDao.insert(newTeamProject)
//
//                } else {
//
//                    val newTeamProject = TeamProject(
//                        idTeam = employeeTeamProject?.idTeam,
//                        idProject = project.idProject,
//                        idEmployee = employee!!.idEmployee,
//                        nameEmployee = employee!!.name,
//                        familyEmployee = employee!!.family,
//                        genderEmployee = employee!!.gender
//                    )
//                    teamProjectDao.delete(newTeamProject)
//
//                }
//
//            }
//
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AddEmployeeToProjectViewHolder {
        binding = ItemAddEmployeeToProjectBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AddEmployeeToProjectViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: AddEmployeeToProjectViewHolder, position: Int) {
        holder.bindData(position)
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