package com.example.omoperation.room.daus

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.omoperation.room.tables.Branches
import com.example.omoperation.room.tables.User

@Dao
interface BranchesDao {
    @Query("select * from branches")
    suspend fun getAll(): List<Branches>
    @Query("delete from branches")
    suspend fun deleteall()

    @Insert
    suspend  fun insertAll(vararg branches: Branches)

}