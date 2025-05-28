package com.example.loginregister;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class HomeActivity extends Activity {

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        prefs = getSharedPreferences("Sesion", MODE_PRIVATE);
        String tken = prefs.getString("token", "");

        findViewById(R.id.buttonStartHome).setOnClickListener(v -> {
            Intent intent;
            if (prefs.getBoolean("sesionIniciada", false)) {
                // Recuperamos todos los datos de sesión
                String user   = prefs.getString("user", "");
                String correo = prefs.getString("correo", "");
                String token  = prefs.getString("token", "");

                // Creamos el Intent una sola vez
                intent = new Intent(this, LobbyActivity.class);
                intent.putExtra("user",   user);
                intent.putExtra("correo", correo);
                intent.putExtra("token",  token);
            } else {
                intent = new Intent(this, StartActivity.class);
            }
            startActivity(intent);
            finish();
        });
    }
}
