package com.example.omoperation.room.daus

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.omoperation.room.tables.CN


@Dao
interface VerifyCnDao {
    @Query("select * from cn")
    suspend fun getAll() : List<CN>

    @Insert
    suspend fun inserbarcode( cn : CN)


    @Query("DELETE FROM cn WHERE cn = :cnvalue")
    suspend fun deletecn(cnvalue: String)

    @Query("DELETE FROM cn")
    suspend fun deleteCN()

    @Query("delete from cn where cn  IN (:cnvalue)")
    suspend fun delete_from_verify(cnvalue : List<String>):Int

    @Query("select box from cn where cn  = (:cnvalue)")
    suspend fun getbox(cnvalue : String):String

}