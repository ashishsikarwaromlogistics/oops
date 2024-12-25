package com.example.omoperation.model.gatepassin

data class GatePassInResp(
    val error: String,
    val message: String,
    val response: List<Response>
)