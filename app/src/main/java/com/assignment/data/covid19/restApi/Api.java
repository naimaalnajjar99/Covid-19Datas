package com.assignment.data.covid19.restApi;

import com.assignment.data.covid19.models.Data;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Api {
    String BASE_URL = "https://api.covid19api.com/";
    @GET("country/{country}/status/confirmed/live")
    Call<List<Data>> getData(@Path("country") String country, @Query("from") String from, @Query("to") String to);
}
