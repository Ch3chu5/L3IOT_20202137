package com.example.l3_20202137;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.AdapterView;
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

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity {

    private Spinner categorySpinner, difficultySpinner;
    private EditText amountEditText;
    private Button checkConnectionButton, startButton;

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

        setupSpinners(); // Configura los Spinners

        // Obtiene las categorías desde la API
        getCategories();

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
                // Obtén los valores seleccionados para la cantidad, categoría y dificultad
                String amountStr = amountEditText.getText().toString();
                int amount = Integer.parseInt(amountStr);
                int difficultyPos = difficultySpinner.getSelectedItemPosition();
                String difficulty = "";
                switch (difficultyPos) {
                    case 1: // Fácil
                        difficulty = "easy";
                        break;
                    case 2: // Medio
                        difficulty = "medium";
                        break;
                    case 3: // Difícil
                        difficulty = "hard";
                        break;
                }

                // Obtiene el ID de la categoría seleccionada
                int categoryPos = categorySpinner.getSelectedItemPosition();

                // Realiza la solicitud para obtener las preguntas
                fetchQuestions(amount, categoryPos, difficulty);
            }
        });
    }

    // Método para obtener las categorías
    private void getCategories() {
        // Crear el cliente Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://opentdb.com/") // Asegúrate de que la URL base esté configurada correctamente
                .addConverterFactory(GsonConverterFactory.create()) // Usamos Gson para deserializar la respuesta
                .build();

        TriviaApiServicio apiService = retrofit.create(TriviaApiServicio.class);

        // Realiza la solicitud GET a la API para obtener categorías
        Call<CategoryResponse> call = apiService.getCategories();

        call.enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                if (response.isSuccessful()) {
                    // Llenar el Spinner con las categorías obtenidas
                    List<CategoryResponse.Category> categories = response.body().getTriviaCategories();
                    List<String> categoryNames = new ArrayList<>();
                    categoryNames.add("Selecciona una categoría");

                    // Agregar categorías al Spinner
                    for (CategoryResponse.Category category : categories) {
                        categoryNames.add(category.getName());
                    }

                    ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, categoryNames);
                    categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    categorySpinner.setAdapter(categoryAdapter);
                } else {
                    Toast.makeText(MainActivity.this, "Error al obtener categorías", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error al realizar la solicitud", Toast.LENGTH_SHORT).show();
            }
        });
    }


    // Método para obtener las preguntas
    private void fetchQuestions(int amount, int category, String difficulty) {
        // Obtener el nombre de la categoría seleccionada
        String categoryName = categorySpinner.getSelectedItem().toString();

        // Crear el cliente Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://opentdb.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TriviaApiServicio apiService = retrofit.create(TriviaApiServicio.class);

        // Realiza la solicitud GET para obtener preguntas
        Call<Answer> call = apiService.getQuestions(amount, category, difficulty, "multiple");
        call.enqueue(new Callback<Answer>() {
            @Override
            public void onResponse(Call<Answer> call, Response<Answer> response) {
                if (response.isSuccessful()) {
                    // Obtén las preguntas de la respuesta
                    Answer answer = response.body();
                    List<Question> questions = answer.getResults();

                    // Inicia la siguiente actividad pasando las preguntas
                    Intent intent = new Intent(MainActivity.this, QuestionsActivity.class);
                    intent.putParcelableArrayListExtra("questions", new ArrayList<>(questions));  // Pasa las preguntas
                    intent.putExtra("category", categoryName); // Pasa el nombre de la categoría
                    int timePerQuestion = 0;

                    // Calcula el tiempo total según la dificultad
                    switch (difficulty) {
                        case "easy":
                            timePerQuestion = 5;
                            break;
                        case "medium":
                            timePerQuestion = 7;
                            break;
                        case "hard":
                            timePerQuestion = 10;
                            break;
                    }

                    int totalTime = timePerQuestion * amount;
                    intent.putExtra("totalTime", totalTime); // Pasa el tiempo total a la siguiente actividad
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Error al obtener preguntas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Answer> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Validación de conexión a internet
    private boolean checkInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return (activeNetwork != null && activeNetwork.isConnected());
    }

    // Validación de campos en el formulario
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

    private void setupSpinners() {
        // Spinner Dificultad
        String[] difficulties = {"Selecciona una dificultad", "Fácil", "Medio", "Difícil"};
        ArrayAdapter<String> difficultyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, difficulties);
        difficultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        difficultySpinner.setAdapter(difficultyAdapter);
    }
}