package com.mrmaximka.weatherapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.CityViewHolder> {

    private final ArrayList<String> cities;
    private FirstFragment.OnClick onClickListener;
    private static int position;

    CityAdapter(ArrayList<String> cities, FirstFragment.OnClick onClickListener){
        this.cities = cities;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public CityViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View textView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_view_item, viewGroup, false);
        return new CityViewHolder(textView);
    }

    @Override
    public void onBindViewHolder(@NonNull final CityAdapter.CityViewHolder cityViewHolder, int i) {
        cityViewHolder.textView.setText(cities.get(i));

        cityViewHolder.textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {        // Листенер на удержание
                setPosition(cityViewHolder.getLayoutPosition());    // Запомнили позицию
                return false;
            }
        });
    }

    @Override
    public void onViewRecycled(@NonNull CityViewHolder holder) {
        holder.textView.setOnLongClickListener(null);
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return cities.size();
    }

    class CityViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        TextView textView;

        CityViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.cv_item_text);
            this.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickListener.onListItemClick(getLayoutPosition());
                }
            });

            this.textView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select The Action");   // Заголовок контексного меню
        }
    }

    static int getPosition() {
        return position;
    }

    private void setPosition(int position) {
        CityAdapter.position = position;
    }
}
