package com.example.projetcalculatriceandroid;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.projetcalculatriceandroid.helpers.LocaleHelper;

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
    private TextView scoreTextView;

    private MediaPlayer correctSound;
    private MediaPlayer wrongSound;

    private FrameLayout animationContainer;
    private Random random = new Random();

    @Override
    protected void attachBaseContext(Context newBase) {
        String lang = LocaleHelper.getLanguage(newBase);
        super.attachBaseContext(LocaleHelper.setLocale(newBase, lang));
    }

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

    private double calculer(String expression) {

        expression = expression.replace("×", "*");
        expression = expression.replace("x", "*");
        expression = expression.replace("÷", "/");
        expression = expression.replace(" ", "");

        String operator = "";

        if (expression.contains("+")) operator = "+";
        else if (expression.contains("-")) operator = "-";
        else if (expression.contains("*")) operator = "*";
        else if (expression.contains("/")) operator = "/";

        String[] parts = expression.split("\\" + operator);

        double a = Double.parseDouble(parts[0]);
        double b = Double.parseDouble(parts[1]);

        switch (operator) {
            case "+": return a + b;
            case "-": return a - b;
            case "*": return a * b;
            case "/": return a / b;
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
        text.setText(getString(R.string.game_over_message, calculerScore()));
        text.setTextSize(20);

        EditText inputPseudo = new EditText(this);
        inputPseudo.setHint(getString(R.string.enter_pseudo_hint));

        Button btnSave = new Button(this);
        btnSave.setText(getString(R.string.save_score_button));

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

    private void updateScore() {
        scoreTextView.setText(getString(R.string.score_label, score));
    }

    private void playSound(MediaPlayer sound) {
        if (sound != null) {
            sound.setVolume(1.0f, 1.0f);
            sound.start();
        }
    }

    private void showScoreAnimation() {
        // Crée une TextView pour afficher "+10"
        TextView scoreText = new TextView(this);
        scoreText.setText("+10");
        scoreText.setTextSize(24f); // Taille du texte
        scoreText.setTypeface(null, Typeface.BOLD); // Texte en gras

        // Génère une couleur aléatoire (claire pour être visible)
        int color = Color.rgb(
                random.nextInt(200) + 55,  // Rouge (55-255)
                random.nextInt(200) + 55,  // Vert (55-255)
                random.nextInt(200) + 55   // Bleu (55-255)
        );
        scoreText.setTextColor(color);

        // Positionne la TextView au centre de l'écran
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        params.gravity = Gravity.CENTER;
        scoreText.setLayoutParams(params);

        // Ajoute la TextView au conteneur d'animation
        animationContainer.addView(scoreText);

        // Animation : agrandit puis réduit la taille (effet "pop")
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(scoreText, "scaleX", 1f, 1.5f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(scoreText, "scaleY", 1f, 1.5f, 1f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(scoreText, "alpha", 1f, 0f); // Disparition

        // Déplace la TextView vers le haut pendant l'animation
        ObjectAnimator translateY = ObjectAnimator.ofFloat(
                scoreText,
                "translationY",
                0f,
                -200f  // Monte de 200 pixels
        );

        // Regroupe les animations
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY, alpha, translateY);
        animatorSet.setDuration(1000); // Durée : 1 seconde

        // Supprime la TextView après l'animation
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animationContainer.removeView(scoreText);
            }
        });

        animatorSet.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Libère les MediaPlayer
        if (correctSound != null) {
            correctSound.release();
            correctSound = null;
        }
        if (wrongSound != null) {
            wrongSound.release();
            wrongSound = null;
        }
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

        Button boutonCheck = findViewById(R.id.bouton_check);
        Button boutonDelete = findViewById(R.id.bouton_delete);
        Button boutonSymbol = findViewById(R.id.bouton_symbol);

        textViewCalcul = findViewById(R.id.calcul);
        textViewResultat = findViewById(R.id.resultat);

        calculCourant = generateRandomExpression();
        textViewCalcul.setText(calculCourant);

        scoreTextView = findViewById(R.id.score);
        updateScore();

        vie1 = findViewById(R.id.vie1);
        vie2 = findViewById(R.id.vie2);
        vie3 = findViewById(R.id.vie3);

        // Initialisation des sons
        correctSound = MediaPlayer.create(this, R.raw.correct_answer);
        wrongSound = MediaPlayer.create(this, R.raw.wrong_answer);

        animationContainer = findViewById(R.id.animation_container);

        // Méthode commune pour les boutons numériques
        Button[] boutonsNumeriques = {bouton_0, bouton_1, bouton_2, bouton_3, bouton_4, bouton_5, bouton_6, bouton_7, bouton_8, bouton_9};
        for (Button bouton : boutonsNumeriques) {
            bouton.setOnClickListener(v -> {
                textViewResultat.append(((Button) v).getText());
            });
        }

        boutonDelete.setOnClickListener(v -> {
            String current = textViewResultat.getText().toString();

            if (!current.isEmpty()) {
                textViewResultat.setText(current.substring(0, current.length() - 1));
            }
        });

        boutonCheck.setOnClickListener(v -> {

            if (gameOver) return;

            String reponseUtilisateur = textViewResultat.getText().toString();

            boolean correct = checkAnswer(reponseUtilisateur, calculCourant);

            if (correct) {
                textViewResultat.setText(getString(R.string.true_answer));
                score += 10;
                updateScore();
                showScoreAnimation();
                playSound(correctSound);
            } else {
                vies--;
                updateVies();
                textViewResultat.setText(getString(R.string.false_answer));
                playSound(wrongSound);
            }

            if (vies <= 0) {
                gameOver = true;
                afficherGameOver();
                return;
            }

            textViewResultat.postDelayed(() -> {
                calculCourant = generateRandomExpression();
                textViewCalcul.setText(calculCourant);
                textViewResultat.setText("");
            }, 1200);
        });
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