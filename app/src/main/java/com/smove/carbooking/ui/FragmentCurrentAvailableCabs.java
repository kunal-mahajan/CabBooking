package com.smove.carbooking.ui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.maps.android.clustering.ClusterItem;
import com.smove.carbooking.AppConstants;
import com.smove.carbooking.api.ApiUtils;
import com.smove.carbooking.pojo.LiveCabLocation;
import com.smove.carbooking.pojo.ResponseLiveData;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Kunal.Mahajan on 8/4/2018.
 */

public class FragmentCurrentAvailableCabs extends Fragment {
    private List<LiveCabLocation> cabDataList;
    private List<LiveCabLocation> avlCabDataList = new ArrayList<>();

    private GoogleMapClusterRenderer mapCluster;
    private CheckBox cbShowAvailableOnly;


    public FragmentCurrentAvailableCabs() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout ll = CabBookingUIUtils.getLinearLayout(getActivity(), true);
        mapCluster = new GoogleMapClusterRenderer(getActivity(), new GoogleMapClusterRenderer.DescriptorCallback() {
            @Override
            public float getDescriptorColor(ClusterItem item) {
                return CabBookingUIUtils.getDescriptorColor(((LiveCabLocation) item).isIsOnTrip());
            }

            @Override
            public void infoWindowClicked(ClusterItem ci) {
                if (!((LiveCabLocation) ci).isIsOnTrip())
                    return;
                CabBookingUIUtils.makeCall(getActivity());
            }

            @Override
            public List<? extends ClusterItem> getClusters() {
                return cbShowAvailableOnly.isChecked() ? avlCabDataList : cabDataList;
            }
        });
        addCheckBoxShowAvailableOnly(ll);
        mapCluster.addGoogleMapVew(ll, savedInstanceState);
        loadCabs();
        return ll;
    }

    private void addCheckBoxShowAvailableOnly(LinearLayout ll) {
        cbShowAvailableOnly = new CheckBox(getContext());
        LinearLayout.LayoutParams rlp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cbShowAvailableOnly.setText("Show available cabs only");
        cbShowAvailableOnly.setLayoutParams(rlp);
        ll.addView(cbShowAvailableOnly);
        cbShowAvailableOnly.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mapCluster.showMarkers();
            }
        });
        cbShowAvailableOnly.setEnabled(false);
    }


    private void loadCabs() {

        ProgressDialog dialog = ProgressDialog.show(getActivity(), "", "Loading. Please wait...", true);
        dialog.show();
        ApiUtils.getAPIService().getLiveCabs().enqueue(new Callback<ResponseLiveData>() {
            @Override
            public void onResponse(Call<ResponseLiveData> call, Response<ResponseLiveData> response) {
                if (response.isSuccessful()) {
                    cabDataList = response.body().getData();
                    for (LiveCabLocation c : cabDataList)
                        if (c.isIsOnTrip()) avlCabDataList.add(c);

                    mapCluster.showMarkers();
                    cbShowAvailableOnly.setEnabled(true);

                } else {
                    Toast.makeText(getActivity(), "Failed to retrieve data", Toast.LENGTH_SHORT).show();
                    cbShowAvailableOnly.setEnabled(false);
                    networkFailureCallRetry();

                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<ResponseLiveData> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(getActivity(), "Internet connection problem", Toast.LENGTH_SHORT).show();
                cbShowAvailableOnly.setEnabled(false);
                networkFailureCallRetry();
            }
        });
    }

    private void networkFailureCallRetry() {
        CabBookingUIUtils.showNetowrkErrorDialog(getActivity(), new Runnable() {
            @Override
            public void run() {
                loadCabs();
            }
        });
    }
}
