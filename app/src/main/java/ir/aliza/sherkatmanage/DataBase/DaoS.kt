package ir.aliza.sherkatmanage.DataBase

import androidx.lifecycle.LiveData
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

    @Query("SELECT * FROM day_table WHERE idEmployee = :idEmployee")
    fun getAllDay(idEmployee: Int): LiveData<Day>

    @Query("SELECT * FROM day_table WHERE idDay = :idDay")
    fun getDay(idDay: Long): Day?

}

@Dao
interface TimeDao : BaceDao<Time> {

    @Query("SELECT * FROM time_table")
    fun getAllTime(): List<Time>

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
interface EffDao : BaceDao<Efficiency> {

    @Query("SELECT * FROM eff_table")
    fun getAllEmployee(): List<Efficiency>

}

@Dao
interface TaskDao : BaceDao<Task> {

    @Query("SELECT * FROM task_table")
    fun getAllEmployee(): List<Task>

}


