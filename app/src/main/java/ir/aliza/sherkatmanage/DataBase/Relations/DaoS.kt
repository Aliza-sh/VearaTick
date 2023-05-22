package ir.aliza.sherkatmanage.DataBase.Relations

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface EmployeeAndEfficiencyDao {

    @Transaction
    @Query("SELECT * FROM EMPLOYEE_TABLE WHERE idEmployee = :idEmployee")
    suspend fun getEmployeeAndEfficiencyWithIdemployee (idEmployee: Int):List<EmployeeAndEfficiency>
}

@Dao
interface EmployeeAndTimeDao {

    @Transaction
    @Query("SELECT * FROM EMPLOYEE_TABLE WHERE idEmployee = :idEmployee")
    suspend fun getEmployeeAndTimeWithIdemployee (idEmployee: Int):List<EmployeeAndTime>
}
