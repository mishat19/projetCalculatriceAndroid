package com.example.projetcalculatriceandroid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.content.res.Configuration;
import androidx.appcompat.app.AppCompatDelegate;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private Spinner spinnerLangue;
    private Spinner spinnerTheme;

    @Override
    protected void attachBaseContext(Context newBase) {
        String lang = com.example.projetcalculatriceandroid.LocaleHelper.getLanguage(newBase);
        super.attachBaseContext(com.example.projetcalculatriceandroid.LocaleHelper.setLocale(newBase, lang));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        spinnerLangue = findViewById(R.id.spinnerLangue);
        spinnerTheme = findViewById(R.id.spinnerTheme);

        String[] langues = {"Français", "Russe"};
        String[] themes = {"Clair", "Sombre"};

        ArrayAdapter<String> adapterLangues =
                new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item,
                        langues);

        adapterLangues.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);

        spinnerLangue.setAdapter(adapterLangues);

        ArrayAdapter<String> adapterThemes =
                new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item,
                        themes);

        adapterThemes.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);

        spinnerTheme.setAdapter(adapterThemes);

        SharedPreferences prefs =
                getSharedPreferences("settings", MODE_PRIVATE);

// Restaurer les choix enregistrés
        String langue = prefs.getString("language", "fr");
        String theme = prefs.getString("theme", "light");

// Position du spinner langue
        if (langue.equals("fr")) {
            spinnerLangue.setSelection(0);
        } else {
            spinnerLangue.setSelection(1);
        }

// Position du spinner thème
        if (theme.equals("light")) {
            spinnerTheme.setSelection(0);
        } else {
            spinnerTheme.setSelection(1);
        }

        // Changement de langue
        spinnerLangue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            boolean first = true;

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (first) { first = false; return; }

                String codeLangue = (position == 0) ? "fr" : "ru";

                prefs.edit()
                        .putString("language", codeLangue)
                        .apply();

                com.example.projetcalculatriceandroid.LocaleHelper.setLocale(SettingsActivity.this, codeLangue);

                restartParent();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}


        });

        // Changement de thème
        spinnerTheme.setOnItemSelectedListener(
                new android.widget.AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(android.widget.AdapterView<?> parent,
                                               android.view.View view,
                                               int position,
                                               long id) {

                        String theme = (position == 0) ? "light" : "dark";

                        prefs.edit()
                                .putString("theme", theme)
                                .apply();

                        if (theme.equals("dark")) {
                            AppCompatDelegate.setDefaultNightMode(
                                    AppCompatDelegate.MODE_NIGHT_YES);
                        } else {
                            AppCompatDelegate.setDefaultNightMode(
                                    AppCompatDelegate.MODE_NIGHT_NO);
                        }
                    }

                    @Override
                    public void onNothingSelected(android.widget.AdapterView<?> parent) {}
                });
    }

    private void restartParent() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}