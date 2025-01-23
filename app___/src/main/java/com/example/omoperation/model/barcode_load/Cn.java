package com.example.omoperation.model.barcode_load;

import androidx.room.Entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Cn {

    @SerializedName("CN_No")
    @Expose
    private String cNNo;
    @SerializedName("BARCODE")
    @Expose
    private String barcode;

    public String getCNNo() {
        return cNNo;
    }

    public void setCNNo(String cNNo) {
        this.cNNo = cNNo;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

}
