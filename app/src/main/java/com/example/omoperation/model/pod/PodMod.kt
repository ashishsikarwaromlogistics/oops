package com.example.omoperation.model.pod

data class PodMod(
    val cnno: String,
    val userid: String,
    val bcode: String,
    val imageList: List<String>,
    val lat: Double,
    val lng: Double,
    val address: String
)