package com.example.omoperation.room.daus

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.omoperation.room.tables.Branches
import com.example.omoperation.room.tables.Employees
@Dao
interface EmployeeDaos {

    @Query("select * from employees")
    suspend fun getAll(): List<Employees>
    @Query("delete from employees")
    suspend fun deleteall()

    @Insert
    suspend  fun insertAll(vararg employee: Employees)

}