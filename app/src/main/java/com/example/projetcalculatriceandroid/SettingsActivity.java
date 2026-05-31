package com.example.projetcalculatriceandroid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.app.AppCompatActivity;
import com.example.projetcalculatriceandroid.helpers.LocaleHelper;

public class SettingsActivity extends AppCompatActivity {

    // Tableaux de configuration
    private static final String[][] LANGUAGES = {
            {"Français", "fr"},
            {"Anglais", "en"},
            {"Russe", "ru"}
    };

    private static final String[][] THEMES = {
            {"Clair", "light"},
            {"Sombre", "dark"}
    };

    private static final String[][] SOUNDS = {
            {"Activés", "true"},
            {"Désactivés", "false"}
    };

    @Override
    protected void attachBaseContext(Context newBase) {
        String lang = LocaleHelper.getLanguage(newBase);
        super.attachBaseContext(LocaleHelper.setLocale(newBase, lang));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Récupération des vues
        Spinner spinnerLangue = findViewById(R.id.spinnerLangue);
        Spinner spinnerTheme = findViewById(R.id.spinnerTheme);
        Switch switchSounds = findViewById(R.id.switchSounds);

        // Initialisation des SharedPreferences
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);

        // Initialisation du Spinner pour les langues
        setupSpinner(spinnerLangue, LANGUAGES, "language", (position, code) -> {
            LocaleHelper.setLocale(this, code);
            restartParent(); // Redémarre l'activité pour appliquer la langue
        });

        // Initialisation du Spinner pour les thèmes
        setupSpinner(spinnerTheme, THEMES, "theme", (position, code) -> {
            int nightMode = code.equals("dark") ?
                    AppCompatDelegate.MODE_NIGHT_YES :
                    AppCompatDelegate.MODE_NIGHT_NO;
            AppCompatDelegate.setDefaultNightMode(nightMode);
        });

        // Initialisation du Switch pour les sons
        boolean soundsEnabled = prefs.getBoolean("sounds_enabled", true); // Par défaut : activé
        switchSounds.setChecked(soundsEnabled);

        // Écouteur pour le Switch : sauvegarde l'état quand il change
        switchSounds.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("sounds_enabled", isChecked).apply();
        });
    }

    // Méthode générique pour configurer un Spinner
    private void setupSpinner(Spinner spinner, String[][] items, String prefKey, SpinnerCallback callback) {
        // Extraire les noms affichables
        String[] displayNames = new String[items.length];
        for (int i = 0; i < items.length; i++) {
            displayNames[i] = items[i][0];
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                displayNames
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Récupère la valeur sauvegardée
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        String savedValue = prefs.getString(prefKey, items[0][1]);

        // Trouve la position de la valeur sauvegardée
        int savedPosition = 0;
        for (int i = 0; i < items.length; i++) {
            if (items[i][1].equals(savedValue)) {
                savedPosition = i;
                break;
            }
        }
        spinner.setSelection(savedPosition);

        // Gère la sélection (évite le déclenchement au premier chargement)
        final boolean[] firstSelection = {true};
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (firstSelection[0]) {
                    firstSelection[0] = false;
                    return;
                }
                String code = items[position][1]; // 2e élément
                prefs.edit().putString(prefKey, code).apply();
                callback.onSelected(position, code);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    // Interface pour les callbacks des Spinners
    private interface SpinnerCallback {
        void onSelected(int position, String code);
    }

    // Redémarre l'activité parent (MainActivity)
    private void restartParent() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}