package com.mrmaximka.weatherapp.rest.entite;

import com.google.gson.annotations.SerializedName;

public class WindRestModel {
    @SerializedName("speed")    public int speed;
    @SerializedName("deg")      public float deg;
}
