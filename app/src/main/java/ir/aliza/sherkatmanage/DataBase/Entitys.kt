package ir.aliza.sherkatmanage.DataBase

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "project_table")
data class Project(

    @PrimaryKey(autoGenerate = true)
    val idProject: Int? = null,

    val nameProject: String,
    val informationProject: String,
    val watchProject: Int,
    val dayProject: String,
    val typeProject: String,
    val progresProject: Int? = null,

    val teamProject: Int? = null,
    val taskProject: Int? = null

)

@Entity(tableName = "employee_table")
data class Employee(

    @PrimaryKey(autoGenerate = true)
    val idEmployee: Int? = null,

    val name: String,
    val family: String,
    val age: Int,
    val gender: String,
    val cellularPhone: Long,
    val homePhone: Long,
    val address: String,
    val specialty: String,
    val skill: String,

    val watch: String

)

@Entity(tableName = "day_table")
data class Day(

    @PrimaryKey()
    val idDay: Long = 1,

    val idEmployee: Int? = null,
    val yearMonth: String,

    val day: String
)

@Entity(tableName = "time_table")
data class Time(

    @PrimaryKey(autoGenerate = true)
    val idTime: Int? = null,

    val idEmployee: Int,
    val yearMonth: String,
    val day: String,
    val arrival: Boolean,

    val entry: String,
    val exit: String
)

@Entity(tableName = "eff_table")
data class Efficiency(

    @PrimaryKey(autoGenerate = true)
    val idEfficiency: Int,

    val idEmployee: Int,

    val efficiencyTask: Int,
    val efficiencyPresence: Int,
    val efficiencyWeekDuties: Int,
    val efficiencyWeekPresence: Int,
    val efficiencyTotal: Int

)

@Entity(tableName = "task_table")
data class Task(

    @PrimaryKey(autoGenerate = true)
    val idTask: Int,

    val idEmployee: Int,

    val nameTask: String,
    val timeTask: String,
    val explanationTask: String
)