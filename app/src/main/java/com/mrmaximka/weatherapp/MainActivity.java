package com.mrmaximka.weatherapp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mrmaximka.weatherapp.database.CitiesTable;
import com.mrmaximka.weatherapp.database.DatabaseHelper;

import java.io.Serializable;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String AREA_KEY = "Area";
    public static final String TV_AREA_TEXT = "tvAreaText";
    public static final String ET_AREA_TEXT = "etAreaText";
    private Toolbar toolbar;        // Тулбар
    private DrawerLayout drawer;    // И гамбургер
    private NavigationView navigationView;
    private EditText etChangeArea;
    private Button btnChangeArea;   //Кнопка сохранения нового региона
    private Button btnLoadArea; // Загрузка сохраненного региона
    private TextView tvArea;    // tv для региона
    private static SQLiteDatabase database;
    private final int permissionRequestCode = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();        // Инициализация вьюх
        initDB();           // Создали БД
        setViewSettings(savedInstanceState);  // Задать им необходимые параметры

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
                        != PackageManager.PERMISSION_GRANTED) {
            final String[] permissions = new String[]{Manifest.permission.RECEIVE_SMS};
            ActivityCompat.requestPermissions(this, permissions, permissionRequestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if(requestCode == permissionRequestCode) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Спасибо!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "Извините, апп без данного разрешения может работать неправильно",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initDB() {
        database = new DatabaseHelper(getApplicationContext()).getWritableDatabase();
    }

    private void initViews() {      // Инициализация вьюх
        toolbar = findViewById(R.id.toolbar);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        etChangeArea = findViewById(R.id.main_activity_et_change_area);
        btnChangeArea = findViewById(R.id.main_activity_btn_change_area);
        btnLoadArea = findViewById(R.id.main_activity_btn_load_area);
        tvArea = findViewById(R.id.main_activity_tv_area);
        initButtonListener();   // Подключаем листенеры
    }

    private void setViewSettings(Bundle savedInstanceState) {    // Настраиваем вьюхи

        if(savedInstanceState != null){
            tvArea.setText(savedInstanceState.getString(TV_AREA_TEXT));
            etChangeArea.setText(savedInstanceState.getString(ET_AREA_TEXT));
        }

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(   // Настройка гамбургера
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

        outState.putSerializable(TV_AREA_TEXT, (Serializable) tvArea.getText());
        outState.putSerializable(ET_AREA_TEXT, (Serializable) etChangeArea.getText());
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {     // Если гамбургер открыт, то закрываем его
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_city:
                addCity();      // Добаить город
                return true;
            case R.id.menu_clear_all:
                clearAll();     // Очистить все
                return true;
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void clearAll() {
        CitiesTable.deleteAll(database);    // Очистили в БД
        FirstFragment.deleteAll();          // И список
    }

    private void addCity() {       // Добавление города
        LayoutInflater inflater = LayoutInflater.from(this);
        final View view = inflater.inflate(R.layout.layout_add_city, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        builder.setTitle("Add city");
        builder.setNegativeButton("Cancel", null);
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                EditText addCityName = view.findViewById(R.id.et_add_city);
                CitiesTable.addCity(String.valueOf(addCityName.getText()), database);   // Запись в БД
                FirstFragment.updateCitiesList(String.valueOf(addCityName.getText()));  // Добавить в список
            }
        });
        builder.show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_camera:
                break;
            case R.id.nav_gallery:

                break;
            case R.id.nav_slideshow:

                break;
            case R.id.nav_manage:

                break;
            case R.id.nav_share:

                break;
            case R.id.nav_send:

                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initButtonListener() {
        btnChangeArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences sharedPref = getSharedPreferences(AREA_KEY, MODE_PRIVATE);// Получаем файл настроек по имени файла, хранящемуся в preferenceName
                savePreferences(sharedPref);// Сохранить настройки
            }
        });

        btnLoadArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences sharedPref = getSharedPreferences(AREA_KEY, MODE_PRIVATE);
                loadPreferences(sharedPref);// Загрузить настройки
            }
        });
    }

    private void savePreferences(SharedPreferences sharedPreferences) {
        String value= String.valueOf(etChangeArea.getText());
        SharedPreferences.Editor editor = sharedPreferences.edit();// Для сохранения настроек надо воспользоваться классом Editor
        editor.putString(AREA_KEY, value);// Установим в Editor значения
        editor.commit();// И сохраним файл настроек
        tvArea.setText(value);
    }

    private void loadPreferences(SharedPreferences sharedPreferences) { // Получаем настройки прямо из SharedPreferences
        String valueFirst = sharedPreferences.getString(AREA_KEY, "value");
        tvArea.setText(valueFirst);
    }

    public static SQLiteDatabase getDatabase() {
        return database;
    }

}
