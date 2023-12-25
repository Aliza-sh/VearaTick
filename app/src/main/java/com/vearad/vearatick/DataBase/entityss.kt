/*
package com.vearad.vearatick.DataBase

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "employee_table")
data class employee(

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

    )

@Entity(
    tableName = "teamProject_table",
    primaryKeys = ["idEmployee", "idProject"],
    foreignKeys =
    [ForeignKey(
        entity = employee::class,
        parentColumns = ["idEmployee"],
        childColumns = ["idEmployee"],
        onDelete = ForeignKey.CASCADE
    ), ForeignKey(
        entity = project::class,
        parentColumns = ["idProject"],
        childColumns = ["idProject"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class teamProject(
    val idEmployee: Int,
    val idProject: Int,
)

@Entity(tableName = "project_table")
data class project(

    @PrimaryKey(autoGenerate = true)
    val idProject: Int? = null,

    val nameProject: String,
    val descriptionProject: String,
    val valueCalendar: String,
    val noDeadlineProject: Boolean? = false,
    val yearCreation: Int,
    val monthCreation: Int,
    val dayCreation: Int,
    val deadlineTask: Int? = 0,
    val typeProject: String,
    val budgetProject: String? = "0",
    val urlProject: String? = "",

    val progressProject: Int? = 0,
    val doneProject: Boolean? = false,
    val settled: Boolean? = false,

    )

@Entity(
    tableName = "createTaskProject_table",
    primaryKeys = ["idProject", "idTaskProject"],
    foreignKeys =
    [ForeignKey(
        entity = project::class,
        parentColumns = ["idProject"],
        childColumns = ["idProject"],
        onDelete = ForeignKey.CASCADE
    ), ForeignKey(
        entity = taskProject::class,
        parentColumns = ["idTaskProject"],
        childColumns = ["idTaskProject"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class createTaskProject(

    val idProject: Int? = null,
    val idTaskProject: Int? = null,

    val nameTask: String,
    val yearCreation: Int,
    val monthCreation: Int,
    val dayCreation: Int,

    val numberTaskProject: Int? = 0,
    val numberDoneTaskProject: Int? = 0,
    val volumeTaskProject: Int? = 0,

    )

@Entity(tableName = "taskProject_table")
data class taskProject(

    @PrimaryKey(autoGenerate = true)
    val idTaskProject: Int? = null,

    val nameSubTask: String,
    val descriptionSubTask: String,
    val doneSubTask: Boolean? = false,
    val valueCalendar: String,
    val yearDeadline: Int,
    val monthDeadline: Int,
    val dayDeadline: Int,
    val yearDone: Int? = 0,
    val monthDone: Int? = 0,
    val dayDone: Int? = 0,
    val deadlineTask: Int,
    val volumeTask: Int,
)

@Entity(
    tableName = "teamTask_table",
    primaryKeys = ["idProject", "idTaskProject","idTeamProject"],
    foreignKeys =
    [ForeignKey(
        entity = teamProject::class,
        parentColumns = ["idTeamProject"],
        childColumns = ["idTeamProject"],
        onDelete = ForeignKey.CASCADE
    ), ForeignKey(
        entity = project::class,
        parentColumns = ["idProject"],
        childColumns = ["idProject"],
        onDelete = ForeignKey.CASCADE
    ), ForeignKey(
        entity = taskProject::class,
        parentColumns = ["idTaskProject"],
        childColumns = ["idTaskProject"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class teamTask(
    val idProject: Int,
    val idTaskProject: Int,
    val idTeamProject: Int

    )*/