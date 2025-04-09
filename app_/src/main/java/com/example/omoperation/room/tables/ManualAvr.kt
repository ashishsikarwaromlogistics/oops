package com.example.omoperation.room.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ManualAvr(
    @PrimaryKey(autoGenerate = true)  val uid: Int=0,
    @ColumnInfo(name = "cn") val cn: String?,
    @ColumnInfo(name = "boxes") val boxes: String?,
    @ColumnInfo(name = "challan") val challan: String?,


    )
