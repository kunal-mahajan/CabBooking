package com.smove.carbooking.pojo;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;
import com.google.maps.android.clustering.ClusterItem;

import java.util.ArrayList;
import java.util.List;

public class CabData implements ClusterItem {

    @SerializedName("dropoff_locations")
    private ArrayList<DropoffLocationsItem> dropoffLocations;

    @SerializedName("location")
    private List<Double> location;

    @SerializedName("id")
    private int id;

    @SerializedName("available_cars")
    private int availableCars;

    public ArrayList<DropoffLocationsItem> getDropoffLocations() {
        return dropoffLocations;
    }

    public int getId() {
        return id;
    }

    public int getAvailableCars() {
        return availableCars;
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
        return availableCars == 0 ? "No cab available" : "" + availableCars + " Cabs, " + dropoffLocations.size() + " drop off locations";
    }

    @Override
    public String getSnippet() {
        return "";
    }

}