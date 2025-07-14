package com.example.omoperation.model

data class CommonRespS(
    val error: String,
    val pkg: String,
    val city: String,
    val trans_no: String,
    val lorry_no: String,
    val date: String,
    val response: String,
    val msg: String,
    val count: String,
    val cn_wt: String,
    var cn_date: String,
    var mcn: String,
    var cn_no: String,
    var tally_flag: String
)