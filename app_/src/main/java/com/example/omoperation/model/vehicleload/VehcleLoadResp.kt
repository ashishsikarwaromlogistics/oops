package com.example.omoperation.model.vehicleload

data class VehcleLoadResp(
    val error: String,
    val lorry: String,
    val response: List<String>,
    val vlu_no: String?
)