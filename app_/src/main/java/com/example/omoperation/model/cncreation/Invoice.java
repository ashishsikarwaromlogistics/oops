package com.example.omoperation.model.cncreation;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Invoice {

    @SerializedName("INV_NO")
    @Expose
    private String invNo;
    @SerializedName("INV_DT")
    @Expose
    private String invDt;
    @SerializedName("PT_NO")
    @Expose
    private String ptNo;
    @SerializedName("PCKGS")
    @Expose
    private String pckgs;
    @SerializedName("QTY")
    @Expose
    private String qty;
    @SerializedName("PCK_TYPE")
    @Expose
    private String pckType;
    @SerializedName("NET_VAL")
    @Expose
    private String netVal;
    @SerializedName("GROSS_VAL")
    @Expose
    private String grossVal;
    @SerializedName("ACT_WT")
    @Expose
    private String actWt;
    @SerializedName("CH_WT")
    @Expose
    private String chWt;
    @SerializedName("EWAY_BILL_NO")
    @Expose
    private String ewayBillNo;
    @SerializedName("EWAY_BILL_DATE")
    @Expose
    private String ewayBillDate;
    @SerializedName("DESC")
    @Expose
    private String desc;
    @SerializedName("l")
    @Expose
    private String l;
    @SerializedName("w")
    @Expose
    private String w;
    @SerializedName("h")
    @Expose
    private String h;
    @SerializedName("unit")
    @Expose
    private String unit;
    @SerializedName("CFT_r")
    @Expose
    private String cFTR;
    @SerializedName("cft_wt")
    @Expose
    private String cftWt;

    public String getInvNo() {
        return invNo;
    }

    public void setInvNo(String invNo) {
        this.invNo = invNo;
    }

    public String getInvDt() {
        return invDt;
    }

    public void setInvDt(String invDt) {
        this.invDt = invDt;
    }

    public String getPtNo() {
        return ptNo;
    }

    public void setPtNo(String ptNo) {
        this.ptNo = ptNo;
    }

    public String getPckgs() {
        return pckgs;
    }

    public void setPckgs(String pckgs) {
        this.pckgs = pckgs;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getPckType() {
        return pckType;
    }

    public void setPckType(String pckType) {
        this.pckType = pckType;
    }

    public String getNetVal() {
        return netVal;
    }

    public void setNetVal(String netVal) {
        this.netVal = netVal;
    }

    public String getGrossVal() {
        return grossVal;
    }

    public void setGrossVal(String grossVal) {
        this.grossVal = grossVal;
    }

    public String getActWt() {
        return actWt;
    }

    public void setActWt(String actWt) {
        this.actWt = actWt;
    }

    public String getChWt() {
        return chWt;
    }

    public void setChWt(String chWt) {
        this.chWt = chWt;
    }

    public String getEwayBillNo() {
        return ewayBillNo;
    }

    public void setEwayBillNo(String ewayBillNo) {
        this.ewayBillNo = ewayBillNo;
    }

    public String getEwayBillDate() {
        return ewayBillDate;
    }

    public void setEwayBillDate(String ewayBillDate) {
        this.ewayBillDate = ewayBillDate;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getL() {
        return l;
    }

    public void setL(String l) {
        this.l = l;
    }

    public String getW() {
        return w;
    }

    public void setW(String w) {
        this.w = w;
    }

    public String getH() {
        return h;
    }

    public void setH(String h) {
        this.h = h;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getCFTR() {
        return cFTR;
    }

    public void setCFTR(String cFTR) {
        this.cFTR = cFTR;
    }

    public String getCftWt() {
        return cftWt;
    }

    public void setCftWt(String cftWt) {
        this.cftWt = cftWt;
    }

}
