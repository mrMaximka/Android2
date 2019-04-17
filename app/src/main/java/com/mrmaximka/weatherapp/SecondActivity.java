package com.mrmaximka.weatherapp;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class SecondActivity extends AppCompatActivity {

    static String cityName;     // Город, получим через интент
    static int cityPosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cityName = Objects.requireNonNull(getIntent().getExtras()).getString(FirstFragment.CITY_NAME);  // Получили город
        cityPosition = Objects.requireNonNull(getIntent().getExtras()).getInt(FirstFragment.CITY_POSITION);
        setContentView(R.layout.activity_second);
        createSensors();        // Создаем датчики
        showMessage();      // Запускаем сервис по уведомлениям
    }

    private void createSensors() {  // Создаем датчики
        SensorManager sensorManager;
        Sensor sensorTemperature;
        Sensor sensorWet;

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            sensorTemperature = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);    // На температуру
            sensorWet = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);      // И влажность

            sensorManager.registerListener(listenerTemperature, sensorTemperature, SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(listenerWet, sensorWet, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    SensorEventListener listenerTemperature = new SensorEventListener() {       // Listener на температуру

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            SecondFragment.showTemperatureSensors(event);   // Передаем температуру фрагменту
        }
    };

    SensorEventListener listenerWet = new SensorEventListener() {   // Листенер на влажность

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            //SecondFragment.showWetSensors(event);       // Передаем влажность фрагменту
        }
    };

    private void showMessage() {    // Тут запускаем сервис через интент
        Intent intent = new Intent(SecondActivity.this, NoteService.class);
        startService(intent);
    }

    public static String getCityName() {    // Гетер на азвание города
        return cityName;
    }

    public static int getCityPosition() {
        return cityPosition;
    }
}
