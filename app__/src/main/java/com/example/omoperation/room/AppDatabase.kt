package com.example.omoperation.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.omoperation.room.daus.BarcodeDao
import com.example.omoperation.room.daus.BranchesDao
import com.example.omoperation.room.daus.EmployeeDaos
import com.example.omoperation.room.daus.ManualDao
import com.example.omoperation.room.daus.RestoreBarcodeDaos
import com.example.omoperation.room.daus.UserDao
import com.example.omoperation.room.daus.VerifyCnDao
import com.example.omoperation.room.tables.Barcode
import com.example.omoperation.room.tables.Branches
import com.example.omoperation.room.tables.CN
import com.example.omoperation.room.tables.Employees
import com.example.omoperation.room.tables.ManualAvr
import com.example.omoperation.room.tables.RestoreBarcode
import com.example.omoperation.room.tables.User

@Database(entities = [User::class,Barcode::class,ManualAvr::class, CN::class, Branches::class,Employees::class, RestoreBarcode::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun   barcodeDao(): BarcodeDao
    abstract fun manualDao(): ManualDao
    abstract fun verifydao(): VerifyCnDao
    abstract fun branchesdao(): BranchesDao
     abstract fun employeedao(): EmployeeDaos
     abstract fun restorebarcodedao(): RestoreBarcodeDaos


    companion object {
      /*  val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create the new table
                database.execSQL("""
            CREATE TABLE IF NOT EXISTS Employees (
                uid INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                BRANCH_NAME TEXT NOT NULL,
                EMP_FIRST_NAME TEXT NOT NULL,
                EMP_EMP_CODE TEXT NOT NULL,
                EMP_EMP_TITLE TEXT NOT NULL,
                EMP_PHONE_NO TEXT NOT NULL,
                EMP_EMAIL_ID TEXT NOT NULL,
                DEPT_NAME TEXT,
                DESIG_NAME TEXT
            )
        """.trimIndent())
            }
        }*/

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    // .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}