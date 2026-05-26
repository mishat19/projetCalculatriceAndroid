package com.example.projetcalculatriceandroid;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.util.Random;

public class CalculatriceActivity extends AppCompatActivity {
    private TextView textViewCalcul;
    private TextView textViewResultat;

    private String calculCourant;

    private int vies = 3;
    private int score = 0;
    private boolean scoreSaved = false;
    private DatabaseHelper dbHelper;
    private boolean gameOver = false;
    private ImageView vie1, vie2, vie3;

    public String generateRandomExpression() {
        Random random = new Random();
        int a, b;
        String operator;
        int operatorChoice = random.nextInt(4); // 0: +, 1: -, 2: *, 3: /

        switch (operatorChoice) {
            case 0:
                a = random.nextInt(100) + 1;
                b = random.nextInt(100) + 1;
                operator = "+";
                break;
            case 1:
                a = random.nextInt(100) + 1;
                b = random.nextInt(a) + 1; // Évite les résultats négatifs
                operator = "-";
                break;
            case 2:
                a = random.nextInt(10) + 1;
                b = random.nextInt(10) + 1;
                operator = "×";
                break;
            case 3:
                // Pour la division, on s'assure que b divise a
                b = random.nextInt(10) + 1;
                a = b * (random.nextInt(10) + 1);
                operator = "÷";
                break;
            default:
                a = 1;
                b = 1;
                operator = "+";
        }
        return a + " " + operator + " " + b;
    }

    public boolean checkAnswer(String userAnswer, String expression) {
        try {
            double expectedResult = calculer(expression);
            double userResult = Double.parseDouble(userAnswer);
            return Math.abs(userResult - expectedResult) < 0.0001; // Tolérance pour les flottants
        } catch (Exception e) {
            return false;
        }
    }

    public double evaluateExpression(String expression) {
        // Remplace les symboles pour uniformiser
        expression = expression.replace("×", "*").replace("÷", "/");

        // Utilise une pile pour gérer les opérations (algorithme de Shunting Yard)
        // ou utilise une approche récursive pour les parenthèses.
        // Ici, un exemple très simple pour les opérations de base SANS parenthèses :
        String[] tokens = expression.split("(?<=[-+*/])|(?=[-+*/])");
        double result = Double.parseDouble(tokens[0]);
        for (int i = 1; i < tokens.length; i += 2) {
            String operator = tokens[i];
            double nextValue = Double.parseDouble(tokens[i + 1]);
            switch (operator) {
                case "+":
                    result += nextValue;
                    break;
                case "-":
                    result -= nextValue;
                    break;
                case "*":
                    result *= nextValue;
                    break;
                case "/":
                    result /= nextValue;
                    break;
            }
        }
        return result;
    }

    private double calculer(String expression) {

        expression = expression.replace("×", "*");
        expression = expression.replace("x", "*");
        expression = expression.replace("÷", "/");

        char operateur = ' ';

        if (expression.contains("+")) {
            operateur = '+';
        } else if (expression.contains("-")) {
            operateur = '-';
        } else if (expression.contains("*")) {
            operateur = '*';
        } else if (expression.contains("/")) {
            operateur = '/';
        }

        String[] parties = expression.split("\\" + operateur);

        double a = Double.parseDouble(parties[0].trim());
        double b = Double.parseDouble(parties[1].trim());

        switch (operateur) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                return a / b;
        }

        return 0;
    }

    private void updateVies() {

        vie1.setVisibility(vies >= 1 ? View.VISIBLE : View.INVISIBLE);
        vie2.setVisibility(vies >= 2 ? View.VISIBLE : View.INVISIBLE);
        vie3.setVisibility(vies >= 3 ? View.VISIBLE : View.INVISIBLE);
    }

    private void afficherGameOver() {

        // Layout simple popup
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 50, 50, 50);

        TextView text = new TextView(this);
        text.setText("Game Over ! Score : " + calculerScore());
        text.setTextSize(20);

        EditText inputPseudo = new EditText(this);
        inputPseudo.setHint("Ton pseudo");

        Button btnSave = new Button(this);
        btnSave.setText("Enregistrer score");

        layout.addView(text);
        layout.addView(inputPseudo);
        layout.addView(btnSave);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(layout)
                .setCancelable(true)
                .create();

        dialog.setOnCancelListener(dialogInterface -> {
                    retourMenu();
        });

        btnSave.setOnClickListener(v -> {

            String pseudo = inputPseudo.getText().toString().trim();

            if (pseudo.isEmpty()) {
                inputPseudo.setError("Entre un pseudo");
                return;
            }

            saveScore(pseudo, calculerScore());

            scoreSaved = true;

            dialog.dismiss();

            retourMenu();
        });

        dialog.show();
    }

    private void retourMenu() {
        Intent intent = new Intent(CalculatriceActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void saveScore(String pseudo, int score) {
        dbHelper.insertScore(pseudo, score);
    }

    private int calculerScore() {
        return score;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calculatrice);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DatabaseHelper(this);

        // Récupération des vues
        Button bouton_9 = findViewById(R.id.bouton_9);
        Button bouton_8 = findViewById(R.id.bouton_8);
        Button bouton_7 = findViewById(R.id.bouton_7);
        Button bouton_6 = findViewById(R.id.bouton_6);
        Button bouton_5 = findViewById(R.id.bouton_5);
        Button bouton_4 = findViewById(R.id.bouton_4);
        Button bouton_3 = findViewById(R.id.bouton_3);
        Button bouton_2 = findViewById(R.id.bouton_2);
        Button bouton_1 = findViewById(R.id.bouton_1);
        Button bouton_0 = findViewById(R.id.bouton_0);

        textViewCalcul = findViewById(R.id.calcul);
        textViewResultat = findViewById(R.id.resultat);

        calculCourant = generateRandomExpression();
        textViewCalcul.setText(calculCourant);

        vie1 = findViewById(R.id.vie1);
        vie2 = findViewById(R.id.vie2);
        vie3 = findViewById(R.id.vie3);

        // Méthode commune pour les boutons numériques
        Button[] boutonsNumeriques = {bouton_0, bouton_1, bouton_2, bouton_3, bouton_4, bouton_5, bouton_6, bouton_7, bouton_8, bouton_9};
        for (Button bouton : boutonsNumeriques) {
            bouton.setOnClickListener(v -> {
                textViewResultat.append(((Button) v).getText());
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuItem boutonRAZ;
        MenuItem boutonEqual;

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.monmenu, menu);
        boutonRAZ = menu.findItem(R.id.menuItem_RAZ);
        boutonEqual = menu.findItem(R.id.btn_equal);

        boutonRAZ.setOnMenuItemClickListener(v -> {
            textViewResultat.setText("");
            return true;
        });

        boutonEqual.setOnMenuItemClickListener(v -> {

            if (gameOver) return true;

            String reponseUtilisateur = textViewResultat.getText().toString();

            boolean correct = checkAnswer(reponseUtilisateur, calculCourant);

            if (correct) {
                textViewResultat.setText("Vrai");
                score += 10;
            } else {
                vies--;
                updateVies();
                textViewResultat.setText("Faux");
            }

            if (vies <= 0) {
                gameOver = true;

                afficherGameOver();

                return true;
            }

            textViewResultat.postDelayed(() -> {
                calculCourant = generateRandomExpression();
                textViewCalcul.setText(calculCourant);
                textViewResultat.setText("");
            }, 1200);

            return true;
        });

        return super.onCreateOptionsMenu(menu);
    }
}