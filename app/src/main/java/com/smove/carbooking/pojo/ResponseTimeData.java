package com.smove.carbooking.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseTimeData {

    @SerializedName("data")
    private List<CabData> data;

    public List<CabData> getData() {
        return data;
    }

}