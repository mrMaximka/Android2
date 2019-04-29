package com.mrmaximka.weatherapp.rest;

import com.mrmaximka.weatherapp.rest.entite.WeatherRequestRestModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherCoordInterface {
    @GET("data/2.5/weather")
    Call<WeatherRequestRestModel> loadCoordWeather(@Query("lat") String lat,
                                                   @Query("lon") String lon,
                                                   @Query("appid") String keyApi,
                                                   @Query("units") String units);
}
