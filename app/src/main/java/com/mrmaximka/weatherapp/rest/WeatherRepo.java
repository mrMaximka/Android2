package com.mrmaximka.weatherapp.rest;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherRepo {
    private static WeatherRepo singleton = null;

    private WeatherInterface API;

    private WeatherRepo(){
        API = createAdapter();
    }

    public WeatherInterface getAPI(){
        return API;
    }

    public static WeatherRepo getSingleton() {
        if(singleton == null) {
            singleton = new WeatherRepo();
        }

        return singleton;
    }

    private WeatherInterface createAdapter() {
        Retrofit adapter = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return adapter.create(WeatherInterface.class);
    }

}
