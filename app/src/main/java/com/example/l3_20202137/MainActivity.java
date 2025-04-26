package com.example.l3_20202137;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
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

    private EditText cantidadEditText;
    private Button startButton;
    private Button comprobarConexionButton;
    private Spinner categoriaSpinner;
    private Spinner dificultadSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        cantidadEditText = findViewById(R.id.amountEditText);
        startButton = findViewById(R.id.startButton);
        comprobarConexionButton = findViewById(R.id.checkConnectionButton);
        categoriaSpinner = findViewById(R.id.categorySpinner);
        dificultadSpinner = findViewById(R.id.difficultySpinner);

        String[] categorias = {"Selecionar Categoria", "Cultura General", "Libros", "Películas", "Música", "Computación", "Deportes", "Matemática", "Historia"};
        categoriaSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categorias));

        String[] dificultades = {"Selecionar Dificultad", "Fácil", "Medio", "Difícil"};
        dificultadSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dificultades));

        comprobarConexionButton.setOnClickListener(v -> {
            if (validarCampos()) {
                if (conextionInt()) {
                    startButton.setEnabled(true);
                    Toast.makeText(this, "Conexión establecida", Toast.LENGTH_SHORT).show();
                } else {
                    startButton.setEnabled(false);
                    Toast.makeText(this, "No hay conexión a internet", Toast.LENGTH_SHORT).show();
                }
            }

            startButton.setOnClickListener(v1 -> {
                if (conextionInt()) {
                    iniciarJuego();
                } else {
                    Toast.makeText(MainActivity.this, "Error: Se perdió la conexión a Internet", Toast.LENGTH_SHORT).show();
                    startButton.setEnabled(false);
                }
            });
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private boolean validarCampos() {
        String categoriaSeleccionada = categoriaSpinner.getSelectedItem().toString();
        String dificultadSeleccionada = dificultadSpinner.getSelectedItem().toString();
        if (categoriaSeleccionada.equals("Selecionar Categoria")) {
            Toast.makeText(this, "Seleccione una categoría válida", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (dificultadSeleccionada.equals("Selecionar Dificultad")) {
            Toast.makeText(this, "Seleccione una dificultad válida", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (categoriaSpinner.getSelectedItem() == null || dificultadSpinner.getSelectedItem() == null || cantidadEditText.getText().toString().isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return false;
        }

        int cantidad = Integer.parseInt(cantidadEditText.getText().toString());
        if (cantidad <= 0) {
            Toast.makeText(this, "La cantidad debe ser positiva", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void iniciarJuego() {
        Intent intent = new Intent(MainActivity.this, QuestionsActivity.class);
        intent.putExtra("cantidad", Integer.parseInt(cantidadEditText.getText().toString()));
        intent.putExtra("categoria", categoriaSpinner.getSelectedItem().toString());
        intent.putExtra("dificultad", dificultadSpinner.getSelectedItem().toString());
        startActivity(intent);
    }

    private boolean conextionInt() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        Network network = cm.getActiveNetwork();
        if (network == null) {
            return false;
        }
        NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
        return capabilities != null &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
    }
}
