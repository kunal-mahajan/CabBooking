package com.smove.carbooking.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseLiveData {

    @SerializedName("data")
    private List<LiveCabLocation> data;

    public List<LiveCabLocation> getData() {
        return data;
    }

}