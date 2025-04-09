package com.example.omoperation.model.rewarehouse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CnRe {

    @SerializedName("BARCODE")
    @Expose
    private String barcode;
    @SerializedName("CN_No")
    @Expose
    private String cNNo;
    @SerializedName("REASON")
    @Expose
    private String reason;

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getCNNo() {
        return cNNo;
    }

    public void setCNNo(String cNNo) {
        this.cNNo = cNNo;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

}
