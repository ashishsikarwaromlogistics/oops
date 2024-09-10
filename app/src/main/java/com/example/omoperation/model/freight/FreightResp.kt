package com.example.omoperation.model.freight

import com.example.omoperation.model.freight.CnEnquiryFright

data class FreightResp(
    val cn_enquiry_fright: List<CnEnquiryFright>,
    val error: String
)