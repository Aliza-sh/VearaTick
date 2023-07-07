package ir.aliza.sherkatmanage.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import ir.aliza.sherkatmanage.DataBase.AppDatabase
import ir.aliza.sherkatmanage.DataBase.Employee
import ir.aliza.sherkatmanage.DataBase.Project
import ir.aliza.sherkatmanage.DataBase.TeamProject
import ir.aliza.sherkatmanage.R
import ir.aliza.sherkatmanage.databinding.ItemNewPersonToProjectBinding

class AddNewPersonToProjectAdapter(private val data: ArrayList<Employee>, val project: Project) :
    RecyclerView.Adapter<AddNewPersonToProjectAdapter.AddNewPersonToProjectViewHolder>() {

    lateinit var binding: ItemNewPersonToProjectBinding

    inner class AddNewPersonToProjectViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        @SuppressLint("SetTextI18n")
        fun bindData(position: Int) {

            val employeeDao = AppDatabase.getDataBase(itemView.context).employeeDao
            val teamProjectDao = AppDatabase.getDataBase(itemView.context).teamProjectDao
            val employee = employeeDao.getEmployee(data[position].idEmployee!!)
            val teamProject = teamProjectDao.getTeamProject(data[position].idEmployee!!)

            if (data[position].gender == "زن") {
                binding.imgprn2.setImageResource(R.drawable.img_matter)
            }

            binding.txtNameEmployee.text = data[position].name + " " + data[position].family
            binding.txtJobEmployee.text = data[position].specialty

            if (employee!!.isToProject != null) {
                if (employee.isToProject == data[position].isToProject) {
                    binding.ckbEmployee.isChecked = true
                }
            }

            binding.ckbEmployee.setOnCheckedChangeListener() { compoundButton: CompoundButton, b: Boolean ->

                if (b) {

                    val newTeamProject = TeamProject(
                        idProject = project.idProject!!,
                        idEmployee = employee.idEmployee,
                        nameEmployee = employee.name,
                        familyEmployee = employee.family,
                        genderEmployee = employee.gender
                    )

                    val newEmployee = Employee(
                        idEmployee = employee.idEmployee,
                        name = employee.name,
                        family = employee.family,
                        age = employee.age,
                        gender = employee.gender,
                        cellularPhone = employee.cellularPhone,
                        homePhone = employee.homePhone,
                        address = employee.address,
                        specialty = employee.specialty,
                        skill = employee.skill,
                        imgEmployee = employee.imgEmployee,
                        isToProject = project.idProject
                    )
                    employeeDao.update(newEmployee)
                    teamProjectDao.insert(newTeamProject)

                } else {

                    val newTeamProject = TeamProject(
                        idTeam = teamProject!!.idTeam,
                        idProject = project.idProject!!,
                        idEmployee = employee!!.idEmployee,
                        nameEmployee = employee.name,
                        familyEmployee = employee.family,
                        genderEmployee = employee.gender
                    )

                    val newEmployee = Employee(
                        idEmployee = employee.idEmployee,
                        name = employee.name,
                        family = employee.family,
                        age = employee.age,
                        gender = employee.gender,
                        cellularPhone = employee.cellularPhone,
                        homePhone = employee.homePhone,
                        address = employee.address,
                        specialty = employee.specialty,
                        skill = employee.skill,
                        imgEmployee = employee.imgEmployee,
                        isToProject = null
                    )
                    employeeDao.update(newEmployee)
                    teamProjectDao.delete(newTeamProject)

                }

            }

        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AddNewPersonToProjectViewHolder {
        binding = ItemNewPersonToProjectBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AddNewPersonToProjectViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: AddNewPersonToProjectViewHolder, position: Int) {
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