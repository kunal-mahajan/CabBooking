package com.smove.carbooking.api;

/**
 * Created by Kunal.Mahajan on 7/23/2018.
 */


import com.smove.carbooking.pojo.ResponseLiveData;
import com.smove.carbooking.pojo.ResponseTimeData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface BaseApiService {

    @GET("availability")
    Call<ResponseTimeData> getTimedCabs(@Query("startTime") long startTimeUTC, @Query("endTime") long endTimeUTC);

    @GET("locations")
    Call<ResponseLiveData> getLiveCabs();
}
