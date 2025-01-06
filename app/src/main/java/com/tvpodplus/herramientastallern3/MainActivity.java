package com.tvpodplus.herramientastallern3;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
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
    private EditText etIdCarrera;
    private String baseUrl = "http://192.168.56.1:3000/carreras";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvResultados = findViewById(R.id.tv_resultados);
        etIdCarrera = findViewById(R.id.et_id_carrera);

        Button btnCrear = findViewById(R.id.btn_crear_carrera);
        Button btnAvanzar = findViewById(R.id.btn_avanzar_carrera);
        Button btnObtener = findViewById(R.id.btn_obtener_carreras);
        Button btnEliminar = findViewById(R.id.btn_eliminar_carrera);

        btnCrear.setOnClickListener(v -> realizarAccion("POST", ""));
        btnAvanzar.setOnClickListener(v -> {
            String id = etIdCarrera.getText().toString().trim();
            if (!id.isEmpty()) {
                realizarAccion("PUT", "/" + id);
            } else {
                Toast.makeText(this, "Por favor, ingrese un ID válido", Toast.LENGTH_SHORT).show();
            }
        });
        btnObtener.setOnClickListener(v -> realizarAccion("GET", ""));
        btnEliminar.setOnClickListener(v -> {
            String id = etIdCarrera.getText().toString().trim();
            if (!id.isEmpty()) {
                realizarAccion("DELETE", "/" + id);
            } else {
                Toast.makeText(this, "Por favor, ingrese un ID válido", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void realizarAccion(String metodo, String ruta) {
        new Thread(() -> {
            try {
                URL url = new URL(baseUrl + ruta);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod(metodo);

                if (metodo.equals("POST")) {
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setDoOutput(true);
                    String body = "{\"numero_de_corredores\":2,\"distancia_recorrida\":20}";
                    OutputStream os = conn.getOutputStream();
                    os.write(body.getBytes());
                    os.close();
                }

                int responseCode = conn.getResponseCode();
                String resultado;

                if (responseCode == 200 || responseCode == 201) {
                    Scanner scanner = new Scanner(conn.getInputStream());
                    String response = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                    scanner.close();

                    // Formatear JSON
                    try {
                        JSONObject json = new JSONObject(new JSONTokener(response));
                        resultado = json.toString(4);
                    } catch (Exception e) {
                        resultado = response;
                    }
                } else {
                    resultado = "Error en la acción: " + responseCode;
                }

                String finalResultado = resultado;
                runOnUiThread(() -> tvResultados.setText(finalResultado));
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
