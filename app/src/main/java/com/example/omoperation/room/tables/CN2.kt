package com.example.omoperation.room.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class CN2(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name = "BARCODE") val box: String,
    @ColumnInfo(name = "CN_No") val cn: String,

)