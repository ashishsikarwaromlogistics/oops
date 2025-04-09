package com.example.omoperation.model.gatepassin
class Response{



    var CN_ACT_WT: String?=null
    var CN_NO: String?=null
    var CHALLAN_NO: String?=null
    var CN_PKG: String?=null
    var LORRY_NO: String?=null
    var isChecked: Boolean?=null

    constructor(CN_ACT_WT: String,
                CN_NO: String,
                CHALLAN_NO: String,
                CN_PKG: String,
                isChecked: Boolean,

                )  {
        this.CN_ACT_WT=CN_ACT_WT
        this.CN_NO=CN_NO
        this.CHALLAN_NO=CHALLAN_NO
        this.CN_PKG=CN_PKG
        this.isChecked=isChecked

    }


}