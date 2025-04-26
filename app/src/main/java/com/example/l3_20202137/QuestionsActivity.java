package com.example.l3_20202137;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.util.Log;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class QuestionsActivity extends AppCompatActivity {

    private Button siguienteButton;
    private RadioGroup respuestasRadioGroup;
    private RadioButton[] opcionesRadioButtons;
    private TextView preguntaTextView, preguntaNumeroTextView, categoriaTextView, timerTextView;

    private List<Question> listaPreguntas;
    private int preguntaActual = 0;
    private int correctas = 0;
    private int incorrectas = 0;
    private CountDownTimer countDownTimer;
    private long tiempoRestante;
    private int tiempoPorPregunta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_questions);

        // Inicialización de las vistas
        categoriaTextView = findViewById(R.id.categoryText);
        preguntaNumeroTextView = findViewById(R.id.questionCountText);
        preguntaTextView = findViewById(R.id.questionText);
        timerTextView = findViewById(R.id.timerText);
        respuestasRadioGroup = findViewById(R.id.optionsGroup);
        siguienteButton = findViewById(R.id.nextButton);

        opcionesRadioButtons = new RadioButton[]{
                findViewById(R.id.option1),
                findViewById(R.id.option2),
                findViewById(R.id.option3),
                findViewById(R.id.option4)
        };

        int cantidad = getIntent().getIntExtra("cantidad", 5);
        String categoriaNombre = getIntent().getStringExtra("categoria");
        String dificultadOriginal = getIntent().getStringExtra("dificultad");
        String dificultad = mapearDificultad(dificultadOriginal);

        int categoriaId = convertirCategoria(categoriaNombre);
        tiempoPorPregunta = getTiempoPorDificultad(dificultad);
        categoriaTextView.setText(categoriaNombre);

        obtenerPreguntas(cantidad, categoriaId, dificultad);

        // Ajustes de la vista para las barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private String mapearDificultad(String dificultad) {
        if (dificultad == null) return "easy";

        switch (dificultad.toLowerCase()) {
            case "fácil": return "easy";
            case "medio": return "medium";
            case "difícil": return "hard";
            default: return "easy";
        }
    }

    private void obtenerPreguntas(int cantidad, int categoria, String dificultad) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://opentdb.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TriviaApiServicio api = retrofit.create(TriviaApiServicio.class);
        Call<Answer> call = api.getQuestions(cantidad, categoria, dificultad, "multiple");

        call.enqueue(new Callback<Answer>() {
            @Override
            public void onResponse(Call<Answer> call, Response<Answer> response) {
                if (response.isSuccessful()) {
                    Answer respuesta = response.body();
                    listaPreguntas = respuesta.getResults();

                    if (listaPreguntas != null && !listaPreguntas.isEmpty()) {
                        iniciarTrivia();
                    } else {
                        mostrarError("No se encontraron preguntas para esta configuración");
                    }
                } else {
                    mostrarError("Error en la respuesta de la API");
                }
            }

            @Override
            public void onFailure(Call<Answer> call, Throwable t) {
                mostrarError("Error de conexión");
            }
        });
    }

    private void iniciarTrivia() {
        iniciarContador();
        mostrarPregunta();

        siguienteButton.setOnClickListener(v -> {
            if (verificarRespuesta()) correctas++;
            else incorrectas++;

            if (++preguntaActual < listaPreguntas.size()) {
                mostrarPregunta();
            } else {
                countDownTimer.cancel();
                irAEstadisticas();
            }
        });
    }

    private void mostrarPregunta() {
        Question question = listaPreguntas.get(preguntaActual);
        preguntaNumeroTextView.setText("Pregunta " + (preguntaActual + 1) + "/" + listaPreguntas.size());
        preguntaTextView.setText(Html.fromHtml(question.getQuestion(), Html.FROM_HTML_MODE_LEGACY));
        respuestasRadioGroup.clearCheck();

        List<String> opciones = new ArrayList<>(question.getIncorrectAnswers());
        opciones.add(question.getCorrectAnswer());
        Collections.shuffle(opciones);

        for (int i = 0; i < opcionesRadioButtons.length; i++) {
            opcionesRadioButtons[i].setText(Html.fromHtml(opciones.get(i), Html.FROM_HTML_MODE_LEGACY));
        }
    }

    private boolean verificarRespuesta() {
        int seleccionId = respuestasRadioGroup.getCheckedRadioButtonId();
        if (seleccionId == -1) return false;

        RadioButton seleccionada = findViewById(seleccionId);
        String respuesta = Html.fromHtml(listaPreguntas.get(preguntaActual).getCorrectAnswer(), Html.FROM_HTML_MODE_LEGACY).toString();
        return seleccionada.getText().toString().equals(respuesta);
    }

    private void iniciarContador() {
        tiempoRestante = listaPreguntas.size() * tiempoPorPregunta * 1000L;

        countDownTimer = new CountDownTimer(tiempoRestante, 1000) {
            public void onTick(long millisUntilFinished) {
                int segundos = (int) (millisUntilFinished / 1000);
                timerTextView.setText(String.format("%02d:%02d", segundos / 60, segundos % 60));
            }

            public void onFinish() {
                irAEstadisticas();
            }
        }.start();
    }

    private void irAEstadisticas() {
        Intent intent = new Intent(this, EstadisticasActivity.class);
        intent.putExtra("correctas", correctas);
        intent.putExtra("total", listaPreguntas.size());
        intent.putExtra("incorrectas", incorrectas);
        startActivity(intent);
        finish();
    }

    private void mostrarError(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
        finish();
    }

    private int convertirCategoria(String categoria) {
        switch (categoria) {
            case "Cultura General": return 9;
            case "Libros": return 10;
            case "Películas": return 11;
            case "Música": return 12;
            case "Computación": return 18;
            case "Deportes": return 21;
            case "Matemática": return 19;
            case "Historia": return 23;
            default: return 9;
        }
    }

    private int getTiempoPorDificultad(String dificultad) {
        switch (dificultad.toLowerCase()) {
            case "fácil": return 5;
            case "medio": return 7;
            case "difícil": return 10;
            default: return 5;
        }
    }
}
