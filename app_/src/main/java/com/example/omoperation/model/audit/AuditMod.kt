package com.example.omoperation.model.audit

import com.example.omoperation.model.barcode_load.Cn

class AuditMod {
      var auditNo: String?=null
      var audit_status: String?=null //1 for pending 2 for completed
      var barcodelist: List<String>?=null  //
      var bcode: String?=null
      var cnlist: List<Cn>?=null
      var deliveredCNList: List<String>?=null
      var deliveredDocList: List<String>?=null
      var docList: List<String>?=null
      var emp: String?=null
      var image_list: List<String>?=null
  }