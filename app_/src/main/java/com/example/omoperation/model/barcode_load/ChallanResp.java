package com.example.omoperation.model.barcode_load;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChallanResp {
    @SerializedName("error")
    @Expose
    private String error;
    @SerializedName("response")
    @Expose
    private String response;

    public String getError() {
        return error;
    }


    public String getResponse() {
        return response;
    }

}
