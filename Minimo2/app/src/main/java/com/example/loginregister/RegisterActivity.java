package com.example.loginregister;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.loginregister.Swagger.API;
import com.example.loginregister.Swagger.RegisterRequest;
import com.example.loginregister.Swagger.RegisterResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends Activity {

    private EditText usernameInput, txtEmail, txtPassword, confirmPasswordInput;
    private CheckBox termsCheckbox;
    private Button createAccountButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Referencias a los elementos de la interfaz gráfica
        usernameInput = findViewById(R.id.usernameInput);
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        termsCheckbox = findViewById(R.id.termsCheckbox);
        createAccountButton = findViewById(R.id.createAccountButton);

        // Configuración del botón de registro
        createAccountButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString();
            String email = txtEmail.getText().toString();
            String password = txtPassword.getText().toString();
            String confirmPassword = confirmPasswordInput.getText().toString();

            // Validación de los datos ingresados
            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show();

                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(RegisterActivity.this, "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!termsCheckbox.isChecked()) {
                Toast.makeText(RegisterActivity.this, "Debes aceptar los términos y condiciones.", Toast.LENGTH_SHORT).show();
                return;
            }
            clickRegister(v);

        });
    }

    public void clickRegister(View view) {
        Toast.makeText(this, "CLICK DETECTADO", Toast.LENGTH_SHORT).show();
        API.getAuthService().register(
                usernameInput.getText().toString(),
                txtEmail.getText().toString(),
                txtPassword.getText().toString()
        ).enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    RegisterResponse registerResponse = response.body();
                    if (registerResponse.isStatus()) {
                        Toast.makeText(RegisterActivity.this, "Registro exitoso: " + registerResponse.getMessage(), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(RegisterActivity.this, SplashScreenActivity.class);
                        intent.putExtra("user", registerResponse.getUsername());
                        intent.putExtra("correo", registerResponse.getCorreo());
                        intent.putExtra("origen", SplashScreenActivity.Constants.ORIGEN_REGISTER);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Error: " + registerResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "Error en el registro.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
