package com.mrmaximka.weatherapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mrmaximka.weatherapp.database.CitiesTable;

import java.util.ArrayList;
import java.util.Objects;

public class FirstFragment extends Fragment {

    public static final String CITY_NAME = "name";
    public static final String CITY_POSITION = "position";
    private TextView behaviorTitle;         // Видимая часть SheetBehavior
    private LinearLayout behaviorLayout;    // Layout Behavior'a
    private static RecyclerView recyclerView;      // Тут список городов
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



        initViews(view);    // Инициализация вьюх`
        registerForContextMenu(recyclerView);   // Добавиди контекстное меню на ресайклер вью

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        townList = CitiesTable.getAllCities(MainActivity.getDatabase());    // Взяли список из БД
        setViewSettings();  // Задаем вьюхам необходимые параметры
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = Objects.requireNonNull(getActivity()).getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int position;
        try {
            position = CityAdapter.getPosition();   // Взяли сохраненную позицию
        } catch (Exception e) {
            return super.onContextItemSelected(item);
        }

        switch (item.getItemId()){
            case R.id.context_menu_delete:
                String city = townList.get(position);   // Получили название
                CitiesTable.deleteCity(city, MainActivity.getDatabase());   // Удалили из БД
                townList.remove(city);          // Из списка
                adapter.notifyDataSetChanged(); // И обновили список
                return true;
            default:
                return super.onContextItemSelected(item);
        }
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

    public static void updateCitiesList(String city){   // Добавить город в список
        townList.add(city);
        adapter.notifyDataSetChanged();
    }

    public static void deleteAll(){     // Удалить все записи
        townList.clear();
        adapter.notifyDataSetChanged();
    }
}
