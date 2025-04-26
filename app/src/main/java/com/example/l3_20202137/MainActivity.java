package com.example.l3_20202137;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class MainActivity extends AppCompatActivity {

    Spinner categorySpinner, difficultySpinner;
    EditText amountEditText;
    Button checkConnectionButton, startButton;

    boolean isConnected = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        categorySpinner = findViewById(R.id.categorySpinner);
        difficultySpinner = findViewById(R.id.difficultySpinner);
        amountEditText = findViewById(R.id.amountEditText);
        checkConnectionButton = findViewById(R.id.checkConnectionButton);
        startButton = findViewById(R.id.startButton);

        setupSpinners(); // <-- AQUÍ


        startButton.setEnabled(false);


        checkConnectionButton.setOnClickListener(view -> {
            if (!validateInputs()) return;
            if (checkInternetConnection()) {
                Toast.makeText(this, "✅ Conexión a Internet exitosa", Toast.LENGTH_SHORT).show();
                isConnected = true;
                startButton.setEnabled(true);
                startButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#826AED")));
                startButton.setTextColor(Color.WHITE);
            } else {
                Toast.makeText(this, "❌ Sin conexión a Internet", Toast.LENGTH_SHORT).show();
                isConnected = false;
                startButton.setEnabled(false);
            }
        });

        startButton.setOnClickListener(view -> {
            if (isConnected) {
                // Aquí iría el Intent a la siguiente vista
                Toast.makeText(this, "Iniciando juego...", Toast.LENGTH_SHORT).show();
            }
        });

        startButton.setOnClickListener(view -> {
            if (isConnected) {
                // Crear un Intent para redirigir a la actividad QuestionsActivity
                Intent intent = new Intent(MainActivity.this, QuestionsActivity.class);
                startActivity(intent);
            }
        });

        startButton.setOnClickListener(view -> {
            if (isConnected) {
                // Obtiene la cantidad de preguntas
                String amountStr = amountEditText.getText().toString();
                int amount = Integer.parseInt(amountStr);

                // Obtiene la dificultad seleccionada
                int difficultyPos = difficultySpinner.getSelectedItemPosition();
                int timePerQuestion = 0;

                switch (difficultyPos) {
                    case 1: // Fácil
                        timePerQuestion = 5;
                        break;
                    case 2: // Medio
                        timePerQuestion = 7;
                        break;
                    case 3: // Difícil
                        timePerQuestion = 10;
                        break;
                }

                // Calcula el tiempo total
                int totalTime = timePerQuestion * amount;

                // Aquí puedes pasar este tiempo a la siguiente actividad
                Intent intent = new Intent(MainActivity.this, QuestionsActivity.class);
                intent.putExtra("totalTime", totalTime); // Pasa el tiempo total a la siguiente actividad
                startActivity(intent);
            }
        });



        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private boolean validateInputs() {
        String amountStr = amountEditText.getText().toString();
        int categoryPos = categorySpinner.getSelectedItemPosition();
        int difficultyPos = difficultySpinner.getSelectedItemPosition();

        if (categoryPos == 0) {
            Toast.makeText(this, "Seleccione una categoría", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (difficultyPos == 0) {
            Toast.makeText(this, "Seleccione una dificultad", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (amountStr.isEmpty() || Integer.parseInt(amountStr) <= 0) {
            Toast.makeText(this, "Ingrese una cantidad válida mayor a 0", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean checkInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return (activeNetwork != null && activeNetwork.isConnected());
    }

    private void setupSpinners() {
        // Spinner Categoría
        String[] categories = {"Selecciona una categoría", "Cultura General", "Libros", "Películas", "Música",
                "Computación", "Matemática", "Deportes", "Historia"};
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        // Spinner Dificultad
        String[] difficulties = {"Selecciona una dificultad", "Fácil", "Medio", "Difícil"};
        ArrayAdapter<String> difficultyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, difficulties);
        difficultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        difficultySpinner.setAdapter(difficultyAdapter);
    }
}
