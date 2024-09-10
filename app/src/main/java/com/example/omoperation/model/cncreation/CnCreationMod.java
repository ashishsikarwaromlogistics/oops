package com.example.omoperation.model.cncreation;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import javax.inject.Inject;

public class CnCreationMod  {
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("bcode")
    @Expose
    private String bcode;
    @SerializedName("empcode")
    @Expose
    private String empcode;
    @SerializedName("invoices")
    @Expose
    private List<Invoice> invoices;


public String getManual_no() {
        return manual_no;
    }

    public void setManual_no(String manual_no) {
        this.manual_no = manual_no;
    }

    public String getManual_date() {
        return manual_date;
    }

    public void setManual_date(String manual_date) {
        this.manual_date = manual_date;
    }

    public String getFrom_branch_code() {
        return from_branch_code;
    }

    public void setFrom_branch_code(String from_branch_code) {
        this.from_branch_code = from_branch_code;
    }

    public String getTo_branch_code() {
        return to_branch_code;
    }

    public void setTo_branch_code(String to_branch_code) {
        this.to_branch_code = to_branch_code;
    }

    public String getCnrCode() {
        return cnrCode;
    }

    public void setCnrCode(String cnrCode) {
        this.cnrCode = cnrCode;
    }

    public String getCneeCode() {
        return cneeCode;
    }

    public void setCneeCode(String cneeCode) {
        this.cneeCode = cneeCode;
    }

    public String getCnr() {
        return cnr;
    }

    public void setCnr(String cnr) {
        this.cnr = cnr;
    }

    public String getCnee() {
        return cnee;
    }

    public void setCnee(String cnee) {
        this.cnee = cnee;
    }

    public String getTran_mode() {
        return tran_mode;
    }

    public void setTran_mode(String tran_mode) {
        this.tran_mode = tran_mode;
    }

    public String getFreight_mode() {
        return freight_mode;
    }

    public void setFreight_mode(String freight_mode) {
        this.freight_mode = freight_mode;
    }

    public String getBill_part_code() {
        return bill_part_code;
    }

    public void setBill_part_code(String bill_part_code) {
        this.bill_part_code = bill_part_code;
    }

    public String getBill_part_name() {
        return bill_part_name;
    }

    public void setBill_part_name(String bill_part_name) {
        this.bill_part_name = bill_part_name;
    }

    public String getLoad_type() {
        return load_type;
    }

    public void setLoad_type(String load_type) {
        this.load_type = load_type;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @SerializedName("manual_no")
    @Expose
    private String manual_no;
    @SerializedName("manual_date")
    @Expose
    private String manual_date;
    @SerializedName("from_branch_code")
    @Expose
    private String from_branch_code;
    @SerializedName("to_branch_code")
    @Expose
    private String to_branch_code;
    @SerializedName("cnrCode")
    @Expose
    private String cnrCode;
    @SerializedName("cneeCode")
    @Expose
    private String cneeCode;
    @SerializedName("cnr")
    @Expose
    private String cnr;
    @SerializedName("cnee")
    @Expose
    private String cnee;
    @SerializedName("tran_mode")
    @Expose
    private String tran_mode;
    @SerializedName("freight_mode")
    @Expose
    private String freight_mode;

    @SerializedName("bill_part_code")
    @Expose
    private String bill_part_code;

    @SerializedName("bill_part_name")
    @Expose
    private String bill_part_name;

    @SerializedName("load_type")
    @Expose
    private String load_type;

    @SerializedName("remarks")
    @Expose
    private String remarks;


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBcode() {
        return bcode;
    }

    public void setBcode(String bcode) {
        this.bcode = bcode;
    }

    public String getEmpcode() {
        return empcode;
    }

    public void setEmpcode(String empcode) {
        this.empcode = empcode;
    }

    public List<Invoice> getInvoices() {
        return invoices;
    }

    public void setInvoices(List<Invoice> invoices) {
        this.invoices = invoices;
    }

}
