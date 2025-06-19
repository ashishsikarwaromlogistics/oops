package com.example.omoperation.room.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity
data class Barcode(@PrimaryKey(autoGenerate = true)  val uid: Int=0,
                   @ColumnInfo(name = "timestamp") val timestamp: String,
                   @ColumnInfo(name = "barcode") val barcode: String?,

)
{
    companion object {

        fun getCurrentTimestamp(): String {
            return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        }
    }
}