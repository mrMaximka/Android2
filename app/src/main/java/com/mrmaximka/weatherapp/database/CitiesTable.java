package com.mrmaximka.weatherapp.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mrmaximka.weatherapp.FirstFragment;

import java.util.ArrayList;

public class CitiesTable {
    private final static String TABLE_NAME = "Cities";
    private final static String COLUMN_ID = "_id";
    private final static String COLUMN_CITY = "city";

    public static void createTable(SQLiteDatabase database){        // Создание таблицы
        database.execSQL("CREATE TABLE " + TABLE_NAME + " (" + COLUMN_ID
                + " INTEGER   PRIMARY KEY AUTOINCREMENT," + COLUMN_CITY + " TEXT (64));");

    }

    public static void onUpgrade(SQLiteDatabase database){  // Пока нечего апгрейдить

    }

    public static void addCity(String city, SQLiteDatabase database){   // Добавить город
        database.execSQL("INSERT INTO " + TABLE_NAME + " (" + COLUMN_CITY + ") "
                + " VALUES ( '" + city + "');");
    }


    public static void deleteCity(String city, SQLiteDatabase database){    // Удалить город
        database.delete(TABLE_NAME, COLUMN_CITY + " = '" + city + "';", null);
    }

    public static void deleteAll(SQLiteDatabase database){      // Удалить все
        database.delete(TABLE_NAME, null, null);
    }

    public static ArrayList<String> getAllCities(SQLiteDatabase database){  // Получить все города
        Cursor cursor = database.query(TABLE_NAME, null, null, null, null, null, null);
        return getResultFromCursor(cursor);
    }

    private static ArrayList<String> getResultFromCursor(Cursor cursor) {
        ArrayList<String> list = null;

        if (cursor != null && cursor.moveToFirst()){
            list = new ArrayList<>(cursor.getCount());

            int cityIdx = cursor.getColumnIndex(COLUMN_CITY);
            do {
                list.add(cursor.getString(cityIdx));
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
        return list == null ? new ArrayList<String>(0) : list;
    }
}
