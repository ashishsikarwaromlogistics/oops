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
import com.example.omoperation.room.tables.CN2
import com.example.omoperation.room.tables.Employees
import com.example.omoperation.room.tables.ManualAvr
import com.example.omoperation.room.tables.RestoreBarcode
import com.example.omoperation.room.tables.User

@Database(entities = [User::class,Barcode::class,ManualAvr::class, CN::class, Branches::class,Employees::class, RestoreBarcode::class , CN2::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun   barcodeDao(): BarcodeDao
    abstract fun manualDao(): ManualDao
    abstract fun verifydao(): VerifyCnDao
    abstract fun branchesdao(): BranchesDao
     abstract fun employeedao(): EmployeeDaos
     abstract fun restorebarcodedao(): RestoreBarcodeDaos


    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add the new column to CN table
                database.execSQL("ALTER TABLE barcode ADD COLUMN find_box TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE RestoreBarcode ADD COLUMN find_box TEXT NOT NULL DEFAULT ''")


               }
        }

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add the new column to CN table
                database.execSQL("ALTER TABLE CN ADD COLUMN find_box TEXT NOT NULL DEFAULT ''")
                database.execSQL("""
            CREATE TABLE IF NOT EXISTS CN2 (
                uid INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                BARCODE TEXT NOT NULL,
                CN_No TEXT NOT NULL,
                find_box TEXT NOT NULL
            )
        """.trimIndent())

            }
        }
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                     .addMigrations(MIGRATION_1_2)
                     .addMigrations(MIGRATION_2_3)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
