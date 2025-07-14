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

    public String getCLIENT_BOX_NO() {
        return CLIENT_BOX_NO;
    }

    public void setCLIENT_BOX_NO(String CLIENT_BOX_NO) {
        this.CLIENT_BOX_NO = CLIENT_BOX_NO;
    }

    @SerializedName("CLIENT_BOX_NO")
    @Expose
    private String CLIENT_BOX_NO;

    public String getMissingpkg() {
        return missingpkg;
    }

    public void setMissingpkg(String missingpkg) {
        this.missingpkg = missingpkg;
    }

    @SerializedName("missingpkg")
    @Expose
    private String missingpkg;

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
