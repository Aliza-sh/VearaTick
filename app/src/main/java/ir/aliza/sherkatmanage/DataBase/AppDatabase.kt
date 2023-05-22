package ir.aliza.sherkatmanage.DataBase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Project::class, Employee::class, Efficiency::class, Task::class, Time::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract val projectDao: ProjectDao
    abstract val employeeDao: EmployeeDao
    abstract val effDao: EffDao
    abstract val TaskDao: TaskDao
    abstract val timeDao: TimeDao

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