package com.example.omoperation.model.barcode_load;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Barcode {

    @SerializedName("barcode")
    @Expose
    private String barcode;
    @SerializedName("time")
    @Expose
    private String time;

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}

