package ir.aliza.sherkatmanage.DataBase

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "employee_table")
data class Employee(

    @PrimaryKey(autoGenerate = true)
    val idEmployee: Int? = null,

    val name: String,
    val family: String,
    val age: Int,
    val gender: String,
    val cellularPhone: Long,
    val homePhone: Long? = 0,
    val address: String? = "",
    val specialty: String,
    val rank: String,
    val skill: String? = "",
    val imgEmployee: ByteArray? = null,

    )

@Entity(tableName = "day_table")
data class Day(

    @PrimaryKey()
    val idDay: Long = 1,

    val idEmployee: Int? = null,
    val year: String,
    val month: String,
    val nameday: String,

    val entry: String? = null,
    val exit: String? = null
)

@Entity(tableName = "time_table")
data class Time(

    @PrimaryKey(autoGenerate = true)
    val idTime: Int? = null,

    val idEmployee: Int,
    val year: String,
    val month: String,
    val day: String,
    val arrival: Boolean,

    val entry: Int,
    val exit: Int
)

@Entity(tableName = "taskEmployee_table")
data class TaskEmployee(

    @PrimaryKey(autoGenerate = true)
    val idTask: Int? = null,

    val idEmployee: Int,

    val nameTask: String,
    val descriptionTask: String,
    val yearCreation: Int,
    val monthCreation: Int,
    val dayCreation: Int,
    val volumeTask: Int,

    val doneTask: Boolean? = false,
    val deadlineTask: Int

    //val progressTask: Int? = 0,
    //val numberSubTaskEmployeeTask: Int? = 0,
    //val numberDoneSubTaskEmployeeTask: Int? = 0,

)

//@Entity(tableName = "subTaskEmployeeTask_table")
//data class SubTaskEmployeeTack(
//
//    @PrimaryKey(autoGenerate = true)
//    val idSubTask: Int? = null,
//
//    val idTask: Int,
//
//    val nameSubTask: String,
//    val descriptionSubTask: String,
//    val doneSubTask: Boolean? = null
//
//)


@Entity(tableName = "project_table")
data class Project(

    @PrimaryKey(autoGenerate = true)
    val idProject: Int? = null,

    val nameProject: String,
    val descriptionProject: String,
    val valueCalendar : String,
    val noDeadlineProject: Boolean? = false,
    val yearCreation: Int,
    val monthCreation: Int,
    val dayCreation: Int,
    val deadlineTask: Int? = 0,
    val typeProject: String,
    val budgetProject: String? = "0",

    val progressProject: Int? = 0,
    val doneProject: Boolean? = false,

    val numberSubTaskProject: Int? = 0,
    val numberDoneSubTaskProject: Int? = 0,
    val doneVolumeProject: Int? = 0,
    val totalVolumeProject: Int? = 0,

    )

@Entity(tableName = "teamProject_table")
data class TeamProject(

    @PrimaryKey(autoGenerate = true)
    val idTeam: Int? = null,

    val idProject: Int,
    val idEmployee: Int? = null,
    val nameEmployee: String? = null,
    val familyEmployee: String? = null,
    val genderEmployee: String? = null,
    val specialtyEmployee: String? = null,

)

@Entity(tableName = "subTaskProject_table")
data class SubTaskProject(

    @PrimaryKey(autoGenerate = true)
    val idSubTask: Int? = null,

    val idProject: Int,

    val nameSubTask: String,
    val descriptionSubTask: String,
    val doneSubTask: Boolean? = false,
    val valueCalendar : String,
    val yearCreation: Int,
    val monthCreation: Int,
    val dayCreation: Int,
    val deadlineTask: Int? = 0,
    val volumeTask: Int,
    )

@Entity(tableName = "teamSubTask_table")
data class TeamSubTask(

    @PrimaryKey(autoGenerate = true)
    val idTeam: Int? = null,

    val idProject: Int,
    val idSubTask: Int,
    val idEmployee: Int? = null,
    val nameEmployee: String? = null,
    val familyEmployee: String? = null,
    val genderEmployee: String? = null,
    val specialtyEmployee: String? = null,

    )

@Entity(tableName = "efficiency_table")
data class EfficiencyEmployee(

    @PrimaryKey(autoGenerate = true)
    val idEfficiency: Int? = null,

    val idEmployee: Int,

    var mustWeekWatch: Int? = 0,
    var numberDay:Int? = 0,
    var totalWeekWatch: Int? = 0,
    var totalMonthWatch: Int? = 0,
    var totalWatch: Int? = 0,
    var efficiencyWeekPresence: Int? = 0,

    var efficiencyTotalPresence: Int? = 0,


    var totalWeekDuties: Int? = 0,
    var totalMonthDuties: Int? = 0,
    var totalDuties: Int? = 0,
    var efficiencyWeekDuties: Int? = 0,
    var efficiencyMonthDuties: Int? = 0,

    var efficiencyTotalDuties: Int? = 0,


    val efficiencyTotal: Int? = 0
)
