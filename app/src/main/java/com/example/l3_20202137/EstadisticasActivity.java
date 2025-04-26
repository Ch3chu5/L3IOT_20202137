package com.example.l3_20202137;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class EstadisticasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_estadisticas);

        Button replayButton = findViewById(R.id.replayButton);


        replayButton.setOnClickListener(view -> {
            // Crear el Intent para volver a MainActivity
            Intent intent = new Intent(EstadisticasActivity.this, MainActivity.class);
            // Iniciar MainActivity
            startActivity(intent);
            // Opcional: Si deseas que se cierre la actividad actual (EstadisticasActivity) al ir a MainActivity
            finish();
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}