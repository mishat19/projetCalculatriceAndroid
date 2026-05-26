package com.example.projetcalculatriceandroid;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context) {
        super(context, "scores.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE scores (id INTEGER PRIMARY KEY AUTOINCREMENT, pseudo TEXT, score INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS scores");
        onCreate(db);
    }

    public void insertScore(String pseudo, int score) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("pseudo", pseudo);
        values.put("score", score);

        db.insert("scores", null, values);
    }

    public Cursor getTopScores() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT pseudo, score FROM scores ORDER BY score DESC LIMIT 10",
                null
        );
    }
}