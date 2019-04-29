package com.mrmaximka.weatherapp.rest;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherCoordRepo {
    private static WeatherCoordRepo singleton = null;

    private WeatherCoordInterface API;

    public WeatherCoordRepo(){
        API = createAdapter();
    }

    public WeatherCoordInterface getAPI(){
        return API;
    }

    public static WeatherCoordRepo getSingleton() {
        if(singleton == null) {
            singleton = new WeatherCoordRepo();
        }

        return singleton;
    }

    private WeatherCoordInterface createAdapter() {
        Retrofit adapter = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return adapter.create(WeatherCoordInterface.class);
    }
}
