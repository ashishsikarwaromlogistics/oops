package com.example.omoperation.model.eway

import com.example.omoperation.model.eway.CneList

data class Detail(
    val cne_Pin: Int,
    val cne_addr: String,
    val cnr_code: String,
    val ewb_no: String,
    val ewb_date: String,
    val cne_gstn: String,
    val cnr_Pin: Int,
    val cnr_addr: String,
    val cnr_gstn: String,
    val inv_date: String,
    val inv_grs_value: Double,
    val inv_net_value: Double,
    val inv_no: String,
    val inv_qty: Int,
    val cneList: ArrayList<CneList>,
    val cnrList: ArrayList<CnrList>,
    val productDesc: String
)