package com.example.loginregister;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);  // Asegúrate de que este es el layout correcto

        // Encuentra los botones por su ID
        Button btnLogin = findViewById(R.id.btn_login);
        Button btnRegister = findViewById(R.id.btn_register);
        Button btnExit = findViewById(R.id.btn_exit);

        // Elimina el 'background tint' programáticamente para que se use el drawable definido
        btnLogin.setBackgroundTintList(null);
        btnRegister.setBackgroundTintList(null);
        btnExit.setBackgroundTintList(null);

        // Configura el clic del botón de login
        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(StartActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        // Configura el clic del botón de registro
        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(StartActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

}
