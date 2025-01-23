package com.example.omoperation.model.eway_dropdown

import com.example.omoperation.model.eway_dropdown.BookingMode

data class Dropdowns(
    val bookingMode: List<BookingMode>,
    val rbookingMode: List<BookingMode>,
    val del_inst: List<DelInst>,
    val freightMode: List<FreightMode>,
    val rFreightMode: List<RFreightMode>,
    val load_type: List<Load_type>,
    val pkgType: List<PkgType>,
    val rate_type: List<Rate_Type>
)