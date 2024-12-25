package com.example.omoperation.model.rewarehouse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReDetailResp {

    @SerializedName("detail")
    @Expose
    private List<Detail> detail;
    @SerializedName("error")
    @Expose
    private String error;

    public String getCn_date() {
        return cn_date;
    }

    public void setCn_date(String cn_date) {
        this.cn_date = cn_date;
    }

    @SerializedName("cn_date")
    @Expose
    private String cn_date;

    public List<Detail> getDetail() {
        return detail;
    }

    public void setDetail(List<Detail> detail) {
        this.detail = detail;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

}
