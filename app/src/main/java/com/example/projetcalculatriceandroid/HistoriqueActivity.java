package com.example.projetcalculatriceandroid;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.projetcalculatriceandroid.helpers.LocaleHelper;

public class HistoriqueActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private TextView textViewScores;

    @Override
    protected void attachBaseContext(Context newBase) {
        String lang = LocaleHelper.getLanguage(newBase);
        super.attachBaseContext(LocaleHelper.setLocale(newBase, lang));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_historique);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DatabaseHelper(this);
        textViewScores = findViewById(R.id.textViewScores);

        afficherScores();
    }

    private void afficherScores() {
        Cursor cursor = dbHelper.getTopScores();

        StringBuilder builder = new StringBuilder();
        int rank = 1;

        if (cursor.moveToFirst()) {
            do {
                String pseudo = cursor.getString(0);
                int score = cursor.getInt(1);

                String medal;

                switch(rank) {
                    case 1:
                        medal = "🥇";
                        break;
                    case 2:
                        medal = "🥈";
                        break;
                    case 3:
                        medal = "🥉";
                        break;
                    default:
                        medal = rank + ".";
                }

                builder.append(medal)
                        .append(" ")
                        .append(pseudo)
                        .append(" - ")
                        .append(score)
                        .append(" pts\n\n");

                rank++;

            } while (cursor.moveToNext());
        } else {
            builder.append("Aucun score");
        }

        textViewScores.setText(builder.toString());
        cursor.close();
    }
}