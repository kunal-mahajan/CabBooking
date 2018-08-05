package com.smove.carbooking.ui;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.widget.LinearLayout;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import java.util.Collection;
import java.util.List;

/**
 * Created by Kunal.Mahajan on 8/4/2018.
 */

public class GoogleMapClusterRenderer implements OnMapReadyCallback {
    private final Context context;
    private final DescriptorCallback callBack;
    private GoogleMap googleMap;
    private ClusterManager<ClusterItem> clusterMgr;
    private boolean isMapLoaded;

    public GoogleMapClusterRenderer(Context context, DescriptorCallback descriptorColor) {
        this.context = context;
        this.callBack = descriptorColor;
    }

    private void animateToLoadedMarkers(Collection<? extends ClusterItem> cluster) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (ClusterItem ci : cluster) {
            builder.include(ci.getPosition());
        }
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(builder.build(), CabBookingUIUtils.convertDpToPixel(30));
        googleMap.animateCamera(cu);
    }

    public void addGoogleMapVew(LinearLayout ll, Bundle savedInstanceState) {
        MapView mapView = new MapView(context);
        LinearLayout.LayoutParams rlp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        mapView.setLayoutParams(rlp);
        mapView.onCreate(savedInstanceState);
        ll.addView(mapView);
        mapView.onResume();
        mapView.getMapAsync(this);

    }

    public void clearMap() {
        clusterMgr.clearItems();
        clusterMgr.cluster();
    }

    private void loadClusters() {
        if (!isMapReady() || !isClusterAvaialbe(callBack.getClusters()))
            return;
        clusterMgr.clearItems();
        for (ClusterItem c : callBack.getClusters())
            clusterMgr.addItem(c);
        clusterMgr.cluster();
        clusterMgr.setOnClusterClickListener(cluster -> {
            animateToLoadedMarkers(cluster.getItems());
            return true;
        });

        DefaultClusterRenderer d = new DefaultClusterRenderer(context, googleMap, clusterMgr) {
            @Override
            protected void onBeforeClusterItemRendered(ClusterItem item, MarkerOptions markerOptions) {
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(callBack.getDescriptorColor(item)));
            }
        };
        clusterMgr.setRenderer(d);
    }

    private void loadLastLocation() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(provider);

        if (location != null) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate loc = CameraUpdateFactory.newLatLngZoom(latLng, 15);
            googleMap.animateCamera(loc);
        }
    }

    public void onMapReady(GoogleMap googleMap) {
        clusterMgr = new ClusterManager<>(context, googleMap);
        this.googleMap = googleMap;
        loadLastLocation();
        googleMap.setOnCameraIdleListener(clusterMgr);
        googleMap.setOnMarkerClickListener(clusterMgr);
        googleMap.setOnInfoWindowClickListener(clusterMgr);
        clusterMgr.setOnClusterItemInfoWindowClickListener(cabData -> callBack.infoWindowClicked(cabData));


        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        }

        loadClusters();
        if (!isMapLoaded)
            showDataOnMapLoad();
    }

    private void showDataOnMapLoad() {
        googleMap.setOnMapLoadedCallback(() -> {
            isMapLoaded = true;
            if (isClusterAvaialbe(callBack.getClusters()))
                animateToLoadedMarkers(callBack.getClusters());
        });
    }

    public void showMarkers() {
        loadClusters();
        if (isMapLoaded) animateToLoadedMarkers(callBack.getClusters());

    }

    public boolean isClusterAvaialbe(List clusters) {
        return !(clusters == null || clusters.size() == 0);
    }

    public boolean isMapReady() {
        return googleMap != null;
    }

    public interface DescriptorCallback {
        public float getDescriptorColor(ClusterItem item);

        void infoWindowClicked(ClusterItem cabData);

        List<? extends ClusterItem> getClusters();
    }

}
