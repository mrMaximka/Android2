package com.mrmaximka.weatherapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();        // Инициализация вьюх
        setViewSettings(savedInstanceState);  // Задать им необходимые параметры
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
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

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
}
