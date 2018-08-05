package com.smove.carbooking.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.smove.carbooking.R;

/**
 * Created by Kunal.Mahajan on 8/4/2018.
 */

public class ActivityHome extends AppCompatActivity {
    private static final int REQUEST_LOCATION_PERMISSION_RESULT = 300;
    TabLayout tabLayout;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout linearLayout = CabBookingUIUtils.getLinearLayout(this, true);
        LinearLayout.LayoutParams llParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        tabLayout = new TabLayout(this);
        tabLayout.addTab(tabLayout.newTab().setText("Available Now"));
        tabLayout.addTab(tabLayout.newTab().setText("Scheduled Availability"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        viewPager = new ViewPager(this);
        viewPager.setId(R.id.activity_home_view_pager_fragments);
        viewPager.setLayoutParams(llParams);

        linearLayout.addView(tabLayout);
        linearLayout.addView(viewPager);


        setContentView(linearLayout, llParams);

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION_RESULT);
        } else init();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION_RESULT) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Application will without your precised location!", Toast.LENGTH_SHORT).show();
            }
            init();
        }
    }

    private void init() {

        FragmentStatePagerAdapter adapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public Fragment getItem(int position) {
                if (position == 0) return new FragmentCurrentAvailableCabs();
                return new FragmentScheduledAvailableCabs();
            }
        };

        viewPager.setAdapter(adapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                tabLayout.getTabAt(position).select();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

}
