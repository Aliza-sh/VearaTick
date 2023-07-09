package ir.aliza.sherkatmanage.DataBase.Relations

import androidx.room.Embedded
import androidx.room.Relation
import ir.aliza.sherkatmanage.DataBase.EfficiencyEmployee
import ir.aliza.sherkatmanage.DataBase.Employee
import ir.aliza.sherkatmanage.DataBase.Time

data class EmployeeAndEfficiency (
    @Embedded val employee: Employee,
    @Relation(
        parentColumn = "idEmployee",
        entityColumn = "idEfficiency"
    )
    val efficiency: EfficiencyEmployee

)data class EmployeeAndTime (
    @Embedded val employee: Employee,
    @Relation(
        parentColumn = "idEmployee",
        entityColumn = "idTime"
    )
    val time: Time
)