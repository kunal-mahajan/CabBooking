package com.smove.carbooking.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.maps.android.clustering.ClusterItem;
import com.smove.carbooking.R;
import com.smove.carbooking.api.ApiUtils;
import com.smove.carbooking.pojo.CabData;
import com.smove.carbooking.pojo.ResponseTimeData;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Kunal.Mahajan on 8/4/2018.
 */

public class FragmentScheduledAvailableCabs extends Fragment {
    private long startTimeUTC = -1;
    private long endTimeUTC = -1;
    private List<CabData> cabDataList;
    private List<CabData> avlCabDataList = new ArrayList<>();

    private GoogleMapClusterRenderer mapCluster;
    private CheckBox cbShowAvailableOnly;


    public FragmentScheduledAvailableCabs() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout ll = CabBookingUIUtils.getLinearLayout(getActivity(), true);
        addSelectionButton(ll);
        mapCluster = new GoogleMapClusterRenderer(getActivity(), new GoogleMapClusterRenderer.DescriptorCallback() {
            @Override
            public float getDescriptorColor(ClusterItem item) {
                return CabBookingUIUtils.getDescriptorColor(((CabData) item).getAvailableCars() > 0);
            }

            @Override
            public void infoWindowClicked(ClusterItem ci) {
                CabData cabData = (CabData) ci;
                if (cabData.getAvailableCars() == 0) return;
                Intent intent = new Intent(getActivity(), ActivityDropOffLocations.class);
                intent.putExtra(ActivityDropOffLocations.KEY_DROP_OFF_LOCATIONS, cabData.getDropoffLocations());
                startActivity(intent);
            }

            @Override
            public List<? extends ClusterItem> getClusters() {
                return cbShowAvailableOnly.isChecked() ? avlCabDataList : cabDataList;
            }
        });
        addCheckBoxShowAvailableOnly(ll);
        mapCluster.addGoogleMapVew(ll, savedInstanceState);
        return ll;
    }

    private void addCheckBoxShowAvailableOnly(LinearLayout ll) {
        cbShowAvailableOnly = new CheckBox(getContext());
        LinearLayout.LayoutParams rlp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cbShowAvailableOnly.setText("Show available cabs only");
        cbShowAvailableOnly.setLayoutParams(rlp);
        ll.addView(cbShowAvailableOnly);
        cbShowAvailableOnly.setOnCheckedChangeListener((buttonView, isChecked) -> mapCluster.showMarkers());
        cbShowAvailableOnly.setEnabled(false);
    }

    private void addSelectionButton(LinearLayout linearLayout) {
        TextView tvStart = new TextView(getActivity());
        TextView tvEnd = new TextView(getActivity());
        LinearLayout ll = new LinearLayout(getActivity());
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        tvStart.setText(R.string.select_time);
        tvEnd.setText(R.string.select_time);

        LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.5f);
        LinearLayout llStart = CabBookingUIUtils.getLinearLayout(getActivity(), true);
        llStart.setLayoutParams(params);
        LinearLayout llEnd = CabBookingUIUtils.getLinearLayout(getActivity(), true);
        llEnd.setLayoutParams(params);
        int mar = CabBookingUIUtils.convertDpToPixel(5);
        params.setMargins(mar, mar, mar, mar);

        int pad = CabBookingUIUtils.convertDpToPixel(10);
        tvEnd.setPadding(0, pad, 0, pad);
        tvEnd.setLayoutParams(tvParams);
        tvStart.setLayoutParams(tvParams);
        tvStart.setPadding(0, pad, 0, pad);

        tvEnd.setGravity(Gravity.CENTER);
        tvStart.setGravity(Gravity.CENTER);


        GradientDrawable border = new GradientDrawable();
        border.setColor(Color.LTGRAY);
        border.setStroke(1, Color.DKGRAY);
        border.setCornerRadius(CabBookingUIUtils.convertDpToPixel(4));


        tvStart.setBackgroundDrawable(border);
        tvEnd.setBackgroundDrawable(border);

        TextView tvStartTitle = new TextView(getActivity());
        TextView tvEndTitle = new TextView(getActivity());
        tvStartTitle.setLayoutParams(tvParams);
        tvEndTitle.setLayoutParams(tvParams);
        tvStartTitle.setText("Start");
        tvEndTitle.setText("End");

        llStart.addView(tvStartTitle);
        llStart.addView(tvStart);

        llEnd.addView(tvEndTitle);
        llEnd.addView(tvEnd);

        ll.addView(llStart);
        ll.addView(llEnd);

        linearLayout.addView(ll);

        setListenersOnStartAndEnd(tvStart, tvEnd);
    }

    private void setListenersOnStartAndEnd(TextView tvStart, TextView tvEnd) {
        tvStart.setOnClickListener(v -> new DateTimePicker(getContext(), d -> {
            if (d == DateTimePicker.DEFAULT_VALUE_NOT_SELECTED) {
                startTimeUTC = -1;
                endTimeUTC = -1;
                invalidateTimeSelector(tvStart);
                invalidateTimeSelector(tvEnd);
                return;
            }

            if (endTimeUTC < startTimeUTC) {
                Toast.makeText(getActivity(), "Start time must be less than end time", Toast.LENGTH_SHORT).show();
                return;
            }

            startTimeUTC = d;
            tvStart.setText(CabBookingUIUtils.getFormatedDateTime(d));
            if (endTimeUTC > startTimeUTC) {
                loadCabs();
            } else {
                endTimeUTC = -1;
                invalidateTimeSelector(tvEnd);
                Toast.makeText(getActivity(), "Please select end time", Toast.LENGTH_SHORT).show();
            }
        }).selectDateTime());

        tvEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (startTimeUTC == -1) {
                    Toast.makeText(getActivity(), "Please select start time first", Toast.LENGTH_SHORT).show();
                    return;
                }
                new DateTimePicker(getContext(), d -> {
                    endTimeUTC = -1;

                    if (d == DateTimePicker.DEFAULT_VALUE_NOT_SELECTED) {
                        invalidateTimeSelector(tvEnd);
                        return;
                    }

                    if (startTimeUTC >= d) {
                        Toast.makeText(getActivity(), "End time must be greater the start time", Toast.LENGTH_SHORT).show();
                        invalidateTimeSelector(tvEnd);
                        return;
                    }

                    endTimeUTC = d;
                    tvEnd.setText(CabBookingUIUtils.getFormatedDateTime(d));
                    loadCabs();
                }).selectDateTime();
            }
        });
    }

    private void invalidateTimeSelector(TextView tv) {
        tv.setText(R.string.select_time);
        mapCluster.clearMap();
    }


    private void loadCabs() {

        ProgressDialog dialog = ProgressDialog.show(getActivity(), "", "Loading. Please wait...", true);
        dialog.show();
        ApiUtils.getAPIService().getTimedCabs(startTimeUTC / 1000, endTimeUTC / 1000).enqueue(new Callback<ResponseTimeData>() {
            @Override
            public void onResponse(Call<ResponseTimeData> call, Response<ResponseTimeData> response) {
                if (response.isSuccessful()) {
                    cabDataList = response.body().getData();
                    for (CabData c : cabDataList)
                        if (c.getAvailableCars() > 0) avlCabDataList.add(c);

                    mapCluster.showMarkers();
                    cbShowAvailableOnly.setEnabled(true);

                } else {
                    Toast.makeText(getActivity(), "Failed to retrieve data", Toast.LENGTH_SHORT).show();
                    cbShowAvailableOnly.setEnabled(false);
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<ResponseTimeData> call, Throwable t) {
                Toast.makeText(getActivity(), "Internet connection problem", Toast.LENGTH_SHORT).show();
                cbShowAvailableOnly.setEnabled(false);
                dialog.dismiss();
                networkFailureCallRetry();
            }
        });
    }

    private void networkFailureCallRetry() {
        CabBookingUIUtils.showNetowrkErrorDialog(getActivity(), () -> loadCabs());
    }
}
