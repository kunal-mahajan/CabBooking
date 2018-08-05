package com.smove.carbooking.pojo;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;
import com.google.maps.android.clustering.ClusterItem;

public class LiveCabLocation implements ClusterItem {

    @SerializedName("is_on_trip")
    private boolean isOnTrip;

    @SerializedName("latitude")
    private double latitude;

    @SerializedName("id")
    private int id;

    @SerializedName("longitude")
    private double longitude;

    public boolean isIsOnTrip() {
        return isOnTrip;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public LatLng getPosition() {
        return new LatLng(latitude, longitude);
    }

    @Override
    public String getTitle() {
        return isOnTrip ? "Call to book" : "Not on trip";
    }

    @Override
    public String getSnippet() {
        return "";
    }
}