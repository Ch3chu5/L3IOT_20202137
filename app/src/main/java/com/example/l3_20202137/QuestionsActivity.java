package com.example.l3_20202137;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class QuestionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_questions);

        int totalTime = getIntent().getIntExtra("totalTime", 0);


        TextView timerText = findViewById(R.id.timerText);
        startTimer(totalTime, timerText);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void startTimer(int totalTime, TextView timerText) {
        // Temporizador descendente
        new CountDownTimer(totalTime * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                // Actualiza el texto del temporizador cada segundo
                int seconds = (int) (millisUntilFinished / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                timerText.setText(String.format("%02d:%02d", minutes, seconds));
            }

            public void onFinish() {
                // Cuando se acabe el tiempo
                timerText.setText("00:00");
                // Redirigir a la siguiente vista (por ejemplo, vista de resultados)
                Intent intent = new Intent(QuestionsActivity.this, EstadisticasActivity.class);
                startActivity(intent);
            }
        }.start();
    }

}