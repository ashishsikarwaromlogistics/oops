package com.example.omoperation.room.daus

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.omoperation.model.barcode_load.Cn
import com.example.omoperation.model.dataclass.CNWithBoxes
import com.example.omoperation.room.tables.Barcode
import com.example.omoperation.room.tables.CN2
import com.example.omoperation.room.tables.ManualAvr

@Dao
interface BarcodeDao {
    @Query("select * from Barcode order by barcode")
   suspend fun getAll() : List<Barcode>

    @Insert
    suspend fun inserbarcode( barcode : Barcode)

    @Query("DELETE FROM Barcode")
    suspend fun deleteAllBarcodes()

    @Query("select (substr(barcode, 1, length(barcode) - 4)) cn,count(substr(barcode, 1, length(barcode) - 4)) box , 0 diffbox \n" +
            "from barcode\n" +
            "group by substr(barcode, 1, length(barcode) - 4)")
    suspend fun cnwithboxes() : List<CNWithBoxes>

    @Query("SELECT COUNT(*) AS a\n" +
            "FROM barcode\n" +
            "WHERE SUBSTR(barcode, 1, LENGTH(barcode) - 4) = :cn")
    suspend infix fun getboxcn(cn : String):Int


    @Query("SELECT barcode " +
            "FROM barcode\n" +
            "WHERE SUBSTR(barcode, 1, LENGTH(barcode) - 4) = :cn")
    suspend infix fun gettotalboxofcn(cn : String):List<String>


       val prefix: String
       get() = "%"

    @Query("select barcode " +
            "from barcode\n" +
            "where barcode like '%' || :cnvalue || '%' order by barcode")
    suspend fun getscan(cnvalue: String) : List<String>

 @Query("select (substr(barcode, 1, length(barcode) - 4)) cn " +
         "from barcode\n" +
         "where cn= :cnvalue")
 suspend fun checkcnexist(cnvalue: String) : List<String>


    @Query("DELETE FROM Barcode WHERE barcode = :barcodeValue")
    suspend fun deleteBarcode(barcodeValue: String)


    @Query("SELECT 0 uid," +
            "    SUBSTR(barcode.barcode, 1, LENGTH(barcode.barcode) - 4) AS cn,\n" +
            "    COUNT(SUBSTR(barcode.barcode, 1, LENGTH(barcode.barcode) - 4)) AS boxes,\n" +
            "    cn.challan\n" +
            "FROM \n" +
            "barcode \n" +
            "JOIN \n" +
            "cn ON SUBSTR(barcode.barcode, 1, LENGTH(barcode.barcode) - 4) = cn.cn\n" +
            "GROUP BY     cn, cn.challan \n" +
            "UNION all\n" +
            "SELECT 0 uid,cn ,boxes ,challan from ManualAvr")
    suspend fun getcnlischallan():List<ManualAvr>


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


 @Query("select count() from barcode where SUBSTR(barcode.barcode, 1, LENGTH(barcode.barcode) - 4)=:cn")
 suspend fun getcurrengr(cn : String):Int


 //@Query("select count() from barcode where SUBSTR(barcode.barcode, 1, LENGTH(barcode.barcode) - 4)=:barcode1")
 @Query("delete from barcode where SUBSTR(barcode.barcode, 1, LENGTH(barcode.barcode) - 4)  IN (:selectedbarcode)")
 suspend fun deleteuncheckdata(selectedbarcode : List<String>):Int

  @Query("SELECT barcode " +
          "FROM barcode " +
          "WHERE " +
          "    SUBSTR(barcode, 1, LENGTH(barcode) - 4) = :cn " +
          "    AND CAST(SUBSTR(barcode, LENGTH(barcode) - 3, 4) AS INTEGER) > :box order by barcode")
   suspend fun Extrascan(cn : String,box : Int): List<String>

    @Query("DELETE FROM Barcode WHERE uid NOT IN (SELECT MIN(uid) FROM Barcode GROUP BY barcode)")
    suspend fun deleteDuplicateBarcodes()


  /*  @Query("SELECT barcode " +
            "FROM barcode ")
    suspend fun Extrascan(cn : String,box : Int): List<String>*/


}