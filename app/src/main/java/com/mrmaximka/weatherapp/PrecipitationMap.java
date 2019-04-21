package com.mrmaximka.weatherapp;

import android.content.Context;

import java.util.HashMap;

public class PrecipitationMap  {

    private Context context;
    private HashMap<String, String> hashMap;

    public PrecipitationMap(Context context) {
        this.context = context;
        hashMap = new HashMap<>();
        writeMap();     // Создаем список
    }

    private void writeMap() {
        String[] keys = context.getResources().getStringArray(R.array.id_description);  // Массив ключей
        String[] values = context.getResources().getStringArray(R.array.description);   // Массив значений

        for (int i = 0; i < keys.length; i++) {     // Запишем в hashMap ключ и его значение
            hashMap.put(keys[i], values[i]);
        }
    }

    public String getDescription(String key){   // Геттера на значение по ключу
        return hashMap.get(key);
    }

}
