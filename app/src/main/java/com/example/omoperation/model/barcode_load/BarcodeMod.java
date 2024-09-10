package com.example.omoperation.model.barcode_load;


import com.example.omoperation.model.avr.Barcodelist;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BarcodeMod {

    @SerializedName("source")
    @Expose
    private String source;
    @SerializedName("destination")
    @Expose
    private String destination;
    @SerializedName("lorry_no")
    @Expose
    private String lorryNo;
    @SerializedName("seal_no")
    @Expose
    private String sealNo;
    @SerializedName("driver_name")
    @Expose
    private String driverName;
    @SerializedName("driver_mob")
    @Expose
    private String driverMob;
    @SerializedName("touchingFlg")
    @Expose
    private String touchingFlg;
    @SerializedName("weight")
    @Expose
    private String weight;
    @SerializedName("emp")
    @Expose
    private String emp;
    @SerializedName("bcode")
    @Expose
    private String bcode;
    @SerializedName("barcodelist")
    @Expose
    private List<Barcodelist> barcodelist;
    @SerializedName("cnlist")
    @Expose
    private List<Cn> cnlist;
    @SerializedName("imei")
    @Expose
    private String imei;
    @SerializedName("doc_remarks")
    @Expose
    private String docRemarks;
    @SerializedName("is_doc")
    @Expose
    private String isDoc;


    @SerializedName("loading_plan")
    @Expose
    private String loadingPlan;

    @SerializedName("airbag")
    @Expose
    private String airbag;




    public String getAirbag() {
        return airbag;
    }

    public void setAirbag(String airbag) {
        this.airbag = airbag;
    }

    public String getSheetbelt() {
        return sheetbelt;
    }

    public void setSheetbelt(String sheetbelt) {
        this.sheetbelt = sheetbelt;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getLashing() {
        return lashing;
    }

    public void setLashing(String lashing) {
        this.lashing = lashing;
    }

    public String getTouchingBranch() {
        return touchingBranch;
    }

    public String getRemarks() {
        return remarks;
    }

    @SerializedName("sheetbelt")
    @Expose
    private String sheetbelt;
    @SerializedName("cargo")
    @Expose
    private String cargo;
    @SerializedName("lashing")
    @Expose
    private String lashing;




    public void setTouchingBranch(String touchingBranch) {
        this.touchingBranch = touchingBranch;
    }

    @SerializedName("touchingBranch")
    @Expose
    private String touchingBranch;

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @SerializedName("remarks")
    @Expose
    private String remarks;

    public String getOda_station_code() {
        return oda_station_code;
    }

    public void setOda_station_code(String oda_station_code) {
        this.oda_station_code = oda_station_code;
    }

    @SerializedName("oda_station_code")
    @Expose
    private String oda_station_code;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getLorryNo() {
        return lorryNo;
    }

    public void setLorryNo(String lorryNo) {
        this.lorryNo = lorryNo;
    }

    public String getSealNo() {
        return sealNo;
    }

    public void setSealNo(String sealNo) {
        this.sealNo = sealNo;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverMob() {
        return driverMob;
    }

    public void setDriverMob(String driverMob) {
        this.driverMob = driverMob;
    }

    public String getTouchingFlg() {
        return touchingFlg;
    }

    public void setTouchingFlg(String touchingFlg) {
        this.touchingFlg = touchingFlg;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getEmp() {
        return emp;
    }

    public void setEmp(String emp) {
        this.emp = emp;
    }

    public String getBcode() {
        return bcode;
    }

    public void setBcode(String bcode) {
        this.bcode = bcode;
    }

    public List<Barcodelist> getBarcodelist() {
        return barcodelist;
    }

    public void setBarcodelist(List<Barcodelist> barcodelist) {
        this.barcodelist = barcodelist;
    }

    public List<Cn> getCnlist() {
        return cnlist;
    }

    public void setCnlist(List<Cn> cnlist) {
        this.cnlist = cnlist;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getDocRemarks() {
        return docRemarks;
    }

    public void setDocRemarks(String docRemarks) {
        this.docRemarks = docRemarks;
    }

    public String getIsDoc() {
        return isDoc;
    }

    public void setIsDoc(String isDoc) {
        this.isDoc = isDoc;
    }

    public String getLoadingPlan() {
        return loadingPlan;
    }

    public void setLoadingPlan(String loadingPlan) {
        this.loadingPlan = loadingPlan;
    }

}