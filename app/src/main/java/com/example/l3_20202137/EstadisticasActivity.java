package com.example.l3_20202137;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class EstadisticasActivity extends AppCompatActivity {

    private Button replayButton;
    private TextView correctCountTextView;
    private TextView incorrectCountTextView;
    private TextView notAnsweredCountTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_estadisticas);

        // Inicializar los componentes
        replayButton = findViewById(R.id.replayButton);
        correctCountTextView = findViewById(R.id.correctCount);
        incorrectCountTextView = findViewById(R.id.incorrectCount);
        notAnsweredCountTextView = findViewById(R.id.notAnsweredCount);

        // Obtener datos de la actividad anterior
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int correctas = extras.getInt("correctas");

            int total = extras.getInt("total");

            int incorrectas = extras.getInt("incorrectas");

            int noRespondidas = total - (correctas + incorrectas);

            correctCountTextView.setText("Correctas: " + correctas);
            incorrectCountTextView.setText("Incorrectas: " + incorrectas);
            notAnsweredCountTextView.setText("No respondidas: " + noRespondidas);
        }

        // Configurar botón para volver a jugar
        replayButton.setOnClickListener(v -> {
            Intent mainIntent = new Intent(EstadisticasActivity.this, MainActivity.class);
            // Limpiar el stack de actividades y empezar de nuevo
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mainIntent);
            finish();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
