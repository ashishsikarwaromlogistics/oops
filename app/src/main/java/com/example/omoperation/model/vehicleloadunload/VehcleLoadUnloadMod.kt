package com.example.omoperation.model.vehicleloadunload

data class VehcleLoadUnloadMod(
    val bcode: String,
    val challan: String,
    val empcode: String,
    val images: List<String>,
    val remarks: String,
    val status: String,
    val vehicle: String
)