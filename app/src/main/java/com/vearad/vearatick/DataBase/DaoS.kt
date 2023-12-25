package com.vearad.vearatick.DataBase

import androidx.room.*

interface BaceDao<T> {
    @Insert
    fun insert(obj: T)
    @Update
    fun update(obj: T)
    @Delete
    fun delete(obj: T)
}

@Dao
interface EmployeeDao : BaceDao<Employee> {
    @Query("SELECT * FROM employee_table")
    fun getAllEmployee(): List<Employee>

    @Query("SELECT * FROM employee_table WHERE name = :name AND family = :family AND cellularPhone =:cellularPhone")
    fun getObjectAllEmployee(name: String, family: String, cellularPhone: Long): Employee?

    @Query("SELECT * FROM employee_table WHERE idEmployee = :idEmployee")
    fun getEmployee(idEmployee: Int): Employee?

    @Query("DELETE FROM employee_table")
    fun deleteAllEmployee()

    @Query("SELECT * FROM employee_table WHERE family LIKE '%'||:searching || '%'")
    fun searchEmployee(searching: String): List<Employee>

    @Query("SELECT * FROM employee_table WHERE rank = :rank")
    fun rankEmployee(rank: String): List<Employee>

    @Query("SELECT * FROM employee_table WHERE idEmployee = :idEmployee AND dayUse = :day AND monthUse = :month AND yearUse = :year")
    fun useEmployee(idEmployee: Int, day: Int, month: Int, year: Int): Boolean
}

@Dao
interface DayDao : BaceDao<Day> {
    @Query("SELECT * FROM day_table ")
    fun getAllDay(): List<Day>
    @Query("SELECT * FROM day_table WHERE idDay = :idDay")
    fun getDay(idDay: Long): Day?
    @Query("SELECT * FROM day_table WHERE idEmployee = :idEmployee")
    fun getEmployeeDay(idEmployee: Int): List<Day>
    @Query("SELECT * FROM day_table WHERE idEmployee = :idEmployee AND year = :year AND month = :month AND  nameday = :nameDay")
    fun getAllNameDay(idEmployee: Int, year: String, month: String, nameDay: String): Day?
    @Query("SELECT * FROM day_table WHERE idEmployee = :idEmployee AND year = :year AND month = :month AND nameday = :nameDay")
    fun getAllEntryExit(
        idEmployee: Int,
        year: String,
        month: String,
        nameDay: String
    ): Day?
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdateFood(day: Day)
}

@Dao
interface TimeDao : BaceDao<Time> {
    @Query("SELECT * FROM time_table")
    fun getAllTime(): List<Time>
    @Query("SELECT * FROM time_table WHERE idEmployee = :idEmployee")
    fun getEmployeeAllTime(idEmployee: Int): List<Time>
    @Query("SELECT * FROM time_table WHERE idEmployee = :idEmployee AND day = :persianDay ")
    fun getTime(idEmployee: Int, persianDay: Int): Time?
    @Query("SELECT * FROM time_table WHERE idEmployee = :idEmployee AND day = :day")
    fun getDayTime(idEmployee: Int, day: String): List<Time>
    @Query("SELECT * FROM time_table WHERE idTime = :idTime")
    fun getDayTimeMenu(idTime: Int): Time?
    @Query("SELECT * FROM time_table WHERE idEmployee = :idEmployee AND year = :year AND month = :month AND day = :day")
    fun getAllArrivalDay(idEmployee: Int, year: String, month: String, day: String): Time?
    @Query("SELECT * FROM time_table WHERE idEmployee = :idEmployee AND entry = :entry AND exit = :exit ")
    fun getDeleteTime(idEmployee: Int, entry: Int, exit: Int): Time?
}

@Dao
interface TaskEmployeeDao : BaceDao<TaskEmployee> {
    @Query("SELECT * FROM taskEmployee_table")
    fun getAllTaskEmployee(): List<TaskEmployee>
    @Query("SELECT * FROM taskEmployee_table WHERE idEmployee = :idEmployee AND dayCreation = :persianDay ")
    fun getTaskDay(idEmployee: Int, persianDay: Int): TaskEmployee?
    @Query("SELECT * FROM taskEmployee_table WHERE idTask = :idTack AND dayCreation = :persianDay ")
    fun getInTaskDay(idTack: Int, persianDay: Int): TaskEmployee?
    @Query("SELECT * FROM taskEmployee_table WHERE idEmployee = :idEmployee AND yearCreation = :year AND monthCreation = :month AND dayCreation = :day")
    fun getAllTaskInDay(idEmployee: Int, year: Int, month: Int, day: Int): TaskEmployee?
    @Query("SELECT * FROM taskEmployee_table WHERE idEmployee = :idEmployee AND yearCreation = :year AND monthCreation = :month AND dayCreation = :day")
    fun getAllTaskDayInMonth(idEmployee: Int, year: Int, month: Int, day: Int): List<TaskEmployee>
    @Query("SELECT * FROM taskEmployee_table WHERE idEmployee = :idEmployee AND yearCreation = :year AND monthCreation = :month AND dayCreation = :day")
    fun getAllTaskInInDay(idEmployee: Int, year: Int, month: Int, day: Int): List<TaskEmployee>

    @Query("SELECT * FROM taskEmployee_table WHERE idTask = :idTaskEmployee")
    fun getOnClickTaskEmployee(idTaskEmployee: Int): TaskEmployee?

    @Query(
        "SELECT * FROM taskEmployee_table WHERE idEmployee = :idEmployee"
    )
    fun getEmployeeAllTask(idEmployee: Int): List<TaskEmployee>

    @Query("SELECT * FROM taskEmployee_table WHERE idTaskProject = :idTaskProject")
    fun getEmployeeTaskProject(idTaskProject: Int): TaskEmployee?

    @Query("SELECT * FROM taskEmployee_table WHERE  idEmployee = :idEmployee AND idTaskProject = :idTaskProject ")
    fun getEmployeeSTaskSProject(idEmployee: Int, idTaskProject: Int): TaskEmployee?

    @Query("SELECT * FROM taskEmployee_table WHERE idEmployee = :idEmployee AND 1 <= (dayCreation - :toDay) <= 7 AND doneTask == 0")
    fun getTaskInWeek(idEmployee: Int, toDay: Int): Boolean
    @Query("SELECT * FROM taskEmployee_table WHERE idEmployee = :idEmployee AND (dayCreation - :toDay) == 1 AND doneTask == 0")
    fun getTaskTomorrow(idEmployee: Int, toDay: Int): Boolean
    @Query("SELECT * FROM taskEmployee_table WHERE idEmployee = :idEmployee AND (dayCreation - :toDay) = 0 AND doneTask == 0")
    fun getTaskToday(idEmployee: Int, toDay: Int): Boolean
    @Query("SELECT * FROM taskEmployee_table WHERE idEmployee = :idEmployee AND (dayCreation - :toDay) <= -1 AND doneTask == 0")
    fun getTaskPast(idEmployee: Int, toDay: Int): Boolean
    @Query("SELECT * FROM taskEmployee_table WHERE idEmployee = :idEmployee AND doneTask = 1")
    fun getDoneTask(idEmployee: Int): Boolean

}

//@Dao
//interface SubTaskEmployeeTackDao : BaceDao<SubTaskEmployeeTack> {
//    @Query("SELECT * FROM subTaskEmployeeTask_table")
//    fun getAllSubTaskEmployeeTack(): List<SubTaskEmployeeTack>
//    @Query("SELECT * FROM subTaskEmployeeTask_table WHERE idTask = :idTack")
//    fun getSubTaskEmployeeTack(idTack: Int,): List<SubTaskEmployeeTack>
//}

@Dao
interface ProjectDao : BaceDao<Project> {
    @Query("SELECT * FROM project_table")
    fun getAllProject(): List<Project>
    @Query("SELECT * FROM project_table WHERE idProject = :idProject")
    fun getProject(idProject: Int): Project?
    @Query("DELETE FROM project_table")
    fun deleteAllProject()
    @Query("SELECT * FROM project_table WHERE nameProject LIKE '%'||:searching || '%'")
    fun searchProject(searching: String): List<Project>
    @Query("SELECT * FROM project_table ORDER BY idProject DESC LIMIT 1")
    fun getLastRecord(): Project?
    @Query("SELECT * FROM project_table LIMIT 1")
    fun getFirstRecord(): Project?
    @Query("SELECT progressProject FROM project_table")
    fun getColumnprogressProject(): List<Int>
    @Query("SELECT * FROM project_table WHERE typeProject = :typeProject AND doneProject = :doneProject")
    fun getNumberProject(typeProject: String,doneProject: Boolean): List<Project>
    @Query("SELECT * FROM project_table WHERE doneProject = :doneProject")
    fun getAllDoneProject(doneProject: Boolean): List<Project>
}
@Dao
interface TeamProjectDao : BaceDao<TeamProject> {
    @Query("SELECT * FROM teamProject_table")
    fun getAllTeamProject(): List<TeamProject>
    @Query("SELECT * FROM teamProject_table WHERE idProject = :idProject")
    fun getListTeamProject(idProject: Int): List<TeamProject>
    @Query("SELECT * FROM teamProject_table WHERE idProject = :idProject")
    fun getTeamProject(idProject: Int): TeamProject?
    @Query("SELECT * FROM teamProject_table WHERE idEmployee = :idEmployee AND idProject = :idProject")
    fun getEmployeeTeamProject(idEmployee: Int, idProject: Int): TeamProject?
}
@Dao
interface SubTaskProjectDao : BaceDao<SubTaskProject> {
    @Query("SELECT * FROM subTaskProject_table")
    fun getAllSubTaskProject(): List<SubTaskProject>
    @Query("SELECT * FROM subTaskProject_table WHERE idProject = :idProject")
    fun getSubTaskProject(idProject: Int): List<SubTaskProject>
    @Query("SELECT * FROM subTaskProject_table WHERE idSubTask = :idSubTaskProject")
    fun getOnClickSubTaskProject(idSubTaskProject: Int): SubTaskProject?
    @Query("SELECT SUM(volumeTask) FROM subTaskProject_table WHERE idProject = :idProject AND doneSubTask = :doneTaskProject")
    fun getDoneVolumeTaskSum(idProject:Int,doneTaskProject: Boolean): Long
    @Query("SELECT SUM(volumeTask) FROM subTaskProject_table WHERE idProject = :idProject")
    fun getTotalVolumeTaskSum(idProject:Int): Long

    @Query("DELETE FROM subTaskProject_table WHERE idProject = :projectId")
    fun deleteSubTasksByProjectId(projectId: Int)
}
@Dao
interface TeamSubTaskDao : BaceDao<TeamSubTask> {
    @Query("SELECT * FROM teamSubTask_table")
    fun getAllTeamSubTask(): List<TeamSubTask>
    @Query("SELECT * FROM teamSubTask_table WHERE  idProject = :idProject AND idSubTask = :idSubTask")
    fun getListTeamSubTask(idProject: Int,idSubTask: Int): List<TeamSubTask>
    @Query("SELECT * FROM teamSubTask_table WHERE idProject = :idProject AND idSubTask = :idSubTask")
    fun getTeamSubTask(idProject: Int,idSubTask: Int): TeamSubTask?
    @Query("SELECT * FROM teamSubTask_table WHERE idEmployee = :idEmployee AND idProject = :idProject AND idSubTask = :idSubTaskProject")
    fun getEmployeeTeamSubTask(idEmployee: Int, idProject: Int,idSubTaskProject: Int): TeamSubTask?
}
@Dao
interface EfficiencyDao : BaceDao<EfficiencyEmployee> {
    @Query("SELECT * FROM efficiency_table")
    fun getAllEfficiency(): List<EfficiencyEmployee>
    @Query("SELECT * FROM efficiency_table WHERE idEmployee = :idEmployee")
    fun getEfficiencyEmployee(idEmployee: Int): EfficiencyEmployee?
    @Query("SELECT efficiencyWeekDuties FROM efficiency_table")
    fun getColumnEfficiencyWeekDuties(): List<Int>
    @Query("SELECT efficiencyMonthDuties FROM efficiency_table")
    fun getColumnEfficiencyMonthDuties(): List<Int>
    @Query("SELECT efficiencyTotalDuties FROM efficiency_table")
    fun getColumnEfficiencyTotalDuties(): List<Int>
    @Query("SELECT efficiencyWeekPresence FROM efficiency_table")
    fun getColumnEfficiencyWeekPresence(): List<Int>
    @Query("SELECT efficiencyTotalPresence FROM efficiency_table")
    fun getColumnEfficiencyTotalPresence(): List<Int>

    @Query("SELECT CASE WHEN SUM(CASE WHEN idEmployee = 0 OR idEfficiency = 0 THEN 0 ELSE 1 END) = 0 THEN 1 ELSE 0 END FROM efficiency_table")
    fun isAllColumnsNonZero(): Boolean
}
@Dao
interface CompanyReceiptDao : BaceDao<CompanyReceipt> {
    @Query("SELECT * FROM companyReceipt_table")
    fun getAllCompanyReceipt(): List<CompanyReceipt>
    @Query("SELECT * FROM companyReceipt_table WHERE idCompanyReceipt = :idReceipt")
    fun getCompanyReceipt(idReceipt: Int): CompanyReceipt?
    @Query("SELECT SUM(companyReceipt) FROM companyReceipt_table")
    fun getReceiptSum(): Long

    @Query("SELECT SUM(companyReceipt) FROM companyReceipt_table WHERE yearCompanyReceipt = :year AND monthCompanyReceipt = :month")
    fun getReceiptSumYearAndMonth(year: Int,month: Int): Long

    @Query("SELECT * FROM companyReceipt_table WHERE idProject = :idProject")
    fun getReceiptProject(idProject: Int): CompanyReceipt?
}
@Dao
interface CompanyExpensesDao : BaceDao<CompanyExpenses> {
    @Query("SELECT * FROM companyExpenses_table")
    fun getAllCompanyExpenses(): List<CompanyExpenses>
    @Query("SELECT * FROM companyExpenses_table WHERE idCompanyExpenses = :idExpenses")
    fun getCompanyExpenses(idExpenses: Int): CompanyExpenses?
    @Query("SELECT SUM(companyExpenses) FROM companyExpenses_table")
    fun getExpensesSum(): Long
    @Query("SELECT SUM(companyExpenses) FROM companyExpenses_table WHERE yearCompanyExpenses = :year AND monthCompanyExpenses = :month")
    fun getExpensesSumYearAndMonth(year: Int,month: Int): Long
}
//@Dao
//interface EmployeeSalaryDao : BaceDao<EmployeeSalary> {
//    @Query("SELECT * FROM employeeSalary_table")
//    fun getAllEmployeeSalary(): List<EmployeeSalary>
//    @Query("SELECT * FROM employeeSalary_table WHERE idEmployee = :idEmployee")
//    fun getEmployeeSalary(idEmployee: Int): EmployeeSalary
//}
@Dao
interface EmployeeInvestmentDao : BaceDao<EmployeeInvestment> {
    @Query("SELECT * FROM employeeInvestment_table")
    fun getAllEmployeeInvestment(): List<EmployeeInvestment>
    @Query("SELECT * FROM employeeInvestment_table WHERE idEmployee = :idEmployee")
    fun getEmployeeInvestment(idEmployee: Int): List<EmployeeInvestment>
    @Query("SELECT * FROM employeeInvestment_table WHERE idInvestment = :idInvestment")
    fun getOnClickEmployeeInvestment(idInvestment: Int): EmployeeInvestment?
    @Query("SELECT SUM(investment) FROM employeeInvestment_table WHERE idEmployee = :idEmployee ")
    fun getEmployeeInvestmentSum(idEmployee: Int): Long
    @Query("SELECT SUM(investment) FROM employeeInvestment_table")
    fun getInvestmentSum(): Long
}
@Dao
interface EmployeeHarvestDao : BaceDao<EmployeeHarvest> {
    @Query("SELECT * FROM employeeHarvest_table")
    fun getAllEmployeeHarvest(): List<EmployeeHarvest>
    @Query("SELECT * FROM employeeHarvest_table WHERE idEmployee = :idEmployee")
    fun getEmployeeHarvest(idEmployee: Int): List<EmployeeHarvest>
    @Query("SELECT * FROM employeeHarvest_table WHERE idHarvest = :idHarvest")
    fun getOnClickEmployeeHarvest(idHarvest: Int): EmployeeHarvest?
    @Query("SELECT SUM(harvest) FROM employeeHarvest_table WHERE idEmployee = :idEmployee ")
    fun getEmployeeHarvestSum(idEmployee: Int): Long
    @Query("SELECT SUM(harvest) FROM employeeHarvest_table")
    fun getHarvestSum(): Long

    @Query("SELECT SUM(harvest) FROM employeeHarvest_table WHERE yearHarvest = :year AND monthHarvest = :month")
    fun getHarvestSumYearAndMonth(year: Int,month: Int): Long
}
@Dao
interface FinancialReportDao : BaceDao<FinancialReport> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(financialReport: FinancialReport)
    @Query("SELECT * FROM financialReport_table")
    fun getAllListFinancialReportDao(): List<FinancialReport>
    @Query("SELECT * FROM financialReport_table WHERE year = :year")
    fun getAllFinancialYearReportDao(year: Int): List<FinancialReport>
    @Query("SELECT * FROM financialReport_table WHERE year = :year")
    fun getFinancialReportYearDao(year: Int): FinancialReport?
    @Query("SELECT * FROM financialReport_table WHERE year = :year AND month = :month")
    fun getFinancialReportYearAndMonthDao(year: Int , month: Int): FinancialReport?
    @Query("SELECT DISTINCT year FROM financialReport_table ORDER BY year ASC")
    fun getDistinctData(): List<Int>

}
@Dao
interface CompanySkillDao : BaceDao<CompanySkill> {

    @Query("SELECT * FROM companySkill_table")
    fun getAllListCompanySkillDao(): List<CompanySkill>
    @Query("SELECT * FROM companySkill_table")
    fun getAllCompanySkillDao(): CompanySkill?
    @Query("SELECT * FROM companySkill_table WHERE idCompanySkill = :idCompanySkill")
    fun getOnClickEmployeeHarvest(idCompanySkill: Int): CompanySkill?

}
@Dao
interface CompanyInfoDao : BaceDao<CompanyInfo> {
    @Query("SELECT * FROM companyInfo_table")
    fun getCompanyInfoDao(): CompanyInfo?

}
//@Dao
//interface EmployeePaymentDao : BaceDao<EmployeePayment> {
//    @Query("SELECT * FROM employeePayment_table")
//    fun getAllEmployeePayment(): List<EmployeePayment>
//    @Query("SELECT * FROM employeePayment_table WHERE idEmployee = :idEmployee")
//    fun getEmployeePayment(idEmployee: Int): EmployeePayment?
//    @Query("SELECT SUM(payment) FROM employeePayment_table")
//    fun getPaymentSum(): Int
//}
