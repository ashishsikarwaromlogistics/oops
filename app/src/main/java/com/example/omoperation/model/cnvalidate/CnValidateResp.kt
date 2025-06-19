package com.example.omoperation.model.cnvalidate

data class CnValidateResp(
    val challan: String,
    val cn_dest: String,
    val error: String,
    val pkg: String,
    val city: String,
    var cn_date: String,
    var cn_wt: String,
    var cn_no: String,
    var mcn: String,
    val response: String
)