package com.example.omoperation.room.daus

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.omoperation.room.tables.Barcode
import com.example.omoperation.room.tables.ManualAvr

@Dao
interface ManualDao {
    @Query("select * from manualavr")
    suspend fun getData(): List<ManualAvr>

    @Query("delete from manualavr where cn =:cnnum")
    suspend fun deleteoneentry(vararg cnnum:String)

    @Insert
    suspend fun inserbarcode( barcode : ManualAvr)

    @Query("delete from ManualAvr")
    suspend fun DeleteAllManual()
}