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

    @Query("SELECT * FROM day_table WHERE idEmployee = :idEmployee AND year = :year AND month = :month AND  nameday = :nameDay")
    fun getAllNameDay(idEmployee: Int, year: String, month: String, nameDay: String): Day?

    @Query("SELECT * FROM day_table WHERE idEmployee = :idEmployee AND year = :year AND month = :month AND nameday = :nameDay")
    fun getAllEntryExit(
        idEmployee: Int,
        year: String,
        month: String,
        nameDay: String
    ): Day?

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
interface ProjectDao : BaceDao<Project> {

    @Query("SELECT * FROM project_table")
    fun getAllProject(): List<Project>

    @Query("DELETE FROM project_table")
    fun deleteAllProject()

    @Query("SELECT * FROM project_table WHERE nameProject LIKE '%'||:searching || '%'")
    fun searchProject(searching: String): List<Project>

}
@Dao
interface TeamProjectDao : BaceDao<TeamProject> {

    @Query("SELECT * FROM teamProject_table")
    fun getAllTeamProject(): List<TeamProject>

}

@Dao
interface EffDao : BaceDao<Efficiency> {

    @Query("SELECT * FROM eff_table")
    fun getAllEff(): List<Efficiency>

}

@Dao
interface TaskEmployeeDao : BaceDao<TaskEmployee> {

    @Query("SELECT * FROM taskEmployee_table")
    fun getAllTaskEmployee(): List<TaskEmployee>

    @Query("SELECT * FROM taskEmployee_table WHERE idEmployee = :idEmployee AND day = :persianDay ")
    fun getTaskDay(idEmployee: Int, persianDay: Int): TaskEmployee?

    @Query("SELECT * FROM taskEmployee_table WHERE idEmployee = :idEmployee AND year = :year AND month = :month AND day = :day")
    fun getAllTaskInDay(idEmployee: Int, year: String, month: String, day: String): TaskEmployee?
    @Query("SELECT * FROM taskEmployee_table WHERE idEmployee = :idEmployee AND year = :year AND month = :month AND day = :day")
    fun getAllTaskInInDay(idEmployee: Int, year: String, month: String, day: String): List<TaskEmployee>

}

@Dao
interface SubTaskEmployeeTackDao : BaceDao<SubTaskEmployeeTack> {

    @Query("SELECT * FROM subTaskEmployeeTask_table")
    fun getAllSubTaskEmployeeTack(): List<SubTaskEmployeeTack>

//    @Query("SELECT * FROM subTaskEmployeeTask_table WHERE idEmployee = :idEmployee AND day = :persianDay ")
//    fun getTaskDay(idEmployee: Int, persianDay: Int): SubTaskEmployeeTack?
//
//    @Query("SELECT * FROM subTaskEmployeeTask_table WHERE idEmployee = :idEmployee AND year = :year AND month = :month AND day = :day")
//    fun getAllTaskInDay(idEmployee: Int, year: String, month: String, day: String): SubTaskEmployeeTack?
//    @Query("SELECT * FROM subTaskEmployeeTask_table WHERE idEmployee = :idEmployee AND year = :year AND month = :month AND day = :day")
//    fun getAllTaskInInDay(idEmployee: Int, year: String, month: String, day: String): List<SubTaskEmployeeTack>

}

@Dao
interface SubTaskProjectDao : BaceDao<SubTaskProject> {

    @Query("SELECT * FROM subTaskProject_table")
    fun getAllSubTaskProject(): List<SubTaskProject>

//    @Query("SELECT * FROM subTaskProject_table WHERE idEmployee = :idEmployee AND day = :persianDay ")
//    fun getTaskDay(idEmployee: Int, persianDay: Int): SubTaskProject?
//
//    @Query("SELECT * FROM subTaskProject_table WHERE idEmployee = :idEmployee AND year = :year AND month = :month AND day = :day")
//    fun getAllTaskInDay(idEmployee: Int, year: String, month: String, day: String): SubTaskProject?
//    @Query("SELECT * FROM subTaskProject_table WHERE idEmployee = :idEmployee AND year = :year AND month = :month AND day = :day")
//    fun getAllTaskInInDay(idEmployee: Int, year: String, month: String, day: String): List<SubTaskProject>

}


