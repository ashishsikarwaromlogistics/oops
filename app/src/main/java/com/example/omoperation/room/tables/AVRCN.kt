package com.example.omoperation.room.tables

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
//3301251017731
data class AVRCN(
    @PrimaryKey(autoGenerate = true) val uid: Int=0,
    @ColumnInfo(name = "cn") val cn: String?,
    @ColumnInfo(name = "boxes") val boxes: String?,
    @ColumnInfo(name = "challan") val challan: String?,
    @ColumnInfo(name = "find_box") val find_box: String?,
)