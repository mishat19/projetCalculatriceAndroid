package com.example.projetcalculatriceandroid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.projetcalculatriceandroid.helpers.LocaleHelper;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        String lang = LocaleHelper.getLanguage(newBase);
        super.attachBaseContext(LocaleHelper.setLocale(newBase, lang));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        SharedPreferences prefs =
                getSharedPreferences("settings", MODE_PRIVATE);

        String language =
                prefs.getString("language", "fr");

        String theme =
                prefs.getString("theme", "light");

        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        assert theme != null;

        if(theme.equals("dark")) {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES);
        }
        else {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO);
        }

        assert language != null;

        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Configuration config =
                getResources().getConfiguration();

        config.setLocale(locale);

        getResources().updateConfiguration(
                config,
                getResources().getDisplayMetrics());

        Button boutonCalculatrice = findViewById(R.id.bouton_jouer);
        boutonCalculatrice.setOnClickListener(v -> {
            Intent intent = new Intent(this, CalculatriceActivity.class);
            startActivity(intent);
        });

        Button boutonHistorique = findViewById(R.id.test_bouton_highscore);
        boutonHistorique.setOnClickListener(v -> {
            Intent intent = new Intent(this, HistoriqueActivity.class);
            startActivity(intent);
        });

        Button boutonParametres = findViewById(R.id.test_bouton_settings);

        boutonParametres.setOnClickListener(v -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        });
    }
}