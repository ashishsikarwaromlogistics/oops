package com.example.omoperation.model.stickervalid

data class StickerValidResp(
    val box: String,
    val cn_no: String,
    val cn_pkg: String,
    val error: String,
    val from: String,
    val pkg_serial: String,
    val box_no: String,
    val box_status: String,
    val to: String
)