package com.example.omoperation.model.checkcn

data class CheckCnResp(
    val challan: String,
    val cn_dest: String,
    val error: String,
    val pkg: String,
      val city: String,
    val response: String,
    var cn_date: String
)