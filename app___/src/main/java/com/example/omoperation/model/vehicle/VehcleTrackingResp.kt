package com.example.omoperation.model.vehicle

import com.example.omoperation.model.vehicle.LoryDetail

data class VehcleTrackingResp(
    val error: String,
    val lory_detail: List<LoryDetail>
)