package com.example.omoperation.room.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Branches(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name = "BRANCH_BRANCH_CODE") val BRANCH_BRANCH_CODE: String,
    @ColumnInfo(name = "BRANCH_BRANCH_NAME") val BRANCH_BRANCH_NAME: String,
    @ColumnInfo(name = "CITY_CITY_NAME") val CITY_CITY_NAME: String,
    @ColumnInfo(name = "BRANCH_STATE") val BRANCH_STATE: String,
    @ColumnInfo(name = "BRANCH_BRANCH_PHONE") val BRANCH_BRANCH_PHONE: String,
        @ColumnInfo(name = "BRANCH_EMAIL") val BRANCH_EMAIL: String,
    @ColumnInfo(name = "BRANCH_CONTACT_PERSON") val BRANCH_CONTACT_PERSON: String,
    @ColumnInfo(name = "BRANCH_LATI") val BRANCH_LATI: String?,
    @ColumnInfo(name = "BRANCH_LONG") val BRANCH_LONG: String?
)