package com.example.projetcalculatriceandroid.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import java.util.Locale;

public class LocaleHelper {

    public static Context setLocale(Context context, String language) {

        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.setLocale(locale);

        return context.createConfigurationContext(config);
    }

    public static String getLanguage(Context context) {
        SharedPreferences prefs =
                context.getSharedPreferences("settings", Context.MODE_PRIVATE);

        return prefs.getString("language", "fr");
    }
}