package com.smove.carbooking.ui;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.widget.LinearLayout;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.smove.carbooking.AppConstants;
import com.smove.carbooking.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Kunal.Mahajan on 8/4/2018.
 */

public class CabBookingUIUtils {

    public static int convertDpToPixel(float dp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }

    public static LinearLayout getLinearLayout(Context context, boolean isVertical) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(isVertical ? LinearLayout.VERTICAL : LinearLayout.HORIZONTAL);
        linearLayout.setId(R.id.activity_home_rl_root_layout);
        LinearLayout.LayoutParams rlp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        linearLayout.setLayoutParams(rlp);
        return linearLayout;
    }

    public static String getFormatedDateTime(long d) {
        SimpleDateFormat format = new SimpleDateFormat("MMM dd,yyyy  HH:mm");
        Date date = new Date(d);
        return format.format(date);
    }

    public static void showNetowrkErrorDialog(Context context, Runnable retryCallback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("ERROR !!");
        builder.setMessage("Network Unavailable!");

        builder.setPositiveButton("Retry", (dialog, which) -> {
            dialog.dismiss();
            retryCallback.run();
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static float getDescriptorColor(boolean isEnabled) {
        return isEnabled ? BitmapDescriptorFactory.HUE_GREEN : AppConstants.HUE_UNAVAILABLE;
    }

    public static void makeCall(Activity activity) {
        Intent callIntent = new Intent(Intent.ACTION_VIEW);
        callIntent.setData(Uri.parse("tel:" + AppConstants.PHONE_NUMBER));
        activity.startActivity(callIntent);
    }
}
