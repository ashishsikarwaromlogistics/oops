package com.example.omoperation.model.avr

import com.google.gson.annotations.SerializedName

class AvrMod{
    var avr_seal: String?=null
    var barcodelist: List<Barcodelist>?=null
    var bcode: String?=null
    var cnlist: List<Cnlist>?=null
    var emp: String?=null
    var gate_no: String?=null
    var imei: String?=null
    var missing_status: Any?=null
    var remarks: Any?=null
    var airbag: String?=null
    var sheetbelt: String?=null
    var cargo: String?=null
    var lashingbelt: String?=null
    var status: String?=null
    var doc_remarks: String?=null
    var vehicle_no: String?=null
    @SerializedName("is_doc")
    var doc: String?=null
}