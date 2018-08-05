package com.smove.carbooking.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.maps.android.clustering.ClusterItem;
import com.smove.carbooking.pojo.DropoffLocationsItem;

import java.util.List;

/**
 * Created by Kunal.Mahajan on 8/5/2018.
 */

public class ActivityDropOffLocations extends AppCompatActivity {

    private List<DropoffLocationsItem> dropoffLocations;
    public static final String KEY_DROP_OFF_LOCATIONS = ActivityDropOffLocations.class + "DROP_OFF_LOCATION";
    private GoogleMapClusterRenderer mapCluster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dropoffLocations = (List<DropoffLocationsItem>) getIntent().getSerializableExtra(KEY_DROP_OFF_LOCATIONS);
        LinearLayout ll = CabBookingUIUtils.getLinearLayout(this, true);
        setContentView(ll, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        GoogleMapClusterRenderer utils = new GoogleMapClusterRenderer(this, new GoogleMapClusterRenderer.DescriptorCallback() {
            @Override
            public float getDescriptorColor(ClusterItem item) {
                return BitmapDescriptorFactory.HUE_GREEN;
            }

            @Override
            public void infoWindowClicked(ClusterItem cabData) {
                CabBookingUIUtils.makeCall(ActivityDropOffLocations.this);
            }

            @Override
            public List<? extends ClusterItem> getClusters() {
                return dropoffLocations;
            }
        });
        utils.addGoogleMapVew(ll, savedInstanceState);
        utils.showMarkers();
        setTitle("Drop off locations");
    }


}
