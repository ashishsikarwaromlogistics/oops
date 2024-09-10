package com.example.omoperation.model.dispactch

import com.example.omoperation.model.dispactch.CnEnquiryDetail

data class DispatchResp(
    val cn_enquiry_detail: List<CnEnquiryDetail>,
    val error: String
)