package com.example.loginregister;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loginregister.Swagger.API;
import com.example.loginregister.Swagger.LoginResponse;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText emailEditText, passwordEditText;
    private SharedPreferences prefs;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        prefs = getSharedPreferences("Sesion", MODE_PRIVATE);

        // si no hay sesion activa entra al if
        if (prefs.getBoolean("sesionIniciada", false)) {
            Intent intent = new Intent(LoginActivity.this, SplashScreenActivity.class);
            intent.putExtra("user", prefs.getString("user", ""));
            intent.putExtra("correo", prefs.getString("correo", ""));
            intent.putExtra("origen", SplashScreenActivity.Constants.ORIGEN_LOGIN);
            startActivity(intent);
            finish();
            return;

        }


        // Referencia al TextView de Sign up
        TextView signUpText = findViewById(R.id.signUp);
        signUpText.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        emailEditText = findViewById(R.id.txtEmail);
        passwordEditText = findViewById(R.id.txtPassword);
    }

    public void clickLogin(View view) {
        String correo = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        loginUser(correo, password);
    }

    private void loginUser(String correo, String password) {
        API.getAuthService().login(correo, password).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    LoginResponse loginResponse = response.body();
                    if (loginResponse != null && loginResponse.isStatus()) {
                        // Guardar sesión en SharedPreferences
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putBoolean("sesionIniciada", true);
                        editor.putString("user", loginResponse.getUser());
                        editor.putString("correo", loginResponse.getCorreo());
                        editor.putString("token", loginResponse.getToken());
                        editor.apply();

                        // Redirigir a la pantalla principal
                        Toast.makeText(LoginActivity.this, "Bienvenido, " + loginResponse.getUser(), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(LoginActivity.this, SplashScreenActivity.class);
                        intent.putExtra("user", loginResponse.getUser());
                        intent.putExtra("correo", loginResponse.getCorreo());
                        intent.putExtra("token", loginResponse.getToken());
                        intent.putExtra("origen", SplashScreenActivity.Constants.ORIGEN_LOGIN);
                        startActivity(intent);
                        finish(); // Evita que el usuario regrese con el botón "Atrás"
                    } else {
                        Toast.makeText(LoginActivity.this, "Error: " + loginResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Login fallido", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
