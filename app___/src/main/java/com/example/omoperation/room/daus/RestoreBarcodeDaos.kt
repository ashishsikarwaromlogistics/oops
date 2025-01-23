package com.example.omoperation.room.daus

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.omoperation.model.dataclass.CNWithBoxes
import com.example.omoperation.room.tables.CN2
import com.example.omoperation.room.tables.RestoreBarcode
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Dao
interface RestoreBarcodeDaos {
    @Query("select * from RestoreBarcode")
    suspend fun getAll() : List<RestoreBarcode>

    @Insert
    suspend fun inserbarcode( barcode : RestoreBarcode)

    @Query("DELETE FROM RestoreBarcode")
    suspend fun deleteAllBarcodes()

    @Query("select (substr(barcode, 1, length(barcode) - 4)) cn,count(substr(barcode, 1, length(barcode) - 4)) box , 0 diffbox \n" +
            "from RestoreBarcode\n" +
            "group by substr(barcode, 1, length(barcode) - 4)")
    suspend fun cnwithboxes() : List<CNWithBoxes>


    @Query("select barcode " +
            "from RestoreBarcode\n" +
            "where barcode like :cnvalue || '%'")
    suspend fun getscan(cnvalue: String) : List<String>


    @Query("DELETE FROM RestoreBarcode WHERE barcode = :barcodeValue")
    suspend fun deleteBarcode(barcodeValue: String)





    @Query("SELECT " +
            "  0 uid,  SUBSTR(barcode.barcode, 1, LENGTH(barcode.barcode) - 4) AS CN_No," +
            "    COUNT(SUBSTR(barcode.barcode, 1, LENGTH(barcode.barcode) - 4)) AS BARCODE " +

            "FROM " +
            "barcode " +
            "JOIN " +
            "cn ON SUBSTR(barcode.barcode, 1, LENGTH(barcode.barcode) - 4) = cn.cn " +
            "GROUP BY cn.cn " +
            "UNION all " +
            "SELECT uid,cn  CN_No ,boxes  BARCODE  from ManualAvr")
    suspend fun getcnboxes():List<CN2>
    @Query("select count() from barcode where SUBSTR(barcode.barcode, 1, LENGTH(barcode.barcode) - 4)=:barcode1")
    suspend fun getcurrengr(barcode1 : String):Int


    //@Query("select count() from barcode where SUBSTR(barcode.barcode, 1, LENGTH(barcode.barcode) - 4)=:barcode1")
    @Query("delete from barcode where SUBSTR(barcode.barcode, 1, LENGTH(barcode.barcode) - 4)  IN (:selectedbarcode)")
    suspend fun restoreselecteddata(selectedbarcode : List<String>):Int


    @Query("delete from RestoreBarcode where barcode  IN (:selectedbarcode)")
    suspend fun delete_from_restore(selectedbarcode : List<String>):Int



    @Query("DELETE FROM RestoreBarcode WHERE timestamp < :timeThreshold")
    suspend fun deleteAfter24Hours(timeThreshold: String)




    companion object {

        fun getCurrentTimestamp(): String {
            return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        }
    }

}