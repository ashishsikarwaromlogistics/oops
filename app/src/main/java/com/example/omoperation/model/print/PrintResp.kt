package com.omlogistics.deepak.omlogistics.model.print

import com.example.omoperation.model.print.CnEnquiry

data class PrintResp(
    val cn_enquiry: List<CnEnquiry>,
    val error: String
)