package com.mrmaximka.weatherapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class FirstFragment extends Fragment {

    public static final String CITY_NAME = "name";
    public static final String CITY_POSITION = "position";
    private TextView behaviorTitle;         // Видимая часть SheetBehavior
    private LinearLayout behaviorLayout;    // Layout Behavior'a
    private RecyclerView recyclerView;      // Тут список городов
    static CityAdapter adapter;             // Адаптер для RecyclerView
    static ArrayList<String> townList = new ArrayList<>();  // Лист с городами
    RecyclerView.LayoutManager layoutManager;   // LayoutManager на RecyclerView

    private CheckBox isCbWind;      // Чекбокс в Behavior на Ветер
    private CheckBox isCbWet;       // На влажность
    private CheckBox isCbPressure;  // И давление

    private static boolean weatherSettings[] = {false, false, false};

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
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String[] cities = Objects.requireNonNull(getActivity()).getResources().getStringArray(R.array.Cities);  // Массив с городами
        if (townList.isEmpty()){            // Засунем в Лист
            townList.addAll(Arrays.asList(cities)); // Доделаю тут добавление города
        }

        initViews(view);    // Инициализация вьюх
        setViewSettings();  // Задаем вьюхам необходимые параметры
    }

    private void initViews(@NonNull View view) {    // Инициализация вьюх
        behaviorTitle = view.findViewById(R.id.first_fragment_bsb_tv_title);
        behaviorLayout = view.findViewById(R.id.first_fragment_bsb_layout);

        recyclerView = view.findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(view.getContext());

        isCbWind = view.findViewById(R.id.first_fragment_cb_wind);
        isCbWet = view.findViewById(R.id.first_fragment_cb_wet);
        isCbPressure = view.findViewById(R.id.first_fragment_cb_pressure);

    }


    private void setViewSettings() {    // Здаем параметры вьюхам
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(behaviorLayout);
        int peekHeight = (int) behaviorTitle.getTextSize();     // Тут будет настройка видимой части Behavior'a
        bottomSheetBehavior.setPeekHeight(peekHeight + behaviorTitle.getPaddingBottom() + behaviorTitle.getPaddingTop());
        bottomSheetBehavior.setHideable(false);
        recyclerView.setLayoutManager(layoutManager);   // Тут настройка RecyclerView
        adapter = new CityAdapter(townList, onClickListener);
        recyclerView.setAdapter(adapter);

        setCbListener();    // Листенер на чекбоксы в Behavior, для передачи в SecondFragment
    }

    public OnClick onClickListener = new OnClick() {   //Обработка клика по итему RecyclerView
        @Override
        public void onListItemClick(int position) {
            sendCity(position);                     // Создаем новую активити
        }
    };

    interface OnClick{
        void onListItemClick(int position);
    }

    public void sendCity(int position){
        Intent intent = new Intent(getActivity(), SecondActivity.class);
        intent.putExtra(CITY_NAME, townList.get(position));    // Передаем название города
        intent.putExtra(CITY_POSITION, position);
        startActivity(intent);
    }

    public void setCbListener(){
        isCbWind.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                weatherSettings[0] = b;
            }
        });

        isCbWet.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                weatherSettings[1] = b;
            }
        });

        isCbPressure.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                weatherSettings[2] = b;
            }
        });
    }

    public static boolean[] getWeatherSettings() {
        return weatherSettings;
    }
}
