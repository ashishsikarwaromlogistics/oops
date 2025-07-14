package com.example.omoperation.room.tables

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

data class CNBoxSummary(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name = "CN_No") val cn: String,
    @ColumnInfo(name = "BARCODE") val box: String
)