package ir.aliza.sherkatmanage.DataBase

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
    fun getObjectAllEmployee(name: String,family:String,cellularPhone: Long): Employee?
    @Query("SELECT * FROM employee_table WHERE idEmployee = :idEmployee")
    fun getEmployee(idEmployee: Int,): Employee?
    @Query("DELETE FROM employee_table")
    fun deleteAllEmployee()
    @Query("SELECT * FROM employee_table WHERE family LIKE '%'||:searching || '%'")
    fun searchEmployee(searching: String): List<Employee>
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
    @Query("SELECT * FROM time_table WHERE idEmployee = :idEmployee AND day = :persianDay ")
    fun getTime(idEmployee: Int, persianDay: Int): Time?
    @Query("SELECT * FROM time_table WHERE idEmployee = :idEmployee AND day = :day")
    fun getDayTime(idEmployee: Int, day: String): List<Time>
    @Query("SELECT * FROM time_table WHERE idEmployee = :idEmployee AND year = :year AND month = :month AND day = :day")
    fun getAllArrivalDay(idEmployee: Int, year: String, month: String, day: String): Time?
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
    fun getAllTaskInInDay(idEmployee: Int, year: Int, month: Int, day: Int): List<TaskEmployee>
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
    fun getProject(idProject: Int,): Project?
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
    fun getListTeamProject(idProject: Int,): List<TeamProject>
    @Query("SELECT * FROM teamProject_table WHERE idProject = :idProject")
    fun getTeamProject(idProject: Int,): TeamProject?
    @Query("SELECT * FROM teamProject_table WHERE idEmployee = :idEmployee AND idProject = :idProject")
    fun getEmployeeTeamProject(idEmployee: Int, idProject: Int,): TeamProject?
}

@Dao
interface SubTaskProjectDao : BaceDao<SubTaskProject> {
    @Query("SELECT * FROM subTaskProject_table")
    fun getAllSubTaskProject(): List<SubTaskProject>
    @Query("SELECT * FROM subTaskProject_table WHERE idProject = :idProject")
    fun getSubTaskProject(idProject: Int,): List<SubTaskProject>
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
}
