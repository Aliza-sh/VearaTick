package ir.aliza.sherkatmanage.DataBase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Project::class, TeamProject::class, Employee::class, Day::class, EfficiencyEmployee::class, TaskEmployee::class, Time::class, SubTaskProject::class, TeamSubTask::class,CompanyReceipt::class, CompanyExpenses::class, EmployeeHarvest::class, EmployeeInvestment::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract val projectDao: ProjectDao
    abstract val teamProjectDao: TeamProjectDao
    abstract val employeeDao: EmployeeDao
    abstract val dayDao: DayDao
    abstract val efficiencyDao: EfficiencyDao
    abstract val TaskDao: TaskEmployeeDao
    abstract val timeDao: TimeDao
    abstract val teamSubTaskDao: TeamSubTaskDao
    abstract val subTaskProjectDao: SubTaskProjectDao
    abstract val employeeInvestmentDao: EmployeeInvestmentDao
    abstract val employeeHarvestDao: EmployeeHarvestDao
    abstract val companyExpensesDao: CompanyExpensesDao
    abstract val companyReceiptDao: CompanyReceiptDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDataBase(context: Context): AppDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {

                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "appDatabase.db"
                    )
                        .allowMainThreadQueries()
                        .build()
                }
                return instance
            }
        }
    }
}