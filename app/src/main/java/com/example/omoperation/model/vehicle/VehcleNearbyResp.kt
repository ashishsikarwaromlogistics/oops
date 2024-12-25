package com.example.omoperation.model.vehicle

import com.example.omoperation.model.vehicle.PinNo

data class VehcleNearbyResp(
    val error: String,
    val pin_no: List<PinNo>
)