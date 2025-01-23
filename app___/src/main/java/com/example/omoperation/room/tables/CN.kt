package com.example.omoperation.room.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class CN(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name = "box") val box: String,
    @ColumnInfo(name = "city") val city: String,
    @ColumnInfo(name = "cn") val cn: String,
    @ColumnInfo(name = "challan") val challan: String,
    @ColumnInfo(name = "weight") val weight: String
)