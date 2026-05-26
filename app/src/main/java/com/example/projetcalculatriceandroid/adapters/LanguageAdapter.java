package com.example.projetcalculatriceandroid.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.projetcalculatriceandroid.R;
import com.example.projetcalculatriceandroid.models.LanguageItem;

import java.util.List;

public class LanguageAdapter extends ArrayAdapter<LanguageItem> {

    public LanguageAdapter(Context context, List<LanguageItem> items) {
        super(context, 0, items);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        return createView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        return createView(position, convertView, parent);
    }

    private View createView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_language, parent, false);
        }

        LanguageItem item = getItem(position);

        ImageView flag = convertView.findViewById(R.id.imgFlag);
        TextView name = convertView.findViewById(R.id.txtName);

        assert item != null;
        flag.setImageResource(item.getFlagRes());
        name.setText(item.getName());

        return convertView;
    }
}