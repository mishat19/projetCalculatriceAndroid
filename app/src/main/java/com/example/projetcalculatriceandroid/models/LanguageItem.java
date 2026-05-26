package com.example.projetcalculatriceandroid.models;

public class LanguageItem {

    private final String name;
    private final int flagRes;
    private final String localeCode;

    public LanguageItem(String name, int flagRes, String localeCode) {
        this.name = name;
        this.flagRes = flagRes;
        this.localeCode = localeCode;
    }

    public String getName() {
        return name;
    }

    public int getFlagRes() {
        return flagRes;
    }

    public String getLocaleCode() {
        return localeCode;
    }
}