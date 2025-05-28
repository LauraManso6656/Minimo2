package com.example.loginregister;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.loginregister.Swagger.API;
import com.example.loginregister.Swagger.AuthService;
import com.example.loginregister.Swagger.GenericResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilActivity extends AppCompatActivity {

    public String user;
    public String correo;
    public String token;
    public String money;
    public String record;
    EditText txtNewPassword, txtActualPassword, txtPasswordDelete;
    Button enviarBtn;
    Button enviarBtn2;

    TextView correoTxtView;

    TextView userTxtView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        this.user = getIntent().getStringExtra("user");
        this.correo = getIntent().getStringExtra("correo");
        this.token = getIntent().getStringExtra("token");
        this.money = getIntent().getStringExtra("money");
        this.record = getIntent().getStringExtra("record");
        correoTxtView = findViewById(R.id.correoTxtView);
        userTxtView = findViewById(R.id.userTxtView);

        correoTxtView.setText("Correo: " + this.correo);
        userTxtView.setText("Usuario: " + this.user);

        txtNewPassword = findViewById(R.id.txtNewPassword);
        txtActualPassword = findViewById(R.id.txtActualPassword);
        txtPasswordDelete = findViewById(R.id.txtPasswordDelete);

        enviarBtn = findViewById(R.id.enviarBtn);
        enviarBtn2 = findViewById(R.id.enviarBtn2);

        // Están ocultos por XML, pero puedes reforzarlo aquí
        txtNewPassword.setVisibility(View.GONE);
        enviarBtn.setVisibility(View.GONE);

    }

    public void changePassClick(View view){
        txtNewPassword.setVisibility(View.VISIBLE);
        txtActualPassword.setVisibility(View.VISIBLE);
        enviarBtn.setVisibility(View.VISIBLE);
        txtPasswordDelete.setVisibility(View.GONE);
        enviarBtn2.setVisibility(View.GONE);
    }

    public void deleteAccountClick(View view){
        txtNewPassword.setVisibility(View.GONE);
        txtActualPassword.setVisibility(View.GONE);
        enviarBtn.setVisibility(View.GONE);
        txtPasswordDelete.setVisibility(View.VISIBLE);
        enviarBtn2.setVisibility(View.VISIBLE);
    }

    public void enviarChangeClick(View view){
        String nuevaPassword = txtNewPassword.getText().toString().trim();
        String actualPassword = txtActualPassword.getText().toString().trim();

        if (this.token == null || this.token.isEmpty()) {
            Toast.makeText(this, "Sesión no válida. Vuelve a iniciar sesión.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (actualPassword.isEmpty()) {
            Toast.makeText(this, "Introduce tu contraseña actual", Toast.LENGTH_SHORT).show();
            return;
        }

        if (nuevaPassword.isEmpty()) {
            Toast.makeText(this, "Introduce una nueva contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        if (nuevaPassword.equals(actualPassword)) {
            Toast.makeText(this, "La nueva contraseña no puede ser igual a la anterior", Toast.LENGTH_SHORT).show();
            return;
        }

        AuthService authService = API.getAuthService();
        Call<GenericResponse> call = authService.actualizarContrasena("Bearer " + this.token, actualPassword, nuevaPassword);

        call.enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(PerfilActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    txtNewPassword.setText("");
                    txtActualPassword.setText("");
                } else {
                    Toast.makeText(PerfilActivity.this, "No se pudo cambiar la contraseña. Verifica tus datos.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                Toast.makeText(PerfilActivity.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void enviarDeleteClick(View view){
        String actualPassword = txtPasswordDelete.getText().toString();

        if (this.token == null || this.token.isEmpty() || actualPassword.isEmpty()) {
            Toast.makeText(this, "Introduce tu contraseña actual", Toast.LENGTH_SHORT).show();
            return;
        }

        AuthService authService = API.getAuthService();
        Call<GenericResponse> call = authService.eliminarUsuario("Bearer " + this.token, actualPassword);

        call.enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isStatus()) {
                    Toast.makeText(PerfilActivity.this, "Cuenta eliminada correctamente", Toast.LENGTH_LONG).show();
                    // Limpiar sesión
                    SharedPreferences.Editor editor = getSharedPreferences("Sesion", MODE_PRIVATE).edit();
                    editor.clear();
                    editor.apply();
                    // Volver al inicio
                    Intent intent = new Intent(PerfilActivity.this, StartActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    String mensaje = response.body() != null ? response.body().getMessage() : "Error al eliminar cuenta";
                    Toast.makeText(PerfilActivity.this, mensaje, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                Toast.makeText(PerfilActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void onClickBack(View view) {
        Intent intent = new Intent(PerfilActivity.this, LobbyActivity.class);
        intent.putExtra("origen", "lobby");
        startActivity(intent);

    }

    public void onClickInventario(View view){
        Intent intent = new Intent(PerfilActivity.this, InventarioActivity.class);
        intent.putExtra("user", this.user);
        intent.putExtra("correo", this.correo);
        intent.putExtra("token", this.token);
        intent.putExtra("money",this.money);
        intent.putExtra("record", this.record);
        startActivity(intent);
    }

    private void overridePendingTransition(int slideInLeft) {
    }


}
