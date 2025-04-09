package com.example.omoperation.model.gatepass

  class Response{
    constructor(ACT_WT: String,
                CHLN_NO: String,
                 CN_NO: String,
                 PKG: String,
                isChecked: Boolean,

                )  {
        this.ACT_WT=ACT_WT
        this.CHLN_NO=CHLN_NO
        this.CN_NO=CN_NO
        this.PKG=PKG
        this.isChecked=isChecked

      }



    var ACT_WT: String?=null
    var CHLN_NO: String?=null
    var CN_NO: String?=null
    var PKG: String?=null
    var isChecked: Boolean?=null
}