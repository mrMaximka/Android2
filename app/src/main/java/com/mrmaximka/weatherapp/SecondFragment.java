package com.mrmaximka.weatherapp;

import android.hardware.SensorEvent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mrmaximka.weatherapp.rest.WeatherRepo;
import com.mrmaximka.weatherapp.rest.entite.WeatherRequestRestModel;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SecondFragment extends Fragment {

    public static final String API_KEY = "762ee61f52313fbd10a4eb54ae4d4de2";
    private TextView tvCityName;        // Название города
    private TextView tvTemperature;     // Температура
    private TextView tvPrecipitatoin;   // Осадки
    private TextView tvWind;        // Ветер
    private TextView tvWet;         // Влажность
    private TextView tvPressure;    // Давление
    private ImageView weatherIcon;  // Иконка погоды

    private boolean isCbWind;       // Включен ли чекбокс "Ветер" на FirstFragment
    private boolean isCbWet;        // Влажность
    private boolean isCbPressure;   // И давление
    private static String temperatureText;  // Температура с датчика

    WeatherRequestRestModel model = new WeatherRequestRestModel();  // Модель с данными

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_second, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);        // Инициализируем вьюхт
        setViewSettings();      // Задаем им настройкт
    }

    private void initViews(View view) {
        String[] eng_towns = getResources().getStringArray(R.array.eng_towns);
        tvCityName = view.findViewById(R.id.second_fragment_tv_town_name);
        tvTemperature = view.findViewById(R.id.second_fragment_tv_temperature);
        tvPrecipitatoin = view.findViewById(R.id.second_fragment_tv_precipitation);
        tvWind = view.findViewById(R.id.second_fragment_tv_wind);
        tvWet = view.findViewById(R.id.second_fragment_tv_wet);
        tvPressure = view.findViewById(R.id.second_fragment_tv_pressure);
        weatherIcon = view.findViewById(R.id.second_fragment_iv_weather_icon);

        requestRetrofit(eng_towns[SecondActivity.getCityPosition()]);   // Запрос погоды через ретрофит для выбранного города
    }

    private void setViewSettings() {

        checkWeatherSettings();     // Проверяем какие настройки отобраджения были включены
        setVisibleSettings();       // Делаем видимыми необходимые объекты
    }

    private void checkWeatherSettings() {
        boolean[] weatherSettings = FirstFragment.getWeatherSettings(); // Берем массив

        isCbWind = weatherSettings[0];  // Смотрим, включены ли настройки ветра
        isCbWet = weatherSettings[1];   // Владности
        isCbPressure = weatherSettings[2];  // и давления
    }

    private void setVisibleSettings() { // Делаем видимыми необходимые части
        if (isCbWind) tvWind.setVisibility(View.VISIBLE);
        if (isCbWet) tvWet.setVisibility(View.VISIBLE);
        if (isCbPressure) tvPressure.setVisibility(View.VISIBLE);
    }

    // Вывод датчика температуры
    public static void showTemperatureSensors(SensorEvent event){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(event.values[0]);
        temperatureText = String.valueOf(stringBuilder);    // Сюда засунули температуру с датчика
    }


    public static double getTemperatureText() { // Геттер для сервиса
        if (temperatureText == null){
            return 0;
        }
        else {
            return Double.parseDouble(temperatureText);
        }
    }

    private void requestRetrofit(String city){
        WeatherRepo.getSingleton().getAPI().loadWeather(city, API_KEY, "metric")    // Создаем запрос
                .enqueue(new Callback<WeatherRequestRestModel>() {
                    @Override
                    public void onResponse(@NonNull Call<WeatherRequestRestModel> call, @NonNull Response<WeatherRequestRestModel> response) {
                        if (response.body() != null && response.isSuccessful()){    // Если выполнен и не пууст
                            model = response.body();    // Получаем модель
                            setTownName();      // Ставим название города
                            setCurrentTemp();   // Температуру
                            loadIcon();         // Иконку погоды
                            setPrecipitation(); // Описание погоды
                            setDetails();       // Ветер, давление, влажность
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<WeatherRequestRestModel> call, Throwable t) {
                        tvPrecipitatoin.setText(getString(R.string.error_msg)); // Сообщение при ошибке
                    }
                });
    }

    private void loadIcon() {
        String url = String.format(getString(R.string.second_fragmend_weather_icon_url), model.weather[0].icon);    // Запрос нужной картинки
        Picasso.get()               // Отображаем ее
                .load(url)
                .resize(200,200)    // С таким размером
                .into(weatherIcon);
    }

    private void setCurrentTemp(){
        tvTemperature.setText(String.format("%s%s", model.main.temp, getString(R.string.tv_celcium)));
    }

    private void setPrecipitation() {
        PrecipitationMap precipitationMap = new PrecipitationMap(getContext());     // Создаем hachMap с осадками
        String id = String.valueOf(model.weather[0].id);
        tvPrecipitatoin.setText(precipitationMap.getDescription(id).toUpperCase()); // Запрос типа осадков по id
    }

    private void setDetails() {
        tvWind.setText(String.format(getString(R.string.second_fragment_tv_wind), model.wind.speed));  // Записываем
        tvWet.setText(String.format(getString(R.string.second_fragment_tv_wet), model.main.humidity));                  // И влажность
        tvPressure.setText(String.format(getString(R.string.second_fragment_tv_pressure), model.main.pressure));   // Пишем Давление
    }

    private void setTownName() {
        tvCityName.setText(SecondActivity.getCityName());
    }
}
