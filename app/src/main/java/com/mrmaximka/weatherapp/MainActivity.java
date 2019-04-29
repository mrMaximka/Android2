package com.mrmaximka.weatherapp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import com.mrmaximka.weatherapp.rest.WeatherCoordRepo;
import com.mrmaximka.weatherapp.rest.entite.WeatherRequestRestModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ActivityCompat.OnRequestPermissionsResultCallback {

    WeatherRequestRestModel model = new WeatherRequestRestModel();

    private static final int PERMISSION_REQUEST_CODE = 10;
    public static final String LATITUDE_COORD = "Latitude";
    public static final String LONGITUDE_COORD = "Longitude";

    private Toolbar toolbar;        // Тулбар
    private DrawerLayout drawer;    // И гамбургер
    private NavigationView navigationView;
    private TextView yourCity;
    private Button checkYourCity;
    private static SQLiteDatabase database;
    private final int permissionRequestCode = 123;

    private String Latitude;
    private String Longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();        // Инициализация вьюх
        initDB();           // Создали БД
        setViewSettings();  // Задать им необходимые параметры

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

        if (requestCode == PERMISSION_REQUEST_CODE) { // Это та самая пермиссия, что мы запрашивали
            if (grantResults.length == 2 &&
                    (grantResults[0] == PackageManager.PERMISSION_GRANTED || grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                requestLocation(); // Пермиссия дана
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
        yourCity = findViewById(R.id.main_activity_tv_gps_city);
        checkYourCity = findViewById(R.id.main_activity_btn_your_city);

        getCoordinates();       // Получаем координаты мастоположения
        initButtonListener();   // Подключаем листенеры
    }

    private void setViewSettings() {    // Настраиваем вьюхи

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(   // Настройка гамбургера
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
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

    private void getCoordinates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED // Проверим на пермиссии, и если их нет, запросим у пользователя
                || ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            requestLocation(); // Запросим координаты
        } else {
            requestLocationPermissions();  // Пермиссии нет, будем запрашивать у пользователя
        }
    }

    private void requestLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);

        String provider = locationManager.getBestProvider(criteria, true);  // Наиболее подходящий провайдер
        if (provider != null) {
            // Будем получать геоположение через каждые 10 секунд или каждые 1000 метров
            locationManager.requestLocationUpdates(provider, 10000, 1000, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    Latitude = Double.toString(location.getLatitude());     // Широта
                    Longitude = Double.toString(location.getLongitude());   // Долгота
                    findCity();     // Определяем город по полученным данным
                }
                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {}
                @Override
                public void onProviderEnabled(String provider) {}
                @Override
                public void onProviderDisabled(String provider) {}
            });
        }
    }

    private void findCity() {
        WeatherCoordRepo.getSingleton().getAPI().loadCoordWeather(Latitude, Longitude, SecondFragment.API_KEY, "metric")
                .enqueue(new Callback<WeatherRequestRestModel>() {  // Через ретрофит запросим данные
                    @Override
                    public void onResponse(@NonNull Call<WeatherRequestRestModel> call, @NonNull Response<WeatherRequestRestModel> response) {
                        if (response.body() != null && response.isSuccessful()){    // Если выполнен и не пуст
                            model = response.body();    // Получаем модель
                            yourCity.setText(model.name);   // Запишем город
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<WeatherRequestRestModel> call, @NonNull Throwable t) {
                        yourCity.setText(getString(R.string.city_not_found)); // Сообщение при ошибке
                    }
                });
    }

    private void requestLocationPermissions() {     // Запрос пермиссии для геолокации
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                },
                PERMISSION_REQUEST_CODE);
    }

    private void initButtonListener() {
        checkYourCity.setOnClickListener(new View.OnClickListener() {   // Кнопка поготы по местоположению
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                intent.putExtra(LATITUDE_COORD, Latitude);    // Передаем долготу и широту
                intent.putExtra(LONGITUDE_COORD, Longitude);
                startActivity(intent);
            }
        });
    }

    public static SQLiteDatabase getDatabase() {
        return database;
    }

}
