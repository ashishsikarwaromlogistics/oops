package com.example.omoperation.room.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Employees (
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name = "BRANCH_NAME") val BRANCH_NAME: String,
    @ColumnInfo(name = "EMP_FIRST_NAME") val EMP_FIRST_NAME: String,
    @ColumnInfo(name = "EMP_EMP_CODE") val EMP_EMP_CODE: String,
    @ColumnInfo(name = "EMP_EMP_TITLE") val EMP_EMP_TITLE: String,
    @ColumnInfo(name = "EMP_PHONE_NO") val EMP_PHONE_NO: String,
    @ColumnInfo(name = "EMP_EMAIL_ID") val EMP_EMAIL_ID: String,
    @ColumnInfo(name = "DEPT_NAME") val DEPT_NAME: String?,
    @ColumnInfo(name = "DESIG_NAME") val DESIG_NAME: String?
)
//{  "EMP_PER_PHONE_NO":null,"EMP_LANDLINE_NO":null,"DEPT_NAME":"INFORMATION TECHNOLOGY","DESIG_NAME":"ASSISTANT","": }