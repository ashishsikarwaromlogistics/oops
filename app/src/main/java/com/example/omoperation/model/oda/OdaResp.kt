package com.example.omoperation.model.oda

data class OdaResp(
    val emp_enquiry: List<EmpEnquiry>,
    val error: String
)