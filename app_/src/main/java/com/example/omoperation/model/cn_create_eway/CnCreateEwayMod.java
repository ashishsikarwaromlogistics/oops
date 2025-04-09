package com.example.omoperation.model.cn_create_eway;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CnCreateEwayMod {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("LOGIN_BR")
    @Expose
    private String loginBr;
    @SerializedName("ENTER_BY")
    @Expose
    private String enterBy;
    @SerializedName("CNR_CODE")
    @Expose
    private String cnrCode;
    @SerializedName("CNR_GSTN")
    @Expose
    private String cnrGstn;
    @SerializedName("CNEE_PINCODE")
    @Expose
    private String cneePincode;
    @SerializedName("CNEE_ADDRESS")
    @Expose
    private String cneeAddress;
    @SerializedName("PALCE_OF_SUPPLY")
    @Expose
    private String palceOfSupply;
    @SerializedName("BOOKING_MODE")
    @Expose
    private String bookingMode;
    @SerializedName("BILLING_GST_NO")
    @Expose
    private String billingGstNo;
    @SerializedName("INVOICE_NO")
    @Expose
    private String invoiceNo;
    @SerializedName("INVOICE_DATE")
    @Expose
    private String invoiceDate;
    @SerializedName("NO_OF_PKG")
    @Expose
    private String noOfPkg;
    @SerializedName("GROSS_VALUE")
    @Expose
    private String grossValue;
    @SerializedName("NET_VALUES")
    @Expose
    private String netValues;
    @SerializedName("QTY")
    @Expose
    private String qty;
    @SerializedName("ACTUAL_WEIGHT")
    @Expose
    private String actualWeight;
    @SerializedName("CHRG_WEIGHT")
    @Expose
    private String chrgWeight;
    @SerializedName("CFT_UNIT")
    @Expose
    private String cftUnit;
    @SerializedName("ITEM_DESCRIPTION")
    @Expose
    private String itemDescription;
    @SerializedName("EWB_NO")
    @Expose
    private String ewbNo;
    @SerializedName("EWB_DATE")
    @Expose
    private String ewbDate;

    @SerializedName("billing_party")
    @Expose
    private String billing_party;
@SerializedName("CNE_CODE")
    @Expose
    private String CNE_CODE;
@SerializedName("DEL_INST")
    @Expose
    private String DEL_INST;
@SerializedName("FREIGHT_MODE")
    @Expose
    private String FREIGHT_MODE;
@SerializedName("PKG_TYPE")
    @Expose
    private String PKG_TYPE;
@SerializedName("PART_NO")
    @Expose
    private String PART_NO;
@SerializedName("LEN")
    @Expose
    private String LEN;
    @SerializedName("WIDTH")
    @Expose
    private String WIDTH;
    @SerializedName("HEIGHT")
    @Expose
    private String HEIGHT;

    @SerializedName("CFT_RATE")
    @Expose
    private String CFT_RATE;
    @SerializedName("MANNUAL_NO")
    @Expose
    private String MANNUAL_NO;
    @SerializedName("PO_NO")
    @Expose
    private String PO_NO;
    @SerializedName("remarks")
    @Expose
    private String remarks;
    public void setPO_NO(String PO_NO) {
        this.PO_NO = PO_NO;
    }



    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }



    public String getCNE_CODE() {
        return CNE_CODE;
    }

    public String getDEL_INST() {
        return DEL_INST;
    }

    public String getFREIGHT_MODE() {
        return FREIGHT_MODE;
    }

    public String getPKG_TYPE() {
        return PKG_TYPE;
    }

    public String getPART_NO() {
        return PART_NO;
    }

    public String getLEN() {
        return LEN;
    }

    public String getWIDTH() {
        return WIDTH;
    }

    public String getHEIGHT() {
        return HEIGHT;
    }




    public void setCFT_RATE(String CFT_RATE) {
        this.CFT_RATE = CFT_RATE;
    }

    public void setMANNUAL_NO(String MANNUAL_NO) {
        this.MANNUAL_NO = MANNUAL_NO;
    }


    public void setCNE_CODE(String CNE_CODE) {
        this.CNE_CODE = CNE_CODE;
    }

    public void setDEL_INST(String DEL_INST) {
        this.DEL_INST = DEL_INST;
    }

    public void setFREIGHT_MODE(String FREIGHT_MODE) {
        this.FREIGHT_MODE = FREIGHT_MODE;
    }

    public void setPKG_TYPE(String PKG_TYPE) {
        this.PKG_TYPE = PKG_TYPE;
    }

    public void setPART_NO(String PART_NO) {
        this.PART_NO = PART_NO;
    }

    public void setLEN(String LEN) {
        this.LEN = LEN;
    }

    public void setWIDTH(String WIDTH) {
        this.WIDTH = WIDTH;
    }

    public void setHEIGHT(String HEIGHT) {
        this.HEIGHT = HEIGHT;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLoginBr() {
        return loginBr;
    }

    public void setLoginBr(String loginBr) {
        this.loginBr = loginBr;
    }

    public String getEnterBy() {
        return enterBy;
    }

    public void setEnterBy(String enterBy) {
        this.enterBy = enterBy;
    }

    public String getCnrCode() {
        return cnrCode;
    }

    public void setCnrCode(String cnrCode) {
        this.cnrCode = cnrCode;
    }

    public String getCnrGstn() {
        return cnrGstn;
    }

    public void setCnrGstn(String cnrGstn) {
        this.cnrGstn = cnrGstn;
    }

    public String getCneePincode() {
        return cneePincode;
    }

    public void setCneePincode(String cneePincode) {
        this.cneePincode = cneePincode;
    }

    public String getCneeAddress() {
        return cneeAddress;
    }

    public void setCneeAddress(String cneeAddress) {
        this.cneeAddress = cneeAddress;
    }

    public String getPalceOfSupply() {
        return palceOfSupply;
    }

    public void setPalceOfSupply(String palceOfSupply) {
        this.palceOfSupply = palceOfSupply;
    }

    public String getBookingMode() {
        return bookingMode;
    }

    public void setBookingMode(String bookingMode) {
        this.bookingMode = bookingMode;
    }

    public String getBillingGstNo() {
        return billingGstNo;
    }

    public void setBillingGstNo(String billingGstNo) {
        this.billingGstNo = billingGstNo;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getNoOfPkg() {
        return noOfPkg;
    }

    public void setNoOfPkg(String noOfPkg) {
        this.noOfPkg = noOfPkg;
    }

    public String getGrossValue() {
        return grossValue;
    }

    public void setGrossValue(String grossValue) {
        this.grossValue = grossValue;
    }

    public String getNetValues() {
        return netValues;
    }

    public void setNetValues(String netValues) {
        this.netValues = netValues;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getActualWeight() {
        return actualWeight;
    }

    public void setActualWeight(String actualWeight) {
        this.actualWeight = actualWeight;
    }

    public String getChrgWeight() {
        return chrgWeight;
    }

    public void setChrgWeight(String chrgWeight) {
        this.chrgWeight = chrgWeight;
    }

    public String getCftUnit() {
        return cftUnit;
    }

    public void setCftUnit(String cftUnit) {
        this.cftUnit = cftUnit;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public String getEwbNo() {
        return ewbNo;
    }

    public void setEwbNo(String ewbNo) {
        this.ewbNo = ewbNo;
    }

    public String getEwbDate() {
        return ewbDate;
    }

    public void setEwbDate(String ewbDate) {
        this.ewbDate = ewbDate;
    }

    public String getBilling_party() {
        return billing_party;
    }

    public void setBilling_party(String billing_party) {
        this.billing_party = billing_party;
    }


}

