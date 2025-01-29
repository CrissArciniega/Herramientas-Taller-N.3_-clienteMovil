package com.tvpodplus.herramientastallern3;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    private TextView tvResultados;
    private String apiUrl = "https://y0xoq93c1l.execute-api.us-east-2.amazonaws.com/post";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvResultados = findViewById(R.id.tv_resultados);

        Button btnEmpezar = findViewById(R.id.btn_empezar_carrera);
        Button btnSinEmpezar = findViewById(R.id.btn_sin_empezar_carrera);

        btnEmpezar.setOnClickListener(v -> realizarAccionCarrera("empezar"));
        btnSinEmpezar.setOnClickListener(v -> realizarAccionCarrera("sin_empezar"));
    }

    private void realizarAccionCarrera(String accion) {
        new Thread(() -> {
            try {
                String body = "{\"accion\": \"" + accion + "\"}";

                URL url = new URL(apiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                OutputStream os = conn.getOutputStream();
                os.write(body.getBytes());
                os.close();

                int responseCode = conn.getResponseCode();
                String resultado;

                if (responseCode == 200 || responseCode == 201) {
                    Scanner scanner = new Scanner(conn.getInputStream());
                    String response = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                    scanner.close();

                    try {
                        JSONObject json = new JSONObject(new JSONTokener(response));
                        resultado = json.getString("body");
                    } catch (Exception e) {
                        resultado = response;
                    }
                } else {
                    resultado = "Error en la acción: " + responseCode;
                }

                String finalResultado = formatoResultados(resultado);
                runOnUiThread(() -> tvResultados.setText(finalResultado));
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private String formatoResultados(String resultados) {
        StringBuilder sb = new StringBuilder();
        sb.append("Resultados de la Carrera\n\n");
        sb.append(resultados.replace("\n", "\n\n"));
        return sb.toString();
    }
}
