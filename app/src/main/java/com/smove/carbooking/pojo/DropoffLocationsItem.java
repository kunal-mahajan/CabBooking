package com.smove.carbooking.pojo;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;
import com.google.maps.android.clustering.ClusterItem;

import java.io.Serializable;
import java.util.List;

public class DropoffLocationsItem implements ClusterItem, Serializable {

    @SerializedName("location")
    private List<Double> location;

    @SerializedName("id")
    private int id;

    public int getId() {
        return id;
    }

    @Override
    public LatLng getPosition() {
        if (location.size() > 1)
            return new LatLng(location.get(0), location.get(1));
        else
            return null;
    }

    @Override
    public String getTitle() {
        return "CAB ID: "+id+", "+ "Call now for assistance";
    }

    @Override
    public String getSnippet() {
        return "";
    }
}