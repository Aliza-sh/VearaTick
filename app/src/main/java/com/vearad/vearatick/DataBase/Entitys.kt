package com.vearad.vearatick.DataBase

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
    val imagePath: String? = ""
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
    val entryAll: String? = null,
    val exit: String? = null,
    val exitAll: String? = null
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
    val entryAll: String,
    val exit: Int? = 0,
    val exitAll: String? = "00:00",
    val differenceTime: Int? = 0

)

@Entity(tableName = "taskEmployee_table")
data class TaskEmployee(

    @PrimaryKey(autoGenerate = true)
    val idTask: Int? = null,

    val idEmployee: Int,
    val idTaskProject: Int? = null,

    val nameTask: String,
    val descriptionTask: String,
    val yearCreation: Int,
    val monthCreation: Int,
    val dayCreation: Int,
    val volumeTask: Int,

    val doneTask: Boolean? = false,
    val deadlineTask: Int,

    val efficiencyTask: Int? = 0,
    val projectTask: Boolean? = false,

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
    val valueCalendar: String,
    val noDeadlineProject: Boolean? = false,
    val yearCreation: Int,
    val monthCreation: Int,
    val dayCreation: Int,
    val deadlineTask: Int? = 0,
    val typeProject: String,
    val budgetProject: String? = "0",

    val progressProject: Int? = 0,
    val doneProject: Boolean? = false,
    val settled: Boolean? = false,

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
)

@Entity(tableName = "subTaskProject_table")
data class SubTaskProject(

    @PrimaryKey(autoGenerate = true)
    val idSubTask: Int? = null,

    val idProject: Int,

    val nameSubTask: String,
    val descriptionSubTask: String,
    val doneSubTask: Boolean? = false,
    val valueCalendar: String,
    val yearCreation: Int,
    val monthCreation: Int,
    val dayCreation: Int,
    val yearDeadline: Int,
    val monthDeadline: Int,
    val dayDeadline: Int,
    val yearDone: Int? = 0,
    val monthDone: Int? = 0,
    val dayDone: Int? = 0,
    val deadlineTask: Int,
    val volumeTask: Int,
)

@Entity(tableName = "teamSubTask_table")
data class TeamSubTask(

    @PrimaryKey(autoGenerate = true)
    val idTeam: Int? = null,

    val idProject: Int,
    val idSubTask: Int,
    val idEmployee: Int? = null,

    )

@Entity(tableName = "efficiency_table")
data class EfficiencyEmployee(

    @PrimaryKey(autoGenerate = true)
    val idEfficiency: Int? = null,

    val idEmployee: Int,

    val mustWeekWatch: Int,
    val numberDay: Int,
    val totalWeekWatch: Int,
    val totalMonthWatch: Int,
    val totalWatch: Int,
    val efficiencyWeekPresence: Int,

    val efficiencyTotalPresence: Int,


    val totalWeekDuties: Int,
    val totalMonthDuties: Int,
    val totalDuties: Int,
    val efficiencyWeekDuties: Int,
    val efficiencyMonthDuties: Int,

    val efficiencyTotalDuties: Int,


    val efficiencyTotal: Int
)

@Entity(tableName = "companyReceipt_table")
data class CompanyReceipt(

    @PrimaryKey(autoGenerate = true)
    val idCompanyReceipt: Int? = null,
    val companyReceipt: Long? = 0,
    val monthCompanyReceipt: Int,
    val yearCompanyReceipt: Int,
    val companyReceiptDate: String? = "",
    val companyReceiptDescription: String? = "",
)

@Entity(tableName = "companyExpenses_table")
data class CompanyExpenses(

    @PrimaryKey(autoGenerate = true)
    val idCompanyExpenses: Int? = null,
    val companyExpenses: Long? = 0,
    val companyExpensesDate: String? = "",
    val monthCompanyExpenses: Int,
    val yearCompanyExpenses: Int,
    val companyExpensesDescription: String? = "",
)

@Entity(tableName = "employeeInvestment_table")
data class EmployeeInvestment(

    @PrimaryKey(autoGenerate = true)
    val idInvestment: Int? = null,
    val idEmployee: Int,
    val investment: Long? = 0,
    val investmentDate: String? = "",
    val investmentDescription: String? = "",
)

@Entity(tableName = "employeeHarvest_table")
data class EmployeeHarvest(

    @PrimaryKey(autoGenerate = true)
    val idHarvest: Int? = null,
    val idEmployee: Int,
    val harvest: Long? = 0,
    val harvestDate: String? = "",
    val monthHarvest: Int,
    val yearHarvest: Int,
    val harvestDescription: String? = "",
)
@Entity(tableName = "financialReport_table")
data class FinancialReport(

    @PrimaryKey(autoGenerate = true)
    val idFinancialReport: Int? = null,

    val year: Int,
    val month: Int,

    val income: Long? = 0,
    val expense: Long? = 0,
    val profit: Long? = 0,

    )
@Entity(tableName = "companySkill_table")
data class CompanySkill(

    @PrimaryKey(autoGenerate = true)
    val idCompanySkill: Int? = null,

    val nameCompanySkill: String,
    val volumeSkill: Int,

    )

//@Entity(tableName = "employeeSalary_table")
//data class EmployeeSalary(
//
//    @PrimaryKey(autoGenerate = true)
//    val idSalary:Int? = null,
//    val idEmployee: Int,
//    val investmentEmployee: Long? = 0,
//    val harvestEmployee: Long? = 0,
//    val paymentEmployee: Long? = 0,
//
//    val total: Long? = 0
//)

//@Entity(tableName = "employeePayment_table")
//data class EmployeePayment(
//
//    @PrimaryKey(autoGenerate = true)
//    val idPayment:Int? = null,
//    val idEmployee: Int,
//    val payment: Long? = 0,
//    val paymentDate: String? = "",
//    val paymentDescription: String? = "",
//)