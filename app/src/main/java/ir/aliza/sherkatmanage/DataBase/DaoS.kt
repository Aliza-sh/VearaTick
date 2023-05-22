package ir.aliza.sherkatmanage.DataBase

import androidx.room.*

interface BaceDao<T> {
    @Insert
    fun insertEmployee(obj: T)

    @Update
    fun updateEmployee(obj: T)

    @Delete
    fun deleteEmployee(obj: T)
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

@Dao
interface TimeDao : BaceDao<Time> {

    @Query("SELECT * FROM time_table")
    fun getAllTime(): List<Time>

}
