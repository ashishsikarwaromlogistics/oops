package com.example.omoperation.model.rewarehouse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReWarehouseMod {

    @SerializedName("bcode")
    @Expose
    private String bcode;
    @SerializedName("emp_code")
    @Expose
    private String empCode;
    @SerializedName("imei")
    @Expose
    private String imei;
    @SerializedName("barcodelist")
    @Expose
    private List<BarcodeRe> barcodelist;
    @SerializedName("cnlist")
    @Expose
    private List<CnRe> cnlist;

    public String getBcode() {
        return bcode;
    }

    public void setBcode(String bcode) {
        this.bcode = bcode;
    }

    public String getEmpCode() {
        return empCode;
    }

    public void setEmpCode(String empCode) {
        this.empCode = empCode;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public List<BarcodeRe> getBarcodelist() {
        return barcodelist;
    }

    public void setBarcodelist(List<BarcodeRe> barcodelist) {
        this.barcodelist = barcodelist;
    }

    public List<CnRe> getCnlist() {
        return cnlist;
    }

    public void setCnlist(List<CnRe> cnlist) {
        this.cnlist = cnlist;
    }

}