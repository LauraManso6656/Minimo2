package com.example.loginregister;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginregister.Swagger.API;
import com.example.loginregister.Swagger.AuthService;
import com.example.loginregister.Swagger.GenericResponse;
import com.example.loginregister.Swagger.InventoryResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InventarioActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventario);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewInventario);
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // Layout vertical por defecto
        String username = getIntent().getStringExtra("user");
        AuthService authService = API.getAuthService();

        authService.getInventario(username).enqueue(new Callback<List<InventoryResponse>>() {
            @Override
            public void onResponse(Call<List<InventoryResponse>> call, Response<List<InventoryResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<InventoryResponse> inventario = response.body();
                    for (InventoryResponse item : inventario) {
                        Log.d("Inventario", "Item: " + item.getNombre() + ", Cantidad: " + item.getCantidad());
                    }
                    // Crear el adaptador con la lista recibida y asignarlo al RecyclerView
                    InventarioAdapter adapter = new InventarioAdapter(InventarioActivity.this, inventario);
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(InventarioActivity.this, "No se pudo obtener el inventario", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<InventoryResponse>> call, Throwable t) {
                Toast.makeText(InventarioActivity.this, "Error de conexión al obtener inventario", Toast.LENGTH_SHORT).show();
                Log.e("InventarioError", "Error: ", t);
            }
        });

    }

    public void onClickBack(View view){
        Intent intent = new Intent(InventarioActivity.this, PerfilActivity.class);
        intent.putExtra("origen", "Inventario");
        startActivity(intent);
    }
}
